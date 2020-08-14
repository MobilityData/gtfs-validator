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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stops.LocationBase;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FastTravelBetweenStopsNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.GeospatialUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.*;
import static org.mockito.Mockito.*;

class ValidateTripTravelSpeedTest {

    @Test
    void tripWithFastTravelContiguousStopsShouldGenerateNotice() {

        final LocationBase mockStop0 = mock(LocationBase.class);
        when(mockStop0.getStopLat()).thenReturn(45.508888f);
        when(mockStop0.getStopLon()).thenReturn(-73.561668f);

        final GeospatialUtils mockGeoUtil = mock(GeospatialUtils.class);
        when(mockGeoUtil.distanceBetweenMeter(
                ArgumentMatchers.anyDouble(),
                ArgumentMatchers.anyDouble(),
                ArgumentMatchers.anyDouble(),
                ArgumentMatchers.anyDouble()
        )).thenReturn(10000);

        final StopTime mockStopTime0 = mock(StopTime.class);
        final StopTime mockStopTime1 = mock(StopTime.class);
        final StopTime mockStopTime2 = mock(StopTime.class);
        when(mockStopTime0.getStopId()).thenReturn("stopTime0_stopId");
        when(mockStopTime0.getDepartureTime()).thenReturn(130);
        when(mockStopTime0.getArrivalTime()).thenReturn(100);

        when(mockStopTime1.getStopId()).thenReturn("stopTime1_stopId");
        when(mockStopTime1.getDepartureTime()).thenReturn(230);
        when(mockStopTime1.getArrivalTime()).thenReturn(200);

        when(mockStopTime2.getStopId()).thenReturn("stopTime2_stopId");
        when(mockStopTime2.getDepartureTime()).thenReturn(33000);
        when(mockStopTime2.getArrivalTime()).thenReturn(30000);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);

        when(mockDataRepo.getTripAll()).thenReturn(Map.of("trip_id__test", mock(Trip.class)));

        SortedMap<Integer, StopTime> mockSortedMap =
                new TreeMap<>(
                        Map.of(0, mockStopTime0, 1, mockStopTime1, 2, mockStopTime2)
                );

        when(mockDataRepo.getStopTimeByTripId(ArgumentMatchers.anyString())).thenReturn(mockSortedMap);

