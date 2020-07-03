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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingAgencyIdNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.NonExistingAgencyId;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.KEY_FIELD_NAME;
import static org.mockito.Mockito.*;

class ValidateRouteAgencyIdTest {

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nullAgencyIdInRouteWhenAgencyHasMoreThanOneRecordShouldGenerateNotice() {
        final Route mockRoute = mock(Route.class);
        when(mockRoute.getAgencyId()).thenReturn(null);
        when(mockRoute.getRouteId()).thenReturn("route id");

        final List<Route> mockRouteCollection = new ArrayList<>(List.of(mockRoute));

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getAgencyCount()).thenReturn(2);
        when(mockDataRepo.getRouteAll()).thenReturn(mockRouteCollection);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateRouteAgencyId underTest = new ValidateRouteAgencyId(mockDataRepo, mockResultRepo, mockLogger);
        underTest.execute();

        verify(mockLogger, times(1)).info(
                ArgumentMatchers.eq("Validating rule 'E035 - GTFS `routes.agency_id` exists in GTFS data" +
                        System.lineSeparator()));

        verify(mockDataRepo, times(1)).getAgencyCount();
        verify(mockDataRepo, times(1)).getRouteAll();

        verify(mockRoute, times(1)).getRouteId();
        verify(mockRoute, times(1)).getAgencyId();

        final ArgumentCaptor<MissingAgencyIdNotice> captor = ArgumentCaptor.forClass(MissingAgencyIdNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<MissingAgencyIdNotice> noticeList = captor.getAllValues();

        assertEquals("routes.txt", noticeList.get(0).getFilename());
        assertEquals("route id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockDataRepo, mockResultRepo, mockLogger, mockRoute);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nonExistingAgencyIdInRouteWhenAgencyHasMoreThanOneRecordShouldGenerateNotice() {
        final Route mockRoute = mock(Route.class);
        when(mockRoute.getAgencyId()).thenReturn("non existing agency id");
        when(mockRoute.getRouteId()).thenReturn("route id");

        final List<Route> mockRouteCollection = new ArrayList<>(List.of(mockRoute));

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getAgencyCount()).thenReturn(2);
        when(mockDataRepo.getRouteAll()).thenReturn(mockRouteCollection);
        when(mockDataRepo.getAgencyById("non existing agency id")).thenReturn(null);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateRouteAgencyId underTest = new ValidateRouteAgencyId(mockDataRepo, mockResultRepo, mockLogger);
        underTest.execute();

        verify(mockLogger, times(1)).info(
                ArgumentMatchers.eq("Validating rule 'E035 - GTFS `routes.agency_id` exists in GTFS data" +
                        System.lineSeparator()));

        verify(mockDataRepo, times(1)).getAgencyCount();
        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockDataRepo, times(1)).getAgencyById(ArgumentMatchers.eq("non existing "+
                "agency id"));

        verify(mockRoute, times(1)).getRouteId();
        verify(mockRoute, times(1)).getAgencyId();

