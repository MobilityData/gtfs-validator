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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingAgencyIdNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

class ValidateAgencyIdRequirementTest {

    @Test
    void agencyCollectionWithOneRecordShouldNotGenerateNotice(){
        final Agency mockAgency = mock(Agency.class);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getAgencyAll()).thenReturn(new ArrayList<>(List.of(mockAgency)));
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateAgencyIdRequirement underTest =
                new ValidateAgencyIdRequirement(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();
        final InOrder inOrder = inOrder(mockDataRepo, mockResultRepo, mockLogger);

        inOrder.verify(mockLogger, times(1))
                .info("Validating rule 'E029 - Missing field `agency_id` for file agency.txt with more than 1 record'"
                        + System.lineSeparator());

        inOrder.verify(mockDataRepo, times(1)).getAgencyAll();

        verifyNoMoreInteractions(mockDataRepo, mockResultRepo, mockLogger, mockAgency);
    }

    @Test
    void agencyCollectionWithMultipleRecordWithIdShouldNotGenerateNotice(){
        final Agency mockAgency00 = mock(Agency.class);
        when(mockAgency00.getAgencyId()).thenReturn("agency id 00");
        final Agency mockAgency01 = mock(Agency.class);
        when(mockAgency01.getAgencyId()).thenReturn("agency id 01");
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getAgencyAll()).thenReturn(new ArrayList<>(List.of(mockAgency00, mockAgency01)));
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateAgencyIdRequirement underTest =
                new ValidateAgencyIdRequirement(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();
        final InOrder inOrder = inOrder(mockDataRepo, mockResultRepo, mockLogger, mockAgency00, mockAgency01);

        inOrder.verify(mockLogger, times(1))
                .info("Validating rule 'E029 - Missing field `agency_id` for file agency.txt with more than 1 record'"
                        + System.lineSeparator());

        inOrder.verify(mockDataRepo, times(1)).getAgencyAll();

        // suppress warning regarding ignored result of method since it is not necessary here.
        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockAgency00, times(1)).getAgencyId();
        // suppress warning regarding ignored result of method since it is not necessary here.
        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockAgency01, times(1)).getAgencyId();

        verifyNoMoreInteractions(mockDataRepo, mockResultRepo, mockLogger, mockAgency00, mockAgency01);
    }

    @Test
    void agencyCollectionWithMultipleRecordWithoutIdShouldGenerateNotices(){
        final Agency mockAgency00 = mock(Agency.class);
        when(mockAgency00.getAgencyId()).thenReturn(null);
        final Agency mockAgency01 = mock(Agency.class);
        when(mockAgency01.getAgencyId()).thenReturn("agency id 01");
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getAgencyAll()).thenReturn(new ArrayList<>(List.of(mockAgency00, mockAgency01)));
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateAgencyIdRequirement underTest =
                new ValidateAgencyIdRequirement(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();
        final InOrder inOrder = inOrder(mockDataRepo, mockResultRepo, mockLogger, mockAgency00, mockAgency01);

        inOrder.verify(mockLogger, times(1))
                .info("Validating rule 'E029 - Missing field `agency_id` for file agency.txt with more than 1 record'"
                        + System.lineSeparator());

        inOrder.verify(mockDataRepo, times(1)).getAgencyAll();

        // suppress warning regarding ignored result of method since it is not necessary here.
        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockAgency00, times(1)).getAgencyId();
        // suppress warning regarding ignored result of method since it is not necessary here.
        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockAgency00, times(1)).getAgencyName();
        inOrder.verify(mockResultRepo, times(1)).addNotice(any(MissingAgencyIdNotice.class));
        // suppress warning regarding ignored result of method since it is not necessary here.
        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockAgency01, times(1)).getAgencyId();

        verifyNoMoreInteractions(mockDataRepo, mockResultRepo, mockLogger, mockAgency00, mockAgency01);
    }
}
