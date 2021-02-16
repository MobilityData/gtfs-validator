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

import static org.mobilitydata.gtfsvalidator.util.GeospatialUtil.TRIP_BUFFER_DEGREES;
import static org.mobilitydata.gtfsvalidator.util.GeospatialUtil.TRIP_BUFFER_METERS;

import com.google.common.collect.Multimaps;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.Shape;
import org.locationtech.spatial4j.shape.ShapeFactory;
import org.locationtech.spatial4j.shape.SpatialRelation;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StopTooFarFromTripShapeNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
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
 */
@GtfsValidator
public class StopTooFarFromTripShapeValidator extends FileValidator {
  @Inject GtfsStopTimeTableContainer stopTimeTable;
  @Inject GtfsTripTableContainer tripTable;
  @Inject GtfsShapeTableContainer shapeTable;
  @Inject GtfsStopTableContainer stopTable;

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
   * @param tripId trip_id for this GTFS trip
   * @param stopTimes a list of StopTimes for a trip, sorted by stop_sequence
   * @param shapeId the shape_id for this GTFS trip
   * @param shapePoints a list of ShapePoints for a trip, sorted by shape_pt_sequence
   * @param stopTable a container for all stops (keyed on stop_id), needed to obtain the latitude
   *     and longitude for each stop on the trip
   * @param testedCache a cache for previously tested shape_id and stop_id pairs (keyed on
   *     shape_id+stop_id). If the combination of shape_id and stop_id appears in this set, we
   *     shouldn't test it again. Shapes and stops tested in this method execution will be added to
   *     this testedCache.
   * @return a list of notices, one for each stop that is too far from the trip shape
   */
  private List<StopTooFarFromTripShapeNotice> checkStopsWithinTripShape(
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

    // Create a polyline from the GTFS shapes data
    ShapeFactory.LineStringBuilder lineBuilder = GeospatialUtil.getShapeFactory().lineString();
    for (GtfsShape shapePoint : shapePoints) {
      lineBuilder.pointXY(shapePoint.shapePtLon(), shapePoint.shapePtLat());
    }
    Shape shapeLine = lineBuilder.build();

    // Create the buffered version of the trip as a polygon
    Shape shapeBuffer = shapeLine.getBuffered(TRIP_BUFFER_DEGREES, shapeLine.getContext());

    // Check if each stop is within the buffer polygon
    for (GtfsStopTime stopTime : stopTimes) {
      GtfsStop stop = stopTable.byStopId(stopTime.stopId());
      if (stop == null || !stop.hasStopLat() || !stop.hasStopLon()) {
        // Lat/lon are optional for location_type 4 - skip to the next stop if they aren't
        // provided
        continue;
      }
      if (!(stop.locationType() == GtfsLocationType.STOP)
          && !(stop.locationType() == GtfsLocationType.BOARDING_AREA)) {
        // This rule only applies to stops of location_type 0 and 4 - skip to next stop
        continue;
      }
      if (testedCache.contains(shapeId + stop.stopId())) {
        // We've already tested this combination of shape ID and stop ID - skip to next stop
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
