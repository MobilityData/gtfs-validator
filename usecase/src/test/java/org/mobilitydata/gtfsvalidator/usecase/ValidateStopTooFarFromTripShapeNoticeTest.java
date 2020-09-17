/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stops.LocationBase;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stops.StopOrPlatform;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.StopTooFarFromTripShapeNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.GeospatialUtils;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mobilitydata.gtfsvalidator.usecase.utils.GeospatialUtils.TRIP_BUFFER_METERS;
import static org.mockito.Mockito.*;

/**
 * Unit tests to validate behavior of use case for E052 "Stop too far from trip shape". Note that geospatial parts
 * of tests are executed in GeospatialUtilsImplTest in the adapter module as the implementation of the GeospatialUtils
 * interface.
 */
class ValidateStopTooFarFromTripShapeNoticeTest {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void stopWithinTripShapeShouldNotGenerateNotice() {
        // See map of trip shape and stops (in GeoJSON) at https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a

        // stops.txt
        final String stopId1 = "1001";
        final LocationBase stop1 = mock(LocationBase.class);
        when(stop1.getStopId()).thenReturn(stopId1);
        when(stop1.getStopLat()).thenReturn(28.05811731042478f);
        when(stop1.getStopLon()).thenReturn(-82.41616877502503f);

        final String stopId2 = "1002";
        final LocationBase stop2 = mock(LocationBase.class);
        when(stop2.getStopId()).thenReturn(stopId2);
        when(stop2.getStopLat()).thenReturn(28.05812364854794f);
        when(stop2.getStopLon()).thenReturn(-82.41617370439423f);

        // stop_times.txt
        final StopTime stopTime1 = mock(StopTime.class);
        when(stopTime1.getStopSequence()).thenReturn(1);
        when(stopTime1.getStopId()).thenReturn(stopId1);

        final StopTime stopTime2 = mock(StopTime.class);
        when(stopTime2.getStopSequence()).thenReturn(2);
        when(stopTime2.getStopId()).thenReturn(stopId2);

        // shapes.txt
        final String shapeId = "shape1";
        final ShapePoint pt1 = mock(ShapePoint.class);
        final ShapePoint pt2 = mock(ShapePoint.class);
        final ShapePoint pt3 = mock(ShapePoint.class);
        final ShapePoint pt4 = mock(ShapePoint.class);
        final ShapePoint pt5 = mock(ShapePoint.class);

        when(pt1.getShapeId()).thenReturn(shapeId);
        when(pt2.getShapeId()).thenReturn(shapeId);
        when(pt3.getShapeId()).thenReturn(shapeId);
        when(pt4.getShapeId()).thenReturn(shapeId);
        when(pt5.getShapeId()).thenReturn(shapeId);

        when(pt1.getShapePtSequence()).thenReturn(1);
        when(pt2.getShapePtSequence()).thenReturn(2);
        when(pt3.getShapePtSequence()).thenReturn(3);
        when(pt4.getShapePtSequence()).thenReturn(4);
        when(pt5.getShapePtSequence()).thenReturn(5);

        when(pt1.getShapePtLat()).thenReturn(28.05724310653972f);
        when(pt1.getShapePtLon()).thenReturn(-82.41350776611507f);
        when(pt2.getShapePtLat()).thenReturn(28.05746701492806f);
        when(pt2.getShapePtLon()).thenReturn(-82.41493135129478f);
        when(pt3.getShapePtLat()).thenReturn(28.05800068503469f);
        when(pt3.getShapePtLon()).thenReturn(-82.4159394137605f);
        when(pt4.getShapePtLat()).thenReturn(28.05808869825447f);
        when(pt4.getShapePtLon()).thenReturn(-82.41648754043338f);
        when(pt5.getShapePtLat()).thenReturn(28.05809979887893f);
        when(pt5.getShapePtLon()).thenReturn(-82.41773971025437f);

        // trips.txt
        final String tripId = "trip1";
        final Trip trip = mock(Trip.class);
        when(trip.getTripId()).thenReturn(tripId);
        when(trip.getShapeId()).thenReturn(shapeId);

        // Map containing StopTime entities. Entities are mapped on keys from GTFS file stop_times.txt:
        // - trip_id
        // - stop_sequence
        final Map<String, TreeMap<Integer, StopTime>> stopTimeCollection = new HashMap<>(1);
        final TreeMap<Integer, StopTime> stopTimes = new TreeMap<>(Map.of(1, stopTime1, 2, stopTime2));
        stopTimeCollection.put(tripId, stopTimes);

