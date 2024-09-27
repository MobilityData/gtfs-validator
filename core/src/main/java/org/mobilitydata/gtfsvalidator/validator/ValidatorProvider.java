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

import java.util.List;
import java.util.function.Consumer;
import org.mobilitydata.gtfsvalidator.table.GtfsEntity;
import org.mobilitydata.gtfsvalidator.table.GtfsEntityContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTableDescriptor;

/**
 * Provider of all kinds of validators for fields, entities and files.
 *
 * <p>A {@code ValidatorProvider} is a handy pack of validators passed to different parts of the
 * system. Unit tests and users may provide their own implementations of {@code ValidatorProvider}.
 */
public interface ValidatorProvider {
  /** Returns a validator for individual fields. */
  GtfsFieldValidator getFieldValidator();

  /** Returns a validator for table headers. */
  TableHeaderValidator getTableHeaderValidator();

  /**
   * Creates a list of validators for a given GTFS entity type.
   *
   * <p>Use {@link ValidatorUtil#invokeSingleEntityValidators} to invoke the created validators on a
   * given entity.
   *
   * @param clazz class of the GTFS entity
   * @param <T> type of the GTFS entity
   * @return a list of validators
   */
  <T extends GtfsEntity> List<SingleEntityValidator<T>> createSingleEntityValidators(
      Class<T> clazz,
      Consumer<Class<? extends SingleEntityValidator<T>>> singleEntityValidatorsWithParsingErrors);

  /**
   * Creates a list of validators for the given table.
   *
   * <p>Use {@link ValidatorUtil#invokeSingleFileValidators} to invoke the created validators.
   *
   * @param table GTFS table to validate
   * @param <T> type of the GTFS entity
   */
  <T extends GtfsEntity, D extends GtfsTableDescriptor>
      List<FileValidator> createSingleFileValidators(
          GtfsEntityContainer<T, D> table,
          Consumer<Class<? extends FileValidator>> validatorsWithParsingErrors);

  /**
   * Creates a list of cross-table validators. Any validator that has a dependency with parse errors
   * will not be returned by this method, but instead noted with a call to the `skippedValidators`
   * callback.
   *
   * <p>Use {@link ValidatorUtil#safeValidate} to invoke each validator.
   *
   * @param feed GTFS feed to validate
   * @param skippedValidators
   */
  List<FileValidator> createMultiFileValidators(
      GtfsFeedContainer feed, Consumer<Class<? extends FileValidator>> skippedValidators);
}
