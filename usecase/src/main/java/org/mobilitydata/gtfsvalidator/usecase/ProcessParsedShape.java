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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Shape;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.EntityMustBeUniqueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.FloatFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.sql.SQLIntegrityConstraintViolationException;

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

    public void execute(final ParsedEntity validatedShapeEntity) throws IllegalArgumentException,
            SQLIntegrityConstraintViolationException {

        final String shapeId = (String) validatedShapeEntity.get("shape_id");
        final float shapePtLat = (float) validatedShapeEntity.get("shape_pt_lat");
        final float shapePtLon = (float) validatedShapeEntity.get("shape_pt_lon");
        final int shapePtSequence = (int) validatedShapeEntity.get("shape_pt_sequence");
        final Float shapeDistTraveled = (Float) validatedShapeEntity.get("shape_dist_traveled");

        try {
            builder.shapeId(shapeId)
                    .shapePtLat(shapePtLat)
                    .shapePtLon(shapePtLon)
                    .shapePtSequence(shapePtSequence)
                    .shapeDistTraveled(shapeDistTraveled);

            gtfsDataRepository.addShape(builder.build());
        } catch (IllegalArgumentException e) {
            if (shapeId == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("shapes.txt", "shape_id",
                        validatedShapeEntity.getEntityId()));
            }

            if (shapePtLat < -90.0f || shapePtLat > 90.0f) {
                resultRepository.addNotice(new FloatFieldValueOutOfRangeNotice("shapes.txt",
                        "shape_pt_lat", validatedShapeEntity.getEntityId(), -90.0f, 90.0f,
                        shapePtLat));
            }

            if (shapePtLon < -180.0f || shapePtLon > 180.0f) {
                resultRepository.addNotice(new FloatFieldValueOutOfRangeNotice("shapes.txt",
                        "shape_pt_lon", validatedShapeEntity.getEntityId(), -180.0f, 180.0f,
                        shapePtLon));
            }

            if (shapePtSequence < 0) {
                resultRepository.addNotice(new IntegerFieldValueOutOfRangeNotice("shapes.txt",
                        "shape_pt_sequence", validatedShapeEntity.getEntityId(), 0, Integer.MAX_VALUE,
                        shapePtSequence));
            }

            if (shapeDistTraveled != null && shapeDistTraveled < 0) {
                resultRepository.addNotice(new FloatFieldValueOutOfRangeNotice("shapes.txt",
                        "shape_dist_traveled", validatedShapeEntity.getEntityId(), 0, Float.MAX_VALUE,
                        shapeDistTraveled));
            }
            throw e;
        } catch (SQLIntegrityConstraintViolationException e) {
            resultRepository.addNotice(new EntityMustBeUniqueNotice("shapes.txt", "shape_id",
                    validatedShapeEntity.getEntityId()));
            throw e;
        }
    }
}