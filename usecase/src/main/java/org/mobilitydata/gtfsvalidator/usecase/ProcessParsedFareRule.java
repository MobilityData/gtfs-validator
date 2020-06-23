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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.FareRule;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.List;

/**
 * This use case turns a parsed entity representing a row from fare_rules.txt into a concrete class
 */
public class ProcessParsedFareRule {
    private final ValidationResultRepository resultRepository;
    private final GtfsDataRepository gtfsDataRepository;
    private final FareRule.FareRuleBuilder builder;

    public ProcessParsedFareRule(final ValidationResultRepository resultRepository,
                                 final GtfsDataRepository gtfsDataRepository,
                                 final FareRule.FareRuleBuilder builder) {
        this.resultRepository = resultRepository;
        this.gtfsDataRepository = gtfsDataRepository;
        this.builder = builder;
    }

    /**
     * Use case execution method to go from a row from fare_rules.txt to an internal representation.
     * <p>
     * This use case extracts values from a {@code ParsedEntity} and creates a {@code FareRule} object if the
     * requirements from the official GTFS specification are met. When these requirements are not met, related notices
     * generated in {@code FareRule.FareRuleBuilder} are added to the result repository provided in the constructor.
     * This use case also adds a {@code DuplicatedEntityNotice} to said repository if the uniqueness constraint on
     * fare rule entities is not respected.
     *
     * @param validatedParsedFareRuleEntity entity to be processed and added to the GTFS data repository
     */
    public void execute(final ParsedEntity validatedParsedFareRuleEntity) {
        final String fareId = (String) validatedParsedFareRuleEntity.get("fare_id");
        final String routeId = (String) validatedParsedFareRuleEntity.get("route_id");
        final String originId = (String) validatedParsedFareRuleEntity.get("origin_id");
        final String destinationId = (String) validatedParsedFareRuleEntity.get("destination_id");
        final String containsId = (String) validatedParsedFareRuleEntity.get("contains_id");

        builder.clear()
                .fareId(fareId)
                .routeId(routeId)
                .originId(originId)
                .destinationId(destinationId)
                .containsId(containsId);

        final EntityBuildResult<?> fareRule = builder.build();

        if (fareRule.isSuccess()) {
            if (gtfsDataRepository.addFareRule((FareRule) fareRule.getData()) == null) {
                resultRepository.addNotice(new DuplicatedEntityNotice("fare_rules.txt", "fare_id; " +
                        "route_id; origin_id; destination_id; contains_id",
                        validatedParsedFareRuleEntity.getEntityId()));
            }
        } else {
            // at this step it is certain that calling getData method will return a list of notices, therefore there is
            // no need for cast check
            //noinspection unchecked
            ((List<Notice>) fareRule.getData()).forEach(resultRepository::addNotice);
        }
    }
}