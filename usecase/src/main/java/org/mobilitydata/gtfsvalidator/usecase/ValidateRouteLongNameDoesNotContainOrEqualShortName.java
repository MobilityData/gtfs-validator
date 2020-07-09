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

import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.RouteLongNameEqualsShortNameNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.RouteLongNameContainsShortNameNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Collection;

/**
 * Use case to validate that a Route long name does not equal or contain the short name.
 */
public class ValidateRouteLongNameDoesNotContainOrEqualShortName {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     */
    public ValidateRouteLongNameDoesNotContainOrEqualShortName(final GtfsDataRepository dataRepo,
                                                               final ValidationResultRepository resultRepo,
                                                               final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method: checks if a Route long name does equal or contain the short name
     * for every Routes in a {@link GtfsDataRepository}. If both are equals, a new error notice is generated.
     * If long name contains short name, a warning error notice is generated.
     * This notice is then added to the {@link ValidationResultRepository} provided in the constructor.
     */
    public void execute() {
        logger.info("Validating rule 'E028 - Route long name equals short name'" + System.lineSeparator());
        Collection<Route> routes = dataRepo.getRouteAll();
        routes.stream()
                .filter(route -> route.getRouteLongName() != null && route.getRouteShortName() != null &&
                        route.getRouteLongName().contains(route.getRouteShortName()))
                .forEach(route -> {
                    if (route.getRouteLongName().equals(route.getRouteShortName())) {
                        resultRepo.addNotice(new RouteLongNameEqualsShortNameNotice("routes.txt", route.getRouteId()));
                    } else {
                        resultRepo.addNotice(new RouteLongNameContainsShortNameNotice("routes.txt", route.getRouteId()));
                    }
                });
    }
}
