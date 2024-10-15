/*
 * Copyright 2021 Google LLC
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

import static org.mobilitydata.gtfsvalidator.table.GtfsFeedLoader.SkippedValidatorReason.*;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.flogger.FluentLogger;
import java.util.ArrayList;
import java.util.List;
import org.mobilitydata.gtfsvalidator.table.GtfsEntity;
import org.mobilitydata.gtfsvalidator.table.GtfsEntityContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedLoader;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTableDescriptor;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader.ValidatorWithDependencyStatus;

/** Default implementation of {@link ValidatorProvider}. */
public class DefaultValidatorProvider implements ValidatorProvider {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private final ValidationContext validationContext;
  private final GtfsFieldValidator fieldValidator;
  private final TableHeaderValidator tableHeaderValidator;
  private final ListMultimap<Class<? extends GtfsEntity>, Class<? extends SingleEntityValidator<?>>>
      singleEntityValidators;
  private final ListMultimap<
          Class<? extends GtfsEntityContainer<?, ?>>, Class<? extends FileValidator>>
      singleFileValidators;
  private final List<Class<? extends FileValidator>> multiFileValidators;

  /** Creates a validator provider that uses given validators. */
  public DefaultValidatorProvider(
      ValidationContext validationContext,
      ValidatorLoader validatorLoader,
      GtfsFieldValidator fieldValidator,
      TableHeaderValidator tableHeaderValidator) {
    this.validationContext = validationContext;
    this.fieldValidator = fieldValidator;
    this.tableHeaderValidator = tableHeaderValidator;
    this.singleEntityValidators = validatorLoader.getSingleEntityValidators();
    this.singleFileValidators = validatorLoader.getSingleFileValidators();
    this.multiFileValidators = validatorLoader.getMultiFileValidators();
  }

  /** Creates a validator provider that uses default validators for fields and headers. */
  public DefaultValidatorProvider(
      ValidationContext validationContext, ValidatorLoader validatorLoader) {
    this(
        validationContext,
        validatorLoader,
        new DefaultFieldValidator(validationContext.countryCode()),
        new DefaultTableHeaderValidator());
  }

  @Override
  public GtfsFieldValidator getFieldValidator() {
    return fieldValidator;
  }

  @Override
  public TableHeaderValidator getTableHeaderValidator() {
    return tableHeaderValidator;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends GtfsEntity> List<SingleEntityValidator<T>> createSingleEntityValidators(
      Class<T> clazz,
      ColumnInspector header,
      Multimap<GtfsFeedLoader.SkippedValidatorReason, Class<?>> skippedValidators) {
    List<SingleEntityValidator<T>> validators = new ArrayList<>();
    for (Class<? extends SingleEntityValidator<?>> validatorClass :
        singleEntityValidators.get(clazz)) {
      try {
        ValidatorWithDependencyStatus<? extends SingleEntityValidator<?>>
            validatorWithDependencyStatus =
                ValidatorLoader.createValidatorWithContext(
                    ((Class<? extends SingleEntityValidator<?>>) validatorClass),
                    validationContext);
        if (validatorWithDependencyStatus.dependenciesHaveErrors()) {
          skippedValidators.put(SINGLE_ENTITY_VALIDATORS_WITH_ERROR, validatorClass);
        } else {
          var validator = validatorWithDependencyStatus.validator();
          if (validator.shouldCallValidate(header)) {
            validators.add((SingleEntityValidator<T>) validator);
          } else {
            skippedValidators.put(VALIDATORS_NO_NEED_TO_RUN, validatorClass);
          }
        }
      } catch (ReflectiveOperationException | ValidatorLoaderException e) {
        logger.atSevere().withCause(e).log(
            "Cannot instantiate validator %s", validatorClass.getCanonicalName());
      }
    }
    return validators;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends GtfsEntity, D extends GtfsTableDescriptor>
      List<FileValidator> createSingleFileValidators(
          GtfsEntityContainer<T, D> table,
          Multimap<GtfsFeedLoader.SkippedValidatorReason, Class<?>> skippedValidators) {
    List<FileValidator> validators = new ArrayList<>();
    for (Class<? extends FileValidator> validatorClass :
        singleFileValidators.get((Class<? extends GtfsTableContainer<?, ?>>) table.getClass())) {
      try {
        ValidatorWithDependencyStatus<? extends FileValidator> validatorWithStatus =
            ValidatorLoader.createSingleFileValidator(validatorClass, table, validationContext);
        if (validatorWithStatus.dependenciesHaveErrors()) {
          skippedValidators.put(SINGLE_FILE_VALIDATORS_WITH_ERROR, validatorClass);
        } else if (validatorWithStatus.validator().shouldCallValidate()) {
          validators.add(validatorWithStatus.validator());
        } else {
          skippedValidators.put(VALIDATORS_NO_NEED_TO_RUN, validatorClass);
        }
      } catch (ReflectiveOperationException | ValidatorLoaderException e) {
        logger.atSevere().withCause(e).log(
            "Cannot instantiate validator %s", validatorClass.getCanonicalName());
      }
    }
    return validators;
  }

  @Override
  public List<FileValidator> createMultiFileValidators(
      GtfsFeedContainer feed,
      Multimap<GtfsFeedLoader.SkippedValidatorReason, Class<?>> skippedValidators) {
    ArrayList<FileValidator> validators = new ArrayList<>();
    validators.ensureCapacity(multiFileValidators.size());
    for (Class<? extends FileValidator> validatorClass : multiFileValidators) {
      try {
        ValidatorWithDependencyStatus<? extends FileValidator> validatorWithStatus =
            ValidatorLoader.createMultiFileValidator(validatorClass, feed, validationContext);
        if (validatorWithStatus.dependenciesHaveErrors()) {
          skippedValidators.put(MULTI_FILE_VALIDATORS_WITH_ERROR, validatorClass);
        } else {
          if (validatorWithStatus.validator().shouldCallValidate()) {
            validators.add(validatorWithStatus.validator());
          } else {
            skippedValidators.put(VALIDATORS_NO_NEED_TO_RUN, validatorClass);
          }
        }
      } catch (ReflectiveOperationException | ValidatorLoaderException e) {
        logger.atSevere().withCause(e).log(
            "Cannot instantiate validator %s", validatorClass.getCanonicalName());
      }
    }
    return validators;
  }
}
