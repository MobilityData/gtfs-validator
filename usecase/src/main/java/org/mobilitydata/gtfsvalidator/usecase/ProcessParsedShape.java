/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Shape;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.List;

/**
 * This use case turns a parsed entity representing a row from shapes.txt into a concrete class
 */
public class ProcessParsedShape {
    private final ValidationResultRepository resultRepository;
    private final GtfsDataRepository gtfsDataRepository;
    private final Shape.ShapeBuilder builder;

    public ProcessParsedShape(final ValidationResultRepository resultRepository,
                              final GtfsDataRepository gtfsDataRepository,
                              final Shape.ShapeBuilder builder) {
        this.resultRepository = resultRepository;
        this.gtfsDataRepository = gtfsDataRepository;
        this.builder = builder;
    }

    /**
     * Use case execution method to go from a row from shapes.txt to an internal representation.
     * <p>
     * This use case extracts values from a {@code ParsedEntity} and creates a {@code Shape} object if the requirements
     * from the official GTFS specification are met. When these requirements are mot met, related notices generated in
     * {@code Shape.ShapeBuilder} are added to the result repository provided to the constructor.
     * This use case also adds a {@code DuplicatedEntityNotice} to said repository if the uniqueness constraint on
     * shape entities is not respected.
     *
     * @param validatedShapeEntity entity to be processed and added to the GTFS data repository
     */
    public void execute(final ParsedEntity validatedShapeEntity) {
        final String shapeId = (String) validatedShapeEntity.get("shape_id");
        final Float shapePtLat = (Float) validatedShapeEntity.get("shape_pt_lat");
        final float shapePtLon = (Float) validatedShapeEntity.get("shape_pt_lon");
        final Integer shapePtSequence = (Integer) validatedShapeEntity.get("shape_pt_sequence");
        final Float shapeDistTraveled = (Float) validatedShapeEntity.get("shape_dist_traveled");

        builder.shapeId(shapeId)
                .shapePtLat(shapePtLat)
                .shapePtLon(shapePtLon)
                .shapePtSequence(shapePtSequence)
                .shapeDistTraveled(shapeDistTraveled);

        @SuppressWarnings("rawtypes") final EntityBuildResult shape = builder.build();

        if (shape.isSuccess()) {
            if (gtfsDataRepository.addShape((Shape) shape.getData()) == null) {
                resultRepository.addNotice(new DuplicatedEntityNotice("shapes.txt",
                        "shape_id", validatedShapeEntity.getEntityId()));
            }
        } else {
            //noinspection unchecked
            ((List<Notice>) shape.getData()).forEach(resultRepository::addNotice);
        }
    }
}