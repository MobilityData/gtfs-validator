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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.DuplicateRouteShortNameNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.HashMap;
import java.util.Map;

public class ValidateRouteShortNameAreUnique {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    public ValidateRouteShortNameAreUnique(final GtfsDataRepository dataRepo,
                                           final ValidationResultRepository resultRepo,
                                           final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    public void execute() {
        logger.info("Validating rule 'W015 - Duplicate `routes.route_short_name`'");

        final Map<String, Route> routeByRouteShortName = new HashMap<>();
        dataRepo.getRouteAll().forEach((routeId, route) -> {
            final String routeShortName = route.getRouteShortName();
            if (routeShortName != null) {
                if (routeByRouteShortName.containsKey(routeShortName)) {
                    final String routeAgencyId = route.getAgencyId();
                    final String otherRouteAgencyId = routeByRouteShortName.get(routeShortName).getAgencyId();
                    final boolean routesAreFromSameAgency =
                            dataRepo.getAgencyCount() == 1 ||
                                    (routeAgencyId != null && routeAgencyId.equals(otherRouteAgencyId));
                    if (routesAreFromSameAgency) {
                        resultRepo.addNotice(
                                new DuplicateRouteShortNameNotice(
                                        routeByRouteShortName.get(routeShortName).getRouteId(),
                                        routeId,
                                        routeShortName)
                        );
                    }
                } else {
                    routeByRouteShortName.put(routeShortName, route);
                }
            }
        });
    }
}
