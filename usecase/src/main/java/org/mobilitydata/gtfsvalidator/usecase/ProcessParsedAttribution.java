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

import org.mobilitydata.gtfsvalidator.domain.entity.Attribution;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.AttributionMustHaveRoleNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.OrganizationNameCanNotBeNullNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

/**
 * Use case to process a {@link ParsedEntity} with an internal representation defined by {@link Attribution}
 */
public class ProcessParsedAttribution {

    private final GtfsSpecRepository specRepo;
    private final ValidationResultRepository resultRepo;
    private final GtfsDataRepository gtfsRepo;

    public ProcessParsedAttribution(final GtfsSpecRepository specRepo,
                                    final ValidationResultRepository resultRepo,
                                    final GtfsDataRepository gtfsRepo) {
        this.specRepo = specRepo;
        this.resultRepo = resultRepo;
        this.gtfsRepo = gtfsRepo;
    }

    /**
     * Use case execution method to go from a row from attributions.txt to an internal representation of the
     * {@link Attribution} object. Each {@link Attribution} is added to the {@link GtfsDataRepository}.
     * <p>
     * This use case extracts values from a {@link ParsedEntity} and creates a {@link Attribution} object. If the
     * required conditions to create such object are not met, either a {@link OrganizationNameCanNotBeNullNotice} is
     * created or a {@link AttributionMustHaveRoleNotice} is created; then added to the validation result repository
     * provided in the use case constructor.
     *
     * @param validatedAttributionEntity entity to be process and added to the {@link GtfsDataRepository}
     */
    @SuppressWarnings("ConstantConditions")
    public void execute(final ParsedEntity validatedAttributionEntity) {

        String attributionId = (String) validatedAttributionEntity.get("attribution_id");
        String agencyId = (String) validatedAttributionEntity.get("agency_id");
        String routeId = (String) validatedAttributionEntity.get("route_id");
        String tripId = (String) validatedAttributionEntity.get("trip_id");
        String organizationName = (String) validatedAttributionEntity.get("organization_name");
        Integer isProducer = (Integer) validatedAttributionEntity.get("is_producer");
        Integer isAuthority = (Integer) validatedAttributionEntity.get("is_authority");
        Integer isOperator = (Integer) validatedAttributionEntity.get("is_operator");
        String attributionUrl = (String) validatedAttributionEntity.get("attribution_url");
        String attributionEmail = (String) validatedAttributionEntity.get("attribution_email");
        String attributionPhone = (String) validatedAttributionEntity.get("attribution_phone");

        Attribution.AttributionBuilder builder = new Attribution.AttributionBuilder(organizationName);

        try {
            builder.attributionId(attributionId)
                    .agencyId(agencyId)
                    .routeId(routeId)
                    .tripId(tripId)
                    .isOperator(isOperator)
                    .isProducer(isProducer)
                    .isAuthority(isAuthority)
                    .attributionEmail(attributionEmail)
                    .attributionUrl(attributionUrl)
                    .attributionPhone(attributionPhone)
                    .build();

        } catch (IllegalArgumentException e) {
            if (organizationName == null) {
                resultRepo.addNotice(new OrganizationNameCanNotBeNullNotice());
            } else {
                resultRepo.addNotice(new AttributionMustHaveRoleNotice(organizationName));
            }
        }
    }
}