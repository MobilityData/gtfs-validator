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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;
import static org.mobilitydata.gtfsvalidator.util.S2Earth.getDistanceMeters;

import java.util.Comparator;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

/**
 * Validates that the distance traveled by a trip is lesser or equals the max length of its shape.
 *
 * <p>Generated notice: {@link TripDistanceExceedsShapeDistanceNotice}.
 */
@GtfsValidator
public class TripAndShapeDistanceValidator extends FileValidator {
  private final GtfsTripTableContainer tripTable;
  private final GtfsStopTimeTableContainer stopTimeTable;
  private final GtfsStopTableContainer stopTable;
  private final GtfsShapeTableContainer shapeTable;
  private final double DISTANCE_THRESHOLD = 11.1; // distance in meters

  @Inject
  TripAndShapeDistanceValidator(
      GtfsTripTableContainer tripTable,
      GtfsStopTimeTableContainer stopTimeTable,
      GtfsStopTableContainer stopTable,
      GtfsShapeTableContainer shapeTable) {
    this.tripTable = tripTable;
    this.stopTimeTable = stopTimeTable;
    this.shapeTable = shapeTable;
    this.stopTable = stopTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    tripTable
        .getEntities()
        .forEach(
            trip -> {
              String shapeId = trip.shapeId();
              String tripId = trip.tripId();

              // Get distance for trip
              int nbStopTimes = stopTimeTable.byTripId(tripId).size();
              if (nbStopTimes == 0) {
                return;
              }
              GtfsStopTime lastStopTime = stopTimeTable.byTripId(tripId).get(nbStopTimes - 1);
              GtfsStop stop = stopTable.byStopId(lastStopTime.stopId()).orElse(null);
              if (stop == null) {
                return;
              }
              double maxStopTimeDist = lastStopTime.shapeDistTraveled();

              // Get max shape distance for trip
              GtfsShape maxShape =
                  shapeTable.byShapeId(shapeId).stream()
                      .max(Comparator.comparingDouble(GtfsShape::shapeDistTraveled))
                      .orElse(null);
              if (maxShape == null) {
                return;
              }

              double maxShapeDist = maxShape.shapeDistTraveled();

              if (maxShapeDist == 0) {
                return;
              }

              double distanceInMeters =
                  getDistanceMeters(maxShape.shapePtLatLon(), stop.stopLatLon());
              if (maxStopTimeDist > maxShapeDist) {
                if (distanceInMeters > DISTANCE_THRESHOLD) {
                  noticeContainer.addValidationNotice(
                      new TripDistanceExceedsShapeDistanceNotice(
                          tripId, shapeId, maxStopTimeDist, maxShapeDist, distanceInMeters));
                } else {
                  noticeContainer.addValidationNotice(
                      new TripDistanceExceedsShapeDistanceBelowThresholdNotice(
                          tripId, shapeId, maxStopTimeDist, maxShapeDist, distanceInMeters));
                }
              }
            });
  }

  /**
   * The distance between the last shape point and last stop point is greater than or equal to the
   * 11.1m threshold.
   */
  @GtfsValidationNotice(
      severity = ERROR,
      files = @FileRefs({GtfsTrip.class, GtfsStopTime.class, GtfsShape.class}))
  static class TripDistanceExceedsShapeDistanceNotice extends ValidationNotice {

    /** The faulty record's trip id. */
    private final String tripId;

    /** The faulty record's shape id. */
    private final String shapeId;

    /** The faulty record's trip max distance traveled. */
    private final double maxTripDistanceTraveled;

    /** The faulty record's shape max distance traveled. */
    private final double maxShapeDistanceTraveled;

    /** The distance in meters between the shape and the stop. */
    private final double geoDistanceToShape;

    TripDistanceExceedsShapeDistanceNotice(
        String tripId,
        String shapeId,
        double maxTripDistanceTraveled,
        double maxShapeDistanceTraveled,
        double geoDistanceToShape) {
      this.tripId = tripId;
      this.shapeId = shapeId;
      this.maxShapeDistanceTraveled = maxShapeDistanceTraveled;
      this.maxTripDistanceTraveled = maxTripDistanceTraveled;
      this.geoDistanceToShape = geoDistanceToShape;
    }
  }

  /**
   * The distance between the last shape point and last stop point is greater than 0 but less than
   * the 11.1m threshold.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @FileRefs({GtfsTrip.class, GtfsStopTime.class, GtfsShape.class}))
  static class TripDistanceExceedsShapeDistanceBelowThresholdNotice extends ValidationNotice {

    /** The faulty record's trip id. */
    private final String tripId;

    /** The faulty record's shape id. */
    private final String shapeId;

    /** The faulty record's trip max distance traveled. */
    private final double maxTripDistanceTraveled;

    /** The faulty record's shape max distance traveled. */
    private final double maxShapeDistanceTraveled;

    /** The distance in meters between the shape and the stop. */
    private final double geoDistanceToShape;

    TripDistanceExceedsShapeDistanceBelowThresholdNotice(
        String tripId,
        String shapeId,
        double maxTripDistanceTraveled,
        double maxShapeDistanceTraveled,
        double geoDistanceToShape) {
      this.tripId = tripId;
      this.shapeId = shapeId;
      this.maxShapeDistanceTraveled = maxShapeDistanceTraveled;
      this.maxTripDistanceTraveled = maxTripDistanceTraveled;
      this.geoDistanceToShape = geoDistanceToShape;
    }
  }
}
