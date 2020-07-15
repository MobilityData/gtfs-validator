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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.CalendarEndDateBeforeStartDateNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

/**
 * Use case for E032 to validate that a calendar.txt end_date must not be earlier than the start_date.
 */
public class ValidateCalendarEndDateBeforeStartDate {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     */
    public ValidateCalendarEndDateBeforeStartDate(final GtfsDataRepository dataRepo,
                                                  final ValidationResultRepository resultRepo,
                                                  final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method: Checks if `end_date` of a service record is earlier than the `start_date` for every
     * Calendar entry in a {@link GtfsDataRepository}. A new notice is generated each time this condition is true.
     * This notice is then added to the {@link ValidationResultRepository} provided in the constructor.
     */
    public void execute() {
        logger.info("Validating rule 'E032 - calendar.txt end_date is before start_date'" + System.lineSeparator());

        dataRepo.getCalendarAll().values().stream()
                .filter(calendar -> calendar.getEndDate().isBefore(calendar.getStartDate()))
                .forEach(calendar -> resultRepo.addNotice(
                        new CalendarEndDateBeforeStartDateNotice(
                                "calendar.txt",
                                calendar.getServiceId(),
                                calendar.getStartDate(),
                                calendar.getEndDate())));
    }
}
