/*
 * Copyright 2020 Google LLC, MobilityData IO
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

package org.mobilitydata.gtfsvalidator.table;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GtfsPathwayModeTest {
  @Test
  public void shouldReturnEnumValue() {
    assertThat(GtfsPathwayMode.forNumber(1)).isEqualTo(GtfsPathwayMode.WALKWAY);
    assertThat(GtfsPathwayMode.forNumber(2)).isEqualTo(GtfsPathwayMode.STAIRS);
    assertThat(GtfsPathwayMode.forNumber(3)).isEqualTo(GtfsPathwayMode.MOVING_SIDEWALK);
    assertThat(GtfsPathwayMode.forNumber(4)).isEqualTo(GtfsPathwayMode.ESCALATOR);
    assertThat(GtfsPathwayMode.forNumber(5)).isEqualTo(GtfsPathwayMode.ELEVATOR);
    assertThat(GtfsPathwayMode.forNumber(6)).isEqualTo(GtfsPathwayMode.FARE_GATE);
    assertThat(GtfsPathwayMode.forNumber(7)).isEqualTo(GtfsPathwayMode.EXIT_GATE);
  }

  @Test
  public void shouldReturnNull() {
    assertThat(GtfsPathwayMode.forNumber(-1)).isNull();
    assertThat(GtfsPathwayMode.forNumber(12)).isNull();
  }
}
