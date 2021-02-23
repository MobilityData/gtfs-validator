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

package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class GeospatialUtilTest {

  @Test
  public void distanceFromToSameCoordinateIsZero() {
    assertThat(GeospatialUtil.distanceInMeterBetween(45.508888, -73.561668, 45.508888, -73.561668))
        .isWithin(.01d)
        .of(0.0d);
  }

  @Test
  public void distanceReferenceCheck() {
    // geographic data extracted and validated with an external tool
    assertThat(GeospatialUtil.distanceInMeterBetween(45.508888, -73.561668, 45.507753, -73.562677))
        .isWithin(.01d)
        .of(148.69d);
  }
}
