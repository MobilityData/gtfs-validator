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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.fareattributes.FareAttribute;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.AgencyIdNotFoundNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingAgencyIdNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.KEY_FIELD_NAME;
import static org.mockito.Mockito.*;

class ValidateFareAttributeAgencyIdTest {

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nullAgencyIdInFareAttributeWhenAgencyHasMoreThanOneRecordShouldGenerateNotice() {
        final FareAttribute mockFareAttribute = mock(FareAttribute.class);
        when(mockFareAttribute.getAgencyId()).thenReturn(null);

        final Map<String, FareAttribute> mockFareAttributeCollection = new HashMap<>();
        mockFareAttributeCollection.put("fare id", mockFareAttribute);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getAgencyCount()).thenReturn(2);
        when(mockDataRepo.getFareAttributeAll()).thenReturn(mockFareAttributeCollection);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateFareAttributeAgencyId underTest =
                new ValidateFareAttributeAgencyId(mockDataRepo, mockResultRepo, mockLogger);
        underTest.execute();

        verify(mockLogger, times(1)).info(
                ArgumentMatchers.eq("Validating rule 'E035 - `agency_id` not found" + System.lineSeparator()));

        verify(mockDataRepo, times(1)).getAgencyCount();
        verify(mockDataRepo, times(1)).getFareAttributeAll();

        verify(mockFareAttribute, times(1)).getAgencyId();

