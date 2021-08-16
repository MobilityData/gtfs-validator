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

import com.google.common.geometry.S1Angle;
import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2Point;

/** The earth modeled as a sphere. */
public class S2Earth {
  private S2Earth() {}

  /* Returns the distance between two points in meters. */
  public static double getDistanceMeters(S2Point a, S2Point b) {
    return radiansToMeters(a.angle(b));
  }

  /* Returns the distance between two points in meters. */
  public static double getDistanceMeters(S2LatLng a, S2LatLng b) {
    return toMeters(a.getDistance(b));
  }

  /* Returns the distance between two points in kilometers. */
  public static double getDistanceKm(S2Point a, S2Point b) {
    return getDistanceMeters(a, b) / 1000.0;
  }

  /* Returns the distance between two points in kilometers. */
  public static double getDistanceKm(S2LatLng a, S2LatLng b) {
    return getDistanceMeters(a, b) / 1000.0;
  }

  /* Converts an angle on the Earth surface to meters. */
  public static double toMeters(S1Angle angle) {
    return angle.radians() * getRadiusMeters();
  }

  /* Converts radians on the Earth surface to meters. */
  public static double radiansToMeters(double radians) {
    return radians * getRadiusMeters();
  }

  /* Returns the Earth's mean radius, which is the radius of the equivalent
     sphere with the same surface area. According to NASA, this value is
     6371.01 +/- 0.02 km.  The equatorial radius is 6378.136 km, and the polar
     radius is 6356.752 km.  They differ by one part in 298.257.

     Reference: http://ssd.jpl.nasa.gov/phys_props_earth.html, which quotes
     Yoder, C.F. 1995. "Astrometric and Geodetic Properties of Earth and the
     Solar System" in Global Earth Physics, A Handbook of Physical ants,
     AGU Reference Shelf 1, American Geophysical Union, Table 2.
  */
  public static double getRadiusMeters() {
    return 6371010.0;
  }
}
