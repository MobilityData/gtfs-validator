/*
 * Copyright 2021 MobilityData IO
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

package org.mobilitydata.gtfsvalidator.input;

import static com.google.common.truth.Truth.assertThat;

import java.time.LocalDate;
import org.junit.Test;

public class DateForValidationTest {
  private static final LocalDate TEST_NOW = LocalDate.of(2021, 1, 1);
  private static final LocalDate OTHER_DATE_TIME = LocalDate.of(2021, 4, 1);

  @Test
  public void getDate() {
    DateForValidation dateForValidation = new DateForValidation(TEST_NOW);
    assertThat(dateForValidation.getDate()).isEqualTo(TEST_NOW);
  }

  @Test
  public void testEquals() {
    assertThat(new DateForValidation(TEST_NOW).equals(new DateForValidation(TEST_NOW))).isTrue();
    assertThat(new DateForValidation(TEST_NOW).equals(new DateForValidation(OTHER_DATE_TIME)))
        .isFalse();
  }
}
