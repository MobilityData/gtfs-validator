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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.StopTooFarFromTripShape;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.GeospatialUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Use case to validate that a stop is within a distance threshold for a trip shape. This use case can be used after
 * the {@code GtfsDataRepository} in the constructor has been populated with {@code StopTime}, {@code Trip},
 * {@code LocationBase} (GTFS stops.txt), and {@code ShapePoint} entities.
 */
public class ValidateStopTooFarFromTripShape {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final GeospatialUtils geospatialUtils;
    private final Logger logger;

    /**
     * @param dataRepo        a repository storing the data of a GTFS dataset
     * @param resultRepo      a repository storing information about the validation process
     * @param geospatialUtils utilities for calculating geospatial information
     * @param logger          a logger used to log information about the validation process
     */
    public ValidateStopTooFarFromTripShape(final GtfsDataRepository dataRepo,
                                           final ValidationResultRepository resultRepo,
                                           final GeospatialUtils geospatialUtils,
                                           final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.geospatialUtils = geospatialUtils;
        this.logger = logger;
    }

    /**
     * Use case execution method: Checks if each stop for a trip falls within a buffer of the trip shape. If this
     * requirement is not met, a {@code StopTooFarFromTripShape} notice is added to the {@code ValidationResultRepo}
     * provided in the constructor.
     */
    public void execute() {
        logger.info("Validating rule 'E052 - Stop too far from trip shape'");

        List<StopTooFarFromTripShape> errors = new ArrayList<>();

        // Cache for previously tested shape_id and stop_id pairs - we don't need to test them more than once
        Set<String> testedCache = new HashSet<>();

        dataRepo.getStopTimeAll().forEach((tripId, tripStopTimes) -> {
            Trip trip = dataRepo.getTripById(tripId);
            if (trip == null || trip.getShapeId() == null) {
                // No shape for this trip - skip to the next trip
                return;
            }
            // Check for possible E052 errors for this combination of stop times and shape points for this trip_id
            List<StopTooFarFromTripShape> errorsForTrip = geospatialUtils.checkStopsWithinTripShape(trip,
                    tripStopTimes,
                    dataRepo.getShapeById(trip.getShapeId()),
                    dataRepo.getStopAll(),
                    testedCache
            );
            errors.addAll(errorsForTrip);
        });

        errors.forEach(resultRepo::addNotice);
    }
}
