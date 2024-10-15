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

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.ForbiddenGeographyIdNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;

/**
 * Validates that only one of stop_id, location_group_id or location_id is defined in a given record
 * of stop_times.txt
 *
 * <p>Generated notice: {@link MissingRequiredFieldNotice}.
 *
 * <p>Generated notice: {@link ForbiddenGeographyIdNotice}.
 */
@GtfsValidator
public class StopTimesGeographyIdPresenceValidator extends SingleEntityValidator<GtfsStopTime> {

  @Override
  public void validate(GtfsStopTime stopTime, NoticeContainer noticeContainer) {
    int presenceCount = 0;
    if (stopTime.hasStopId()) {
      presenceCount++;
    }
    if (stopTime.hasLocationGroupId()) {
      presenceCount++;
    }

    if (stopTime.hasLocationId()) {
      presenceCount++;
    }

    if (presenceCount == 0) {
      // None of the 3 geography IDs are present, but we need at least stop_id
      noticeContainer.addValidationNotice(
          new MissingRequiredFieldNotice(
              GtfsStopTime.FILENAME, stopTime.csvRowNumber(), GtfsStopTime.STOP_ID_FIELD_NAME));
    }
    // TODO: Put this back once we are ready to publish this notice.
    //    else if (presenceCount > 1) {
    //      // More than one geography ID is present, but only one is allowed
    //      noticeContainer.addValidationNotice(
    //          new ForbiddenGeographyIdNotice(
    //              stopTime.csvRowNumber(),
    //              stopTime.stopId(),
    //              stopTime.locationGroupId(),
    //              stopTime.locationId()));
    //    }
  }

  @Override
  public boolean shouldCallValidate(ColumnInspector header) {
    if (header.hasColumn(GtfsStopTime.STOP_ID_FIELD_NAME)
        || header.hasColumn(GtfsStopTime.LOCATION_GROUP_ID_FIELD_NAME)
        || header.hasColumn(GtfsStopTime.LOCATION_ID_FIELD_NAME)) {
      return true;
    }
    return false;
  }
}
