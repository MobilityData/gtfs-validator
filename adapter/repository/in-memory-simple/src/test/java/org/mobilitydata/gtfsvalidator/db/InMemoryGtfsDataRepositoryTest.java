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
    void getTransferByStopPairShouldReturnRelatedTransfer() throws SQLIntegrityConstraintViolationException {
        final Transfer.TransferBuilder mockBuilder = mock(Transfer.TransferBuilder.class);
        when(mockBuilder.fromStopId(anyString())).thenCallRealMethod();
        when(mockBuilder.toStopId(anyString())).thenCallRealMethod();
        when(mockBuilder.transferType(anyInt())).thenCallRealMethod();
        when(mockBuilder.minTransferTime(anyInt())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.fromStopId("from_stop_id_0")
                .toStopId("to_stop_id_1")
                .transferType(2)
                .minTransferTime(20);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final Transfer transfer00 = mockBuilder.build();
        underTest.addTransfer(transfer00);

        mockBuilder.fromStopId("from_stop_id_1")
                .toStopId("to_stop_id_2")
                .transferType(3)
                .minTransferTime(null);

        final Transfer transfer01 = mockBuilder.build();
        underTest.addTransfer(transfer01);

        assertEquals(transfer00, underTest.getTransferByStopPair("from_stop_id_0", "to_stop_id_1"));
        assertEquals(transfer01, underTest.getTransferByStopPair("from_stop_id_1", "to_stop_id_2"));
    }

    @Test
    void callToAddTransferShouldAddTransferToRepoAndReturnEntity() throws SQLIntegrityConstraintViolationException {
        final Transfer.TransferBuilder mockBuilder = mock(Transfer.TransferBuilder.class);
        when(mockBuilder.fromStopId(anyString())).thenCallRealMethod();
        when(mockBuilder.toStopId(anyString())).thenCallRealMethod();
        when(mockBuilder.transferType(anyInt())).thenCallRealMethod();
        when(mockBuilder.minTransferTime(anyInt())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.fromStopId("from_stop_id_0")
                .toStopId("to_stop_id_1")
                .transferType(2)
                .minTransferTime(20);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final Transfer transfer00 = mockBuilder.build();

        Transfer toCheck = underTest.addTransfer(transfer00);
        assertEquals(transfer00, toCheck);
        assertEquals(transfer00, underTest.getTransferByStopPair("from_stop_id_0", "to_stop_id_1"));

        mockBuilder.fromStopId("from_stop_id_1")
                .toStopId("to_stop_id_2")
                .transferType(3)
                .minTransferTime(null);

        final Transfer transfer01 = mockBuilder.build();
        toCheck = underTest.addTransfer(transfer01);
        assertEquals(transfer01, toCheck);
        assertEquals(transfer01, underTest.getTransferByStopPair("from_stop_id_1", "to_stop_id_2"));
    }

    @Test
    void tryToAddTwiceTheSameTransferShouldThrowException() throws SQLIntegrityConstraintViolationException {
        final Transfer.TransferBuilder mockBuilder = mock(Transfer.TransferBuilder.class);
        when(mockBuilder.fromStopId(anyString())).thenCallRealMethod();
        when(mockBuilder.toStopId(anyString())).thenCallRealMethod();
        when(mockBuilder.transferType(anyInt())).thenCallRealMethod();
        when(mockBuilder.minTransferTime(anyInt())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.fromStopId("from_stop_id_0")
                .toStopId("to_stop_id_1")
                .transferType(2)
                .minTransferTime(20);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addTransfer(mockBuilder.build());

        final Exception exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> underTest.addTransfer(mockBuilder.build()));

        assertEquals("transfer must be unique in dataset", exception.getMessage());
    }
}