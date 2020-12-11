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
import org.mobilitydata.gtfsvalidator.notice.InconsistentAgencyFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldError;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableLoader;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;

public class AgencyConsistencyValidatorTest {
    @Mock
    final GtfsAgencyTableContainer mockAgencyTable = mock(GtfsAgencyTableContainer.class);
    @InjectMocks
    final AgencyConsistencyValidator underTest = new AgencyConsistencyValidator();

    @Before
    public void openMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void noMoreThanTwoAgenciesShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        when(mockAgencyTable.entityCount()).thenReturn(1);

        underTest.validate(mockNoticeContainer);
        verifyNoInteractions(mockNoticeContainer);
    }

    @Test
    public void multipleAgenciesPresentButNoAgencyIdSetShouldGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        when(mockAgencyTable.entityCount()).thenReturn(2);
        List<GtfsAgency> agencyCollection = new ArrayList<>();
        GtfsAgency mockAgency0 = mock(GtfsAgency.class);
        when(mockAgency0.hasAgencyId()).thenReturn(true);
        when(mockAgency0.hasAgencyLang()).thenReturn(true);
        when(mockAgency0.agencyTimezone()).thenReturn(TimeZone.getDefault());
        when(mockAgency0.agencyLang()).thenReturn(Locale.forLanguageTag("en"));
        GtfsAgency mockAgency1 = mock(GtfsAgency.class);
        when(mockAgency1.hasAgencyId()).thenReturn(false);
        when(mockAgency1.hasAgencyLang()).thenReturn(true);
        when(mockAgency1.agencyTimezone()).thenReturn(TimeZone.getDefault());
        when(mockAgency1.agencyLang()).thenReturn(Locale.forLanguageTag("en"));
        when(mockAgency1.csvRowNumber()).thenReturn(1L);
        agencyCollection.add(mockAgency0);
        agencyCollection.add(mockAgency1);

        when(mockAgencyTable.gtfsFilename()).thenReturn("agency.txt");
        when(mockAgencyTable.getEntities()).thenReturn(agencyCollection);

        underTest.validate(mockNoticeContainer);

        ArgumentCaptor<MissingRequiredFieldError> captor =
                ArgumentCaptor.forClass(MissingRequiredFieldError.class);

        verify(mockNoticeContainer, times(1)).addNotice(captor.capture());
        MissingRequiredFieldError notice = captor.getValue();

        assertThat(notice.getCode()).matches("missing_required_field");
        assertThat(notice.getContext()).containsEntry("filename", "agency.txt");
        assertThat(notice.getContext()).containsEntry("csvRowNumber", 1L);
        assertThat(notice.getContext()).containsEntry("fieldName", GtfsAgencyTableLoader.AGENCY_ID_FIELD_NAME);
        verify(mockAgencyTable, times(1)).entityCount();
        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockAgencyTable, times(5)).getEntities();
        verify(mockAgencyTable, times(1)).gtfsFilename();
        verify(mockAgency1, times(1)).csvRowNumber();
        verify(mockAgency0, times(1)).hasAgencyId();
        verify(mockAgency0, times(1)).hasAgencyLang();
        verify(mockAgency0, times(1)).agencyTimezone();
        verify(mockAgency0, times(1)).agencyLang();
        verify(mockAgency1, times(1)).hasAgencyId();
        verify(mockAgency1, times(1)).agencyTimezone();
        verify(mockAgency1, times(2)).hasAgencyLang();
        verify(mockAgency1, times(1)).agencyLang();

        verifyNoMoreInteractions(mockAgencyTable, mockAgency0, mockAgency1, mockNoticeContainer);
    }

    @Test
    public void agenciesWithDifferentTimezoneShouldGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        when(mockAgencyTable.entityCount()).thenReturn(2);
        List<GtfsAgency> agencyCollection = new ArrayList<>();
        GtfsAgency mockAgency0 = mock(GtfsAgency.class);
        when(mockAgency0.hasAgencyId()).thenReturn(true);
        when(mockAgency0.hasAgencyLang()).thenReturn(true);
        when(mockAgency0.agencyTimezone()).thenReturn(TimeZone.getTimeZone("America/Bogota"));
        when(mockAgency0.agencyLang()).thenReturn(Locale.forLanguageTag("en"));
        GtfsAgency mockAgency1 = mock(GtfsAgency.class);
        when(mockAgency1.hasAgencyId()).thenReturn(true);
        when(mockAgency1.hasAgencyLang()).thenReturn(true);
        when(mockAgency1.agencyTimezone()).thenReturn(TimeZone.getTimeZone("America/Montreal"));
        when(mockAgency1.agencyLang()).thenReturn(Locale.forLanguageTag("en"));
        when(mockAgency1.csvRowNumber()).thenReturn(1L);
        agencyCollection.add(mockAgency0);
        agencyCollection.add(mockAgency1);

        when(mockAgencyTable.gtfsFilename()).thenReturn("agency.txt");
        when(mockAgencyTable.getEntities()).thenReturn(agencyCollection);

        underTest.validate(mockNoticeContainer);

        ArgumentCaptor<InconsistentAgencyFieldNotice> captor =
                ArgumentCaptor.forClass(InconsistentAgencyFieldNotice.class);

        verify(mockNoticeContainer, times(1)).addNotice(captor.capture());
        InconsistentAgencyFieldNotice notice = captor.getValue();

        assertThat(notice.getCode()).matches("inconsistent_agency_field");
        assertThat(notice.getContext()).containsEntry("csvRowNumber", 1L);
        assertThat(notice.getContext()).containsEntry("fieldName", GtfsAgencyTableLoader.AGENCY_TIMEZONE_FIELD_NAME);
        assertThat(notice.getContext()).containsEntry("expected", "America/Bogota");
        assertThat(notice.getContext()).containsEntry("actual", "America/Montreal");
        verify(mockAgencyTable, times(1)).entityCount();
        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockAgencyTable, times(5)).getEntities();
        verify(mockAgency1, times(1)).csvRowNumber();
        verify(mockAgency0, times(1)).hasAgencyId();
        verify(mockAgency0, times(1)).hasAgencyLang();
        verify(mockAgency0, times(1)).agencyTimezone();
        verify(mockAgency0, times(1)).agencyLang();
        verify(mockAgency1, times(1)).hasAgencyId();
        verify(mockAgency1, times(2)).agencyTimezone();
        verify(mockAgency1, times(2)).hasAgencyLang();
        verify(mockAgency1, times(1)).agencyLang();

        verifyNoMoreInteractions(mockAgencyTable, mockAgency0, mockAgency1, mockNoticeContainer);
    }

    @Test
    public void agenciesWithSameTimezoneShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        when(mockAgencyTable.entityCount()).thenReturn(2);
        List<GtfsAgency> agencyCollection = new ArrayList<>();
        GtfsAgency mockAgency0 = mock(GtfsAgency.class);
        when(mockAgency0.hasAgencyId()).thenReturn(true);
        when(mockAgency0.hasAgencyLang()).thenReturn(true);
        when(mockAgency0.agencyTimezone()).thenReturn(TimeZone.getTimeZone("America/Bogota"));
        when(mockAgency0.agencyLang()).thenReturn(Locale.forLanguageTag("en"));
        GtfsAgency mockAgency1 = mock(GtfsAgency.class);
        when(mockAgency1.hasAgencyId()).thenReturn(true);
        when(mockAgency1.hasAgencyLang()).thenReturn(true);
        when(mockAgency1.agencyTimezone()).thenReturn(TimeZone.getTimeZone("America/Bogota"));
        when(mockAgency1.agencyLang()).thenReturn(Locale.forLanguageTag("en"));
        when(mockAgency1.csvRowNumber()).thenReturn(1L);
        agencyCollection.add(mockAgency0);
        agencyCollection.add(mockAgency1);

        when(mockAgencyTable.gtfsFilename()).thenReturn("agency.txt");
        when(mockAgencyTable.getEntities()).thenReturn(agencyCollection);

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);

        verify(mockAgencyTable, times(1)).entityCount();
        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockAgencyTable, times(5)).getEntities();
        verify(mockAgency0, times(1)).hasAgencyId();
        verify(mockAgency0, times(1)).hasAgencyLang();
        verify(mockAgency0, times(1)).agencyTimezone();
        verify(mockAgency0, times(1)).agencyLang();
        verify(mockAgency1, times(1)).hasAgencyId();
        verify(mockAgency1, times(1)).agencyTimezone();
        verify(mockAgency1, times(2)).hasAgencyLang();
        verify(mockAgency1, times(1)).agencyLang();

        verifyNoMoreInteractions(mockAgencyTable, mockAgency0, mockAgency1, mockNoticeContainer);
    }

    @SuppressWarnings("EmptyMethod") // will be implemented later
    @Test
    public void agenciesWithDifferentLanguagesShouldNotGenerateNotice() {
        // TODO: implement after discussion on #549 and #560
    }

    @Test
    public void agenciesWithSameLanguagesShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        when(mockAgencyTable.entityCount()).thenReturn(2);
        List<GtfsAgency> agencyCollection = new ArrayList<>();
        GtfsAgency mockAgency0 = mock(GtfsAgency.class);
        when(mockAgency0.hasAgencyId()).thenReturn(true);
        when(mockAgency0.hasAgencyLang()).thenReturn(true);
        when(mockAgency0.agencyTimezone()).thenReturn(TimeZone.getTimeZone("America/Chicago"));
        when(mockAgency0.agencyLang()).thenReturn(Locale.forLanguageTag("en"));
        GtfsAgency mockAgency1 = mock(GtfsAgency.class);
        when(mockAgency1.hasAgencyId()).thenReturn(true);
        when(mockAgency1.hasAgencyLang()).thenReturn(true);
        when(mockAgency1.agencyTimezone()).thenReturn(TimeZone.getTimeZone("America/Chicago"));
        when(mockAgency1.agencyLang()).thenReturn(Locale.forLanguageTag("en"));
        when(mockAgency1.csvRowNumber()).thenReturn(1L);
        agencyCollection.add(mockAgency0);
        agencyCollection.add(mockAgency1);

        when(mockAgencyTable.gtfsFilename()).thenReturn("agency.txt");
        when(mockAgencyTable.getEntities()).thenReturn(agencyCollection);

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);

        verify(mockAgencyTable, times(1)).entityCount();
        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockAgencyTable, times(5)).getEntities();
        verify(mockAgency0, times(1)).hasAgencyId();
        verify(mockAgency0, times(1)).hasAgencyLang();
        verify(mockAgency0, times(1)).agencyTimezone();
        verify(mockAgency0, times(1)).agencyLang();
        verify(mockAgency1, times(1)).hasAgencyId();
        verify(mockAgency1, times(1)).agencyTimezone();
        verify(mockAgency1, times(2)).hasAgencyLang();
        verify(mockAgency1, times(1)).agencyLang();

        verifyNoMoreInteractions(mockAgencyTable, mockAgency0, mockAgency1, mockNoticeContainer);
    }
}
