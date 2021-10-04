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

package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.truth.Truth8.assertThat;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.BOARDING_AREA;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.ENTRANCE;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.STATION;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.STOP;
import static org.mobilitydata.gtfsvalidator.util.StopUtil.getIncludingStation;

import com.google.common.collect.ImmutableList;
import com.google.common.geometry.S2LatLng;
import com.google.common.truth.Expect;
import javax.annotation.Nullable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;

@RunWith(JUnit4.class)
public class StopUtilTest {
  @Rule public final Expect expect = Expect.create();

  private static String numberToStopId(int number) {
    return "s" + number;
  }

  private static GtfsStop createStop(
      int id, @Nullable Double stopLat, @Nullable Double stopLon, @Nullable String parentStation) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(id)
        .setStopId(numberToStopId(id))
        .setStopLat(stopLat)
        .setStopLon(stopLon)
        .setParentStation(parentStation)
        .build();
  }

  @Test
  public void getStopOrParentLatLng_valid() {
    ImmutableList<GtfsStop> stops =
        ImmutableList.of(
            createStop(0, 10.0, 11.0, null),
            createStop(1, 20.0, 21.0, numberToStopId(0)),
            createStop(2, null, null, numberToStopId(0)),
            createStop(3, null, null, numberToStopId(2)));
    GtfsStopTableContainer stopTable =
        GtfsStopTableContainer.forEntities(stops, new NoticeContainer());

    expect
        .that(StopUtil.getStopOrParentLatLng(stopTable, numberToStopId(0)))
        .isEqualTo(stops.get(0).stopLatLon());
    expect
        .that(StopUtil.getStopOrParentLatLng(stopTable, numberToStopId(1)))
        .isEqualTo(stops.get(1).stopLatLon());
    expect
        .that(StopUtil.getStopOrParentLatLng(stopTable, numberToStopId(2)))
        .isEqualTo(stops.get(0).stopLatLon());
    expect
        .that(StopUtil.getStopOrParentLatLng(stopTable, numberToStopId(3)))
        .isEqualTo(stops.get(0).stopLatLon());
  }

  @Test
  public void getStopOrParentLatLng_infiniteLoop() {
    ImmutableList<GtfsStop> stops =
        ImmutableList.of(
            createStop(0, null, null, numberToStopId(1)),
            createStop(1, null, null, numberToStopId(0)));
    GtfsStopTableContainer stopTable =
        GtfsStopTableContainer.forEntities(stops, new NoticeContainer());

    expect
        .that(StopUtil.getStopOrParentLatLng(stopTable, numberToStopId(0)))
        .isEqualTo(S2LatLng.CENTER);
  }

  @Test
  public void getStopOrParentLatLng_noLatLng() {
    ImmutableList<GtfsStop> stops = ImmutableList.of(createStop(0, null, null, null));
    GtfsStopTableContainer stopTable =
        GtfsStopTableContainer.forEntities(stops, new NoticeContainer());

    expect
        .that(StopUtil.getStopOrParentLatLng(stopTable, numberToStopId(0)))
        .isEqualTo(S2LatLng.CENTER);
  }

  private static GtfsStop createStop(
      int id, GtfsLocationType locationType, @Nullable String parentStation) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(id)
        .setStopId(numberToStopId(id))
        .setLocationType(locationType)
        .setParentStation(parentStation)
        .build();
  }

  @Test
  public void getIncludingStation_valid() {
    ImmutableList<GtfsStop> stops =
        ImmutableList.of(
            createStop(0, STATION, null),
            createStop(1, STOP, numberToStopId(0)),
            createStop(2, BOARDING_AREA, numberToStopId(1)),
            createStop(3, ENTRANCE, numberToStopId(0)));
    GtfsStopTableContainer stopTable =
        GtfsStopTableContainer.forEntities(stops, new NoticeContainer());

    assertThat(getIncludingStation(stopTable, numberToStopId(0))).hasValue(stops.get(0));
    assertThat(getIncludingStation(stopTable, numberToStopId(1))).hasValue(stops.get(0));
    assertThat(getIncludingStation(stopTable, numberToStopId(2))).hasValue(stops.get(0));
    assertThat(getIncludingStation(stopTable, numberToStopId(3))).hasValue(stops.get(0));
  }

  @Test
  public void getIncludingStation_infiniteLoop() {
    ImmutableList<GtfsStop> stops =
        ImmutableList.of(
            createStop(0, STOP, numberToStopId(1)), createStop(1, STOP, numberToStopId(0)));
    GtfsStopTableContainer stopTable =
        GtfsStopTableContainer.forEntities(stops, new NoticeContainer());

    assertThat(getIncludingStation(stopTable, numberToStopId(0))).isEmpty();
  }

  @Test
  public void getIncludingStation_noParent() {
    ImmutableList<GtfsStop> stops = ImmutableList.of(createStop(0, STOP, null));
    GtfsStopTableContainer stopTable =
        GtfsStopTableContainer.forEntities(stops, new NoticeContainer());

    assertThat(getIncludingStation(stopTable, numberToStopId(0))).isEmpty();
  }
}
