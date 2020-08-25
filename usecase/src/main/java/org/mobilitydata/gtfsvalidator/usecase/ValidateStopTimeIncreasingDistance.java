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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DecreasingStopTimeDistanceErrorNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.DecreasingStopTimeDistanceWarningNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.TreeMap;

public class ValidateStopTimeIncreasingDistance {
    private final ValidationResultRepository resultRepo;

    /**
     * @param resultRepo a repository storing information about the validation process
     */
    public ValidateStopTimeIncreasingDistance(final ValidationResultRepository resultRepo) {
        this.resultRepo = resultRepo;
    }

    public void execute(final TreeMap<Integer, StopTime> stopTimeSequence) {
        // get previous stoptime relevant information
        final var previousStopTimeData = new Object() {
            Float shapeDistTraveled = null;
            Integer stopSequence = null;
        };
        stopTimeSequence.forEach((stopSequence, stopTime) -> {
            final Float shapeDistTraveled = stopTime.getShapeDistTraveled();
            if (shapeDistTraveled != null && previousStopTimeData.shapeDistTraveled != null) {
                if (previousStopTimeData.shapeDistTraveled > shapeDistTraveled) {
                    resultRepo.addNotice(
                            new DecreasingStopTimeDistanceErrorNotice(
                                    stopTime.getTripId(),
                                    previousStopTimeData.stopSequence,
                                    previousStopTimeData.shapeDistTraveled,
                                    stopSequence,
                                    shapeDistTraveled)
                    );
                } else if (previousStopTimeData.shapeDistTraveled.equals(shapeDistTraveled)) {
                    resultRepo.addNotice(
                            new DecreasingStopTimeDistanceWarningNotice(
                                    stopTime.getTripId(),
                                    previousStopTimeData.stopSequence,
                                    previousStopTimeData.shapeDistTraveled,
                                    stopSequence)
                    );
                }
            }
            // to exclude any row where field `shape_dist_traveled` is not provided
            if (shapeDistTraveled != null) {
                previousStopTimeData.shapeDistTraveled = shapeDistTraveled;
                previousStopTimeData.stopSequence = stopSequence;
            }
        });
    }
}
