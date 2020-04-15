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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.CalendarDate;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.EntityMustBeUniqueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.UnexpectedValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;

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

    public void execute(final ParsedEntity validatedParsedRoute) throws IllegalArgumentException,
            SQLIntegrityConstraintViolationException {

        final String serviceId = (String) validatedParsedRoute.get("service_id");
        final LocalDateTime date = (LocalDateTime) validatedParsedRoute.get("date");
        final Integer exceptionType = (Integer) validatedParsedRoute.get("exception_type");

        try {
            builder.serviceId(serviceId)
                    .date(date)
                    .exceptionType(exceptionType);

            gtfsDataRepository.addCalendarDate(builder.build());

        } catch (IllegalArgumentException e) {

            if (serviceId == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("calendar_dates.txt",
                        "service_id", validatedParsedRoute.getEntityId()));
            }
            if (date == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("calendar_dates.txt",
                        "date", validatedParsedRoute.getEntityId()));
            }
            if (exceptionType == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("calendar_dates.txt",
                        "exception_type", validatedParsedRoute.getEntityId()));
            } else if (exceptionType < 1 || exceptionType > 2) {
                resultRepository.addNotice(new UnexpectedValueNotice("calendar_dates.txt",
                        "exception_type", validatedParsedRoute.getEntityId(), exceptionType));
            }
            throw e;
        } catch (SQLIntegrityConstraintViolationException e) {
            resultRepository.addNotice(new EntityMustBeUniqueNotice("calendar_dates.txt", "service_id",
                    validatedParsedRoute.getEntityId()));
            throw e;
        }
    }
}
