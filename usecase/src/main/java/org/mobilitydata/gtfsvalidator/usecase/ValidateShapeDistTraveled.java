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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FloatFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.usecase.distancecalculationutils.DistanceCalculationUtils;
import org.mobilitydata.gtfsvalidator.usecase.distancecalculationutils.DistanceUnit;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Map;
import java.util.TreeMap;

/**
 * Use case to validate that in `stop_times.txt` all`shape_dist_travelled` are smaller or equal to their total shape
 * related length. Note that this use case is triggered after checking the range validity of field
 * `shape_dist_traveled`, which means that all values provided for this field are supposed to be positive.
 */
public class ValidateShapeDistTraveled {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final DistanceCalculationUtils distanceCalculationUtils;
    private final Logger logger;

    /**
     * @param dataRepo    a repository storing the data of a GTFS dataset
     * @param resultRepo  a repository storing information about the validation process
     * @param logger      a logger displaying information about the validation process
     */
    public ValidateShapeDistTraveled(final GtfsDataRepository dataRepo,
                                     final ValidationResultRepository resultRepo,
                                     final DistanceCalculationUtils distanceCalculationUtils,
                                     final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.distanceCalculationUtils = distanceCalculationUtils;
        this.logger = logger;
    }

    /**
     * Use case execution method: checks if `stop_times.shape_dist_traveled` is smaller or equal to the related shape
     * total length.
     */
    public void execute() {
        logger.info("Validating rule 'E036 - `If provided, stop_times.shape_dist_traveled` must be smaller or equal to"+
                " shape total distance" + System.lineSeparator());

        final Map<String, TreeMap<Integer, StopTime>> stopTimePerTripId = dataRepo.getStopTimeAll();
        stopTimePerTripId.forEach(
                (tripId, stopTimeCollection) -> dataRepo.getStopTimeByTripId(tripId)
                        .forEach((stopSequence, stopTime) -> {
                            final Float shapeDistanceTraveled = stopTime.getShapeDistTraveled();
                            if (shapeDistanceTraveled != null) {
                                final Trip trip = dataRepo.getTripById(tripId);
                                if (trip != null) {
                                    final String shapeId = trip.getShapeId();
                                    if (shapeId != null) {
                                        final Map<Integer, ShapePoint> shape = dataRepo.getShapeById(shapeId);
                                        if (shape != null) {
                                            final float shapeTotalDistance = (float) distanceCalculationUtils
                                                    .getShapeTotalDistance(shape, DistanceUnit.KILOMETER);
                                            if (shapeDistanceTraveled > shapeTotalDistance) {
                                                resultRepo.addNotice(
                                                        new FloatFieldValueOutOfRangeNotice("stop_times.txt",
                                                                "shape_dist_traveled", 0,
                                                                shapeTotalDistance,
                                                                shapeDistanceTraveled,
                                                                "trip_id",
                                                                "stop_sequence",
                                                                tripId, stopSequence));
                                            }
                                        }
                                    }
                                }
                            }
                        })
        );
    }
}
