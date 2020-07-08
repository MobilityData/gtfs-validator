/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.AgencyIdNotFoundNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingAgencyIdNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

/**
 * Use case for E035 to validate that all records of `fare_attributes.txt` refer to an existing {@code Agency} from file
 * `agency.txt`
 */
public class ValidateFareAttributeAgencyId {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     * @param logger     a logger to log information about the validation process
     */
    public ValidateFareAttributeAgencyId(final GtfsDataRepository dataRepo,
                                         final ValidationResultRepository resultRepo,
                                         final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method: Checks if `agency_id` of a record from fare_attributes.txt is in GTFS data
     * A new notice is generated each time this condition is false.
     * This notice is then added to the {@link ValidationResultRepository} provided in the constructor.
     */
    public void execute() {
        logger.info("Validating rule 'E035 - `agency_id` not found" + System.lineSeparator());

        final int agencyCount = dataRepo.getAgencyCount();
        dataRepo.getFareAttributeAll().forEach((fareId, fareAttribute) -> {
            final String agencyId = fareAttribute.getAgencyId();
            if (agencyCount > 1) {
                if (agencyId == null) {
                    resultRepo.addNotice(
                            new MissingAgencyIdNotice("fare_attributes.txt", fareId)
                    );
                } else {
                    if (dataRepo.getAgencyById(agencyId) == null) {
                        resultRepo.addNotice(
                                new AgencyIdNotFoundNotice("fare_attributes.txt",
                                "agency_id",
                                        fareId)
                        );
                    }
                }
            } else {
                if (agencyId != null && dataRepo.getAgencyById(agencyId) == null) {
                    resultRepo.addNotice(
                            new AgencyIdNotFoundNotice("fare_attributes.txt",
                            "agency_id",
                                    fareId)
                    );
                }
            }
        });
    }
}
