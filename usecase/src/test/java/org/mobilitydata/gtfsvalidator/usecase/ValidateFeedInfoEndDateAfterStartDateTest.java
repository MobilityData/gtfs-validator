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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FeedInfoStartDateAfterEndDateNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ValidateFeedInfoEndDateAfterStartDateTest {

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void feedInfoWithStartDateBeforeEndDateShouldNotGenerateNotice() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        final LocalDate mockStartDate = mock(LocalDate.class);
        final LocalDate mockEndDate = mock(LocalDate.class);
        when(mockFeedInfo.getFeedStartDate()).thenReturn(mockStartDate);
        when(mockFeedInfo.getFeedEndDate()).thenReturn(mockEndDate);
        when(mockStartDate.isBefore(mockEndDate)).thenReturn(true);

        final Map<String, FeedInfo> mockFeedInfoCollection = new HashMap<>();
        mockFeedInfoCollection.put("feed publisher name", mockFeedInfo);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getFeedInfoAll()).thenReturn(mockFeedInfoCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateFeedInfoEndDateAfterStartDate underTest =
                new ValidateFeedInfoEndDateAfterStartDate(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();
        verify(mockLogger, times(1)).info("Validating rule 'E037 - `feed_start_date`" +
                " and `feed_end_date` out of order");

        verify(mockDataRepo, times(1)).getFeedInfoAll();

        verify(mockFeedInfo, times(1)).getFeedEndDate();
        verify(mockFeedInfo, times(1)).getFeedStartDate();

        verify(mockStartDate, times(1)).isBefore(mockEndDate);
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockFeedInfo, mockStartDate, mockEndDate);
    }

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void feedInfoWithStartDateAfterEndDateShouldGenerateNotice() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        final LocalDate mockStartDate = mock(LocalDate.class);
        when(mockStartDate.toString()).thenReturn("start date");
        final LocalDate mockEndDate = mock(LocalDate.class);
        when(mockEndDate.toString()).thenReturn("end date");
        when(mockFeedInfo.getFeedStartDate()).thenReturn(mockStartDate);
        when(mockFeedInfo.getFeedEndDate()).thenReturn(mockEndDate);
        when(mockFeedInfo.getFeedPublisherUrl()).thenReturn("feed publisher url");
        when(mockFeedInfo.getFeedLang()).thenReturn("feed lang");
        when(mockStartDate.isBefore(mockEndDate)).thenReturn(false);

        final Map<String, FeedInfo> mockFeedInfoCollection = new HashMap<>();
        mockFeedInfoCollection.put("feed publisher name", mockFeedInfo);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getFeedInfoAll()).thenReturn(mockFeedInfoCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateFeedInfoEndDateAfterStartDate underTest =
                new ValidateFeedInfoEndDateAfterStartDate(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();
        verify(mockLogger, times(1)).info("Validating rule 'E037 - `feed_start_date`" +
                " and `feed_end_date` out of order");

        verify(mockDataRepo, times(1)).getFeedInfoAll();

        verify(mockFeedInfo, times(1)).getFeedEndDate();
        verify(mockFeedInfo, times(1)).getFeedStartDate();
        verify(mockFeedInfo, times(1)).getFeedPublisherUrl();
        verify(mockFeedInfo, times(1)).getFeedLang();
        verify(mockStartDate, times(1)).isBefore(mockEndDate);

        final ArgumentCaptor<FeedInfoStartDateAfterEndDateNotice> captor =
                ArgumentCaptor.forClass(FeedInfoStartDateAfterEndDateNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<FeedInfoStartDateAfterEndDateNotice> noticeList = captor.getAllValues();

        assertEquals("feed_info.txt", noticeList.get(0).getFilename());
        assertEquals("start date", noticeList.get(0).getNoticeSpecific(Notice.KEY_FEED_INFO_START_DATE));
        assertEquals("end date", noticeList.get(0).getNoticeSpecific(Notice.KEY_FEED_INFO_END_DATE));
        assertEquals("feed_publisher_name", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("feed_publisher_url", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("feed_lang", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_THIRD_PART));
        assertEquals("feed publisher name", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals("feed publisher url", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals("feed lang", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_THIRD_VALUE));
        assertEquals(String.format("`feed_end_date`: `%s` precedes `feed_start_date`: `%s` in file `%s`" +
                        " for entity with composite id: `%s`: `%s` -- `%s`: `%s` -- `%s`: `%s`.",
                "end date", "start date", "feed_info.txt", "feed_publisher_name", "feed publisher name",
                "feed_publisher_url", "feed publisher url", "feed_lang", "feed lang"), noticeList.get(0).getDescription());
        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo, mockFeedInfo, mockStartDate, mockEndDate);
    }

    // suppressed warning regarding ignored result of method since it is not necessary here
    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void feedInfoWithEndDateAndNullStartDateShouldNotGenerateNotice() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        final LocalDate mockEndDate = mock(LocalDate.class);
        when(mockFeedInfo.getFeedStartDate()).thenReturn(null);
        when(mockFeedInfo.getFeedEndDate()).thenReturn(mockEndDate);

        final Map<String, FeedInfo> mockFeedInfoCollection = new HashMap<>();
        mockFeedInfoCollection.put("feed publisher name", mockFeedInfo);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getFeedInfoAll()).thenReturn(mockFeedInfoCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateFeedInfoEndDateAfterStartDate underTest =
                new ValidateFeedInfoEndDateAfterStartDate(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();
        verify(mockLogger, times(1)).info("Validating rule 'E037 - `feed_start_date`" +
                " and `feed_end_date` out of order");

        verify(mockDataRepo, times(1)).getFeedInfoAll();

        verify(mockFeedInfo, times(1)).getFeedStartDate();
        verify(mockFeedInfo, times(1)).getFeedEndDate();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockFeedInfo, mockEndDate);
    }

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void feedInfoWithStartDateButWithoutEndDateShouldNotGenerateNotice() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        final LocalDate mockStartDate = mock(LocalDate.class);
        when(mockFeedInfo.getFeedStartDate()).thenReturn(mockStartDate);
        when(mockFeedInfo.getFeedEndDate()).thenReturn(null);

        final Map<String, FeedInfo> mockFeedInfoCollection = new HashMap<>();
        mockFeedInfoCollection.put("feed publisher name", mockFeedInfo);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getFeedInfoAll()).thenReturn(mockFeedInfoCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateFeedInfoEndDateAfterStartDate underTest =
                new ValidateFeedInfoEndDateAfterStartDate(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();
        verify(mockLogger, times(1)).info("Validating rule 'E037 - `feed_start_date`" +
                " and `feed_end_date` out of order");

        verify(mockDataRepo, times(1)).getFeedInfoAll();
        verify(mockFeedInfo, times(1)).getFeedStartDate();
        verify(mockFeedInfo, times(1)).getFeedEndDate();

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo, mockFeedInfo, mockStartDate);
    }
}
