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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.TripNotUsedNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ValidateTripUsageTest {

    @Test
    void usedTripShouldNotGenerateNotice() {
        final Logger mockLogger = mock(Logger.class);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, Trip> mockTripCollection = new HashMap<>();
        mockTripCollection.put("12345", mock(Trip.class));
        when(mockDataRepo.getTripAll()).thenReturn(mockTripCollection);

        final Map<String, TreeMap<Integer, StopTime>> mockStopTimeCollection = new HashMap<>();
        final TreeMap<Integer, StopTime> innerStopTimeMap = new TreeMap<>();
        mockStopTimeCollection.put("12345", innerStopTimeMap);
        when(mockDataRepo.getStopTimeAll()).thenReturn(mockStopTimeCollection);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final ValidateTripUsage underTest = new ValidateTripUsage(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule 'E050 - Trips must be used in" +
                " `stop_times.txt`.'");
        verify(mockDataRepo, times(1)).getStopTimeAll();
        verify(mockDataRepo, times(1)).getTripAll();
        verifyNoMoreInteractions(mockDataRepo, mockLogger);
        verifyNoInteractions(mockResultRepo);
    }

    @Test
    void unusedTripShouldGenerateNotice() {
        final Logger mockLogger = mock(Logger.class);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, Trip> mockTripCollection = new HashMap<>();
        mockTripCollection.put("unused trip id value: 4455", mock(Trip.class));
        when(mockDataRepo.getTripAll()).thenReturn(mockTripCollection);

        final Map<String, TreeMap<Integer, StopTime>> mockStopTimeCollection = new HashMap<>();
        final TreeMap<Integer, StopTime> innerStopTimeMap = new TreeMap<>();
        mockStopTimeCollection.put("12345", innerStopTimeMap);
        when(mockDataRepo.getStopTimeAll()).thenReturn(mockStopTimeCollection);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final ValidateTripUsage underTest = new ValidateTripUsage(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule 'E050 - Trips must be used in " +
                "`stop_times.txt`.'");
        verify(mockDataRepo, times(1)).getStopTimeAll();
        verify(mockDataRepo, times(1)).getTripAll();

        final ArgumentCaptor<TripNotUsedNotice> captor = ArgumentCaptor.forClass(TripNotUsedNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<TripNotUsedNotice> noticeList = captor.getAllValues();

        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("unused trip id value: 4455", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockResultRepo);

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo);
    }
}
