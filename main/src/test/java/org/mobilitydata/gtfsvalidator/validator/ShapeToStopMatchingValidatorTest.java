/*
 * Copyright 2021 Google LLC
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
import com.google.common.geometry.S2LatLng;
import com.google.common.truth.Correspondence;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteType;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.util.shape.StopToShapeMatcher;
import org.mobilitydata.gtfsvalidator.util.shape.StopToShapeMatcherSettings;
import org.mobilitydata.gtfsvalidator.validator.ShapeToStopMatchingValidator.StopHasTooManyMatchesForShapeNotice;
import org.mobilitydata.gtfsvalidator.validator.ShapeToStopMatchingValidator.StopTooFarFromShapeNotice;
import org.mobilitydata.gtfsvalidator.validator.ShapeToStopMatchingValidator.StopsMatchShapeOutOfOrderNotice;

@RunWith(JUnit4.class)
public class ShapeToStopMatchingValidatorTest {

  private static final double DOUBLE_PRECISION = 1e-4;
  private static final Correspondence<ValidationNotice, ValidationNotice> APPROX_SAME_NOTICE =
      Correspondence.from((n1, n2) -> noticeApproxEquals(n1, n2, DOUBLE_PRECISION), "same notice");
  private static final String TEST_TRIP_ID = "trip1";
  private static final String TEST_ROUTE_ID = "route1";
  private static final String TEST_SHAPE_ID = "shape1";

  private static boolean noticeApproxEquals(
      ValidationNotice n1, ValidationNotice n2, double maxError) {
    if (!n1.getClass().equals(n2.getClass())
        || !n1.getSeverityLevel().equals(n2.getSeverityLevel())) {
      return false;
    }
    for (Field field : n1.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      try {
        Object v1 = field.get(n1);
        Object v2 = field.get(n2);
        if (field.getType() == double.class) {
          if (Math.abs((Double) v1 - (Double) v2) >= maxError) {
            return false;
          }
        } else if (field.getType() == S2LatLng.class) {
          if (!((S2LatLng) v1).approxEquals((S2LatLng) v2, maxError)) {
            return false;
          }
        } else if (!Objects.equals(v1, v2)) {
          return false;
        }
      } catch (IllegalAccessException e) {
        // This should never happen.
        throw new RuntimeException(e);
      }
    }
    return true;
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsStop> stops,
      List<GtfsTrip> trips,
      List<GtfsRoute> routes,
      List<GtfsStopTime> stopTimes,
      List<GtfsShape> shapes,
      StopToShapeMatcher stopToShapeMatcher) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new ShapeToStopMatchingValidator(
            GtfsStopTableContainer.forEntities(stops, noticeContainer),
            GtfsTripTableContainer.forEntities(trips, noticeContainer),
            GtfsRouteTableContainer.forEntities(routes, noticeContainer),
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer),
            GtfsShapeTableContainer.forEntities(shapes, noticeContainer),
            stopToShapeMatcher)
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  private static GtfsTrip createTrip() {
    return new GtfsTrip.Builder()
        .setCsvRowNumber(2)
        .setTripId(TEST_TRIP_ID)
        .setRouteId(TEST_ROUTE_ID)
        .setShapeId(TEST_SHAPE_ID)
        .build();
  }

  private static GtfsRoute createRoute() {
    return new GtfsRoute.Builder()
        .setCsvRowNumber(2)
        .setRouteId(TEST_ROUTE_ID)
        .setRouteType(GtfsRouteType.BUS)
        .build();
  }

  private static GtfsShape createGtfsShape(
      int ptSequence, double latDegrees, double lngDegrees, Double shapeDistTraveled) {
    return new GtfsShape.Builder()
        .setShapeId(TEST_SHAPE_ID)
        .setCsvRowNumber(ptSequence + 2)
        .setShapePtSequence(ptSequence)
        .setShapePtLat(latDegrees)
        .setShapePtLon(lngDegrees)
        .setShapeDistTraveled(shapeDistTraveled)
        .build();
  }

  private static ImmutableList<GtfsShape> createShapePoints() {
    return ImmutableList.of(
        createGtfsShape(0, 47.365399, 8.525138, 0.0),
        createGtfsShape(1, 47.366013, 8.524972, 1.0),
        createGtfsShape(2, 47.366073, 8.525384, 2.0),
        createGtfsShape(3, 47.364120, 8.525886, 3.0),
        createGtfsShape(4, 47.364046, 8.525559, 4.0),
        createGtfsShape(5, 47.364376, 8.525376, 5.0),
        createGtfsShape(6, 47.364976, 8.525258, 6.0),
        createGtfsShape(7, 47.365016, 8.525666, 7.0),
        createGtfsShape(8, 47.365103, 8.525650, 8.0),
        createGtfsShape(9, 47.365143, 8.526015, 9.0));
  }

  private static String numberToStopId(int number) {
    return "s" + number;
  }

  private static GtfsStop createStop(int number, double stopLat, double stopLon) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(2 + number)
        .setStopId(numberToStopId(number))
        .setStopName("Stop " + number)
        .setStopLat(stopLat)
        .setStopLon(stopLon)
        .build();
  }

  private static List<GtfsStop> createStops(List<S2LatLng> points) {
    List<GtfsStop> stops = new ArrayList<>(points.size());
    for (int i = 0; i < points.size(); ++i) {
      stops.add(createStop(i, points.get(i).latDegrees(), points.get(i).lngDegrees()));
    }
    return stops;
  }

  private static List<GtfsStopTime> createStopTimes(Double[] shapeDistTraveled) {
    List<GtfsStopTime> stopTimes = new ArrayList<>(shapeDistTraveled.length);
    for (int i = 0; i < shapeDistTraveled.length; ++i) {
      stopTimes.add(
          new GtfsStopTime.Builder()
              .setCsvRowNumber(i + 2)
              .setStopId(numberToStopId(i))
              .setTripId(TEST_TRIP_ID)
              .setStopSequence(i)
              .setShapeDistTraveled(shapeDistTraveled[i])
              .build());
    }
    return stopTimes;
  }

  @Test
  public void goodFeed_yieldsNoNotice() {
    List<GtfsStop> stops =
        createStops(
            ImmutableList.of(
                S2LatLng.fromDegrees(47.365478, 8.525152),
                S2LatLng.fromDegrees(47.365051, 8.525622),
                S2LatLng.fromDegrees(47.364991, 8.525632)));
    List<GtfsStopTime> stopTimes = createStopTimes(new Double[] {0.2, 2.4, 6.8});

    assertThat(
            generateNotices(
                stops,
                ImmutableList.of(createTrip()),
                ImmutableList.of(createRoute()),
                stopTimes,
                createShapePoints(),
                new StopToShapeMatcher(new StopToShapeMatcherSettings())))
        .isEmpty();
  }

  @Test
  public void tooFar_yieldsNotice() {
    StopToShapeMatcherSettings settings = new StopToShapeMatcherSettings();
    settings.setMaxDistanceFromStopToShapeInMeters(150.0);

    List<GtfsStop> stops =
        createStops(
            ImmutableList.of(
                S2LatLng.fromDegrees(47.365478, 8.525152),
                S2LatLng.fromDegrees(47.366375, 8.527333),
                S2LatLng.fromDegrees(47.364991, 8.525632)));
    GtfsTrip trip = createTrip();
    List<GtfsStopTime> stopTimes = createStopTimes(new Double[] {0.2, 2.4, 6.8});

    assertThat(
            generateNotices(
                stops,
                ImmutableList.of(trip),
                ImmutableList.of(createRoute()),
                stopTimes,
                createShapePoints(),
                new StopToShapeMatcher(settings)))
        .comparingElementsUsing(APPROX_SAME_NOTICE)
        .containsExactly(
            new StopTooFarFromShapeNotice(
                trip,
                stopTimes.get(1),
                stops.get(1),
                S2LatLng.fromDegrees(47.366073, 8.525384),
                150.578313));
  }

  @Test
  public void tooManyMatches_yieldsNotice() {
    StopToShapeMatcherSettings settings = new StopToShapeMatcherSettings();
    settings.setMaxDistanceFromStopToShapeInMeters(20.0);
    settings.setPotentialMatchesForStopProblemThreshold(2);

    List<GtfsStop> stops = createStops(ImmutableList.of(S2LatLng.fromDegrees(47.365033, 8.525638)));
    GtfsTrip trip = createTrip();
    List<GtfsStopTime> stopTimes = createStopTimes(new Double[] {null});

    assertThat(
            generateNotices(
                stops,
                ImmutableList.of(trip),
                ImmutableList.of(createRoute()),
                stopTimes,
                createShapePoints(),
                new StopToShapeMatcher(settings)))
        .comparingElementsUsing(APPROX_SAME_NOTICE)
        .containsExactly(
            new StopHasTooManyMatchesForShapeNotice(
                trip,
                stopTimes.get(0),
                stops.get(0),
                S2LatLng.fromDegrees(47.365034, 8.525651),
                3));
  }

  @Test
  public void outOfOrder_yieldsNotice() {
    StopToShapeMatcherSettings settings = new StopToShapeMatcherSettings();
    settings.setMaxDistanceFromStopToShapeInMeters(20.0);

    List<GtfsStop> stops =
        createStops(
            ImmutableList.of(
                S2LatLng.fromDegrees(47.364107, 8.525701),
                S2LatLng.fromDegrees(47.366026, 8.525189)));
    GtfsTrip trip = createTrip();
    List<GtfsStopTime> stopTimes = createStopTimes(new Double[] {null, null});

    assertThat(
            generateNotices(
                stops,
                ImmutableList.of(trip),
                ImmutableList.of(createRoute()),
                stopTimes,
                createShapePoints(),
                new StopToShapeMatcher(settings)))
        .comparingElementsUsing(APPROX_SAME_NOTICE)
        .containsExactly(
            new StopsMatchShapeOutOfOrderNotice(
                trip,
                stopTimes.get(1),
                stops.get(1),
                S2LatLng.fromDegrees(47.366044, 8.525183),
                stopTimes.get(0),
                stops.get(0),
                S2LatLng.fromDegrees(47.364081, 8.525713)));
  }
}
