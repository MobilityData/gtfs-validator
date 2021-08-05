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
import static org.mobilitydata.gtfsvalidator.validator.StopTimeTravelSpeedValidator.getSpeedKphBetweenStops;
import static org.mobilitydata.gtfsvalidator.validator.StopTimeTravelSpeedValidator.getStopLatLng;

import com.google.common.collect.ImmutableList;
import com.google.common.geometry.S2LatLng;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.util.S2Earth;
import org.mobilitydata.gtfsvalidator.validator.StopTimeTravelSpeedValidator.FastTravelBetweenConsecutiveStopsNotice;
import org.mobilitydata.gtfsvalidator.validator.StopTimeTravelSpeedValidator.FastTravelBetweenFarStopsNotice;

@RunWith(JUnit4.class)
public final class StopTimeTravelSpeedValidatorTest {

  private static List<ValidationNotice> generateNotices(
      List<GtfsRoute> routes,
      List<GtfsTrip> trips,
      List<GtfsStopTime> stopTimes,
      List<GtfsStop> stops) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new StopTimeTravelSpeedValidator(
            GtfsRouteTableContainer.forEntities(routes, noticeContainer),
            GtfsTripTableContainer.forEntities(trips, noticeContainer),
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer),
            GtfsStopTableContainer.forEntities(stops, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  private static final String TEST_ROUTE_ID = "route0";
  private static final String TEST_TRIP_ID = "trip0";

  private static GtfsRoute createRoute(GtfsRouteType routeType) {
    return new GtfsRoute.Builder()
        .setCsvRowNumber(2)
        .setRouteId(TEST_ROUTE_ID)
        .setRouteType(routeType)
        .build();
  }

  private static GtfsTrip createTrip() {
    return new GtfsTrip.Builder()
        .setCsvRowNumber(2)
        .setTripId(TEST_TRIP_ID)
        .setRouteId(TEST_ROUTE_ID)
        .build();
  }

  private static List<GtfsStop> createStops(List<S2LatLng> points) {
    List<GtfsStop> stops = new ArrayList<>(points.size());
    for (int i = 0; i < points.size(); ++i) {
      stops.add(
          new GtfsStop.Builder()
              .setCsvRowNumber(i + 2)
              .setStopId("s" + i)
              .setStopName("Stop " + i)
              .setStopLat(points.get(i).latDegrees())
              .setStopLon(points.get(i).lngDegrees())
              .build());
    }
    return stops;
  }

  private static List<GtfsStopTime> createStopTimesSameDepartureArrival(List<GtfsTime> times) {
    List<GtfsStopTime> stopTimes = new ArrayList<>(times.size());
    for (int i = 0; i < times.size(); ++i) {
      stopTimes.add(
          new GtfsStopTime.Builder()
              .setCsvRowNumber(i + 2)
              .setTripId(TEST_TRIP_ID)
              .setStopId("s" + i)
              .setStopSequence(i)
              .setArrivalTime(times.get(i))
              .setDepartureTime(times.get(i))
              .build());
    }
    return stopTimes;
  }

  private static FastTravelBetweenConsecutiveStopsNotice
      createFastTravelBetweenConsecutiveStopsNotice(
          GtfsTrip trip,
          GtfsStopTime stopTime1,
          GtfsStop stop1,
          GtfsStopTime stopTime2,
          GtfsStop stop2) {
    final double distanceKm = S2Earth.getDistanceKm(stop1.stopLatLon(), stop2.stopLatLon());
    return new FastTravelBetweenConsecutiveStopsNotice(
        trip,
        stopTime1,
        stop1,
        stopTime2,
        stop2,
        getSpeedKphBetweenStops(distanceKm, stopTime1, stopTime2),
        distanceKm);
  }

  private static FastTravelBetweenFarStopsNotice createFastTravelBetweenFarStopsNotice(
      GtfsTrip trip,
      GtfsStopTime stopTime1,
      GtfsStop stop1,
      GtfsStopTime stopTime2,
      GtfsStop stop2,
      double distanceKm) {
    return new FastTravelBetweenFarStopsNotice(
        trip,
        stopTime1,
        stop1,
        stopTime2,
        stop2,
        getSpeedKphBetweenStops(distanceKm, stopTime1, stopTime2),
        distanceKm);
  }

  @Test
  public void bus100kph_yieldsNoNotice() {
    // Two stops with ~5km between them.
    List<GtfsStop> stops =
        createStops(
            ImmutableList.of(
                S2LatLng.fromDegrees(47.457962, 8.555991),
                S2LatLng.fromDegrees(47.414149, 8.540938)));
    GtfsRoute route = createRoute(GtfsRouteType.BUS);
    GtfsTrip trip = createTrip();
    // 5km in 200 seconds = 90 kph: under our limit of 150 kph for buses.
    List<GtfsStopTime> stopTimes =
        createStopTimesSameDepartureArrival(
            ImmutableList.of(GtfsTime.fromString("08:00:00"), GtfsTime.fromString("08:03:20")));
    assertThat(generateNotices(ImmutableList.of(route), ImmutableList.of(trip), stopTimes, stops))
        .isEmpty();
  }

  @Test
  public void bus180kph_yieldsNotice() {
    // Two stops with ~5km between them.
    List<GtfsStop> stops =
        createStops(
            ImmutableList.of(
                S2LatLng.fromDegrees(47.457962, 8.555991),
                S2LatLng.fromDegrees(47.414149, 8.540938)));
    GtfsRoute route = createRoute(GtfsRouteType.BUS);
    GtfsTrip trip = createTrip();
    // 5km in 100 seconds = 180 kph = over our limit of 150 kph for buses.
    List<GtfsStopTime> stopTimes =
        createStopTimesSameDepartureArrival(
            ImmutableList.of(GtfsTime.fromString("08:00:00"), GtfsTime.fromString("08:01:40")));
    assertThat(generateNotices(ImmutableList.of(route), ImmutableList.of(trip), stopTimes, stops))
        .containsExactly(
            createFastTravelBetweenConsecutiveStopsNotice(
                trip, stopTimes.get(0), stops.get(0), stopTimes.get(1), stops.get(1)));
  }

  @Test
  public void train300kph_yieldsNoNotice() {
    // Two stops with ~5km between them.
    List<GtfsStop> stops =
        createStops(
            ImmutableList.of(
                S2LatLng.fromDegrees(47.457962, 8.555991),
                S2LatLng.fromDegrees(47.414149, 8.540938)));
    GtfsRoute route = createRoute(GtfsRouteType.RAIL);
    GtfsTrip trip = createTrip();
    // The limit is high (300 kph) for rail.
    List<GtfsStopTime> stopTimes =
        createStopTimesSameDepartureArrival(
            ImmutableList.of(GtfsTime.fromString("08:00:00"), GtfsTime.fromString("08:01:40")));
    assertThat(generateNotices(ImmutableList.of(route), ImmutableList.of(trip), stopTimes, stops))
        .isEmpty();
  }

  @Test
  public void minuteAccuracy_yieldsNoNotice() {
    // Two stops with ~2km between them.
    List<GtfsStop> stops =
        createStops(
            ImmutableList.of(
                S2LatLng.fromDegrees(47.457962, 8.555991),
                S2LatLng.fromDegrees(47.442304, 8.570692)));
    GtfsRoute route = createRoute(GtfsRouteType.RAIL);
    GtfsTrip trip = createTrip();
    // We evaluate the case when two stop-times only have minute accuracy. There should be a minute
    // of slack in the times.  2km in 60 seconds = 120 kph is under our limit of 300 kph for rail.
    List<GtfsStopTime> stopTimes =
        createStopTimesSameDepartureArrival(
            ImmutableList.of(GtfsTime.fromString("08:00:00"), GtfsTime.fromString("08:00:00")));
    assertThat(generateNotices(ImmutableList.of(route), ImmutableList.of(trip), stopTimes, stops))
        .isEmpty();
  }

  @Test
  public void farStops_yieldsNoNotice() {
    // Two stops with ~10.5km between them, and another one roughly in the middle.
    List<GtfsStop> stops =
        createStops(
            ImmutableList.of(
                S2LatLng.fromDegrees(47.457962, 8.555991),
                S2LatLng.fromDegrees(47.414149, 8.540938),
                S2LatLng.fromDegrees(47.365122, 8.524940)));
    GtfsRoute route = createRoute(GtfsRouteType.BUS);
    GtfsTrip trip = createTrip();
    // 10.5km in 300 seconds = 90 kph = under our limit of 150 kph for buses.
    List<GtfsStopTime> stopTimes =
        createStopTimesSameDepartureArrival(
            ImmutableList.of(
                GtfsTime.fromString("08:00:00"),
                GtfsTime.fromString("08:02:30"),
                GtfsTime.fromString("08:05:00")));
    assertThat(generateNotices(ImmutableList.of(route), ImmutableList.of(trip), stopTimes, stops))
        .isEmpty();
  }

  @Test
  public void farStops_yieldsNotice() {
    // Two stops with ~10.5km between them, and another one roughly in the middle.
    List<GtfsStop> stops =
        createStops(
            ImmutableList.of(
                S2LatLng.fromDegrees(47.457962, 8.555991),
                S2LatLng.fromDegrees(47.414149, 8.540938),
                S2LatLng.fromDegrees(47.365122, 8.524940)));
    GtfsRoute route = createRoute(GtfsRouteType.BUS);
    GtfsTrip trip = createTrip();
    // 10.5km in 150 seconds = 180 kph = over our limit of 150 kph for buses.
    List<GtfsStopTime> stopTimes =
        createStopTimesSameDepartureArrival(
            ImmutableList.of(
                GtfsTime.fromString("08:00:00"),
                GtfsTime.fromString("08:01:15"),
                GtfsTime.fromString("08:02:30")));
    assertThat(generateNotices(ImmutableList.of(route), ImmutableList.of(trip), stopTimes, stops))
        .containsExactly(
            createFastTravelBetweenConsecutiveStopsNotice(
                trip, stopTimes.get(0), stops.get(0), stopTimes.get(1), stops.get(1)),
            createFastTravelBetweenConsecutiveStopsNotice(
                trip, stopTimes.get(1), stops.get(1), stopTimes.get(2), stops.get(2)),
            createFastTravelBetweenFarStopsNotice(
                trip,
                stopTimes.get(0),
                stops.get(0),
                stopTimes.get(2),
                stops.get(2),
                S2Earth.getDistanceKm(stops.get(0).stopLatLon(), stops.get(1).stopLatLon())
                    + S2Earth.getDistanceKm(stops.get(1).stopLatLon(), stops.get(2).stopLatLon())));
  }

  @Test
  public void farStopsMissingTimeInBetween_yieldsNotice() {
    // Two stops with ~10.5km between them, and another one roughly in the middle.
    // Times are provided for the first and last stops but for the middle one.
    List<GtfsStop> stops =
        createStops(
            ImmutableList.of(
                S2LatLng.fromDegrees(47.457962, 8.555991),
                S2LatLng.fromDegrees(47.414149, 8.540938),
                S2LatLng.fromDegrees(47.365122, 8.524940)));
    GtfsRoute route = createRoute(GtfsRouteType.BUS);
    GtfsTrip trip = createTrip();
    // 10.5km in 150 seconds = 180 kph = over our limit of 150 kph for buses.
    List<GtfsStopTime> stopTimes =
        createStopTimesSameDepartureArrival(
            Arrays.asList( // ImmutableList does not support null items.
                GtfsTime.fromString("08:00:00"),
                null, // No time for the middle stop.
                GtfsTime.fromString("08:02:30")));
    assertThat(generateNotices(ImmutableList.of(route), ImmutableList.of(trip), stopTimes, stops))
        .containsExactly(
            createFastTravelBetweenFarStopsNotice(
                trip,
                stopTimes.get(0),
                stops.get(0),
                stopTimes.get(2),
                stops.get(2),
                S2Earth.getDistanceKm(stops.get(0).stopLatLon(), stops.get(1).stopLatLon())
                    + S2Earth.getDistanceKm(stops.get(1).stopLatLon(), stops.get(2).stopLatLon())));
  }

  @Test
  public void getSpeedKphBetweenStops_travelBackInTime() {
    // The data says that the vehicle travels back in time. We treat it as a normal travel forward
    // in time by 1 minute.
    List<GtfsStopTime> stopTimes =
        createStopTimesSameDepartureArrival(
            ImmutableList.of(GtfsTime.fromString("08:01:00"), GtfsTime.fromString("08:00:00")));
    assertThat(getSpeedKphBetweenStops(1, stopTimes.get(0), stopTimes.get(1))).isEqualTo(60);
  }

  @Test
  public void getSpeedKphBetweenStops_teleport() {
    // The data says that the vehicle teleports. We treat it as a normal travel forward in time by 1
    // minute.
    List<GtfsStopTime> stopTimes =
        createStopTimesSameDepartureArrival(ImmutableList.of(GtfsTime.fromString("08:00:00")));
    assertThat(getSpeedKphBetweenStops(2, stopTimes.get(0), stopTimes.get(0))).isEqualTo(120);
  }

  @Test
  public void getStopLatLng_chain() {
    List<GtfsStop> stops =
        ImmutableList.of(
            new GtfsStop.Builder().setStopId("boardingArea1").setParentStation("platform1").build(),
            new GtfsStop.Builder().setStopId("platform1").setParentStation("station1").build(),
            new GtfsStop.Builder().setStopId("station1").setStopLat(40.0).setStopLon(30.0).build(),
            new GtfsStop.Builder().setStopId("stop1").setStopLat(50.0).setStopLon(60.0).build());
    GtfsStopTableContainer stopTable =
        GtfsStopTableContainer.forEntities(stops, new NoticeContainer());
    assertThat(getStopLatLng(stopTable, "boardingArea1")).isEqualTo(stops.get(2).stopLatLon());
    assertThat(getStopLatLng(stopTable, "platform1")).isEqualTo(stops.get(2).stopLatLon());
    assertThat(getStopLatLng(stopTable, "station1")).isEqualTo(stops.get(2).stopLatLon());

    assertThat(getStopLatLng(stopTable, "stop1")).isEqualTo(stops.get(3).stopLatLon());
  }
}