        // Map containing Stop entities. Entities are keyed on GTFS stops.txt stop_id
        final Map<String, LocationBase> stopPerId = new HashMap<>(Map.of(stopId1, stop1, stopId2, stop2));

        // Entities are keyed on shape_pt_sequence of GTFS file shapes.txt
        SortedMap<Integer, ShapePoint> points = new TreeMap<>(Map.of(1, pt1, 2, pt2, 3, pt3, 4, pt4, 5, pt5));

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getStopAll()).thenReturn(stopPerId);
        when(mockDataRepo.getStopTimeAll()).thenReturn(stopTimeCollection);
        when(mockDataRepo.getTripById(tripId)).thenReturn(trip);
        when(mockDataRepo.getShapeById(shapeId)).thenReturn(points);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final GeospatialUtils mockGeoUtil = mock(GeospatialUtils.class);

        final ValidateStopTooFarFromTripShape underTest =
                new ValidateStopTooFarFromTripShape(mockDataRepo, mockResultRepo,
                        mockGeoUtil, mockLogger);

        underTest.execute();
        verify(mockLogger, times(1)).info("Validating rule 'E052 - Stop too far from trip shape'");

        verify(mockGeoUtil, times(1)).checkStopsWithinTripShape(trip, stopTimes, points, stopPerId, new HashSet<>());

        verify(mockDataRepo, times(1)).getStopTimeAll();
        verify(mockDataRepo, times(1)).getShapeById(shapeId);
        verify(mockDataRepo, times(1)).getTripById(tripId);
        verify(mockDataRepo, times(1)).getStopAll();

        verify(trip, times(2)).getShapeId();

        verifyNoMoreInteractions(stop1);
        verifyNoMoreInteractions(stop2);
        verifyNoMoreInteractions(trip);
        verifyNoMoreInteractions(mockGeoUtil);

        verifyNoInteractions(mockResultRepo);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void stopOutsideTripShapeShouldGenerateNotice() {
        // See map of trip shape and stops (in GeoJSON) at https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a

        // stops.txt
        final String stopId1 = "1001";
        final LocationBase stop1 = mock(LocationBase.class);
        when(stop1.getStopId()).thenReturn(stopId1);
        when(stop1.getStopLat()).thenReturn(28.05811731042478f);
        when(stop1.getStopLon()).thenReturn(-82.41616877502503f);

        final String stopId2 = "1002";
        final LocationBase stop2 = mock(LocationBase.class);
        when(stop2.getStopId()).thenReturn(stopId2);
        when(stop2.getStopLat()).thenReturn(28.05812364854794f);
        when(stop2.getStopLon()).thenReturn(-82.41617370439423f);

        final String stopId3 = "1003";
        final StopOrPlatform stop3 = mock(StopOrPlatform.class);
        when(stop3.getStopId()).thenReturn(stopId3);
        // Location OUTSIDE buffer
        when(stop3.getStopLat()).thenReturn(28.05673053256373f);
        when(stop3.getStopLon()).thenReturn(-82.4170801432763f);

        // stop_times.txt
        final StopTime stopTime1 = mock(StopTime.class);
        when(stopTime1.getStopSequence()).thenReturn(1);
        when(stopTime1.getStopId()).thenReturn(stopId1);

        final StopTime stopTime2 = mock(StopTime.class);
        when(stopTime2.getStopSequence()).thenReturn(2);
        when(stopTime2.getStopId()).thenReturn(stopId2);

        final StopTime stopTime3 = mock(StopTime.class);
        when(stopTime3.getStopSequence()).thenReturn(3);
        when(stopTime3.getStopId()).thenReturn(stopId3);

        // shapes.txt
        final String shapeId = "shape1";
        final ShapePoint pt1 = mock(ShapePoint.class);
        final ShapePoint pt2 = mock(ShapePoint.class);
        final ShapePoint pt3 = mock(ShapePoint.class);
        final ShapePoint pt4 = mock(ShapePoint.class);
        final ShapePoint pt5 = mock(ShapePoint.class);

        when(pt1.getShapeId()).thenReturn(shapeId);
        when(pt2.getShapeId()).thenReturn(shapeId);
        when(pt3.getShapeId()).thenReturn(shapeId);
        when(pt4.getShapeId()).thenReturn(shapeId);
        when(pt5.getShapeId()).thenReturn(shapeId);

        when(pt1.getShapePtSequence()).thenReturn(1);
        when(pt2.getShapePtSequence()).thenReturn(2);
        when(pt3.getShapePtSequence()).thenReturn(3);
        when(pt4.getShapePtSequence()).thenReturn(4);
        when(pt5.getShapePtSequence()).thenReturn(5);

        when(pt1.getShapePtLat()).thenReturn(28.05724310653972f);
        when(pt1.getShapePtLon()).thenReturn(-82.41350776611507f);
        when(pt2.getShapePtLat()).thenReturn(28.05746701492806f);
        when(pt2.getShapePtLon()).thenReturn(-82.41493135129478f);
        when(pt3.getShapePtLat()).thenReturn(28.05800068503469f);
        when(pt3.getShapePtLon()).thenReturn(-82.4159394137605f);
        when(pt4.getShapePtLat()).thenReturn(28.05808869825447f);
        when(pt4.getShapePtLon()).thenReturn(-82.41648754043338f);
        when(pt5.getShapePtLat()).thenReturn(28.05809979887893f);
        when(pt5.getShapePtLon()).thenReturn(-82.41773971025437f);

        // trips.txt
        final String tripId = "trip1";
        final Trip trip = mock(Trip.class);
        when(trip.getTripId()).thenReturn(tripId);
        when(trip.getShapeId()).thenReturn(shapeId);

        // Map containing StopTime entities. Entities are mapped on keys from GTFS file stop_times.txt:
        // - trip_id
        // - stop_sequence
        final Map<String, TreeMap<Integer, StopTime>> stopTimeCollection = new HashMap<>(1);
        final TreeMap<Integer, StopTime> stopTimes = new TreeMap<>(Map.of(1, stopTime1, 2, stopTime2, 3, stopTime3));
        stopTimeCollection.put(tripId, stopTimes);

        // Map containing Stop entities. Entities are keyed on GTFS stops.txt stop_id
        final Map<String, LocationBase> stopPerId = new HashMap<>(Map.of(stopId1, stop1, stopId2, stop2, stopId3, stop3));

        // Entities are keyed on shape_pt_sequence of GTFS file shapes.txt
        SortedMap<Integer, ShapePoint> points = new TreeMap<>(Map.of(1, pt1, 2, pt2, 3, pt3, 4, pt4, 5, pt5));

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getStopAll()).thenReturn(stopPerId);
        when(mockDataRepo.getStopTimeAll()).thenReturn(stopTimeCollection);
        when(mockDataRepo.getTripById(tripId)).thenReturn(trip);
        when(mockDataRepo.getShapeById(shapeId)).thenReturn(points);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final GeospatialUtils mockGeoUtil = mock(GeospatialUtils.class);
        Set<String> testedCache = new HashSet<>();
        StopTooFarFromTripShapeNotice stopTooFarFromTripShapeNotice = new StopTooFarFromTripShapeNotice(
                stopId3,
                3,
                trip.getTripId(),
                trip.getShapeId(),
                TRIP_BUFFER_METERS);
        // Mock a call to the GeospatialUtils implementation and an error response
        when(mockGeoUtil.checkStopsWithinTripShape(trip,
                stopTimes,
                points,
                stopPerId,
                testedCache)).thenReturn(List.of(stopTooFarFromTripShapeNotice));

