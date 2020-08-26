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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.DuplicateRouteLongNameRouteShortNameCombinationNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * Use case to validate that combination of fields `route_long_name` and `route_short_name` from {@link Route} entities
 * are unique. This use case is triggered after completing the {@code GtfsDataRepository} provided in the constructor
 * with {@code Route} entities.
 */
public class ValidateUniqueRouteLongNameRouteShortNameCombination {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     * @param logger     a logger used to log information about the validation process
     */
    public ValidateUniqueRouteLongNameRouteShortNameCombination(final GtfsDataRepository dataRepo,
                                                                final ValidationResultRepository resultRepo,
                                                                final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method: checks unicity of combination of fields `route_long_name` and `route_short_name`
     * in file `routes.txt`. A notice is generated and added to the {@code ValidationResultRepository} provided in the
     * constructor each time this requirement is not satisfied.
     */
    public void execute() {
        logger.info("Validating rule 'W016 - Duplicate combination od fields`route_short_name` and `route_long_name`'");

        final Map<String, Route> routeByRouteLongNameRouteShortName = new HashMap<>();
        dataRepo.getRouteAll().forEach((routeId, route) -> {
            final String routeShortName = route.getRouteShortName();
            final String routeLongName = route.getRouteLongName();
            if (routeShortName != null && routeLongName != null) {
                if (routeByRouteLongNameRouteShortName.containsKey(routeLongName + routeShortName)) {
                    resultRepo.addNotice(
                            new DuplicateRouteLongNameRouteShortNameCombinationNotice(
                                    routeByRouteLongNameRouteShortName.get(routeLongName + routeShortName).getRouteId(),
                                    routeId,
                                    routeLongName,
                                    routeShortName)
                    );
                } else {
                    routeByRouteLongNameRouteShortName.put(routeLongName + routeShortName, route);
                }
            }
        });
    }
}
