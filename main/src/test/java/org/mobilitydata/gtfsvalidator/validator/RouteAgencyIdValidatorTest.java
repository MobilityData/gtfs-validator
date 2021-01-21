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

import org.junit.Before;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldError;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.table.GtfsRouteTableLoader.AGENCY_ID_FIELD_NAME;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class RouteAgencyIdValidatorTest {
    @Mock
    final GtfsAgencyTableContainer mockAgencyTable = mock(GtfsAgencyTableContainer.class);
    @Mock
    final GtfsRouteTableContainer mockRouteTable = mock(GtfsRouteTableContainer.class);

    @InjectMocks
    final RouteAgencyIdValidator underTest = new RouteAgencyIdValidator();

    @Before
    public void openMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void onlyOneAgencyInDatasetShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        when(mockAgencyTable.entityCount()).thenReturn(1);

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer, mockRouteTable);
        verify(mockAgencyTable, times(1)).entityCount();
        verifyNoMoreInteractions(mockAgencyTable);
    }

    @Test
    public void undefinedRouteAgencyIdShouldGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        when(mockAgencyTable.entityCount()).thenReturn(2);
        when(mockRouteTable.gtfsFilename()).thenReturn("routes.txt");

        GtfsRoute mockRoute0 = mock(GtfsRoute.class);
        when(mockRoute0.hasAgencyId()).thenReturn(true);
        GtfsRoute mockRoute1 = mock(GtfsRoute.class);
        when(mockRoute1.hasAgencyId()).thenReturn(true);
        GtfsRoute mockRoute2 = mock(GtfsRoute.class);
        when(mockRoute2.hasAgencyId()).thenReturn(false);
        when(mockRoute2.csvRowNumber()).thenReturn(3L);

        List<GtfsRoute> routeCollection = new ArrayList<>();
        routeCollection.add(mockRoute0);
        routeCollection.add(mockRoute1);
        routeCollection.add(mockRoute2);
        when(mockRouteTable.getEntities()).thenReturn(routeCollection);

        underTest.validate(mockNoticeContainer);

        ArgumentCaptor<MissingRequiredFieldError> captor =
                ArgumentCaptor.forClass(MissingRequiredFieldError.class);

        verify(mockNoticeContainer, times(1)).addValidationNotice(captor.capture());
        MissingRequiredFieldError notice = captor.getValue();
        assertThat(notice.getCode()).matches("missing_required_field");
        assertThat(notice.getContext()).containsEntry("filename", "routes.txt");
        assertThat(notice.getContext()).containsEntry("csvRowNumber", 3L);
        assertThat(notice.getContext()).containsEntry("fieldName", AGENCY_ID_FIELD_NAME);

        verify(mockAgencyTable, times(1)).entityCount();
        verify(mockRoute0, times(1)).hasAgencyId();
        verify(mockRoute1, times(1)).hasAgencyId();
        verify(mockRoute2, times(1)).hasAgencyId();
        verify(mockRouteTable, times(1)).gtfsFilename();
        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockRouteTable, times(1)).getEntities();
        verify(mockRoute2, times(1)).csvRowNumber();

        verifyNoMoreInteractions(mockAgencyTable, mockRouteTable, mockRoute0, mockRoute1, mockRoute2);
    }

    @Test
    public void definedRouteAgencyIdShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        when(mockAgencyTable.entityCount()).thenReturn(3);
        when(mockRouteTable.gtfsFilename()).thenReturn("routes.txt");

        GtfsRoute mockRoute0 = mock(GtfsRoute.class);
        when(mockRoute0.hasAgencyId()).thenReturn(true);
        GtfsRoute mockRoute1 = mock(GtfsRoute.class);
        when(mockRoute1.hasAgencyId()).thenReturn(true);
        GtfsRoute mockRoute2 = mock(GtfsRoute.class);
        when(mockRoute2.hasAgencyId()).thenReturn(true);

        List<GtfsRoute> routeCollection = new ArrayList<>();
        routeCollection.add(mockRoute0);
        routeCollection.add(mockRoute1);
        routeCollection.add(mockRoute2);
        when(mockRouteTable.getEntities()).thenReturn(routeCollection);

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        verify(mockAgencyTable, times(1)).entityCount();
        verify(mockRoute0, times(1)).hasAgencyId();
        verify(mockRoute1, times(1)).hasAgencyId();
        verify(mockRoute2, times(1)).hasAgencyId();
        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockRouteTable, times(1)).getEntities();

        verifyNoMoreInteractions(mockAgencyTable, mockRouteTable, mockRoute0, mockRoute1, mockRoute2);
    }
}
