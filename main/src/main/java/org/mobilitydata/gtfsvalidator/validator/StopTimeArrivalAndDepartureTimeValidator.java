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
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StopTimeWithArrivalBeforePreviousDepartureTimeNotice;
import org.mobilitydata.gtfsvalidator.notice.StopTimeWithDepartureBeforeArrivalTimeNotice;
import org.mobilitydata.gtfsvalidator.notice.StopTimeWithOnlyArrivalOrDepartureTimeNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader;

import java.util.List;

/**
 * Validates departure_time and arrival_time fields in "stop_times.txt".
 * <p>
 * Generated notices:
 * * StopTimeWithOnlyArrivalOrDepartureTimeNotice - a single departure_time or arrival_time is defined for a row (both
 * or none are expected)
 * * StopTimeWithDepartureBeforeArrivalTimeNotice - departure_time < arrival_time
 * * StopTimeWithArrivalBeforePreviousDepartureTimeNotice - prev(arrival_time) < curr(departure_time)
 */
@GtfsValidator
public class StopTimeArrivalAndDepartureTimeValidator extends FileValidator {
    @Inject
    GtfsStopTimeTableContainer table;

    @Override
    public void validate(NoticeContainer noticeContainer) {
        //noinspection UnstableApiUsage
        for (List<GtfsStopTime> stopTimeList : Multimaps.asMap(table.byTripIdMap()).values()) {
            int previousDepartureRow = -1;
            for (int i = 0; i < stopTimeList.size(); ++i) {
                GtfsStopTime stopTime = stopTimeList.get(i);
                final boolean hasDeparture = stopTime.hasDepartureTime();
                final boolean hasArrival = stopTime.hasArrivalTime();
                if (hasArrival != hasDeparture) {
                    noticeContainer.addNotice(
                            new StopTimeWithOnlyArrivalOrDepartureTimeNotice(
                                    stopTime.csvRowNumber(),
                                    stopTime.tripId(),
                                    stopTime.stopSequence(),
                                    hasArrival ? GtfsStopTimeTableLoader.ARRIVAL_TIME_FIELD_NAME
                                            : GtfsStopTimeTableLoader.DEPARTURE_TIME_FIELD_NAME
                    ));
                }
                if (hasDeparture && hasArrival) {
                    if (stopTime.departureTime().isBefore(stopTime.arrivalTime())) {
                        noticeContainer.addNotice(
                                new StopTimeWithDepartureBeforeArrivalTimeNotice(
                                        stopTime.csvRowNumber(),
                                        stopTime.tripId(),
                                        stopTime.stopSequence(),
                                        stopTime.departureTime(),
                                        stopTime.arrivalTime()
                        ));
                    }
                }
                if (hasArrival && previousDepartureRow != -1 &&
                        stopTime.arrivalTime().isBefore(stopTimeList.get(previousDepartureRow).departureTime())) {
                    noticeContainer.addNotice(
                            new StopTimeWithArrivalBeforePreviousDepartureTimeNotice(
                                    stopTime.csvRowNumber(),
                                    stopTimeList.get(previousDepartureRow).csvRowNumber(),
                                    stopTime.tripId(),
                                    stopTime.arrivalTime(),
                                    stopTimeList.get(previousDepartureRow).departureTime()
                            )
                    );
                }
                if (hasDeparture) {
                    previousDepartureRow = i;
                }
            }
        }
    }
}

