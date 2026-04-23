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

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.time.LocalDate;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.input.DateForValidation;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.TableStatus;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestEntityValidator;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestSingleFileValidator;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestTableContainer;
import org.mobilitydata.gtfsvalidator.testgtfs.WholeFeedValidator;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader.ValidatorWithDependencyStatus;

public class ValidatorLoaderTest {
  private static final CountryCode COUNTRY_CODE = CountryCode.forStringOrUnknown("AU");
  private static final DateForValidation CURRENT_DATE =
      new DateForValidation(LocalDate.of(2021, 1, 1));
  private static final ValidationContext VALIDATION_CONTEXT =
      ValidationContext.builder()
          .setCountryCode(COUNTRY_CODE)
          .setDateForValidation(CURRENT_DATE)
          .build();

  @Test
  public void createValidatorWithContext_injectsContext()
      throws ReflectiveOperationException, ValidatorLoaderException {
    GtfsTestEntityValidator validator =
        ValidatorLoader.createValidatorWithContext(
                GtfsTestEntityValidator.class, VALIDATION_CONTEXT)
            .validator();

    assertThat(validator.getCountryCode()).isEqualTo(VALIDATION_CONTEXT.countryCode());
    assertThat(validator.getDateForValidation()).isEqualTo(VALIDATION_CONTEXT.dateForValidation());
  }

  @Test
  public void createSingleFileValidator_injectsTableContainerAndContext()
      throws ReflectiveOperationException, ValidatorLoaderException {
    GtfsTestTableContainer table = new GtfsTestTableContainer(TableStatus.EMPTY_FILE);
    GtfsTestSingleFileValidator validator =
        (GtfsTestSingleFileValidator)
            ValidatorLoader.createSingleFileValidator(
                    GtfsTestSingleFileValidator.class, table, VALIDATION_CONTEXT)
                .validator();

    assertThat(validator.getCountryCode()).isEqualTo(VALIDATION_CONTEXT.countryCode());
    assertThat(validator.getDateForValidation()).isEqualTo(VALIDATION_CONTEXT.dateForValidation());
    assertThat(validator.getStopTable()).isEqualTo(table);
  }

  @Test
  public void createMultiFileValidator_injectsFeedContainerAndContext()
      throws ReflectiveOperationException, ValidatorLoaderException {
    GtfsTestTableContainer stopTable =
        new GtfsTestTableContainer(TableStatus.PARSABLE_HEADERS_AND_ROWS);
    GtfsFeedContainer feedContainer = new GtfsFeedContainer(ImmutableList.of(stopTable));

    ValidatorWithDependencyStatus<WholeFeedValidator> validatorWithStatus =
        ValidatorLoader.createMultiFileValidator(
            WholeFeedValidator.class, feedContainer, VALIDATION_CONTEXT);
    assertThat(validatorWithStatus.dependenciesHaveErrors()).isFalse();

    WholeFeedValidator validator = validatorWithStatus.validator();
    assertThat(validator.getCountryCode()).isEqualTo(VALIDATION_CONTEXT.countryCode());
    assertThat(validator.getDateForValidation()).isEqualTo(VALIDATION_CONTEXT.dateForValidation());
    assertThat(validator.getFeedContainer()).isEqualTo(feedContainer);
  }

  @Test
  public void createMultiFileValidator_singleContainer_dependenciesHaveErrors()
      throws ReflectiveOperationException, ValidatorLoaderException {
    GtfsTestTableContainer table = new GtfsTestTableContainer(TableStatus.UNPARSABLE_ROWS);
    GtfsFeedContainer feedContainer = new GtfsFeedContainer(ImmutableList.of(table));

    ValidatorWithDependencyStatus<GtfsTestSingleFileValidator> validatorWithStatus =
        ValidatorLoader.createMultiFileValidator(
            GtfsTestSingleFileValidator.class, feedContainer, VALIDATION_CONTEXT);

    assertThat(validatorWithStatus.dependenciesHaveErrors()).isTrue();
    assertThat(validatorWithStatus.validator().getStopTable()).isEqualTo(table);
  }
}
