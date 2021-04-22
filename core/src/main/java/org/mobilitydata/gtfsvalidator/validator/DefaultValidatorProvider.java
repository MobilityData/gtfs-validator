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

import com.google.common.collect.ListMultimap;
import com.google.common.flogger.FluentLogger;
import java.util.ArrayList;
import java.util.List;
import org.mobilitydata.gtfsvalidator.table.GtfsEntity;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer;

/** Default implementation of {@link ValidatorProvider}. */
public class DefaultValidatorProvider implements ValidatorProvider {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private final ValidationContext validationContext;
  private final GtfsFieldValidator fieldValidator;
  private final TableHeaderValidator tableHeaderValidator;
  private final ListMultimap<Class<? extends GtfsEntity>, Class<? extends SingleEntityValidator<?>>>
      singleEntityValidators;
  private final ListMultimap<Class<? extends GtfsTableContainer>, Class<? extends FileValidator>>
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
      Class<T> clazz) {
    List<SingleEntityValidator<T>> validators = new ArrayList<>();
    for (Class<? extends SingleEntityValidator<?>> validatorClass :
        singleEntityValidators.get(clazz)) {
      try {
        validators.add(
            ValidatorLoader.createValidatorWithContext(
                ((Class<? extends SingleEntityValidator<T>>) validatorClass), validationContext));
      } catch (ReflectiveOperationException e) {
        logger.atSevere().withCause(e).log(
            "Cannot instantiate validator %s", validatorClass.getCanonicalName());
      }
    }
    return validators;
  }

  @Override
  public <T extends GtfsEntity> List<FileValidator> createSingleFileValidators(
      GtfsTableContainer<T> table) {
    List<FileValidator> validators = new ArrayList<>();
    for (Class<? extends FileValidator> validatorClass :
        singleFileValidators.get(table.getClass())) {
      try {
        validators.add(
            ValidatorLoader.createSingleFileValidator(validatorClass, table, validationContext));
      } catch (ReflectiveOperationException e) {
        logger.atSevere().withCause(e).log(
            "Cannot instantiate validator %s", validatorClass.getCanonicalName());
        continue;
      }
    }
    return validators;
  }

  @Override
  public List<FileValidator> createMultiFileValidators(GtfsFeedContainer feed) {
    ArrayList<FileValidator> validators = new ArrayList<>();
    validators.ensureCapacity(multiFileValidators.size());
    for (Class<? extends FileValidator> validatorClass : multiFileValidators) {
      try {
        validators.add(
            ValidatorLoader.createMultiFileValidator(validatorClass, feed, validationContext));
      } catch (ReflectiveOperationException e) {
        logger.atSevere().withCause(e).log(
            "Cannot instantiate validator %s", validatorClass.getCanonicalName());
      }
    }
    return validators;
  }
}
