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

import com.google.common.collect.ImmutableMap;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
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
  public void validate(GtfsStop location, NoticeContainer noticeContainer) {
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

  /**
   * A station has `parent_station` field set.
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  static class StationWithParentStationNotice extends ValidationNotice {
    StationWithParentStationNotice(
        long csvRowNumber, String stopId, String stopName, String parentStation) {
      super(
          ImmutableMap.of(
              "stopId",
              stopId,
              "stopName",
              stopName,
              "csvRowNumber",
              csvRowNumber,
              "parentStation",
              parentStation),
          SeverityLevel.ERROR);
    }
  }

  /**
   * A location that must have `parent_station` field does not have it.
   *
   * <p>The following location types must have `parent_station`: entrance, generic node, boarding
   * area.
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  static class LocationWithoutParentStationNotice extends ValidationNotice {
    LocationWithoutParentStationNotice(
        long csvRowNumber, String stopId, String stopName, int locationType) {
      super(
          ImmutableMap.of(
              "csvRowNumber",
              csvRowNumber,
              "stopId",
              stopId,
              "stopName",
              stopName,
              "locationType",
              locationType),
          SeverityLevel.ERROR);
    }
  }

  /**
   * A platform has no `parent_station` field set.
   *
   * <p>This is different from {@code LocationWithoutParentStationNotice} since it is less severe.
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  static class PlatformWithoutParentStationNotice extends ValidationNotice {
    PlatformWithoutParentStationNotice(long csvRowNumber, String stopId, String stopName) {
      super(
          ImmutableMap.of("csvRowNumber", csvRowNumber, "stopId", stopId, "stopName", stopName),
          SeverityLevel.WARNING);
    }
  }
}
