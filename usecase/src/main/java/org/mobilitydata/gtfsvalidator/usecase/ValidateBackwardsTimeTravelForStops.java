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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.BackwardsTimeTravelInStopNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;

import java.util.TreeMap;

/**
 * Use case to validate that for a given `trip_id`, the `arrival_time` of (n+1)-th stoptime in sequence does not precede
 * the `departure_time` of n-th stoptime in sequence
 */
public class ValidateBackwardsTimeTravelForStops {

    public void execute(final ValidationResultRepository resultRepo,
                        final TreeMap<Integer, StopTime> stopTimeSequence,
                        final TimeUtils timeUtils) {

        // get previous stoptime relevant information
        final var previousStopTimeData = new Object() {
            Integer departureTime = null;
            Integer stopSequence = null;
        };

        if (stopTimeSequence.lastEntry().getValue().getDepartureTime() != null &&
                stopTimeSequence.lastEntry().getValue().getArrivalTime() != null &&
                stopTimeSequence.firstEntry().getValue().getDepartureTime() != null &&
                stopTimeSequence.firstEntry().getValue().getArrivalTime() != null) {
            stopTimeSequence.forEach((stopSequence, stopTime) -> {

                if (stopTime.getArrivalTime() != null && previousStopTimeData.departureTime != null) {
                    if (stopTime.getArrivalTime() < previousStopTimeData.departureTime) {
                        resultRepo.addNotice(
                                new BackwardsTimeTravelInStopNotice(
                                        stopTime.getTripId(),
                                        stopTime.getStopSequence(),
                                        timeUtils.convertIntegerToHMMSS(stopTime.getArrivalTime()),
                                        timeUtils.convertIntegerToHMMSS(previousStopTimeData.departureTime),
                                        previousStopTimeData.stopSequence));
                    }
                }
                // to exclude any row where only one time field is provided
                if (stopTime.getArrivalTime() != null && stopTime.getDepartureTime() != null) {
                    previousStopTimeData.departureTime = stopTime.getDepartureTime();
                    previousStopTimeData.stopSequence = stopSequence;
                }
            });
        }
    }
}
