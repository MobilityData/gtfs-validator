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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.pathways.Pathway;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.*;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.sql.SQLIntegrityConstraintViolationException;

public class ProcessParsedPathway {
    private final ValidationResultRepository resultRepository;
    private final GtfsDataRepository gtfsDataRepository;
    private final Pathway.PathwayBuilder builder;

    public ProcessParsedPathway(final ValidationResultRepository resultRepository,
                                final GtfsDataRepository gtfsDataRepository,
                                final Pathway.PathwayBuilder builder) {
        this.resultRepository = resultRepository;
        this.gtfsDataRepository = gtfsDataRepository;
        this.builder = builder;
    }

    @SuppressWarnings("unused")
    public void execute(final ParsedEntity validatedParsedPathway) throws IllegalArgumentException,
            SQLIntegrityConstraintViolationException {

        final String pathwayId = (String) validatedParsedPathway.get("pathway_id");
        final String fromStopId = (String) validatedParsedPathway.get("from_stop_id");
        final String toStopId = (String) validatedParsedPathway.get("to_stop_id");
        final Integer pathwayMode = (Integer) validatedParsedPathway.get("pathway_mode");
        final Integer isBidirectional = (Integer) validatedParsedPathway.get("is_bidirectional");
        final Float length = (Float) validatedParsedPathway.get("length");
        final Integer traversalTime = (Integer) validatedParsedPathway.get("traversal_tine");
        final Integer stairCount = (Integer) validatedParsedPathway.get("stair_count");
        final Float maxSlope = (Float) validatedParsedPathway.get("max_slope");
        final Float minWidth = (Float) validatedParsedPathway.get("min_width");
        final String signpostedAs = (String) validatedParsedPathway.get("signposted_as");
        final String reversedSignpostedAs = (String) validatedParsedPathway.get("reserved_signposted_as");

        try {
            builder.pathwayId(pathwayId)
                    .fromStopId(fromStopId)
                    .toStopId(toStopId)
                    .pathwayMode(pathwayMode)
                    .isBidirectional(isBidirectional)
                    .length(length)
                    .traversalTime(traversalTime)
                    .stairCount(stairCount)
                    .maxSlope(maxSlope)
                    .minWidth(minWidth)
                    .signpostedAs(signpostedAs)
                    .reversedSignpostedAs(reversedSignpostedAs);

            gtfsDataRepository.addPathway(builder.build());

        } catch (IllegalArgumentException e) {
            if (pathwayId == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("pathways.txt",
                        "pathway_id", validatedParsedPathway.getEntityId()));
            }

            if (fromStopId == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("pathways.txt",
                        "from_stop_id", validatedParsedPathway.getEntityId()));
            }

            if (toStopId == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("pathways.txt",
                        "to_stop_id", validatedParsedPathway.getEntityId()));
            }

            if (pathwayMode == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("pathways.txt",
                        "pathway_mode", validatedParsedPathway.getEntityId()));
            } else if (pathwayMode < 1 || pathwayMode > 7) {
                resultRepository.addNotice(new UnexpectedValueNotice("pathways.txt",
                        "pathway_mode", validatedParsedPathway.getEntityId(), pathwayMode));
            }

            if (isBidirectional == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("pathways.txt",
                        "is_bidirectional", validatedParsedPathway.getEntityId()));
            } else if (isBidirectional < 0 || isBidirectional > 1) {
                resultRepository.addNotice(new UnexpectedValueNotice("pathways.txt",
                        "is_bidirectional", validatedParsedPathway.getEntityId(), isBidirectional));
            }

            if (length != null && length < 0) {
                resultRepository.addNotice(new FloatFieldValueOutOfRangeNotice("pathways.txt",
                        "length", validatedParsedPathway.getEntityId(), 0, Float.MAX_VALUE, length));
            }

            if (traversalTime != null && traversalTime < 0) {
                resultRepository.addNotice(new IntegerFieldValueOutOfRangeNotice("pathways.txt",
                        "traversal_time", validatedParsedPathway.getEntityId(), 0, Integer.MAX_VALUE,
                        traversalTime));
            }

            if (stairCount != null && stairCount < 0) {
                resultRepository.addNotice(new IntegerFieldValueOutOfRangeNotice("pathways.txt",
                        "stair_count", validatedParsedPathway.getEntityId(), 0, Integer.MAX_VALUE,
                        stairCount));
            }

            if (minWidth != null && minWidth < 0) {
                resultRepository.addNotice(new FloatFieldValueOutOfRangeNotice("pathways.txt",
                        "min_width", validatedParsedPathway.getEntityId(), 0, Float.MAX_VALUE,
                        minWidth));
            }
            throw e;

        } catch (SQLIntegrityConstraintViolationException e) {
            resultRepository.addNotice(new EntityMustBeUniqueNotice("pathways.txt", "pathway_id",
                    validatedParsedPathway.getEntityId()));
            throw e;
        }
    }
}