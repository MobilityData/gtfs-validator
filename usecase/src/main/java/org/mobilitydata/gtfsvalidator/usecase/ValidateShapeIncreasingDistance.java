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

import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DecreasingShapeDistanceNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This use case checks that `shape_dist_traveled` increase must increase along with `shape_pt_sequence` of rows from
 * GTFS shapes.txt.
 */
public class ValidateShapeIncreasingDistance {

    /**
     * @param shape   map storing all shape point information for a given `shape_id`
     * @param shapeId id of the shape to process
     */
    public void execute(final Map<Integer, ShapePoint> shape,
                        final String shapeId,
                        final ValidationResultRepository resultRepo) {
        // get previous shape point relevant information
        final var previousShapePointData = new Object() {
            Float shapeDistTraveled = null;
            Integer shapePtSequence = null;
        };
        final List<ShapePoint> shapePointCollection = shape.values().stream()
                .filter(shapePoint -> shapePoint.getShapeDistTraveled() != null)
                .collect(Collectors.toList());
        shapePointCollection.forEach(shapePoint -> {
            final Float shapeDistTraveled = shapePoint.getShapeDistTraveled();
            final int shapePtSequence = shapePoint.getShapePtSequence();
            if (shapeDistTraveled != null && previousShapePointData.shapeDistTraveled != null) {
                if (previousShapePointData.shapeDistTraveled >= shapeDistTraveled) {
                    resultRepo.addNotice(
                            new DecreasingShapeDistanceNotice(
                                    shapeId,
                                    shapePtSequence,
                                    shapeDistTraveled,
                                    previousShapePointData.shapePtSequence,
                                    previousShapePointData.shapeDistTraveled)
                    );
                }
            }
            previousShapePointData.shapeDistTraveled = shapeDistTraveled;
            previousShapePointData.shapePtSequence = shapePtSequence;
//            // to exclude any row where field `shape_dist_traveled` is not provided
//            if (shapeDistTraveled != null) {
//            }
        });
    }
}
