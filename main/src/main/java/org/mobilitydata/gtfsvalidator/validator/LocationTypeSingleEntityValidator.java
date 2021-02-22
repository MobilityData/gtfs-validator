/*
 * Copyright 2020 Google LLC
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
import org.mobilitydata.gtfsvalidator.notice.ErrorDetectedException;
import org.mobilitydata.gtfsvalidator.notice.LocationWithoutParentStationNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.PlatformWithoutParentStationNotice;
import org.mobilitydata.gtfsvalidator.notice.StationWithParentStationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;

/**
 * Validates presence or absence of `parent_station` field based on `location_type`.
 *
 * <p>Generated notice:
 *
 * <ul>
 *   <li>{@link StationWithParentStationNotice}
 *   <li>{@link PlatformWithoutParentStationNotice}
 *   <li>{@link LocationWithoutParentStationNotice}
 * </ul>
 */
@GtfsValidator
public class LocationTypeSingleEntityValidator extends SingleEntityValidator<GtfsStop> {
  private boolean requiresParentStation(GtfsLocationType locationType) {
    return locationType == GtfsLocationType.ENTRANCE
        || locationType == GtfsLocationType.GENERIC_NODE
        || locationType == GtfsLocationType.BOARDING_AREA;
  }

  @Override
  public void validate(GtfsStop location, NoticeContainer noticeContainer) throws ErrorDetectedException {
    if (location.hasParentStation()) {
      if (location.locationType() == GtfsLocationType.STATION) {
        noticeContainer.addValidationNotice(
            new StationWithParentStationNotice(
                location.csvRowNumber(), location.stopId(),
                location.stopName(), location.parentStation()));
      }
    } else if (location.locationType() == GtfsLocationType.STOP) {
      if (!location.platformCode().isEmpty()) {
        // This is a platform since it has platform_code. This is a separate notice from
        // LocationWithoutParentStationNotice because it is less severe.
        noticeContainer.addValidationNotice(
            new PlatformWithoutParentStationNotice(
                location.csvRowNumber(), location.stopId(), location.stopName()));
      }
    } else if (requiresParentStation(location.locationType())) {
      noticeContainer.addValidationNotice(
          new LocationWithoutParentStationNotice(
              location.csvRowNumber(), location.stopId(),
              location.stopName(), location.locationTypeValue()));
    }
  }
}
