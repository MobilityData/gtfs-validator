/*
 * Copyright 2020 Google LLC, MobilityData IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.validator;

import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.RouteColorContrastNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mockito.ArgumentCaptor;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class RouteColorContrastValidatorTest {

    @Test
    public void noRouteColorShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        GtfsRoute mockRoute = mock(GtfsRoute.class);
        when(mockRoute.hasRouteColor()).thenReturn(false);

        RouteColorContrastValidator underTest = new RouteColorContrastValidator();

        underTest.validate(mockRoute, mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        verify(mockRoute, times(1)).hasRouteColor();
        verifyNoMoreInteractions(mockRoute);
    }

    @Test
    public void noRouteTextColorShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        GtfsRoute mockRoute = mock(GtfsRoute.class);
        when(mockRoute.hasRouteColor()).thenReturn(true);
        when(mockRoute.hasRouteTextColor()).thenReturn(false);

        RouteColorContrastValidator underTest = new RouteColorContrastValidator();

        underTest.validate(mockRoute, mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        verify(mockRoute, times(1)).hasRouteColor();
        verify(mockRoute, times(1)).hasRouteTextColor();
        verifyNoMoreInteractions(mockRoute);
    }

    @Test
    public void contrastingRouteColorAndRouteTextColorShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        GtfsRoute mockRoute = mock(GtfsRoute.class);
        when(mockRoute.hasRouteColor()).thenReturn(true);
        when(mockRoute.hasRouteTextColor()).thenReturn(true);
        GtfsColor mockColor = mock(GtfsColor.class);
        when(mockColor.rec601Luma()).thenReturn(100, 20);
        when(mockRoute.routeTextColor()).thenReturn(mockColor);
        when(mockRoute.routeColor()).thenReturn(mockColor);

        RouteColorContrastValidator underTest = new RouteColorContrastValidator();

        underTest.validate(mockRoute, mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        verify(mockRoute, times(1)).hasRouteColor();
        verify(mockRoute, times(1)).hasRouteTextColor();
        verify(mockRoute, times(1)).routeColor();
        verify(mockRoute, times(1)).routeTextColor();
        verify(mockColor, times(2)).rec601Luma();
        verifyNoMoreInteractions(mockRoute, mockColor);
    }

    @Test
    public void nonContrastingRouteColorAndRouteTextColorShouldGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        GtfsRoute mockRoute = mock(GtfsRoute.class);
        when(mockRoute.hasRouteColor()).thenReturn(true);
        when(mockRoute.hasRouteTextColor()).thenReturn(true);
        when(mockRoute.routeId()).thenReturn("route id value");
        when(mockRoute.csvRowNumber()).thenReturn(2L);
        GtfsColor mockColor = mock(GtfsColor.class);
        when(mockColor.rec601Luma()).thenReturn(100, 80);
        when(mockColor.toHtmlColor()).thenReturn("route color value", "route text color value");
        when(mockRoute.routeTextColor()).thenReturn(mockColor);
        when(mockRoute.routeColor()).thenReturn(mockColor);

        RouteColorContrastValidator underTest = new RouteColorContrastValidator();

        underTest.validate(mockRoute, mockNoticeContainer);

        final ArgumentCaptor<RouteColorContrastNotice> captor =
                ArgumentCaptor.forClass(RouteColorContrastNotice.class);

        verify(mockNoticeContainer, times(1)).addValidationNotice(captor.capture());
        RouteColorContrastNotice notice = captor.getValue();
        assertThat(notice.getCode()).matches("route_color_contrast");
        assertThat(notice.getContext()).containsEntry("routeId","route id value" );
        assertThat(notice.getContext()).containsEntry("csvRowNumber", 2L);
        assertThat(notice.getContext()).containsEntry("routeColor", "route color value");
        assertThat(notice.getContext()).containsEntry("routeTextColor", "route text color value");

        verify(mockRoute, times(1)).routeId();
        verify(mockRoute, times(1)).csvRowNumber();
        verify(mockRoute, times(1)).hasRouteColor();
        verify(mockRoute, times(1)).hasRouteTextColor();
        verify(mockRoute, times(2)).routeColor();
        verify(mockRoute, times(2)).routeTextColor();
        verify(mockColor, times(2)).rec601Luma();
        verify(mockColor, times(2)).toHtmlColor();
        verifyNoMoreInteractions(mockRoute, mockColor);
    }
}
