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
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class InMemoryGtfsDataRepositoryTest {
    private final String STRING_TEST_VALUE = "test_value";

    @Test
    void getAgencyCollectionShouldReturnRouteCollection() throws SQLIntegrityConstraintViolationException {
        Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
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
        underTest.addEntity(agency00);

        mockBuilder.agencyId("test_id1");

        final Agency agency01 = mockBuilder.build();
        underTest.addEntity(agency01);

        final Map<String, Agency> toVerify = underTest.getAgencyCollection();

        assertEquals("test_id0", toVerify.get("test_id0").getAgencyId());
        assertEquals("test_id1", toVerify.get("test_id1").getAgencyId());
    }

    @Test
    void callToAddEntityShouldAddAgencyToRepoAndReturnSameEntity() throws SQLIntegrityConstraintViolationException {
        Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
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
        Agency toCheck = underTest.addEntity(agency00);

        assertEquals(1, underTest.getAgencyCollection().size());
        assertEquals(agency00, toCheck);

        mockBuilder.agencyId("test_id1");

        final Agency agency01 = mockBuilder.build();
        toCheck = underTest.addEntity(agency01);

        assertEquals(2, underTest.getAgencyCollection().size());
        assertEquals(toCheck, agency01);
    }

    @Test
    void getAgencyByIdShouldReturnRelatedAgency() throws SQLIntegrityConstraintViolationException {
        Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
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

        underTest.addEntity(mockBuilder.build());

        mockBuilder.agencyId("test_id1");

        underTest.addEntity(mockBuilder.build());

        assertEquals("test_id0", underTest.getAgencyById("test_id0").getAgencyId());
        assertEquals("test_id1", underTest.getAgencyById("test_id1").getAgencyId());
    }

    @Test
    public void isPresentShouldReturnTrueIfAgencyIsAlreadyPresentInRepository()
            throws SQLIntegrityConstraintViolationException {
        Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
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

        Agency agency00 = mockBuilder.build();
        underTest.addEntity(agency00);

        mockBuilder.agencyId("test_id1");

        Agency agency01 = mockBuilder.build();
        underTest.addEntity(agency01);

        assertTrue(underTest.isPresent(agency00));
        assertTrue(underTest.isPresent(agency01));
    }

    @Test
    public void isPresentShouldReturnFalseIfAgencyIsNotAlreadyPresentInRepository()
            throws SQLIntegrityConstraintViolationException {
        Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
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

        Agency agency00 = mockBuilder.build();
        underTest.addEntity(agency00);

        mockBuilder.agencyId("test_id1");

        Agency agency01 = mockBuilder.build();
        assertFalse(underTest.isPresent(agency01));
    }

    @Test
    public void tryToAddTwiceTheSameAgencyShouldThrowError() throws SQLIntegrityConstraintViolationException {
        Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
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

        underTest.addEntity(mockBuilder.build());

        mockBuilder.agencyId("test_id0");

        assertThrows(SQLIntegrityConstraintViolationException.class, () -> underTest.addEntity(mockBuilder.build()));
    }

    @Test
    void callToAddEntityShouldAddRouteToRepoAndReturnEntity() throws SQLIntegrityConstraintViolationException {

        Route.RouteBuilder mockBuilder = mock(Route.RouteBuilder.class);
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
        Route toCheck = underTest.addEntity(route00);

        assertEquals(1, underTest.getRouteCollection().size());
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
        toCheck = underTest.addEntity(route01);

        assertEquals(2, underTest.getRouteCollection().size());
        assertEquals(toCheck, route01);
    }

    @Test
    void getRouteCollectionShouldReturnRouteCollection() throws SQLIntegrityConstraintViolationException {

        Route.RouteBuilder mockBuilder = mock(Route.RouteBuilder.class);
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
        underTest.addEntity(route00);

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
        underTest.addEntity(route01);

        final Map<String, Route> toVerify = underTest.getRouteCollection();

        assertEquals("test_id_0", toVerify.get("test_id_0").getRouteId());
        assertEquals("test_id_1", toVerify.get("test_id_1").getRouteId());
    }

    @Test
    void getRouteByIdShouldReturnRelatedRoute() throws SQLIntegrityConstraintViolationException {

        Route.RouteBuilder mockBuilder = mock(Route.RouteBuilder.class);
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

        underTest.addEntity(mockBuilder.build());

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

        underTest.addEntity(mockBuilder.build());

        assertEquals("test_id_0", underTest.getRouteById("test_id_0").getRouteId());
        assertEquals("test_id_1", underTest.getRouteById("test_id_1").getRouteId());
    }

    @Test
    public void tryToAddTwiceTheSameRouteShouldThrowException() throws SQLIntegrityConstraintViolationException {

        Route.RouteBuilder mockBuilder = mock(Route.RouteBuilder.class);
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

        GtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addEntity(mockBuilder.build());

        assertThrows(SQLIntegrityConstraintViolationException.class, () -> underTest.addEntity(mockBuilder.build()));
    }

    @Test
    public void getCalendarCollectionShouldReturnCalendarCollection() throws SQLIntegrityConstraintViolationException {
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

        final Map<String, Calendar> toCheck = underTest.getCalendarCollection();

        final Map<String, Calendar> mockCalendarMap = new HashMap<>();
        mockCalendarMap.put(calendar00.getServiceId(), calendar00);
        mockCalendarMap.put(calendar01.getServiceId(), calendar01);

        assertEquals(mockCalendarMap, toCheck);
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
        assertEquals(1, underTest.getCalendarCollection().size());
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