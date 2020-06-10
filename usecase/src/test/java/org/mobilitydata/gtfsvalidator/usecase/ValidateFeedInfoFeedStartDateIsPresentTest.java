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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.MissingFeedStartDateNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ValidateFeedInfoFeedStartDateIsPresentTest {

    @Test
    void feedInfoWithNullFeedEndDateAndNonNullFeedStartDateShouldNotGenerateNotice() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        final LocalDateTime mockDate = mock(LocalDateTime.class);
        when(mockFeedInfo.getFeedPublisherName()).thenReturn("entity id");
        when(mockFeedInfo.getStartDate()).thenReturn(mockDate);
        when(mockFeedInfo.getFeedEndDate()).thenReturn(null);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Collection<FeedInfo> mockFeedInfoCollection = new ArrayList<>();
        mockFeedInfoCollection.add(mockFeedInfo);
        when(mockDataRepo.getFeedInfoAll()).thenReturn(mockFeedInfoCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateFeedInfoFeedStartDateIsPresent underTest =
                new ValidateFeedInfoFeedStartDateIsPresent(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule W011 - `feed_start_date` should be"+
                " provided if `feed_end_date` is provided" + System.lineSeparator());

        verify(mockDataRepo, times(1)).getFeedInfoAll();

        // suppressed warning regarding ignored result of method since it is not necessary here
        //noinspection ResultOfMethodCallIgnored
        verify(mockFeedInfo, times(1)).getStartDate();
        // suppressed warning regarding ignored result of method since it is not necessary here
        //noinspection ResultOfMethodCallIgnored
        verify(mockFeedInfo, times(1)).getFeedEndDate();

        final ArgumentCaptor<MissingFeedStartDateNotice> captor =
                ArgumentCaptor.forClass(MissingFeedStartDateNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());
        // suppressed warning regarding ignored result of method since it is not necessary here
        //noinspection ResultOfMethodCallIgnored
        verify(mockFeedInfo, times(1)).getFeedPublisherName();

        final List<MissingFeedStartDateNotice> noticeList = captor.getAllValues();

        assertEquals("feed_info.txt", noticeList.get(0).getFilename());
        assertEquals("feed_start_date", noticeList.get(0).getFieldName());
        assertEquals("entity id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockDataRepo, mockDate, mockFeedInfo, mockLogger, mockResultRepo);
    }

    @Test
    void feedInfoWithNonNullFeedEndDateAndNonNullFeedStartDateShouldNoteGenerateNotice() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        final LocalDateTime mockDate = mock(LocalDateTime.class);
        when(mockFeedInfo.getFeedPublisherName()).thenReturn("entity id");
        when(mockFeedInfo.getStartDate()).thenReturn(mockDate);
        when(mockFeedInfo.getFeedEndDate()).thenReturn(mockDate);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Collection<FeedInfo> mockFeedInfoCollection = new ArrayList<>();
        mockFeedInfoCollection.add(mockFeedInfo);
        when(mockDataRepo.getFeedInfoAll()).thenReturn(mockFeedInfoCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateFeedInfoFeedStartDateIsPresent underTest =
                new ValidateFeedInfoFeedStartDateIsPresent(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule W011 - `feed_start_date` should be" +
                " provided if `feed_end_date` is provided" + System.lineSeparator());

        verify(mockDataRepo, times(1)).getFeedInfoAll();
        // suppressed warning regarding ignored result of method since it is not necessary here
        //noinspection ResultOfMethodCallIgnored
        verify(mockFeedInfo, times(1)).getStartDate();
        // suppressed warning regarding ignored result of method since it is not necessary here
        //noinspection ResultOfMethodCallIgnored
        verify(mockFeedInfo, times(1)).getFeedEndDate();

        verifyNoMoreInteractions(mockDataRepo, mockDate, mockFeedInfo, mockLogger, mockResultRepo);
    }

    @Test
    void feedInfoWithoutValueForDateFieldsShouldNotGenerateNotice() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        when(mockFeedInfo.getFeedPublisherName()).thenReturn("entity id");
        when(mockFeedInfo.getStartDate()).thenReturn(null);
        when(mockFeedInfo.getFeedEndDate()).thenReturn(null);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Collection<FeedInfo> mockFeedInfoCollection = new ArrayList<>();
        mockFeedInfoCollection.add(mockFeedInfo);
        when(mockDataRepo.getFeedInfoAll()).thenReturn(mockFeedInfoCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateFeedInfoFeedStartDateIsPresent underTest =
                new ValidateFeedInfoFeedStartDateIsPresent(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule W011 - `feed_start_date` should be"+
                " provided if `feed_end_date` is provided" + System.lineSeparator());

        verify(mockDataRepo, times(1)).getFeedInfoAll();
        // suppressed warning regarding ignored result of method since it is not necessary here
        //noinspection ResultOfMethodCallIgnored
        verify(mockFeedInfo, times(1)).getStartDate();

        verifyNoMoreInteractions(mockDataRepo, mockFeedInfo, mockLogger, mockResultRepo);
    }
}
