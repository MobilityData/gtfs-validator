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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.stops.LocationBase;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.GeospatialUtils;

import java.util.*;

import static org.mockito.Mockito.*;

/**
 * Unit tests to validate behavior of use case for E052 "Stop too far from trip shape". Note that geospatial parts
 * of tests are executed in GeospatialUtilsImplTest in the adapter module as the implementation of the GeospatialUtils
 * interface.
 */
class ValidateStopTooFarFromTripShapeNoticeTest {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void stopWithinTripShapeShouldCallProperMethods() {
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

        verifyNoInteractions(mockResultRepo);
    }
}
