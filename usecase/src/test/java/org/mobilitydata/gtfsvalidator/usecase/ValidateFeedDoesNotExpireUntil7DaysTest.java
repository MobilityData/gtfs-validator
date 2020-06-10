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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FeedInfoExpiresInLessThan7DaysNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ValidateFeedDoesNotExpireUntil7DaysTest {

    @Test
    void feedInfoExpiringInLessThan7DaysShouldGenerateNotice() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        final LocalDateTime mockDate = mock(LocalDateTime.class);
        when(mockFeedInfo.getEndDate()).thenReturn(mockDate);
        when(mockDate.isBefore(any())).thenReturn(true);
        when(mockDate.toString()).thenReturn("feed end date");
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Collection<FeedInfo> mockFeedInfoCollection = new ArrayList<>();
        mockFeedInfoCollection.add(mockFeedInfo);
        when(mockGtfsDataRepo.getFeedInfoAll()).thenReturn(mockFeedInfoCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final LocalDateTime currentDate = LocalDateTime.now();
        final LocalDateTime currentDateAsYYYYMMDDHHMM = LocalDateTime.of(
                currentDate.getYear(), currentDate.getMonthValue(), currentDate.getDayOfMonth(), 0, 0);

        final ValidateFeedDoesNotExpireUntil7Days underTest =
                new ValidateFeedDoesNotExpireUntil7Days(mockGtfsDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1))
                .info("Validating rule 'E033 - Dataset should be valid for at least the next 7 days'"
                        + System.lineSeparator());

        verify(mockGtfsDataRepo, times(1)).getFeedInfoAll();

        // suppressed warning regarding ignored result of method since it is not necessary here
        //noinspection ResultOfMethodCallIgnored
        verify(mockFeedInfo, times(3)).getEndDate();

        verify(mockDate, times(1)).isBefore(currentDateAsYYYYMMDDHHMM.plusDays(7));
        final ArgumentCaptor<FeedInfoExpiresInLessThan7DaysNotice> captor =
                ArgumentCaptor.forClass(FeedInfoExpiresInLessThan7DaysNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());
        // suppressed warning regarding ignored result of method since it is not necessary here
        //noinspection ResultOfMethodCallIgnored
        verify(mockFeedInfo, times(1)).getFeedPublisherName();

        final List<FeedInfoExpiresInLessThan7DaysNotice> noticeList = captor.getAllValues();

        assertEquals("feed_info.txt", noticeList.get(0).getFilename());
        assertEquals("feed_end_date", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals("feed end date", noticeList.get(0).getFeedEndDate());
        assertEquals(currentDateAsYYYYMMDDHHMM.toString(), noticeList.get(0).getCurrentDate());

        verifyNoMoreInteractions(mockDate, mockFeedInfo, mockResultRepo, mockGtfsDataRepo, mockLogger);
    }

    @Test
    void feedInfoWithExpirationDateInMoreThan60DaysShouldNotGenerateNotice() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        final LocalDateTime mockDate = mock(LocalDateTime.class);
        when(mockFeedInfo.getEndDate()).thenReturn(mockDate);
        when(mockDate.isBefore(any())).thenReturn(false);
        when(mockDate.toString()).thenReturn("feed end date");
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Collection<FeedInfo> mockFeedInfoCollection = new ArrayList<>();
        mockFeedInfoCollection.add(mockFeedInfo);
        when(mockGtfsDataRepo.getFeedInfoAll()).thenReturn(mockFeedInfoCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final LocalDateTime currentDate = LocalDateTime.now();
        final LocalDateTime currentDateAsYYYYMMDDHHMM = LocalDateTime.of(
                currentDate.getYear(), currentDate.getMonthValue(), currentDate.getDayOfMonth(), 0, 0);

        final ValidateFeedDoesNotExpireUntil7Days underTest =
                new ValidateFeedDoesNotExpireUntil7Days(mockGtfsDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1))
                .info("Validating rule 'E033 - Dataset should be valid for at least the next 7 days'"
                        + System.lineSeparator());

        verify(mockGtfsDataRepo, times(1)).getFeedInfoAll();

        // suppressed warning regarding ignored result of method since it is not necessary here
        //noinspection ResultOfMethodCallIgnored
        verify(mockFeedInfo, times(2)).getEndDate();
        verify(mockDate, times(1)).isBefore(currentDateAsYYYYMMDDHHMM.plusDays(7));

        verifyNoMoreInteractions(mockDate, mockFeedInfo, mockResultRepo, mockGtfsDataRepo, mockLogger);
    }
}
