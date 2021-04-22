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

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.Test;

public class CurrentDateTimeTest {
  private static final ZonedDateTime TEST_NOW =
      ZonedDateTime.of(2021, 1, 1, 14, 30, 0, 0, ZoneOffset.UTC);
  private static final ZonedDateTime OTHER_DATE_TIME =
      ZonedDateTime.of(2021, 4, 1, 14, 30, 0, 0, ZoneOffset.UTC);

  @Test
  public void getNow() {
    CurrentDateTime currentDateTime = new CurrentDateTime(TEST_NOW);
    assertThat(currentDateTime.getNow()).isEqualTo(TEST_NOW);
  }

  @Test
  public void testEquals() {
    assertThat(new CurrentDateTime(TEST_NOW).equals(new CurrentDateTime(TEST_NOW))).isTrue();
    assertThat(new CurrentDateTime(TEST_NOW).equals(new CurrentDateTime(OTHER_DATE_TIME))).isFalse();
  }
}
