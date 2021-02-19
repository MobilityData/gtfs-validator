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

package org.mobilitydata.gtfsvalidator.validator;

import com.google.common.collect.Multimaps;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.Shape;
import org.locationtech.spatial4j.shape.ShapeFactory;
import org.locationtech.spatial4j.shape.SpatialRelation;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StopTooFarFromTripShapeNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.util.GeospatialUtil;

/**
 * Validates: a {@link GtfsStop} is within a distance threshold for a trip shape.
 *
 * <p>Generated notice: {@link StopTooFarFromTripShapeNotice}.
 *
 * <p>Time complexity: <i>O(n * p)</i>, where <i>n</i> is the number of records in
 * <i>stop_times.txt</i>
 * and <i>p</i> is the number of points in a trip shape. This assumes that the time complexity to
 * check if the stop location lies within the buffered trip shape is <i>O(p)</i> (see below).
 *
 * <p>In an extreme, not-very-likely case where a GTFS file consists of entirely of
 * trips that reuse the same shape but all have disjoint sets of stops along that shape, <i>p</i>
 * may be the number of records in <i>shapes.txt</i>. This is because every stop time must be
 * compared against every trip shape point.
 *
 * <p>However, in a typical case where most trips have independent shapes and the trips that do
 * re-use shapes share most of the same stops, the time complexity has a tighter bound of
 * <i>O(n)</i>, where <i>n</i> is the number of records in <i>stop_times.txt</i>. This is because
 * <i>p</i> is a fixed constant less than the number of records in <i>shapes.txt</i> and each point
 * is not processed more than once. Note that this point-in-polygon operation can still take a
 * reasonable amount of time to execute (the constant/overhead can be large) and should be
 * considered expensive. A cache is used to avoid calculating the distance between the same stop and
 * shape more than once when multiple trips share the same shape.
 *
 * <p>This validator uses spatial4j Euclidean operations to check if the stop location lies within
 * the buffered trip shape, which as mentioned above has a time complexity of <i>O(p)</i>, where p
 * is the number of points in a trip shape. See:
 * * https://github.com/locationtech/spatial4j/blob/1f6e2047f0574a430fc711cf2cd5adf141a8bda9/src/main/java/org/locationtech/spatial4j/shape/impl/BufferedLineString.java#L107
 * * https://github.com/locationtech/spatial4j/blob/1f6e2047f0574a430fc711cf2cd5adf141a8bda9/src/main/java/org/locationtech/spatial4j/shape/impl/InfBufLine.java#L81
 *
 * <p>A future running time optimization could be caching the buffered line for each shape when it
 * is first constructed. However, this is not currently implemented because shapes.txt is typically
 * a large file and this would have negative memory implications. This would help reduce the
 * initialization overhead for each shape IFF multiple trips share the same shape.
 *
 * <p>If additional accuracy is desired geodesic calculations may be use instead of Euclidean
 * calculations, although this comes at a performance cost - see https://github.com/MobilityData/gtfs-validator/pull/750#discussion_r578667817.
 */
@GtfsValidator
public class StopTooFarFromTripShapeValidator extends FileValidator {

  static final double TRIP_BUFFER_METERS =
      100.0d; // Per GTFS Best Practices (https://gtfs.org/best-practices/#shapestxt)
  static final double TRIP_BUFFER_DEGREES =
      DistanceUtils.KM_TO_DEG * TRIP_BUFFER_METERS
          * GeospatialUtil.METER_TO_KILOMETER_CONVERSION_FACTOR;

  @Inject
  GtfsStopTimeTableContainer stopTimeTable;
  @Inject
  GtfsTripTableContainer tripTable;
  @Inject
  GtfsShapeTableContainer shapeTable;
  @Inject
  GtfsStopTableContainer stopTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    // Cache for previously tested shape_id and stop_id pairs - we don't need to test them more than
    // once
    final Set<String> testedCache = new HashSet<>();

