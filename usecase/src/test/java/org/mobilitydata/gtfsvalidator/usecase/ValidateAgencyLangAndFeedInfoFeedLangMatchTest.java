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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Agency;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.FeedInfo;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FeedInfoLangAgencyLangMismatchNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.*;
import static org.mockito.Mockito.*;

class ValidateAgencyLangAndFeedInfoFeedLangMatchTest {

    @Test
    void feedInfoFileNotProvidedShouldNotGenerateNotice() {
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getFeedInfoAll()).thenReturn(new HashMap<>());
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateAgencyLangAndFeedInfoFeedLangMatch underTest =
                new ValidateAgencyLangAndFeedInfoFeedLangMatch(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule 'E055 - Mismatching feed and " +
                "agency language fields'");

        verify(mockDataRepo, times(1)).getFeedInfoAll();
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockDataRepo, mockLogger);
    }

    // suppressed warning regarding ignored result of method, since the method is called in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void matchingFeedInfoFeedLangShouldNotGenerateNotice() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        when(mockFeedInfo.getFeedLang()).thenReturn("common language");

        final Agency mockAgency0 = mock(Agency.class);
        when(mockAgency0.getAgencyLang()).thenReturn("common language");
        final Agency mockAgency1 = mock(Agency.class);
        when(mockAgency1.getAgencyLang()).thenReturn("common language");

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getFeedInfoAll()).thenReturn(new HashMap<>(Map.of("feed publisher name", mockFeedInfo)));
        when(mockDataRepo.getAgencyAll()).thenReturn(new HashMap<>(Map.of("agency id 0", mockAgency0,
                "agency id 1", mockAgency1)));
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateAgencyLangAndFeedInfoFeedLangMatch underTest =
                new ValidateAgencyLangAndFeedInfoFeedLangMatch(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule 'E055 - Mismatching feed and " +
                "agency language fields'");

        verify(mockDataRepo, times(2)).getFeedInfoAll();
        verify(mockDataRepo, times(2)).getAgencyAll();

        verify(mockFeedInfo, times(1)).getFeedLang();

        verify(mockAgency0, times(2)).getAgencyLang();
        verify(mockAgency1, times(2)).getAgencyLang();

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo, mockFeedInfo, mockAgency0, mockAgency1);
    }

    // suppressed warning regarding ignored result of method, since the method is called in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void mulMatchesWithAnyAgencyLangAndShouldNotGenerateNotice() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        when(mockFeedInfo.getFeedLang()).thenReturn("mul");

        final Agency mockAgency0 = mock(Agency.class);
        when(mockAgency0.getAgencyLang()).thenReturn("french");
        final Agency mockAgency1 = mock(Agency.class);
        when(mockAgency1.getAgencyLang()).thenReturn("english");

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getFeedInfoAll()).thenReturn(new HashMap<>(Map.of("feed publisher name", mockFeedInfo)));
        when(mockDataRepo.getAgencyAll()).thenReturn(new HashMap<>(Map.of("agency id 0", mockAgency0,
                "agency id 1", mockAgency1)));
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateAgencyLangAndFeedInfoFeedLangMatch underTest =
                new ValidateAgencyLangAndFeedInfoFeedLangMatch(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule 'E055 - Mismatching feed and " +
                "agency language fields'");

        verify(mockDataRepo, times(2)).getFeedInfoAll();
        verify(mockDataRepo, times(2)).getAgencyAll();

        verify(mockFeedInfo, times(1)).getFeedLang();

        verify(mockAgency0, times(1)).getAgencyLang();
        verify(mockAgency1, times(1)).getAgencyLang();

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo, mockFeedInfo, mockAgency0, mockAgency1);
    }

    // suppressed warning regarding ignored result of method, since the method is called in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nonMatchingFeedInfoFeedLangShouldGenerateNotice() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        when(mockFeedInfo.getFeedLang()).thenReturn("default language");

        final Agency mockAgency0 = mock(Agency.class);
        when(mockAgency0.getAgencyLang()).thenReturn("non matching language");
        when(mockAgency0.getAgencyName()).thenReturn("agency name");
        final Agency mockAgency1 = mock(Agency.class);
        when(mockAgency1.getAgencyName()).thenReturn("other agency name");
        when(mockAgency1.getAgencyLang()).thenReturn("default language");

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getFeedInfoAll()).thenReturn(new HashMap<>(Map.of("feed publisher name", mockFeedInfo)));
        when(mockDataRepo.getAgencyAll()).thenReturn(
                new HashMap<>(Map.of("agency id 0", mockAgency0, "agency id 1", mockAgency1)));
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateAgencyLangAndFeedInfoFeedLangMatch underTest =
                new ValidateAgencyLangAndFeedInfoFeedLangMatch(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule 'E055 - Mismatching feed and " +
                "agency language fields'");

        verify(mockDataRepo, times(2)).getFeedInfoAll();
        verify(mockDataRepo, times(2)).getAgencyAll();

        verify(mockFeedInfo, times(1)).getFeedLang();

        verify(mockAgency0, times(3)).getAgencyLang();
        verify(mockAgency0, times(1)).getAgencyName();

        verify(mockAgency1, times(3)).getAgencyLang();

        final ArgumentCaptor<FeedInfoLangAgencyLangMismatchNotice> captor =
                ArgumentCaptor.forClass(FeedInfoLangAgencyLangMismatchNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<FeedInfoLangAgencyLangMismatchNotice> noticeList = captor.getAllValues();

        assertEquals(1, noticeList.size());
        assertEquals("feed_info.txt", noticeList.get(0).getFilename());
        assertEquals("ERROR", noticeList.get(0).getLevel());
        assertEquals(55, noticeList.get(0).getCode());
        assertEquals("agency name", noticeList.get(0).getNoticeSpecific(KEY_AGENCY_AGENCY_NAME));
        assertEquals("non matching language", noticeList.get(0).getNoticeSpecific(KEY_AGENCY_AGENCY_LANG));
        assertEquals("default language", noticeList.get(0).getNoticeSpecific(KEY_FEED_INFO_FEED_LANG));
        assertEquals("agency id 0", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo, mockFeedInfo, mockAgency0, mockAgency1);
    }

    @Test
    void feedLangNotMulAndMoreThanOneAgencyLangShouldGenerateNotice() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        when(mockFeedInfo.getFeedLang()).thenReturn("default language");

        final Agency mockAgency0 = mock(Agency.class);
        when(mockAgency0.getAgencyLang()).thenReturn("non matching language");
        when(mockAgency0.getAgencyName()).thenReturn("agency name");

        final Agency mockAgency1 = mock(Agency.class);
        when(mockAgency1.getAgencyName()).thenReturn("agency name");
        when(mockAgency1.getAgencyLang()).thenReturn("other non matching language");

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getFeedInfoAll()).thenReturn(new HashMap<>(Map.of("feed publisher name", mockFeedInfo)));
        when(mockDataRepo.getAgencyAll()).thenReturn(
                new HashMap<>(Map.of("agency id 0", mockAgency0, "agency id 1", mockAgency1)));
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateAgencyLangAndFeedInfoFeedLangMatch underTest =
                new ValidateAgencyLangAndFeedInfoFeedLangMatch(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule 'E055 - Mismatching feed and " +
                "agency language fields'");

        verify(mockDataRepo, times(2)).getFeedInfoAll();
        verify(mockDataRepo, times(2)).getAgencyAll();

        verify(mockFeedInfo, times(1)).getFeedLang();

        verify(mockAgency0, times(3)).getAgencyLang();
        verify(mockAgency0, times(1)).getAgencyName();

        verify(mockAgency1, times(1)).getAgencyName();
        verify(mockAgency1, times(3)).getAgencyLang();

        final ArgumentCaptor<FeedInfoLangAgencyLangMismatchNotice> captor =
                ArgumentCaptor.forClass(FeedInfoLangAgencyLangMismatchNotice.class);

        verify(mockResultRepo, times(2)).addNotice(captor.capture());

        final List<FeedInfoLangAgencyLangMismatchNotice> noticeList = captor.getAllValues();
        assertEquals(2, noticeList.size());

        assertEquals("feed_info.txt", noticeList.get(0).getFilename());
        assertEquals("ERROR", noticeList.get(0).getLevel());
        assertEquals(55, noticeList.get(0).getCode());
        assertEquals("agency name", noticeList.get(0).getNoticeSpecific(KEY_AGENCY_AGENCY_NAME));
        assertEquals("non matching language", noticeList.get(0).getNoticeSpecific(KEY_AGENCY_AGENCY_LANG));
        assertEquals("default language", noticeList.get(0).getNoticeSpecific(KEY_FEED_INFO_FEED_LANG));
        assertEquals("agency id 0", noticeList.get(0).getEntityId());

        assertEquals("feed_info.txt", noticeList.get(1).getFilename());
        assertEquals("ERROR", noticeList.get(1).getLevel());
        assertEquals(55, noticeList.get(1).getCode());
        assertEquals("agency name", noticeList.get(1).getNoticeSpecific(KEY_AGENCY_AGENCY_NAME));
        assertEquals("other non matching language", noticeList.get(1).getNoticeSpecific(KEY_AGENCY_AGENCY_LANG));
        assertEquals("default language", noticeList.get(1).getNoticeSpecific(KEY_FEED_INFO_FEED_LANG));
        assertEquals("agency id 1", noticeList.get(1).getEntityId());

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo, mockFeedInfo, mockAgency0, mockAgency1);
    }

    @Test
    void mulFeedLangAndNoMoreThanOneAgencyLangShouldGenerateNotice() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        when(mockFeedInfo.getFeedLang()).thenReturn("mul");

        final Agency mockAgency0 = mock(Agency.class);
        when(mockAgency0.getAgencyLang()).thenReturn("some language");
        when(mockAgency0.getAgencyName()).thenReturn("agency name");

        final Agency mockAgency1 = mock(Agency.class);
        when(mockAgency1.getAgencyName()).thenReturn("agency name");
        when(mockAgency1.getAgencyLang()).thenReturn("some language");

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getFeedInfoAll()).thenReturn(new HashMap<>(Map.of("feed publisher name", mockFeedInfo)));
        when(mockDataRepo.getAgencyAll()).thenReturn(
                new HashMap<>(Map.of("agency id 0", mockAgency0, "agency id 1", mockAgency1)));
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateAgencyLangAndFeedInfoFeedLangMatch underTest =
                new ValidateAgencyLangAndFeedInfoFeedLangMatch(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule 'E055 - Mismatching feed and " +
                "agency language fields'");

        verify(mockDataRepo, times(2)).getFeedInfoAll();
        verify(mockDataRepo, times(2)).getAgencyAll();

        verify(mockFeedInfo, times(1)).getFeedLang();

        verify(mockAgency0, times(1)).getAgencyName();
        verify(mockAgency0, times(2)).getAgencyLang();

        verify(mockAgency1, times(1)).getAgencyName();
        verify(mockAgency1, times(2)).getAgencyLang();

        final ArgumentCaptor<FeedInfoLangAgencyLangMismatchNotice> captor =
                ArgumentCaptor.forClass(FeedInfoLangAgencyLangMismatchNotice.class);

        verify(mockResultRepo, times(2)).addNotice(captor.capture());

        final List<FeedInfoLangAgencyLangMismatchNotice> noticeList = captor.getAllValues();
        assertEquals(2, noticeList.size());

        assertEquals("feed_info.txt", noticeList.get(0).getFilename());
        assertEquals("ERROR", noticeList.get(0).getLevel());
        assertEquals(55, noticeList.get(0).getCode());
        assertEquals("agency name", noticeList.get(0).getNoticeSpecific(KEY_AGENCY_AGENCY_NAME));
        assertEquals("some language", noticeList.get(0).getNoticeSpecific(KEY_AGENCY_AGENCY_LANG));
        assertEquals("mul", noticeList.get(0).getNoticeSpecific(KEY_FEED_INFO_FEED_LANG));
        assertEquals("agency id 0", noticeList.get(0).getEntityId());

        assertEquals("feed_info.txt", noticeList.get(1).getFilename());
        assertEquals("ERROR", noticeList.get(1).getLevel());
        assertEquals(55, noticeList.get(1).getCode());
        assertEquals("agency name", noticeList.get(1).getNoticeSpecific(KEY_AGENCY_AGENCY_NAME));
        assertEquals("some language", noticeList.get(1).getNoticeSpecific(KEY_AGENCY_AGENCY_LANG));
        assertEquals("mul", noticeList.get(1).getNoticeSpecific(KEY_FEED_INFO_FEED_LANG));
        assertEquals("agency id 1", noticeList.get(1).getEntityId());

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo, mockFeedInfo, mockAgency0, mockAgency1);
    }
}
