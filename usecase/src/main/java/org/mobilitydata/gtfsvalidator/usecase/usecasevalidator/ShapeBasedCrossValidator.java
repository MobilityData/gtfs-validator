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

package org.mobilitydata.gtfsvalidator.usecase.usecasevalidator;

import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.usecase.ValidateShapeIncreasingDistance;
import org.mobilitydata.gtfsvalidator.usecase.ValidateShapeUsage;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.HashSet;
import java.util.Set;

/**
 * Use case to execute cross validation based on GTFS files `shapes.txt`
 * E038 - All shapes should be used in `trips.txt`
 */
public class ShapeBasedCrossValidator {
    private final ValidationResultRepository resultRepo;
    private final GtfsDataRepository dataRepo;
    private final Logger logger;
    private final ValidateShapeUsage validateShapeUsage;
    private final ValidateShapeIncreasingDistance validateShapeIncreasingDistance;

    public ShapeBasedCrossValidator(final GtfsDataRepository dataRepo,
                                    final ValidationResultRepository resultRepo,
                                    final Logger logger,
                                    final ValidateShapeUsage validateShapeUsage,
                                    final ValidateShapeIncreasingDistance validateShapeIncreasingDistance) {
        this.resultRepo = resultRepo;
        this.dataRepo = dataRepo;
        this.logger = logger;
        this.validateShapeUsage = validateShapeUsage;
        this.validateShapeIncreasingDistance = validateShapeIncreasingDistance;
    }

    /**
     * Executes cross validation rules based on file `shapes.txt`
     */
    public void execute() {
        logger.info("Validating rules :'E038 - All shapes should be used in GTFS `trips.txt`");
        logger.info("Validating rules :'E058 - Decreasing `shape_dist_traveled` in `shapes.txt`");

        final Set<String> tripShapeIdCollection = new HashSet<>();
        dataRepo.getTripAll().values()
                .forEach(trip -> tripShapeIdCollection.add(trip.getShapeId()));
        dataRepo.getShapeAll().values().
                forEach(shape -> {
                    final String shapeId = shape.values().stream()
                            .findFirst()
                            .get()
                            .getShapeId();
                    validateShapeUsage.execute(resultRepo, shapeId, tripShapeIdCollection);
                    validateShapeIncreasingDistance.execute(shape, shapeId);
                });
    }
}
