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
import com.google.common.collect.ListMultimap;
import com.google.common.flogger.FluentLogger;
import com.google.common.reflect.ClassPath;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.ErrorDetectedException;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsEntity;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer;

/**
 * A {@code ValidatorLoader} object locates all validators registered with {@code @GtfsValidator}
 * annotation and provides convenient methods to invoke them on a single entity of file.
 */
public class ValidatorLoader {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private final ListMultimap<Class<? extends GtfsEntity>, Class<? extends SingleEntityValidator<?>>>
      singleEntityValidators = ArrayListMultimap.create();
  private final ListMultimap<Class<? extends GtfsTableContainer>, Class<? extends FileValidator>>
      singleFileValidators = ArrayListMultimap.create();
  private final List<Class<? extends FileValidator>> multiFileValidators = new ArrayList<>();

  public ValidatorLoader() {
    List<Class<? extends SingleEntityValidator>> singleEntityValidatorClasses = new ArrayList<>();
    List<Class<? extends FileValidator>> fileValidatorClasses = new ArrayList<>();
    ClassPath classPath;
    try {
      classPath = ClassPath.from(ClassLoader.getSystemClassLoader());
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
    for (ClassPath.ClassInfo classInfo :
        classPath.getTopLevelClassesRecursive("org.mobilitydata.gtfsvalidator.validator")) {
      Class<?> clazz = classInfo.load();
      if (clazz.isAnnotationPresent(GtfsValidator.class)) {
        if (SingleEntityValidator.class.isAssignableFrom(clazz)) {
          singleEntityValidatorClasses.add((Class<? extends SingleEntityValidator>) clazz);
        } else if (FileValidator.class.isAssignableFrom(clazz)) {
          fileValidatorClasses.add((Class<? extends FileValidator>) clazz);
        }
      }
    }

    for (Class<? extends SingleEntityValidator> validatorClass : singleEntityValidatorClasses) {
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
          singleEntityValidators.put(
              (Class<? extends GtfsEntity>) parameterTypes[0],
              ((Class<? extends SingleEntityValidator<?>>) validatorClass));
          break;
        }
      }
    }

