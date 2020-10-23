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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnusableTripNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ValidateTripNumberOfStopsTest {

    @Test
    void tripServingMoreThanOneStopShouldNotGenerateNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Trip mockTrip = mock(Trip.class);

        final StopTime firstStopTimeInSequence = mock(StopTime.class);
        final StopTime secondStopTimeInSequence = mock(StopTime.class);

        final TreeMap<Integer, StopTime> mockStopTimeSequence = new TreeMap<>();
        mockStopTimeSequence.put(0, firstStopTimeInSequence);
        mockStopTimeSequence.put(1, secondStopTimeInSequence);

        final Map<String, Trip> mockTripCollection = new HashMap<>();
        mockTripCollection.put("trip id", mockTrip);
        when(mockDataRepo.getTripAll()).thenReturn(mockTripCollection);
        when(mockDataRepo.getStopTimeByTripId(ArgumentMatchers.eq("trip id"))).thenReturn(mockStopTimeSequence);

        final Map<String, TreeMap<Integer, StopTime>> mockStopTimeCollection = new HashMap<>();
        final TreeMap<Integer, StopTime> innerStopTimeMap = new TreeMap<>();
        mockStopTimeCollection.put("trip id", innerStopTimeMap);
        when(mockDataRepo.getStopTimeAll()).thenReturn(mockStopTimeCollection);


        final ValidateTripNumberOfStops underTest =
                new ValidateTripNumberOfStops(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1))
                .info("Validating rule 'E051 - Trips must have more than one stop to be usable.");

        verify(mockDataRepo, times(1)).getTripAll();
        verify(mockDataRepo, times(1)).getStopTimeByTripId(ArgumentMatchers.eq("trip id"));

        verifyNoInteractions(firstStopTimeInSequence, secondStopTimeInSequence, mockResultRepo);
        verifyNoMoreInteractions(mockDataRepo, mockLogger);
    }

    @Test
    void tripServingOneStopShouldGenerateNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Trip mockTrip = mock(Trip.class);

        final StopTime onlyStopTimeInSequence = mock(StopTime.class);

        final TreeMap<Integer, StopTime> mockStopTimeSequence = new TreeMap<>();
        mockStopTimeSequence.put(0, onlyStopTimeInSequence);

        final Map<String, Trip> mockTripCollection = new HashMap<>();
        mockTripCollection.put("trip id", mockTrip);
        when(mockDataRepo.getTripAll()).thenReturn(mockTripCollection);
        when(mockDataRepo.getStopTimeByTripId(ArgumentMatchers.eq("trip id"))).thenReturn(mockStopTimeSequence);

        final Map<String, TreeMap<Integer, StopTime>> mockStopTimeCollection = new HashMap<>();
        final TreeMap<Integer, StopTime> innerStopTimeMap = new TreeMap<>();
        mockStopTimeCollection.put("trip id", innerStopTimeMap);
        when(mockDataRepo.getStopTimeAll()).thenReturn(mockStopTimeCollection);


        final ValidateTripNumberOfStops underTest =
                new ValidateTripNumberOfStops(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1))
                .info("Validating rule 'E051 - Trips must have more than one stop to be usable.");

        verify(mockDataRepo, times(1)).getTripAll();
        verify(mockDataRepo, times(1)).getStopTimeByTripId(ArgumentMatchers.eq("trip id"));

        final ArgumentCaptor<UnusableTripNotice> captor = ArgumentCaptor.forClass(UnusableTripNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<UnusableTripNotice> noticeList = captor.getAllValues();

        assertEquals("stop_times.txt", noticeList.get(0).getFilename());
        assertEquals("trip id", noticeList.get(0).getEntityId());

        verifyNoInteractions(onlyStopTimeInSequence);
        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo);
    }
}
