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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.CalendarDate;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * This use case turns a parsed entity representing a row from calendar_dates.txt into a concrete class
 */
public class ProcessParsedCalendarDate {
    private final ValidationResultRepository resultRepository;
    private final GtfsDataRepository gtfsDataRepository;
    private final CalendarDate.CalendarDateBuilder builder;

    public ProcessParsedCalendarDate(final ValidationResultRepository resultRepository,
                                     final GtfsDataRepository gtfsDataRepository,
                                     final CalendarDate.CalendarDateBuilder builder) {
        this.resultRepository = resultRepository;
        this.gtfsDataRepository = gtfsDataRepository;
        this.builder = builder;
    }

    /**
     * Use case execution method to go from a row from calendar_dates.txt to an internal representation.
     * <p>
     * This use case extracts values from a {@code ParsedEntity} and creates a {@code CalendarDate} object if the
     * requirements from the official GTFS specification are met. When these requirements are not met, related notices
     * generated in {@code CalendarDate.CalendarDateBuilder} are added to the result repository provided in the
     * constructor. This use case also adds a {@code DuplicatedEntityNotice} to said repository if the uniqueness
     * constraint on CalendarDate entities is not respected.
     *
     * @param validatedParsedRoute entity to be processed and added to the GTFS data repository
     */
    public void execute(final ParsedEntity validatedParsedRoute) {
        final String serviceId = (String) validatedParsedRoute.get("service_id");
        final LocalDate date = (LocalDate) validatedParsedRoute.get("date");
        final Integer exceptionType = (Integer) validatedParsedRoute.get("exception_type");

        builder.clearFieldAll()
                .serviceId(serviceId)
                .date(date)
                .exceptionType(exceptionType);

        final EntityBuildResult<?> calendarDate = builder.build();

        if (calendarDate.isSuccess()) {
            if (gtfsDataRepository.addCalendarDate((CalendarDate) calendarDate.getData()) == null) {
                resultRepository.addNotice(new DuplicatedEntityNotice("calendar_dates.txt",
                        "service_id, date", validatedParsedRoute.getEntityId()));
            }
        } else {
            // at this step it is certain that calling getData method will return a list of notices, therefore there is
            // no need for cast check
            //noinspection unchecked
            ((List<Notice>) calendarDate.getData()).forEach(resultRepository::addNotice);
        }
    }
}