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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Calendar;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.CalendarDate;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.ServiceIdNotFoundNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Map;

/**
 * Use case for E036 to validate that all records of `trips.txt` refer to an existing {@code Calendar} or
 * {@code CalendarDate} from files `calendar.txt` or `calendar_dates.txt
 */
public class ValidateTripServiceId {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    /**
     * @param gtfsDataRepo a repository storing the data of a GTFS dataset
     * @param resultRepo   a repository storing information about the validation process
     * @param logger       a logger to log information about the validation process
     */
    public ValidateTripServiceId(final GtfsDataRepository gtfsDataRepo,
                                 final ValidationResultRepository resultRepo,
                                 final Logger logger) {
        this.dataRepo = gtfsDataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method: Checks if `service_id` of a trip refers to a record from files `calendar.txt` or
     * `calendar_dates.txt`. A new notice is generated each time this condition is false.
     * This notice is then added to the {@link ValidationResultRepository} provided in the constructor.
     */
    public void execute() {
        logger.info("Validating rule E036 - `service_id` not found");

        // calendar entities are mapped on service_id
        final Map<String, Calendar> calendarCollection = dataRepo.getCalendarAll();
        // CalendarDate entities are mapped on service_id and date in a nested map
        final Map<String, Map<String, CalendarDate>> calendarDateCollection = dataRepo.getCalendarDateAll();
        dataRepo.getTripAll().values().stream()
                .filter(trip -> !calendarCollection.containsKey(trip.getServiceId()) &&
                        !calendarDateCollection.containsKey(trip.getServiceId()))
                .forEach(trip -> resultRepo.addNotice(
                        new ServiceIdNotFoundNotice("trips.txt",
                                trip.getTripId(),
                                "service_id",
                                trip.getServiceId())
                ));
    }
}
