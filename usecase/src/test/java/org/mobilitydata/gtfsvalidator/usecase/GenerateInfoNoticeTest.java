/*
 * Copyright (c) 2019. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Agency;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.info.ValidationProcessInfoNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.CustomFileUtils;
import org.mockito.ArgumentCaptor;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.*;
import static org.mockito.Mockito.*;

class GenerateInfoNoticeTest {

    @Test
    void infoNoticeShouldBeGeneratedAndAddedToResultRepoWhenFeedInfoIsProvided() {
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getFeedPublisherName()).thenReturn("feed publisher name");

        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.getExecParamValue(mockExecParamRepo.URL_KEY)).thenReturn(mockExecParamRepo.URL_KEY);
        when(mockExecParamRepo.getExecParamValue(mockExecParamRepo.INPUT_KEY)).thenReturn(mockExecParamRepo.INPUT_KEY);
        when(mockExecParamRepo.getExecParamValue(mockExecParamRepo.EXTRACT_KEY)).thenReturn(mockExecParamRepo.EXTRACT_KEY);
        when(mockExecParamRepo.hasExecParamValue(mockExecParamRepo.URL_KEY)).thenReturn(true);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        when(mockResultRepo.getErrorNoticeCount()).thenReturn(0);
        when(mockResultRepo.getWarningNoticeCount()).thenReturn(0);

        final Timestamp mockTimestamp = mock(Timestamp.class);
        when(mockTimestamp.toString()).thenReturn("timestamp");

        final long mockProcessingTime = 500000L;
        final Set<String> mockProcessedFilenameCollection = mock(Set.class);
        when(mockProcessedFilenameCollection.toString()).thenReturn("processed filename collection as String");

        final CustomFileUtils mockCustomFileUtils = mock(CustomFileUtils.class);
        when(mockCustomFileUtils.sizeOf(anyString())).thenReturn(56L);
        when(mockCustomFileUtils.sizeOfDirectory(anyString())).thenReturn(89L);

        final GenerateInfoNotice underTest =
                new GenerateInfoNotice(mockResultRepo, mockExecParamRepo, mockDataRepo, mockTimestamp,
                        mockProcessingTime, mockProcessedFilenameCollection, mockCustomFileUtils);

        underTest.execute();

        final ArgumentCaptor<ValidationProcessInfoNotice> captor =
                ArgumentCaptor.forClass(ValidationProcessInfoNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        ValidationProcessInfoNotice notice = captor.getAllValues().get(0);
        assertNull(notice.getFilename());
        assertEquals("INFO", notice.getLevel());
        assertEquals("Info notice", notice.getTitle());
        assertEquals("More information regarding the validation process", notice.getDescription());
        assertEquals("no id", notice.getEntityId());
        assertEquals("feed publisher name", notice.getNoticeSpecific(FEED_PUBLISHER_NAME_OR_AGENCY_NAME));
        assertEquals("timestamp", notice.getNoticeSpecific(VALIDATION_TIMESTAMP));
        assertEquals(0, notice.getNoticeSpecific(WARNING_NOTICE_COUNT));
        assertEquals(0, notice.getNoticeSpecific(ERROR_NOTICE_COUNT));
        assertEquals(mockExecParamRepo.URL_KEY, notice.getNoticeSpecific(PATH_OR_URL_TO_GTFS_ARCHIVE));
        assertEquals(56L, notice.getNoticeSpecific(GTFS_ARCHIVE_SIZE_BEFORE_UNZIPPING_BYTE));
        assertEquals(89L, notice.getNoticeSpecific(GTFS_ARCHIVE_SIZE_AFTER_UNZIPPING_BYTE));
        assertEquals("v1.3.0-SNAPSHOT", notice.getNoticeSpecific(GTFS_VALIDATOR_VERSION));
        assertEquals(500000L, notice.getNoticeSpecific(PROCESSING_TIME_SECS));
        assertEquals(mockProcessedFilenameCollection.toString(),
                notice.getNoticeSpecific(PROCESSED_FILENAME_COLLECTION));
    }

    @Test
    void infoNoticeShouldBeGeneratedAndAddedToResultRepoWhenAgencyIsProvided() {
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getFeedPublisherName()).thenReturn("");
        when(mockDataRepo.getAgencyCount()).thenReturn(2);
        final Map<String, Agency> mockAgencyContainer = new HashMap<>();
        final Agency mockAgency = mock(Agency.class);
        when(mockAgency.getAgencyName()).thenReturn("agency name");
        mockAgencyContainer.put("agencyId", mockAgency);
        when(mockDataRepo.getAgencyAll()).thenReturn(mockAgencyContainer);

        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.getExecParamValue(mockExecParamRepo.URL_KEY)).thenReturn(mockExecParamRepo.URL_KEY);
        when(mockExecParamRepo.getExecParamValue(mockExecParamRepo.INPUT_KEY)).thenReturn(mockExecParamRepo.INPUT_KEY);
        when(mockExecParamRepo.getExecParamValue(mockExecParamRepo.EXTRACT_KEY)).thenReturn(mockExecParamRepo.EXTRACT_KEY);
        when(mockExecParamRepo.hasExecParamValue(mockExecParamRepo.URL_KEY)).thenReturn(true);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        when(mockResultRepo.getErrorNoticeCount()).thenReturn(0);
        when(mockResultRepo.getWarningNoticeCount()).thenReturn(0);

        final Timestamp mockTimestamp = mock(Timestamp.class);
        when(mockTimestamp.toString()).thenReturn("timestamp");

        final long mockProcessingTime = 500000L;
        final Set<String> mockProcessedFilenameCollection = mock(Set.class);
        when(mockProcessedFilenameCollection.toString()).thenReturn("processed filename collection as String");

        final CustomFileUtils mockCustomFileUtils = mock(CustomFileUtils.class);
        when(mockCustomFileUtils.sizeOf(anyString())).thenReturn(56L);
        when(mockCustomFileUtils.sizeOfDirectory(anyString())).thenReturn(89L);

        final GenerateInfoNotice underTest =
                new GenerateInfoNotice(mockResultRepo, mockExecParamRepo, mockDataRepo, mockTimestamp,
                        mockProcessingTime, mockProcessedFilenameCollection, mockCustomFileUtils);

        underTest.execute();

        final ArgumentCaptor<ValidationProcessInfoNotice> captor =
                ArgumentCaptor.forClass(ValidationProcessInfoNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        ValidationProcessInfoNotice notice = captor.getAllValues().get(0);
        assertNull(notice.getFilename());
        assertEquals("INFO", notice.getLevel());
        assertEquals("Info notice", notice.getTitle());
        assertEquals("More information regarding the validation process", notice.getDescription());
        assertEquals("no id", notice.getEntityId());
        assertEquals("agency name", notice.getNoticeSpecific(FEED_PUBLISHER_NAME_OR_AGENCY_NAME));
        assertEquals("timestamp", notice.getNoticeSpecific(VALIDATION_TIMESTAMP));
        assertEquals(0, notice.getNoticeSpecific(WARNING_NOTICE_COUNT));
        assertEquals(0, notice.getNoticeSpecific(ERROR_NOTICE_COUNT));
        assertEquals(mockExecParamRepo.URL_KEY, notice.getNoticeSpecific(PATH_OR_URL_TO_GTFS_ARCHIVE));
        assertEquals(56L, notice.getNoticeSpecific(GTFS_ARCHIVE_SIZE_BEFORE_UNZIPPING_BYTE));
        assertEquals(89L, notice.getNoticeSpecific(GTFS_ARCHIVE_SIZE_AFTER_UNZIPPING_BYTE));
        assertEquals("v1.3.0-SNAPSHOT", notice.getNoticeSpecific(GTFS_VALIDATOR_VERSION));
        assertEquals(500000L, notice.getNoticeSpecific(PROCESSING_TIME_SECS));
        assertEquals(mockProcessedFilenameCollection.toString(),
                notice.getNoticeSpecific(PROCESSED_FILENAME_COLLECTION));
    }

    @Test
    void infoNoticeShouldBeGeneratedAndAddedToResultRepoWhenFeedInfoAndAgencyAreNotProvided() {
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getFeedPublisherName()).thenReturn("");
        when(mockDataRepo.getAgencyCount()).thenReturn(0);

        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.getExecParamValue(mockExecParamRepo.URL_KEY)).thenReturn(mockExecParamRepo.URL_KEY);
        when(mockExecParamRepo.getExecParamValue(mockExecParamRepo.INPUT_KEY)).thenReturn(mockExecParamRepo.INPUT_KEY);
        when(mockExecParamRepo.getExecParamValue(mockExecParamRepo.EXTRACT_KEY)).thenReturn(mockExecParamRepo.EXTRACT_KEY);
        when(mockExecParamRepo.hasExecParamValue(mockExecParamRepo.URL_KEY)).thenReturn(true);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        when(mockResultRepo.getErrorNoticeCount()).thenReturn(0);
        when(mockResultRepo.getWarningNoticeCount()).thenReturn(0);

        final Timestamp mockTimestamp = mock(Timestamp.class);
        when(mockTimestamp.toString()).thenReturn("timestamp");

        final long mockProcessingTime = 500000L;
        final Set<String> mockProcessedFilenameCollection = mock(Set.class);
        when(mockProcessedFilenameCollection.toString()).thenReturn("processed filename collection as String");

        final CustomFileUtils mockCustomFileUtils = mock(CustomFileUtils.class);
        when(mockCustomFileUtils.sizeOf(anyString())).thenReturn(56L);
        when(mockCustomFileUtils.sizeOfDirectory(anyString())).thenReturn(89L);

        final GenerateInfoNotice underTest =
                new GenerateInfoNotice(mockResultRepo, mockExecParamRepo, mockDataRepo, mockTimestamp,
                        mockProcessingTime, mockProcessedFilenameCollection, mockCustomFileUtils);

        underTest.execute();

        final ArgumentCaptor<ValidationProcessInfoNotice> captor =
                ArgumentCaptor.forClass(ValidationProcessInfoNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        ValidationProcessInfoNotice notice = captor.getAllValues().get(0);
        assertNull(notice.getFilename());
        assertEquals("INFO", notice.getLevel());
        assertEquals("Info notice", notice.getTitle());
        assertEquals("More information regarding the validation process", notice.getDescription());
        assertEquals("no id", notice.getEntityId());
        assertEquals("no agency or feed publisher found",
                notice.getNoticeSpecific(FEED_PUBLISHER_NAME_OR_AGENCY_NAME));
        assertEquals("timestamp", notice.getNoticeSpecific(VALIDATION_TIMESTAMP));
        assertEquals(0, notice.getNoticeSpecific(WARNING_NOTICE_COUNT));
        assertEquals(0, notice.getNoticeSpecific(ERROR_NOTICE_COUNT));
        assertEquals(mockExecParamRepo.URL_KEY, notice.getNoticeSpecific(PATH_OR_URL_TO_GTFS_ARCHIVE));
        assertEquals(56L, notice.getNoticeSpecific(GTFS_ARCHIVE_SIZE_BEFORE_UNZIPPING_BYTE));
        assertEquals(89L, notice.getNoticeSpecific(GTFS_ARCHIVE_SIZE_AFTER_UNZIPPING_BYTE));
        assertEquals("v1.3.0-SNAPSHOT", notice.getNoticeSpecific(GTFS_VALIDATOR_VERSION));
        assertEquals(500000L, notice.getNoticeSpecific(PROCESSING_TIME_SECS));
        assertEquals(mockProcessedFilenameCollection.toString(),
                notice.getNoticeSpecific(PROCESSED_FILENAME_COLLECTION));
    }

    @Test
    void infoNoticeShouldBeGeneratedAndAddedToResultRepoWhenBothFilesAreProvided() {
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getFeedPublisherName()).thenReturn("feed publisher name");
        when(mockDataRepo.getAgencyCount()).thenReturn(2);
        final Map<String, Agency> mockAgencyContainer = new HashMap<>();
        final Agency mockAgency = mock(Agency.class);
        when(mockAgency.getAgencyName()).thenReturn("agency name");
        mockAgencyContainer.put("agencyId", mockAgency);
        when(mockDataRepo.getAgencyAll()).thenReturn(mockAgencyContainer);

        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.getExecParamValue(mockExecParamRepo.URL_KEY)).thenReturn(mockExecParamRepo.URL_KEY);
        when(mockExecParamRepo.getExecParamValue(mockExecParamRepo.INPUT_KEY)).thenReturn(mockExecParamRepo.INPUT_KEY);
        when(mockExecParamRepo.getExecParamValue(mockExecParamRepo.EXTRACT_KEY)).thenReturn(mockExecParamRepo.EXTRACT_KEY);
        when(mockExecParamRepo.hasExecParamValue(mockExecParamRepo.URL_KEY)).thenReturn(true);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        when(mockResultRepo.getErrorNoticeCount()).thenReturn(0);
        when(mockResultRepo.getWarningNoticeCount()).thenReturn(0);

        final Timestamp mockTimestamp = mock(Timestamp.class);
        when(mockTimestamp.toString()).thenReturn("timestamp");

        final long mockProcessingTime = 500000L;
        final Set<String> mockProcessedFilenameCollection = mock(Set.class);
        when(mockProcessedFilenameCollection.toString()).thenReturn("processed filename collection as String");

        final CustomFileUtils mockCustomFileUtils = mock(CustomFileUtils.class);
        when(mockCustomFileUtils.sizeOf(anyString())).thenReturn(56L);
        when(mockCustomFileUtils.sizeOfDirectory(anyString())).thenReturn(89L);

        final GenerateInfoNotice underTest =
                new GenerateInfoNotice(mockResultRepo, mockExecParamRepo, mockDataRepo, mockTimestamp,
                        mockProcessingTime, mockProcessedFilenameCollection, mockCustomFileUtils);

        underTest.execute();

        final ArgumentCaptor<ValidationProcessInfoNotice> captor =
                ArgumentCaptor.forClass(ValidationProcessInfoNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        ValidationProcessInfoNotice notice = captor.getAllValues().get(0);
        assertNull(notice.getFilename());
        assertEquals("INFO", notice.getLevel());
        assertEquals("Info notice", notice.getTitle());
        assertEquals("More information regarding the validation process", notice.getDescription());
        assertEquals("no id", notice.getEntityId());
        assertEquals("feed publisher name",
                notice.getNoticeSpecific(FEED_PUBLISHER_NAME_OR_AGENCY_NAME));
        assertEquals("timestamp", notice.getNoticeSpecific(VALIDATION_TIMESTAMP));
        assertEquals(0, notice.getNoticeSpecific(WARNING_NOTICE_COUNT));
        assertEquals(0, notice.getNoticeSpecific(ERROR_NOTICE_COUNT));
        assertEquals(mockExecParamRepo.URL_KEY, notice.getNoticeSpecific(PATH_OR_URL_TO_GTFS_ARCHIVE));
        assertEquals(56L, notice.getNoticeSpecific(GTFS_ARCHIVE_SIZE_BEFORE_UNZIPPING_BYTE));
        assertEquals(89L, notice.getNoticeSpecific(GTFS_ARCHIVE_SIZE_AFTER_UNZIPPING_BYTE));
        assertEquals("v1.3.0-SNAPSHOT", notice.getNoticeSpecific(GTFS_VALIDATOR_VERSION));
        assertEquals(500000L, notice.getNoticeSpecific(PROCESSING_TIME_SECS));
        assertEquals(mockProcessedFilenameCollection.toString(),
                notice.getNoticeSpecific(PROCESSED_FILENAME_COLLECTION));
    }
}
