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

package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StopTooFarFromTripShapeNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class StopTooFarFromTripShapeValidatorTest {

  public static GtfsStopTime createStopTime(
      long csvRowNumber, String tripId, String stopId, int stopSequence) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId(tripId)
        .setStopSequence(stopSequence)
        .setStopId(stopId)
        .build();
  }

  private static GtfsStopTimeTableContainer createStopTimeTable(
      NoticeContainer noticeContainer, List<GtfsStopTime> entities) {
    return GtfsStopTimeTableContainer.forEntities(entities, noticeContainer);
  }

  private static GtfsTripTableContainer createTripTable(
      NoticeContainer noticeContainer, List<GtfsTrip> entities) {
    return GtfsTripTableContainer.forEntities(entities, noticeContainer);
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

  private static GtfsShapeTableContainer createShapeTable(
      NoticeContainer noticeContainer, List<GtfsShape> entities) {
    return GtfsShapeTableContainer.forEntities(entities, noticeContainer);
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

  private static GtfsStopTableContainer createStopTable(
      NoticeContainer noticeContainer, List<GtfsStop> entities) {
    return GtfsStopTableContainer.forEntities(entities, noticeContainer);
  }

  public static GtfsStop createStop(
      long csvRowNumber, String stopId, double stopLat, double stopLon, int locationType) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStopId(stopId)
        .setStopLat(stopLat)
        .setStopLon(stopLon)
        .setLocationType(locationType)
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
  public void stopOutsideTripShapeShouldGenerateNotice() {
    // See map of trip shape and stops (in GeoJSON) at
    // https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
    NoticeContainer noticeContainer = new NoticeContainer();
    StopTooFarFromTripShapeValidator underTest = new StopTooFarFromTripShapeValidator();

    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                createStop(2, "1001", 28.05808869825447D, -82.41648754043338D, 0),
                createStop(4, "1002", 28.05809979887893D, -82.41773971025437D, 0),
                // this location is outside buffer
                createStop(5, "1003", 17.456467, -45.4569865, 0)));
    underTest.stopTimeTable =
        createStopTimeTable(
            noticeContainer,
            ImmutableList.of(
                createStopTime(5, "t1", "1001", 1),
                createStopTime(8, "t1", "1002", 2),
                createStopTime(9, "t1", "1003", 3)));
    underTest.shapeTable =
        createShapeTable(
            noticeContainer,
            ImmutableList.of(
                createShapePoint(5, "shape id", 28.05724310653972D, -82.41350776611507D, 1, 400f),
                createShapePoint(6, "shape id", 28.05746701492806D, -82.41493135129478D, 2, 400f),
                createShapePoint(7, "shape id", 28.05800068503469D, -82.4159394137605D, 3, 400f),
                createShapePoint(8, "shape id", 28.05808869825447D, -82.41648754043338D, 4, 400f),
                createShapePoint(9, "shape id", 28.05809979887893D, -82.41773971025437D, 5, 400f)));
    underTest.tripTable =
        createTripTable(
            noticeContainer,
            ImmutableList.of(createTrip(55, "route id", "service id", "t1", "shape id")));
    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new StopTooFarFromTripShapeNotice("1003", 3, "t1", "shape id", 100));
  }

  @Test
  public void stopWithinTripShapeShouldNotGenerateNotice() {
    // See map of trip shape and stops (in GeoJSON) at
    // https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
    NoticeContainer noticeContainer = new NoticeContainer();
    StopTooFarFromTripShapeValidator underTest = new StopTooFarFromTripShapeValidator();

    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                createStop(2, "1001", 28.05808869825447D, -82.41648754043338D, 4),
                createStop(4, "1002", 28.05809979887893D, -82.41773971025437D, 4)));
    underTest.stopTimeTable =
        createStopTimeTable(
            noticeContainer,
            ImmutableList.of(
                createStopTime(5, "t1", "1001", 1), createStopTime(8, "t1", "1002", 2)));
    underTest.shapeTable =
        createShapeTable(
            noticeContainer,
            ImmutableList.of(
                createShapePoint(5, "shape id", 28.05724310653972D, -82.41350776611507D, 1, 400f),
                createShapePoint(6, "shape id", 28.05746701492806D, -82.41493135129478D, 2, 400f),
                createShapePoint(7, "shape id", 28.05800068503469f, -82.4159394137605D, 3, 400f),
                createShapePoint(8, "shape id", 28.05808869825447D, -82.41648754043338D, 4, 400f),
                createShapePoint(9, "shape id", 28.05809979887893D, -82.41773971025437D, 5, 400f)));
    underTest.tripTable =
        createTripTable(
            noticeContainer,
            ImmutableList.of(createTrip(55, "route id", "service id", "t1", "shape id")));

    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  /**
   * See map of trip shape and stops (in GeoJSON) at
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
    StopTooFarFromTripShapeValidator underTest = new StopTooFarFromTripShapeValidator();

    underTest.tripTable =
        createTripTable(
            noticeContainer,
            ImmutableList.of(
                createTrip(4, "r1", "service1", "t1", "shape1"),
                createTrip(9, "r1", "service1", "t2", "shape1")));

    underTest.stopTimeTable =
        createStopTimeTable(
            noticeContainer,
            ImmutableList.of(
                createStopTime(5, "t1", "1001", 1),
                createStopTime(8, "t1", "1002", 2),
                createStopTime(9, "t1", "1003", 3)));

    underTest.shapeTable =
        createShapeTable(
            noticeContainer,
            ImmutableList.of(
                createShapePoint(5, "shape1", 28.05724310653972D, -82.41350776611507D, 1, 400f),
                createShapePoint(6, "shape1", 28.05746701492806D, -82.41493135129478D, 2, 400f),
                createShapePoint(7, "shape1", 28.05800068503469f, -82.4159394137605D, 3, 400f),
                createShapePoint(8, "shape1", 28.05808869825447D, -82.41648754043338D, 4, 400f),
                createShapePoint(9, "shape1", 28.05809979887893D, -82.41773971025437D, 5, 400f)));

    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                createStop(2, "1001", 28.05808869825447D, -82.41648754043338D, 0),
                createStop(4, "1002", 28.05809979887893D, -82.41773971025437D, 0),
                // this location is outside buffer
                createStop(5, "1003", 28.05673053256373D, -82.4170801432763D, 4)));

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new StopTooFarFromTripShapeNotice("1003", 3, "t1", "shape1", 100));
  }

  /**
   * See map of trip shape and stops (in GeoJSON) at
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
    StopTooFarFromTripShapeValidator underTest = new StopTooFarFromTripShapeValidator();
    underTest.tripTable =
        createTripTable(
            noticeContainer, ImmutableList.of(createTrip(4, "r1", "service1", "t1", null)));

    underTest.stopTimeTable =
        createStopTimeTable(
            noticeContainer,
            ImmutableList.of(
                createStopTime(5, "t1", "1001", 1), createStopTime(8, "t1", "1002", 2)));

    // No shapes.txt data
    underTest.shapeTable = GtfsShapeTableContainer.forEntities(new ArrayList<>(), noticeContainer);

    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                createStop(2, "1001", 28.05811731042478D, -82.41616877502503D, 0),
                createStop(4, "1002", 28.05812364854794D, -82.41617370439423D, 0)));

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  /**
   * See map of trip shape and stops (in GeoJSON) at
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
    StopTooFarFromTripShapeValidator underTest = new StopTooFarFromTripShapeValidator();
    underTest.tripTable =
        createTripTable(
            noticeContainer, ImmutableList.of(createTrip(4, "r1", "service1", "t1", "shape1")));

    underTest.stopTimeTable =
        createStopTimeTable(
            noticeContainer,
            ImmutableList.of(
                createStopTime(5, "t1", "1001", 1), createStopTime(8, "t1", "1002", 2)));

    underTest.shapeTable =
        createShapeTable(
            noticeContainer,
            ImmutableList.of(
                createShapePoint(5, "shape1", 28.05724310653972D, -82.41350776611507D, 1, 400f),
                createShapePoint(6, "shape1", 28.05746701492806D, -82.41493135129478D, 2, 400f),
                createShapePoint(7, "shape1", 28.05800068503469f, -82.4159394137605D, 3, 400f),
                createShapePoint(8, "shape1", 28.05808869825447D, -82.41648754043338D, 4, 400f),
                createShapePoint(9, "shape1", 28.05809979887893D, -82.41773971025437D, 5, 400f)));

    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                // No location - optional for location_type=4
                createStop(2, "1001", null, null, 4),
                // No location - optional for location_type=4
                createStop(4, "1002", null, null, 4),
                // No location - optional for location_type=4
                createStop(5, "1003", null, null, 4)));

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  /**
   * See map of trip shape and stops (in GeoJSON) at
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
    StopTooFarFromTripShapeValidator underTest = new StopTooFarFromTripShapeValidator();

    underTest.tripTable =
        createTripTable(
            noticeContainer, ImmutableList.of(createTrip(4, "r1", "service1", "t1", "shape1")));

    underTest.stopTimeTable =
        createStopTimeTable(
            noticeContainer,
            ImmutableList.of(
                createStopTime(5, "t1", "1001", 1),
                createStopTime(8, "t1", "1002", 2),
                createStopTime(9, "t1", "1003", 3)));

    underTest.shapeTable =
        createShapeTable(
            noticeContainer,
            ImmutableList.of(
                createShapePoint(5, "shape1", 28.05724310653972D, -82.41350776611507D, 1, 400f),
                createShapePoint(6, "shape1", 28.05746701492806D, -82.41493135129478D, 2, 400f),
                createShapePoint(7, "shape1", 28.05800068503469f, -82.4159394137605D, 3, 400f),
                createShapePoint(8, "shape1", 28.05808869825447D, -82.41648754043338D, 4, 400f),
                createShapePoint(9, "shape1", 28.05809979887893D, -82.41773971025437D, 5, 400f)));

    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                // this location is inside buffer
                createStop(2, "1001", 28.05724310653972D, -82.41350776611507D, 0)));

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  /**
   * See map of trip shape and stops (in GeoJSON) at
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
    StopTooFarFromTripShapeValidator underTest = new StopTooFarFromTripShapeValidator();

    underTest.tripTable =
        createTripTable(
            noticeContainer, ImmutableList.of(createTrip(4, "r1", "service1", "t1", "shape1")));

    underTest.stopTimeTable =
        GtfsStopTimeTableContainer.forEntities(new ArrayList<>(), noticeContainer);

    underTest.shapeTable =
        createShapeTable(
            noticeContainer,
            ImmutableList.of(
                createShapePoint(5, "shape1", 28.05724310653972D, -82.41350776611507D, 1, 400f),
                createShapePoint(6, "shape1", 28.05746701492806D, -82.41493135129478D, 2, 400f),
                createShapePoint(7, "shape1", 28.05800068503469f, -82.4159394137605D, 3, 400f),
                createShapePoint(8, "shape1", 28.05808869825447D, -82.41648754043338D, 4, 400f),
                createShapePoint(9, "shape1", 28.05809979887893D, -82.41773971025437D, 5, 400f)));

    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                createStop(2, "1001", 28.05811731042478D, -82.41616877502503D, 4),
                createStop(4, "1002", 28.05812364854794D, -82.41617370439423D, 4),
                // this location is outside buffer
                createStop(5, "1003", 28.05673053256373D, -82.4170801432763D, 4)));

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  /**
   * See map of trip shape and stops (in GeoJSON) at
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
    StopTooFarFromTripShapeValidator underTest = new StopTooFarFromTripShapeValidator();

    underTest.tripTable =
        createTripTable(
            noticeContainer, ImmutableList.of(createTrip(4, "r1", "service1", "t1", "shape1")));

    underTest.stopTimeTable =
        GtfsStopTimeTableContainer.forEntities(new ArrayList<>(), noticeContainer);

    underTest.shapeTable =
        createShapeTable(
            noticeContainer,
            ImmutableList.of(
                createShapePoint(5, "shape1", 28.05724310653972D, -82.41350776611507D, 1, 400f),
                createShapePoint(6, "shape1", 28.05746701492806D, -82.41493135129478D, 2, 400f),
                createShapePoint(7, "shape1", 28.05800068503469f, -82.4159394137605D, 3, 400f),
                createShapePoint(8, "shape1", 28.05808869825447D, -82.41648754043338D, 4, 400f),
                createShapePoint(9, "shape1", 28.05809979887893D, -82.41773971025437D, 5, 400f)));

    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                createStop(2, "1001", 28.05811731042478D, -82.41616877502503D, 4),
                createStop(4, "1002", 28.05812364854794D, -82.41617370439423D, 4),
                // this location is outside buffer
                createStop(5, "1003", 28.05673053256373D, -82.4170801432763D, 4)));

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  /**
   * See map of trip shape and stops (in GeoJSON) at
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
    StopTooFarFromTripShapeValidator underTest = new StopTooFarFromTripShapeValidator();

    underTest.tripTable =
        createTripTable(
            noticeContainer, ImmutableList.of(createTrip(4, "r1", "service1", "t1", "shape1")));

    underTest.stopTimeTable =
        createStopTimeTable(
            noticeContainer,
            ImmutableList.of(
                createStopTime(5, "t1", "1001", 1),
                createStopTime(8, "t1", "1002", 2),
                createStopTime(9, "t1", "1003", 3)));

    underTest.shapeTable = GtfsShapeTableContainer.forEntities(new ArrayList<>(), noticeContainer);

    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                createStop(2, "1001", 28.05811731042478D, -82.41616877502503D, 4),
                createStop(4, "1002", 28.05812364854794D, -82.41617370439423D, 4),
                // this location is outside buffer
                createStop(5, "1003", 28.05673053256373D, -82.4170801432763D, 4)));

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  /**
   * See map of trip shape and stops (in GeoJSON) at
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
    StopTooFarFromTripShapeValidator underTest = new StopTooFarFromTripShapeValidator();

    underTest.tripTable =
        createTripTable(
            noticeContainer, ImmutableList.of(createTrip(4, "r1", "service1", "t1", "shape1")));

    underTest.stopTimeTable =
        createStopTimeTable(
            noticeContainer,
            ImmutableList.of(
                createStopTime(5, "t1", "1001", 1),
                createStopTime(8, "t1", "1002", 2),
                createStopTime(9, "t1", "1003", 3)));

    underTest.shapeTable = GtfsShapeTableContainer.forEntities(new ArrayList<>(), noticeContainer);

    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                createStop(2, "1001", 28.05811731042478D, -82.41616877502503D, 4),
                createStop(4, "1002", 28.05812364854794D, -82.41617370439423D, 4),
                // this location is outside buffer
                createStop(5, "1003", 28.05673053256373D, -82.4170801432763D, 4)));

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void eachTripShouldOnlyBeProcessedOnce() {
    NoticeContainer noticeContainer = new NoticeContainer();
    StopTooFarFromTripShapeValidator underTest =
        Mockito.spy(new StopTooFarFromTripShapeValidator());

    underTest.tripTable =
        createTripTable(
            noticeContainer,
            ImmutableList.of(
                createTrip(4, "r1", "service1", "t1", "shape1"),
                createTrip(9, "r2", "service2", "t2", "shape2")));

    underTest.stopTimeTable =
        createStopTimeTable(
            noticeContainer,
            ImmutableList.of(
                createStopTime(5, "t1", "1001", 1),
                createStopTime(8, "t1", "1002", 2),
                createStopTime(9, "t1", "1003", 3),
                createStopTime(10, "t2", "1004", 4),
                createStopTime(11, "t2", "1005", 5)));

    underTest.shapeTable =
        createShapeTable(
            noticeContainer,
            ImmutableList.of(
                createShapePoint(5, "shape1", 28.05724310653972D, -82.41350776611507D, 1, 400f),
                createShapePoint(6, "shape1", 28.05746701492806D, -82.41493135129478D, 2, 400f),
                createShapePoint(7, "shape1", 28.05800068503469D, -82.4159394137605D, 3, 400f),
                createShapePoint(8, "shape2", 16.373032D, -61.459167D, 4, 400f),
                createShapePoint(9, "shape2", 16.371539D, -61.459886D, 5, 400f)));

    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                createStop(2, "1001", 28.05808869825447D, -82.41648754043338D, 0),
                createStop(4, "1002", 28.05808869825447D, -82.41648754043338D, 0),
                // this location is outside buffer of shape1
                createStop(5, "1003", 28.05673053256373D, -82.4170801432763D, 4),
                createStop(7, "1004", 16.373032D, -61.459167D, 4),
                // this location is outside buffer of shape2
                createStop(8, "1005", 28.05673053256373D, -82.4170801432763D, 4)));

    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactlyElementsIn(
            new StopTooFarFromTripShapeNotice[] {
              new StopTooFarFromTripShapeNotice("1003", 3, "t1", "shape1", 100),
              new StopTooFarFromTripShapeNotice("1005", 5, "t2", "shape2", 100),
            });

    ArgumentCaptor<String> tripIdCapture = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<List<GtfsStopTime>> stopTimeCapture = ArgumentCaptor.forClass(List.class);
    ArgumentCaptor<String> shapeIdCapture = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<List<GtfsShape>> shapesCapture = ArgumentCaptor.forClass(List.class);
    ArgumentCaptor<GtfsStopTableContainer> stopTableCapture =
        ArgumentCaptor.forClass(GtfsStopTableContainer.class);
    ArgumentCaptor<Set<String>> cacheCapture = ArgumentCaptor.forClass(Set.class);

    verify(underTest, times(1)).validate(noticeContainer);
    verify(underTest, times(2))
        .checkStopsWithinTripShape(
            tripIdCapture.capture(),
            stopTimeCapture.capture(),
            shapeIdCapture.capture(),
            shapesCapture.capture(),
            stopTableCapture.capture(),
            cacheCapture.capture());

    // Make sure we only captured 2 sets of input parameters, one for each invocation
    assertThat(tripIdCapture.getAllValues().size()).isEqualTo(2);
    assertThat(stopTimeCapture.getAllValues().size()).isEqualTo(2);
    assertThat(shapeIdCapture.getAllValues().size()).isEqualTo(2);
    assertThat(shapesCapture.getAllValues().size()).isEqualTo(2);
    assertThat(stopTableCapture.getAllValues().size()).isEqualTo(2);
    assertThat(cacheCapture.getAllValues().size()).isEqualTo(2);

    // Check first execution parameters of checkStopsWithinTripShape()
    assertThat(tripIdCapture.getAllValues().get(0)).isEqualTo("t1");
    assertThat(stopTimeCapture.getAllValues().get(0))
        .isEqualTo(underTest.stopTimeTable.byTripId("t1"));
    assertThat(shapeIdCapture.getAllValues().get(0)).isEqualTo("shape1");
    assertThat(shapesCapture.getAllValues().get(0))
        .isEqualTo(underTest.shapeTable.byShapeId("shape1"));
    assertThat(stopTableCapture.getAllValues().get(0)).isEqualTo(underTest.stopTable);

    // Check 2nd execution parameters of checkStopsWithinTripShape()
    assertThat(tripIdCapture.getAllValues().get(1)).isEqualTo("t2");
    assertThat(stopTimeCapture.getAllValues().get(1))
        .isEqualTo(underTest.stopTimeTable.byTripId("t2"));
    assertThat(shapeIdCapture.getAllValues().get(1)).isEqualTo("shape2");
    assertThat(shapesCapture.getAllValues().get(1))
        .isEqualTo(underTest.shapeTable.byShapeId("shape2"));
    assertThat(stopTableCapture.getAllValues().get(1)).isEqualTo(underTest.stopTable);

    // Note that the contents of the cache are the final state, not the state at specific invocation
    // because it's passed by reference and not value
    Set<String> expected = new java.util.HashSet<>();
    expected.add("shape11001");
    expected.add("shape11002");
    expected.add("shape11003");
    expected.add("shape21005");
    expected.add("shape21004");
    assertThat(cacheCapture.getAllValues().get(0))
        .isEqualTo(expected);
    assertThat(cacheCapture.getAllValues().get(1))
        .isEqualTo(expected);

    Mockito.verifyNoMoreInteractions(underTest);
  }
}
