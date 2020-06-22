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

import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Calendar;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * This use case turns a parsed entity representing a row from calendar.txt into a concrete class
 */
public class ProcessParsedCalendar {
    private final ValidationResultRepository resultRepository;
    private final GtfsDataRepository gtfsDataRepository;
    private final Calendar.CalendarBuilder builder;

    public ProcessParsedCalendar(final ValidationResultRepository resultRepository,
                                 final GtfsDataRepository gtfsDataRepository,
                                 final Calendar.CalendarBuilder builder) {
        this.resultRepository = resultRepository;
        this.gtfsDataRepository = gtfsDataRepository;
        this.builder = builder;
    }

    /**
     * Use case execution method to go from a row from calendar.txt to an internal representation.
     * <p>
     * This use case extracts values from a {@code ParsedEntity} and creates a {@code Calendar} object if the
     * requirements from the official GTFS specification are met. When these requirements are not met, related notices
     * generated in {@code Calendar.CalendarBuilder} are added to the result repository provided in the constructor.
     * This use case also adds a {@code DuplicatedEntityNotice} to said repository if the uniqueness constraint on
     * calendar entities is not respected.
     *
     * @param validatedParsedCalendar entity to be processed and added to the GTFS data repository
     */
    public void execute(final ParsedEntity validatedParsedCalendar) {
        final String serviceId = (String) validatedParsedCalendar.get("service_id");
        final Integer monday = (Integer) validatedParsedCalendar.get("monday");
        final Integer tuesday = (Integer) validatedParsedCalendar.get("tuesday");
        final Integer wednesday = (Integer) validatedParsedCalendar.get("wednesday");
        final Integer thursday = (Integer) validatedParsedCalendar.get("thursday");
        final Integer friday = (Integer) validatedParsedCalendar.get("friday");
        final Integer saturday = (Integer) validatedParsedCalendar.get("saturday");
        final Integer sunday = (Integer) validatedParsedCalendar.get("sunday");
        final LocalDate startDate = (LocalDate) validatedParsedCalendar.get("start_date");
        final LocalDate endDate = (LocalDate) validatedParsedCalendar.get("end_date");

        builder.clearFieldAll()
                .serviceId(serviceId)
                .monday(monday)
                .tuesday(tuesday)
                .wednesday(wednesday)
                .thursday(thursday)
                .friday(friday)
                .saturday(saturday)
                .sunday(sunday)
                .startDate(startDate)
                .endDate(endDate);

        final EntityBuildResult<?> calendar = builder.build();

        if (calendar.isSuccess()) {
            if (gtfsDataRepository.addCalendar((Calendar) calendar.getData()) == null) {
                resultRepository.addNotice(new DuplicatedEntityNotice("calendar.txt", "service_id",
                        validatedParsedCalendar.getEntityId()));
            }
        } else {
            // at thi step it is certain that calling getData method will return a list of notices, therefore there is
            // no need for cast check
            //noinspection unchecked
            ((List<Notice>) calendar.getData()).forEach(resultRepository::addNotice);
        }
    }
}