        final ArgumentCaptor<NonExistingAgencyId> captor = ArgumentCaptor.forClass(NonExistingAgencyId.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<NonExistingAgencyId> noticeList = captor.getAllValues();

        assertEquals("routes.txt", noticeList.get(0).getFilename());
        assertEquals("agency_id", noticeList.get(0).getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("route id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockDataRepo, mockResultRepo, mockLogger, mockRoute);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nonExistingAgencyIdInRouteWhenAgencyHasOneRecordInAgencyShouldGenerateNotice() {
        final Route mockRoute = mock(Route.class);
        when(mockRoute.getAgencyId()).thenReturn("non existing agency id");
        when(mockRoute.getRouteId()).thenReturn("route id");

        final List<Route> mockRouteCollection = new ArrayList<>(List.of(mockRoute));

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getAgencyCount()).thenReturn(1);
        when(mockDataRepo.getRouteAll()).thenReturn(mockRouteCollection);
        when(mockDataRepo.getAgencyById("non existing agency id")).thenReturn(null);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateRouteAgencyId underTest = new ValidateRouteAgencyId(mockDataRepo, mockResultRepo, mockLogger);
        underTest.execute();

        verify(mockLogger, times(1)).info(
                ArgumentMatchers.eq("Validating rule 'E035 - GTFS `routes.agency_id` exists in GTFS data" +
                        System.lineSeparator()));

        verify(mockDataRepo, times(1)).getAgencyCount();
        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockDataRepo, times(1)).getAgencyById(ArgumentMatchers.eq("non existing "+
                "agency id"));

        verify(mockRoute, times(1)).getRouteId();
        verify(mockRoute, times(1)).getAgencyId();

        final ArgumentCaptor<NonExistingAgencyId> captor = ArgumentCaptor.forClass(NonExistingAgencyId.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<NonExistingAgencyId> noticeList = captor.getAllValues();

        assertEquals("routes.txt", noticeList.get(0).getFilename());
        assertEquals("agency_id", noticeList.get(0).getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("route id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockDataRepo, mockResultRepo, mockLogger, mockRoute);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nullAgencyIdInRouteWhenAgencyHasOneRecordShouldNoteGenerateNotice() {
        final Route mockRoute = mock(Route.class);
        when(mockRoute.getAgencyId()).thenReturn(null);
        when(mockRoute.getRouteId()).thenReturn("route id");

        final List<Route> mockRouteCollection = new ArrayList<>(List.of(mockRoute));

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getAgencyCount()).thenReturn(1);
        when(mockDataRepo.getRouteAll()).thenReturn(mockRouteCollection);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateRouteAgencyId underTest = new ValidateRouteAgencyId(mockDataRepo, mockResultRepo, mockLogger);
        underTest.execute();

        verify(mockLogger, times(1)).info(
                ArgumentMatchers.eq("Validating rule 'E035 - GTFS `routes.agency_id` exists in GTFS data" +
                        System.lineSeparator()));

        verify(mockDataRepo, times(1)).getAgencyCount();
        verify(mockDataRepo, times(1)).getRouteAll();

        verify(mockRoute, times(1)).getRouteId();
        verify(mockRoute, times(1)).getAgencyId();

        verifyNoMoreInteractions(mockDataRepo, mockResultRepo, mockLogger, mockRoute);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void existingAgencyIdWhenAgencyCountsMoreThanOneRecordShouldNotGenerateNotice() {
        final Agency mockAgency = mock(Agency.class);

        final Route mockRoute = mock(Route.class);
        when(mockRoute.getAgencyId()).thenReturn("existing agency id");
        when(mockRoute.getRouteId()).thenReturn("route id");

        final List<Route> mockRouteCollection = new ArrayList<>(List.of(mockRoute));

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getAgencyCount()).thenReturn(2);
        when(mockDataRepo.getRouteAll()).thenReturn(mockRouteCollection);
        when(mockDataRepo.getAgencyById("existing agency id")).thenReturn(mockAgency);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateRouteAgencyId underTest = new ValidateRouteAgencyId(mockDataRepo, mockResultRepo, mockLogger);
        underTest.execute();

        verify(mockLogger, times(1)).info(
                ArgumentMatchers.eq("Validating rule 'E035 - GTFS `routes.agency_id` exists in GTFS data" +
                        System.lineSeparator()));

        verify(mockDataRepo, times(1)).getAgencyCount();
        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockDataRepo, times(1))
                .getAgencyById(ArgumentMatchers.eq("existing agency id"));

        verify(mockRoute, times(1)).getRouteId();
        verify(mockRoute, times(1)).getAgencyId();

        verifyNoMoreInteractions(mockDataRepo, mockResultRepo, mockLogger, mockRoute);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void existingAgencyIdWhenAgencyCountsOneRecordShouldNotGenerateNotice() {
        final Agency mockAgency = mock(Agency.class);

        final Route mockRoute = mock(Route.class);
        when(mockRoute.getAgencyId()).thenReturn("existing agency id");
        when(mockRoute.getRouteId()).thenReturn("route id");

        final List<Route> mockRouteCollection = new ArrayList<>(List.of(mockRoute));

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getAgencyCount()).thenReturn(1);
        when(mockDataRepo.getRouteAll()).thenReturn(mockRouteCollection);
        when(mockDataRepo.getAgencyById("existing agency id")).thenReturn(mockAgency);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateRouteAgencyId underTest = new ValidateRouteAgencyId(mockDataRepo, mockResultRepo, mockLogger);
        underTest.execute();

        verify(mockLogger, times(1)).info(
                ArgumentMatchers.eq("Validating rule 'E035 - GTFS `routes.agency_id` exists in GTFS data" +
                        System.lineSeparator()));

        verify(mockDataRepo, times(1)).getAgencyCount();
        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockDataRepo, times(1))
                .getAgencyById(ArgumentMatchers.eq("existing agency id"));

        verify(mockRoute, times(1)).getRouteId();
        verify(mockRoute, times(1)).getAgencyId();

        verifyNoMoreInteractions(mockDataRepo, mockResultRepo, mockLogger, mockRoute);
    }
}
