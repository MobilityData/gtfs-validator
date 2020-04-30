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
import org.mobilitydata.gtfsvalidator.domain.entity.Calendar;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Agency;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
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
        assertThrows(IllegalArgumentException.class, () -> underTest.addAgency(null));
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
        assertThrows(IllegalArgumentException.class, () -> underTest.addRoute(null));
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
    public void getCalendarByServiceIdShouldReturnRelatedCalendar() throws SQLIntegrityConstraintViolationException {
        final Calendar.CalendarBuilder mockBuilder = spy(Calendar.CalendarBuilder.class);

        mockBuilder.serviceId("test_id_0")
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final Calendar calendar00 = mockBuilder.build();
        underTest.addCalendar(calendar00);

        mockBuilder.serviceId("test_id_1");

        final Calendar calendar01 = mockBuilder.build();

        underTest.addCalendar(calendar01);

        assertEquals(calendar00, underTest.getCalendarByServiceId("test_id_0"));
        assertEquals(calendar01, underTest.getCalendarByServiceId("test_id_1"));
    }

    @Test
    public void callToAddCalendarShouldAddCalendarToRepoAndReturnEntity()
            throws SQLIntegrityConstraintViolationException {
        final Calendar.CalendarBuilder mockBuilder = spy(Calendar.CalendarBuilder.class);

        mockBuilder.serviceId("test_id_0")
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final Calendar calendar00 = mockBuilder.build();
        final Calendar toCheck = underTest.addCalendar(calendar00);

        assertEquals(toCheck, calendar00);
    }

    @Test
    public void duplicateCalendarShouldThrowException()
            throws SQLIntegrityConstraintViolationException {
        final Calendar.CalendarBuilder mockBuilder = spy(Calendar.CalendarBuilder.class);

        mockBuilder.serviceId("test_id_0")
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final Calendar calendar00 = mockBuilder.build();
        underTest.addCalendar(calendar00);

        final Exception exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> underTest.addCalendar(calendar00));
        assertEquals("service_id must be unique in calendar.txt", exception.getMessage());
    }
}