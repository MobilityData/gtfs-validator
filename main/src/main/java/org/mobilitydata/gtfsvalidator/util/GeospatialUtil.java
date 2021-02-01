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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.locationtech.spatial4j.distance.DistanceCalculator;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.Shape;
import org.locationtech.spatial4j.shape.ShapeFactory;
import org.locationtech.spatial4j.shape.SpatialRelation;
import org.mobilitydata.gtfsvalidator.notice.StopTooFarFromTripShapeNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;

public class GeospatialUtil {
  static final int KILOMETER_TO_METER_CONVERSION_FACTOR =
      1000; // conversion factor from kilometers to meters
  static final double TRIP_BUFFER_METERS =
      100; // Per GTFS Best Practices (https://gtfs.org/best-practices/#shapestxt)
  public static final double TRIP_BUFFER_DEGREES =
      DistanceUtils.KM_TO_DEG * (TRIP_BUFFER_METERS / 1000.0d);

  private GeospatialUtil() {}

  /**
   * Method returning a {@code ShapeFactory}
   *
   * @return a {@link ShapeFactory}
   */
  private static ShapeFactory getShapeFactory() {
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
   * This method calculates distance between two sets of coordinates. Result is expressed in meters.
   *
   * @param fromLat latitude of the first coordinates
   * @param fromLng longitude of the first coordinates
   * @param toLat latitude of the second coordinates
   * @param toLng longitude of the second coordinates
   * @return the calculation result in meters
   */
  public static int distanceBetweenMeter(
      double fromLat, double fromLng, double toLat, double toLng) {
    final ShapeFactory shapeFactory = getShapeFactory();
    final DistanceCalculator distanceCalculator = getDistanceCalculator();
    final Point origin = shapeFactory.pointXY(fromLng, fromLat);
    final Point destination = shapeFactory.pointXY(toLng, toLat);
    return (int)
        (DistanceUtils.DEG_TO_KM
            * distanceCalculator.distance(origin, destination)
            * KILOMETER_TO_METER_CONVERSION_FACTOR);
  }

  /**
   * Returns a list of E052 errors for the given input, one for each stop that is too far from the
   * trip shapePoints
   *
   * @param trip Trip for this GTFS trip
   * @param stopTimes a map of StopTimes for a trip, sorted by stop_sequence
   * @param shapePoints a map of ShapePoints for a trip, sorted by shape_pt_sequence
   * @param stopTable a map of all stops (keyed on stop_id), needed to obtain the latitude and
   *     longitude for each stop
   * @param testedCache a cache for previously tested shape_id and stop_id pairs (keyed on
   *     shape_id+stop_id). If the combination of shape_id and stop_id appears in this set, we
   *     shouldn't test it again. Shapes and stops tested in this method execution will be added to
   *     this testedCache.
   * @return a list of E052 errors, one for each stop that is too far from the trip shapePoints
   */
  public static List<StopTooFarFromTripShapeNotice> checkStopsWithinTripShape(
      final GtfsTrip trip,
      final List<GtfsStopTime> stopTimes,
      final List<GtfsShape> shapePoints,
      final GtfsStopTableContainer stopTable,
      final Set<String> testedCache) {
    List<StopTooFarFromTripShapeNotice> errors = new ArrayList<>();
    if (trip == null
        || stopTimes == null
        || stopTimes.isEmpty()
        || shapePoints == null
        || shapePoints.isEmpty()) {
      // Nothing to do - return empty list
      return errors;
    }

    // Create a polyline from the GTFS shapes data
    ShapeFactory.LineStringBuilder lineBuilder = getShapeFactory().lineString();
    shapePoints.forEach(
        (shapePoint -> lineBuilder.pointXY(shapePoint.shapePtLon(), shapePoint.shapePtLat())));
    Shape shapeLine = lineBuilder.build();

    // Create the buffered version of the trip as a polygon
    Shape shapeBuffer = shapeLine.getBuffered(TRIP_BUFFER_DEGREES, shapeLine.getContext());

    // Check if each stop is within the buffer polygon
    stopTimes.forEach(
        stopTime -> {
          GtfsStop stop = stopTable.byStopId(stopTime.stopId());
          if (stop == null || stop.hasStopLat() || stop.hasStopLon()) {
            // Lat/lon are optional for location_type 4 - skip to the next stop if they aren't
            // provided
            return;
          }
          if (!(stop.locationType() == GtfsLocationType.STOP)
              && !(stop.locationType() == GtfsLocationType.BOARDING_AREA)) {
            // This rule only applies to stops of location_type 0 and 4 - skip to next stop
            return;
          }
          if (testedCache.contains(trip.shapeId() + stop.stopId())) {
            // We've already tested this combination of shape ID and stop ID - skip to next stop
            return;
          }
          testedCache.add(trip.shapeId() + stop.stopId());
          org.locationtech.spatial4j.shape.Point p =
              getShapeFactory().pointXY(stop.stopLon(), stop.stopLat());
          if (!shapeBuffer.relate(p).equals(SpatialRelation.CONTAINS)) {
            errors.add(
                new StopTooFarFromTripShapeNotice(
                    stopTime.stopId(),
                    stopTime.stopSequence(),
                    trip.tripId(),
                    trip.shapeId(),
                    TRIP_BUFFER_METERS));
          }
        });

    return errors;
  }
}
