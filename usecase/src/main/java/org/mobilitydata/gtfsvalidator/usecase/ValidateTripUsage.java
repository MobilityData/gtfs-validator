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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.TripNotUsedNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Map;
import java.util.TreeMap;

/**
 * Use case to validate rule E050: All trips defined by `trips.txt` must be referred to at least once in `stop_times.txt`
 */
public class ValidateTripUsage {
    private final ValidationResultRepository resultRepo;
    private final GtfsDataRepository dataRepository;
    private final Logger logger;

    public ValidateTripUsage(final GtfsDataRepository dataRepository,
                             final ValidationResultRepository resultRepo,
                             final Logger logger) {
        this.resultRepo = resultRepo;
        this.dataRepository = dataRepository;
        this.logger = logger;
    }

    /**
     * Use case execution method: checks if every trip is referred to at least once in `stop_times.txt`
     */
    public void execute() {
        logger.info("Validating rule 'E050 - Trips must be used in `stop_times.txt`.'");
        final Map<String, TreeMap<Integer, StopTime>> stopTimeCollection = dataRepository.getStopTimeAll();
        dataRepository.getTripAll().forEach((tripId, trip) -> {
            if (!stopTimeCollection.containsKey(tripId)) {
                resultRepo.addNotice(new TripNotUsedNotice(tripId));
            }
        });
    }
}
