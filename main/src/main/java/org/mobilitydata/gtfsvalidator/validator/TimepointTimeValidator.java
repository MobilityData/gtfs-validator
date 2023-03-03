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
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTimepoint;

/**
 * Validates timepoints from GTFS file "stop_times.txt" have time fields.
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link StopTimeTimepointWithoutTimesNotice} - a timepoint does not specifies arrival_time
 *       or departure_time
 *   <li>{@link MissingTimepointValueNotice} - value for {@code stop_times.timepoint} is missing
 *   <li>{@link MissingTimepointColumnNotice} - field {@code stop_times.timepoint} is missing
 * </ul>
 */
@GtfsValidator
public class TimepointTimeValidator extends FileValidator {
  private final GtfsStopTimeTableContainer stopTimes;

  @Inject
  TimepointTimeValidator(GtfsStopTimeTableContainer stopTimes) {
    this.stopTimes = stopTimes;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    if (!stopTimes.hasColumn(GtfsStopTime.TIMEPOINT_FIELD_NAME)) {
      // legacy datasets do not use timepoint column in stop_times.txt as a result:
      // - this should be flagged;
      // - but also no notice regarding the absence of arrival_time or departure_time should be
      // generated
      noticeContainer.addValidationNotice(new MissingTimepointColumnNotice());
      return;
    }
    for (GtfsStopTime stopTime : stopTimes.getEntities()) {
      if (!stopTime.hasTimepoint()) {
        noticeContainer.addValidationNotice(new MissingTimepointValueNotice(stopTime));
      }
      if (isTimepoint(stopTime)) {
        if (!stopTime.hasArrivalTime()) {
          noticeContainer.addValidationNotice(
              new StopTimeTimepointWithoutTimesNotice(
                  stopTime, GtfsStopTime.ARRIVAL_TIME_FIELD_NAME));
        }
        if (!stopTime.hasDepartureTime()) {
          noticeContainer.addValidationNotice(
              new StopTimeTimepointWithoutTimesNotice(
                  stopTime, GtfsStopTime.DEPARTURE_TIME_FIELD_NAME));
        }
      }
    }
  }

  private boolean isTimepoint(GtfsStopTime stopTime) {
    return stopTime.hasTimepoint() && stopTime.timepoint().equals(GtfsStopTimeTimepoint.EXACT);
  }

  /**
   * Timepoint without time
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  static class StopTimeTimepointWithoutTimesNotice extends ValidationNotice {
    private final int csvRowNumber;
    private final String tripId;
    private final long stopSequence;
    private final String specifiedField;

    StopTimeTimepointWithoutTimesNotice(GtfsStopTime stopTime, String specifiedField) {
      super(SeverityLevel.ERROR);
      this.csvRowNumber = stopTime.csvRowNumber();
      this.tripId = stopTime.tripId();
      this.stopSequence = stopTime.stopSequence();
      this.specifiedField = specifiedField;
    }
  }

  /**
   * {@code stop_times.timepoint} value is missing
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  static class MissingTimepointValueNotice extends ValidationNotice {
    private final int csvRowNumber;
    private final String tripId;
    private final long stopSequence;

    MissingTimepointValueNotice(GtfsStopTime stopTime) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = stopTime.csvRowNumber();
      this.tripId = stopTime.tripId();
      this.stopSequence = stopTime.stopSequence();
    }
  }

  /**
   * Column {@code stop_times.timepoint} is missing.
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  static class MissingTimepointColumnNotice extends ValidationNotice {
    private final String filename;

    MissingTimepointColumnNotice() {
      super(SeverityLevel.WARNING);
      this.filename = GtfsStopTime.FILENAME;
    }
  }
}
