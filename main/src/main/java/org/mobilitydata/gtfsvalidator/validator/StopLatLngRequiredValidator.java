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

import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.ENTRANCE;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.STATION;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.STOP;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;

/**
 * Checks that conditionally required fields {@code stop_lat} and {@code stop_lon} are set depending
 * on {@code location_type}.
 *
 * <p>{@code stop_lat} and {@code stop_lon} are required for stations ({@code location_type=0}),
 * stops ({@code location_type=1}) and entrances ({@code location_type=2}) and optional for other
 * types.
 * <p>
 * Generated notice: {@code MissingRequiredFieldError}.
 */
@GtfsValidator
public class StopLatLngRequiredValidator extends SingleEntityValidator<GtfsStop> {
  @Override
  public void validate(GtfsStop location, NoticeContainer noticeContainer) {
    GtfsLocationType locationType = location.locationType();
    if (!(locationType == STOP || locationType == STATION || locationType == ENTRANCE)) {
      return;
    }
    if (!location.hasStopLat()) {
      noticeContainer.addValidationNotice(
          createMissingRequiredFieldNotice(location, GtfsStop.STOP_LAT_FIELD_NAME));
    }
    if (!location.hasStopLon()) {
      noticeContainer.addValidationNotice(
          createMissingRequiredFieldNotice(location, GtfsStop.STOP_LON_FIELD_NAME));
    }
  }

  private static ValidationNotice createMissingRequiredFieldNotice(
      GtfsStop location, String fieldName) {
    return new MissingRequiredFieldNotice(GtfsStop.FILENAME, location.csvRowNumber(), fieldName);
  }
}
