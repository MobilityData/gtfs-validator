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

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.validator.StopTooFarFromTripShapeValidator.StopTooFarFromTripShapeNotice;

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

  private List<ValidationNotice> generateNotices(
      List<GtfsStop> stops,
      List<GtfsStopTime> stopTimes,
      List<GtfsShape> shapes,
      List<GtfsTrip> trips) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new StopTooFarFromTripShapeValidator(
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer),
            GtfsTripTableContainer.forEntities(trips, noticeContainer),
            GtfsShapeTableContainer.forEntities(shapes, noticeContainer),
            GtfsStopTableContainer.forEntities(stops, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void stopOutsideTripShapeShouldGenerateNotice() {
    // See map of trip shape and stops (in GeoJSON) at
    // https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop(2, "1001", 28.05808869825447D, -82.41648754043338D, 0),
                    createStop(4, "1002", 28.05809979887893D, -82.41773971025437D, 0),
                    // this location is outside buffer
                    createStop(5, "1003", 17.456467, -45.4569865, 0)),
                ImmutableList.of(
                    createStopTime(5, "t1", "1001", 1),
                    createStopTime(8, "t1", "1002", 2),
                    createStopTime(9, "t1", "1003", 3)),
                ImmutableList.of(
                    createShapePoint(
                        5, "shape id", 28.05724310653972D, -82.41350776611507D, 1, 400f),
                    createShapePoint(
                        6, "shape id", 28.05746701492806D, -82.41493135129478D, 2, 400f),
                    createShapePoint(
                        7, "shape id", 28.05800068503469D, -82.4159394137605D, 3, 400f),
                    createShapePoint(
                        8, "shape id", 28.05808869825447D, -82.41648754043338D, 4, 400f),
                    createShapePoint(
                        9, "shape id", 28.05809979887893D, -82.41773971025437D, 5, 400f)),
                ImmutableList.of(createTrip(55, "route id", "service id", "t1", "shape id"))))
        .containsExactly(new StopTooFarFromTripShapeNotice("1003", 3, "t1", "shape id", 100));
  }

  @Test
  public void stopWithinTripShapeShouldNotGenerateNotice() {
    // See map of trip shape and stops (in GeoJSON) at
    // https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop(2, "1001", 28.05808869825447D, -82.41648754043338D, 4),
                    createStop(4, "1002", 28.05809979887893D, -82.41773971025437D, 4)),
                ImmutableList.of(
                    createStopTime(5, "t1", "1001", 1), createStopTime(8, "t1", "1002", 2)),
                ImmutableList.of(
                    createShapePoint(
                        5, "shape id", 28.05724310653972D, -82.41350776611507D, 1, 400f),
                    createShapePoint(
                        6, "shape id", 28.05746701492806D, -82.41493135129478D, 2, 400f),
                    createShapePoint(
                        7, "shape id", 28.05800068503469f, -82.4159394137605D, 3, 400f),
                    createShapePoint(
                        8, "shape id", 28.05808869825447D, -82.41648754043338D, 4, 400f),
                    createShapePoint(
                        9, "shape id", 28.05809979887893D, -82.41773971025437D, 5, 400f)),
                ImmutableList.of(createTrip(55, "route id", "service id", "t1", "shape id"))))
        .isEmpty();
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
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop(2, "1001", 28.05808869825447D, -82.41648754043338D, 0),
                    createStop(4, "1002", 28.05809979887893D, -82.41773971025437D, 0),
                    // this location is outside buffer
                    createStop(5, "1003", 28.05673053256373D, -82.4170801432763D, 4)),
                ImmutableList.of(
                    createStopTime(5, "t1", "1001", 1),
                    createStopTime(8, "t1", "1002", 2),
                    createStopTime(9, "t1", "1003", 3)),
                ImmutableList.of(
                    createShapePoint(5, "shape1", 28.05724310653972D, -82.41350776611507D, 1, 400f),
                    createShapePoint(6, "shape1", 28.05746701492806D, -82.41493135129478D, 2, 400f),
                    createShapePoint(7, "shape1", 28.05800068503469f, -82.4159394137605D, 3, 400f),
                    createShapePoint(8, "shape1", 28.05808869825447D, -82.41648754043338D, 4, 400f),
                    createShapePoint(
                        9, "shape1", 28.05809979887893D, -82.41773971025437D, 5, 400f)),
                ImmutableList.of(
                    createTrip(4, "r1", "service1", "t1", "shape1"),
                    createTrip(9, "r1", "service1", "t2", "shape1"))))
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
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop(2, "1001", 28.05811731042478D, -82.41616877502503D, 0),
                    createStop(4, "1002", 28.05812364854794D, -82.41617370439423D, 0)),
                ImmutableList.of(
                    createStopTime(5, "t1", "1001", 1), createStopTime(8, "t1", "1002", 2)),
                // No shapes.txt data
                ImmutableList.of(),
                ImmutableList.of(createTrip(4, "r1", "service1", "t1", null))))
        .isEmpty();
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
    assertThat(
            generateNotices(
                ImmutableList.of(
                    // No location - optional for location_type=4
                    createStop(2, "1001", null, null, 4),
                    // No location - optional for location_type=4
                    createStop(4, "1002", null, null, 4),
                    // No location - optional for location_type=4
                    createStop(5, "1003", null, null, 4)),
                ImmutableList.of(
                    createStopTime(5, "t1", "1001", 1), createStopTime(8, "t1", "1002", 2)),
                ImmutableList.of(
                    createShapePoint(5, "shape1", 28.05724310653972D, -82.41350776611507D, 1, 400f),
                    createShapePoint(6, "shape1", 28.05746701492806D, -82.41493135129478D, 2, 400f),
                    createShapePoint(7, "shape1", 28.05800068503469f, -82.4159394137605D, 3, 400f),
                    createShapePoint(8, "shape1", 28.05808869825447D, -82.41648754043338D, 4, 400f),
                    createShapePoint(
                        9, "shape1", 28.05809979887893D, -82.41773971025437D, 5, 400f)),
                ImmutableList.of(createTrip(4, "r1", "service1", "t1", "shape1"))))
        .isEmpty();
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
    assertThat(
            generateNotices(
                ImmutableList.of(
                    // this location is inside buffer
                    createStop(2, "1001", 28.05724310653972D, -82.41350776611507D, 0)),
                ImmutableList.of(
                    createStopTime(5, "t1", "1001", 1),
                    createStopTime(8, "t1", "1002", 2),
                    createStopTime(9, "t1", "1003", 3)),
                ImmutableList.of(
                    createShapePoint(5, "shape1", 28.05724310653972D, -82.41350776611507D, 1, 400f),
                    createShapePoint(6, "shape1", 28.05746701492806D, -82.41493135129478D, 2, 400f),
                    createShapePoint(7, "shape1", 28.05800068503469f, -82.4159394137605D, 3, 400f),
                    createShapePoint(8, "shape1", 28.05808869825447D, -82.41648754043338D, 4, 400f),
                    createShapePoint(
                        9, "shape1", 28.05809979887893D, -82.41773971025437D, 5, 400f)),
                ImmutableList.of(createTrip(4, "r1", "service1", "t1", "shape1"))))
        .isEmpty();
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
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop(2, "1001", 28.05811731042478D, -82.41616877502503D, 4),
                    createStop(4, "1002", 28.05812364854794D, -82.41617370439423D, 4),
                    // this location is outside buffer
                    createStop(5, "1003", 28.05673053256373D, -82.4170801432763D, 4)),
                ImmutableList.of(),
                ImmutableList.of(
                    createShapePoint(5, "shape1", 28.05724310653972D, -82.41350776611507D, 1, 400f),
                    createShapePoint(6, "shape1", 28.05746701492806D, -82.41493135129478D, 2, 400f),
                    createShapePoint(7, "shape1", 28.05800068503469f, -82.4159394137605D, 3, 400f),
                    createShapePoint(8, "shape1", 28.05808869825447D, -82.41648754043338D, 4, 400f),
                    createShapePoint(
                        9, "shape1", 28.05809979887893D, -82.41773971025437D, 5, 400f)),
                ImmutableList.of(createTrip(4, "r1", "service1", "t1", "shape1"))))
        .isEmpty();
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
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop(2, "1001", 28.05811731042478D, -82.41616877502503D, 4),
                    createStop(4, "1002", 28.05812364854794D, -82.41617370439423D, 4),
                    // this location is outside buffer
                    createStop(5, "1003", 28.05673053256373D, -82.4170801432763D, 4)),
                ImmutableList.of(),
                ImmutableList.of(
                    createShapePoint(5, "shape1", 28.05724310653972D, -82.41350776611507D, 1, 400f),
                    createShapePoint(6, "shape1", 28.05746701492806D, -82.41493135129478D, 2, 400f),
                    createShapePoint(7, "shape1", 28.05800068503469f, -82.4159394137605D, 3, 400f),
                    createShapePoint(8, "shape1", 28.05808869825447D, -82.41648754043338D, 4, 400f),
                    createShapePoint(
                        9, "shape1", 28.05809979887893D, -82.41773971025437D, 5, 400f)),
                ImmutableList.of(createTrip(4, "r1", "service1", "t1", "shape1"))))
        .isEmpty();
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
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop(2, "1001", 28.05811731042478D, -82.41616877502503D, 4),
                    createStop(4, "1002", 28.05812364854794D, -82.41617370439423D, 4),
                    // this location is outside buffer
                    createStop(5, "1003", 28.05673053256373D, -82.4170801432763D, 4)),
                ImmutableList.of(
                    createStopTime(5, "t1", "1001", 1),
                    createStopTime(8, "t1", "1002", 2),
                    createStopTime(9, "t1", "1003", 3)),
                ImmutableList.of(),
                ImmutableList.of(createTrip(4, "r1", "service1", "t1", "shape1"))))
        .isEmpty();
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
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop(2, "1001", 28.05811731042478D, -82.41616877502503D, 4),
                    createStop(4, "1002", 28.05812364854794D, -82.41617370439423D, 4),
                    // this location is outside buffer
                    createStop(5, "1003", 28.05673053256373D, -82.4170801432763D, 4)),
                ImmutableList.of(
                    createStopTime(5, "t1", "1001", 1),
                    createStopTime(8, "t1", "1002", 2),
                    createStopTime(9, "t1", "1003", 3)),
                ImmutableList.of(),
                ImmutableList.of(createTrip(4, "r1", "service1", "t1", "shape1"))))
        .isEmpty();
  }
}
