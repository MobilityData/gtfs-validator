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

package org.mobilitydata.gtfsvalidator.util.shape;

import com.google.common.collect.ImmutableList;
import com.google.common.geometry.S2Point;
import com.google.common.truth.Correspondence;
import com.google.common.truth.Expect;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.util.shape.StopPoints.StationSize;
import org.mobilitydata.gtfsvalidator.util.shape.StopPoints.StopPoint;

@RunWith(JUnit4.class)
public class StopPointsTest {
  @Rule public final Expect expect = Expect.create();

  private static final double DOUBLE_PRECISION = 0.1;

  private static final Correspondence<StopPoint, StopPoint> APPROX_SAME_POINT =
      Correspondence.from((p1, p2) -> p1.approxEquals(p2, DOUBLE_PRECISION), "same stop point");

  private static GtfsStop createStop(
      String stopId, String stopName, double stopLat, double stopLon) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(stopId.hashCode())
        .setStopId(stopId)
        .setStopName(stopName)
        .setStopLat(stopLat)
        .setStopLon(stopLon)
        .build();
  }

  private static ImmutableList<GtfsStop> createStops() {
    return ImmutableList.of(
        createStop("zuerichHB", "Zürich HB", 47.3778615, 8.5381339),
        createStop("selnau", "Zürich Selnau", 47.3706941, 8.5358666),
        createStop("giesshuebel", "Zürich Giesshübel", 47.3680534, 8.5361065));
  }

  private static ImmutableList<GtfsStopTime> createStopTimes() {
    return ImmutableList.of(
        new GtfsStopTime.Builder().setStopId("zuerichHB").build(),
        new GtfsStopTime.Builder().setStopId("selnau").setShapeDistTraveled(10.0).build(),
        new GtfsStopTime.Builder().setStopId("giesshuebel").setShapeDistTraveled(20.0).build());
  }

  @Test
  public void fromStopTimes_smallStation() {
    ImmutableList<GtfsStop> stops = createStops();
    ImmutableList<GtfsStopTime> stopTimes = createStopTimes();
    StopPoints stopPoints =
        StopPoints.fromStopTimes(
            stopTimes,
            GtfsStopTableContainer.forEntities(createStops(), new NoticeContainer()),
            StationSize.SMALL);
    expect
        .that(stopPoints.getPoints())
        .comparingElementsUsing(APPROX_SAME_POINT)
        .containsExactly(
            new StopPoint(stops.get(0).stopLatLon().toPoint(), 0, stopTimes.get(0), false),
            new StopPoint(stops.get(1).stopLatLon().toPoint(), 10.0, stopTimes.get(1), false),
            new StopPoint(stops.get(2).stopLatLon().toPoint(), 20.0, stopTimes.get(2), false));
  }

  @Test
  public void fromStopTimes_largeStation() {
    ImmutableList<GtfsStop> stops = createStops();
    ImmutableList<GtfsStopTime> stopTimes = createStopTimes();
    StopPoints stopPoints =
        StopPoints.fromStopTimes(
            stopTimes,
            GtfsStopTableContainer.forEntities(createStops(), new NoticeContainer()),
            StationSize.LARGE);
    expect
        .that(stopPoints.getPoints())
        .comparingElementsUsing(APPROX_SAME_POINT)
        .containsExactly(
            new StopPoint(stops.get(0).stopLatLon().toPoint(), 0, stopTimes.get(0), true),
            new StopPoint(stops.get(1).stopLatLon().toPoint(), 10.0, stopTimes.get(1), false),
            new StopPoint(stops.get(2).stopLatLon().toPoint(), 20.0, stopTimes.get(2), true));
  }

  @Test
  public void hasUserDistance() {
    expect.that(new StopPoints(ImmutableList.of()).hasUserDistance()).isFalse();
    expect
        .that(
            new StopPoints(ImmutableList.of(new StopPoint(new S2Point(), 0, null, false)))
                .hasUserDistance())
        .isFalse();
    expect
        .that(
            new StopPoints(
                    ImmutableList.of(
                        new StopPoint(new S2Point(), 0.0, null, false),
                        new StopPoint(new S2Point(), 1.0, null, false)))
                .hasUserDistance())
        .isTrue();
    expect
        .that(
            new StopPoints(
                    ImmutableList.of(
                        new StopPoint(new S2Point(), 0.0, null, false),
                        new StopPoint(new S2Point(), 1.0, null, false),
                        new StopPoint(new S2Point(), 0.0, null, false)))
                .hasUserDistance())
        .isFalse();
  }
}
