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

package org.mobilitydata.gtfsvalidator.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GtfsTimeTest {
  @Test
  public void fromString() {
    assertThat(GtfsTime.fromString("12:20:30").getSecondsSinceMidnight())
        .isEqualTo(12 * 3600 + 20 * 60 + 30);
    assertThat(GtfsTime.fromString("2:34:12").getSecondsSinceMidnight())
        .isEqualTo(2 * 3600 + 34 * 60 + 12);
    assertThat(GtfsTime.fromString("101:34:12").getSecondsSinceMidnight())
        .isEqualTo(101 * 3600 + 34 * 60 + 12);

    assertThrows(IllegalArgumentException.class, () -> GtfsTime.fromString("0"));
    assertThrows(IllegalArgumentException.class, () -> GtfsTime.fromString("qwerty"));
    assertThrows(IllegalArgumentException.class, () -> GtfsTime.fromString("midnight"));
    assertThrows(IllegalArgumentException.class, () -> GtfsTime.fromString("1234:00:12"));
    assertThrows(IllegalArgumentException.class, () -> GtfsTime.fromString("prefix4:00:12suffix"));
  }
}
