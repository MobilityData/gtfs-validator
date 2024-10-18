/*
 * Copyright 2024 MobilityData
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

import static org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRef.FILE_REQUIREMENTS;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;

/**
 * Check that only entries with stop_id have shape_dist_traveled. A GeoJSON location or location
 * group is forbidden to have an associated shape_dist_traveled field in stop_times.txt.
 *
 * <p>Generated notice: {@link ForbiddenShapeDistTraveledNotice}.
 */
@GtfsValidator
public class StopTimesShapeDistTraveledPresenceValidator
    extends SingleEntityValidator<GtfsStopTime> {

  @Override
  public void validate(GtfsStopTime stopTime, NoticeContainer noticeContainer) {
    if (stopTime.hasStopId()) {
      return;
    }
    if (stopTime.hasLocationGroupId() || stopTime.hasLocationId()) {
      if (stopTime.hasShapeDistTraveled()) {
        noticeContainer.addValidationNotice(
            new ForbiddenShapeDistTraveledNotice(
                stopTime.csvRowNumber(),
                stopTime.tripId(),
                stopTime.locationGroupId(),
                stopTime.locationId(),
                stopTime.shapeDistTraveled()));
      }
    }
  }

  @Override
  public boolean shouldCallValidate(ColumnInspector header) {
    // No point in validating if there is no shape_dist_traveled column
    // And we need to have either location_id or location_group_id for this validator to make sense
    return header.hasColumn(GtfsStopTime.SHAPE_DIST_TRAVELED_FIELD_NAME)
        && (header.hasColumn(GtfsStopTime.LOCATION_ID_FIELD_NAME)
            || header.hasColumn(GtfsStopTime.LOCATION_GROUP_ID_FIELD_NAME));
  }

  /**
   * A stop_time entry has a `shape_dist_traveled` without a `stop_id` value.
   *
   * <p>A GeoJSON location or location group has an associated shape_dist_traveled field in
   * stop_times.txt. shape_dist_traveled values should only be provided for stops.
   */
  @GtfsValidationNotice(
      severity = ERROR,
      sections = @GtfsValidationNotice.SectionRefs(FILE_REQUIREMENTS))
  public static class ForbiddenShapeDistTraveledNotice extends ValidationNotice {

    /** The row of the faulty record. */
    private final int csvRowNumber;

    /** The trip_id for which the shape_dist_traveled is defined */
    private final String tripId;

    /** The location_grpup_id for which the shape_dist_traveled is defined */
    private final String locationGroupId;

    /** The location_id for which the shape_dist_traveled is defined */
    private final String locationId;

    /** The shape_dist_traveled value */
    private final double shapeDistTraveled;

    public ForbiddenShapeDistTraveledNotice(
        int csvRowNumber,
        String tripId,
        String locationGroupId,
        String locationId,
        double shapeDistTraveled) {
      this.csvRowNumber = csvRowNumber;
      this.tripId = tripId;
      this.locationGroupId = locationGroupId;
      this.locationId = locationId;
      this.shapeDistTraveled = shapeDistTraveled;
    }
  }
}
