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