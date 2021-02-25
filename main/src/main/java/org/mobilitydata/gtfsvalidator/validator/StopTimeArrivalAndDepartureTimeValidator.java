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

import com.google.common.collect.Multimaps;
import java.util.List;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.StopTimeWithArrivalBeforePreviousDepartureTimeNotice;
import org.mobilitydata.gtfsvalidator.notice.StopTimeWithDepartureBeforeArrivalTimeNotice;
import org.mobilitydata.gtfsvalidator.notice.StopTimeWithOnlyArrivalOrDepartureTimeNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader;

/**
 * Validates departure_time and arrival_time fields in "stop_times.txt".
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link StopTimeWithOnlyArrivalOrDepartureTimeNotice} - a single departure_time or
 *       arrival_time is defined for a row (both or none are expected)
 *   <li>{@link StopTimeWithDepartureBeforeArrivalTimeNotice} - departure_time &lt; arrival_time
 *   <li>{@link StopTimeWithArrivalBeforePreviousDepartureTimeNotice} - prev(arrival_time) &lt;
 *       curr(departure_time)
 * </ul>
 */
@GtfsValidator
public class StopTimeArrivalAndDepartureTimeValidator extends FileValidator {
  @Inject GtfsStopTimeTableContainer table;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (List<GtfsStopTime> stopTimeList : Multimaps.asMap(table.byTripIdMap()).values()) {
      int previousDepartureRow = -1;
      for (int i = 0; i < stopTimeList.size(); ++i) {
        GtfsStopTime stopTime = stopTimeList.get(i);
        final boolean hasDeparture = stopTime.hasDepartureTime();
        final boolean hasArrival = stopTime.hasArrivalTime();
        if (hasArrival != hasDeparture) {
          noticeContainer.addValidationNotice(
              new StopTimeWithOnlyArrivalOrDepartureTimeNotice(
                  stopTime.csvRowNumber(),
                  stopTime.tripId(),
                  stopTime.stopSequence(),
                  hasArrival
                      ? GtfsStopTimeTableLoader.ARRIVAL_TIME_FIELD_NAME
                      : GtfsStopTimeTableLoader.DEPARTURE_TIME_FIELD_NAME,
                  SeverityLevel.ERROR));
        }
        if (hasDeparture && hasArrival) {
          if (stopTime.departureTime().isBefore(stopTime.arrivalTime())) {
            noticeContainer.addValidationNotice(
                new StopTimeWithDepartureBeforeArrivalTimeNotice(
                    stopTime.csvRowNumber(),
                    stopTime.tripId(),
                    stopTime.stopSequence(),
                    stopTime.departureTime(),
                    stopTime.arrivalTime(),
                    SeverityLevel.ERROR));
          }
        }
        if (hasArrival
            && previousDepartureRow != -1
            && stopTime
                .arrivalTime()
                .isBefore(stopTimeList.get(previousDepartureRow).departureTime())) {
          noticeContainer.addValidationNotice(
              new StopTimeWithArrivalBeforePreviousDepartureTimeNotice(
                  stopTime.csvRowNumber(),
                  stopTimeList.get(previousDepartureRow).csvRowNumber(),
                  stopTime.tripId(),
                  stopTime.arrivalTime(),
                  stopTimeList.get(previousDepartureRow).departureTime(),
                  SeverityLevel.ERROR));
        }
        if (hasDeparture) {
          previousDepartureRow = i;
        }
      }
    }
  }
}
