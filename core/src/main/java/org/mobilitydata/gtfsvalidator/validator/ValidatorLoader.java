/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.validator;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsEntity;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer;

/**
 * A {@code ValidatorLoader} object locates all validators registered with {@code @GtfsValidator}
 * annotation and provides convenient methods to invoke them on a single entity of file.
 *
 * <p>This class contains all logic of automatic dependency injection to the instantiated validator
 * classes.
 */
public class ValidatorLoader {
  private final ListMultimap<Class<? extends GtfsEntity>, Class<? extends SingleEntityValidator<?>>>
      singleEntityValidators = ArrayListMultimap.create();
  private final ListMultimap<Class<? extends GtfsTableContainer<?>>, Class<? extends FileValidator>>
      singleFileValidators = ArrayListMultimap.create();
  private final List<Class<? extends FileValidator>> multiFileValidators = new ArrayList<>();

  /** Create a validator with the specified validator classes loaded. */
  public static ValidatorLoader createForClasses(ImmutableList<Class<?>> validatorClasses)
      throws ValidatorLoaderException {
    ValidatorLoader loader = new ValidatorLoader();
    for (Class<?> clazz : validatorClasses) {
      if (SingleEntityValidator.class.isAssignableFrom(clazz)) {
        loader.addSingleEntityValidator((Class<? extends SingleEntityValidator<?>>) clazz);
      } else if (FileValidator.class.isAssignableFrom(clazz)) {
        loader.addFileValidator((Class<? extends FileValidator>) clazz);
      }
    }
    return loader;
  }

  /** Create an empty loader with no validators loaded. */
  public static ValidatorLoader createEmpty() {
    return new ValidatorLoader();
  }

  private ValidatorLoader() {}

  /** Loaded {@code SingleEntityValidator} classes keyed by entity class. */
  public ListMultimap<Class<? extends GtfsEntity>, Class<? extends SingleEntityValidator<?>>>
      getSingleEntityValidators() {
    return singleEntityValidators;
  }

  /** Loaded single-file validator classes keyed by table container class. */
  public ListMultimap<Class<? extends GtfsTableContainer<?>>, Class<? extends FileValidator>>
      getSingleFileValidators() {
    return singleFileValidators;
  }

  /** Loaded cross-file validator classes. */
  public List<Class<? extends FileValidator>> getMultiFileValidators() {
    return multiFileValidators;
  }

