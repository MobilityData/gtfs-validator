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

package org.mobilitydata.gtfsvalidator.geoutils;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stops.BoardingArea;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stops.Entrance;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stops.LocationBase;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stops.StopOrPlatform;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.StopTooFarFromTripShapeNotice;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.ErrorNotice.E_052;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.KEY_COMPOSITE_KEY_FIRST_VALUE;
import static org.mockito.Mockito.*;

public class GeospatialUtilsImplTest {

    private static final GeospatialUtilsImpl GEO_UTILS = GeospatialUtilsImpl.getInstance();

    @Test
    void distanceFromToSameCoordinateIsZero() {

        int toCheck = GEO_UTILS.distanceBetweenMeter(45.508888, -73.561668, 45.508888, -73.561668);

        assertEquals(0, toCheck, 0);
    }

    @Test
    void distanceReferenceCheck() {
        // geographic data extracted and validated with an external tool
        int toCheck = GEO_UTILS.distanceBetweenMeter(45.508888, -73.561668, 45.507753, -73.562677);

        assertEquals(148, toCheck);
    }

    /**
     * Test geospatial implementation for rule "E052 - Stop too far from trip shape"
     * <p>
     * See map of trip shape and stops (in GeoJSON) at https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
     * <p>
     * For debugging, you can export a JTS-version of the buffer in WKT format using code at
     * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554.
     * The WKT output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
     * <p>
     * The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because it uses a LineString
     * and a proprietary "buffer" extension to GeoJSON and WKT, which most tools don't support.
     */
    @Test
    void stopWithinTripShapeBufferShouldNotGenerateNotice() {
        // stops.txt
        final String stopId1 = "1001";
        final StopOrPlatform stop1 = mock(StopOrPlatform.class);
        when(stop1.getStopId()).thenReturn(stopId1);
        // Location inside buffer
        when(stop1.getStopLat()).thenReturn(28.05811731042478f);
        when(stop1.getStopLon()).thenReturn(-82.41616877502503f);

        final String stopId2 = "1002";
        final StopOrPlatform stop2 = mock(StopOrPlatform.class);
        when(stop2.getStopId()).thenReturn(stopId2);
        // Location inside buffer
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

        List<StopTooFarFromTripShapeNotice> errorList =
                GEO_UTILS.checkStopsWithinTripShape(trip, stopTimes, points, stopPerId, new HashSet<>());
        assertEquals(0, errorList.size());

        verify(trip, times(4)).getShapeId();
        verify(stop1, times(2)).getStopId();
        verify(stop1, times(2)).getStopLon();
        verify(stop1, times(2)).getStopLat();
        verify(stop2, times(2)).getStopLon();
        verify(stop2, times(2)).getStopLat();
        verify(stop2, times(2)).getStopId();
        verify(stopTime1, times(1)).getStopId();
        verify(stopTime2, times(1)).getStopId();
        verify(pt1, times(1)).getShapePtLon();
        verify(pt1, times(1)).getShapePtLat();
        verify(pt2, times(1)).getShapePtLon();
        verify(pt2, times(1)).getShapePtLat();
        verify(pt3, times(1)).getShapePtLon();
        verify(pt3, times(1)).getShapePtLat();
        verify(pt4, times(1)).getShapePtLon();
        verify(pt4, times(1)).getShapePtLat();
        verify(pt5, times(1)).getShapePtLon();
        verify(pt5, times(1)).getShapePtLat();

        verifyNoMoreInteractions(trip);
        verifyNoMoreInteractions(stop1);
        verifyNoMoreInteractions(stop2);
        verifyNoMoreInteractions(stopTime1);
        verifyNoMoreInteractions(stopTime2);
        verifyNoMoreInteractions(pt1);
        verifyNoMoreInteractions(pt2);
        verifyNoMoreInteractions(pt3);
        verifyNoMoreInteractions(pt4);
        verifyNoMoreInteractions(pt5);
    }

