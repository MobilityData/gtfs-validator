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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Calendar;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Level;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.CalendarDate;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.ExceptionType;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

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
    void getRouteAllShouldReturnAllRoutes() {
        final Route mockRoute00 = mock(Route.class);
        final Route mockRoute01 = mock(Route.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockRoute00.getRouteId()).thenReturn("route id0");
        when(mockRoute01.getRouteId()).thenReturn("route id1");

        underTest.addRoute(mockRoute00);
        underTest.addRoute(mockRoute01);

        Collection<Route> mockRoutes = List.of(mockRoute00, mockRoute01);

        assertTrue(underTest.getRouteAll().containsAll(mockRoutes));
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
        //for the purpose of the test addCalendarDate is called with null parameter. A warning is emitted since this
        // parameter is annotated as non null
        // noinspection ConstantConditions
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addCalendarDate(null));

        assertEquals("Cannot add null calendar date to data repository", exception.getMessage());
    }

    @Test
    void addCalendarDateAndGetCalendarDateShouldReturnSameEntity() {
        final CalendarDate calendarDate00 = mock(CalendarDate.class);
        final CalendarDate calendarDate01 = mock(CalendarDate.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        final LocalDateTime date = LocalDateTime.now();

        when(calendarDate00.getServiceId()).thenReturn("service id 00");
        when(calendarDate00.getDate()).thenReturn(date);
        when(calendarDate00.getExceptionType()).thenReturn(ExceptionType.ADDED_SERVICE);
        when(calendarDate00.getCalendarDateMappingKey()).thenReturn("service id 00" + date.toString());

        when(calendarDate01.getServiceId()).thenReturn("service id 01");
        when(calendarDate01.getDate()).thenReturn(date);
        when(calendarDate01.getExceptionType()).thenReturn(ExceptionType.REMOVED_SERVICE);
        when(calendarDate01.getCalendarDateMappingKey()).thenReturn("service id 01" + date.toString());

        assertEquals(calendarDate00, underTest.addCalendarDate(calendarDate00));
        assertEquals(calendarDate01, underTest.addCalendarDate(calendarDate01));

        assertEquals(calendarDate00, underTest.getCalendarDateByServiceIdDate("service id 00", date));
        assertEquals(calendarDate01, underTest.getCalendarDateByServiceIdDate("service id 01", date));
    }

    @Test
    void addSameLevelTwiceShouldReturnNull() {
        final Level mockLevel = mock(Level.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockLevel.getLevelId()).thenReturn("level id");

        underTest.addLevel(mockLevel);
        assertNull(underTest.addLevel(mockLevel));
    }

    @Test
    void addNullLevelShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addLevel(null));
        assertEquals("Cannot add null level to data repository", exception.getMessage());
    }

    @Test
    public void addLevelAndGetLevelByIdShouldReturnSameEntity() {
        final Level mockLevel00 = mock(Level.class);
        final Level mockLevel01 = mock(Level.class);
        when(mockLevel00.getLevelId()).thenReturn("level id 0");
        when(mockLevel01.getLevelId()).thenReturn("level id 1");

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        assertEquals(mockLevel00, underTest.addLevel(mockLevel00));
        assertEquals(mockLevel01, underTest.addLevel(mockLevel01));

        assertEquals(mockLevel00, underTest.getLevelById("level id 0"));
        assertEquals(mockLevel01, underTest.getLevelById("level id 1"));
    }

    @Test
    void addSameCalendarTwiceShouldReturnNull() {
        final Calendar mockCalendar = mock(Calendar.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockCalendar.getServiceId()).thenReturn("service id");

        underTest.addCalendar(mockCalendar);

        assertNull(underTest.addCalendar(mockCalendar));
    }

    @Test
    void addNullCalendarShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        assertThrows(IllegalArgumentException.class, () -> underTest.addCalendar(null));
    }

    @Test
    void getCalendarByServiceIdShouldReturnRelatedCalendar() {
        final Calendar mockCalendar00 = mock(Calendar.class);
        final Calendar mockCalendar01 = mock(Calendar.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        when(mockCalendar00.getServiceId()).thenReturn("service id00");
        when(mockCalendar01.getServiceId()).thenReturn("service id01");

        underTest.addCalendar(mockCalendar00);
        underTest.addCalendar(mockCalendar01);

        assertEquals(mockCalendar00, underTest.getCalendarByServiceId("service id00"));
        assertEquals(mockCalendar01, underTest.getCalendarByServiceId("service id01"));
    }

    @Test
    public void addSameTripTwiceShouldReturnNull() {
        final Trip mockTrip = mock(Trip.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockTrip.getTripId()).thenReturn("trip id");

        underTest.addTrip(mockTrip);

        assertNull(underTest.addTrip(mockTrip));
    }

    @Test
    void addNullTripShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        assertThrows(IllegalArgumentException.class, () -> underTest.addTrip(null));
    }

    @Test
    public void addTripShouldReturnSameEntityAndCallToGetTripByIdShouldReturnRelatedTrip() {
        final Trip mockTrip00 = mock(Trip.class);
        final Trip mockTrip01 = mock(Trip.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockTrip00.getTripId()).thenReturn("trip id00");
        when(mockTrip01.getTripId()).thenReturn("trip id01");

        assertEquals(mockTrip00, underTest.addTrip(mockTrip00));
        assertEquals(mockTrip01, underTest.addTrip(mockTrip01));

        assertEquals(mockTrip00, underTest.getTripById("trip id00"));
        assertEquals(mockTrip01, underTest.getTripById("trip id01"));
    }
}