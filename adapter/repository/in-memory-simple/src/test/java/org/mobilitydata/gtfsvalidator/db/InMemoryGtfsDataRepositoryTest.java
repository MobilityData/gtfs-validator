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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.fareattributes.FareAttribute;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    void callToAddAgencyShouldReturnNull() {
        final Agency mockAgency = mock(Agency.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockAgency.getAgencyId()).thenReturn("agency id");

        underTest.addAgency(mockAgency);

        assertNull(underTest.addAgency(mockAgency));
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
    void callToAddRouteShouldReturnNull() {
        final Route mockRoute = mock(Route.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockRoute.getRouteId()).thenReturn("route id");

        underTest.addRoute(mockRoute);

        assertNull(underTest.addRoute(mockRoute));
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
    void getFareAttributeByFareIdShouldReturnRelatedEntity() throws SQLIntegrityConstraintViolationException {
        final FareAttribute.FareAttributeBuilder mockBuilder = mock(FareAttribute.FareAttributeBuilder.class);
        when(mockBuilder.fareId(anyString())).thenCallRealMethod();

        final FareAttribute mockFareAttribute = mock(FareAttribute.class);
        when(mockFareAttribute.getFareId()).thenReturn("fare id");
        when(mockBuilder.build()).thenReturn(mockFareAttribute);

        mockBuilder.fareId("fare id");
        final FareAttribute fareAttribute = mockBuilder.build();

        final GtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addFareAttribute(fareAttribute);

        assertEquals(fareAttribute, underTest.getFareAttributeByFareId("fare id"));
    }

    @Test
    void callToAddFareAttributeShouldAddEntityToRepoAndReturnSameEntity()
            throws SQLIntegrityConstraintViolationException {
        final FareAttribute.FareAttributeBuilder mockBuilder = mock(FareAttribute.FareAttributeBuilder.class);
        when(mockBuilder.fareId(anyString())).thenCallRealMethod();

        final FareAttribute mockFareAttribute = mock(FareAttribute.class);
        when(mockFareAttribute.getFareId()).thenReturn("fare id");
        when(mockBuilder.build()).thenReturn(mockFareAttribute);

        mockBuilder.fareId("fare id");
        final FareAttribute fareAttribute = mockBuilder.build();

        final GtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        FareAttribute toCheck = underTest.addFareAttribute(fareAttribute);

        assertEquals(fareAttribute, toCheck);
    }

    @Test
    void tryToAddTwiceTheSameFareAttributeShouldThrowException() throws SQLIntegrityConstraintViolationException {
        final FareAttribute.FareAttributeBuilder mockBuilder = mock(FareAttribute.FareAttributeBuilder.class);
        when(mockBuilder.fareId(anyString())).thenCallRealMethod();

        final FareAttribute mockFareRule = mock(FareAttribute.class);
        when(mockFareRule.getFareId()).thenReturn("fare id");
        when(mockBuilder.build()).thenReturn(mockFareRule);

        mockBuilder.fareId("fare id");
        final FareAttribute fareAttribute = mockBuilder.build();

        final GtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addFareAttribute(fareAttribute);

        Exception exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> underTest.addFareAttribute(fareAttribute));
        assertEquals("fare attribute must be unique in dataset", exception.getMessage());
    }
}