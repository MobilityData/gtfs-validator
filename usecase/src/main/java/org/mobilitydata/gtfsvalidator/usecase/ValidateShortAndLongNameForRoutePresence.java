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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.MissingRouteLongNameNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.MissingRouteShortNameNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

/**
 * Use case to validate that Route short name and long name are present.
 */
public class ValidateShortAndLongNameForRoutePresence {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     */
    public ValidateShortAndLongNameForRoutePresence(final GtfsDataRepository dataRepo,
                                                    final ValidationResultRepository resultRepo,
                                                    final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method: checks if Route short name and long name are missing
     * for every Routes in a {@link GtfsDataRepository}. If one of the fields is missing, a warning error notice is
     * generated. This notice is then added to the {@link ValidationResultRepository} provided in the constructor.
     * Note that no record from `routes.txt` can have both fields null or blank.
     */
    public void execute() {
        logger.info("Validating rule 'E027 - Missing route short name and long name'");
        dataRepo.getRouteAll().values().stream()
                .filter(route -> !(isPresentName(route.getRouteLongName()) && isPresentName(route.getRouteShortName())))
                .forEach(route -> {
                    if (!isPresentName(route.getRouteLongName())) {
                        resultRepo.addNotice(new MissingRouteLongNameNotice("routes.txt", route.getRouteId()));
                    } else {
                        resultRepo.addNotice(new MissingRouteShortNameNotice("routes.txt", route.getRouteId()));
                    }
                });
    }

    /**
     * @param routeName a name of a Route
     * @return true if this Route name is not missing or empty, false if not.
     */
    private boolean isPresentName(final String routeName) {
        return routeName != null && !routeName.isBlank();
    }
}
