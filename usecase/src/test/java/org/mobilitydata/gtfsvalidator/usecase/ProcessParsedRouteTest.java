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

package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.EntityMustBeUniqueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.UnexpectedValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProcessParsedRouteTest {

    private final String STRING_TEST_VALUE = "test_value";
    private final int INT_TEST_VALUE = 0;

    @Test
    void validatedParsedRouteShouldCreateRouteEntityAndBeAddedToGtfsDataRepository()
            throws SQLIntegrityConstraintViolationException {

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Route mockRoute = mock(Route.class);

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
        when(mockBuilder.build()).thenReturn(mockRoute);

        ProcessParsedRoute underTest = new ProcessParsedRoute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedRoute = mock(ParsedEntity.class);

        when(mockParsedRoute.get("route_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("agency_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_short_name")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_long_name")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_desc")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_type")).thenReturn(INT_TEST_VALUE);
        when(mockParsedRoute.get("route_url")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_color")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_text_color")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_sort_order")).thenReturn(INT_TEST_VALUE);

        underTest.execute(mockParsedRoute);

        verify(mockParsedRoute, times(10)).get(ArgumentMatchers.anyString());

        verify(mockBuilder, times(1)).routeId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).routeShortName(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).routeLongName(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).routeDesc(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).routeType(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).routeUrl(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).routeColor(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).routeTextColor(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).routeSortOrder(ArgumentMatchers.anyInt());

        InOrder inOrder = inOrder(mockBuilder, mockResultRepo, mockGtfsDataRepo);

        inOrder.verify(mockBuilder, times(1)).build();
        inOrder.verify(mockGtfsDataRepo, times(1)).addRoute(ArgumentMatchers.eq(mockRoute));

        verifyNoMoreInteractions(mockBuilder, mockResultRepo, mockGtfsDataRepo, mockParsedRoute);
    }

    @Test
    public void nullRouteIdShouldThrowExceptionAndAddMissingRequiredValueNoticeToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Route.RouteBuilder mockBuilder = spy(Route.RouteBuilder.class);

        final ProcessParsedRoute underTest = new ProcessParsedRoute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedRoute = mock(ParsedEntity.class);

        when(mockParsedRoute.get("route_id")).thenReturn(null);
        when(mockParsedRoute.get("agency_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_short_name")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_long_name")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_desc")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_type")).thenReturn(INT_TEST_VALUE);
        when(mockParsedRoute.get("route_url")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_color")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_text_color")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_sort_order")).thenReturn(INT_TEST_VALUE);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedRoute));

        Assertions.assertEquals("route_id can not be null in routes.txt", exception.getMessage());

        verify(mockParsedRoute, times(10)).get(anyString());

        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).routeId(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).routeShortName(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).routeLongName(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).routeDesc(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).routeType(ArgumentMatchers.eq(INT_TEST_VALUE));
        verify(mockBuilder, times(1)).routeUrl(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).routeColor(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).routeTextColor(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).routeSortOrder(ArgumentMatchers.eq(INT_TEST_VALUE));

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedRoute, times(1)).getEntityId();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("routes.txt", noticeList.get(0).getFilename());
        assertEquals("route_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedRoute, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void invalidRouteTypeShouldThrowExceptionAndAddMissingRequiredValueNoticeToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Route.RouteBuilder mockBuilder = spy(Route.RouteBuilder.class);

        final ProcessParsedRoute underTest = new ProcessParsedRoute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedRoute = mock(ParsedEntity.class);

        when(mockParsedRoute.get("route_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("agency_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_short_name")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_long_name")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_desc")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_type")).thenReturn(15);
        when(mockParsedRoute.get("route_url")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_color")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_text_color")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_sort_order")).thenReturn(INT_TEST_VALUE);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedRoute));

        Assertions.assertEquals("Unexpected value, or null value for field route_type in routes.txt",
                exception.getMessage());

        verify(mockParsedRoute, times(10)).get(anyString());

        verify(mockBuilder, times(1)).routeId(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).routeShortName(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).routeLongName(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).routeDesc(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).routeType(ArgumentMatchers.eq(15));
        verify(mockBuilder, times(1)).routeUrl(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).routeColor(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).routeTextColor(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).routeSortOrder(ArgumentMatchers.eq(INT_TEST_VALUE));

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedRoute, times(1)).getEntityId();

        final ArgumentCaptor<UnexpectedValueNotice> captor = ArgumentCaptor.forClass(UnexpectedValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        final List<UnexpectedValueNotice> noticeList = captor.getAllValues();

        assertEquals("routes.txt", noticeList.get(0).getFilename());
        assertEquals("route_type", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals("15", noticeList.get(0).getEnumValue());

        verifyNoMoreInteractions(mockParsedRoute, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void duplicateRouteShouldThrowExceptionAndAddEntityMustBeUniqueNoticeToResultRepo()
            throws SQLIntegrityConstraintViolationException {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteId()).thenReturn(STRING_TEST_VALUE);

        final Route.RouteBuilder mockBuilder = mock(Route.RouteBuilder.class, RETURNS_SELF);
        when(mockBuilder.build()).thenReturn(mockRoute);

        final ProcessParsedRoute underTest = new ProcessParsedRoute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedRoute = mock(ParsedEntity.class);
        when(mockParsedRoute.get("route_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("agency_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_short_name")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_long_name")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_desc")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_type")).thenReturn(7);
        when(mockParsedRoute.get("route_url")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_color")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_text_color")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get("route_sort_order")).thenReturn(INT_TEST_VALUE);

        when(mockGtfsDataRepo.addRoute(mockRoute)).thenThrow(new SQLIntegrityConstraintViolationException("route " +
                "must be unique in dataset"));

        final Exception exception = Assertions.assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> underTest.execute(mockParsedRoute));
        Assertions.assertEquals("route must be unique in dataset", exception.getMessage());

        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq("route_id"));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq("agency_id"));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq("route_short_name"));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq("route_long_name"));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq("route_desc"));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq("route_type"));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq("route_url"));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq("route_color"));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq("route_text_color"));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq("route_sort_order"));

        verify(mockGtfsDataRepo, times(1)).addRoute(ArgumentMatchers.isA(Route.class));

        verify(mockBuilder, times(1)).routeId(anyString());
        verify(mockBuilder, times(1)).agencyId(anyString());
        verify(mockBuilder, times(1)).routeShortName(anyString());
        verify(mockBuilder, times(1)).routeLongName(anyString());
        verify(mockBuilder, times(1)).routeDesc(anyString());
        verify(mockBuilder, times(1)).routeType(anyInt());
        verify(mockBuilder, times(1)).routeUrl(anyString());
        verify(mockBuilder, times(1)).routeColor(anyString());
        verify(mockBuilder, times(1)).routeTextColor(anyString());
        verify(mockBuilder, times(1)).routeSortOrder(anyInt());
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedRoute, times(1)).getEntityId();

        final ArgumentCaptor<EntityMustBeUniqueNotice> captor = ArgumentCaptor.forClass(EntityMustBeUniqueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<EntityMustBeUniqueNotice> noticeList = captor.getAllValues();

        assertEquals("routes.txt", noticeList.get(0).getFilename());
        assertEquals("route_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
    }
}