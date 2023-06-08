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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer.TableStatus;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestEntityValidator;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestFileValidator;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestTableContainer;
import org.mobilitydata.gtfsvalidator.testgtfs.WholeFeedValidator;

public class ValidatorLoaderTest {
  private static final CountryCode COUNTRY_CODE = CountryCode.forStringOrUnknown("AU");
  private static final CurrentDateTime CURRENT_DATE_TIME =
      new CurrentDateTime(ZonedDateTime.of(2021, 1, 1, 14, 30, 0, 0, ZoneOffset.UTC));
  private static final ValidationContext VALIDATION_CONTEXT =
      ValidationContext.builder()
          .setCountryCode(COUNTRY_CODE)
          .setCurrentDateTime(CURRENT_DATE_TIME)
          .build();

  @Test
  public void createValidatorWithContext_injectsContext() throws ReflectiveOperationException {
    GtfsTestEntityValidator validator =
        ValidatorLoader.createValidatorWithContext(
            GtfsTestEntityValidator.class, VALIDATION_CONTEXT);

    assertThat(validator.getCountryCode()).isEqualTo(VALIDATION_CONTEXT.countryCode());
    assertThat(validator.getCurrentDateTime()).isEqualTo(VALIDATION_CONTEXT.currentDateTime());
  }

  @Test
  public void createSingleFileValidator_injectsTableContainerAndContext()
      throws ReflectiveOperationException {
    GtfsTestTableContainer table = new GtfsTestTableContainer(TableStatus.EMPTY_FILE);
    GtfsTestFileValidator validator =
        (GtfsTestFileValidator)
            ValidatorLoader.createSingleFileValidator(
                GtfsTestFileValidator.class, table, VALIDATION_CONTEXT);

    assertThat(validator.getCountryCode()).isEqualTo(VALIDATION_CONTEXT.countryCode());
    assertThat(validator.getCurrentDateTime()).isEqualTo(VALIDATION_CONTEXT.currentDateTime());
    assertThat(validator.getStopTable()).isEqualTo(table);
  }

  @Test
  public void createMultiFileValidator_injectsFeedContainerAndContext()
      throws ReflectiveOperationException {
    GtfsTestTableContainer stopTable = new GtfsTestTableContainer(TableStatus.EMPTY_FILE);
    GtfsFeedContainer feedContainer = new GtfsFeedContainer(ImmutableList.of(stopTable));
    WholeFeedValidator validator =
        (WholeFeedValidator)
            ValidatorLoader.createMultiFileValidator(
                    WholeFeedValidator.class, feedContainer, VALIDATION_CONTEXT)
                .validator();

    assertThat(validator.getCountryCode()).isEqualTo(VALIDATION_CONTEXT.countryCode());
    assertThat(validator.getCurrentDateTime()).isEqualTo(VALIDATION_CONTEXT.currentDateTime());
    assertThat(validator.getFeedContainer()).isEqualTo(feedContainer);
  }
}
