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
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.FeedInfoLangAndAgencyLangMismatchNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class MatchingFeedAndAgencyLangValidatorTest {
    @Mock
    final GtfsFeedInfoTableContainer mockFeedInfoTable = mock(GtfsFeedInfoTableContainer.class);
    @Mock
    final GtfsAgencyTableContainer mockAgencyTable = mock(GtfsAgencyTableContainer.class);
    @InjectMocks
    final MatchingFeedAndAgencyLangValidator underTest = new MatchingFeedAndAgencyLangValidator();

    @Before
    public void openMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void feedInfoFileNotProvidedShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        when(mockFeedInfoTable.isEmptyFile()).thenReturn(true);

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer, mockAgencyTable);
        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockFeedInfoTable, times(1)).isEmptyFile();
    }

    @Test
    public void mulFeedLangAndNoMoreThanOneAgencyLangShouldGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        when(mockFeedInfoTable.isEmptyFile()).thenReturn(false);

        GtfsFeedInfo mockFeedInfo = mock(GtfsFeedInfo.class);
        when(mockFeedInfo.feedLang()).thenReturn(Locale.forLanguageTag("mul"));
        List<GtfsFeedInfo> feedInfoCollection = new ArrayList<>();
        feedInfoCollection.add(mockFeedInfo);
        when(mockFeedInfoTable.getEntities()).thenReturn(feedInfoCollection);

        GtfsAgency mockAgency = mock(GtfsAgency.class);
        when(mockAgency.agencyLang()).thenReturn(Locale.forLanguageTag("fr"));
        List<GtfsAgency> agencyCollection = new ArrayList<>();
        agencyCollection.add(mockAgency);
        when(mockAgencyTable.getEntities()).thenReturn(agencyCollection);

        underTest.validate(mockNoticeContainer);

        final ArgumentCaptor<FeedInfoLangAndAgencyLangMismatchNotice> captor =
                ArgumentCaptor.forClass(FeedInfoLangAndAgencyLangMismatchNotice.class);

        verify(mockNoticeContainer, times(1)).addNotice(captor.capture());

        FeedInfoLangAndAgencyLangMismatchNotice notice = captor.getValue();
        assertThat(notice.getCode()).matches("feed_info_lang_and_agency_lang_mismatch");
        assertThat(notice.getContext()).containsEntry("feedInfoLang", "mul");
        assertThat(notice.getContext()).containsEntry("agencyLangCollection", new HashSet<>(Collections.singletonList("fra")));
    }

    @Test
    public void feedLangNotMulAndMoreThanOneAgencyLangShouldGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        when(mockFeedInfoTable.isEmptyFile()).thenReturn(false);

        GtfsFeedInfo mockFeedInfo = mock(GtfsFeedInfo.class);
        when(mockFeedInfo.feedLang()).thenReturn(Locale.forLanguageTag("fr"));
        List<GtfsFeedInfo> feedInfoCollection = new ArrayList<>();
        feedInfoCollection.add(mockFeedInfo);
        when(mockFeedInfoTable.getEntities()).thenReturn(feedInfoCollection);

        GtfsAgency mockAgency0 = mock(GtfsAgency.class);
        GtfsAgency mockAgency1 = mock(GtfsAgency.class);
        when(mockAgency0.agencyLang()).thenReturn(Locale.forLanguageTag("fr"));
        when(mockAgency1.agencyLang()).thenReturn(Locale.forLanguageTag("en"));
        List<GtfsAgency> agencyCollection = new ArrayList<>();
        agencyCollection.add(mockAgency0);
        agencyCollection.add(mockAgency1);
        when(mockAgencyTable.getEntities()).thenReturn(agencyCollection);

        underTest.validate(mockNoticeContainer);

        final ArgumentCaptor<FeedInfoLangAndAgencyLangMismatchNotice> captor =
                ArgumentCaptor.forClass(FeedInfoLangAndAgencyLangMismatchNotice.class);

        verify(mockNoticeContainer, times(1)).addNotice(captor.capture());

        FeedInfoLangAndAgencyLangMismatchNotice notice = captor.getValue();
        assertThat(notice.getCode()).matches("feed_info_lang_and_agency_lang_mismatch");
        assertThat(notice.getContext()).containsEntry("feedInfoLang", "fra");
        assertThat(notice.getContext()).containsEntry("agencyLangCollection", new HashSet<>(Arrays.asList("fra", "eng")));
    }

    @Test
    public void feedLangNotMulAndOnlyOneMatchingAgencyLangShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        when(mockFeedInfoTable.isEmptyFile()).thenReturn(false);

        GtfsFeedInfo mockFeedInfo = mock(GtfsFeedInfo.class);
        when(mockFeedInfo.feedLang()).thenReturn(Locale.forLanguageTag("fr"));
        List<GtfsFeedInfo> feedInfoCollection = new ArrayList<>();
        feedInfoCollection.add(mockFeedInfo);
        when(mockFeedInfoTable.getEntities()).thenReturn(feedInfoCollection);

        GtfsAgency mockAgency0 = mock(GtfsAgency.class);
        when(mockAgency0.agencyLang()).thenReturn(Locale.forLanguageTag("fr"));
        List<GtfsAgency> agencyCollection = new ArrayList<>();
        agencyCollection.add(mockAgency0);
        when(mockAgencyTable.getEntities()).thenReturn(agencyCollection);

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockFeedInfoTable, times(1)).isEmptyFile();
        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockFeedInfoTable, times(1)).getEntities();
        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockAgencyTable, times(2)).getEntities();
        verify(mockFeedInfo, times(1)).feedLang();
        verify(mockAgency0, times(2)).agencyLang();

        verifyNoMoreInteractions(mockFeedInfoTable, mockFeedInfo, mockAgencyTable, mockAgency0);
    }

    @Test
    public void feedLangNotMulAndOnlyOneMismatchingAgencyLangShouldGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        when(mockFeedInfoTable.isEmptyFile()).thenReturn(false);

        GtfsFeedInfo mockFeedInfo = mock(GtfsFeedInfo.class);
        when(mockFeedInfo.feedLang()).thenReturn(Locale.forLanguageTag("fr"));
        List<GtfsFeedInfo> feedInfoCollection = new ArrayList<>();
        feedInfoCollection.add(mockFeedInfo);
        when(mockFeedInfoTable.getEntities()).thenReturn(feedInfoCollection);

        GtfsAgency mockAgency0 = mock(GtfsAgency.class);
        when(mockAgency0.agencyLang()).thenReturn(Locale.forLanguageTag("en"));
        List<GtfsAgency> agencyCollection = new ArrayList<>();
        agencyCollection.add(mockAgency0);
        when(mockAgencyTable.getEntities()).thenReturn(agencyCollection);

        underTest.validate(mockNoticeContainer);

        final ArgumentCaptor<FeedInfoLangAndAgencyLangMismatchNotice> captor =
                ArgumentCaptor.forClass(FeedInfoLangAndAgencyLangMismatchNotice.class);

        verify(mockNoticeContainer, times(1)).addNotice(captor.capture());

        FeedInfoLangAndAgencyLangMismatchNotice notice = captor.getValue();
        assertThat(notice.getCode()).matches("feed_info_lang_and_agency_lang_mismatch");
        assertThat(notice.getContext()).containsEntry("feedInfoLang", "fra");
        assertThat(notice.getContext()).containsEntry("agencyLangCollection",
                new HashSet<>(Collections.singletonList("eng")));
    }

    @Test
    public void matchingFeedInfoFeedLangShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        when(mockFeedInfoTable.isEmptyFile()).thenReturn(false);

        GtfsFeedInfo mockFeedInfo = mock(GtfsFeedInfo.class);
        when(mockFeedInfo.feedLang()).thenReturn(Locale.forLanguageTag("fr"));
        List<GtfsFeedInfo> feedInfoCollection = new ArrayList<>();
        feedInfoCollection.add(mockFeedInfo);
        when(mockFeedInfoTable.getEntities()).thenReturn(feedInfoCollection);

        GtfsAgency mockAgency0 = mock(GtfsAgency.class);
        when(mockAgency0.agencyLang()).thenReturn(Locale.forLanguageTag("fr"));
        GtfsAgency mockAgency1 = mock(GtfsAgency.class);
        when(mockAgency1.agencyLang()).thenReturn(Locale.forLanguageTag("fr"));
        List<GtfsAgency> agencyCollection = new ArrayList<>();
        agencyCollection.add(mockAgency0);
        agencyCollection.add(mockAgency1);
        when(mockAgencyTable.getEntities()).thenReturn(agencyCollection);

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockFeedInfoTable, times(1)).isEmptyFile();
        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockFeedInfoTable, times(1)).getEntities();
        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockAgencyTable, times(2)).getEntities();
        verify(mockFeedInfo, times(1)).feedLang();
        verify(mockAgency0, times(2)).agencyLang();
        verify(mockAgency1, times(2)).agencyLang();

        verifyNoMoreInteractions(mockFeedInfoTable, mockFeedInfo, mockAgencyTable, mockAgency0, mockAgency1);
    }

    @Test
    public void feedLangNotMulAndMultipleNonMatchingAgencyLangShouldGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        when(mockFeedInfoTable.isEmptyFile()).thenReturn(false);

        GtfsFeedInfo mockFeedInfo = mock(GtfsFeedInfo.class);
        when(mockFeedInfo.feedLang()).thenReturn(Locale.forLanguageTag("fr"));
        List<GtfsFeedInfo> feedInfoCollection = new ArrayList<>();
        feedInfoCollection.add(mockFeedInfo);
        when(mockFeedInfoTable.getEntities()).thenReturn(feedInfoCollection);

        GtfsAgency mockAgency0 = mock(GtfsAgency.class);
        when(mockAgency0.agencyLang()).thenReturn(Locale.forLanguageTag("en"));
        GtfsAgency mockAgency1 = mock(GtfsAgency.class);
        when(mockAgency1.agencyLang()).thenReturn(Locale.forLanguageTag("es"));
        List<GtfsAgency> agencyCollection = new ArrayList<>();
        agencyCollection.add(mockAgency0);
        agencyCollection.add(mockAgency1);
        when(mockAgencyTable.getEntities()).thenReturn(agencyCollection);

        underTest.validate(mockNoticeContainer);

        final ArgumentCaptor<FeedInfoLangAndAgencyLangMismatchNotice> captor =
                ArgumentCaptor.forClass(FeedInfoLangAndAgencyLangMismatchNotice.class);

        verify(mockNoticeContainer, times(1)).addNotice(captor.capture());

        FeedInfoLangAndAgencyLangMismatchNotice notice = captor.getValue();
        assertThat(notice.getCode()).matches("feed_info_lang_and_agency_lang_mismatch");
        assertThat(notice.getContext()).containsEntry("feedInfoLang", "fra");
        assertThat(notice.getContext()).containsEntry("agencyLangCollection",
                new HashSet<>(Arrays.asList("spa", "eng")));
    }

    @Test
    public void mulFeedLandAndMoreThanOneAgencyShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        when(mockFeedInfoTable.isEmptyFile()).thenReturn(false);

        GtfsFeedInfo mockFeedInfo = mock(GtfsFeedInfo.class);
        when(mockFeedInfo.feedLang()).thenReturn(Locale.forLanguageTag("mul"));
        List<GtfsFeedInfo> feedInfoCollection = new ArrayList<>();
        feedInfoCollection.add(mockFeedInfo);
        when(mockFeedInfoTable.getEntities()).thenReturn(feedInfoCollection);

        GtfsAgency mockAgency0 = mock(GtfsAgency.class);
        when(mockAgency0.agencyLang()).thenReturn(Locale.forLanguageTag("fr"));
        GtfsAgency mockAgency1 = mock(GtfsAgency.class);
        when(mockAgency1.agencyLang()).thenReturn(Locale.forLanguageTag("en"));
        List<GtfsAgency> agencyCollection = new ArrayList<>();
        agencyCollection.add(mockAgency0);
        agencyCollection.add(mockAgency1);
        when(mockAgencyTable.getEntities()).thenReturn(agencyCollection);

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockFeedInfoTable, times(1)).isEmptyFile();
        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockFeedInfoTable, times(1)).getEntities();
        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockAgencyTable, times(1)).getEntities();
        verify(mockFeedInfo, times(1)).feedLang();
        verify(mockAgency0, times(1)).agencyLang();
        verify(mockAgency1, times(1)).agencyLang();

        verifyNoMoreInteractions(mockFeedInfoTable, mockFeedInfo, mockAgencyTable, mockAgency0, mockAgency1);
    }
}
