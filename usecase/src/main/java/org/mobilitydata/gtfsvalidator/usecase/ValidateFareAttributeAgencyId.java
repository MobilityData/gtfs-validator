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

public class ValidateFareAttributeAgencyId {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    public ValidateFareAttributeAgencyId(final GtfsDataRepository dataRepo,
                                         final ValidationResultRepository resultRepo,
                                         final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    public void execute() {
        logger.info("Validating rule 'E035 - `agency_id` not found");

        final int agencyCount = dataRepo.getAgencyCount();
        dataRepo.getFareAttributeAll().values()
                .forEach(fareAttribute -> {
                    final String fareId = fareAttribute.getFareId();
                    final String agencyId = fareAttribute.getAgencyId();
                    if (agencyCount > 1) {
                        if (agencyId == null) {
                            resultRepo.addNotice(new MissingAgencyIdNotice("fare_attributes.txt", fareId));
                        } else {
                            if (dataRepo.getAgencyById(agencyId) == null) {
                                resultRepo.addNotice(new AgencyIdNotFoundNotice("fare_attributes.txt", "agency_id",
                                        fareId));
                            }
                        }
                    } else {
                        if (dataRepo.getAgencyById(agencyId) == null) {
                            resultRepo.addNotice(new AgencyIdNotFoundNotice("fare_attributes.txt",
                                    "agency_id", fareId));
                        }
                    }
                });
    }
}
