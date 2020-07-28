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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.InconsistentAgencyTimezoneNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.HashSet;
import java.util.Set;

/**
 * Use case to validate that all rows of file agency.txt have an identical value for field agency_timezone
 * This use case is triggered after defining the content of the {@code GtfsDataRepository} provided in the constructor.
 */
public class ValidateAgenciesHaveSameAgencyTimezone {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     * @param logger     a logger displaying information about the validation process
     */
    public ValidateAgenciesHaveSameAgencyTimezone(final GtfsDataRepository dataRepo,
                                                  final ValidationResultRepository resultRepo,
                                                  final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method: checks if every record of file agency.txt have the same value for field agency_timezone
     * If a row from agency.txt has a different value for said field an error notice is generated and added to the
     * {@code ValidationResultRepository} provided in the constructor
     */
    public void execute() {
        logger.info("Validating rule 'E030 - Different 'agency_timezone'");
        final Set<String> timezoneCollection = new HashSet<>();
        dataRepo.getAgencyAll()
                .forEach((agencyId, agency) -> timezoneCollection.add(agency.getAgencyTimezone()));
        if (timezoneCollection.size() > 1) {
            resultRepo.addNotice(
                    new InconsistentAgencyTimezoneNotice(timezoneCollection.size(), timezoneCollection.toString())
            );
        }
    }
}