    for (Class<? extends FileValidator> validatorClass : fileValidatorClasses) {
      List<Field> tableInjectableFields = new ArrayList<>();
      for (Field field : validatorClass.getDeclaredFields()) {
        if (isTableInjectableField(field)) {
          tableInjectableFields.add(field);
        }
      }
      if (tableInjectableFields.size() == 1) {
        singleFileValidators.put(
            (Class<? extends GtfsTableContainer>) tableInjectableFields.get(0).getType(),
            validatorClass);
      } else {
        multiFileValidators.add(validatorClass);
      }
    }
  }

  private static boolean isTableInjectableField(Field field) {
    return field.isAnnotationPresent(Inject.class)
        && GtfsTableContainer.class.isAssignableFrom(field.getType());
  }

  /**
   * Creates a list of validators for a given GTFS entity type and instantiated with the given
   * context.
   *
   * <p>Use {@code invokeSingleEntityValidators()} to invoke the created validators on a given
   * entity.
   *
   * @param clazz class of the GTFS entity
   * @param validationContext context to pass to all validators
   * @param <T> type of the GTFS entity
   * @return a list of validators
   */
  public <T extends GtfsEntity> List<SingleEntityValidator<T>> createSingleEntityValidators(
      Class<T> clazz, ValidationContext validationContext) {
    List<SingleEntityValidator<T>> validators = new ArrayList<>();
    for (Class<? extends SingleEntityValidator<?>> validatorClass :
        singleEntityValidators.get(clazz)) {
      try {
        SingleEntityValidator<T> validator =
            ((Class<? extends SingleEntityValidator<T>>) validatorClass)
                .getConstructor()
                .newInstance();
        for (Field field : validatorClass.getDeclaredFields()) {
          maybeInjectValidatorContext(validator, field, validationContext);
        }
        validators.add(validator);
      } catch (ReflectiveOperationException e) {
        logger.atSevere().withCause(e).log(
            "Cannot instantiate validator %s", validatorClass.getCanonicalName());
      }
    }
    return validators;
  }

  /**
   * Invokes all single-entity validators in the list.
   *
   * <p>Use {@code createSingleEntityValidators()} to create validators that can be passed here.
   *
   * @param entity GTFS entity to validate
   * @param validators list of single-entity validators
   * @param noticeContainer container for accumulating notices
   * @param <T> type of the GTFS entity
   */
  public static <T extends GtfsEntity> void invokeSingleEntityValidators(
      T entity, List<SingleEntityValidator<T>> validators, NoticeContainer noticeContainer)
      throws ErrorDetectedException {
    for (SingleEntityValidator<T> validator : validators) {
      validator.validate(entity, noticeContainer);
    }
  }

  /**
   * Invokes single-file validators on a given table.
   *
   * @param table GTFS table to validate
   * @param validationContext context to pass to all validators
   * @param noticeContainer container for accumulating notices
   * @param <T> type of the GTFS entity
   */
  public <T extends GtfsEntity> void invokeSingleFileValidators(
      GtfsTableContainer<T> table,
      ValidationContext validationContext,
      NoticeContainer noticeContainer) throws ErrorDetectedException {
    for (Class<? extends FileValidator> validatorClass :
        singleFileValidators.get(table.getClass())) {
      FileValidator validator;
      try {
        validator = createValidator(validatorClass, table, validationContext);
      } catch (ReflectiveOperationException e) {
        logger.atSevere().withCause(e).log(
            "Cannot instantiate validator %s", validatorClass.getCanonicalName());
        continue;
      }
      validator.validate(noticeContainer);
    }
  }

  private FileValidator createValidator(
      Class<? extends FileValidator> clazz,
      GtfsTableContainer table,
      ValidationContext validationContext)
      throws ReflectiveOperationException {
    FileValidator validator = clazz.getConstructor().newInstance();
    for (Field field : clazz.getDeclaredFields()) {
      if (maybeInjectValidatorContext(validator, field, validationContext)) {
        continue;
      }
      if (!isTableInjectableField(field)) {
        continue;
      }
      if (!field.getType().isAssignableFrom(table.getClass())) {
        throw new InstantiationException(
            "Cannot inject a field of type " + field.getType().getSimpleName());
      }
      field.set(validator, table);
    }
    return validator;
  }

  private FileValidator createValidator(
      Class<? extends FileValidator> clazz,
      GtfsFeedContainer feed,
      ValidationContext validationContext)
      throws ReflectiveOperationException {
    FileValidator validator = clazz.getConstructor().newInstance();
    for (Field field : clazz.getDeclaredFields()) {
      if (maybeInjectValidatorContext(validator, field, validationContext)) {
        continue;
      }
      if (!isTableInjectableField(field)) {
        continue;
      }
      GtfsTableContainer table =
          feed.getTable((Class<? extends GtfsTableContainer>) field.getType());
      if (table == null) {
        throw new InstantiationException(
            "Cannot find " + field.getType().getSimpleName() + " in feed container");
      }
      field.set(validator, table);
    }
    return validator;
  }

  private boolean maybeInjectValidatorContext(
      Object validator, Field field, ValidationContext validationContext)
      throws IllegalAccessException {
    if (!(field.isAnnotationPresent(Inject.class)
        && field.getType().isAssignableFrom(ValidationContext.class))) {
      return false;
    }
    field.set(validator, validationContext);
    return true;
  }

  public List<FileValidator> createMultiFileValidators(
      GtfsFeedContainer feed, ValidationContext validationContext) {
    ArrayList<FileValidator> validators = new ArrayList<>();
    validators.ensureCapacity(multiFileValidators.size());
    for (Class<? extends FileValidator> validatorClass : multiFileValidators) {
      try {
        validators.add(createValidator(validatorClass, feed, validationContext));
      } catch (ReflectiveOperationException e) {
        logger.atSevere().withCause(e).log(
            "Cannot instantiate validator %s", validatorClass.getCanonicalName());
      }
    }
    return validators;
  }

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
              Class<? extends GtfsTableContainer>, Collection<Class<? extends FileValidator>>>
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
