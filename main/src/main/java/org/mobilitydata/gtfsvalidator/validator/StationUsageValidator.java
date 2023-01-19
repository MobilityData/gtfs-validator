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

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;

/**
 * Check that every station in {@code stops.txt} has child platforms.
 *
 * <p>Note that a station that has child entrances or generic nodes is also reported.
 */
@GtfsValidator
public class StationUsageValidator extends FileValidator {
  private final GtfsStopTableContainer stopTable;

  @Inject
  StationUsageValidator(GtfsStopTableContainer stopTable) {
    this.stopTable = stopTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsStop parent : stopTable.getEntities()) {
      if (parent.locationType().equals(GtfsLocationType.STATION)
          && stopTable.byParentStation(parent.stopId())
                 .stream()
                 .noneMatch(s -> s.locationType().equals(GtfsLocationType.STOP))) {
        noticeContainer.addValidationNotice(new StationWithoutPlatformsNotice(parent));
      }
    }
  }

  static class StationWithoutPlatformsNotice extends ValidationNotice {
    private final long csvRowNumber;
    private final String stopId;
    private final String stopName;

    StationWithoutPlatformsNotice(GtfsStop location) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = location.csvRowNumber();
      this.stopId = location.stopId();
      this.stopName = location.stopName();
    }
  }
}
