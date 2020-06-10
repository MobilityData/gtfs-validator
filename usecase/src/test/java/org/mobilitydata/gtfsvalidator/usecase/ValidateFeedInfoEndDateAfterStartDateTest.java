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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FeedInfoStartDateAfterEndDateNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ValidateFeedInfoEndDateAfterStartDateTest {

    @Test
    void feedInfoWithStartDateBeforeEndDateShouldNotGenerateNotice() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        final LocalDateTime mockStartDate = mock(LocalDateTime.class);
        final LocalDateTime mockEndDate = mock(LocalDateTime.class);
        when(mockFeedInfo.getStartDate()).thenReturn(mockStartDate);
        when(mockFeedInfo.getFeedEndDate()).thenReturn(mockEndDate);
        when(mockStartDate.isBefore(mockEndDate)).thenReturn(false);

        final Collection<FeedInfo> mockFeedInfoCollection = new ArrayList<>();
        mockFeedInfoCollection.add(mockFeedInfo);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getFeedInfoAll()).thenReturn(mockFeedInfoCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateFeedInfoEndDateAfterStartDate underTest =
                new ValidateFeedInfoEndDateAfterStartDate(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();
        verify(mockLogger, times(1)).info("Validating rule 'E032 - `feed_start_date`" +
        " and `feed_end_date` out of order" + System.lineSeparator());

        verify(mockDataRepo, times(1)).getFeedInfoAll();

        // suppressed warning regarding ignored result of method since it is not necessary here
        //noinspection ResultOfMethodCallIgnored
        verify(mockFeedInfo, times(2)).getFeedEndDate();
        // suppressed warning regarding ignored result of method since it is not necessary here
        //noinspection ResultOfMethodCallIgnored
        verify(mockFeedInfo, times(2)).getStartDate();

        verify(mockStartDate, times(1)).isBefore(mockEndDate);

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo, mockFeedInfo, mockStartDate, mockEndDate);
    }

    @Test
    void feedInfoWithStartDateAfterEndDateShouldGenerateNotice() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        final LocalDateTime mockStartDate = mock(LocalDateTime.class);
        when(mockStartDate.toString()).thenReturn("start date");
        final LocalDateTime mockEndDate = mock(LocalDateTime.class);
        when(mockEndDate.toString()).thenReturn("end date");
        when(mockFeedInfo.getStartDate()).thenReturn(mockStartDate);
        when(mockFeedInfo.getFeedEndDate()).thenReturn(mockEndDate);
        when(mockStartDate.isBefore(mockEndDate)).thenReturn(true);

        final Collection<FeedInfo> mockFeedInfoCollection = new ArrayList<>();
        mockFeedInfoCollection.add(mockFeedInfo);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getFeedInfoAll()).thenReturn(mockFeedInfoCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateFeedInfoEndDateAfterStartDate underTest =
                new ValidateFeedInfoEndDateAfterStartDate(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();
        verify(mockLogger, times(1)).info("Validating rule 'E032 - `feed_start_date`" +
                " and `feed_end_date` out of order" + System.lineSeparator());

        verify(mockDataRepo, times(1)).getFeedInfoAll();

        // suppressed warning regarding ignored result of method since it is not necessary here
        //noinspection ResultOfMethodCallIgnored
        verify(mockFeedInfo, times(3)).getFeedEndDate();
        // suppressed warning regarding ignored result of method since it is not necessary here
        //noinspection ResultOfMethodCallIgnored
        verify(mockFeedInfo, times(3)).getStartDate();
        // suppressed warning regarding ignored result of method since it is not necessary here
        //noinspection ResultOfMethodCallIgnored
        verify(mockFeedInfo, times(1)).getFeedPublisherName();

        verify(mockStartDate, times(1)).isBefore(mockEndDate);

        final ArgumentCaptor<FeedInfoStartDateAfterEndDateNotice> captor =
                ArgumentCaptor.forClass(FeedInfoStartDateAfterEndDateNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<FeedInfoStartDateAfterEndDateNotice> noticeList = captor.getAllValues();

        assertEquals("feed_info.txt", noticeList.get(0).getFilename());
        assertEquals("start date", noticeList.get(0).getStartDate());
        assertEquals("end date", noticeList.get(0).getEndDate());
        assertEquals("no id", noticeList.get(0).getEntityId());
        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo, mockFeedInfo, mockStartDate, mockEndDate);
    }

    @Test
    void feedInfoWithEndDateButWithoutStartDateShouldNotGenerateFeedInfoStartDateAfterEndDateNotice() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        final LocalDateTime mockEndDate = mock(LocalDateTime.class);
        when(mockFeedInfo.getStartDate()).thenReturn(null);
        when(mockFeedInfo.getFeedEndDate()).thenReturn(mockEndDate);

        final Collection<FeedInfo> mockFeedInfoCollection = new ArrayList<>();
        mockFeedInfoCollection.add(mockFeedInfo);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getFeedInfoAll()).thenReturn(mockFeedInfoCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateFeedInfoEndDateAfterStartDate underTest =
                new ValidateFeedInfoEndDateAfterStartDate(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();
        verify(mockLogger, times(1)).info("Validating rule 'E032 - `feed_start_date`" +
                " and `feed_end_date` out of order" + System.lineSeparator());

        verify(mockDataRepo, times(1)).getFeedInfoAll();

        // suppressed warning regarding ignored result of method since it is not necessary here
        //noinspection ResultOfMethodCallIgnored
        verify(mockFeedInfo, times(1)).getStartDate();

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo, mockFeedInfo, mockEndDate);
    }

    @Test
    void feedInfoWithStartButWithoutEndDateShouldNotGenerateFeedInfoStartDateAfterEndDateNotice(){
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        final LocalDateTime mockStartDate = mock(LocalDateTime.class);
        when(mockFeedInfo.getStartDate()).thenReturn(mockStartDate);
        when(mockFeedInfo.getFeedEndDate()).thenReturn(null);

        final Collection<FeedInfo> mockFeedInfoCollection = new ArrayList<>();
        mockFeedInfoCollection.add(mockFeedInfo);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getFeedInfoAll()).thenReturn(mockFeedInfoCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateFeedInfoEndDateAfterStartDate underTest =
                new ValidateFeedInfoEndDateAfterStartDate(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();
        verify(mockLogger, times(1)).info("Validating rule 'E032 - `feed_start_date`" +
                " and `feed_end_date` out of order" + System.lineSeparator());

        verify(mockDataRepo, times(1)).getFeedInfoAll();

        // suppressed warning regarding ignored result of method since it is not necessary here
        //noinspection ResultOfMethodCallIgnored
        verify(mockFeedInfo, times(1)).getStartDate();
        // suppressed warning regarding ignored result of method since it is not necessary here
        //noinspection ResultOfMethodCallIgnored
        verify(mockFeedInfo, times(1)).getFeedEndDate();

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo, mockFeedInfo, mockStartDate);
    }
}
