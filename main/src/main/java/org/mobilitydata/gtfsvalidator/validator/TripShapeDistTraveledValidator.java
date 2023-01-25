/*
 * Copyright 2023 Google LLC
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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

/**
 * Validates that trips specify {@code stop_times.shape_dist_traveled} values properly.
 * Please see generated notices for details.
 *
 * <p> Generated notices:
 * <ul>
 *   <li> {@link TripWithShapeDistTraveledButNoShapeNotice}
 *   <li> {@link TripWithShapeDistTraveledButNoShapeDistancesNotice}
 *   <li> {@link TripWithPartialShapeDistTraveledNotice}
 * </ul>
 */
@GtfsValidator
public class TripShapeDistTraveledValidator extends FileValidator {
  private final GtfsTripTableContainer tripTable;
  private final GtfsStopTimeTableContainer stopTimeTable;
  private final GtfsShapeTableContainer shapeTable;

  @Inject
  TripShapeDistTraveledValidator(GtfsTripTableContainer tripTable,
      GtfsStopTimeTableContainer stopTimeTable, GtfsShapeTableContainer shapeTable) {
    this.tripTable = tripTable;
    this.stopTimeTable = stopTimeTable;
    this.shapeTable = shapeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    final Set<String> shapesWithDistTraveled = findShapesWithDistTraveled();
    for (GtfsTrip trip : tripTable.getEntities()) {
      List<GtfsStopTime> stopTimes = stopTimeTable.byTripId(trip.tripId());
      int withShapeDistTraveled = 0;
      boolean missingFirst = false;
      for (int i = 0; i < stopTimes.size(); ++i) {
        if (stopTimes.get(i).hasShapeDistTraveled()) {
          ++withShapeDistTraveled;
        } else if (i == 0) {
          missingFirst = true;
        }
      }
      if (withShapeDistTraveled == 0) {
        // No shape_dist_traveled for that trip, so nothing to validate.
        continue;
      }
      if (missingFirst) {
        // shape_dist_traveled for the first stop time of a given trip defaults to 0.
        ++withShapeDistTraveled;
      }
      if (!trip.hasShapeId()) {
        noticeContainer.addValidationNotice(new TripWithShapeDistTraveledButNoShapeNotice(trip));
        continue;
      }
      if (!shapesWithDistTraveled.contains(trip.shapeId())) {
        noticeContainer.addValidationNotice(
            new TripWithShapeDistTraveledButNoShapeDistancesNotice(trip));
        continue;
      }
      if (withShapeDistTraveled < stopTimes.size()) {
        noticeContainer.addValidationNotice(new TripWithPartialShapeDistTraveledNotice(trip));
      }
    }
  }

  private Set<String> findShapesWithDistTraveled() {
    Set<String> shapeIds = new HashSet<>();
    for (Collection<GtfsShape> shapeList : shapeTable.byShapeIdMap().asMap().values()) {
      for (GtfsShape shape : shapeList) {
        if (shape.hasShapeDistTraveled()) {
          shapeIds.add(shape.shapeId());
          break;
        }
      }
    }
    return shapeIds;
  }

  /**
   * Describes a trip that specifies some {@code stop_times.shape_dist_traveled} values but does not
   * have {@code shape_id}.
   */
  static class TripWithShapeDistTraveledButNoShapeNotice extends ValidationNotice {
    private final long csvRowNumber;
    private final String tripId;

    TripWithShapeDistTraveledButNoShapeNotice(GtfsTrip trip) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = trip.csvRowNumber();
      this.tripId = trip.tripId();
    }
  }

  /**
   * Describes a trip that specifies some {@code stop_times.shape_dist_traveled} values but the
   * associated shape does not have {@code shapes.shape_dist_traveled} values.
   */
  static class TripWithShapeDistTraveledButNoShapeDistancesNotice extends ValidationNotice {
    private final long csvRowNumber;
    private final String tripId;
    private final String shapeId;

    TripWithShapeDistTraveledButNoShapeDistancesNotice(GtfsTrip trip) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = trip.csvRowNumber();
      this.tripId = trip.tripId();
      this.shapeId = trip.shapeId();
    }
  }

  /**
   * Describes a trip that specifies {@code stop_times.shape_dist_traveled} values for some but not
   * all stops belonging to the trip. Distance values should be specified for all stops or none at
   * all.
   */
  static class TripWithPartialShapeDistTraveledNotice extends ValidationNotice {
    private final long csvRowNumber;
    private final String tripId;

    TripWithPartialShapeDistTraveledNotice(GtfsTrip trip) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = trip.csvRowNumber();
      this.tripId = trip.tripId();
    }
  }
}
