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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.EntityMustBeUniqueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.sql.SQLIntegrityConstraintViolationException;

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
     * This use case extracts values from a {@link ParsedEntity} and creates a {@link Agency} object. If values for
     * agency_name, agency_url and agency_timezone fields are null, a {@link MissingRequiredValueNotice} is created and
     * added to the validation result repository provided in the use case constructor; and
     * {@link IllegalArgumentException} is thrown.
     *
     * @param validatedAgencyEntity entity to be processed and added to the GTFS data repository
     * @throws IllegalArgumentException if specification requirements are not met regarding values for agency_name,
     *                                  agency_url and agency_timezone fields
     */
    public void execute(final ParsedEntity validatedAgencyEntity) throws IllegalArgumentException,
            SQLIntegrityConstraintViolationException {

        final String agencyId = (String) validatedAgencyEntity.get("agency_id");
        final String agencyName = (String) validatedAgencyEntity.get("agency_name");
        final String agencyUrl = (String) validatedAgencyEntity.get("agency_url");
        final String agencyTimezone = (String) validatedAgencyEntity.get("agency_timezone");
        final String agencyLang = (String) validatedAgencyEntity.get("agency_lang");
        final String agencyPhone = (String) validatedAgencyEntity.get("agency_phone");
        final String agencyFareUrl = (String) validatedAgencyEntity.get("agency_fare_url");
        final String agencyEmail = (String) validatedAgencyEntity.get("agency_email");

        try {
            builder.agencyId(agencyId)
                    .agencyName(agencyName)
                    .agencyUrl(agencyUrl)
                    .agencyTimezone(agencyTimezone)
                    .agencyLang(agencyLang)
                    .agencyPhone(agencyPhone)
                    .agencyFareUrl(agencyFareUrl)
                    .agencyEmail(agencyEmail);

            gtfsDataRepository.addAgency(builder.build());

        } catch (IllegalArgumentException e) {

            if (agencyName == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("agency.txt", "agency_name",
                        validatedAgencyEntity.getEntityId()));
            }

            if (agencyUrl == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("agency.txt", "agency_url",
                        validatedAgencyEntity.getEntityId()));
            }

            if (agencyTimezone == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("agency.txt",
                        "agency_timezone", validatedAgencyEntity.getEntityId()));
            }
            throw e;

        } catch (SQLIntegrityConstraintViolationException e) {
            resultRepository.addNotice(new EntityMustBeUniqueNotice("agency.txt", "agency_id",
                    validatedAgencyEntity.getEntityId()));
            throw e;
        }
    }
}