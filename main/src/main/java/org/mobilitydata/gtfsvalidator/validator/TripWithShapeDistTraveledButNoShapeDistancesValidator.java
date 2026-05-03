/*
 * Copyright 2025 MobilityData
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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import com.google.common.collect.Multimaps;
import java.util.List;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

/**
 * Validates that if a trip has shape_dist_traveled values in stop_times.txt and references a shape
 * via shape_id, the corresponding shape points in shapes.txt also have shape_dist_traveled values.
 *
 * <p>When stop times carry shape_dist_traveled but the referenced shape does not, the distance
 * values cannot be used to align stops to the shape, which may cause incorrect routing or display
 * behaviour for consumers.
 *
 * <p>Generated notice: {@link TripWithShapeDistTraveledButNoShapeDistancesNotice}.
 */
@GtfsValidator
public class TripWithShapeDistTraveledButNoShapeDistancesValidator extends FileValidator {

  private final GtfsTripTableContainer tripTable;
  private final GtfsStopTimeTableContainer stopTimeTable;
  private final GtfsShapeTableContainer shapeTable;

  @Inject
  TripWithShapeDistTraveledButNoShapeDistancesValidator(
      GtfsTripTableContainer tripTable,
      GtfsStopTimeTableContainer stopTimeTable,
      GtfsShapeTableContainer shapeTable) {
    this.tripTable = tripTable;
    this.stopTimeTable = stopTimeTable;
    this.shapeTable = shapeTable;
  }

  @Override
  public boolean shouldCallValidate() {
    // Only run when stop_times.txt actually contains a shape_dist_traveled column.
    return stopTimeTable.hasColumn(GtfsStopTime.SHAPE_DIST_TRAVELED_FIELD_NAME);
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (List<GtfsStopTime> stopTimesForTrip :
        Multimaps.asMap(stopTimeTable.byTripIdMap()).values()) {

      // Check whether any stop time in this trip carries a shape_dist_traveled value.
      boolean stopTimesHaveShapeDistTraveled =
          stopTimesForTrip.stream().anyMatch(GtfsStopTime::hasShapeDistTraveled);
      if (!stopTimesHaveShapeDistTraveled) {
        continue;
      }

      // Look up the trip to get its shape_id.
      String tripId = stopTimesForTrip.get(0).tripId();
      GtfsTrip trip = tripTable.byTripId(tripId).orElse(null);
      if (trip == null || !trip.hasShapeId() || trip.shapeId().isEmpty()) {
        // No associated shape – nothing to check.
        continue;
      }

      // Check whether any shape point in the referenced shape has a shape_dist_traveled value.
      List<GtfsShape> shapePoints = shapeTable.byShapeId(trip.shapeId());
      if (shapePoints.isEmpty()) {
        // Missing shape is reported by a foreign-key rule; skip here.
        continue;
      }
      boolean shapeHasDistTraveled =
          shapePoints.stream().anyMatch(GtfsShape::hasShapeDistTraveled);
      if (!shapeHasDistTraveled) {
        // Find the first stop time that carries shape_dist_traveled for the notice context.
        GtfsStopTime firstStopTimeWithDist =
            stopTimesForTrip.stream()
                .filter(GtfsStopTime::hasShapeDistTraveled)
                .findFirst()
                .orElseThrow(); // safe: we already confirmed at least one exists

        noticeContainer.addValidationNotice(
            new TripWithShapeDistTraveledButNoShapeDistancesNotice(
                trip.csvRowNumber(),
                tripId,
                trip.shapeId(),
                firstStopTimeWithDist.csvRowNumber()));
      }
    }
  }

  /**
   * A trip has shape_dist_traveled values in stop_times.txt but the shape referenced by the trip's
   * shape_id has no shape_dist_traveled values in shapes.txt.
   *
   * <p>When stop times define distance values but the shape does not, consumers cannot use those
   * distances to align stops to the shape geometry. This inconsistency may cause incorrect routing
   * or display behaviour.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files =
          @FileRefs({
            GtfsTripSchema.class,
            GtfsStopTimeSchema.class,
            GtfsShapeSchema.class,
          }))
  public static class TripWithShapeDistTraveledButNoShapeDistancesNotice extends ValidationNotice {

    /** The row number of the faulty record in trips.txt. */
    private final long tripCsvRowNumber;

    /** The trip_id of the faulty trip. */
    private final String tripId;

    /** The shape_id referenced by the trip. */
    private final String shapeId;

    /**
     * The row number of the first stop_times.txt record for this trip that contains a
     * shape_dist_traveled value.
     */
    private final long stopTimeCsvRowNumber;

    TripWithShapeDistTraveledButNoShapeDistancesNotice(
        long tripCsvRowNumber, String tripId, String shapeId, long stopTimeCsvRowNumber) {
      this.tripCsvRowNumber = tripCsvRowNumber;
      this.tripId = tripId;
      this.shapeId = shapeId;
      this.stopTimeCsvRowNumber = stopTimeCsvRowNumber;
    }
  }
}
