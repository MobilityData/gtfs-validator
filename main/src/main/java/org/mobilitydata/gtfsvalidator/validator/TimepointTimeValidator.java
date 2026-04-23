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
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeSchema;
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
      // - this should be flagged in the header tests.
      // - but also no notice regarding the absence of arrival_time or departure_time should be
      // generated
      return;
    }
    for (GtfsStopTime stopTime : stopTimes.getEntities()) {
      if ((stopTime.hasArrivalTime() || stopTime.hasDepartureTime()) && !stopTime.hasTimepoint()) {
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
   * `arrival_time` or `departure_time` not specified for timepoint.
   *
   * <p>Any records with `stop_times.timepoint` set to 1 must define a value for
   * `stop_times.arrival_time` and `stop_times.departure_time` fields.
   */
  @GtfsValidationNotice(
      severity = ERROR,
      files = @FileRefs({GtfsStopTimeSchema.class, GtfsStopTimeSchema.class}))
  static class StopTimeTimepointWithoutTimesNotice extends ValidationNotice {

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The faulty record's id. */
    private final String tripId;

    /** The faulty record's `stops.stop_sequence`. */
    private final long stopSequence;

    /** Either `departure_time` or `arrival_time`. */
    private final String specifiedField;

    StopTimeTimepointWithoutTimesNotice(GtfsStopTime stopTime, String specifiedField) {
      this.csvRowNumber = stopTime.csvRowNumber();
      this.tripId = stopTime.tripId();
      this.stopSequence = stopTime.stopSequence();
      this.specifiedField = specifiedField;
    }
  }

  /**
   * `stop_times.timepoint` value is missing for a record.
   *
   * <p>When at least one of `stop_times.arrival_time` or `stop_times.departure_time` are provided,
   * `stop_times.timepoint` should be defined
   */
  @GtfsValidationNotice(severity = WARNING, files = @FileRefs(GtfsStopTimeSchema.class))
  static class MissingTimepointValueNotice extends ValidationNotice {

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The faulty record's `stop_times.trip_id`. */
    private final String tripId;

    /** The faulty record's `stop_times.stop_sequence`. */
    private final long stopSequence;

    MissingTimepointValueNotice(GtfsStopTime stopTime) {
      this.csvRowNumber = stopTime.csvRowNumber();
      this.tripId = stopTime.tripId();
      this.stopSequence = stopTime.stopSequence();
    }
  }
}
