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
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SameNameAndDescriptionForRouteNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mockito.ArgumentCaptor;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DifferentRouteNameAndDescriptionValidatorTest {

    @Test
    public void equalRouteShortNameAndRouteDescShouldGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        GtfsRoute mockRoute = mock(GtfsRoute.class);
        when(mockRoute.hasRouteDesc()).thenReturn(true);
        when(mockRoute.hasRouteShortName()).thenReturn(true);
        when(mockRoute.routeDesc()).thenReturn("duplicate value");
        when(mockRoute.routeShortName()).thenReturn("duplicate value");
        when(mockRoute.routeId()).thenReturn("route id value");
        when(mockRoute.csvRowNumber()).thenReturn(3L);

        DifferentRouteNameAndDescriptionValidator underTest = new DifferentRouteNameAndDescriptionValidator();

        underTest.validate(mockRoute, mockNoticeContainer);

        ArgumentCaptor<SameNameAndDescriptionForRouteNotice> captor =
                ArgumentCaptor.forClass(SameNameAndDescriptionForRouteNotice.class);

        verify(mockNoticeContainer, times(1)).addNotice(captor.capture());
        SameNameAndDescriptionForRouteNotice notice = captor.getValue();
        assertThat(notice.getCode()).matches("same_route_name_and_description");
        assertThat(notice.getContext()).containsEntry("filename", "routes.txt");
        assertThat(notice.getContext()).containsEntry("routeId", "route id value");
        assertThat(notice.getContext()).containsEntry("csvRowNumber", 3L);
        assertThat(notice.getContext()).containsEntry("routeDesc", "duplicate value");
        assertThat(notice.getContext()).containsEntry("specifiedField", "route_short_name");

        verify(mockRoute, times(1)).hasRouteDesc();
        verify(mockRoute, times(1)).routeDesc();
        verify(mockRoute, times(1)).routeId();
        verify(mockRoute, times(1)).hasRouteShortName();
        verify(mockRoute, times(1)).routeShortName();
        verify(mockRoute, times(1)).csvRowNumber();
        verifyNoMoreInteractions(mockRoute);
    }

    @Test
    public void equalRouteLongNameAndRouteDescShouldGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        GtfsRoute mockRoute = mock(GtfsRoute.class);
        when(mockRoute.hasRouteDesc()).thenReturn(true);
        when(mockRoute.hasRouteShortName()).thenReturn(false);
        when(mockRoute.hasRouteLongName()).thenReturn(true);
        when(mockRoute.routeDesc()).thenReturn("duplicate value");
        when(mockRoute.routeLongName()).thenReturn("duplicate value");
        when(mockRoute.routeId()).thenReturn("route id value");
        when(mockRoute.csvRowNumber()).thenReturn(3L);

        DifferentRouteNameAndDescriptionValidator underTest = new DifferentRouteNameAndDescriptionValidator();

        underTest.validate(mockRoute, mockNoticeContainer);

        ArgumentCaptor<SameNameAndDescriptionForRouteNotice> captor =
                ArgumentCaptor.forClass(SameNameAndDescriptionForRouteNotice.class);

        verify(mockNoticeContainer, times(1)).addNotice(captor.capture());
        SameNameAndDescriptionForRouteNotice notice = captor.getValue();
        assertThat(notice.getCode()).matches("same_route_name_and_description");
        assertThat(notice.getContext()).containsEntry("filename", "routes.txt");
        assertThat(notice.getContext()).containsEntry("routeId", "route id value");
        assertThat(notice.getContext()).containsEntry("csvRowNumber", 3L);
        assertThat(notice.getContext()).containsEntry("routeDesc", "duplicate value");
        assertThat(notice.getContext()).containsEntry("specifiedField", "route_long_name");

        verify(mockRoute, times(1)).hasRouteDesc();
        verify(mockRoute, times(1)).routeDesc();
        verify(mockRoute, times(1)).routeId();
        verify(mockRoute, times(1)).hasRouteShortName();
        verify(mockRoute, times(1)).hasRouteLongName();
        verify(mockRoute, times(1)).routeLongName();
        verify(mockRoute, times(1)).csvRowNumber();
        verifyNoMoreInteractions(mockRoute);
    }

    @Test
    public void noLongNameDifferentRouteShortNameAndRouteDescShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        GtfsRoute mockRoute = mock(GtfsRoute.class);
        when(mockRoute.hasRouteDesc()).thenReturn(true);
        when(mockRoute.hasRouteShortName()).thenReturn(true);
        when(mockRoute.hasRouteLongName()).thenReturn(false);
        when(mockRoute.routeDesc()).thenReturn("route desc");
        when(mockRoute.routeShortName()).thenReturn("route short name");
        when(mockRoute.routeId()).thenReturn("route id value");

        DifferentRouteNameAndDescriptionValidator underTest = new DifferentRouteNameAndDescriptionValidator();

        underTest.validate(mockRoute, mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        verify(mockRoute, times(1)).hasRouteDesc();
        verify(mockRoute, times(1)).routeShortName();
        verify(mockRoute, times(1)).hasRouteLongName();
        verify(mockRoute, times(1)).routeDesc();
        verify(mockRoute, times(1)).routeId();
        verify(mockRoute, times(1)).hasRouteShortName();
        verifyNoMoreInteractions(mockRoute);
    }

    @Test
    public void noShortNameDifferentRouteLongNameAndRouteDescShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        GtfsRoute mockRoute = mock(GtfsRoute.class);
        when(mockRoute.hasRouteDesc()).thenReturn(true);
        when(mockRoute.hasRouteShortName()).thenReturn(false);
        when(mockRoute.hasRouteLongName()).thenReturn(true);
        when(mockRoute.routeDesc()).thenReturn("route desc");
        when(mockRoute.routeLongName()).thenReturn("route long name");
        when(mockRoute.routeId()).thenReturn("route id value");

        DifferentRouteNameAndDescriptionValidator underTest = new DifferentRouteNameAndDescriptionValidator();

        underTest.validate(mockRoute, mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        verify(mockRoute, times(1)).hasRouteDesc();
        verify(mockRoute, times(1)).routeLongName();
        verify(mockRoute, times(1)).hasRouteLongName();
        verify(mockRoute, times(1)).routeDesc();
        verify(mockRoute, times(1)).routeId();
        verify(mockRoute, times(1)).hasRouteShortName();
        verifyNoMoreInteractions(mockRoute);
    }

    @Test
    public void allNamesProvidedAndDifferentFromRouteDescShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        GtfsRoute mockRoute = mock(GtfsRoute.class);
        when(mockRoute.hasRouteDesc()).thenReturn(true);
        when(mockRoute.hasRouteShortName()).thenReturn(true);
        when(mockRoute.hasRouteLongName()).thenReturn(true);
        when(mockRoute.routeDesc()).thenReturn("route desc");
        when(mockRoute.routeShortName()).thenReturn("route short name");
        when(mockRoute.routeLongName()).thenReturn("route long name");
        when(mockRoute.routeId()).thenReturn("route id value");

        DifferentRouteNameAndDescriptionValidator underTest = new DifferentRouteNameAndDescriptionValidator();

        underTest.validate(mockRoute, mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        verify(mockRoute, times(1)).hasRouteDesc();
        verify(mockRoute, times(1)).routeShortName();
        verify(mockRoute, times(1)).routeLongName();
        verify(mockRoute, times(1)).hasRouteLongName();
        verify(mockRoute, times(1)).routeDesc();
        verify(mockRoute, times(1)).routeId();
        verify(mockRoute, times(1)).hasRouteShortName();
        verifyNoMoreInteractions(mockRoute);
    }

    @Test
    public void equalRouteShortNameRouteLongNameAndRouteDescShouldGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        GtfsRoute mockRoute = mock(GtfsRoute.class);
        when(mockRoute.hasRouteDesc()).thenReturn(true);
        when(mockRoute.hasRouteShortName()).thenReturn(true);
        when(mockRoute.hasRouteLongName()).thenReturn(true);
        when(mockRoute.routeDesc()).thenReturn("duplicate value");
        when(mockRoute.routeShortName()).thenReturn("duplicate value");
        when(mockRoute.routeLongName()).thenReturn("duplicate value");
        when(mockRoute.routeId()).thenReturn("route id value");
        when(mockRoute.csvRowNumber()).thenReturn(3L);

        DifferentRouteNameAndDescriptionValidator underTest = new DifferentRouteNameAndDescriptionValidator();

        underTest.validate(mockRoute, mockNoticeContainer);

        ArgumentCaptor<SameNameAndDescriptionForRouteNotice> captor =
                ArgumentCaptor.forClass(SameNameAndDescriptionForRouteNotice.class);

        verify(mockNoticeContainer, times(1)).addNotice(captor.capture());
        SameNameAndDescriptionForRouteNotice notice = captor.getValue();
        assertThat(notice.getCode()).matches("same_route_name_and_description");
        assertThat(notice.getContext()).containsEntry("filename", "routes.txt");
        assertThat(notice.getContext()).containsEntry("routeId", "route id value");
        assertThat(notice.getContext()).containsEntry("csvRowNumber", 3L);
        assertThat(notice.getContext()).containsEntry("routeDesc", "duplicate value");
        assertThat(notice.getContext()).containsEntry("specifiedField", "route_short_name");

        verify(mockRoute, times(1)).hasRouteDesc();
        verify(mockRoute, times(1)).routeDesc();
        verify(mockRoute, times(1)).routeId();
        verify(mockRoute, times(1)).hasRouteShortName();
        verify(mockRoute, times(1)).routeShortName();
        verify(mockRoute, times(1)).csvRowNumber();
        verifyNoMoreInteractions(mockRoute);
    }
}
