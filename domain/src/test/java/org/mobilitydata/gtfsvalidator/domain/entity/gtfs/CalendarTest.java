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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.Calendar;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalendarTest {

    @Test
    public void createCalendarWithNullServiceIdShouldThrowException() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        //noinspection ConstantConditions
        underTest.serviceId(null)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("field service_id can not be null", exception.getMessage());
    }

    @Test
    public void createCalendarWithNullStartDAteShouldThrowException() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        //noinspection ConstantConditions
        underTest.serviceId("test id")
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(null)
                .endDate(LocalDateTime.now());

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("field start_date can not be null", exception.getMessage());
    }

    @Test
    public void createCalendarWithNullEndDateShouldThrowException() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        //noinspection ConstantConditions
        underTest.serviceId("test id")
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(null);

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("field end_date can not be null", exception.getMessage());
    }

    @Test
    public void createCalendarWithInvalidMondayValueShouldThrowException() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        underTest.serviceId("test id")
                .monday(3)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value found for field monday", exception.getMessage());
    }

    @Test
    public void createCalendarWithInvalidTuesdayValueShouldThrowException() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        underTest.serviceId("test id")
                .monday(0)
                .tuesday(2)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value found for field tuesday", exception.getMessage());
    }

    @Test
    public void createCalendarWithInvalidWednesdayValueShouldThrowException() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        underTest.serviceId("test id")
                .monday(0)
                .tuesday(0)
                .wednesday(3)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value found for field wednesday", exception.getMessage());
    }

    @Test
    public void createCalendarWithInvalidThursdayValueShouldThrowException() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        underTest.serviceId("test id")
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(2)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value found for field thursday", exception.getMessage());
    }

    @Test
    public void createCalendarWithInvalidFridayValueShouldThrowException() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        underTest.serviceId("test id")
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(2)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value found for field friday", exception.getMessage());
    }

    @Test
    public void createCalendarWithInvalidSaturdayValueShouldThrowException() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        underTest.serviceId("test id")
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(2)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value found for field saturday", exception.getMessage());
    }

    @Test
    public void createCalendarWithInvalidSundayValueShouldThrowException() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        underTest.serviceId("test id")
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(2)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value found for field sunday", exception.getMessage());
    }

    @Test
    public void createCalendarWithValidValuesShouldNotThrowException() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        underTest.serviceId("test id")
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();
    }
}