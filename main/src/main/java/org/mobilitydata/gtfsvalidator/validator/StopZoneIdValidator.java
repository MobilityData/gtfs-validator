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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFareRule;
import org.mobilitydata.gtfsvalidator.table.GtfsFareRuleTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFareruleSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;

/**
 * Checks that all stops and platforms (location_type = 0) have {@code stops.zone_id} assigned.
 * assigned if {@code fare_rules.txt} is provided and at least one of the following fields is
 * provided:
 *
 * <ul>
 *   <li>{@code fare_rules.origin_id}
 *   <li>{@code fare_rules.contains_id}
 *   <li>{@code fare_rules.destination_id}
 * </ul>
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
    if (!hasFareZoneStructure(fareRuleTable)) {
      return;
    }
    for (GtfsStop stop : stopTable.getEntities()) {
      if (!stop.locationType().equals(GtfsLocationType.STOP)) {
        continue;
      }
      if (!stop.hasZoneId()) {
        noticeContainer.addValidationNotice(new StopWithoutZoneIdNotice(stop));
      }
    }
  }

  /**
   * Checks if the {@code GtfsFareRuleTableContainer} provided as parameter has a fare structure
   * that uses zones.
   *
   * @param fareRuleTable the {@code GtfsFareRuleTableContainer} to be checked
   * @return true if the {@code GtfsFareRuleTableContainer} provided as parameter has a fare
   *     structure that uses zones; false otherwise.
   */
  private static boolean hasFareZoneStructure(GtfsFareRuleTableContainer fareRuleTable) {
    for (GtfsFareRule fareRule : fareRuleTable.getEntities()) {
      if (fareRule.hasContainsId() || fareRule.hasDestinationId() || fareRule.hasOriginId()) {
        return true;
      }
    }
    return false;
  }

  /**
   * A {@code GtfsShape} should be referred to at least once in {@code GtfsTripTableContainer}
   * station).
   *
   * <p>Severity: {@code SeverityLevel.WARNING} - Will be upgraded to {@code SeverityLevel.ERROR}
   */
  @GtfsValidationNotice(
      severity = ERROR,
      files = @FileRefs({GtfsStopSchema.class, GtfsFareruleSchema.class}))
  static class StopWithoutZoneIdNotice extends ValidationNotice {

    /** The faulty record's id. */
    private final String stopId;

    /** The faulty record's `stops.stop_name`. */
    private final String stopName;

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    StopWithoutZoneIdNotice(GtfsStop stop) {
      super(SeverityLevel.ERROR);
      this.stopId = stop.stopId();
      this.stopName = stop.stopName();
      this.csvRowNumber = stop.csvRowNumber();
    }
  }
}
