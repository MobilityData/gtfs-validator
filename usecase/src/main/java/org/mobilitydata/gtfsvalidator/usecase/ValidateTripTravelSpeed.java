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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FastTravelBetweenStopsNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.stops.LocationBase;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.GeospatialUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Use case for E046 to validate that all records of `trips.txt` refer to an a collection of {@code StopTime} from file
 * `stop_times.txt` of which -when sorted by `stop_sequence`-
 * the travel speed between each stop is not above 150 km/h
 */
public class ValidateTripTravelSpeed {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final GeospatialUtils geoUtils;
    private final Logger logger;
    private final static float METER_PER_SECOND_TO_KMH_CONVERSION_FACTOR = 3.6f;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     * @param logger     a logger to log information about the validation process
     */
    public ValidateTripTravelSpeed(final GtfsDataRepository dataRepo,
                                   final ValidationResultRepository resultRepo,
                                   final GeospatialUtils geoUtils,
                                   final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.geoUtils = geoUtils;
        this.logger = logger;
    }

    /**
     * Use case execution method: Checks all records of `trips.txt` refer to an a collection of {@code StopTime} from file
     * `stop_times.txt` of which -when sorted by `stop_sequence`- the travel speed between each stop is not above 150 km/h
     * A new {@link FastTravelBetweenStopsNotice} notice is generated each time this condition is false.
     * This notice is then added to the {@link ValidationResultRepository} provided in the constructor.
     */
    public void execute() {
        logger.info("Validating rule E046 - Fast travel between stops");

        dataRepo.getTripAll().keySet().forEach(tripId -> {
            var previousStopsData = new Object() {
                Integer departureTime = null;
                double latitude;
                double longitude;

                // used to accumulate distance between stops with same arrival and departure times
                int accumulatedDistanceMeter = 0;
                final List<Integer> accumulatedStopSequence = new ArrayList<>();
            };

            dataRepo.getStopTimeByTripId(tripId).forEach((stopSequence, stopTime) -> {

                // prepare data for current iteration
                LocationBase currentStop = dataRepo.getStopById(stopTime.getStopId());
                double currentStopLat = currentStop.getStopLat();
                double currentStopLon = currentStop.getStopLon();

                Integer currentArrivalTime = stopTime.getArrivalTime();
                int distanceFromPreviousStopMeter = geoUtils.distanceBetweenMeter(
                        previousStopsData.latitude,
                        previousStopsData.longitude,
                        currentStopLat,
                        currentStopLon
                );
                boolean sameArrivalAndDeparture = false;

                if (previousStopsData.departureTime != null && currentArrivalTime != null) {
                    sameArrivalAndDeparture = currentArrivalTime.equals(previousStopsData.departureTime);

                    if (!sameArrivalAndDeparture) {
                        int durationSecond = currentArrivalTime - previousStopsData.departureTime;
                        int distanceMeter = distanceFromPreviousStopMeter + previousStopsData.accumulatedDistanceMeter;
                        int speedMeterPerSecond = distanceMeter / durationSecond;

                        if (speedMeterPerSecond > 42) { // roughly 150 km per hour. Put it in default parameters
                            previousStopsData.accumulatedStopSequence.add(stopSequence);

                            resultRepo.addNotice(
                                    new FastTravelBetweenStopsNotice(
                                            tripId,
                                            speedMeterPerSecond * METER_PER_SECOND_TO_KMH_CONVERSION_FACTOR,
                                            new ArrayList<>(previousStopsData.accumulatedStopSequence)
                                    )
                            );
                        }
                    }
                }

                // Prepare data for next iteration
                if (sameArrivalAndDeparture) {
                    previousStopsData.accumulatedDistanceMeter += distanceFromPreviousStopMeter;
                } else {
                    previousStopsData.accumulatedDistanceMeter = 0;
                    previousStopsData.accumulatedStopSequence.clear();
                }

                previousStopsData.departureTime = stopTime.getDepartureTime();
                previousStopsData.latitude = currentStopLat;
                previousStopsData.longitude = currentStopLon;
                previousStopsData.accumulatedStopSequence.add(stopSequence);
            });
        });
    }
}
