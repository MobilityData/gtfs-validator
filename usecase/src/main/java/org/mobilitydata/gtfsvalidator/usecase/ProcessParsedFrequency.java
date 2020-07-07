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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.frequencies.Frequency;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;

import java.util.List;


/**
 * This use case turns a parsed entity representing a row from frequencies.txt into a concrete class
 */
public class ProcessParsedFrequency {
    private final ValidationResultRepository resultRepository;
    private final GtfsDataRepository gtfsDataRepository;
    private final TimeUtils timeUtils;
    private final Frequency.FrequencyBuilder builder;

    public ProcessParsedFrequency(final ValidationResultRepository resultRepository,
                                  final GtfsDataRepository gtfsDataRepository,
                                  final TimeUtils timeUtils,
                                  final Frequency.FrequencyBuilder builder) {
        this.resultRepository = resultRepository;
        this.gtfsDataRepository = gtfsDataRepository;
        this.timeUtils = timeUtils;
        this.builder = builder;
    }

    /**
     * Use case execution method to go from a row from frequencies.txt to an internal representation.
     * <p>
     * This use case extracts values from a {@code ParsedEntity} and creates a {@code Frequency} object if the
     * requirements from the official GTFS specification are met. When these requirements are not met, related notices
     * generated in {@code Frequency.FrequencyBuilder} are added to the result repository provided in the
     * constructor. This use case also adds a {@code EntityMustBeUniqueNotice} to said repository if the uniqueness
     * constraint on fare attribute entities is not respected.
     *
     * @param validatedFrequency entity to be processed and added to the GTFS data repository
     */
    public void execute(final ParsedEntity validatedFrequency) {
        final String tripId = (String) validatedFrequency.get("trip_id");
        final Integer startTime = timeUtils.convertHHMMSSToIntFromNoonOfDayOfService(
                (String) validatedFrequency.get("start_time"));
        final Integer endTime = timeUtils.convertHHMMSSToIntFromNoonOfDayOfService(
                (String) validatedFrequency.get("end_time"));
        final Integer headwaySecs = (Integer) validatedFrequency.get("headway_secs");
        final Integer exactTimes = (Integer) validatedFrequency.get("exact_times");

        builder.tripId(tripId)
                .startTime(startTime)
                .endTime(endTime)
                .headwaySecs(headwaySecs)
                .exactTimes(exactTimes);

        final EntityBuildResult<?> frequency = builder.build();

        if (frequency.isSuccess()) {
            if (gtfsDataRepository.addFrequency((Frequency) frequency.getData()) == null) {
                resultRepository.addNotice(new DuplicatedEntityNotice("frequencies.txt",
                        "trip_id; start_time", validatedFrequency.getEntityId()));
            }
        } else {
            // at this step it is certain that calling getData method will return a list of notices, therefore there is
            // no need for cast check
            //noinspection unchecked
            ((List<Notice>) frequency.getData()).forEach(resultRepository::addNotice);
        }
    }
}