        when(mockDataRepo.getStopById(ArgumentMatchers.anyString())).thenReturn(mockStop0);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateTripTravelSpeed underTest =
                new ValidateTripTravelSpeed(mockDataRepo, mockResultRepo, mockGeoUtil, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info(ArgumentMatchers.eq(
                "Validating rule E046 - Fast travel between stops"));

        verify(mockDataRepo, times(1)).getTripAll();
        verify(mockDataRepo, times(1)).getStopTimeByTripId(ArgumentMatchers.eq("trip_id__test"));
        verify(mockDataRepo, times(1)).getStopById(ArgumentMatchers.eq("stopTime0_stopId"));
        verify(mockDataRepo, times(1)).getStopById(ArgumentMatchers.eq("stopTime1_stopId"));
        verify(mockDataRepo, times(1)).getStopById(ArgumentMatchers.eq("stopTime2_stopId"));

        final ArgumentCaptor<FastTravelBetweenStopsNotice> captor =
                ArgumentCaptor.forClass(FastTravelBetweenStopsNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        FastTravelBetweenStopsNotice notice = captor.getValue();

        assertEquals("stop_times.txt", notice.getFilename());
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("trip_id__test", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals("stop_sequence_list", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(List.of(0, 1), notice.getNoticeSpecific(KEY_STOP_TIME_STOP_SEQUENCE_LIST));

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo);
    }

    @Test
    void tripWithFastTravelFarStopsShouldGenerateNotice() {

        final LocationBase mockStop0 = mock(LocationBase.class);
        when(mockStop0.getStopLat()).thenReturn(45.508888f);
        when(mockStop0.getStopLon()).thenReturn(-73.561668f);

        final GeospatialUtils mockGeoUtil = mock(GeospatialUtils.class);
        when(mockGeoUtil.distanceBetweenMeter(
                ArgumentMatchers.anyDouble(),
                ArgumentMatchers.anyDouble(),
                ArgumentMatchers.anyDouble(),
                ArgumentMatchers.anyDouble()
        )).thenReturn(10000);

        final StopTime mockStopTime0 = mock(StopTime.class);
        final StopTime mockStopTime1 = mock(StopTime.class);
        final StopTime mockStopTime2 = mock(StopTime.class);
        final StopTime mockStopTime3 = mock(StopTime.class);
        when(mockStopTime0.getStopId()).thenReturn("stopTime0_stopId");
        when(mockStopTime0.getDepartureTime()).thenReturn(3130);
        when(mockStopTime0.getArrivalTime()).thenReturn(100);

        when(mockStopTime1.getStopId()).thenReturn("stopTime1_stopId");
        when(mockStopTime1.getDepartureTime()).thenReturn(5200);
        when(mockStopTime1.getArrivalTime()).thenReturn(5200);

        when(mockStopTime2.getStopId()).thenReturn("stopTime2_stopId");
        when(mockStopTime2.getDepartureTime()).thenReturn(5200);
        when(mockStopTime2.getArrivalTime()).thenReturn(5200);

        when(mockStopTime3.getStopId()).thenReturn("stopTime3_stopId");
        when(mockStopTime3.getDepartureTime()).thenReturn(5400);
        when(mockStopTime3.getArrivalTime()).thenReturn(5300);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);

        when(mockDataRepo.getTripAll()).thenReturn(Map.of("trip_id__test", mock(Trip.class)));

        SortedMap<Integer, StopTime> mockSortedMap =
                new TreeMap<>(
                        Map.of(0, mockStopTime0, 1, mockStopTime1, 2, mockStopTime2, 3, mockStopTime3)
                );

        when(mockDataRepo.getStopTimeByTripId(ArgumentMatchers.anyString())).thenReturn(mockSortedMap);

        when(mockDataRepo.getStopById(ArgumentMatchers.anyString())).thenReturn(mockStop0);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateTripTravelSpeed underTest =
                new ValidateTripTravelSpeed(mockDataRepo, mockResultRepo, mockGeoUtil, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info(ArgumentMatchers.eq(
                "Validating rule E046 - Fast travel between stops"));

        verify(mockDataRepo, times(1)).getTripAll();
        verify(mockDataRepo, times(1)).getStopTimeByTripId(ArgumentMatchers.eq("trip_id__test"));
        verify(mockDataRepo, times(1)).getStopById(ArgumentMatchers.eq("stopTime0_stopId"));
        verify(mockDataRepo, times(1)).getStopById(ArgumentMatchers.eq("stopTime1_stopId"));
        verify(mockDataRepo, times(1)).getStopById(ArgumentMatchers.eq("stopTime2_stopId"));
        verify(mockDataRepo, times(1)).getStopById(ArgumentMatchers.eq("stopTime3_stopId"));

        final ArgumentCaptor<FastTravelBetweenStopsNotice> captor =
                ArgumentCaptor.forClass(FastTravelBetweenStopsNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        FastTravelBetweenStopsNotice notice = captor.getValue();

        assertEquals("stop_times.txt", notice.getFilename());
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("trip_id__test", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals("stop_sequence_list", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(List.of(1, 2, 3), notice.getNoticeSpecific(KEY_STOP_TIME_STOP_SEQUENCE_LIST));

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo);
    }

    @Test
    void noFastTravelShouldNotGenerateNotice() {

        final LocationBase mockStop0 = mock(LocationBase.class);
        when(mockStop0.getStopLat()).thenReturn(45.508888f);
        when(mockStop0.getStopLon()).thenReturn(-73.561668f);

        final GeospatialUtils mockGeoUtil = mock(GeospatialUtils.class);
        when(mockGeoUtil.distanceBetweenMeter(
                ArgumentMatchers.anyDouble(),
                ArgumentMatchers.anyDouble(),
                ArgumentMatchers.anyDouble(),
                ArgumentMatchers.anyDouble()
        )).thenReturn(10);

        final StopTime mockStopTime0 = mock(StopTime.class);
        final StopTime mockStopTime1 = mock(StopTime.class);
        final StopTime mockStopTime2 = mock(StopTime.class);
        final StopTime mockStopTime3 = mock(StopTime.class);
        when(mockStopTime0.getStopId()).thenReturn("stopTime0_stopId");
        when(mockStopTime0.getDepartureTime()).thenReturn(130);
        when(mockStopTime0.getArrivalTime()).thenReturn(100);

        when(mockStopTime1.getStopId()).thenReturn("stopTime1_stopId");
        when(mockStopTime1.getDepartureTime()).thenReturn(230);
        when(mockStopTime1.getArrivalTime()).thenReturn(200);

        when(mockStopTime2.getStopId()).thenReturn("stopTime2_stopId");
        when(mockStopTime2.getDepartureTime()).thenReturn(230);
        when(mockStopTime2.getArrivalTime()).thenReturn(230);

        when(mockStopTime3.getStopId()).thenReturn("stopTime3_stopId");
        when(mockStopTime3.getDepartureTime()).thenReturn(430);
        when(mockStopTime3.getArrivalTime()).thenReturn(400);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);

        when(mockDataRepo.getTripAll()).thenReturn(Map.of("trip_id__test", mock(Trip.class)));

        SortedMap<Integer, StopTime> mockSortedMap =
                new TreeMap<>(
                        Map.of(0, mockStopTime0, 1, mockStopTime1, 2, mockStopTime2, 3, mockStopTime3)
                );

        when(mockDataRepo.getStopTimeByTripId(ArgumentMatchers.anyString())).thenReturn(mockSortedMap);

        when(mockDataRepo.getStopById(ArgumentMatchers.anyString())).thenReturn(mockStop0);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateTripTravelSpeed underTest =
                new ValidateTripTravelSpeed(mockDataRepo, mockResultRepo, mockGeoUtil, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info(ArgumentMatchers.eq(
                "Validating rule E046 - Fast travel between stops"));

        verify(mockDataRepo, times(1)).getTripAll();
        verify(mockDataRepo, times(1)).getStopTimeByTripId(ArgumentMatchers.eq("trip_id__test"));
        verify(mockDataRepo, times(1)).getStopById(ArgumentMatchers.eq("stopTime0_stopId"));
        verify(mockDataRepo, times(1)).getStopById(ArgumentMatchers.eq("stopTime1_stopId"));
        verify(mockDataRepo, times(1)).getStopById(ArgumentMatchers.eq("stopTime2_stopId"));
        verify(mockDataRepo, times(1)).getStopById(ArgumentMatchers.eq("stopTime3_stopId"));

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo);
    }
}
