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

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTimepoint;

/**
 * Validates timepoints from GTFS file "stop_times.txt" have time fields.
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link StopTimeTimepointWithoutTimesNotice} - a timepoint does not specifies arrival_time
 *       or departure_time
 * </ul>
 */
@GtfsValidator
public class TimepointTimeValidator extends SingleEntityValidator<GtfsStopTime> {

  @Override
  public void validate(GtfsStopTime stopTime, NoticeContainer noticeContainer) {
    if (!isTimepoint(stopTime)) {
      return;
    }
    if (!stopTime.hasArrivalTime()) {
      noticeContainer.addValidationNotice(
          new StopTimeTimepointWithoutTimesNotice(
              stopTime.csvRowNumber(),
              stopTime.tripId(),
              stopTime.stopSequence(),
              String.format(GtfsStopTimeTableLoader.ARRIVAL_TIME_FIELD_NAME)));
    }
    if (!stopTime.hasDepartureTime()) {
      noticeContainer.addValidationNotice(
          new StopTimeTimepointWithoutTimesNotice(
              stopTime.csvRowNumber(),
              stopTime.tripId(),
              stopTime.stopSequence(),
              GtfsStopTimeTableLoader.DEPARTURE_TIME_FIELD_NAME));
    }
  }

  private boolean isTimepoint(GtfsStopTime stopTime) {
    return stopTime.timepoint().equals(GtfsStopTimeTimepoint.EXACT);
  }

  /**
   * Timepoint without time
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  static class StopTimeTimepointWithoutTimesNotice extends ValidationNotice {
    private long csvRowNumber;
    private String tripId;
    private long stopSequence;
    private String specifiedField;

    StopTimeTimepointWithoutTimesNotice(
        long csvRowNumber, String tripId, long stopSequence, String specifiedField) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = csvRowNumber;
      this.tripId = tripId;
      this.stopSequence = stopSequence;
      this.specifiedField = specifiedField;
    }
  }
}
