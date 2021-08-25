/*
 * Copyright 2021 Google LLC
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

import java.util.List;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;

/**
 * Checks that stops and only stops have stop times.
 *
 * <ul>
 *   <li>every stop (or platform) should have stop times;
 *   <li>every non-stop location (station, entrance etc) may not have stop times.
 * </ul>
 */
@GtfsValidator
public class LocationHasStopTimesValidator extends FileValidator {

  private final GtfsStopTableContainer stopTable;
  private final GtfsStopTimeTableContainer stopTimeTable;

  @Inject
  LocationHasStopTimesValidator(
      GtfsStopTableContainer stopTable, GtfsStopTimeTableContainer stopTimeTable) {
    this.stopTable = stopTable;
    this.stopTimeTable = stopTimeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsStop stop : stopTable.getEntities()) {
      List<GtfsStopTime> stopTimes = stopTimeTable.byStopId(stop.stopId());
      if (stop.locationType().equals(GtfsLocationType.STOP)) {
        if (stopTimes.isEmpty()) {
          noticeContainer.addValidationNotice(new StopWithoutStopTimeNotice(stop));
        }
      } else if (!stopTimes.isEmpty()) {
        noticeContainer.addValidationNotice(
            new LocationWithUnexpectedStopTimeNotice(stop, stopTimes.get(0)));
      }
    }
  }

  /** Describes a stop that does not have any stop time associated. */
  static class StopWithoutStopTimeNotice extends ValidationNotice {
    private final long csvRowNumber;
    private final String stopId;
    private final String stopName;

    StopWithoutStopTimeNotice(GtfsStop stop) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = stop.csvRowNumber();
      this.stopId = stop.stopId();
      this.stopName = stop.stopName();
    }
  }

  /**
   * Describes a location in {@code stops.txt} that is not a stop but has a stop time associated.
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  static class LocationWithUnexpectedStopTimeNotice extends ValidationNotice {
    private final long csvRowNumber;
    private final String stopId;
    private final String stopName;
    private final long stopTimeCsvRowNumber;

    LocationWithUnexpectedStopTimeNotice(GtfsStop location, GtfsStopTime stopTime) {
      super(SeverityLevel.ERROR);
      this.csvRowNumber = location.csvRowNumber();
      this.stopId = location.stopId();
      this.stopName = location.stopName();
      this.stopTimeCsvRowNumber = stopTime.csvRowNumber();
    }
  }
}
