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
import org.mobilitydata.gtfsvalidator.notice.StopTooFarFromTripShapeNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

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
}
