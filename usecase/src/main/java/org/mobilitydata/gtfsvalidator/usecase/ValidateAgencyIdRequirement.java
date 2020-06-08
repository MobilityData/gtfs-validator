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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Agency;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingAgencyIdNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Collection;

/**
 * Use case to validate that a agency_id is present when GTFS file agency.txt counts more than one record.
 * This use case is triggered after defining the content of the {@code GtfsDataRepository} provided in the constructor.
 */
public class ValidateAgencyIdRequirement {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     * @param logger     a logger displaying information about the validation process
     */
    public ValidateAgencyIdRequirement(final GtfsDataRepository dataRepo,
                                       final ValidationResultRepository resultRepo,
                                       final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method: checks if every record of file agency.txt has a non null value for field agency_id
     * when said file counts more than one record. If a row from agency.txt has no value for field agency_id, a new
     * error notice is generated and added to the {@code ValidationResultRepository} provided in the constructor
     */
    public void execute() {
        logger.info("Validating rule 'E029 - Missing field `agency_id` for file agency.txt with more than 1 record'"
                + System.lineSeparator());
        final Collection<Agency> agencyPerId = dataRepo.getAgencyAll();
        if (agencyPerId.size() > 1) {
            agencyPerId.stream()
                    .filter(agency -> agency.getAgencyId() == null)
                    .forEach(invalidAgency -> resultRepo
                            .addNotice(new MissingAgencyIdNotice(invalidAgency.getAgencyName())));
        }
    }
}
