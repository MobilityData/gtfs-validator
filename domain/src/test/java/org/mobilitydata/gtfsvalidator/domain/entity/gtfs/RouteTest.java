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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class RouteTest {

    private static final String STRING_TEST_VALUE = "test_value";
    private static final int INT_TEST_VALUE = 0;

    // Field routeId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @SuppressWarnings("ConstantConditions")
    @Test
    public void createRouteWithNullRouteIdShouldGenerateMissingRequiredValueNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Route.RouteBuilder underTest = new Route.RouteBuilder(mockNoticeCollection);

        underTest.routeId(null)
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(INT_TEST_VALUE)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(INT_TEST_VALUE);

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("routes.txt", noticeList.get(0).getFilename());
        assertEquals("route_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertTrue(entityBuildResult.getData() instanceof List);

        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createRouteWithInvalidRouteTypeShouldGenerateUnexpectedEnumValueNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Route.RouteBuilder underTest = new Route.RouteBuilder(mockNoticeCollection);

        underTest.routeId(STRING_TEST_VALUE)
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(15)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(INT_TEST_VALUE);

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<UnexpectedEnumValueNotice> captor =
                ArgumentCaptor.forClass(UnexpectedEnumValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<UnexpectedEnumValueNotice> noticeList = captor.getAllValues();

        assertEquals("routes.txt", noticeList.get(0).getFilename());
        assertEquals("route_type", noticeList.get(0).getFieldName());
        assertEquals(STRING_TEST_VALUE, noticeList.get(0).getEntityId());
        assertEquals("15", noticeList.get(0).getEnumValue());
        assertTrue(entityBuildResult.getData() instanceof List);

        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createRouteWithValidValuesForFieldShouldNotGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Route.RouteBuilder underTest = new Route.RouteBuilder(mockNoticeCollection);

        underTest.routeId(STRING_TEST_VALUE)
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(INT_TEST_VALUE)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(INT_TEST_VALUE);

        final EntityBuildResult<?> entityBuildResult = underTest.build();
        verify(mockNoticeCollection, times(1)).clear();

        assertTrue(entityBuildResult.getData() instanceof Route);
        verifyNoMoreInteractions(mockNoticeCollection);
    }
}