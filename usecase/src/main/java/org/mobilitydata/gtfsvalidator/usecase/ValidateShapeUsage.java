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

import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.ShapeNotUsedNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Set;

public class ValidateShapeUsage {
    /**
     * Checks if all {@code Shape} entities defined by GTFS `shapes.txt` are referred to in GTFS `trips.txt`. A new
     * notice is generated each time this condition is false.
     * This notice is then added to the {@link ValidationResultRepository} provided in the constructor.
     */
    public void execute(final ValidationResultRepository resultRepo,
                        final String shapeId,
                        final Set<String> tripShapeIdCollection) {
        if (!tripShapeIdCollection.contains(shapeId)) {
            resultRepo.addNotice(new ShapeNotUsedNotice(shapeId, "shape_id"));
        }
    }
}
