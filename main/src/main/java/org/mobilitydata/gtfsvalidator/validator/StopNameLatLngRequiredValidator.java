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

import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.BOARDING_AREA;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.ENTRANCE;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.GENERIC_NODE;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.validator.SingleEntityValidator;

/**
 * Checks that conditionally required fields "stop_lat", "stop_lon" and "stop_name" are set
 * depending on "location_type".
 *
 * <ul>
 *   <li>"stop_name" is optional for entrances, generic nodes and boarding areas and required for
 *       other types
 *   <li>"stop_lat" and "stop_lon" are optional for generic nodes and boarding areas and required
 *       for other types
 * </ul>
 *
 * Generated notice: {@code MissingRequiredFieldError}.
 */
@GtfsValidator
public class StopNameLatLngRequiredValidator extends SingleEntityValidator<GtfsStop> {
  @Override
  public void validate(GtfsStop location, NoticeContainer noticeContainer) {
    if (!(location.locationType().equals(ENTRANCE) || location.locationType().equals(GENERIC_NODE)
            || location.locationType().equals(BOARDING_AREA))
        && !location.hasStopName()) {
      noticeContainer.addValidationNotice(
          createMissingRequiredFieldNotice(location, GtfsStop.STOP_NAME_FIELD_NAME));
    }

    if (!(location.locationType().equals(GENERIC_NODE)
            || location.locationType().equals(BOARDING_AREA))) {
      if (!location.hasStopLat()) {
        noticeContainer.addValidationNotice(
            createMissingRequiredFieldNotice(location, GtfsStop.STOP_LAT_FIELD_NAME));
      }
      if (!location.hasStopLon()) {
        noticeContainer.addValidationNotice(
            createMissingRequiredFieldNotice(location, GtfsStop.STOP_LON_FIELD_NAME));
      }
    }
  }

  private static ValidationNotice createMissingRequiredFieldNotice(
      GtfsStop location, String fieldName) {
    return new MissingRequiredFieldNotice(GtfsStop.FILENAME, location.csvRowNumber(), fieldName);
  }
}
