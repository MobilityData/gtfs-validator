/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.SuspiciousRouteSortOrderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ValidateRouteSortSemanticValueTest {

    @Test
    void routeWithTooBigRouteSortOrderShouldGenerateNotice() {
        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.ROUTE__ROUTE_SORT_ORDER_LOWER_BOUND_KEY))
                .thenReturn("30");
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.ROUTE__ROUTE_SORT_ORDER_UPPER_BOUND_KEY))
                .thenReturn("50");

        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteSortOrder()).thenReturn(70);
        when(mockRoute.getRouteId()).thenReturn("route id");
        final Collection<Route> mockRouteCollection = new ArrayList<>(List.of(mockRoute));
        when(mockGtfsDataRepo.getRouteAll()).thenReturn(mockRouteCollection);

        final ValidationResultRepository mockResultRepo = spy(ValidationResultRepository.class);
        final ValidateRouteSortSemanticValue underTest =
                new ValidateRouteSortSemanticValue(mockExecParamRepo, mockGtfsDataRepo, mockResultRepo);

        underTest.execute();

        InOrder inOrder = Mockito.inOrder(mockGtfsDataRepo, mockExecParamRepo, mockResultRepo, mockRoute);

        inOrder.verify(mockGtfsDataRepo, times(1)).getRouteAll();
        inOrder.verify(mockExecParamRepo, times(1)).getExecParamValue(
                ExecParamRepository.ROUTE__ROUTE_SORT_ORDER_LOWER_BOUND_KEY);
        inOrder.verify(mockExecParamRepo, times(1)).getExecParamValue(
                ExecParamRepository.ROUTE__ROUTE_SORT_ORDER_UPPER_BOUND_KEY);

        // suppressed warning since the results of method getRouteSortOrder is not to be checked here
        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockRoute, times(1)).getRouteSortOrder();

        final ArgumentCaptor<SuspiciousRouteSortOrderNotice> captor =
                ArgumentCaptor.forClass(SuspiciousRouteSortOrderNotice.class);

        inOrder.verify(mockResultRepo, times(1)).addNotice(captor.capture());
        // suppressed warning since the results of method getRouteId is not to be checked here
        //noinspection ResultOfMethodCallIgnored
        verify(mockRoute, times(1)).getRouteId();
        final List<SuspiciousRouteSortOrderNotice> noticeList = captor.getAllValues();

        assertEquals("routes.txt", noticeList.get(0).getFilename());
        assertEquals("route_sort_order", noticeList.get(0).getFieldName());
        assertEquals("route id", noticeList.get(0).getEntityId());
        assertEquals(30, noticeList.get(0).getRangeMin());
        assertEquals(50, noticeList.get(0).getRangeMax());
        assertEquals(70, noticeList.get(0).getActualValue());
        verifyNoMoreInteractions(mockExecParamRepo, mockGtfsDataRepo, mockResultRepo, mockRoute);
    }

    @Test
    void routeWithTooSmallRouteSortOrderShouldGenerateNotice() {
        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.ROUTE__ROUTE_SORT_ORDER_LOWER_BOUND_KEY))
                .thenReturn("30");
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.ROUTE__ROUTE_SORT_ORDER_UPPER_BOUND_KEY))
                .thenReturn("50");

        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteSortOrder()).thenReturn(20);
        when(mockRoute.getRouteId()).thenReturn("route id");
        final Collection<Route> mockRouteCollection = new ArrayList<>(List.of(mockRoute));
        when(mockGtfsDataRepo.getRouteAll()).thenReturn(mockRouteCollection);

        final ValidationResultRepository mockResultRepo = spy(ValidationResultRepository.class);
        final ValidateRouteSortSemanticValue underTest =
                new ValidateRouteSortSemanticValue(mockExecParamRepo, mockGtfsDataRepo, mockResultRepo);

        underTest.execute();

        InOrder inOrder = Mockito.inOrder(mockGtfsDataRepo, mockExecParamRepo, mockResultRepo, mockRoute);

        inOrder.verify(mockGtfsDataRepo, times(1)).getRouteAll();
        inOrder.verify(mockExecParamRepo, times(1)).getExecParamValue(
                ExecParamRepository.ROUTE__ROUTE_SORT_ORDER_LOWER_BOUND_KEY);
        inOrder.verify(mockExecParamRepo, times(1)).getExecParamValue(
                ExecParamRepository.ROUTE__ROUTE_SORT_ORDER_UPPER_BOUND_KEY);

        // suppressed warning since the results of method getRouteSortOrder is not to be checked here
        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockRoute, times(1)).getRouteSortOrder();

        final ArgumentCaptor<SuspiciousRouteSortOrderNotice> captor =
                ArgumentCaptor.forClass(SuspiciousRouteSortOrderNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());
        // suppressed warning since the results of method getRouteId is not to be checked here
        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockRoute, times(1)).getRouteId();

        final List<SuspiciousRouteSortOrderNotice> noticeList = captor.getAllValues();

        assertEquals("routes.txt", noticeList.get(0).getFilename());
        assertEquals("route_sort_order", noticeList.get(0).getFieldName());
        assertEquals("route id", noticeList.get(0).getEntityId());
        assertEquals(30, noticeList.get(0).getRangeMin());
        assertEquals(50, noticeList.get(0).getRangeMax());
        assertEquals(20, noticeList.get(0).getActualValue());
        verifyNoMoreInteractions(mockExecParamRepo, mockGtfsDataRepo, mockResultRepo, mockRoute);
    }


    @Test
    void routeWithoutSuspiciousRouteSortOrderShouldNotGenerateNotice() {
        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.ROUTE__ROUTE_SORT_ORDER_LOWER_BOUND_KEY))
                .thenReturn("30");
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.ROUTE__ROUTE_SORT_ORDER_UPPER_BOUND_KEY))
                .thenReturn("50");

        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteSortOrder()).thenReturn(40);
        when(mockRoute.getRouteId()).thenReturn("route id");
        final Collection<Route> mockRouteCollection = new ArrayList<>(List.of(mockRoute));
        when(mockGtfsDataRepo.getRouteAll()).thenReturn(mockRouteCollection);

        final ValidationResultRepository mockResultRepo = spy(ValidationResultRepository.class);
        final ValidateRouteSortSemanticValue underTest =
                new ValidateRouteSortSemanticValue(mockExecParamRepo, mockGtfsDataRepo, mockResultRepo);

        underTest.execute();

        InOrder inOrder = Mockito.inOrder(mockGtfsDataRepo, mockExecParamRepo, mockResultRepo, mockRoute);

        inOrder.verify(mockGtfsDataRepo, times(1)).getRouteAll();
        inOrder.verify(mockExecParamRepo, times(1)).getExecParamValue(
                ExecParamRepository.ROUTE__ROUTE_SORT_ORDER_LOWER_BOUND_KEY);
        inOrder.verify(mockExecParamRepo, times(1)).getExecParamValue(
                ExecParamRepository.ROUTE__ROUTE_SORT_ORDER_UPPER_BOUND_KEY);

        // suppressed warning since the results of method getRouteSortOrder is not to be checked here
        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockRoute, times(1)).getRouteSortOrder();
        verifyNoMoreInteractions(mockExecParamRepo, mockGtfsDataRepo, mockResultRepo, mockRoute);
    }
}