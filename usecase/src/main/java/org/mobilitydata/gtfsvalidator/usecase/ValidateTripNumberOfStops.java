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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnusableTripNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

/**
 * Use case to validate rule E051: {@code Trip} must have more than one stop to be usable
 */
public class ValidateTripNumberOfStops {
    private final GtfsDataRepository dataRepository;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    public ValidateTripNumberOfStops(final GtfsDataRepository dataRepository,
                                     final ValidationResultRepository resultRepo,
                                     final Logger logger) {
        this.resultRepo = resultRepo;
        this.dataRepository = dataRepository;
        this.logger = logger;
    }

    /**
     * Use case to validate that each {@link Trip} has more than one stop. Each time this condition is not met, a
     * {@code UnusableTripNotice} is generated and added to the {@code ValidationResultRepository} provided in the
     * constructor.
     */
    public void execute() {
        logger.info("Validating rule 'E051 - Trips must have more than one stop to be usable.");
        dataRepository.getTripAll().forEach((tripId, trip) -> {
            if (dataRepository.getStopTimeByTripId(tripId).size() <= 1) {
                resultRepo.addNotice(new UnusableTripNotice(tripId));
            }
        });
    }
}
