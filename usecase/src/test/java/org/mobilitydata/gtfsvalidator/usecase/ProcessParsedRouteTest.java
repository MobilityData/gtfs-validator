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

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProcessParsedRouteTest {

    private static final String ROUTE_ID = "route_id";
    private static final String AGENCY_ID = "agency_id";
    private static final String ROUTE_SHORT_NAME = "route_short_name";
    private static final String ROUTE_LONG_NAME = "route_long_name";
    private static final String ROUTE_DESC = "route_desc";
    private static final String ROUTE_TYPE = "route_type";
    private static final String ROUTE_URL = "route_url";
    private static final String ROUTE_COLOR = "route_color";
    private static final String ROUTE_TEXT_COLOR = "route_text_color";
    private static final String ROUTE_SORT_ORDER = "route_sort_order";
    private final String STRING_TEST_VALUE = "test_value";
    private final int INT_TEST_VALUE = 0;

    @Test
    void validatedParsedRouteShouldCreateRouteEntityAndBeAddedToGtfsDataRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Route.RouteBuilder mockBuilder = mock(Route.RouteBuilder.class, RETURNS_SELF);
        final Route mockRoute = mock(Route.class);
        final ParsedEntity mockParsedRoute = mock(ParsedEntity.class);
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(List.class);
        final var mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(mockRoute);
        when(mockGenericObject.isSuccess()).thenReturn(true);

        when(mockBuilder.build(mockNoticeCollection)).thenReturn(mockGenericObject);

        when(mockParsedRoute.get(ROUTE_ID)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(AGENCY_ID)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_SHORT_NAME)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_LONG_NAME)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_DESC)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_TYPE)).thenReturn(INT_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_URL)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_COLOR)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_TEXT_COLOR)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_SORT_ORDER)).thenReturn(INT_TEST_VALUE);

        when(mockGtfsDataRepo.addRoute(mockRoute)).thenReturn(mockRoute);

        final ProcessParsedRoute underTest = new ProcessParsedRoute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        underTest.execute(mockParsedRoute, mockNoticeCollection);

        final InOrder inOrder = inOrder(mockBuilder, mockGtfsDataRepo, mockParsedRoute);

        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_ID));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(AGENCY_ID));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_SHORT_NAME));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_LONG_NAME));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_DESC));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_TYPE));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_URL));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_COLOR));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_TEXT_COLOR));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_SORT_ORDER));

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

        inOrder.verify(mockBuilder, times(1)).build(mockNoticeCollection);
        inOrder.verify(mockGtfsDataRepo, times(1)).addRoute(ArgumentMatchers.eq(mockRoute));

        verifyNoMoreInteractions(mockBuilder, mockResultRepo, mockGtfsDataRepo, mockParsedRoute);
    }

    @Test
    public void nullRouteIdShouldAddNoticeToResultRepoAndShouldNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Route.RouteBuilder mockBuilder = mock(Route.RouteBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedRoute = mock(ParsedEntity.class);
        final List<Notice> noticeCollection = new ArrayList<>();
        final MissingRequiredValueNotice mockNotice = mock(MissingRequiredValueNotice.class);
        noticeCollection.add(mockNotice);
        final var mockGenericType = mock(EntityBuildResult.class);

        when(mockGenericType.isSuccess()).thenReturn(false);
        when(mockGenericType.getData()).thenReturn(noticeCollection);

        when(mockBuilder.build(noticeCollection)).thenReturn(mockGenericType);

        final ProcessParsedRoute underTest = new ProcessParsedRoute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        when(mockParsedRoute.get(ROUTE_ID)).thenReturn(null);
        when(mockParsedRoute.get(AGENCY_ID)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_SHORT_NAME)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_LONG_NAME)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_DESC)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_TYPE)).thenReturn(INT_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_URL)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_COLOR)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_TEXT_COLOR)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_SORT_ORDER)).thenReturn(INT_TEST_VALUE);

        underTest.execute(mockParsedRoute, noticeCollection);

        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_ID));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(AGENCY_ID));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_SHORT_NAME));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_LONG_NAME));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_DESC));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_TYPE));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_URL));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_COLOR));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_TEXT_COLOR));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_SORT_ORDER));

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
        verify(mockBuilder, times(1)).build(noticeCollection);

        verify(mockResultRepo, times(1)).addNotice(isA(Notice.class));
        verifyNoMoreInteractions(mockParsedRoute, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void invalidRouteTypeShouldAddNoticeToResultRepoAndShouldNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Route.RouteBuilder mockBuilder = mock(Route.RouteBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedRoute = mock(ParsedEntity.class);
        final List<Notice> noticeCollection = new ArrayList<>();
        final Notice mockNotice = mock(Notice.class);
        noticeCollection.add(mockNotice);
        final var mockGeneticType = mock(EntityBuildResult.class);
        when(mockGeneticType.getData()).thenReturn(noticeCollection);
        when(mockGeneticType.isSuccess()).thenReturn(false);

        when(mockBuilder.build(noticeCollection)).thenReturn(mockGeneticType);

        final ProcessParsedRoute underTest = new ProcessParsedRoute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        when(mockParsedRoute.get(ROUTE_ID)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(AGENCY_ID)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_SHORT_NAME)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_LONG_NAME)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_DESC)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_TYPE)).thenReturn(15);
        when(mockParsedRoute.get(ROUTE_URL)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_COLOR)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_TEXT_COLOR)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_SORT_ORDER)).thenReturn(INT_TEST_VALUE);

        underTest.execute(mockParsedRoute, noticeCollection);

        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_ID));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(AGENCY_ID));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_SHORT_NAME));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_LONG_NAME));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_DESC));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_TYPE));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_URL));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_COLOR));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_TEXT_COLOR));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_SORT_ORDER));

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
        verify(mockBuilder, times(1)).build(noticeCollection);

        verify(mockResultRepo, times(1)).addNotice(isA(Notice.class));
        verifyNoMoreInteractions(mockParsedRoute, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void duplicateRouteShouldAddNoticeToResultRepoAndShouldNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Route.RouteBuilder mockBuilder = mock(Route.RouteBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedRoute = mock(ParsedEntity.class);
        final Route mockRoute = mock(Route.class);
        final List<Notice> noticeCollection = new ArrayList<>();
        final var mockGenericObject = mock(EntityBuildResult.class);
        when(mockGenericObject.isSuccess()).thenReturn(true);
        when(mockGenericObject.getData()).thenReturn(mockRoute);

        when(mockRoute.getRouteId()).thenReturn(STRING_TEST_VALUE);
        when(mockBuilder.build(noticeCollection)).thenReturn(mockGenericObject);
        when(mockGtfsDataRepo.addRoute(mockRoute)).thenReturn(null);

        final ProcessParsedRoute underTest = new ProcessParsedRoute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        when(mockParsedRoute.get(ROUTE_ID)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(AGENCY_ID)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_SHORT_NAME)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_LONG_NAME)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_DESC)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_TYPE)).thenReturn(7);
        when(mockParsedRoute.get(ROUTE_URL)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_COLOR)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_TEXT_COLOR)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedRoute.get(ROUTE_SORT_ORDER)).thenReturn(INT_TEST_VALUE);

        underTest.execute(mockParsedRoute, noticeCollection);

        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_ID));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(AGENCY_ID));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_SHORT_NAME));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_LONG_NAME));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_DESC));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_TYPE));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_URL));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_COLOR));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_TEXT_COLOR));
        verify(mockParsedRoute, times(1)).get(ArgumentMatchers.eq(ROUTE_SORT_ORDER));

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedRoute, times(1)).getEntityId();

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
        verify(mockBuilder, times(1)).build(noticeCollection);

        verify(mockParsedRoute, times(1)).getEntityId();

        final ArgumentCaptor<DuplicatedEntityNotice> captor = ArgumentCaptor.forClass(DuplicatedEntityNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DuplicatedEntityNotice> noticeList = captor.getAllValues();

        assertEquals("routes.txt", noticeList.get(0).getFilename());
        assertEquals(ROUTE_ID, noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockBuilder, mockGtfsDataRepo, mockResultRepo, mockParsedRoute, mockRoute);
    }
}