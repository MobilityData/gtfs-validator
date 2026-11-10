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

/**
 * Checks for coordinates that are likely invalid because they lie near the origin (0, 0) or near
 * one of the poles.
 *
 * <p>These are the bounds used to emit {@code PointNearOriginNotice} and {@code
 * PointNearPoleNotice}. Validators that compare coordinates with each other (e.g. distance checks)
 * can use them to skip points that are already reported as invalid.
 */
public final class CoordinateUtil {

  /** Maximum absolute latitude and longitude, in degrees, for a point to be near the origin. */
  public static final double ORIGIN_THRESHOLD_DEGREES = 1.0;

  /** Minimum absolute latitude, in degrees, for a point to be near a pole. */
  public static final double POLE_LAT_THRESHOLD_DEGREES = 89.0;

  private CoordinateUtil() {}

  /** Returns true if both {@code |lat|} and {@code |lon|} are within 1 degree of 0. */
  public static boolean isNearOrigin(double lat, double lon) {
    return Math.abs(lat) <= ORIGIN_THRESHOLD_DEGREES && Math.abs(lon) <= ORIGIN_THRESHOLD_DEGREES;
  }

  /** Returns true if {@code |lat|} is at least 89 degrees. */
  public static boolean isNearPole(double lat) {
    return Math.abs(lat) >= POLE_LAT_THRESHOLD_DEGREES;
  }

  /** Returns true if the point is near the origin or near one of the poles. */
  public static boolean isNearOriginOrPole(double lat, double lon) {
    return isNearOrigin(lat, lon) || isNearPole(lat);
  }
}
