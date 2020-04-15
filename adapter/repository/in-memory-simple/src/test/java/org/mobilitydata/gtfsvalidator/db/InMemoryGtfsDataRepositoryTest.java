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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InMemoryGtfsDataRepositoryTest {
    private final String STRING_TEST_VALUE = "test_value";

    @Test
    void getAgencyCollectionShouldReturnRouteCollection() throws SQLIntegrityConstraintViolationException {
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

        underTest.addEntity(mockBuilder.build());

        mockBuilder.agencyId("test_id1");

        underTest.addEntity(mockBuilder.build());

        assertEquals("test_id0", underTest.getAgencyById("test_id0").getAgencyId());
        assertEquals("test_id1", underTest.getAgencyById("test_id1").getAgencyId());
    }

    @Test
    public void isPresentShouldReturnTrueIfAgencyIsAlreadyPresentInRepository()
            throws SQLIntegrityConstraintViolationException {
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

        final Agency agency00 = mockBuilder.build();
        underTest.addEntity(agency00);

        mockBuilder.agencyId("test_id1");

        final Agency agency01 = mockBuilder.build();
        underTest.addEntity(agency01);

        assertTrue(underTest.isPresent(agency00));
        assertTrue(underTest.isPresent(agency01));
    }

    @Test
    public void isPresentShouldReturnFalseIfAgencyIsNotAlreadyPresentInRepository()
            throws SQLIntegrityConstraintViolationException {
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

        final Agency agency00 = mockBuilder.build();
        underTest.addEntity(agency00);

        mockBuilder.agencyId("test_id1");

        final Agency agency01 = mockBuilder.build();
        assertFalse(underTest.isPresent(agency01));
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

        underTest.addEntity(mockBuilder.build());

        mockBuilder.agencyId("test_id0");

        assertThrows(SQLIntegrityConstraintViolationException.class, () -> underTest.addEntity(mockBuilder.build()));
    }

    @Test
    void callToAddEntityShouldAddRouteToRepoAndReturnEntity() throws SQLIntegrityConstraintViolationException {
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

        underTest.addEntity(mockBuilder.build());

        assertThrows(SQLIntegrityConstraintViolationException.class, () -> underTest.addEntity(mockBuilder.build()));
    }

    @Test
    public void getTripCollectionShouldShouldReturnTripCollection() throws SQLIntegrityConstraintViolationException {
        final Trip.TripBuilder mockBuilder = mock(Trip.TripBuilder.class, RETURNS_SELF);
        when(mockBuilder.routeId(anyString())).thenCallRealMethod();
        when(mockBuilder.serviceId(anyString())).thenCallRealMethod();
        when(mockBuilder.tripId(anyString())).thenCallRealMethod();
        when(mockBuilder.tripHeadsign(anyString())).thenCallRealMethod();
        when(mockBuilder.tripShortName(anyString())).thenCallRealMethod();
        when(mockBuilder.directionId(anyInt())).thenCallRealMethod();
        when(mockBuilder.blockId(anyString())).thenCallRealMethod();
        when(mockBuilder.shapeId(anyString())).thenCallRealMethod();
        when(mockBuilder.wheelchairAccessible(anyInt())).thenCallRealMethod();
        when(mockBuilder.bikesAllowed(anyInt())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.routeId(STRING_TEST_VALUE)
                .serviceId(STRING_TEST_VALUE)
                .tripId("test_id_0")
                .tripHeadsign("test")
                .tripShortName("test")
                .directionId(1)
                .blockId("test")
                .shapeId("test")
                .wheelchairAccessible(1)
                .bikesAllowed(0);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final Trip trip00 = mockBuilder.build();
        underTest.addTrip(trip00);

        mockBuilder.tripId("test_id_1");

        final Trip trip01 = mockBuilder.build();
        underTest.addTrip(trip01);

        final Map<String, Trip> mockTripMap = new HashMap<>();
        mockTripMap.put("test_id_0", trip00);
        mockTripMap.put("test_id_1", trip01);

        final Map<String, Trip> toVerify = underTest.getTripCollection();

        assertEquals(mockTripMap, toVerify);
    }

    @Test
    public void getTripByIdShouldReturnRelatedTrip() throws SQLIntegrityConstraintViolationException {
        final Trip.TripBuilder mockBuilder = mock(Trip.TripBuilder.class, RETURNS_SELF);
        when(mockBuilder.routeId(anyString())).thenCallRealMethod();
        when(mockBuilder.serviceId(anyString())).thenCallRealMethod();
        when(mockBuilder.tripId(anyString())).thenCallRealMethod();
        when(mockBuilder.tripHeadsign(anyString())).thenCallRealMethod();
        when(mockBuilder.tripShortName(anyString())).thenCallRealMethod();
        when(mockBuilder.directionId(anyInt())).thenCallRealMethod();
        when(mockBuilder.blockId(anyString())).thenCallRealMethod();
        when(mockBuilder.shapeId(anyString())).thenCallRealMethod();
        when(mockBuilder.wheelchairAccessible(anyInt())).thenCallRealMethod();
        when(mockBuilder.bikesAllowed(anyInt())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.routeId(STRING_TEST_VALUE)
                .serviceId(STRING_TEST_VALUE)
                .tripId("test_id_0")
                .tripHeadsign("test")
                .tripShortName("test")
                .directionId(1)
                .blockId("test")
                .shapeId("test")
                .wheelchairAccessible(1)
                .bikesAllowed(0);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final Trip trip00 = mockBuilder.build();
        underTest.addTrip(trip00);

        mockBuilder.tripId("test_id_1");

        final Trip trip01 = mockBuilder.build();
        underTest.addTrip(trip01);

        assertEquals(trip00, underTest.getTripById("test_id_0"));
        assertEquals(trip01, underTest.getTripById("test_id_1"));
    }

    @Test
    public void callToAddTripShouldAddEntityToRepoAndReturnSameEntity() throws SQLIntegrityConstraintViolationException {
        final Trip.TripBuilder mockBuilder = mock(Trip.TripBuilder.class, RETURNS_SELF);
        when(mockBuilder.routeId(anyString())).thenCallRealMethod();
        when(mockBuilder.serviceId(anyString())).thenCallRealMethod();
        when(mockBuilder.tripId(anyString())).thenCallRealMethod();
        when(mockBuilder.tripHeadsign(anyString())).thenCallRealMethod();
        when(mockBuilder.tripShortName(anyString())).thenCallRealMethod();
        when(mockBuilder.directionId(anyInt())).thenCallRealMethod();
        when(mockBuilder.blockId(anyString())).thenCallRealMethod();
        when(mockBuilder.shapeId(anyString())).thenCallRealMethod();
        when(mockBuilder.wheelchairAccessible(anyInt())).thenCallRealMethod();
        when(mockBuilder.bikesAllowed(anyInt())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.routeId(STRING_TEST_VALUE)
                .serviceId(STRING_TEST_VALUE)
                .tripId("test_id_0")
                .tripHeadsign("test")
                .tripShortName("test")
                .directionId(1)
                .blockId("test")
                .shapeId("test")
                .wheelchairAccessible(1)
                .bikesAllowed(0);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final Trip trip00 = mockBuilder.build();
        Trip toCheck = underTest.addTrip(trip00);

        assertEquals(trip00, toCheck);
    }

    @Test
    public void tryToAddTwiceTheSameTripShouldThrowException() throws SQLIntegrityConstraintViolationException {
        final Trip.TripBuilder mockBuilder = mock(Trip.TripBuilder.class, RETURNS_SELF);
        when(mockBuilder.routeId(anyString())).thenCallRealMethod();
        when(mockBuilder.serviceId(anyString())).thenCallRealMethod();
        when(mockBuilder.tripId(anyString())).thenCallRealMethod();
        when(mockBuilder.tripHeadsign(anyString())).thenCallRealMethod();
        when(mockBuilder.tripShortName(anyString())).thenCallRealMethod();
        when(mockBuilder.directionId(anyInt())).thenCallRealMethod();
        when(mockBuilder.blockId(anyString())).thenCallRealMethod();
        when(mockBuilder.shapeId(anyString())).thenCallRealMethod();
        when(mockBuilder.wheelchairAccessible(anyInt())).thenCallRealMethod();
        when(mockBuilder.bikesAllowed(anyInt())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.routeId(STRING_TEST_VALUE)
                .serviceId(STRING_TEST_VALUE)
                .tripId("test_id_0")
                .tripHeadsign("test")
                .tripShortName("test")
                .directionId(1)
                .blockId("test")
                .shapeId("test")
                .wheelchairAccessible(1)
                .bikesAllowed(0);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final Trip trip = mockBuilder.build();
        underTest.addTrip(trip);

        Exception exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> underTest.addTrip(trip));

        assertEquals("trip must be unique in dataset", exception.getMessage());
    }
}