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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.RouteIdNotFoundNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.HashSet;
import java.util.Set;

 /**
 * Use case for E033 to validate that all records of `trips.txt` refer to an existing {@code Route} from file
  * `routes.txt`
 */
public class ValidateTripRouteId {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     * @param logger     a logger to log information about the validation process
     */
    public ValidateTripRouteId(final GtfsDataRepository dataRepo,
                               final ValidationResultRepository resultRepo,
                               final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

     /**
      * Use case execution method: Checks if `route_id` of a trip refers to a record from file `routes.txt`. A new
      * notice is generated each time this condition is false.
      * This notice is then added to the {@link ValidationResultRepository} provided in the constructor.
      */
    public void execute() {
        logger.info("Validating rule E033 - `route_id` not found" + System.lineSeparator());
        final Set<String> routeIdCollection = new HashSet<>();
        dataRepo.getRouteAll().forEach(route -> routeIdCollection.add(route.getRouteId()));
        dataRepo.getTripAll()
                .forEach(trip -> {
                    if (!routeIdCollection.contains(trip.getRouteId())) {
                        resultRepo.addNotice(
                                new RouteIdNotFoundNotice("trips.txt",
                                        trip.getTripId(),
                                        trip.getRouteId(),
                                        "route_id")
                        );
                    }
                });
    }
}
