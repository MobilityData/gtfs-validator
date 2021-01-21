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
import org.mobilitydata.gtfsvalidator.notice.ForeignKeyError;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDate;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableLoader;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableLoader;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableLoader.SERVICE_ID_FIELD_NAME;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class GtfsTripServiceIdForeignKeyValidatorTest {
    @Mock
    final GtfsTripTableContainer mockTripTable = mock(GtfsTripTableContainer.class);
    @Mock
    final GtfsCalendarTableContainer mockCalendarTable = mock(GtfsCalendarTableContainer.class);
    @Mock
    final GtfsCalendarDateTableContainer mockCalendarDateTable = mock(GtfsCalendarDateTableContainer.class);

    @InjectMocks
    final GtfsTripServiceIdForeignKeyValidator underTest = new GtfsTripServiceIdForeignKeyValidator();

    @Before
    public void openMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void tripServiceIdInCalendarTableShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        GtfsTrip mockTrip = mock(GtfsTrip.class);
        when(mockTrip.serviceId()).thenReturn("service id value");
        List<GtfsTrip> tripCollection = new ArrayList<>();
        tripCollection.add(mockTrip);
        GtfsCalendar mockCalendar = mock(GtfsCalendar.class);
        when(mockTripTable.getEntities()).thenReturn(tripCollection);
        when(mockCalendarTable.byServiceId("service id value")).thenReturn(mockCalendar);

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        verify(mockCalendarTable, times(1)).byServiceId("service id value");
        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockTripTable, times(1)).getEntities();
    }

    @Test
    public void tripServiceIdInCalendarDateTableShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        GtfsTrip mockTrip = mock(GtfsTrip.class);
        when(mockTrip.serviceId()).thenReturn("service id value");
        List<GtfsTrip> tripCollection = new ArrayList<>();
        tripCollection.add(mockTrip);
        GtfsCalendarDate mockCalendarDate = mock(GtfsCalendarDate.class);
        when(mockTripTable.getEntities()).thenReturn(tripCollection);
        when(mockCalendarDateTable.byServiceId("service id value"))
                .thenReturn(Collections.singletonList(mockCalendarDate));

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        verify(mockCalendarTable, times(1)).byServiceId("service id value");
        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockTripTable, times(1)).getEntities();
    }

    @Test
    public void tripServiceIdNotInDataShouldGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        GtfsTrip mockTrip = mock(GtfsTrip.class);
        when(mockTrip.serviceId()).thenReturn("service id value");
        when(mockTrip.csvRowNumber()).thenReturn(2L);
        List<GtfsTrip> tripCollection = new ArrayList<>();
        tripCollection.add(mockTrip);
        when(mockTripTable.getEntities()).thenReturn(tripCollection);

        underTest.validate(mockNoticeContainer);

        ArgumentCaptor<ForeignKeyError> captor = ArgumentCaptor.forClass(ForeignKeyError.class);

        verify(mockNoticeContainer, times(1)).addNotice(captor.capture());
        ForeignKeyError notice = captor.getValue();
        assertThat(notice.getCode()).matches("foreign_key_error");
        assertThat(notice.getContext()).containsEntry("childFilename", GtfsCalendarDateTableLoader.FILENAME);
        assertThat(notice.getContext()).containsEntry("childFieldName", SERVICE_ID_FIELD_NAME);
        assertThat(notice.getContext()).containsEntry("parentFilename",
                GtfsCalendarTableLoader.FILENAME + " or " + GtfsCalendarDateTableLoader.FILENAME);
        assertThat(notice.getContext()).containsEntry("parentFieldName", GtfsCalendarTableLoader.SERVICE_ID_FIELD_NAME);
        assertThat(notice.getContext()).containsEntry("fieldValue", "service id value");
        assertThat(notice.getContext()).containsEntry("csvRowNumber", 2L);

        verify(mockCalendarTable, times(1)).byServiceId("service id value");
        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockTripTable, times(1)).getEntities();
    }
}
