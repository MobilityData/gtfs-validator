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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.NonExistingShapeNotice;
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

class ValidateShapeIdReferenceInStopTimeTest {

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void stopTimeWithNullShapeDistTravelledShouldNotGenerateNotice() {
        final StopTime mockStopTime = mock(StopTime.class);
        when(mockStopTime.getTripId()).thenReturn("trip id");
        when(mockStopTime.getStopSequence()).thenReturn(2);
        when(mockStopTime.getShapeDistTraveled()).thenReturn(null);
        final Map<String, TreeMap<Integer, StopTime>> mockStopTimeCollection = new HashMap<>();
        final TreeMap<Integer, StopTime> innerMap = new TreeMap<>();
        innerMap.put(2, mockStopTime);
        mockStopTimeCollection.put("trip id", innerMap);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getStopTimeAll()).thenReturn(mockStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("trip id")).thenReturn(innerMap);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateShapeIdReferenceInStopTime underTest =
                new ValidateShapeIdReferenceInStopTime(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info(ArgumentMatchers.eq(
                "Validating rule 'E034 - shape_id must be provided and valid when" +
                        " stop_times.shape_dist_travelled is provided'" + System.lineSeparator()));

        verify(mockDataRepo, times(1)).getStopTimeAll();
        verify(mockDataRepo, times(1))
                .getStopTimeByTripId(ArgumentMatchers.eq("trip id"));

        verify(mockStopTime, times(1)).getShapeDistTraveled();

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo, mockStopTime);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void stopTimeWithNonNullShapeDistTravelledPlusInvalidTripIdShouldNotGenerateNotice() {
        final StopTime mockStopTime = mock(StopTime.class);
        when(mockStopTime.getTripId()).thenReturn("trip id");
        when(mockStopTime.getStopSequence()).thenReturn(2);
        when(mockStopTime.getShapeDistTraveled()).thenReturn(5f);
        final Map<String, TreeMap<Integer, StopTime>> mockStopTimeCollection = new HashMap<>();
        final TreeMap<Integer, StopTime> innerMap = new TreeMap<>();
        innerMap.put(2, mockStopTime);
        mockStopTimeCollection.put("non existing trip id", innerMap);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getStopTimeAll()).thenReturn(mockStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("non existing trip id")).thenReturn(innerMap);
        when(mockDataRepo.getTripById("non existing trip id")).thenReturn(null);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateShapeIdReferenceInStopTime underTest =
                new ValidateShapeIdReferenceInStopTime(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info(ArgumentMatchers.eq(
                "Validating rule 'E034 - shape_id must be provided and valid when" +
                        " stop_times.shape_dist_travelled is provided'" + System.lineSeparator()));

        verify(mockDataRepo, times(1)).getStopTimeAll();
        verify(mockDataRepo, times(1))
                .getStopTimeByTripId(ArgumentMatchers.eq("non existing trip id"));
        verify(mockDataRepo, times(1)).getTripById("non existing trip id");

        verify(mockStopTime, times(1)).getShapeDistTraveled();

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo, mockStopTime);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void stopTimeWithNonNullShapeDistTravelledPlusValidTripIdAndNullShapeIdShouldGenerateNotice() {
        final StopTime mockStopTime = mock(StopTime.class);
        when(mockStopTime.getTripId()).thenReturn("trip id");
        when(mockStopTime.getStopSequence()).thenReturn(2);
        when(mockStopTime.getShapeDistTraveled()).thenReturn(5f);
        final Map<String, TreeMap<Integer, StopTime>> mockStopTimeCollection = new HashMap<>();
        final TreeMap<Integer, StopTime> innerMap = new TreeMap<>();
        innerMap.put(2, mockStopTime);
        mockStopTimeCollection.put("trip id", innerMap);

        final Trip mockTrip = mock(Trip.class);
        when(mockTrip.getShapeId()).thenReturn(null);
        when(mockTrip.getTripId()).thenReturn("trip id");

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getStopTimeAll()).thenReturn(mockStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("trip id")).thenReturn(innerMap);
        when(mockDataRepo.getTripById("trip id")).thenReturn(mockTrip);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateShapeIdReferenceInStopTime underTest =
                new ValidateShapeIdReferenceInStopTime(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info(ArgumentMatchers.eq(
                "Validating rule 'E034 - shape_id must be provided and valid when" +
                        " stop_times.shape_dist_travelled is provided'" + System.lineSeparator()));

        verify(mockDataRepo, times(1)).getStopTimeAll();
        verify(mockDataRepo, times(1))
                .getStopTimeByTripId(ArgumentMatchers.eq("trip id"));
        verify(mockDataRepo, times(1)).getTripById("trip id");

        verify(mockStopTime, times(1)).getShapeDistTraveled();
        verify(mockTrip, times(1)).getShapeId();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("shape_id", noticeList.get(0).getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("trip id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo, mockStopTime, mockTrip);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void stopTimeWithNonNullShapeDistTravelledPlusValidTripIdAndInvalidShapeIdShouldGenerateNotice() {
        final StopTime mockStopTime = mock(StopTime.class);
        when(mockStopTime.getTripId()).thenReturn("trip id");
        when(mockStopTime.getStopSequence()).thenReturn(2);
        when(mockStopTime.getShapeDistTraveled()).thenReturn(5f);
        final Map<String, TreeMap<Integer, StopTime>> mockStopTimeCollection = new HashMap<>();
        final TreeMap<Integer, StopTime> innerMap = new TreeMap<>();
        innerMap.put(2, mockStopTime);
        mockStopTimeCollection.put("trip id", innerMap);

        final Trip mockTrip = mock(Trip.class);
        when(mockTrip.getShapeId()).thenReturn(null);
        when(mockTrip.getTripId()).thenReturn("trip id");
        when(mockTrip.getShapeId()).thenReturn("non existing shape id");

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getStopTimeAll()).thenReturn(mockStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("trip id")).thenReturn(innerMap);
        when(mockDataRepo.getTripById("trip id")).thenReturn(null);
        when(mockDataRepo.getTripById("trip id")).thenReturn(mockTrip);
        when(mockDataRepo.getShapeById("non existing shape id")).thenReturn(null);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateShapeIdReferenceInStopTime underTest =
                new ValidateShapeIdReferenceInStopTime(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info(ArgumentMatchers.eq(
                "Validating rule 'E034 - shape_id must be provided and valid when" +
                        " stop_times.shape_dist_travelled is provided'" + System.lineSeparator()));

        verify(mockDataRepo, times(1)).getStopTimeAll();
        verify(mockDataRepo, times(1))
                .getStopTimeByTripId(ArgumentMatchers.eq("trip id"));
        verify(mockDataRepo, times(1)).getTripById("trip id");
        verify(mockDataRepo, times(1)).getShapeById("non existing shape id");

        verify(mockStopTime, times(1)).getShapeDistTraveled();
        verify(mockTrip, times(1)).getShapeId();

        final ArgumentCaptor<NonExistingShapeNotice> captor =
                ArgumentCaptor.forClass(NonExistingShapeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<NonExistingShapeNotice> noticeList = captor.getAllValues();

        assertEquals("stop_times.txt", noticeList.get(0).getFilename());
        assertEquals("shape_id", noticeList.get(0).getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("trip_id", noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stop_sequence", noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("trip id", noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(2, noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals("non existing shape id", noticeList.get(0).getNoticeSpecific(KEY_UNKNOWN_SHAPE_ID));

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo, mockStopTime, mockTrip);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void stopTimeWithNonNullShapeDistTravelledPlusValidTripIdAndNullAndValidShapeIdShouldNotGenerateNotice() {
        final StopTime mockStopTime = mock(StopTime.class);
        when(mockStopTime.getTripId()).thenReturn("trip id");
        when(mockStopTime.getStopSequence()).thenReturn(2);
        when(mockStopTime.getShapeDistTraveled()).thenReturn(5f);
        final Map<String, TreeMap<Integer, StopTime>> mockStopTimeCollection = new HashMap<>();
        final TreeMap<Integer, StopTime> innerMap = new TreeMap<>();
        innerMap.put(2, mockStopTime);
        mockStopTimeCollection.put("trip id", innerMap);

        final Trip mockTrip = mock(Trip.class);
        when(mockTrip.getShapeId()).thenReturn(null);
        when(mockTrip.getTripId()).thenReturn("trip id");
        when(mockTrip.getShapeId()).thenReturn("non existing shape id");

        final ShapePoint mockShapePoint = mock(ShapePoint.class);
        final TreeMap<Integer, ShapePoint> mockShapePointCollection = new TreeMap<>();
        mockShapePointCollection.put(28, mockShapePoint);
        mockStopTimeCollection.put("trip id", innerMap);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getStopTimeAll()).thenReturn(mockStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("trip id")).thenReturn(innerMap);
        when(mockDataRepo.getTripById("trip id")).thenReturn(null);
        when(mockDataRepo.getTripById("trip id")).thenReturn(mockTrip);
        when(mockDataRepo.getShapeById("shape id")).thenReturn(mockShapePointCollection);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateShapeIdReferenceInStopTime underTest =
                new ValidateShapeIdReferenceInStopTime(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info(ArgumentMatchers.eq(
                "Validating rule 'E034 - shape_id must be provided and valid when" +
                        " stop_times.shape_dist_travelled is provided'" + System.lineSeparator()));

        verify(mockDataRepo, times(1)).getStopTimeAll();
        verify(mockDataRepo, times(1))
                .getStopTimeByTripId(ArgumentMatchers.eq("trip id"));
        verify(mockDataRepo, times(1)).getTripById("trip id");
        verify(mockDataRepo, times(1)).getShapeById("non existing shape id");

        verify(mockStopTime, times(1)).getShapeDistTraveled();
        verify(mockTrip, times(1)).getShapeId();

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo, mockStopTime, mockTrip);
    }
}
