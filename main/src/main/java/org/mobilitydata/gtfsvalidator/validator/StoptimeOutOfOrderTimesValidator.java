/*
 * Copyright 2021 MobilityData
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
import org.mobilitydata.gtfsvalidator.notice.StopTimeWithDepartureBeforeArrivalTimeNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;

/**
 * Validates departure_time after arrival_time in "stop_times.txt".
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link StopTimeWithDepartureBeforeArrivalTimeNotice} - departure_time &lt; arrival_time
 * </ul>
 */
@GtfsValidator
public class StoptimeOutOfOrderTimesValidator extends SingleEntityValidator<GtfsStopTime> {

  @Override
  public void validate(GtfsStopTime stopTime, NoticeContainer noticeContainer) {
    if (stopTime.hasDepartureTime() && stopTime.hasArrivalTime()) {
      if (stopTime.departureTime().isBefore(stopTime.arrivalTime())) {
        noticeContainer.addValidationNotice(
            new StopTimeWithDepartureBeforeArrivalTimeNotice(
                stopTime.csvRowNumber(),
                stopTime.tripId(),
                stopTime.stopSequence(),
                stopTime.departureTime(),
                stopTime.arrivalTime()));
      }
    }
  }
}
