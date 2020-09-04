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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.StopTimeArrivalTimeAfterDepartureTimeNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ValidateStopTimeDepartureTimeAfterArrivalTimeTest {

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void stopTimeArrivalTimeBeforeDepartureTimeShouldNotGenerateNotice() {
        final StopTime mockStopTime = mock(StopTime.class);
        final Integer arrivalTime = 0;
        final Integer departureTime = 60;
        when(mockStopTime.getArrivalTime()).thenReturn(arrivalTime);
        when(mockStopTime.getDepartureTime()).thenReturn(departureTime);

        final Map<String, TreeMap<Integer, StopTime>> mockStopTimeCollection = new HashMap<>();
        final TreeMap<Integer, StopTime> mockStopTimeEntry = new TreeMap<>();
        final Integer stopSequence = 0;
        mockStopTimeEntry.put(stopSequence, mockStopTime);
        mockStopTimeCollection.put("trip_id", mockStopTimeEntry);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getStopTimeAll()).thenReturn(mockStopTimeCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final TimeUtils mockTimeUtil = mock(TimeUtils.class);

        final ValidateStopTimeDepartureTimeAfterArrivalTime underTest =
                new ValidateStopTimeDepartureTimeAfterArrivalTime(mockDataRepo, mockResultRepo,
                        mockTimeUtil, mockLogger);

        underTest.execute();
        verify(mockLogger, times(1)).info("Validating rule 'E045 - `departure_time` and " +
                "`arrival_time` out of order");

        verify(mockDataRepo, times(1)).getStopTimeAll();

        verify(mockStopTime, times(1)).getArrivalTime();
        verify(mockStopTime, times(1)).getDepartureTime();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockStopTime, mockTimeUtil);
    }

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void stopTimeArrivalTimeAfterDepartureTimeShouldGenerateNotice() {
        final StopTime mockStopTime = mock(StopTime.class);
        final Integer arrivalTime = 60;
        final Integer departureTime = 0;
        when(mockStopTime.getArrivalTime()).thenReturn(arrivalTime);
        when(mockStopTime.getDepartureTime()).thenReturn(departureTime);

        final Map<String, TreeMap<Integer, StopTime>> mockStopTimeCollection = new HashMap<>();
        final TreeMap<Integer, StopTime> mockStopTimeEntry = new TreeMap<>();
        final Integer stopSequence = 0;
        mockStopTimeEntry.put(stopSequence, mockStopTime);
        mockStopTimeCollection.put("trip_id", mockStopTimeEntry);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getStopTimeAll()).thenReturn(mockStopTimeCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final TimeUtils mockTimeUtil = mock(TimeUtils.class);
        when(mockTimeUtil.convertIntegerToHHMMSS(arrivalTime)).thenReturn("arrival_time");
        when(mockTimeUtil.convertIntegerToHHMMSS(departureTime)).thenReturn("departure_time");

        final ValidateStopTimeDepartureTimeAfterArrivalTime underTest =
                new ValidateStopTimeDepartureTimeAfterArrivalTime(mockDataRepo, mockResultRepo,
                        mockTimeUtil, mockLogger);

        underTest.execute();
        verify(mockLogger, times(1)).info("Validating rule 'E045 - `departure_time` and " +
                "`arrival_time` out of order");

        verify(mockDataRepo, times(1)).getStopTimeAll();

        verify(mockStopTime, times(1)).getArrivalTime();
        verify(mockStopTime, times(1)).getDepartureTime();

        final ArgumentCaptor<StopTimeArrivalTimeAfterDepartureTimeNotice> captor =
                ArgumentCaptor.forClass(StopTimeArrivalTimeAfterDepartureTimeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        verify(mockTimeUtil, times(2)).convertIntegerToHHMMSS(anyInt());

        final List<StopTimeArrivalTimeAfterDepartureTimeNotice> noticeList = captor.getAllValues();

        assertEquals("stop_times.txt", noticeList.get(0).getFilename());
        assertEquals("arrival_time", noticeList.get(0).getNoticeSpecific(Notice.KEY_STOP_TIME_ARRIVAL_TIME));
        assertEquals("departure_time", noticeList.get(0)
                .getNoticeSpecific(Notice.KEY_STOP_TIME_DEPARTURE_TIME));
        assertEquals("tripId", noticeList.get(0)
                .getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stopSequence", noticeList.get(0)
                .getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("trip_id", noticeList.get(0)
                .getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(0, noticeList.get(0)
                .getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_SECOND_VALUE));
        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo, mockTimeUtil);
    }

    // suppressed warning regarding ignored result of method since it is not necessary here
    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void stopTimeWithNullArrivalTimeShouldNotGenerateNotice() {
        final StopTime mockStopTime = mock(StopTime.class);
        final Integer arrivalTime = null;
        final Integer departureTime = 60;
        when(mockStopTime.getArrivalTime()).thenReturn(arrivalTime);
        when(mockStopTime.getDepartureTime()).thenReturn(departureTime);

        final Map<String, TreeMap<Integer, StopTime>> mockStopTimeCollection = new HashMap<>();
        final TreeMap<Integer, StopTime> mockStopTimeEntry = new TreeMap<>();
        final Integer stopSequence = 0;
        mockStopTimeEntry.put(stopSequence, mockStopTime);
        mockStopTimeCollection.put("trip_id", mockStopTimeEntry);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getStopTimeAll()).thenReturn(mockStopTimeCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final TimeUtils mockTimeUtil = mock(TimeUtils.class);

        final ValidateStopTimeDepartureTimeAfterArrivalTime underTest =
                new ValidateStopTimeDepartureTimeAfterArrivalTime(mockDataRepo, mockResultRepo,
                        mockTimeUtil, mockLogger);

        underTest.execute();
        verify(mockLogger, times(1)).info("Validating rule 'E045 - `departure_time` and " +
                "`arrival_time` out of order");

        verify(mockDataRepo, times(1)).getStopTimeAll();

        verify(mockStopTime, times(1)).getArrivalTime();
        verify(mockStopTime, times(1)).getDepartureTime();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockStopTime, mockTimeUtil);
    }

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void stopTimeWithNullDepartureTimeShouldNotGenerateNotice() {
        final StopTime mockStopTime = mock(StopTime.class);
        final Integer arrivalTime = 0;
        final Integer departureTime = null;
        when(mockStopTime.getArrivalTime()).thenReturn(arrivalTime);
        when(mockStopTime.getDepartureTime()).thenReturn(departureTime);

        final Map<String, TreeMap<Integer, StopTime>> mockStopTimeCollection = new HashMap<>();
        final TreeMap<Integer, StopTime> mockStopTimeEntry = new TreeMap<>();
        final Integer stopSequence = 0;
        mockStopTimeEntry.put(stopSequence, mockStopTime);
        mockStopTimeCollection.put("trip_id", mockStopTimeEntry);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getStopTimeAll()).thenReturn(mockStopTimeCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final TimeUtils mockTimeUtil = mock(TimeUtils.class);

        final ValidateStopTimeDepartureTimeAfterArrivalTime underTest =
                new ValidateStopTimeDepartureTimeAfterArrivalTime(mockDataRepo, mockResultRepo,
                        mockTimeUtil, mockLogger);

        underTest.execute();
        verify(mockLogger, times(1)).info("Validating rule 'E045 - `departure_time` and " +
                "`arrival_time` out of order");

        verify(mockDataRepo, times(1)).getStopTimeAll();

        verify(mockStopTime, times(1)).getArrivalTime();
        verify(mockStopTime, times(1)).getDepartureTime();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockStopTime, mockTimeUtil);
    }
}
