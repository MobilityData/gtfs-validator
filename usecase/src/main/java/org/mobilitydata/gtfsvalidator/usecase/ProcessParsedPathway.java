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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.pathways.Pathway;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.List;

/**
 * This use case turns a parsed entity representing a row from agency.txt into a concrete class
 */
public class ProcessParsedPathway {
    private final ValidationResultRepository resultRepository;
    private final GtfsDataRepository gtfsDataRepository;
    private final ExecParamRepository execParamRepository;
    private final Pathway.PathwayBuilder builder;

    public ProcessParsedPathway(final ValidationResultRepository resultRepository,
                                final GtfsDataRepository gtfsDataRepository,
                                final ExecParamRepository execParamRepository,
                                final Pathway.PathwayBuilder builder) {
        this.resultRepository = resultRepository;
        this.gtfsDataRepository = gtfsDataRepository;
        this.execParamRepository = execParamRepository;
        this.builder = builder;
    }

    /**
     * Use case execution method to go from a row from pathways.txt to an internal representation.
     * <p>
     * This use case extracts values from a {@code ParsedEntity} and creates a {@code Pathway} object if the
     * requirements from the official GTFS specification are met. When these requirements are not met, related notices
     * generated in {@code Pathway.PathwayBuilder} are added to the result repository provided in the constructor.
     * This use case also adds a {@code DuplicatedEntityNotice} to said repository if the uniqueness constraint on
     * agency entities is not respected.
     *
     * @param validatedParsedPathway entity to be processed and added to the GTFS data repository
     */
    public void execute(final ParsedEntity validatedParsedPathway) {
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

        final EntityBuildResult<?> pathway = builder.build(
                Float.parseFloat(execParamRepository.getExecParamValue(ExecParamRepository.PATHWAY_MIN_LENGTH_KEY)),
                Float.parseFloat(execParamRepository.getExecParamValue(ExecParamRepository.PATHWAY_MAX_LENGTH_KEY)),
                Integer.parseInt(
                        execParamRepository.getExecParamValue(ExecParamRepository.PATHWAY_MIN_TRAVERSAL_TIME_KEY)),
                Integer.parseInt(
                        execParamRepository.getExecParamValue(ExecParamRepository.PATHWAY_MAX_TRAVERSAL_TIME_KEY)),
                Integer.parseInt(
                        execParamRepository.getExecParamValue(ExecParamRepository.PATHWAY_MIN_STAIR_COUNT_KEY)),
                Integer.parseInt(
                        execParamRepository.getExecParamValue(ExecParamRepository.PATHWAY_MAX_STAIR_COUNT_KEY)),
                Float.parseFloat(execParamRepository.getExecParamValue(ExecParamRepository.PATHWAY_MAX_SLOPE_KEY)),
                Float.parseFloat(
                        execParamRepository.getExecParamValue(ExecParamRepository.PATHWAY_MIN_WIDTH_LOWER_BOUND_KEY)),
                Float.parseFloat(
                        execParamRepository.getExecParamValue(ExecParamRepository.PATHWAY_MIN_WIDTH_UPPER_BOUND_KEY))
        );

        if (pathway.isSuccess()) {
            if (gtfsDataRepository.addPathway((Pathway) pathway.getData()) == null) {
                resultRepository.addNotice(new DuplicatedEntityNotice("pathways.txt", "pathway_id",
                        validatedParsedPathway.getEntityId()));
            }
        } else {
            // at this step it is certain that calling getData method will return a list of notices, therefore there is
            // no need for cast check
            //noinspection unchecked
            ((List<Notice>) pathway.getData()).forEach(resultRepository::addNotice);
        }
    }
}