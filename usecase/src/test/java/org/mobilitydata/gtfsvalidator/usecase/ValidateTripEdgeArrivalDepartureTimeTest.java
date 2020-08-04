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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingTripEdgeStopTimeNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.*;
import static org.mockito.Mockito.*;

class ValidateTripEdgeArrivalDepartureTimeTest {

    @Test
    void tripWithFirstStopMissingArrivalAndDepartureTimeShouldGenerateNotice() {

        final StopTime mockStopTime0 = mock(StopTime.class);
        final StopTime mockStopTime1 = mock(StopTime.class);
        final StopTime mockStopTime2 = mock(StopTime.class);
        when(mockStopTime0.getDepartureTime()).thenReturn(null);
        when(mockStopTime0.getArrivalTime()).thenReturn(null);
        when(mockStopTime0.getStopSequence()).thenReturn(514);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);

        when(mockDataRepo.getTripAll()).thenReturn(Map.of("trip_id__test", mock(Trip.class)));

        SortedMap<Integer, StopTime> mockSortedMap =
                new TreeMap<>(
                        Map.of(0, mockStopTime0, 1, mockStopTime1, 2, mockStopTime2)
                );

        when(mockDataRepo.getStopTimeByTripId(ArgumentMatchers.anyString())).thenReturn(mockSortedMap);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateTripEdgeArrivalDepartureTime underTest =
                new ValidateTripEdgeArrivalDepartureTime(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info(ArgumentMatchers.eq(
                "Validating rule E044 - Missing trip edge arrival_time or departure_time"));

        verify(mockDataRepo, times(1)).getTripAll();
        verify(mockDataRepo, times(1)).getStopTimeByTripId(ArgumentMatchers.eq("trip_id__test"));
        verifyNoInteractions(mockStopTime1);

        final ArgumentCaptor<MissingTripEdgeStopTimeNotice> captor =
                ArgumentCaptor.forClass(MissingTripEdgeStopTimeNotice.class);

        verify(mockResultRepo, times(2)).addNotice(captor.capture());

        MissingTripEdgeStopTimeNotice notice = captor.getAllValues().get(0);

        assertEquals("stop_times.txt", notice.getFilename());
        assertEquals("arrival_time", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("trip_id__test", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals("stop_sequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(514, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));

        notice = captor.getAllValues().get(1);

        assertEquals("stop_times.txt", notice.getFilename());
        assertEquals("departure_time", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("trip_id__test", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals("stop_sequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(514, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo);
    }

    @Test
    void tripWithLastStopMissingArrivalAndDepartureTimeShouldGenerateNotice() {

        final StopTime mockStopTime0 = mock(StopTime.class);
        final StopTime mockStopTime1 = mock(StopTime.class);
        final StopTime mockStopTime2 = mock(StopTime.class);
        when(mockStopTime2.getDepartureTime()).thenReturn(null);
        when(mockStopTime2.getArrivalTime()).thenReturn(null);
        when(mockStopTime2.getStopSequence()).thenReturn(514);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);

        when(mockDataRepo.getTripAll()).thenReturn(Map.of("trip_id__test", mock(Trip.class)));

        SortedMap<Integer, StopTime> mockSortedMap =
                new TreeMap<>(
                        Map.of(0, mockStopTime0, 1, mockStopTime1, 2, mockStopTime2)
                );

        when(mockDataRepo.getStopTimeByTripId(ArgumentMatchers.anyString())).thenReturn(mockSortedMap);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateTripEdgeArrivalDepartureTime underTest =
                new ValidateTripEdgeArrivalDepartureTime(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info(ArgumentMatchers.eq(
                "Validating rule E044 - Missing trip edge arrival_time or departure_time"));

        verify(mockDataRepo, times(1)).getTripAll();
        verify(mockDataRepo, times(1)).getStopTimeByTripId(ArgumentMatchers.eq("trip_id__test"));
        verifyNoInteractions(mockStopTime1);

        final ArgumentCaptor<MissingTripEdgeStopTimeNotice> captor =
                ArgumentCaptor.forClass(MissingTripEdgeStopTimeNotice.class);

        verify(mockResultRepo, times(2)).addNotice(captor.capture());

        MissingTripEdgeStopTimeNotice notice = captor.getAllValues().get(0);

        assertEquals("stop_times.txt", notice.getFilename());
        assertEquals("arrival_time", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("trip_id__test", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals("stop_sequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(514, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));

        notice = captor.getAllValues().get(1);

        assertEquals("stop_times.txt", notice.getFilename());
        assertEquals("departure_time", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("trip_id__test", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals("stop_sequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(514, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo);
    }

    @Test
    void tripWithValidEdgeArrivalAndDepartureTimeShouldNotGenerateNotice() {

        final StopTime mockStopTime0 = mock(StopTime.class);
        final StopTime mockStopTime1 = mock(StopTime.class);
        final StopTime mockStopTime2 = mock(StopTime.class);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);

        when(mockDataRepo.getTripAll()).thenReturn(Map.of("trip_id__test", mock(Trip.class)));

        SortedMap<Integer, StopTime> mockSortedMap =
                new TreeMap<>(
                        Map.of(0, mockStopTime0, 1, mockStopTime1, 2, mockStopTime2)
                );

        when(mockDataRepo.getStopTimeByTripId(ArgumentMatchers.anyString())).thenReturn(mockSortedMap);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateTripEdgeArrivalDepartureTime underTest =
                new ValidateTripEdgeArrivalDepartureTime(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info(ArgumentMatchers.eq(
                "Validating rule E044 - Missing trip edge arrival_time or departure_time"));

        verify(mockDataRepo, times(1)).getTripAll();
        verify(mockDataRepo, times(1)).getStopTimeByTripId(ArgumentMatchers.eq("trip_id__test"));
        verifyNoInteractions(mockStopTime1, mockResultRepo);
        verifyNoMoreInteractions(mockLogger, mockResultRepo);
    }

    @Test
    void validateAllTripCheck() {

        final StopTime mockStopTime0 = mock(StopTime.class);
        final StopTime mockStopTime1 = mock(StopTime.class);
        final StopTime mockStopTime2 = mock(StopTime.class);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);

        when(mockDataRepo.getTripAll()).thenReturn(Map.of("trip_id__test_0", mock(Trip.class),
                "trip_id__test_1", mock(Trip.class)));

        SortedMap<Integer, StopTime> mockSortedMap =
                new TreeMap<>(
                        Map.of(0, mockStopTime0, 1, mockStopTime1, 2, mockStopTime2)
                );

        when(mockDataRepo.getStopTimeByTripId(ArgumentMatchers.anyString())).thenReturn(mockSortedMap);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateTripEdgeArrivalDepartureTime underTest =
                new ValidateTripEdgeArrivalDepartureTime(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info(ArgumentMatchers.eq(
                "Validating rule E044 - Missing trip edge arrival_time or departure_time"));

        verify(mockDataRepo, times(1)).getTripAll();
        verify(mockDataRepo, times(2)).getStopTimeByTripId(ArgumentMatchers.anyString());
        verifyNoInteractions(mockStopTime1, mockResultRepo);
        verifyNoMoreInteractions(mockLogger, mockResultRepo);
    }
}
