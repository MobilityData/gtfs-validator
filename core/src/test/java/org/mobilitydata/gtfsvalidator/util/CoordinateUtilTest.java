/*
 * Copyright 2026 MobilityData IO
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

import com.google.common.truth.Expect;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CoordinateUtilTest {
  @Rule public final Expect expect = Expect.create();

  @Test
  public void isNearOrigin() {
    expect.that(CoordinateUtil.isNearOrigin(0.0, 0.0)).isTrue();
    expect.that(CoordinateUtil.isNearOrigin(1.0, 1.0)).isTrue();
    expect.that(CoordinateUtil.isNearOrigin(-1.0, -1.0)).isTrue();
    expect.that(CoordinateUtil.isNearOrigin(0.5, -0.5)).isTrue();

    expect.that(CoordinateUtil.isNearOrigin(1.0001, 0.0)).isFalse();
    expect.that(CoordinateUtil.isNearOrigin(0.0, -1.0001)).isFalse();
    expect.that(CoordinateUtil.isNearOrigin(0.5, 30.0)).isFalse();
    expect.that(CoordinateUtil.isNearOrigin(45.5, -73.6)).isFalse();
  }

  @Test
  public void isNearPole() {
    expect.that(CoordinateUtil.isNearPole(89.0)).isTrue();
    expect.that(CoordinateUtil.isNearPole(-89.0)).isTrue();
    expect.that(CoordinateUtil.isNearPole(90.0)).isTrue();
    expect.that(CoordinateUtil.isNearPole(-90.0)).isTrue();

    expect.that(CoordinateUtil.isNearPole(88.9999)).isFalse();
    expect.that(CoordinateUtil.isNearPole(-88.9999)).isFalse();
    expect.that(CoordinateUtil.isNearPole(0.0)).isFalse();
  }

  @Test
  public void isNearOriginOrPole() {
    expect.that(CoordinateUtil.isNearOriginOrPole(0.0, 0.0)).isTrue();
    expect.that(CoordinateUtil.isNearOriginOrPole(89.5, 120.0)).isTrue();
    expect.that(CoordinateUtil.isNearOriginOrPole(-89.5, -120.0)).isTrue();

    expect.that(CoordinateUtil.isNearOriginOrPole(45.5, -73.6)).isFalse();
    expect.that(CoordinateUtil.isNearOriginOrPole(0.5, 30.0)).isFalse();
  }
}
