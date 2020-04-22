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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class InMemoryGtfsDataRepositoryTest {
    private final String STRING_TEST_VALUE = "test_value";

    @Test
    void callToAddAgencyShouldAddAgencyToRepoAndReturnSameEntity() throws SQLIntegrityConstraintViolationException {
        final Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
        when(mockBuilder.agencyId(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyName(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyTimezone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyLang(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyPhone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyFareUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyEmail(anyString())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.agencyId("test_id0")
                .agencyName(STRING_TEST_VALUE)
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final Agency agency00 = mockBuilder.build();
        Agency toCheck = underTest.addAgency(agency00);

        assertEquals(agency00, toCheck);

        mockBuilder.agencyId("test_id1");

        final Agency agency01 = mockBuilder.build();
        toCheck = underTest.addAgency(agency01);

        assertEquals(toCheck, agency01);
    }

    @Test
    void getAgencyByIdShouldReturnRelatedAgency() throws SQLIntegrityConstraintViolationException {
        final Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
        when(mockBuilder.agencyId(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyName(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyTimezone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyLang(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyPhone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyFareUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyEmail(anyString())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.agencyId("test_id0")
                .agencyName(STRING_TEST_VALUE)
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addAgency(mockBuilder.build());

        mockBuilder.agencyId("test_id1");

        underTest.addAgency(mockBuilder.build());

        assertEquals("test_id0", underTest.getAgencyById("test_id0").getAgencyId());
        assertEquals("test_id1", underTest.getAgencyById("test_id1").getAgencyId());
    }

    @Test
    public void tryToAddTwiceTheSameAgencyShouldThrowError() throws SQLIntegrityConstraintViolationException {
        final Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
        when(mockBuilder.agencyId(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyName(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyTimezone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyLang(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyPhone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyFareUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyEmail(anyString())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        mockBuilder.agencyId("test_id0")
                .agencyName(STRING_TEST_VALUE)
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        underTest.addAgency(mockBuilder.build());

        mockBuilder.agencyId("test_id0");

        assertThrows(SQLIntegrityConstraintViolationException.class, () -> underTest.addAgency(mockBuilder.build()));
    }

    @Test
    void callToAddRouteShouldAddRouteToRepoAndReturnEntity() throws SQLIntegrityConstraintViolationException {
        final Route.RouteBuilder mockBuilder = mock(Route.RouteBuilder.class);
        when(mockBuilder.routeId(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyId(anyString())).thenCallRealMethod();
        when(mockBuilder.routeShortName(anyString())).thenCallRealMethod();
        when(mockBuilder.routeLongName(anyString())).thenCallRealMethod();
        when(mockBuilder.routeDesc(anyString())).thenCallRealMethod();
        when(mockBuilder.routeType(anyInt())).thenCallRealMethod();
        when(mockBuilder.routeUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.routeColor(anyString())).thenCallRealMethod();
        when(mockBuilder.routeTextColor(anyString())).thenCallRealMethod();
        when(mockBuilder.routeSortOrder(anyInt())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.routeId("test_id_0")
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(3)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(1);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final Route route00 = mockBuilder.build();
        Route toCheck = underTest.addRoute(route00);

        assertEquals(toCheck, route00);

        mockBuilder.routeId("test_id_1")
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(3)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(1);


        final Route route01 = mockBuilder.build();
        toCheck = underTest.addRoute(route01);

        assertEquals(toCheck, route01);
    }

    @Test
    void getRouteByIdShouldReturnRelatedRoute() throws SQLIntegrityConstraintViolationException {
        final Route.RouteBuilder mockBuilder = mock(Route.RouteBuilder.class);
        when(mockBuilder.routeId(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyId(anyString())).thenCallRealMethod();
        when(mockBuilder.routeShortName(anyString())).thenCallRealMethod();
        when(mockBuilder.routeLongName(anyString())).thenCallRealMethod();
        when(mockBuilder.routeDesc(anyString())).thenCallRealMethod();
        when(mockBuilder.routeType(anyInt())).thenCallRealMethod();
        when(mockBuilder.routeUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.routeColor(anyString())).thenCallRealMethod();
        when(mockBuilder.routeTextColor(anyString())).thenCallRealMethod();
        when(mockBuilder.routeSortOrder(anyInt())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.routeId("test_id_0")
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(3)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(1);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addRoute(mockBuilder.build());

        mockBuilder.routeId("test_id_1")
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(3)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(1);

        underTest.addRoute(mockBuilder.build());

        assertEquals("test_id_0", underTest.getRouteById("test_id_0").getRouteId());
        assertEquals("test_id_1", underTest.getRouteById("test_id_1").getRouteId());
    }

    @Test
    public void tryToAddTwiceTheSameRouteShouldThrowException() throws SQLIntegrityConstraintViolationException {
        final Route.RouteBuilder mockBuilder = mock(Route.RouteBuilder.class);
        when(mockBuilder.routeId(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyId(anyString())).thenCallRealMethod();
        when(mockBuilder.routeShortName(anyString())).thenCallRealMethod();
        when(mockBuilder.routeLongName(anyString())).thenCallRealMethod();
        when(mockBuilder.routeDesc(anyString())).thenCallRealMethod();
        when(mockBuilder.routeType(anyInt())).thenCallRealMethod();
        when(mockBuilder.routeUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.routeColor(anyString())).thenCallRealMethod();
        when(mockBuilder.routeTextColor(anyString())).thenCallRealMethod();
        when(mockBuilder.routeSortOrder(anyInt())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.routeId("test_id_0")
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(3)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(1);

        final GtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addRoute(mockBuilder.build());

        assertThrows(SQLIntegrityConstraintViolationException.class, () -> underTest.addRoute(mockBuilder.build()));
    }

    @Test
    void getCalendarDateCollectionShouldReturnCalendarDateCollection() throws SQLIntegrityConstraintViolationException {
        final CalendarDate.CalendarDateBuilder mockBuilder = mock(CalendarDate.CalendarDateBuilder.class, RETURNS_SELF);
        when(mockBuilder.serviceId(anyString())).thenCallRealMethod();
        when(mockBuilder.date(any(LocalDateTime.class))).thenCallRealMethod();
        when(mockBuilder.exceptionType(anyInt())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        final LocalDateTime date = LocalDateTime.now();

        mockBuilder.serviceId("service_id0")
                .date(date)
                .exceptionType(1);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final CalendarDate calendarDate00 = mockBuilder.build();
        underTest.addCalendarDate(calendarDate00);

        mockBuilder.serviceId("service_id1")
                .date(date)
                .exceptionType(1);

        final CalendarDate calendarDate01 = mockBuilder.build();
        underTest.addCalendarDate(calendarDate01);

        final Map<String, Map<LocalDateTime, CalendarDate>> mockCalendarDateCollection = new HashMap<>();

        final Map<LocalDateTime, CalendarDate> innerMap0 = new HashMap<>();
        innerMap0.put(date, calendarDate00);

        final Map<LocalDateTime, CalendarDate> innerMap1 = new HashMap<>();
        innerMap1.put(date, calendarDate01);

        mockCalendarDateCollection.put("service_id0", innerMap0);
        mockCalendarDateCollection.put("service_id1", innerMap1);

        assertEquals(mockCalendarDateCollection, underTest.getCalendarDateCollection());
    }

    @Test
    void getCalendarByServiceIdAndDateShouldReturnRelatedCalendarDate()
            throws SQLIntegrityConstraintViolationException {
        final CalendarDate.CalendarDateBuilder mockBuilder = mock(CalendarDate.CalendarDateBuilder.class, RETURNS_SELF);
        when(mockBuilder.serviceId(anyString())).thenCallRealMethod();
        when(mockBuilder.date(any(LocalDateTime.class))).thenCallRealMethod();
        when(mockBuilder.exceptionType(anyInt())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        final LocalDateTime date0 = LocalDateTime.now();

        mockBuilder.serviceId("service_id0")
                .date(date0)
                .exceptionType(1);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final CalendarDate calendarDate00 = mockBuilder.build();
        underTest.addCalendarDate(calendarDate00);

        final LocalDateTime date1 = LocalDateTime.now();
        mockBuilder.serviceId("service_id1")
                .date(date1)
                .exceptionType(2);

        final CalendarDate calendarDate01 = mockBuilder.build();
        underTest.addCalendarDate(calendarDate01);

        assertEquals(calendarDate00, underTest.getCalendarDateByServiceIdAndDate("service_id0", date0));
        assertEquals(calendarDate01, underTest.getCalendarDateByServiceIdAndDate("service_id1", date1));
    }

    @Test
    void callToAddCalendarDateShouldAddEntityToGtfsDataRepoAndReturnSameEntity()
            throws SQLIntegrityConstraintViolationException {
        final CalendarDate.CalendarDateBuilder mockBuilder = mock(CalendarDate.CalendarDateBuilder.class, RETURNS_SELF);
        when(mockBuilder.serviceId(anyString())).thenCallRealMethod();
        when(mockBuilder.date(any(LocalDateTime.class))).thenCallRealMethod();
        when(mockBuilder.exceptionType(anyInt())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        final LocalDateTime date0 = LocalDateTime.now();

        mockBuilder.serviceId("service_id0")
                .date(date0)
                .exceptionType(1);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final CalendarDate calendarDate00 = mockBuilder.build();
        assertEquals(calendarDate00, underTest.addCalendarDate(calendarDate00));
        assertEquals(1, underTest.getCalendarDateCollection().size());

        final LocalDateTime date1 = LocalDateTime.now();
        mockBuilder.serviceId("service_id1")
                .date(date1)
                .exceptionType(2);

        final CalendarDate calendarDate01 = mockBuilder.build();

        assertEquals(calendarDate01, underTest.addCalendarDate(calendarDate01));
        assertEquals(2, underTest.getCalendarDateCollection().size());
    }

    @Test
    void tryToAddTwiceSameCalendarDateBasedOnServiceIdAndDateShouldThrowException()
            throws SQLIntegrityConstraintViolationException {
        final CalendarDate.CalendarDateBuilder mockBuilder = mock(CalendarDate.CalendarDateBuilder.class, RETURNS_SELF);
        when(mockBuilder.serviceId(anyString())).thenCallRealMethod();
        when(mockBuilder.date(any(LocalDateTime.class))).thenCallRealMethod();
        when(mockBuilder.exceptionType(anyInt())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        final LocalDateTime date = LocalDateTime.now();

        mockBuilder.serviceId("service_id0")
                .date(date)
                .exceptionType(1);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final CalendarDate calendarDate = mockBuilder.build();
        underTest.addCalendarDate(calendarDate);

        final Exception exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> underTest.addCalendarDate(calendarDate));

        assertEquals("calendar_dates based on service_id and date must be unique in dataset",
                exception.getMessage());
    }
}