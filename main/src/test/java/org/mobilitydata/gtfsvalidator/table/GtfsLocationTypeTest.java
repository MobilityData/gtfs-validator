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
public class GtfsLocationTypeTest {
  @Test
  public void shouldReturnEnumValue() {
    assertThat(GtfsLocationType.forNumber(0)).isEqualTo(GtfsLocationType.STOP);
    assertThat(GtfsLocationType.forNumber(1)).isEqualTo(GtfsLocationType.STATION);
    assertThat(GtfsLocationType.forNumber(2)).isEqualTo(GtfsLocationType.ENTRANCE);
    assertThat(GtfsLocationType.forNumber(3)).isEqualTo(GtfsLocationType.GENERIC_NODE);
    assertThat(GtfsLocationType.forNumber(4)).isEqualTo(GtfsLocationType.BOARDING_AREA);
  }

  @Test
  public void shouldReturnNull() {
    assertThat(GtfsLocationType.forNumber(-1)).isNull();
    assertThat(GtfsLocationType.forNumber(5)).isNull();
  }
}
