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

package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2Point;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Testing S2Earth. */
@RunWith(JUnit4.class)
public class S2EarthTest {

  @Test
  public void testGetDistanceMetersS2Point() {
    S2Point north = new S2Point(0, 0, 1);
    S2Point south = new S2Point(0, 0, -1);
    S2Point west = new S2Point(0, -1, 0);

    assertThat(S2Earth.getDistanceMeters(north, south))
        .isEqualTo(Math.PI * S2Earth.getRadiusMeters());
    assertThat(S2Earth.getDistanceMeters(west, west)).isEqualTo(0);
    assertThat(S2Earth.getDistanceMeters(north, west))
        .isEqualTo((Math.PI / 2) * S2Earth.getRadiusMeters());

    assertThat(
            S2Earth.getDistanceMeters(S2LatLng.fromDegrees(0, -90), S2LatLng.fromDegrees(-90, -38)))
        .isWithin(1e-7)
        .of(S2Earth.getDistanceMeters(west, south));
    assertThat(
            S2Earth.getDistanceMeters(S2LatLng.fromRadians(0, 0.6), S2LatLng.fromRadians(0, -0.4)))
        .isEqualTo(S2Earth.getRadiusMeters());

    final double expected = S2Earth.getRadiusMeters() * Math.PI / 4;
    assertThat(
            S2Earth.getDistanceMeters(S2LatLng.fromDegrees(80, 27), S2LatLng.fromDegrees(55, -153)))
        .isWithin(Math.ulp(expected))
        .of(expected);
  }
}
