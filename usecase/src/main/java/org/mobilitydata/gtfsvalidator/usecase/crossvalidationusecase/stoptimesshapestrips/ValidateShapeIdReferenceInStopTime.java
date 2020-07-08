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

import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.ShapeIdNotFoundNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.*;

/**
 * Use case to validate that in `stop_times.txt` all records having a non-null value for field `shape_dist_travelled`
 * refer to a record from `trips.txt` that itself refers to an existing record of `shapes.txt`.
 */
public class ValidateShapeIdReferenceInStopTime {
    public void execute(final ValidationResultRepository resultRepo,
                        final StopTime stopTime,
                        final Map<Integer, ShapePoint> shape,
                        final Trip trip) {
        if(stopTime != null) {
            if (stopTime.getShapeDistTraveled() != null) {
                if (trip != null) {
                    final String tripId = trip.getTripId();
                    final String shapeId = trip.getShapeId();
                    final Integer stopSequence = stopTime.getStopSequence();
                    if (shapeId == null) {
                        resultRepo.addNotice(new MissingRequiredValueNotice("trips.txt", "shape_id",
                                tripId));
                    } else {
                        if (shape == null) {
                            resultRepo.addNotice(new ShapeIdNotFoundNotice("stop_times.txt", "shape_id",
                                    "trip_id", "stop_sequence", tripId,
                                    stopSequence, shapeId));
                        }
                    }
                }
            }
        }
    }
}
