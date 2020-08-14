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

package org.mobilitydata.gtfsvalidator.usecase.usecaseprocessor;

import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.usecase.ValidateBackwardsTimeTravelForStops;
import org.mobilitydata.gtfsvalidator.usecase.ValidateShapeIdReferenceInStopTime;
import org.mobilitydata.gtfsvalidator.usecase.ValidateStopTimeTripId;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;

import java.util.Map;

/**
 * Use case to execute validation for GTFS files `stop_times.txt`, `shapes.txt`, and `trips.txt`
 * E034 - `shape_id` not found
 * E037 - `trip_id` not found
 */
public class StopTimeBasedValidator {
    private final ValidationResultRepository resultRepo;
    private final GtfsDataRepository dataRepo;
    private final Logger logger;
    private final TimeUtils timeUtils;
    private final ValidateShapeIdReferenceInStopTime validateShapeIdReferenceInStopTime;
    private final ValidateStopTimeTripId validateStopTimeTripId;
    private final ValidateBackwardsTimeTravelForStops validateBackwardsTimeTravelForStops;

    public StopTimeBasedValidator(final GtfsDataRepository dataRepo,
                                  final ValidationResultRepository resultRepo,
                                  final Logger logger,
                                  final TimeUtils timeUtils,
                                  final ValidateShapeIdReferenceInStopTime validateShapeIdReferenceInStopTime,
                                  final ValidateStopTimeTripId validateStopTimeTripId,
                                  final ValidateBackwardsTimeTravelForStops validateBackwardsTimeTravelForStops
    ) {
        this.resultRepo = resultRepo;
        this.dataRepo = dataRepo;
        this.logger = logger;
        this.timeUtils = timeUtils;
        this.validateShapeIdReferenceInStopTime = validateShapeIdReferenceInStopTime;
        this.validateStopTimeTripId = validateStopTimeTripId;
        this.validateBackwardsTimeTravelForStops = validateBackwardsTimeTravelForStops;
    }

    /**
     * Executes validation rules based on file `stop_times.txt`
     */
    public void execute() {
        logger.info("Validating rules: 'E047 - Bad combination of stoptime arrival and departure times`");
        logger.info("                  'E034 - `shape_id` not found");
        logger.info("                  'E037 - `trip_id` not found");

        dataRepo.getStopTimeAll().values().forEach(stopTimeCollection -> {
            // E047
            validateBackwardsTimeTravelForStops.execute(resultRepo, stopTimeCollection, timeUtils);
            stopTimeCollection.values().forEach(stopTime -> {
                final Trip trip = dataRepo.getTripById(stopTime.getTripId());
                final Map<Integer, ShapePoint> shape = dataRepo.getShapeById(trip == null ? null : trip.getShapeId());
                // E034 - cross validation
                validateShapeIdReferenceInStopTime.execute(resultRepo, stopTime, shape, trip);
                // E037 - cross validation
                validateStopTimeTripId.execute(resultRepo, stopTime, dataRepo.getTripAll());
            });
        });
    }
}
