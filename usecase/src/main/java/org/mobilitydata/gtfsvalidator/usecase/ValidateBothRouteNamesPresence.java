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

import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingBothRouteNamesNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.MissingRouteLongNameNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.MissingRouteShortNameNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Collection;

/**
 * Use case to validate that Route short name and long name are present.
 */
public class ValidateBothRouteNamesPresence {

    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     */
    public ValidateBothRouteNamesPresence(final GtfsDataRepository dataRepo,
                                          final ValidationResultRepository resultRepo) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
    }

    /**
     * Use case execution method: checks if Route short name and long name are missing
     * for every Routes in a {@link GtfsDataRepository}. If both are missing, a new error notice is generated.
     * If only one is missing, a warning error notice is generated. This notice is then added
     * to the {@link ValidationResultRepository} provided in the constructor.
     */
    public void execute() {
        Collection<Route> routes = dataRepo.getRouteAll();
        routes.stream()
                .filter(route -> !(isPresentLongName(route.getRouteLongName()) && isPresentShortName(route.getRouteShortName())))
                .forEach(route -> {
                    if (!isPresentLongName(route.getRouteLongName()) && !isPresentShortName(route.getRouteShortName())) {
                        resultRepo.addNotice(new MissingBothRouteNamesNotice("route.txt", route.getRouteId()));
                    } else if (!isPresentLongName(route.getRouteLongName())) {
                        resultRepo.addNotice(new MissingRouteLongNameNotice("route.txt", route.getRouteId()));
                    } else {
                        resultRepo.addNotice(new MissingRouteShortNameNotice("route.txt", route.getRouteId()));
                    }
                });
    }

    /**
     * @param routeShortName the short name of a Route
     * @return true if Route short name is not missing or empty, false if not.
     */
    private boolean isPresentShortName(final String routeShortName) {
        return routeShortName != null && !routeShortName.replaceAll("\\s+", "").isEmpty();
    }

    /**
     * @param routeLongName the long name of a Route
     * @return true if Route long name is not missing or empty, false if not.
     */
    private boolean isPresentLongName(final String routeLongName) {
        return routeLongName != null && !routeLongName.replaceAll("\\s+", "").isEmpty();
    }
}