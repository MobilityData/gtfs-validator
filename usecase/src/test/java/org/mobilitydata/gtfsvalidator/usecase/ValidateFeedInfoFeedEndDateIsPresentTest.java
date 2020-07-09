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

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.FeedInfo;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.MissingFeedEndDateNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ValidateFeedInfoFeedEndDateIsPresentTest {

    // suppressed warning regarding ignored result of method since it is not necessary here
    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void feedInfoWithNullFeedEndDateAndNonNullFeedStartDateShouldGenerateNotice() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        final LocalDate mockDate = mock(LocalDate.class);
        when(mockFeedInfo.getFeedPublisherUrl()).thenReturn("feed publisher url");
        when(mockFeedInfo.getFeedLang()).thenReturn("feed lang");
        when(mockFeedInfo.getFeedStartDate()).thenReturn(mockDate);
        when(mockFeedInfo.getFeedEndDate()).thenReturn(null);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, FeedInfo> mockFeedInfoCollection = new HashMap<>();
        mockFeedInfoCollection.put("feed publisher name", mockFeedInfo);

        when(mockDataRepo.getFeedInfoAll()).thenReturn(mockFeedInfoCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateFeedInfoFeedEndDateIsPresent underTest =
                new ValidateFeedInfoFeedEndDateIsPresent(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule W010 - `feed_end_date` should be" +
                " provided if `feed_start_date` is provided" + System.lineSeparator());

        verify(mockDataRepo, times(1)).getFeedInfoAll();

        final ArgumentCaptor<MissingFeedEndDateNotice> captor = ArgumentCaptor.forClass(MissingFeedEndDateNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());
        verify(mockFeedInfo, times(1)).getFeedStartDate();
        verify(mockFeedInfo, times(1)).getFeedEndDate();
        verify(mockFeedInfo, times(1)).getFeedPublisherUrl();
        verify(mockFeedInfo, times(1)).getFeedLang();

        final List<MissingFeedEndDateNotice> noticeList = captor.getAllValues();

        assertEquals("feed_info.txt", noticeList.get(0).getFilename());
        assertEquals("feed_end_date", noticeList.get(0).getNoticeSpecific(Notice.KEY_FIELD_NAME));
        assertEquals("feed_publisher_name", noticeList.get(0).getNoticeSpecific(
                Notice.KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("feed_publisher_url", noticeList.get(0).getNoticeSpecific(
                Notice.KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("feed_lang", noticeList.get(0).getNoticeSpecific(
                Notice.KEY_COMPOSITE_KEY_THIRD_PART));
        assertEquals("feed publisher name", noticeList.get(0).getNoticeSpecific(
                Notice.KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals("feed publisher url", noticeList.get(0).getNoticeSpecific(
                Notice.KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals("feed lang", noticeList.get(0).getNoticeSpecific(
                Notice.KEY_COMPOSITE_KEY_THIRD_VALUE));

        verifyNoMoreInteractions(mockDataRepo, mockDate, mockResultRepo, mockFeedInfo, mockLogger);
    }

    // suppressed warning regarding ignored result of method since it is not necessary here
    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void feedInfoWithNonNullFeedEndDateAndNonNullFeedStartDateShouldNotGenerateNotice() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        final LocalDate mockDate = mock(LocalDate.class);
        when(mockFeedInfo.getFeedPublisherName()).thenReturn("feed publisher name");
        when(mockFeedInfo.getFeedStartDate()).thenReturn(mockDate);
        when(mockFeedInfo.getFeedEndDate()).thenReturn(mockDate);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, FeedInfo> mockFeedInfoCollection = new HashMap<>();
        mockFeedInfoCollection.put("feed publisher name", mockFeedInfo);

        when(mockDataRepo.getFeedInfoAll()).thenReturn(mockFeedInfoCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateFeedInfoFeedEndDateIsPresent underTest =
                new ValidateFeedInfoFeedEndDateIsPresent(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule W010 - `feed_end_date` should be" +
                " provided if `feed_start_date` is provided" + System.lineSeparator());

        verify(mockDataRepo, times(1)).getFeedInfoAll();
        verify(mockFeedInfo, times(1)).getFeedStartDate();
        verify(mockFeedInfo, times(1)).getFeedEndDate();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockDataRepo, mockDate, mockFeedInfo, mockLogger);
    }

    // suppressed warning regarding ignored result of method since it is not necessary here
    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void feedInfoWithoutValueForDateFieldsShouldNotGenerateNotice() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        when(mockFeedInfo.getFeedPublisherName()).thenReturn("entity id");
        when(mockFeedInfo.getFeedStartDate()).thenReturn(null);
        when(mockFeedInfo.getFeedEndDate()).thenReturn(null);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, FeedInfo> mockFeedInfoCollection = new HashMap<>();
        mockFeedInfoCollection.put("feed publisher name", mockFeedInfo);

        when(mockDataRepo.getFeedInfoAll()).thenReturn(mockFeedInfoCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateFeedInfoFeedEndDateIsPresent underTest =
                new ValidateFeedInfoFeedEndDateIsPresent(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule W010 - `feed_end_date` should be" +
                " provided if `feed_start_date` is provided" + System.lineSeparator());

        verify(mockDataRepo, times(1)).getFeedInfoAll();
        verify(mockFeedInfo, times(1)).getFeedStartDate();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockDataRepo, mockFeedInfo, mockLogger);
    }
}
