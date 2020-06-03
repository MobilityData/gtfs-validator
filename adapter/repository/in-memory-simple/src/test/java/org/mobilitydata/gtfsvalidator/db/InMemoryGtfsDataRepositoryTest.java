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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Attribution;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Level;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.CalendarDate;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.ExceptionType;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void addNullAttributionShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addAttribution(null));
        assertEquals("Cannot add null attribution to data repository", exception.getMessage());
    }

    @Test
    void addSameAttributionTwiceShouldReturnNull() {
        final Attribution mockAttribution = mock(Attribution.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockAttribution.getOrganizationName()).thenReturn("organization name");
        when(mockAttribution.getAttributionKey()).thenReturn("mock key");
        underTest.addAttribution(mockAttribution);

        assertNull(underTest.addAttribution(mockAttribution));
        verify(mockAttribution, times(2)).getAttributionKey();
        verifyNoMoreInteractions(mockAttribution);
    }

    @Test
    void addAttributionAndGetAttributionShouldReturnSameEntity() {
        final Attribution mockAttribution00 = mock(Attribution.class);
        when(mockAttribution00.getOrganizationName()).thenReturn("organization name 0");
        final Attribution mockAttribution01 = mock(Attribution.class);
        when(mockAttribution01.getOrganizationName()).thenReturn("organization name 1");

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockAttribution00.getAttributionKey())
                .thenReturn("null; null; null; null; organization name 0; false; false; false; null; null; null");
        when(mockAttribution01.getAttributionKey())
                .thenReturn("null; null; null; null; organization name 1; false; false; false; null; null; null");

        assertEquals(mockAttribution00, underTest.addAttribution(mockAttribution00));
        assertEquals(mockAttribution01, underTest.addAttribution(mockAttribution01));

        assertEquals(mockAttribution00, underTest.getAttribution(null, null, null,
                null, "organization name 0", false, false, false,
                null, null, null));
        assertEquals(mockAttribution01, underTest.getAttribution(null, null, null,
                null, "organization name 1", false, false, false,
                null, null, null));
        verify(mockAttribution00, times(1)).getAttributionKey();
        verify(mockAttribution01, times(1)).getAttributionKey();
        verifyNoMoreInteractions(mockAttribution00, mockAttribution01);
    }
}