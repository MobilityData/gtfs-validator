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

public class ValidateRouteAgencyId {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    public ValidateRouteAgencyId(final GtfsDataRepository dataRepo,
                                 final ValidationResultRepository resultRepo,
                                 final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    public void execute() {
        logger.info("Validating rule 'E035 - `agency_id` not found" + System.lineSeparator());

        final int agencyCount = dataRepo.getAgencyCount();
        dataRepo.getRouteAll().values()
                .forEach(route -> {
                    final String routeId = route.getRouteId();
                    final String agencyId = route.getAgencyId();
                    if (agencyCount > 1) {
                        if (agencyId == null) {
                            resultRepo.addNotice(new MissingAgencyIdNotice("routes.txt", routeId));
                        } else {
                            if (dataRepo.getAgencyById(agencyId) == null) {
                                resultRepo.addNotice(new AgencyIdNotFoundNotice("routes.txt", "agency_id",
                                        routeId));
                            }
                        }
            } else {
                if (agencyId != null && dataRepo.getAgencyById(agencyId) == null) {
                    resultRepo.addNotice(new AgencyIdNotFoundNotice("routes.txt", "agency_id", routeId));
                }
            }
        });
    }
}