    for (Entry<String, List<GtfsStopTime>> entry :
        Multimaps.asMap(stopTimeTable.byTripIdMap()).entrySet()) {
      String tripId = entry.getKey();
      List<GtfsStopTime> stopTimesForTrip = entry.getValue();
      GtfsTrip trip = tripTable.byTripId(tripId);
      if (trip == null || !trip.hasShapeId()) {
        // No shape for this trip - skip to the next trip
        continue;
      }
      // Check for possible errors for this combination of stop times and shape points for
      // this trip_id
      List<StopTooFarFromTripShapeNotice> noticesForTrip =
          checkStopsWithinTripShape(
              tripId,
              stopTimesForTrip,
              trip.shapeId(),
              shapeTable.byShapeId(trip.shapeId()),
              stopTable,
              testedCache);
      for (StopTooFarFromTripShapeNotice notice : noticesForTrip) {
        noticeContainer.addValidationNotice(notice);
      }
    }
  }

  /**
   * Returns a list of notices for the given input, one for each stop that is too far from the trip
   * shapePoints
   *
   * @param tripId      trip_id for this GTFS trip
   * @param stopTimes   a list of StopTimes for a trip, sorted by stop_sequence
   * @param shapeId     the shape_id for this GTFS trip
   * @param shapePoints a list of ShapePoints for a trip, sorted by shape_pt_sequence
   * @param stopTable   a container for all stops (keyed on stop_id), needed to obtain the latitude
   *                    and longitude for each stop on the trip
   * @param testedCache a cache for previously tested shape_id and stop_id pairs (keyed on
   *                    shape_id+stop_id). If the combination of shape_id and stop_id appears in
   *                    this set, we shouldn't test it again. Shapes and stops tested in this method
   *                    execution will be added to this testedCache.
   * @return a list of notices, one for each stop that is too far from the trip shape
   */
  List<StopTooFarFromTripShapeNotice> checkStopsWithinTripShape(
      final String tripId,
      final List<GtfsStopTime> stopTimes,
      final String shapeId,
      final List<GtfsShape> shapePoints,
      final GtfsStopTableContainer stopTable,
      final Set<String> testedCache) {
    List<StopTooFarFromTripShapeNotice> notices = new ArrayList<>();
    if (stopTimes == null || stopTimes.isEmpty() || shapePoints == null || shapePoints.isEmpty()) {
      // Nothing to do - return empty list
      return notices;
    }

    // Create a buffered polyline from the GTFS shapes data - uses Euclidean operations (not geodesic)
    ShapeFactory.LineStringBuilder lineBuilder = GeospatialUtil.getShapeFactory().lineString();
    for (GtfsShape shapePoint : shapePoints) {
      lineBuilder.pointXY(shapePoint.shapePtLon(), shapePoint.shapePtLat());
    }
    lineBuilder.buffer(TRIP_BUFFER_DEGREES);
    Shape shapeBuffer = lineBuilder.build();

    // Check if each stop is within the buffer polygon
    for (GtfsStopTime stopTime : stopTimes) {
      GtfsStop stop = stopTable.byStopId(stopTime.stopId());
      if (stop == null || !stop.hasStopLat() || !stop.hasStopLon()) {
        // Lat/lon are optional for location_type 4 - skip to the next stop if they aren't
        // provided
        continue;
      }
      if (testedCache.contains(shapeId + stop.stopId())) {
        // We've already tested this combination of shape ID and stop ID - skip to next stop to
        // avoid spamming multiple duplicate notices and as a performance optimization
        continue;
      }
      testedCache.add(shapeId + stop.stopId());
      Point p = GeospatialUtil.getShapeFactory().pointXY(stop.stopLon(), stop.stopLat());
      if (!shapeBuffer.relate(p).equals(SpatialRelation.CONTAINS)) {
        notices.add(
            new StopTooFarFromTripShapeNotice(
                stopTime.stopId(), stopTime.stopSequence(), tripId, shapeId, TRIP_BUFFER_METERS));
      }
    }

    return notices;
  }
}
