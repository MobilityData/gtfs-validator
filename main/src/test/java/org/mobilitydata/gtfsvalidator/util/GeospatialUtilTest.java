/*
 * Copyright 2021 MobilityData IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StopTooFarFromTripShapeNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;

public class GeospatialUtilTest {
  public static GtfsStopTime createStopTime(
      long csvRowNumber, String tripId, String stopId, int stopSequence) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId(tripId)
        .setStopSequence(stopSequence)
        .setStopId(stopId)
        .build();
  }

  public static GtfsTrip createTrip(
      long csvRowNumber, String routeId, String serviceId, String tripId, String shapeId) {
    return new GtfsTrip.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setRouteId(routeId)
        .setServiceId(serviceId)
        .setTripId(tripId)
        .setShapeId(shapeId)
        .build();
  }

  public static GtfsShape createShapePoint(
      long csvRowNumber,
      String shapeId,
      double shapePtLat,
      double shapePtLon,
      int shapePtSequence,
      double shapeDistTraveled) {
    return new GtfsShape.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setShapeId(shapeId)
        .setShapePtLat(shapePtLat)
        .setShapePtLon(shapePtLon)
        .setShapePtSequence(shapePtSequence)
        .setShapeDistTraveled(shapeDistTraveled)
        .build();
  }

  public static GtfsStop createStop(
      long csvRowNumber, String stopId, Double stopLat, Double stopLon, int locationType) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStopId(stopId)
        .setStopLat(stopLat)
        .setStopLon(stopLon)
        .setLocationType(locationType)
        .build();
  }

  @Test
  public void distanceFromToSameCoordinateIsZero() {
    assertThat(GeospatialUtil.distanceInMeterBetween(45.508888, -73.561668, 45.508888, -73.561668))
        .isEqualTo(0);
  }

  @Test
  public void distanceReferenceCheck() {
    // geographic data extracted and validated with an external tool
    assertThat(GeospatialUtil.distanceInMeterBetween(45.508888, -73.561668, 45.507753, -73.562677))
        .isEqualTo(148);
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void stopWithinTripShapeBufferShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsTrip trip = createTrip(4, "route id value", "service id value", "trip id", "shape id");
    new GtfsTrip.Builder().setTripId("trip id value").setShapeId("shape id value").build();
    List<GtfsStopTime> stopTimes =
        ImmutableList.of(createStopTime(5, "t1", "1001", 1), createStopTime(8, "t1", "1002", 2));

    List<GtfsShape> shapePoints =
        ImmutableList.of(
            createShapePoint(5, "shape id", 28.05724310653972D, -82.41350776611507D, 1, 400f),
            createShapePoint(6, "shape id", 28.05746701492806D, -82.41493135129478D, 2, 400f),
            createShapePoint(7, "shape id", 28.05800068503469f, -82.4159394137605D, 3, 400f),
            createShapePoint(8, "shape id", 28.05808869825447D, -82.41648754043338D, 4, 400f),
            createShapePoint(9, "shape id", 28.05809979887893D, -82.41773971025437D, 5, 400f));

    GtfsStopTableContainer stopTable =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                createStop(2, "1001", 28.05808869825447D, -82.41648754043338D, 0),
                createStop(4, "1002", 28.05809979887893D, -82.41773971025437D, 0)),
            noticeContainer);

    List<StopTooFarFromTripShapeNotice> underTest =
        GeospatialUtil.checkStopsWithinTripShape(
            trip, stopTimes, shapePoints, stopTable, new HashSet<>());
    assertThat(underTest).isEmpty();
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void stopOutsideTripShapeBufferShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsTrip trip = createTrip(4, "route id value", "service id value", "t1", "shape id");
    new GtfsTrip.Builder().setTripId("trip id value").setShapeId("shape id value").build();
    List<GtfsStopTime> stopTimes =
        ImmutableList.of(
            createStopTime(5, "t1", "1001", 1),
            createStopTime(8, "t1", "1002", 2),
            createStopTime(9, "t1", "1003", 3));

    List<GtfsShape> shapePoints =
        ImmutableList.of(
            createShapePoint(5, "shape id", 28.05724310653972D, -82.41350776611507D, 1, 400f),
            createShapePoint(6, "shape id", 28.05746701492806D, -82.41493135129478D, 2, 400f),
            createShapePoint(7, "shape id", 28.05800068503469f, -82.4159394137605D, 3, 400f),
            createShapePoint(8, "shape id", 28.05808869825447D, -82.41648754043338D, 4, 400f),
            createShapePoint(9, "shape id", 28.05809979887893D, -82.41773971025437D, 5, 400f));

    GtfsStopTableContainer stopTable =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                createStop(2, "1001", 28.05808869825447D, -82.41648754043338D, 4),
                createStop(4, "1002", 28.05809979887893D, -82.41773971025437D, 4),
                // this location is outside buffer
                createStop(5, "1003", 17.05673053256373D, -45.4170801432763D, 4)),
            noticeContainer);

    List<StopTooFarFromTripShapeNotice> underTest =
        GeospatialUtil.checkStopsWithinTripShape(
            trip, stopTimes, shapePoints, stopTable, new HashSet<>());
    assertThat(underTest)
        .containsExactly(new StopTooFarFromTripShapeNotice("1003", 3, "t1", "shape id", 100));
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void twoTripsWithSameShapeStopOutsideBufferShouldGenerateOneNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsTrip firstTrip = createTrip(4, "route id value", "service id value", "t1", "shape id");
    GtfsTrip secondTrip = createTrip(9, "route id value", "service id value", "t2", "shape id");

    new GtfsTrip.Builder().setTripId("trip id value").setShapeId("shape id value").build();
    List<GtfsStopTime> stopTimes =
        ImmutableList.of(
            createStopTime(5, "t1", "1001", 1),
            createStopTime(8, "t1", "1002", 2),
            createStopTime(9, "t1", "1003", 3));

    List<GtfsShape> shapePoints =
        ImmutableList.of(
            createShapePoint(5, "shape id", 28.05724310653972D, -82.41350776611507D, 1, 400f),
            createShapePoint(6, "shape id", 28.05746701492806D, -82.41493135129478D, 2, 400f),
            createShapePoint(7, "shape id", 28.05800068503469f, -82.4159394137605D, 3, 400f),
            createShapePoint(8, "shape id", 28.05808869825447D, -82.41648754043338D, 4, 400f),
            createShapePoint(9, "shape id", 28.05809979887893D, -82.41773971025437D, 5, 400f));

    GtfsStopTableContainer stopTable =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                createStop(2, "1001", 28.05808869825447D, -82.41648754043338D, 0),
                createStop(4, "1002", 28.05809979887893D, -82.41773971025437D, 0),
                // this location is outside buffer
                createStop(5, "1003", 28.05673053256373D, -82.4170801432763D, 4)),
            noticeContainer);

    Set<String> testedCache = new HashSet<>();
    List<StopTooFarFromTripShapeNotice> underTest =
        GeospatialUtil.checkStopsWithinTripShape(
            firstTrip, stopTimes, shapePoints, stopTable, testedCache);

    assertThat(underTest)
        .containsExactly(new StopTooFarFromTripShapeNotice("1003", 3, "t1", "shape id", 100));
    // Validate the 2nd trip - no new errors should be added, because the shapeId+stopId combination
    // has already been flagged
    underTest.addAll(
        GeospatialUtil.checkStopsWithinTripShape(
            secondTrip, stopTimes, shapePoints, stopTable, testedCache));
    assertThat(underTest)
        .containsExactly(new StopTooFarFromTripShapeNotice("1003", 3, "t1", "shape id", 100));
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void tripWithoutShapeShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsTrip trip = createTrip(4, "route id value", "service id value", "trip id", null);
    new GtfsTrip.Builder().setTripId("trip id value").setShapeId("shape id value").build();
    List<GtfsStopTime> stopTimes =
        ImmutableList.of(createStopTime(5, "t1", "1001", 1), createStopTime(8, "t1", "1002", 2));

    // No shapes.txt data
    List<GtfsShape> shapePoints = null;

    GtfsStopTableContainer stopTable =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                createStop(2, "1001", 28.05811731042478D, -82.41616877502503D, 0),
                createStop(4, "1002", 28.05812364854794D, -82.41617370439423D, 0)),
            noticeContainer);

    List<StopTooFarFromTripShapeNotice> underTest =
        GeospatialUtil.checkStopsWithinTripShape(
            trip, stopTimes, shapePoints, stopTable, new HashSet<>());
    assertThat(underTest).isEmpty();
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void stopWithoutLocationShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsTrip trip = createTrip(4, "route id value", "service id value", "trip id", "shape id");
    new GtfsTrip.Builder().setTripId("trip id value").setShapeId("shape id value").build();
    List<GtfsStopTime> stopTimes =
        ImmutableList.of(createStopTime(5, "t1", "1001", 1), createStopTime(8, "t1", "1002", 2));

    List<GtfsShape> shapePoints =
        ImmutableList.of(
            createShapePoint(5, "shape id", 28.05724310653972D, -82.41350776611507D, 1, 400f),
            createShapePoint(6, "shape id", 28.05746701492806D, -82.41493135129478D, 2, 400f),
            createShapePoint(7, "shape id", 28.05800068503469f, -82.4159394137605D, 3, 400f),
            createShapePoint(8, "shape id", 28.05808869825447D, -82.41648754043338D, 4, 400f),
            createShapePoint(9, "shape id", 28.05809979887893D, -82.41773971025437D, 5, 400f));

    GtfsStopTableContainer stopTable =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                // No location - optional for location_type=4
                createStop(2, "1001", null, null, 4),
                // No location - optional for location_type=4
                createStop(4, "1002", null, null, 4),
                // No location - optional for location_type=4
                createStop(5, "1003", null, null, 4)),
            noticeContainer);

    List<StopTooFarFromTripShapeNotice> underTest =
        GeospatialUtil.checkStopsWithinTripShape(
            trip, stopTimes, shapePoints, stopTable, new HashSet<>());
    assertThat(underTest).isEmpty();
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void stopLocationTypeNotZeroOrFourShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsTrip firstTrip = createTrip(4, "route id value", "service id value", "t1", "shape id");

    new GtfsTrip.Builder().setTripId("trip id value").setShapeId("shape id value").build();
    List<GtfsStopTime> stopTimes =
        ImmutableList.of(
            createStopTime(5, "t1", "1001", 1),
            createStopTime(8, "t1", "1002", 2),
            createStopTime(9, "t1", "1003", 3));

    List<GtfsShape> shapePoints =
        ImmutableList.of(
            createShapePoint(5, "shape id", 28.05724310653972D, -82.41350776611507D, 1, 400f),
            createShapePoint(6, "shape id", 28.05746701492806D, -82.41493135129478D, 2, 400f),
            createShapePoint(7, "shape id", 28.05800068503469f, -82.4159394137605D, 3, 400f),
            createShapePoint(8, "shape id", 28.05808869825447D, -82.41648754043338D, 4, 400f),
            createShapePoint(9, "shape id", 28.05809979887893D, -82.41773971025437D, 5, 400f));

    GtfsStopTableContainer stopTable =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                createStop(2, "1001", 28.05811731042478D, -82.41616877502503D, 2),
                createStop(4, "1002", 28.05812364854794D, -82.41617370439423D, 2),
                // this location is outside buffer
                createStop(5, "1003", 28.05673053256373D, -82.4170801432763D, 2)),
            noticeContainer);

    Set<String> testedCache = new HashSet<>();
    assertThat(
            GeospatialUtil.checkStopsWithinTripShape(
                firstTrip, stopTimes, shapePoints, stopTable, testedCache))
        .isEmpty();
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void singleStopWithinBufferShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsTrip firstTrip = createTrip(4, "route id value", "service id value", "t1", "shape id");

    new GtfsTrip.Builder().setTripId("trip id value").setShapeId("shape id value").build();
    List<GtfsStopTime> stopTimes =
        ImmutableList.of(
            createStopTime(5, "t1", "1001", 1),
            createStopTime(8, "t1", "1002", 2),
            createStopTime(9, "t1", "1003", 3));

    List<GtfsShape> shapePoints =
        ImmutableList.of(
            createShapePoint(5, "shape id", 28.05724310653972D, -82.41350776611507D, 1, 400f),
            createShapePoint(6, "shape id", 28.05746701492806D, -82.41493135129478D, 2, 400f),
            createShapePoint(7, "shape id", 28.05800068503469f, -82.4159394137605D, 3, 400f),
            createShapePoint(8, "shape id", 28.05808869825447D, -82.41648754043338D, 4, 400f),
            createShapePoint(9, "shape id", 28.05809979887893D, -82.41773971025437D, 5, 400f));

    GtfsStopTableContainer stopTable =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                // this location is inside buffer
                createStop(2, "1001", 28.05724310653972D, -82.41350776611507D, 0)),
            noticeContainer);

    Set<String> testedCache = new HashSet<>();
    assertThat(
            GeospatialUtil.checkStopsWithinTripShape(
                firstTrip, stopTimes, shapePoints, stopTable, testedCache))
        .isEmpty();
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void nullTripShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsTrip trip = null;
    new GtfsTrip.Builder().setTripId("trip id value").setShapeId("shape id value").build();
    List<GtfsStopTime> stopTimes =
        ImmutableList.of(
            createStopTime(5, "t1", "1001", 1),
            createStopTime(8, "t1", "1002", 2),
            createStopTime(9, "t1", "1003", 3));

    List<GtfsShape> shapePoints =
        ImmutableList.of(
            createShapePoint(5, "shape id", 28.05724310653972D, -82.41350776611507D, 1, 400f),
            createShapePoint(6, "shape id", 28.05746701492806D, -82.41493135129478D, 2, 400f),
            createShapePoint(7, "shape id", 28.05800068503469f, -82.4159394137605D, 3, 400f),
            createShapePoint(8, "shape id", 28.05808869825447D, -82.41648754043338D, 4, 400f),
            createShapePoint(9, "shape id", 28.05809979887893D, -82.41773971025437D, 5, 400f));

    GtfsStopTableContainer stopTable =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                createStop(2, "1001", 28.05811731042478D, -82.41616877502503D, 4),
                createStop(4, "1002", 28.05812364854794D, -82.41617370439423D, 4),
                // this location is outside buffer
                createStop(5, "1003", 28.05673053256373D, -82.4170801432763D, 4)),
            noticeContainer);

    List<StopTooFarFromTripShapeNotice> underTest =
        GeospatialUtil.checkStopsWithinTripShape(
            trip, stopTimes, shapePoints, stopTable, new HashSet<>());
    assertThat(underTest).isEmpty();
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void nullStopTimeShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsTrip trip = createTrip(4, "route id value", "service id value", "t1", "shape id");
    new GtfsTrip.Builder().setTripId("trip id value").setShapeId("shape id value").build();
    List<GtfsStopTime> stopTimes = null;

    List<GtfsShape> shapePoints =
        ImmutableList.of(
            createShapePoint(5, "shape id", 28.05724310653972D, -82.41350776611507D, 1, 400f),
            createShapePoint(6, "shape id", 28.05746701492806D, -82.41493135129478D, 2, 400f),
            createShapePoint(7, "shape id", 28.05800068503469f, -82.4159394137605D, 3, 400f),
            createShapePoint(8, "shape id", 28.05808869825447D, -82.41648754043338D, 4, 400f),
            createShapePoint(9, "shape id", 28.05809979887893D, -82.41773971025437D, 5, 400f));

    GtfsStopTableContainer stopTable =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                createStop(2, "1001", 28.05811731042478D, -82.41616877502503D, 4),
                createStop(4, "1002", 28.05812364854794D, -82.41617370439423D, 4),
                // this location is outside buffer
                createStop(5, "1003", 28.05673053256373D, -82.4170801432763D, 4)),
            noticeContainer);

    List<StopTooFarFromTripShapeNotice> underTest =
        GeospatialUtil.checkStopsWithinTripShape(
            trip, stopTimes, shapePoints, stopTable, new HashSet<>());
    assertThat(underTest).isEmpty();
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void emptyStopTimesShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsTrip trip = createTrip(4, "route id value", "service id value", "t1", "shape id");
    new GtfsTrip.Builder().setTripId("trip id value").setShapeId("shape id value").build();
    List<GtfsStopTime> stopTimes = new ArrayList<>();

    List<GtfsShape> shapePoints =
        ImmutableList.of(
            createShapePoint(5, "shape id", 28.05724310653972D, -82.41350776611507D, 1, 400f),
            createShapePoint(6, "shape id", 28.05746701492806D, -82.41493135129478D, 2, 400f),
            createShapePoint(7, "shape id", 28.05800068503469f, -82.4159394137605D, 3, 400f),
            createShapePoint(8, "shape id", 28.05808869825447D, -82.41648754043338D, 4, 400f),
            createShapePoint(9, "shape id", 28.05809979887893D, -82.41773971025437D, 5, 400f));

    GtfsStopTableContainer stopTable =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                createStop(2, "1001", 28.05811731042478D, -82.41616877502503D, 4),
                createStop(4, "1002", 28.05812364854794D, -82.41617370439423D, 4),
                // this location is outside buffer
                createStop(5, "1003", 28.05673053256373D, -82.4170801432763D, 4)),
            noticeContainer);

    List<StopTooFarFromTripShapeNotice> underTest =
        GeospatialUtil.checkStopsWithinTripShape(
            trip, stopTimes, shapePoints, stopTable, new HashSet<>());
    assertThat(underTest).isEmpty();
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void nullShapePointsShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsTrip trip = createTrip(4, "route id value", "service id value", "t1", "shape id");
    new GtfsTrip.Builder().setTripId("trip id value").setShapeId("shape id value").build();
    List<GtfsStopTime> stopTimes =
        ImmutableList.of(
            createStopTime(5, "t1", "1001", 1),
            createStopTime(8, "t1", "1002", 2),
            createStopTime(9, "t1", "1003", 3));
    List<GtfsShape> shapePoints = null;

    GtfsStopTableContainer stopTable =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                createStop(2, "1001", 28.05811731042478D, -82.41616877502503D, 4),
                createStop(4, "1002", 28.05812364854794D, -82.41617370439423D, 4),
                // this location is outside buffer
                createStop(5, "1003", 28.05673053256373D, -82.4170801432763D, 4)),
            noticeContainer);

    List<StopTooFarFromTripShapeNotice> underTest =
        GeospatialUtil.checkStopsWithinTripShape(
            trip, stopTimes, shapePoints, stopTable, new HashSet<>());
    assertThat(underTest).isEmpty();
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void emptyShapePointsShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsTrip trip = createTrip(4, "route id value", "service id value", "t1", "shape id");
    new GtfsTrip.Builder().setTripId("trip id value").setShapeId("shape id value").build();
    List<GtfsStopTime> stopTimes =
        ImmutableList.of(
            createStopTime(5, "t1", "1001", 1),
            createStopTime(8, "t1", "1002", 2),
            createStopTime(9, "t1", "1003", 3));
    List<GtfsShape> shapePoints = new ArrayList<>();

    GtfsStopTableContainer stopTable =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                createStop(2, "1001", 28.05811731042478D, -82.41616877502503D, 4),
                createStop(4, "1002", 28.05812364854794D, -82.41617370439423D, 4),
                // this location is outside buffer
                createStop(5, "1003", 28.05673053256373D, -82.4170801432763D, 4)),
            noticeContainer);

    List<StopTooFarFromTripShapeNotice> underTest =
        GeospatialUtil.checkStopsWithinTripShape(
            trip, stopTimes, shapePoints, stopTable, new HashSet<>());
    assertThat(underTest).isEmpty();
  }
}
