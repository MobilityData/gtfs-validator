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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingTripEdgeStopTimeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.stops.LocationBase;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

/**
 * Use case for E046 to validate that all records of `trips.txt` refer to an a collection of {@code StopTime} from file
 * `stop_times.txt` of which -when sorted by `stop_sequence`-
 * the travel speed between each stop is not above 150 km/h
 */
public class ValidateTripTravelSpeed {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     * @param logger     a logger to log information about the validation process
     */
    public ValidateTripTravelSpeed(final GtfsDataRepository dataRepo,
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
        logger.info("Validating rule E046 - Fast travel between stops");

        dataRepo.getTripAll().keySet().forEach(tripId -> {

            var previousStopData = new Object() {
                Integer departureTime = null;
                float latitude;
                float longitude;
            };

            dataRepo.getStopTimeByTripId(tripId).forEach((stopSequence, stopTime) -> {
                if (previousStopData.departureTime != null) {
                    Integer currentStopArrivalTime = stopTime.getArrivalTime();
                    if (currentStopArrivalTime != null) {
                        int durationSecond = currentStopArrivalTime - previousStopData.departureTime;
                        int distanceMeter = 666; //From distance Utils class
                        int speedMeterPerSecond = distanceMeter / durationSecond;

                        if (speedMeterPerSecond > 42) { // roughly 150 km per hour. Put it in default parameters
                            //Add notice
                            resultRepo.addNotice(
                                    new MissingTripEdgeStopTimeNotice("arrival_time",
                                            tripId,
                                            stopTime.getStopSequence()
                                    )
                            );
                        }
                    }
                }
                previousStopData.departureTime = stopTime.getDepartureTime();
                LocationBase stop = dataRepo.getStopById(stopTime.getStopId());
                previousStopData.latitude = stop.getStopLat();
                previousStopData.longitude = stop.getStopLon();
            });
        });
    }
}