        final ValidateStopTooFarFromTripShape underTest =
                new ValidateStopTooFarFromTripShape(mockDataRepo, mockResultRepo,
                        mockGeoUtil, mockLogger);

        underTest.execute();
        verify(mockLogger, times(1)).info("Validating rule 'E052 - Stop too far from trip shape'");

        verify(mockGeoUtil, times(1)).checkStopsWithinTripShape(trip, stopTimes, points, stopPerId, testedCache);

        verify(mockDataRepo, times(1)).getStopTimeAll();
        verify(mockDataRepo, times(1)).getShapeById(shapeId);
        verify(mockDataRepo, times(1)).getTripById(tripId);
        verify(mockDataRepo, times(1)).getStopAll();

        final ArgumentCaptor<StopTooFarFromTripShapeNotice> captor =
                ArgumentCaptor.forClass(StopTooFarFromTripShapeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<StopTooFarFromTripShapeNotice> noticeList = captor.getAllValues();

        assertEquals(1, noticeList.size());
        assertEquals("shapes.txt", noticeList.get(0).getFilename());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals("ERROR", noticeList.get(0).getLevel());
        assertEquals(52, noticeList.get(0).getCode());

        assertEquals(TRIP_BUFFER_METERS, noticeList.get(0).getNoticeSpecific(Notice.KEY_EXPECTED_DISTANCE));
        assertEquals("stop_id", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("1003", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals("trip_id", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("trip1", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals("shape_id", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_THIRD_PART));
        assertEquals("shape1", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_THIRD_VALUE));
        assertEquals("stop_sequence", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_FOURTH_PART));
        assertEquals(3, noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_FOURTH_VALUE));

        verifyNoMoreInteractions(mockResultRepo);
    }
}
