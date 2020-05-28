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
import org.mobilitydata.gtfsvalidator.domain.entity.FileSpecificUsecase;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.RouteShortNameTooLongNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Collection;

/**
 * Use case to validate that a Route short name is not longer than 12 characters.
 */
public class ValidateRouteShortNameLength implements FileSpecificUsecase {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     * @param logger
     */
    public ValidateRouteShortNameLength(final GtfsDataRepository dataRepo,
                                        final ValidationResultRepository resultRepo,
                                        final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method: checks if Route short name is not longer than 12 characters
     * for every Routes in a {@link GtfsDataRepository}. A new notice is generated each time this condition is true.
     * This notice is then added to the {@link ValidationResultRepository} provided in the constructor.
     *
     * @return a list of notices generated each time a Route short name is longer than 12 characters.
     */
    @Override
    public void execute() {
        logger.info("Validating rule E024 - Route short name too long");
        Collection<Route> routes = dataRepo.getRouteAll();
        routes.stream()
                .filter(route -> !(isValidRouteShortName(route.getRouteShortName())))
                .forEach(route -> resultRepo.addNotice(new RouteShortNameTooLongNotice("route.txt",
                        route.getRouteId(), String.valueOf(route.getRouteShortName().length()))));
    }

    /**
     * @param routeShortName the short name of a Route
     * @return true if Route short name is null and less or equal than 12 characters, false if not.
     */
    private boolean isValidRouteShortName(final String routeShortName) {
        return routeShortName == null || routeShortName.length() <= 12;
    }
}