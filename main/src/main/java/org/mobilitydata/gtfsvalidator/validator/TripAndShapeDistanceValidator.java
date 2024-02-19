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

  private final GtfsShapeTableContainer shapeTable;

  @Inject
  TripAndShapeDistanceValidator(
      GtfsTripTableContainer tripTable,
      GtfsStopTimeTableContainer stopTimeTable,
      GtfsShapeTableContainer shapeTable) {
    this.tripTable = tripTable;
    this.stopTimeTable = stopTimeTable;
    this.shapeTable = shapeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    tripTable
        .getEntities()
        .forEach(
            trip -> {
              String shapeId = trip.shapeId();
              // Get distance for trip
              int nbStopTimes = stopTimeTable.byTripId(trip.tripId()).size();
              if (nbStopTimes == 0) {
                return;
              }
              double maxStopTimeDist =
                  stopTimeTable.byTripId(trip.tripId()).get(nbStopTimes - 1).shapeDistTraveled();

              // Get max shape distance for trip
              double maxShapeDist =
                  shapeTable.byShapeId(shapeId).stream()
                      .mapToDouble(GtfsShape::shapeDistTraveled)
                      .max()
                      .orElse(Double.POSITIVE_INFINITY);
              if (maxStopTimeDist > maxShapeDist) {
                noticeContainer.addValidationNotice(
                    new TripDistanceExceedsShapeDistanceNotice(
                        trip.tripId(), shapeId, maxStopTimeDist, maxShapeDist));
              }
            });
  }

  /** The distance traveled by a trip should be less or equal to the max length of its shape. */
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

    TripDistanceExceedsShapeDistanceNotice(
        String tripId,
        String shapeId,
        double maxTripDistanceTraveled,
        double maxShapeDistanceTraveled) {
      this.tripId = tripId;
      this.shapeId = shapeId;
      this.maxShapeDistanceTraveled = maxShapeDistanceTraveled;
      this.maxTripDistanceTraveled = maxTripDistanceTraveled;
    }
  }
}
