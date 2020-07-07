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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.NonExistingShapeNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.*;

/**
 * Use case to validate that in `stop_times.txt` all records having a non-null value for field `shape_dist_travelled`
 * refer to a record from `trips.txt` that itself refers to an existing record of `shapes.txt`.
 */
public class ValidateShapeIdReferenceInStopTime {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    /**
     *
     * @param dataRepo    a repository storing the data of a GTFS dataset
     * @param resultRepo  a repository storing information about the validation process
     * @param logger      a logger displaying information about the validation process
     */
    public ValidateShapeIdReferenceInStopTime(final GtfsDataRepository dataRepo,
                                              final ValidationResultRepository resultRepo,
                                              final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method: checks if every record of file `stop_times.txt` that have a non-null value for field
     * `shape_dist_travelled` refer to a record from `trips.txt` which itself refers to an existing record from
     * `shapes.txt`.
     * Each time a record from `stop_times.txt` that has a non-null value for field `shape_dist_travelled` refers to
     * a non existing record from `shapes.txt` a {@code NonExistingShapeNotice} is generated and added to the
     * {@code ValidationResultRepository} provided in the constructor.
     * Each time a record from `stop_times.txt` that has a non-null value for field `shape_dist_travelled` refers to a
     * record from `trips.txt` that itself does not refer to any record from `shapes.txt` a
     * {@code MissingRequiredValueNotice} is generated and added to the {@code ValidationResultRepository} in the
     * constructor.
     */
    public void execute() {
        logger.info("Validating rule 'E034 - Invalid `shape_id`" + System.lineSeparator());

        final Map<String, TreeMap<Integer, StopTime>> stopTimePerTripId = dataRepo.getStopTimeAll();
        stopTimePerTripId.forEach(
                (tripId, stopTimeCollection) -> dataRepo.getStopTimeByTripId(tripId)
                        .forEach((stopSequence, stopTime) -> {
                            if (stopTime.getShapeDistTraveled() != null) {
                                final Trip trip = dataRepo.getTripById(tripId);
                                if (trip != null) {
                                    // cross references to trips.txt are checked in E035
                                    final String shapeId = trip.getShapeId();
                                    if (shapeId == null) {
                                        resultRepo.addNotice(new MissingRequiredValueNotice("trips.txt",
                                                "shape_id", tripId));
                                    } else {
                                        if (dataRepo.getShapeById(shapeId) == null) {
                                            resultRepo.addNotice(new NonExistingShapeNotice("stop_times.txt",
                                                    "shape_id", "trip_id",
                                                    "stop_sequence", tripId, stopSequence,
                                                    shapeId));
                                        }
                                    }
                                }
                            }
                        })
        );
    }
}
