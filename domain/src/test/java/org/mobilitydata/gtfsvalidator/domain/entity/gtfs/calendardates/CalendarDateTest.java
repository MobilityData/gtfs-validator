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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalendarDateTest {

    @Test
    void createCalendarDateWithValidValueShouldNotThrowException() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        underTest.serviceId("service_id")
                .date(LocalDateTime.now())
                .exceptionType(1);
    }

    // Field serviceId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    void createCalendarDateWithNullServiceIdShouldThrowException() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        //noinspection ConstantConditions
        underTest.serviceId(null)
                .date(LocalDateTime.now())
                .exceptionType(1);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                underTest::build);

        assertEquals("field service_id in calendar_dates.txt can not be null", exception.getMessage());
    }

    // Field date is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    void createCalendarDateWithNullDateShouldThrowException() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        //noinspection ConstantConditions
        underTest.serviceId("test")
                .date(null)
                .exceptionType(1);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                underTest::build);

        assertEquals("field date in calendar_dates.txt can not be null", exception.getMessage());
    }

    // Field exceptionType is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    void createCalendarDateWithNullExceptionTypeShouldThrowException() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        //noinspection ConstantConditions
        underTest.serviceId("test")
                .date(LocalDateTime.now())
                .exceptionType(null);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                underTest::build);

        assertEquals("unexpected value found for field exception_type of calendar_dates.txt",
                exception.getMessage());
    }

    @Test
    void createCalendarDateWithInvalidExceptionTypeShouldThrowException() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        underTest.serviceId("test")
                .date(LocalDateTime.now())
                .exceptionType(5);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                underTest::build);

        assertEquals("unexpected value found for field exception_type of calendar_dates.txt",
                exception.getMessage());
    }
}