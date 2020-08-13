/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.BadStopTimeTimeCombinationNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;

import java.util.Map;

/**
 * Use case to validate that for a given `trip_id`, the `arrival_time` of (n+1)-th stoptime in sequence does not precede
 * the `departure_time` of n-th stoptime in sequence
 */
public class ValidateStopTimeTimeCombination {

    public void execute(final ValidationResultRepository resultRepo,
                        final Map<Integer, StopTime> stopTimeSequence,
                        final TimeUtils timeUtils) {

        // get previous stoptime relevant information
        final var previousStopTimeData = new Object() {
            Integer arrivalTime = null;
            Integer stopSequence = null;
        };

        stopTimeSequence.forEach((stopSequence, stopTime) -> {

            // useful when value of departure_time is null, the comparison is therefore done using the arrival_time of
            // the preceding stoptime in sequence. Here we consider that if departure_time is null, then it can be
            // approached by the previous stoptime in sequence's arrival_time value.
            // Note that both `arrival_time` and `departure_time` fields should not be null
            // (see best practices: http://gtfs.org/best-practices/#stop_timestxt)
            final Integer currentStopTimeDepartureTime = stopTime.getDepartureTime() != null ?
                    stopTime.getDepartureTime() : stopTime.getArrivalTime();

            if (currentStopTimeDepartureTime != null && previousStopTimeData.arrivalTime != null) {
                if (currentStopTimeDepartureTime < previousStopTimeData.arrivalTime) {
                    resultRepo.addNotice(
                            new BadStopTimeTimeCombinationNotice(
                                    stopTime.getTripId(),
                                    stopTime.getStopSequence(),
                                    timeUtils.convertIntegerToHMMSS(previousStopTimeData.arrivalTime),
                                    timeUtils.convertIntegerToHMMSS(stopTime.getDepartureTime()),
                                    previousStopTimeData.stopSequence));
                }
            }
            // useful when value of arrival_time is null, the comparison is therefore done using the departure_time of
            // the preceding stoptime in sequence. Here we consider that if arrival_time is null, then it can be approa-
            // ched by the current stoptime's departure_time value.
            previousStopTimeData.arrivalTime = stopTime.getArrivalTime() != null ?
                    stopTime.getArrivalTime() : stopTime.getDepartureTime();
            previousStopTimeData.stopSequence = stopSequence;
        });
    }
}