    /**
     * Test geospatial implementation for rule "E052 - Stop too far from trip shape"
     *
     * See map of trip shape and stops (in GeoJSON) at https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
     * <p>
     * For debugging, you can export a JTS-version of the buffer in WKT format using code at
     * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554.
     * The WKT output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
     * <p>
     * The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because it uses a LineString
     * and a proprietary "buffer" extension to GeoJSON and WKT, which most tools don't support.
     */
    @Test
    void stopOutsideTripShapeBufferShouldGenerateNotice() {
        // stops.txt
        final String stopId1 = "1001";
        final StopOrPlatform stop1 = mock(StopOrPlatform.class);
        when(stop1.getStopId()).thenReturn(stopId1);
        // Location inside buffer
        when(stop1.getStopLat()).thenReturn(28.05811731042478f);
        when(stop1.getStopLon()).thenReturn(-82.41616877502503f);

        final String stopId2 = "1002";
        final StopOrPlatform stop2 = mock(StopOrPlatform.class);
        when(stop2.getStopId()).thenReturn(stopId2);
        // Location inside buffer
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

        List<StopTooFarFromTripShapeNotice> errorList =
                GEO_UTILS.checkStopsWithinTripShape(trip, stopTimes, points, stopPerId, new HashSet<>());

        assertEquals(1, errorList.size());

        StopTooFarFromTripShapeNotice notice = errorList.get(0);
        assertEquals(E_052, notice.getCode());
        assertEquals("Stop too far from trip shape", notice.getTitle());
        assertEquals(stopId3, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));

        verify(trip, times(7)).getShapeId();
        verify(trip, times(1)).getTripId();
        verify(stop1, times(2)).getStopId();
        verify(stop1, times(2)).getStopLon();
        verify(stop1, times(2)).getStopLat();
        verify(stop2, times(2)).getStopLon();
        verify(stop2, times(2)).getStopLat();
        verify(stop2, times(2)).getStopId();
        verify(stop3, times(2)).getStopLon();
        verify(stop3, times(2)).getStopLat();
        verify(stop3, times(2)).getStopId();
        verify(stopTime1, times(1)).getStopId();
        verify(stopTime2, times(1)).getStopId();
        verify(stopTime3, times(2)).getStopId();
        verify(stopTime3, times(1)).getStopSequence();
        verify(pt1, times(1)).getShapePtLon();
        verify(pt1, times(1)).getShapePtLat();
        verify(pt2, times(1)).getShapePtLon();
        verify(pt2, times(1)).getShapePtLat();
        verify(pt3, times(1)).getShapePtLon();
        verify(pt3, times(1)).getShapePtLat();
        verify(pt4, times(1)).getShapePtLon();
        verify(pt4, times(1)).getShapePtLat();
        verify(pt5, times(1)).getShapePtLon();
        verify(pt5, times(1)).getShapePtLat();

