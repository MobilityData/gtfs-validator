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

import org.mobilitydata.gtfsvalidator.domain.entity.Calendar;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.EntityMustBeUniqueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;

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

    public void execute(final ParsedEntity validatedParsedCalendar) throws IllegalArgumentException,
            SQLIntegrityConstraintViolationException {

        String serviceId = (String) validatedParsedCalendar.get("service_id");
        Integer monday = (Integer) validatedParsedCalendar.get("monday");
        Integer tuesday = (Integer) validatedParsedCalendar.get("tuesday");
        Integer wednesday = (Integer) validatedParsedCalendar.get("wednesday");
        Integer thursday = (Integer) validatedParsedCalendar.get("thursday");
        Integer friday = (Integer) validatedParsedCalendar.get("friday");
        Integer saturday = (Integer) validatedParsedCalendar.get("saturday");
        Integer sunday = (Integer) validatedParsedCalendar.get("sunday");
        LocalDateTime startDate = (LocalDateTime) validatedParsedCalendar.get("start_date");
        LocalDateTime endDate = (LocalDateTime) validatedParsedCalendar.get("end_date");

        try {
            builder.serviceId(serviceId)
                    .monday(monday)
                    .tuesday(tuesday)
                    .wednesday(wednesday)
                    .thursday(thursday)
                    .friday(friday)
                    .saturday(saturday)
                    .sunday(sunday)
                    .startDate(startDate)
                    .endDate(endDate);

            gtfsDataRepository.addCalendar(builder.build());

        } catch (IllegalArgumentException e) {

            if (monday == null) {
                resultRepository.addNotice(
                        new MissingRequiredValueNotice("calendar.txt", "monday",
                                validatedParsedCalendar.getEntityId()));
            } else if (monday < 0 || monday > 1) {
                resultRepository.addNotice(
                        new IntegerFieldValueOutOfRangeNotice("calendar.txt", "monday",
                                validatedParsedCalendar.getEntityId(), 0, 1, monday));
            }
            if (tuesday == null) {
                resultRepository.addNotice(
                        new MissingRequiredValueNotice("calendar.txt", "tuesday",
                                validatedParsedCalendar.getEntityId()));
            } else if (tuesday < 0 || tuesday > 1) {
                resultRepository.addNotice(
                        new IntegerFieldValueOutOfRangeNotice("calendar.txt", "tuesday",
                                validatedParsedCalendar.getEntityId(), 0, 1, tuesday));
            }
            if (wednesday == null) {
                resultRepository.addNotice(
                        new MissingRequiredValueNotice("calendar.txt", "wednesday",
                                validatedParsedCalendar.getEntityId()));
            } else if (wednesday < 0 || wednesday > 1) {
                resultRepository.addNotice(
                        new IntegerFieldValueOutOfRangeNotice("calendar.txt", "wednesday",
                                validatedParsedCalendar.getEntityId(), 0, 1, wednesday));
            }
            if (thursday == null) {
                resultRepository.addNotice(
                        new MissingRequiredValueNotice("calendar.txt", "thursday",
                                validatedParsedCalendar.getEntityId()));
            } else if (thursday < 0 || thursday > 1) {
                resultRepository.addNotice(
                        new IntegerFieldValueOutOfRangeNotice("calendar.txt", "thursday",
                                validatedParsedCalendar.getEntityId(), 0, 1, thursday));
            }
            if (friday == null) {
                resultRepository.addNotice(
                        new MissingRequiredValueNotice("calendar.txt", "friday",
                                validatedParsedCalendar.getEntityId()));
            } else if (friday < 0 || friday > 1) {
                resultRepository.addNotice(
                        new IntegerFieldValueOutOfRangeNotice("calendar.txt", "friday",
                                validatedParsedCalendar.getEntityId(), 0, 1, friday));
            }
            if (saturday == null) {
                resultRepository.addNotice(
                        new MissingRequiredValueNotice("calendar.txt", "saturday",
                                validatedParsedCalendar.getEntityId()));
            } else if (saturday < 0 || saturday > 1) {
                resultRepository.addNotice(
                        new IntegerFieldValueOutOfRangeNotice("calendar.txt", "saturday",
                                validatedParsedCalendar.getEntityId(), 0, 1, saturday));
            }
            if (sunday == null) {
                resultRepository.addNotice(
                        new MissingRequiredValueNotice("calendar.txt", "sunday",
                                validatedParsedCalendar.getEntityId()));
            } else if (sunday < 0 || sunday > 1) {
                resultRepository.addNotice(
                        new IntegerFieldValueOutOfRangeNotice("calendar.txt", "sunday",
                                validatedParsedCalendar.getEntityId(), 0, 1, sunday));
            }
            if (serviceId == null) {
                resultRepository.addNotice(
                        new MissingRequiredValueNotice("calendar.txt", "service_id",
                                validatedParsedCalendar.getEntityId()));
            }
            if (startDate == null) {
                resultRepository.addNotice(
                        new MissingRequiredValueNotice("calendar.txt", "start_date",
                                validatedParsedCalendar.getEntityId()));
            }
            if (endDate == null) {
                resultRepository.addNotice(
                        new MissingRequiredValueNotice("calendar.txt", "end_date",
                                validatedParsedCalendar.getEntityId()));
            }
            throw e;
        } catch (SQLIntegrityConstraintViolationException e) {
            resultRepository.addNotice(new EntityMustBeUniqueNotice("calendar.txt", "service_id",
                    validatedParsedCalendar.getEntityId()));
            throw e;
        }
    }
}