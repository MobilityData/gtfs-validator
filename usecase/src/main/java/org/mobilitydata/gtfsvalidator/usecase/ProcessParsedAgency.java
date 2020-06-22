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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Agency;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.List;

/**
 * This use case turns a parsed entity representing a row from agency.txt into a concrete class
 */
public class ProcessParsedAgency {
    private final ValidationResultRepository resultRepository;
    private final GtfsDataRepository gtfsDataRepository;
    private final Agency.AgencyBuilder builder;

    public ProcessParsedAgency(final ValidationResultRepository resultRepository,
                               final GtfsDataRepository gtfsDataRepository,
                               final Agency.AgencyBuilder builder) {
        this.resultRepository = resultRepository;
        this.gtfsDataRepository = gtfsDataRepository;
        this.builder = builder;
    }

    /**
     * Use case execution method to go from a row from agency.txt to an internal representation.
     * <p>
     * This use case extracts values from a {@code ParsedEntity} and creates a {@code Agency} object if the requirements
     * from the official GTFS specification are met. When these requirements are not met, related notices generated in
     * {@code Agency.AgencyBuilder} are added to the result repository provided in the constructor.
     * This use case also adds a {@code DuplicatedEntityNotice} to said repository if the uniqueness constraint on
     * agency entities is not respected.
     *
     * @param validatedAgencyEntity entity to be processed and added to the GTFS data repository
     */
    public void execute(final ParsedEntity validatedAgencyEntity) {
        final String agencyId = (String) validatedAgencyEntity.get("agency_id");
        final String agencyName = (String) validatedAgencyEntity.get("agency_name");
        final String agencyUrl = (String) validatedAgencyEntity.get("agency_url");
        final String agencyTimezone = (String) validatedAgencyEntity.get("agency_timezone");
        final String agencyLang = (String) validatedAgencyEntity.get("agency_lang");
        final String agencyPhone = (String) validatedAgencyEntity.get("agency_phone");
        final String agencyFareUrl = (String) validatedAgencyEntity.get("agency_fare_url");
        final String agencyEmail = (String) validatedAgencyEntity.get("agency_email");

        builder.clearFieldAll()
                .agencyId(agencyId)
                .agencyName(agencyName)
                .agencyUrl(agencyUrl)
                .agencyTimezone(agencyTimezone)
                .agencyLang(agencyLang)
                .agencyPhone(agencyPhone)
                .agencyFareUrl(agencyFareUrl)
                .agencyEmail(agencyEmail);

        @SuppressWarnings("rawtypes") final EntityBuildResult agency = builder.build();

        if (agency.isSuccess()) {
            if (gtfsDataRepository.addAgency((Agency) agency.getData()) == null) {
                resultRepository.addNotice(new DuplicatedEntityNotice("agency.txt", "agency_id",
                        validatedAgencyEntity.getEntityId()));
            }
        } else {
            //noinspection unchecked
            ((List<Notice>) agency.getData()).forEach(resultRepository::addNotice);
        }
    }
}