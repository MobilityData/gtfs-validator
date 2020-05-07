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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Attribution;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.List;

/**
 * Use case to process a {@link ParsedEntity} with an internal representation defined by {@link Attribution}
 */
public class ProcessParsedAttribution {
    private final ValidationResultRepository resultRepository;
    private final GtfsDataRepository gtfsDataRepository;
    private final Attribution.AttributionBuilder builder;

    public ProcessParsedAttribution(final ValidationResultRepository resultRepository,
                                    final GtfsDataRepository gtfsDataRepository,
                                    final Attribution.AttributionBuilder builder) {
        this.resultRepository = resultRepository;
        this.gtfsDataRepository = gtfsDataRepository;
        this.builder = builder;
    }

    /**
     * Use case execution method to go from a row from attributions.txt to an internal representation.
     * <p>
     * This use case extracts values from a {@code ParsedEntity} and creates a {@code Attribution} object if the
     * requirements from the official GTFS specification are met. When these requirements are mot met, related notices
     * generated in {@code Attribution.AttributionBuilder} are added to the result repository provided to the
     * constructor. This use case also adds a {@code DuplicatedEntityNotice} to said repository if the uniqueness
     * constraint on attribution entities is not respected.
     *
     * @param validatedAttributionEntity entity to be process and added to the {@link GtfsDataRepository}
     */
    public void execute(final ParsedEntity validatedAttributionEntity) {
        final String attributionId = (String) validatedAttributionEntity.get("attribution_id");
        final String agencyId = (String) validatedAttributionEntity.get("agency_id");
        final String routeId = (String) validatedAttributionEntity.get("route_id");
        final String tripId = (String) validatedAttributionEntity.get("trip_id");
        final String organizationName = (String) validatedAttributionEntity.get("organization_name");
        final Integer isProducer = (Integer) validatedAttributionEntity.get("is_producer");
        final Integer isAuthority = (Integer) validatedAttributionEntity.get("is_authority");
        final Integer isOperator = (Integer) validatedAttributionEntity.get("is_operator");
        final String attributionUrl = (String) validatedAttributionEntity.get("attribution_url");
        final String attributionEmail = (String) validatedAttributionEntity.get("attribution_email");
        final String attributionPhone = (String) validatedAttributionEntity.get("attribution_phone");

        final EntityBuildResult<?> attribution = builder.attributionId(attributionId)
                .agencyId(agencyId)
                .routeId(routeId)
                .tripId(tripId)
                .organizationName(organizationName)
                .isProducer(isProducer)
                .isAuthority(isAuthority)
                .isOperator(isOperator)
                .attributionUrl(attributionUrl)
                .attributionEmail(attributionEmail)
                .attributionPhone(attributionPhone)
                .build();

        if (attribution.isSuccess()) {
            if (gtfsDataRepository.addAttribution((Attribution) attribution.getData()) == null) {
                resultRepository.addNotice(new DuplicatedEntityNotice("attributions.txt",
                        "organization_name", validatedAttributionEntity.getEntityId()));
            }
        } else {
            //noinspection unchecked to avoid lint
            ((List<Notice>) attribution.getData()).forEach(resultRepository::addNotice);
        }
    }
}