        verifyNoMoreInteractions(trip);
        verifyNoMoreInteractions(stop1);
        verifyNoMoreInteractions(stop2);
        verifyNoMoreInteractions(stopTime1);
        verifyNoMoreInteractions(stopTime2);
        verifyNoMoreInteractions(pt1);
        verifyNoMoreInteractions(pt2);
        verifyNoMoreInteractions(pt3);
        verifyNoMoreInteractions(pt4);
        verifyNoMoreInteractions(pt5);
    }

    /**
     * Test geospatial implementation for rule "E052 - Stop too far from trip shape"
     *
     * See map of trip shape and stops (in GeoJSON) at https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
     * <p>
     * For debugging, you can export a JTS-version of the buffer in WKT format using code at
     * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554.
     * The WKT output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
     * <p>
     * The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because it uses a LineString
     * and a proprietary "buffer" extension to GeoJSON and WKT, which most tools don't support.
     */
    @Test
    void twoTripsWithSameShapeStopOutsideBufferShouldGenerateOneNotice() {
        // stops.txt
        final String stopId1 = "1001";
        final StopOrPlatform stop1 = mock(StopOrPlatform.class);
        when(stop1.getStopId()).thenReturn(stopId1);
        // Location inside buffer
        when(stop1.getStopLat()).thenReturn(28.05811731042478f);
        when(stop1.getStopLon()).thenReturn(-82.41616877502503f);

        final String stopId2 = "1002";
        final StopOrPlatform stop2 = mock(StopOrPlatform.class);
        when(stop2.getStopId()).thenReturn(stopId2);
        // Location inside buffer
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
        final String tripId1 = "trip1";
        final Trip trip1 = mock(Trip.class);
        when(trip1.getTripId()).thenReturn(tripId1);
        when(trip1.getShapeId()).thenReturn(shapeId);

        final String tripId2 = "trip2";
        final Trip trip2 = mock(Trip.class);
        when(trip2.getTripId()).thenReturn(tripId2);
        when(trip2.getShapeId()).thenReturn(shapeId);

        // Map containing StopTime entities. Entities are mapped on keys from GTFS file stop_times.txt:
        // - trip_id
        // - stop_sequence
        final Map<String, TreeMap<Integer, StopTime>> stopTimeCollection = new HashMap<>(1);
        final TreeMap<Integer, StopTime> stopTimes = new TreeMap<>(Map.of(1, stopTime1, 2, stopTime2, 3, stopTime3));
        stopTimeCollection.put(tripId1, stopTimes);
        stopTimeCollection.put(tripId2, stopTimes);

        // Map containing Stop entities. Entities are keyed on GTFS stops.txt stop_id
        final Map<String, LocationBase> stopPerId = new HashMap<>(Map.of(stopId1, stop1, stopId2, stop2, stopId3, stop3));

        // Entities are keyed on shape_pt_sequence of GTFS file shapes.txt
        SortedMap<Integer, ShapePoint> points = new TreeMap<>(Map.of(1, pt1, 2, pt2, 3, pt3, 4, pt4, 5, pt5));

        // The testedCache will get populated with combination of each tested shapeId+stopId
        Set<String> testedCache = new HashSet<>();

        List<StopTooFarFromTripShapeNotice> trip1ErrorList =
                GEO_UTILS.checkStopsWithinTripShape(trip1, stopTimes, points, stopPerId, testedCache);

        assertEquals(1, trip1ErrorList.size());

        StopTooFarFromTripShapeNotice notice = trip1ErrorList.get(0);
        assertEquals(E_052, notice.getCode());
        assertEquals("Stop too far from trip shape", notice.getTitle());
        assertEquals(stopId3, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));

        // Validate the 2nd trip - no new errors should be added, because the shapeId+stopId combination has already been flagged
        List<StopTooFarFromTripShapeNotice> trip2ErrorList =
                GEO_UTILS.checkStopsWithinTripShape(trip1, stopTimes, points, stopPerId, testedCache);
        assertEquals(0, trip2ErrorList.size());

        verify(trip1, times(10)).getShapeId();
        verify(trip1, times(1)).getTripId();
        verify(stop1, times(3)).getStopId();
        verify(stop1, times(3)).getStopLon();
        verify(stop1, times(3)).getStopLat();
        verify(stop2, times(3)).getStopLon();
        verify(stop2, times(3)).getStopLat();
        verify(stop2, times(3)).getStopId();
        verify(stop3, times(3)).getStopLon();
        verify(stop3, times(3)).getStopLat();
        verify(stop3, times(3)).getStopId();
        verify(stopTime1, times(2)).getStopId();
        verify(stopTime2, times(2)).getStopId();
        verify(stopTime3, times(3)).getStopId();
        verify(stopTime3, times(1)).getStopSequence();
        verify(pt1, times(2)).getShapePtLon();
        verify(pt1, times(2)).getShapePtLat();
        verify(pt2, times(2)).getShapePtLon();
        verify(pt2, times(2)).getShapePtLat();
        verify(pt3, times(2)).getShapePtLon();
        verify(pt3, times(2)).getShapePtLat();
        verify(pt4, times(2)).getShapePtLon();
        verify(pt4, times(2)).getShapePtLat();
        verify(pt5, times(2)).getShapePtLon();
        verify(pt5, times(2)).getShapePtLat();

        verifyNoMoreInteractions(trip1);
        verifyNoMoreInteractions(trip2);
        verifyNoMoreInteractions(stop1);
        verifyNoMoreInteractions(stop2);
        verifyNoMoreInteractions(stopTime1);
        verifyNoMoreInteractions(stopTime2);
        verifyNoMoreInteractions(pt1);
        verifyNoMoreInteractions(pt2);
        verifyNoMoreInteractions(pt3);
        verifyNoMoreInteractions(pt4);
        verifyNoMoreInteractions(pt5);
    }

    /**
     * Test geospatial implementation for rule "E052 - Stop too far from trip shape"
     *
     * See map of trip shape and stops (in GeoJSON) at https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
     * <p>
     * For debugging, you can export a JTS-version of the buffer in WKT format using code at
     * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554.
     * The WKT output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
     * <p>
     * The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because it uses a LineString
     * and a proprietary "buffer" extension to GeoJSON and WKT, which most tools don't support.
     */
    @Test
    void tripWithoutShapeShouldNotGenerateNotice() {
        // stops.txt
        final String stopId1 = "1001";
        final StopOrPlatform stop1 = mock(StopOrPlatform.class);
        when(stop1.getStopId()).thenReturn(stopId1);
        // Location inside buffer
        when(stop1.getStopLat()).thenReturn(28.05811731042478f);
        when(stop1.getStopLon()).thenReturn(-82.41616877502503f);

        final String stopId2 = "1002";
        final StopOrPlatform stop2 = mock(StopOrPlatform.class);
        when(stop2.getStopId()).thenReturn(stopId2);
        // Location inside buffer
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

        // No shapes.txt data

        // trips.txt
        final String tripId1 = "trip1";
        final Trip trip1 = mock(Trip.class);
        when(trip1.getTripId()).thenReturn(tripId1);

        // Map containing StopTime entities. Entities are mapped on keys from GTFS file stop_times.txt:
        // - trip_id
        // - stop_sequence
        final Map<String, TreeMap<Integer, StopTime>> stopTimeCollection = new HashMap<>(1);
        final TreeMap<Integer, StopTime> stopTimes = new TreeMap<>(Map.of(1, stopTime1, 2, stopTime2, 3, stopTime3));
        stopTimeCollection.put(tripId1, stopTimes);

        // Map containing Stop entities. Entities are keyed on GTFS stops.txt stop_id
        final Map<String, LocationBase> stopPerId = new HashMap<>(Map.of(stopId1, stop1, stopId2, stop2, stopId3, stop3));

        List<StopTooFarFromTripShapeNotice> errorList =
                GEO_UTILS.checkStopsWithinTripShape(trip1, stopTimes, null, stopPerId, new HashSet<>());

        assertEquals(0, errorList.size());

        verifyNoMoreInteractions(trip1);
        verifyNoMoreInteractions(stop1);
        verifyNoMoreInteractions(stop2);
        verifyNoMoreInteractions(stopTime1);
        verifyNoMoreInteractions(stopTime2);
    }

    /**
     * Test geospatial implementation for rule "E052 - Stop too far from trip shape"
     *
     * See map of trip shape and stops (in GeoJSON) at https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
     * <p>
     * For debugging, you can export a JTS-version of the buffer in WKT format using code at
     * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554.
     * The WKT output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
     * <p>
     * The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because it uses a LineString
     * and a proprietary "buffer" extension to GeoJSON and WKT, which most tools don't support.
     */
    @Test
    void stopWithoutLocationShouldNotGenerateNotice() {
        // stops.txt
        final String stopId1 = "1001";
        final BoardingArea stop1 = mock(BoardingArea.class);
        when(stop1.getStopId()).thenReturn(stopId1);
        // No location - optional for location_type=4
        when(stop1.getStopLat()).thenReturn(null);
        when(stop1.getStopLon()).thenReturn(null);

        final String stopId2 = "1002";
        final BoardingArea stop2 = mock(BoardingArea.class);
        when(stop2.getStopId()).thenReturn(stopId2);
        // No location - optional for location_type=4
        when(stop2.getStopLat()).thenReturn(null);
        when(stop2.getStopLon()).thenReturn(null);

        final String stopId3 = "1003";
        final BoardingArea stop3 = mock(BoardingArea.class);
        when(stop3.getStopId()).thenReturn(stopId3);
        // No location - optional for location_type=4
        when(stop3.getStopLat()).thenReturn(null);
        when(stop3.getStopLon()).thenReturn(null);

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

        List<StopTooFarFromTripShapeNotice> errorList =
                GEO_UTILS.checkStopsWithinTripShape(trip, stopTimes, points, stopPerId, new HashSet<>());

        assertEquals(0, errorList.size());

        verify(stop1, times(1)).getStopLat();
        verify(stop2, times(1)).getStopLat();
        verify(stop3, times(1)).getStopLat();
        verify(stopTime1, times(1)).getStopId();
        verify(stopTime2, times(1)).getStopId();
        verify(stopTime3, times(1)).getStopId();
        verify(pt1, times(1)).getShapePtLon();
        verify(pt1, times(1)).getShapePtLat();
        verify(pt2, times(1)).getShapePtLon();
        verify(pt2, times(1)).getShapePtLat();
        verify(pt3, times(1)).getShapePtLon();
        verify(pt3, times(1)).getShapePtLat();
        verify(pt4, times(1)).getShapePtLon();
        verify(pt4, times(1)).getShapePtLat();
        verify(pt5, times(1)).getShapePtLon();
        verify(pt5, times(1)).getShapePtLat();

        verifyNoMoreInteractions(trip);
        verifyNoMoreInteractions(stop1);
        verifyNoMoreInteractions(stop2);
        verifyNoMoreInteractions(stopTime1);
        verifyNoMoreInteractions(stopTime2);
        verifyNoMoreInteractions(pt1);
        verifyNoMoreInteractions(pt2);
        verifyNoMoreInteractions(pt3);
        verifyNoMoreInteractions(pt4);
        verifyNoMoreInteractions(pt5);
    }

    /**
     * Test geospatial implementation for rule "E052 - Stop too far from trip shape"
     *
     * See map of trip shape and stops (in GeoJSON) at https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
     * <p>
     * For debugging, you can export a JTS-version of the buffer in WKT format using code at
     * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554.
     * The WKT output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
     * <p>
     * The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because it uses a LineString
     * and a proprietary "buffer" extension to GeoJSON and WKT, which most tools don't support.
     */
    @Test
    void stopLocationTypeNotZeroOrFourShouldNotGenerateNotice() {
        // stops.txt
        final String stopId1 = "1001";
        final Entrance stop1 = mock(Entrance.class);
        when(stop1.getStopId()).thenReturn(stopId1);
        // Location inside buffer
        when(stop1.getStopLat()).thenReturn(28.05811731042478f);
        when(stop1.getStopLon()).thenReturn(-82.41616877502503f);

        final String stopId2 = "1002";
        final Entrance stop2 = mock(Entrance.class);
        when(stop2.getStopId()).thenReturn(stopId2);
        // Location inside buffer
        when(stop2.getStopLat()).thenReturn(28.05812364854794f);
        when(stop2.getStopLon()).thenReturn(-82.41617370439423f);

        final String stopId3 = "1003";
        final Entrance stop3 = mock(Entrance.class);
        when(stop3.getStopId()).thenReturn(stopId3);
        // Location OUTSIDE buffer - but this rule doesn't apply to Entrances/Exits (location_type=2)
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

        List<StopTooFarFromTripShapeNotice> errorList =
                GEO_UTILS.checkStopsWithinTripShape(trip, stopTimes, points, stopPerId, new HashSet<>());

        assertEquals(0, errorList.size());

        verify(stop1, times(1)).getStopLat();
        verify(stop1, times(1)).getStopLon();
        verify(stop2, times(1)).getStopLat();
        verify(stop2, times(1)).getStopLon();
        verify(stop3, times(1)).getStopLat();
        verify(stop3, times(1)).getStopLon();
        verify(stopTime1, times(1)).getStopId();
        verify(stopTime2, times(1)).getStopId();
        verify(stopTime3, times(1)).getStopId();
        verify(pt1, times(1)).getShapePtLon();
        verify(pt1, times(1)).getShapePtLat();
        verify(pt2, times(1)).getShapePtLon();
        verify(pt2, times(1)).getShapePtLat();
        verify(pt3, times(1)).getShapePtLon();
        verify(pt3, times(1)).getShapePtLat();
        verify(pt4, times(1)).getShapePtLon();
        verify(pt4, times(1)).getShapePtLat();
        verify(pt5, times(1)).getShapePtLon();
        verify(pt5, times(1)).getShapePtLat();

        verifyNoMoreInteractions(trip);
        verifyNoMoreInteractions(stop1);
        verifyNoMoreInteractions(stop2);
        verifyNoMoreInteractions(stopTime1);
        verifyNoMoreInteractions(stopTime2);
        verifyNoMoreInteractions(pt1);
        verifyNoMoreInteractions(pt2);
        verifyNoMoreInteractions(pt3);
        verifyNoMoreInteractions(pt4);
        verifyNoMoreInteractions(pt5);
    }
}