        final ArgumentCaptor<MissingAgencyIdNotice> captor = ArgumentCaptor.forClass(MissingAgencyIdNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<MissingAgencyIdNotice> noticeList = captor.getAllValues();

        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals("fare id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockDataRepo, mockResultRepo, mockLogger, mockFareAttribute);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nonExistingAgencyIdInFareAttributeWhenAgencyHasMoreThanOneRecordShouldGenerateNotice() {
        final FareAttribute mockFareAttribute = mock(FareAttribute.class);
        when(mockFareAttribute.getAgencyId()).thenReturn("non existing agency id");

        final Map<String, FareAttribute> mockFareAttributeCollection = new HashMap<>();
        mockFareAttributeCollection.put("fare id", mockFareAttribute);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getAgencyCount()).thenReturn(2);
        when(mockDataRepo.getFareAttributeAll()).thenReturn(mockFareAttributeCollection);

        when(mockDataRepo.getAgencyById("non existing agency id")).thenReturn(null);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateFareAttributeAgencyId underTest =
                new ValidateFareAttributeAgencyId(mockDataRepo, mockResultRepo, mockLogger);
        underTest.execute();

        verify(mockLogger, times(1)).info(
                ArgumentMatchers.eq("Validating rule 'E035 - `agency_id` not found" + System.lineSeparator()));

        verify(mockDataRepo, times(1)).getAgencyCount();
        verify(mockDataRepo, times(1)).getFareAttributeAll();
        verify(mockDataRepo, times(1)).getAgencyById(ArgumentMatchers.eq("non existing "+
                "agency id"));

        verify(mockFareAttribute, times(1)).getAgencyId();

        final ArgumentCaptor<AgencyIdNotFoundNotice> captor = ArgumentCaptor.forClass(AgencyIdNotFoundNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<AgencyIdNotFoundNotice> noticeList = captor.getAllValues();

        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals("agency_id", noticeList.get(0).getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("fare id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockDataRepo, mockResultRepo, mockLogger, mockFareAttribute);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nonExistingAgencyIdInFareAttributeWhenAgencyHasOneRecordInAgencyShouldGenerateNotice() {
        final FareAttribute mockFareAttribute = mock(FareAttribute.class);
        when(mockFareAttribute.getAgencyId()).thenReturn("non existing agency id");

        final Map<String, FareAttribute> mockFareAttributeCollection = new HashMap<>();
        mockFareAttributeCollection.put("fare id", mockFareAttribute);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getAgencyCount()).thenReturn(1);
        when(mockDataRepo.getFareAttributeAll()).thenReturn(mockFareAttributeCollection);
        when(mockDataRepo.getAgencyById("non existing agency id")).thenReturn(null);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateFareAttributeAgencyId underTest =
                new ValidateFareAttributeAgencyId(mockDataRepo, mockResultRepo, mockLogger);
        underTest.execute();

        verify(mockLogger, times(1)).info(
                ArgumentMatchers.eq("Validating rule 'E035 - `agency_id` not found" + System.lineSeparator()));

        verify(mockDataRepo, times(1)).getAgencyCount();
        verify(mockDataRepo, times(1)).getFareAttributeAll();
        verify(mockDataRepo, times(1)).getAgencyById(ArgumentMatchers.eq("non existing "+
                "agency id"));

        verify(mockFareAttribute, times(1)).getAgencyId();

        final ArgumentCaptor<AgencyIdNotFoundNotice> captor = ArgumentCaptor.forClass(AgencyIdNotFoundNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<AgencyIdNotFoundNotice> noticeList = captor.getAllValues();

        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals("agency_id", noticeList.get(0).getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("fare id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockDataRepo, mockResultRepo, mockLogger, mockFareAttribute);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nullAgencyIdInFareAttributeWhenAgencyHasOneRecordShouldNoteGenerateNotice() {
        final FareAttribute mockFareAttribute = mock(FareAttribute.class);
        when(mockFareAttribute.getAgencyId()).thenReturn(null);

        final Map<String, FareAttribute> mockFareAttributeCollection = new HashMap<>();
        mockFareAttributeCollection.put("fare id", mockFareAttribute);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getAgencyCount()).thenReturn(1);
        when(mockDataRepo.getFareAttributeAll()).thenReturn(mockFareAttributeCollection);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateFareAttributeAgencyId underTest =
                new ValidateFareAttributeAgencyId(mockDataRepo, mockResultRepo, mockLogger);
        underTest.execute();

        verify(mockLogger, times(1)).info(
                ArgumentMatchers.eq("Validating rule 'E035 - `agency_id` not found" + System.lineSeparator()));

        verify(mockDataRepo, times(1)).getAgencyCount();
        verify(mockDataRepo, times(1)).getFareAttributeAll();

        verify(mockFareAttribute, times(1)).getAgencyId();

        verifyNoMoreInteractions(mockDataRepo, mockResultRepo, mockLogger, mockFareAttribute);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void existingAgencyIdWhenAgencyCountsMoreThanOneRecordShouldNotGenerateNotice() {
        final Agency mockAgency = mock(Agency.class);

        final FareAttribute mockFareAttribute = mock(FareAttribute.class);
        when(mockFareAttribute.getAgencyId()).thenReturn("existing agency id");

        final Map<String, FareAttribute> mockFareAttributeCollection = new HashMap<>();
        mockFareAttributeCollection.put("fare id", mockFareAttribute);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getAgencyCount()).thenReturn(2);
        when(mockDataRepo.getFareAttributeAll()).thenReturn(mockFareAttributeCollection);
        when(mockDataRepo.getAgencyById("existing agency id")).thenReturn(mockAgency);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateFareAttributeAgencyId underTest =
                new ValidateFareAttributeAgencyId(mockDataRepo, mockResultRepo, mockLogger);
        underTest.execute();

        verify(mockLogger, times(1)).info(
                ArgumentMatchers.eq("Validating rule 'E035 - `agency_id` not found" + System.lineSeparator()));

        verify(mockDataRepo, times(1)).getAgencyCount();
        verify(mockDataRepo, times(1)).getFareAttributeAll();
        verify(mockDataRepo, times(1))
                .getAgencyById(ArgumentMatchers.eq("existing agency id"));

        verify(mockFareAttribute, times(1)).getAgencyId();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockFareAttribute);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void existingAgencyIdWhenAgencyCountsOneRecordShouldNotGenerateNotice() {
        final Agency mockAgency = mock(Agency.class);

        final FareAttribute mockFareAttribute = mock(FareAttribute.class);
        when(mockFareAttribute.getAgencyId()).thenReturn("existing agency id");

        final Map<String, FareAttribute> mockFareAttributeCollection = new HashMap<>();
        mockFareAttributeCollection.put("fare id", mockFareAttribute);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getAgencyCount()).thenReturn(1);
        when(mockDataRepo.getFareAttributeAll()).thenReturn(mockFareAttributeCollection);
        when(mockDataRepo.getAgencyById("existing agency id")).thenReturn(mockAgency);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateFareAttributeAgencyId underTest =
                new ValidateFareAttributeAgencyId(mockDataRepo, mockResultRepo, mockLogger);
        underTest.execute();

        verify(mockLogger, times(1)).info(
                ArgumentMatchers.eq("Validating rule 'E035 - `agency_id` not found" + System.lineSeparator()));

        verify(mockDataRepo, times(1)).getAgencyCount();
        verify(mockDataRepo, times(1)).getFareAttributeAll();
        verify(mockDataRepo, times(1))
                .getAgencyById(ArgumentMatchers.eq("existing agency id"));

        verify(mockFareAttribute, times(1)).getAgencyId();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockFareAttribute);
    }
}