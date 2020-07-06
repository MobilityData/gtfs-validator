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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FloatFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.usecase.distancecalculationutils.DistanceCalculationUtils;
import org.mobilitydata.gtfsvalidator.usecase.distancecalculationutils.DistanceUnit;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.*;
import static org.mockito.Mockito.*;

class ValidateShapeDistTraveledTest {
    private static final int STOP_SEQUENCE_VALUE = 2;

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void stoptimeWithNullShapeDistTravelledShouldNotGenerateNotice() {
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, TreeMap<Integer, StopTime>> mockStopTimeCollection = new HashMap<>();
        final TreeMap<Integer, StopTime> innerMap = new TreeMap<>();
        final StopTime mockStopTime = mock(StopTime.class);
        when(mockStopTime.getShapeDistTraveled()).thenReturn(null);

        innerMap.put(STOP_SEQUENCE_VALUE, mockStopTime);
        mockStopTimeCollection.put("trip id", innerMap);

        when(mockDataRepo.getStopTimeAll()).thenReturn(mockStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("trip id")).thenReturn(innerMap);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final DistanceCalculationUtils mockDistanceCalculationUtils = mock(DistanceCalculationUtils.class);
        final Logger mockLogger = mock(Logger.class);


        final ValidateShapeDistTraveled underTest =
                new ValidateShapeDistTraveled(mockDataRepo, mockResultRepo, mockDistanceCalculationUtils, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule 'E036 - `If provided," +
                " stop_times.shape_dist_traveled` must be smaller or equal to shape total distance" +
                System.lineSeparator());

        verify(mockDataRepo, times(1)).getStopTimeAll();

        verify(mockDataRepo, times(1)).getStopTimeByTripId(ArgumentMatchers.eq("trip id"));

        verify(mockStopTime, times(1)).getShapeDistTraveled();
        verifyNoMoreInteractions(mockDataRepo, mockResultRepo, mockDistanceCalculationUtils, mockLogger, mockStopTime);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void stoptimeWithTooBigShapeDistTravelledShouldGenerateNotice(){
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, TreeMap<Integer, StopTime>> mockStopTimeCollection = new HashMap<>();
        final TreeMap<Integer, StopTime> innerMap = new TreeMap<>();
        final StopTime mockStopTime = mock(StopTime.class);
        when(mockStopTime.getShapeDistTraveled()).thenReturn(1800f); // kilometers

        innerMap.put(STOP_SEQUENCE_VALUE, mockStopTime);
        mockStopTimeCollection.put("trip id", innerMap);

        final Trip mockTrip = mock(Trip.class);
        when(mockTrip.getShapeId()).thenReturn("shape id");

        final TreeMap<Integer, ShapePoint> mockShape = new TreeMap<>();
        final ShapePoint mockShapePoint = mock(ShapePoint.class);
        mockShape.put(3, mockShapePoint);

        when(mockDataRepo.getStopTimeAll()).thenReturn(mockStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("trip id")).thenReturn(innerMap);
        when(mockDataRepo.getTripById("trip id")).thenReturn(mockTrip);
        when(mockDataRepo.getShapeById("shape id")).thenReturn(mockShape);


        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final DistanceCalculationUtils mockDistanceCalculationUtils = mock(DistanceCalculationUtils.class);
        when(mockDistanceCalculationUtils.getShapeTotalDistance(mockShape, DistanceUnit.KILOMETER)).thenReturn(1000d);

        final Logger mockLogger = mock(Logger.class);

        final ValidateShapeDistTraveled underTest =
                new ValidateShapeDistTraveled(mockDataRepo, mockResultRepo, mockDistanceCalculationUtils, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule 'E036 - `If provided," +
                " stop_times.shape_dist_traveled` must be smaller or equal to shape total distance" +
                System.lineSeparator());

        verify(mockDataRepo, times(1)).getStopTimeAll();
        verify(mockDataRepo, times(1)).getStopTimeByTripId(ArgumentMatchers.eq("trip id"));

        verify(mockStopTime, times(1)).getShapeDistTraveled();

        verify(mockDataRepo, times(1)).getTripById(ArgumentMatchers.eq("trip id"));

        verify(mockTrip, times(1)).getShapeId();

        verify(mockDataRepo, times(1)).getShapeById(ArgumentMatchers.eq("shape id"));

        verify(mockDistanceCalculationUtils, times(1))
                .getShapeTotalDistance(ArgumentMatchers.eq(mockShape), ArgumentMatchers.eq(DistanceUnit.KILOMETER));

        final ArgumentCaptor<FloatFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(FloatFieldValueOutOfRangeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<FloatFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("stop_times.txt", noticeList.get(0).getFilename());
        assertEquals("shape_dist_traveled", noticeList.get(0).getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(0f, noticeList.get(0).getNoticeSpecific(KEY_RANGE_MIN));
        assertEquals(1000f, noticeList.get(0).getNoticeSpecific(KEY_RANGE_MAX));
        assertEquals(1800f, noticeList.get(0).getNoticeSpecific(KEY_ACTUAL_VALUE));
        assertEquals("trip_id", noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stop_sequence", noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("trip id", noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(STOP_SEQUENCE_VALUE, noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));

        verifyNoMoreInteractions(mockDataRepo, mockResultRepo, mockDistanceCalculationUtils, mockLogger, mockStopTime,
                mockShapePoint, mockTrip);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void shapeIdNotInDatasetShouldNotGenerateNotice() {
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, TreeMap<Integer, StopTime>> mockStopTimeCollection = new HashMap<>();
        final TreeMap<Integer, StopTime> innerMap = new TreeMap<>();
        final StopTime mockStopTime = mock(StopTime.class);
        when(mockStopTime.getShapeDistTraveled()).thenReturn(800f);

        innerMap.put(STOP_SEQUENCE_VALUE, mockStopTime);
        mockStopTimeCollection.put("trip id", innerMap);

        final Trip mockTrip = mock(Trip.class);
        when(mockTrip.getShapeId()).thenReturn("undefined shape id");

        when(mockDataRepo.getStopTimeAll()).thenReturn(mockStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("trip id")).thenReturn(innerMap);
        when(mockDataRepo.getShapeById("undefined shape id")).thenReturn(null);
        when(mockDataRepo.getTripById("trip id")).thenReturn(mockTrip);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final DistanceCalculationUtils mockDistanceCalculationUtils = mock(DistanceCalculationUtils.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateShapeDistTraveled underTest =
                new ValidateShapeDistTraveled(mockDataRepo, mockResultRepo, mockDistanceCalculationUtils, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule 'E036 - `If provided," +
                " stop_times.shape_dist_traveled` must be smaller or equal to shape total distance" +
                System.lineSeparator());

        verify(mockDataRepo, times(1)).getStopTimeAll();
        verify(mockDataRepo, times(1)).getStopTimeByTripId(ArgumentMatchers.eq("trip id"));
        verify(mockStopTime, times(1)).getShapeDistTraveled();
        verify(mockDataRepo, times(1)).getTripById("trip id");
        verify(mockTrip, times(1)).getShapeId();
        verify(mockDataRepo, times(1)).getShapeById("undefined shape id");
        verifyNoMoreInteractions(mockDataRepo, mockResultRepo, mockDistanceCalculationUtils, mockLogger, mockStopTime,
                mockTrip);
    }
}
