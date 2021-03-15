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
import org.mobilitydata.gtfsvalidator.notice.StopTimeTimepointWithoutTimesNotice;
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
    if (isTimepoint(stopTime)) {
      if (!stopTime.hasArrivalTime() && !stopTime.hasDepartureTime()) {
        noticeContainer.addValidationNotice(
            new StopTimeTimepointWithoutTimesNotice(
                stopTime.csvRowNumber(),
                stopTime.tripId(),
                stopTime.stopSequence(),
                String.format(
                    "%s and %s",
                    GtfsStopTimeTableLoader.ARRIVAL_TIME_FIELD_NAME,
                    GtfsStopTimeTableLoader.DEPARTURE_TIME_FIELD_NAME)));
      } else if (!stopTime.hasArrivalTime() || !stopTime.hasDepartureTime()) {
        noticeContainer.addValidationNotice(
            new StopTimeTimepointWithoutTimesNotice(
                stopTime.csvRowNumber(),
                stopTime.tripId(),
                stopTime.stopSequence(),
                stopTime.hasArrivalTime()
                    ? GtfsStopTimeTableLoader.DEPARTURE_TIME_FIELD_NAME
                    : GtfsStopTimeTableLoader.ARRIVAL_TIME_FIELD_NAME));
      }
    }
  }

  private boolean isTimepoint(GtfsStopTime stopTime) {
    return stopTime.timepoint().equals(GtfsStopTimeTimepoint.EXACT);
  }
}
