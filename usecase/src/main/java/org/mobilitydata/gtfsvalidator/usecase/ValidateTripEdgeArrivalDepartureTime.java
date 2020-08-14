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

import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingTripEdgeStopTimeNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.SortedMap;

/**
 * Use case for E043 to validate that all records of `trips.txt` refer to an a collection of {@code StopTime} from file
 * `stop_times.txt` of which -when sorted by `stop_sequence`-
 * the first and last element define both `departure_time` and `arrival_time`
 */
public class ValidateTripEdgeArrivalDepartureTime {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     * @param logger     a logger to log information about the validation process
     */
    public ValidateTripEdgeArrivalDepartureTime(final GtfsDataRepository dataRepo,
                                                final ValidationResultRepository resultRepo,
                                                final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method: Checks if first and last stop in sequence associated with a trip define
     * both fields `departure_time` and `arrival_time`.
     * A new {@link MissingTripEdgeStopTimeNotice} notice is generated each time this condition is false.
     * This notice is then added to the {@link ValidationResultRepository} provided in the constructor.
     */
    public void execute() {
        logger.info("Validating rule E044 - Missing trip edge arrival_time or departure_time");

        dataRepo.getTripAll().keySet().forEach(tripId -> {
            SortedMap<Integer, StopTime> stopTimesForTrip = dataRepo.getStopTimeByTripId(tripId);

            StopTime tripStartStop = stopTimesForTrip.get(stopTimesForTrip.firstKey());
            StopTime tripEndStop = stopTimesForTrip.get(stopTimesForTrip.lastKey());

            if (tripStartStop.getArrivalTime() == null) {
                resultRepo.addNotice(
                        new MissingTripEdgeStopTimeNotice("arrival_time",
                                tripId,
                                tripStartStop.getStopSequence()
                        )
                );
            }
            if (tripStartStop.getDepartureTime() == null) {
                resultRepo.addNotice(
                        new MissingTripEdgeStopTimeNotice("departure_time",
                                tripId,
                                tripStartStop.getStopSequence()
                        )
                );
            }

            if (tripEndStop.getArrivalTime() == null) {
                resultRepo.addNotice(
                        new MissingTripEdgeStopTimeNotice("arrival_time",
                                tripId,
                                tripEndStop.getStopSequence()
                        )
                );
            }
            if (tripEndStop.getDepartureTime() == null) {
                resultRepo.addNotice(
                        new MissingTripEdgeStopTimeNotice("departure_time",
                                tripId,
                                tripEndStop.getStopSequence()
                        )
                );
            }
        });
    }
}
