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

public class ValidateStopTimeTimeCombination {

    public void execute(final ValidationResultRepository resultRepo,
                        final Map<Integer, StopTime> stopTimeSequence,
                        final TimeUtils timeUtils) {

        final var previousStopTimeData = new Object() {
            Integer arrivalTime = null;
            Integer stopSequence = null;
        };

        stopTimeSequence.forEach((stopSequence, stopTime) -> {

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
            previousStopTimeData.arrivalTime = stopTime.getArrivalTime() != null ?
                    stopTime.getArrivalTime() : stopTime.getDepartureTime();
            previousStopTimeData.stopSequence = stopSequence;
        });
    }
}
