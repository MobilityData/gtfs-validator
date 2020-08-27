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

/**
 * Use case to validate that that each non-null value of `route_short_name` from {@link Route} entities are unique.
 * This use case is triggered after completing the {@code GtfsDataRepository} provided in the constructor with
 * {@code Route} entities.
 */
public class ValidateRouteShortNameAreUnique {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     * @param logger     a logger used to log information about the validation process
     */
    public ValidateRouteShortNameAreUnique(final GtfsDataRepository dataRepo,
                                           final ValidationResultRepository resultRepo,
                                           final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method: checks unicity of `route_short_name` in file `routes.txt`. A notice is
     * generated and added to the {@code ValidationResultRepository} provided in the constructor each time this
     * requirement is not satisfied.
     * Note that two {@link Route} can have the same `route_short_name` if they do not belong to the same agency
     */
    public void execute() {
        logger.info("Validating rule 'W015 - Duplicate `routes.route_short_name`'");

        final Map<String, Route> routeByRouteShortName = new HashMap<>();
        dataRepo.getRouteAll().forEach((routeId, route) -> {
            final String routeShortName = route.getRouteShortName();
            if (routeShortName != null) {
                if (routeByRouteShortName.containsKey(routeShortName)) {
                    if (areRouteFromSameAgency(
                            dataRepo,
                            route.getAgencyId(),
                            routeByRouteShortName.get(routeShortName).getAgencyId())) {
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

    /**
     * Utility method to determine if two routes are from the same agency
     *
     * @param dataRepo           a repository storing the data of a GTFS dataset
     * @param routeAgencyId      first agency_id
     * @param otherRouteAgencyId second agency_id
     * @return true if both agency ids are equals or the GTFS data repository contains only one agency, returns false
     * otherwise.
     */
    private boolean areRouteFromSameAgency(final GtfsDataRepository dataRepo,
                                           final String routeAgencyId,
                                           final String otherRouteAgencyId) {
        return dataRepo.getAgencyCount() == 1 ||
                (routeAgencyId != null && routeAgencyId.equals(otherRouteAgencyId));
    }
}
