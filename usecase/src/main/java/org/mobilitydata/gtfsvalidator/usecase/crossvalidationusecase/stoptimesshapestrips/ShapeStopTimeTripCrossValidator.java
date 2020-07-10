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

package org.mobilitydata.gtfsvalidator.usecase.crossvalidationusecase.stoptimesshapestrips;

import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Map;

/**
 * Use case to execute cross validation for GTFS files `shapes.txt`, `stop_times.txt` and `trips.txt`
 * E034 - `shape_id` not found
 */
public class ShapeStopTimeTripCrossValidator {
    private final ValidationResultRepository resultRepo;
    private final GtfsDataRepository dataRepo;
    private final Logger logger;

    public ShapeStopTimeTripCrossValidator(final GtfsDataRepository dataRepo,
                                           final ValidationResultRepository resultRepo,
                                           final Logger logger) {
        this.resultRepo = resultRepo;
        this.dataRepo = dataRepo;
        this.logger = logger;
    }

    /**
     * Executes cross validation rules
     */
    public void execute() {
        logger.info("Validating rule 'E034 - `shape_id` not found" + System.lineSeparator());
        checkE034();
    }

    /**
     * Instantiates and executes validation of rule E034
     */
    private void checkE034() {
        final ValidateShapeIdReferenceInStopTime validateShapeIdReferenceInStopTime =
                new ValidateShapeIdReferenceInStopTime();

        dataRepo.getStopTimeAll().forEach((tripId, stopTimeCollection) -> stopTimeCollection.
                forEach((stopSequence, stopTime) -> {
                    final Trip trip = dataRepo.getTripById(tripId);
                    final Map<Integer, ShapePoint> shape = dataRepo.getShapeById(trip == null ? null : trip.getShapeId());
                    validateShapeIdReferenceInStopTime.execute(resultRepo, stopTime, shape, trip);
                }));
    }
}