  @SuppressWarnings("unchecked")
  private <T extends SingleEntityValidator<?>> void addSingleEntityValidator(
      Class<T> validatorClass) throws ValidatorLoaderException {
    Constructor<T> constructor = chooseConstructor(validatorClass);
    for (Class<?> parameterType : constructor.getParameterTypes()) {
      if (!isInjectableFromContext(parameterType)) {
        throw new ValidatorLoaderException(
            String.format(
                "Cannot inject parameter of type %s to %s constructor",
                parameterType.getCanonicalName(), validatorClass.getCanonicalName()));
      }
    }
    for (Method method : validatorClass.getMethods()) {
      // A child class of SingleEntityValidator has two `validate' methods:
      // 1) the inherited void validate(GtfsEntity entity, NoticeContainer noticeContainer);
      // 2) the type-specific void validate(Gtfs<name> entity, NoticeContainer noticeContainer).
      // We need to skip the first one and use the second one.
      Class<?>[] parameterTypes = method.getParameterTypes();
      if (method.getName().equals("validate")
          && method.getParameterCount() == 2
          && GtfsEntity.class.isAssignableFrom(parameterTypes[0])
          && !parameterTypes[0].isAssignableFrom(GtfsEntity.class)
          && parameterTypes[1].isAssignableFrom(NoticeContainer.class)) {
        singleEntityValidators.put((Class<? extends GtfsEntity>) parameterTypes[0], validatorClass);
        return;
      }
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends FileValidator> void addFileValidator(Class<T> validatorClass)
      throws ValidatorLoaderException {
    Constructor<T> constructor = chooseConstructor(validatorClass);

    // Indicates that the full GtfsFeedContainer needs to be injected.
    boolean injectFeedContainer = false;
    // Find out which GTFS tables need to be injected.
    List<Class<? extends GtfsTableContainer<?>>> injectedTables = new ArrayList<>();
    for (Class<?> parameterType : constructor.getParameterTypes()) {
      if (isInjectableFromContext(parameterType)) {
        continue;
      }
      if (GtfsFeedContainer.class.isAssignableFrom(parameterType)) {
        injectFeedContainer = true;
        continue;
      }
      if (!GtfsTableContainer.class.isAssignableFrom(parameterType)) {
        throw new ValidatorLoaderException(
            String.format(
                "Cannot inject parameter of type %s to %s constructor",
                parameterType.getCanonicalName(), validatorClass.getCanonicalName()));
      }
      injectedTables.add((Class<? extends GtfsTableContainer<?>>) parameterType);
    }

    if (!injectFeedContainer && injectedTables.size() == 1) {
      singleFileValidators.put(injectedTables.get(0), validatorClass);
    } else {
      multiFileValidators.add(validatorClass);
    }
  }

  private static boolean isInjectableFromContext(Class<?> parameterType) {
    return parameterType.isAssignableFrom(CurrentDateTime.class)
        || parameterType.isAssignableFrom(CountryCode.class);
  }

  /** Chooses the default or injectable constructor. */
  @SuppressWarnings("unchecked")
  private static <T> Constructor<T> chooseConstructor(Class<T> validatorClass)
      throws ValidatorLoaderException {
    for (Constructor<?> constructor : validatorClass.getDeclaredConstructors()) {
      if (constructor.getParameterCount() == 0 || constructor.isAnnotationPresent(Inject.class)) {
        return (Constructor<T>) constructor;
      }
    }
    throw new ValidatorLoaderException(
        String.format(
            "Validator %s has no injectable or default constructors",
            validatorClass.getCanonicalName()));
  }

  /**
   * Instantiates a validator of given class and injects its dependencies. A dependency is typically
   * a table that is used by the validator. If any of the dependencies have errors, we put this
   * information in the returned {@link ValidatorWithDependencyStatus}.
   */
  private static <T> ValidatorWithDependencyStatus<T> createValidator(
      Class<T> clazz, DependencyResolver dependencyResolver) throws ReflectiveOperationException {
    Constructor<T> chosenConstructor;
    try {
      chosenConstructor = chooseConstructor(clazz);
    } catch (ValidatorLoaderException e) {
      // This should never happen since that problem when loading the validator class.
      throw new NoSuchMethodException("Injectable or default constructor is not found");
    }
    // Inject constructor parameters.
    Object[] parameters = new Object[chosenConstructor.getParameterCount()];
    for (int i = 0; i < parameters.length; ++i) {
      parameters[i] =
          dependencyResolver.resolveDependency(chosenConstructor.getParameters()[i].getType());
    }
    chosenConstructor.setAccessible(true);
    T validator = chosenConstructor.newInstance(parameters);
    return new ValidatorWithDependencyStatus<T>(
        validator, dependencyResolver.dependenciesHaveErrors);
  }

  /**
   * Instantiates a validator of given class and injects its dependencies.
   *
   * @param clazz validator class to instantiate
   * @param validationContext context to inject
   * @param <T> type of the validator to instantiate
   * @return a new validator
   */
  public static <T extends SingleEntityValidator>
      ValidatorWithDependencyStatus<T> createValidatorWithContext(
          Class<T> clazz, ValidationContext validationContext) throws ReflectiveOperationException {
    return (ValidatorWithDependencyStatus<T>)
        createValidator(clazz, new DependencyResolver(validationContext, null, null));
  }

  /** Instantiates a {@code FileValidator} for a single table. */
  public static <T extends FileValidator>
      ValidatorWithDependencyStatus<T> createSingleFileValidator(
          Class<? extends FileValidator> clazz,
          GtfsTableContainer<?> table,
          ValidationContext validationContext)
          throws ReflectiveOperationException {
    return (ValidatorWithDependencyStatus<T>)
        createValidator(clazz, new DependencyResolver(validationContext, table, null));
  }

  /** Instantiates a {@code FileValidator} for multiple tables in a given feed. */
  @SuppressWarnings("unchecked")
  public static <T extends FileValidator> ValidatorWithDependencyStatus<T> createMultiFileValidator(
      Class<T> clazz, GtfsFeedContainer feed, ValidationContext validationContext)
      throws ReflectiveOperationException {
    return createValidator(clazz, new DependencyResolver(validationContext, null, feed));
  }

  /**
   * Helper class for resolving injected dependencies of validators, while also tracking if those
   * dependencies have blocking errors.
   */
  private static class DependencyResolver {
    private final ValidationContext context;
    @Nullable private final GtfsTableContainer<?> tableContainer;
    @Nullable private final GtfsFeedContainer feedContainer;

    /** This will be set to true if a resolved dependency was not parsed successfully. */
    private boolean dependenciesHaveErrors = false;

    public DependencyResolver(
        ValidationContext context,
        @Nullable GtfsTableContainer<?> tableContainer,
        @Nullable GtfsFeedContainer feedContainer) {
      this.context = context;
      this.tableContainer = tableContainer;
      this.feedContainer = feedContainer;
    }

    /**
     * This is typically called to obtain the container related to each parameter in a validator's
     * constructor or a basic type related to country code or date.
     *
     * @param parameterClass The parameter of the constructor we want to examine.
     * @return
     */
    public Object resolveDependency(Class<?> parameterClass) {
      if (feedContainer != null && parameterClass.isAssignableFrom(GtfsFeedContainer.class)) {
        if (!feedContainer.isParsedSuccessfully()) {
          dependenciesHaveErrors = true;
        }
        return feedContainer;
      }
      if (tableContainer != null && parameterClass.isAssignableFrom(tableContainer.getClass())) {
        if (!tableContainer.isParsedSuccessfully()) {
          dependenciesHaveErrors = true;
        }
        return tableContainer;
      }
      if (feedContainer != null && GtfsTableContainer.class.isAssignableFrom(parameterClass)) {
        GtfsTableContainer<?> container =
            feedContainer.getTable((Class<? extends GtfsTableContainer<?>>) parameterClass);
        if (container != null && !container.isParsedSuccessfully()) {
          dependenciesHaveErrors = true;
        }
        return container;
      }
      return context.get(parameterClass);
    }
  }

  public static final class ValidatorWithDependencyStatus<T> {
    private final T validator;
    // Will be true if one of the injected dependencies of the validator has parse errors.
    private final boolean dependenciesHaveErrors;

    public ValidatorWithDependencyStatus(T validator, boolean dependenciesHaveErrors) {
      this.validator = validator;
      this.dependenciesHaveErrors = dependenciesHaveErrors;
    }

    public T validator() {
      return validator;
    }

    public boolean dependenciesHaveErrors() {
      return dependenciesHaveErrors;
    }
  }

  /** Describes all loaded validators. */
  public String listValidators() {
    StringBuilder builder = new StringBuilder();
    if (!singleEntityValidators.isEmpty()) {
      builder.append("Single-entity validators\n");
      for (Entry<Class<? extends GtfsEntity>, Collection<Class<? extends SingleEntityValidator<?>>>>
          entry : singleEntityValidators.asMap().entrySet()) {
        builder.append("\t").append(entry.getKey().getSimpleName()).append(": ");
        for (Class<? extends SingleEntityValidator<?>> validatorClass : entry.getValue()) {
          builder.append(validatorClass.getSimpleName()).append(" ");
        }
        builder.append("\n");
      }
    }
    if (!singleFileValidators.isEmpty()) {
      builder.append("Single-file validators\n");
      for (Map.Entry<
              Class<? extends GtfsTableContainer<?>>, Collection<Class<? extends FileValidator>>>
          entry : singleFileValidators.asMap().entrySet()) {
        builder.append("\t").append(entry.getKey().getSimpleName()).append(": ");
        for (Class<? extends FileValidator> validatorClass : entry.getValue()) {
          builder.append(validatorClass.getSimpleName()).append(" ");
        }
        builder.append("\n");
      }
    }
    if (!multiFileValidators.isEmpty()) {
      builder.append("Multi-file validators\n").append("\t");
      for (Class<? extends FileValidator> validatorClass : multiFileValidators) {
        builder.append(validatorClass.getSimpleName()).append(" ");
      }
      builder.append("\n");
    }
    return builder.toString();
  }
}
