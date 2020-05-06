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

package org.mobilitydata.gtfsvalidator.db;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Agency;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.CalendarDate;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.ExceptionType;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InMemoryGtfsDataRepositoryTest {

    @Test
    void callToAddAgencyShouldAddAgencyToRepoAndReturnSameEntity() {
        final Agency mockAgency = mock(Agency.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockAgency.getAgencyId()).thenReturn("agency id");

        assertEquals(underTest.addAgency(mockAgency), mockAgency);
    }

    @Test
    void addSameAgencyTwiceShouldReturnNull() {
        final Agency mockAgency = mock(Agency.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockAgency.getAgencyId()).thenReturn("agency id");

        underTest.addAgency(mockAgency);

        assertNull(underTest.addAgency(mockAgency));
    }

    @Test
    void addNullAgencyShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        //noinspection ConstantConditions
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addAgency(null));
        assertEquals("Cannot add null agency to data repository", exception.getMessage());
    }

    @Test
    void getAgencyByIdShouldReturnRelatedAgency() {
        final Agency mockAgency00 = mock(Agency.class);
        final Agency mockAgency01 = mock(Agency.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockAgency00.getAgencyId()).thenReturn("agency id0");
        when(mockAgency01.getAgencyId()).thenReturn("agency id1");

        underTest.addAgency(mockAgency00);
        underTest.addAgency(mockAgency01);

        assertEquals(mockAgency00, underTest.getAgencyById("agency id0"));
        assertEquals(mockAgency01, underTest.getAgencyById("agency id1"));
    }

    @Test
    void callToAddRouteShouldAddRouteToRepoAndReturnEntity() {
        final Route mockRoute = mock(Route.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockRoute.getAgencyId()).thenReturn("route id");

        assertEquals(underTest.addRoute(mockRoute), mockRoute);
    }

    @Test
    void addSameRouteTwiceShouldReturnNull() {
        final Route mockRoute = mock(Route.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockRoute.getRouteId()).thenReturn("route id");

        underTest.addRoute(mockRoute);

        assertNull(underTest.addRoute(mockRoute));
    }

    @Test
    void addNullRouteShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        //noinspection ConstantConditions
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addRoute(null));
        assertEquals("Cannot add null route to data repository", exception.getMessage());
    }

    @Test
    void getRouteByIdShouldReturnRelatedRoute() {
        final Route mockRoute00 = mock(Route.class);
        final Route mockRoute01 = mock(Route.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockRoute00.getRouteId()).thenReturn("route id0");
        when(mockRoute01.getRouteId()).thenReturn("route id1");

        underTest.addRoute(mockRoute00);
        underTest.addRoute(mockRoute01);

        assertEquals(mockRoute00, underTest.getRouteById("route id0"));
        assertEquals(mockRoute01, underTest.getRouteById("route id1"));
    }

    @Test
    void addSameCalendarDateShouldReturnNull() {
        final CalendarDate mockCalendarDate = mock(CalendarDate.class);
        final LocalDateTime mockDate = mock(LocalDateTime.class);
        when(mockCalendarDate.getServiceId()).thenReturn("service id");
        when(mockCalendarDate.getDate()).thenReturn(mockDate);
        when(mockCalendarDate.getExceptionType()).thenReturn(ExceptionType.REMOVED_SERVICE);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addCalendarDate(mockCalendarDate);
        assertNull(underTest.addCalendarDate(mockCalendarDate));
    }

    @Test
    void addNullCalendarDateShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        //noinspection ConstantConditions to avoid lint
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addCalendarDate(null));

        assertEquals("Cannot add null calendar date to data repository", exception.getMessage());
    }

    @Test
    void addCalendarDateAndGetCalendarDateShouldReturnSameEntity() {
        final CalendarDate calendarDate00 = mock(CalendarDate.class);
        final CalendarDate calendarDate01 = mock(CalendarDate.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        final LocalDateTime mockDate = mock(LocalDateTime.class);

        when(calendarDate00.getServiceId()).thenReturn("service id 00");
        when(calendarDate00.getDate()).thenReturn(mockDate);
        when(calendarDate00.getExceptionType()).thenReturn(ExceptionType.ADDED_SERVICE);

        when(calendarDate01.getServiceId()).thenReturn("service id 01");
        when(calendarDate01.getDate()).thenReturn(mockDate);
        when(calendarDate01.getExceptionType()).thenReturn(ExceptionType.REMOVED_SERVICE);

        assertEquals(calendarDate00, underTest.addCalendarDate(calendarDate00));
        assertEquals(calendarDate01, underTest.addCalendarDate(calendarDate01));

        assertEquals(calendarDate00, underTest.getCalendarDateByServiceIdDateAndExceptionType("service id 00",
                mockDate, ExceptionType.ADDED_SERVICE));
        assertEquals(calendarDate01, underTest.getCalendarDateByServiceIdDateAndExceptionType("service id 01",
                mockDate, ExceptionType.REMOVED_SERVICE));
    }
}