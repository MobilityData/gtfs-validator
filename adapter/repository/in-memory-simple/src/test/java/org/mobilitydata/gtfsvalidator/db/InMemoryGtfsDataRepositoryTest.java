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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers.Transfer;

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
    void callToAddTransferShouldAddTransferToRepoAndReturnSameEntity() {
        final Transfer mockTransfer00 = mock(Transfer.class);
        when(mockTransfer00.getFromStopId()).thenReturn("stop id 0");
        when(mockTransfer00.getToStopId()).thenReturn("stop id 1");

        final Transfer mockTransfer01 = mock(Transfer.class);
        when(mockTransfer01.getFromStopId()).thenReturn("stop id 0");
        when(mockTransfer01.getToStopId()).thenReturn("stop id 2");

        final Transfer mockTransfer02 = mock(Transfer.class);
        when(mockTransfer02.getFromStopId()).thenReturn("stop id 4");
        when(mockTransfer02.getToStopId()).thenReturn("stop id 5");

        final Transfer mockTransfer03 = mock(Transfer.class);
        when(mockTransfer03.getFromStopId()).thenReturn("stop id 0");
        when(mockTransfer03.getToStopId()).thenReturn("stop id 5");

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        assertEquals(mockTransfer00, underTest.addTransfer(mockTransfer00));
        assertEquals(mockTransfer01, underTest.addTransfer(mockTransfer01));
        assertEquals(mockTransfer02, underTest.addTransfer(mockTransfer02));
        assertEquals(mockTransfer03, underTest.addTransfer(mockTransfer03));
    }

    @Test
    void addSameTransferTwiceShouldReturnNull() {
        final Transfer mockTransfer = mock(Transfer.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockTransfer.getFromStopId()).thenReturn("stop id 0");
        when(mockTransfer.getToStopId()).thenReturn("stop id 1");

        underTest.addTransfer(mockTransfer);

        assertNull(underTest.addTransfer(mockTransfer));
    }

    @Test
    void addNullTransferShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addTransfer(null));
        assertEquals("Cannot add null transfer to data repository", exception.getMessage());
    }

    @Test
    void addTransferAndGetTransferByIShouldReturnSameEntity() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final Transfer mockTransfer00 = mock(Transfer.class);
        when(mockTransfer00.getFromStopId()).thenReturn("stop id 0");
        when(mockTransfer00.getToStopId()).thenReturn("stop id 1");

        final Transfer mockTransfer01 = mock(Transfer.class);
        when(mockTransfer01.getFromStopId()).thenReturn("stop id 0");
        when(mockTransfer01.getToStopId()).thenReturn("stop id 2");

        final Transfer mockTransfer02 = mock(Transfer.class);
        when(mockTransfer02.getFromStopId()).thenReturn("stop id 4");
        when(mockTransfer02.getToStopId()).thenReturn("stop id 5");

        final Transfer mockTransfer03 = mock(Transfer.class);
        when(mockTransfer03.getFromStopId()).thenReturn("stop id 0");
        when(mockTransfer03.getToStopId()).thenReturn("stop id 5");

        assertEquals(mockTransfer00, underTest.addTransfer(mockTransfer00));
        assertEquals(mockTransfer01, underTest.addTransfer(mockTransfer01));
        assertEquals(mockTransfer02, underTest.addTransfer(mockTransfer02));
        assertEquals(mockTransfer03, underTest.addTransfer(mockTransfer03));

        assertEquals(mockTransfer00, underTest.getTransferByStopPair("stop id 0", "stop id 1"));
        assertEquals(mockTransfer01, underTest.getTransferByStopPair("stop id 0", "stop id 2"));
        assertEquals(mockTransfer02, underTest.getTransferByStopPair("stop id 4", "stop id 5"));
        assertEquals(mockTransfer03, underTest.getTransferByStopPair("stop id 0", "stop id 5"));
    }
}