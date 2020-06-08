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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.InconsistentAgencyTimezoneNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ValidateAgencyTimezoneAreInAccordTest {

    @Test
    void agenciesWithSameAgencyTimezoneShouldNotGenerateNotice() {
        final Agency mockAgency00 = mock(Agency.class);
        when(mockAgency00.getAgencyId()).thenReturn("agency id 00");
        when(mockAgency00.getAgencyTimezone()).thenReturn("timezone");
        final Agency mockAgency01 = mock(Agency.class);
        when(mockAgency01.getAgencyId()).thenReturn("agency id 01");
        when(mockAgency01.getAgencyTimezone()).thenReturn("timezone");

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getAgencyAll()).thenReturn(new ArrayList<>(List.of(mockAgency00, mockAgency01)));
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateAgencyTimezoneAreInAccord underTest =
                new ValidateAgencyTimezoneAreInAccord(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();
        final InOrder inOrder = inOrder(mockDataRepo, mockResultRepo, mockLogger, mockAgency00, mockAgency01);

        inOrder.verify(mockLogger, times(1))
                .info("Validating rule 'E030 - Different 'agency_timezone'" + System.lineSeparator());

        inOrder.verify(mockDataRepo, times(1)).getAgencyAll();
        // suppress warning regarding ignored result of method since it is not necessary here.
        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockAgency00, times(1)).getAgencyTimezone();
        // suppress warning regarding ignored result of method since it is not necessary here.
        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockAgency01, times(1)).getAgencyTimezone();
        verifyNoMoreInteractions(mockDataRepo, mockResultRepo, mockLogger, mockAgency00, mockAgency01);
    }

    @Test
    void agenciesWithDifferentTimezoneShouldGenerateNotice() {
        final Agency mockAgency00 = mock(Agency.class);
        when(mockAgency00.getAgencyId()).thenReturn("agency id 00");
        when(mockAgency00.getAgencyTimezone()).thenReturn("timezone 00");
        final Agency mockAgency01 = mock(Agency.class);
        when(mockAgency01.getAgencyId()).thenReturn("agency id 01");
        when(mockAgency01.getAgencyTimezone()).thenReturn("timezone 01");

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getAgencyAll()).thenReturn(new ArrayList<>(List.of(mockAgency00, mockAgency01)));
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateAgencyTimezoneAreInAccord underTest =
                new ValidateAgencyTimezoneAreInAccord(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();
        final InOrder inOrder = inOrder(mockDataRepo, mockResultRepo, mockLogger, mockAgency00, mockAgency01);

        inOrder.verify(mockLogger, times(1))
                .info("Validating rule 'E030 - Different 'agency_timezone'" + System.lineSeparator());

        inOrder.verify(mockDataRepo, times(1)).getAgencyAll();
        // suppress warning regarding ignored result of method since it is not necessary here.
        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockAgency00, times(1)).getAgencyTimezone();
        // suppress warning regarding ignored result of method since it is not necessary here.
        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockAgency01, times(1)).getAgencyTimezone();

        final ArgumentCaptor<InconsistentAgencyTimezoneNotice> captor =
                ArgumentCaptor.forClass(InconsistentAgencyTimezoneNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<InconsistentAgencyTimezoneNotice> noticeList = captor.getAllValues();

        assertEquals("agency.txt", noticeList.get(0).getFilename());
        assertEquals("agency_timezone", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(2, noticeList.get(0).getDistinctTimezoneCount());
        assertTrue(noticeList.get(0).getConflictingTimezoneCollection().contains("timezone 00"));
        assertTrue(noticeList.get(0).getConflictingTimezoneCollection().contains("timezone 01"));

        verifyNoMoreInteractions(mockDataRepo, mockResultRepo, mockLogger, mockAgency00, mockAgency01);
    }

    @Test
    void uniqueAgencyShouldNotGenerateNotice() {
        final Agency mockAgency00 = mock(Agency.class);
        when(mockAgency00.getAgencyId()).thenReturn("agency id 00");
        when(mockAgency00.getAgencyTimezone()).thenReturn("timezone 00");

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getAgencyAll()).thenReturn(new ArrayList<>(List.of(mockAgency00)));
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateAgencyTimezoneAreInAccord underTest =
                new ValidateAgencyTimezoneAreInAccord(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();
        final InOrder inOrder = inOrder(mockDataRepo, mockResultRepo, mockLogger, mockAgency00);

        inOrder.verify(mockLogger, times(1))
                .info("Validating rule 'E030 - Different 'agency_timezone'" + System.lineSeparator());

        inOrder.verify(mockDataRepo, times(1)).getAgencyAll();
        // suppress warning regarding ignored result of method since it is not necessary here.
        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockAgency00, times(1)).getAgencyTimezone();
        verifyNoMoreInteractions(mockDataRepo, mockResultRepo, mockLogger, mockAgency00);
    }
}
