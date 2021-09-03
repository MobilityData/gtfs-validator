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

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFareRuleTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;

/**
 * Check that if {@code fare_rules.txt} is provided, then all stops and platforms (location_type =
 * 0) have {@code stops.zone_id} assigned.
 *
 * <p>Generated notice: {@link StopWithoutZoneIdNotice}.
 */
@GtfsValidator
public class StopZoneIdValidator extends FileValidator {

  private final GtfsStopTableContainer stopTable;
  private final GtfsFareRuleTableContainer fareRuleTable;

  @Inject
  StopZoneIdValidator(GtfsStopTableContainer stopTable, GtfsFareRuleTableContainer fareRuleTable) {
    this.stopTable = stopTable;
    this.fareRuleTable = fareRuleTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    if (fareRuleTable.getEntities().isEmpty()) {
      return;
    }
    for (GtfsStop stop : stopTable.getEntities()) {
      if (stop.locationType().equals(GtfsLocationType.STOP) && !stop.hasZoneId()) {
        noticeContainer.addValidationNotice(new StopWithoutZoneIdNotice(stop));
      }
    }
  }

  /**
   * A {@code GtfsShape} should be referred to at least once in {@code GtfsTripTableContainer}
   * station).
   *
   * <p>Severity: {@code SeverityLevel.WARNING} - Will be upgraded to {@code SeverityLevel.ERROR}
   */
  static class StopWithoutZoneIdNotice extends ValidationNotice {
    private final String stopId;
    private final String stopName;
    private final long csvRowNumber;

    StopWithoutZoneIdNotice(GtfsStop stop) {
      super(SeverityLevel.WARNING);
      this.stopId = stop.stopId();
      this.stopName = stop.stopName();
      this.csvRowNumber = stop.csvRowNumber();
    }
  }
}
