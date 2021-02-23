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

import static org.locationtech.spatial4j.context.SpatialContext.GEO;

import org.locationtech.spatial4j.distance.DistanceCalculator;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.ShapeFactory;

public class GeospatialUtil {
  public static final double KILOMETER_TO_METER_CONVERSION_FACTOR =
      1000.0d; // conversion factor from kilometers to meters
  public static final double METER_TO_KILOMETER_CONVERSION_FACTOR =
      1 / 1000.0d; // conversion factor from kilometers to meters

  private GeospatialUtil() {}

  /**
   * Method returning a {@code ShapeFactory}
   *
   * @return a {@link ShapeFactory}
   */
  public static ShapeFactory getShapeFactory() {
    return GEO.getShapeFactory();
  }

  /**
   * Method returning a {@code DistanceCalculator}
   *
   * @return a {@link DistanceCalculator}
   */
  private static DistanceCalculator getDistanceCalculator() {
    return GEO.getDistCalc();
  }

  /**
   * Return the distance between two points given there lat/lon positions in meters. the distance is
   * computed following the haversine formula. See
   * https://locationtech.github.io/spatial4j/apidocs/org/locationtech/spatial4j/context/SpatialContext.html
   * Note that points of origin (from) and destination (to) can be swapped. Result is expressed in
   * meters.
   *
   * @param fromLat latitude of the first coordinates
   * @param fromLng longitude of the first coordinates
   * @param toLat latitude of the second coordinates
   * @param toLng longitude of the second coordinates
   * @return the calculation result in meters
   */
  public static double distanceInMeterBetween(
      double fromLat, double fromLng, double toLat, double toLng) {
    final ShapeFactory shapeFactory = getShapeFactory();
    final DistanceCalculator distanceCalculator = getDistanceCalculator();
    final Point origin = shapeFactory.pointXY(fromLng, fromLat);
    final Point destination = shapeFactory.pointXY(toLng, toLat);
    return DistanceUtils.DEG_TO_KM
        * distanceCalculator.distance(origin, destination)
        * KILOMETER_TO_METER_CONVERSION_FACTOR;
  }
}
