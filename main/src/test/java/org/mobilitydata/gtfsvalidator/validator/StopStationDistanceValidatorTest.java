/*
 * Copyright 2026 MobilityData IO
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
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.validator.StopStationDistanceValidator.StationsTooCloseNotice;
import org.mobilitydata.gtfsvalidator.validator.StopStationDistanceValidator.StopTooCloseToNonParentStationNotice;
import org.mobilitydata.gtfsvalidator.validator.StopStationDistanceValidator.StopsTooCloseNotice;

@RunWith(JUnit4.class)
public class StopStationDistanceValidatorTest {

  // 0.00001 degrees of latitude is ~1.11 meters (< 2m).
  private static final double BASE_LAT = 45.5017;
  private static final double BASE_LON = -73.5673;
  private static final double CLOSE_LAT = BASE_LAT + 0.00001;
  // 0.00003 degrees of latitude is ~3.34 meters (> 2m).
  private static final double FAR_LAT = BASE_LAT + 0.00003;

  private static GtfsStop createLocation(
      int csvRowNumber,
      String stopId,
      GtfsLocationType locationType,
      @Nullable Double stopLat,
      @Nullable Double stopLon,
      @Nullable String parentStation) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStopId(stopId)
        .setLocationType(locationType)
        .setStopLat(stopLat)
        .setStopLon(stopLon)
        .setParentStation(parentStation)
        .build();
  }

  private static List<ValidationNotice> generateNotices(List<GtfsStop> stops) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new StopStationDistanceValidator(GtfsStopTableContainer.forEntities(stops, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void stationsTooClose_generatesNotice() {
    GtfsStop station1 =
        createLocation(2, "station1", GtfsLocationType.STATION, BASE_LAT, BASE_LON, null);
    GtfsStop station2 =
        createLocation(3, "station2", GtfsLocationType.STATION, CLOSE_LAT, BASE_LON, null);

    assertThat(generateNotices(ImmutableList.of(station1, station2)))
        .containsExactly(new StationsTooCloseNotice(station1, station2));
  }

  @Test
  public void stationsFarEnough_noNotice() {
    GtfsStop station1 =
        createLocation(2, "station1", GtfsLocationType.STATION, BASE_LAT, BASE_LON, null);
    GtfsStop station2 =
        createLocation(3, "station2", GtfsLocationType.STATION, FAR_LAT, BASE_LON, null);

    assertThat(generateNotices(ImmutableList.of(station1, station2))).isEmpty();
  }

  @Test
  public void stopsTooClose_generatesNotice() {
    GtfsStop stop1 = createLocation(2, "stop1", GtfsLocationType.STOP, BASE_LAT, BASE_LON, null);
    GtfsStop stop2 = createLocation(3, "stop2", GtfsLocationType.STOP, CLOSE_LAT, BASE_LON, null);

    assertThat(generateNotices(ImmutableList.of(stop1, stop2)))
        .containsExactly(new StopsTooCloseNotice(stop1, stop2));
  }

  @Test
  public void stopsFarEnough_noNotice() {
    GtfsStop stop1 = createLocation(2, "stop1", GtfsLocationType.STOP, BASE_LAT, BASE_LON, null);
    GtfsStop stop2 = createLocation(3, "stop2", GtfsLocationType.STOP, FAR_LAT, BASE_LON, null);

    assertThat(generateNotices(ImmutableList.of(stop1, stop2))).isEmpty();
  }

  @Test
  public void stopTooCloseToNonParentStation_withoutParentStation_generatesNotice() {
    GtfsStop stop = createLocation(2, "stop1", GtfsLocationType.STOP, BASE_LAT, BASE_LON, null);
    GtfsStop station =
        createLocation(3, "station1", GtfsLocationType.STATION, CLOSE_LAT, BASE_LON, null);

    assertThat(generateNotices(ImmutableList.of(stop, station)))
        .containsExactly(new StopTooCloseToNonParentStationNotice(stop, station));
  }

  @Test
  public void stopTooCloseToNonParentStation_withDifferentParentStation_generatesNotice() {
    GtfsStop station =
        createLocation(2, "station1", GtfsLocationType.STATION, BASE_LAT, BASE_LON, null);
    GtfsStop stop =
        createLocation(3, "stop1", GtfsLocationType.STOP, CLOSE_LAT, BASE_LON, "other_station");

    assertThat(generateNotices(ImmutableList.of(station, stop)))
        .containsExactly(new StopTooCloseToNonParentStationNotice(stop, station));
  }

  @Test
  public void stopCloseToOwnParentStation_noNotice() {
    GtfsStop station =
        createLocation(2, "station1", GtfsLocationType.STATION, BASE_LAT, BASE_LON, null);
    GtfsStop stop =
        createLocation(3, "stop1", GtfsLocationType.STOP, CLOSE_LAT, BASE_LON, "station1");

    assertThat(generateNotices(ImmutableList.of(station, stop))).isEmpty();
  }

  @Test
  public void stopFarFromNonParentStation_noNotice() {
    GtfsStop station =
        createLocation(2, "station1", GtfsLocationType.STATION, BASE_LAT, BASE_LON, null);
    GtfsStop stop = createLocation(3, "stop1", GtfsLocationType.STOP, FAR_LAT, BASE_LON, null);

    assertThat(generateNotices(ImmutableList.of(station, stop))).isEmpty();
  }

  @Test
  public void missingCoordinates_noNotice() {
    GtfsStop stop1 = createLocation(2, "stop1", GtfsLocationType.STOP, null, BASE_LON, null);
    GtfsStop stop2 = createLocation(3, "stop2", GtfsLocationType.STOP, null, null, null);
    GtfsStop stop3 = createLocation(4, "stop3", GtfsLocationType.STOP, BASE_LAT, BASE_LON, null);

    assertThat(generateNotices(ImmutableList.of(stop1, stop2, stop3))).isEmpty();
  }

  @Test
  public void pointsNearOrigin_noNotice() {
    GtfsStop originStop1 = createLocation(2, "stop1", GtfsLocationType.STOP, 0.0, 0.0, null);
    GtfsStop originStop2 = createLocation(3, "stop2", GtfsLocationType.STOP, 0.0, 0.0, null);
    GtfsStop nearOriginStation1 =
        createLocation(4, "station1", GtfsLocationType.STATION, 0.5, -0.5, null);
    GtfsStop nearOriginStation2 =
        createLocation(5, "station2", GtfsLocationType.STATION, 0.50001, -0.5, null);
    GtfsStop boundaryStop = createLocation(6, "stop3", GtfsLocationType.STOP, 1.0, -1.0, null);
    GtfsStop boundaryStation =
        createLocation(7, "station3", GtfsLocationType.STATION, 1.0, -1.0, null);

    assertThat(
            generateNotices(
                ImmutableList.of(
                    originStop1,
                    originStop2,
                    nearOriginStation1,
                    nearOriginStation2,
                    boundaryStop,
                    boundaryStation)))
        .isEmpty();
  }

  @Test
  public void pointsNearPole_noNotice() {
    GtfsStop northPoleStop1 = createLocation(2, "stop1", GtfsLocationType.STOP, 89.5, 0.0, null);
    GtfsStop northPoleStop2 =
        createLocation(3, "stop2", GtfsLocationType.STOP, 89.500001, 0.0, null);
    GtfsStop boundaryStation1 =
        createLocation(4, "station1", GtfsLocationType.STATION, 89.0, 10.0, null);
    GtfsStop boundaryStation2 =
        createLocation(5, "station2", GtfsLocationType.STATION, 89.0, 10.0, null);
    GtfsStop southPoleStop1 = createLocation(6, "stop3", GtfsLocationType.STOP, -89.5, 0.0, null);
    GtfsStop southPoleStop2 =
        createLocation(7, "stop4", GtfsLocationType.STOP, -89.500001, 0.0, null);

    assertThat(
            generateNotices(
                ImmutableList.of(
                    northPoleStop1,
                    northPoleStop2,
                    boundaryStation1,
                    boundaryStation2,
                    southPoleStop1,
                    southPoleStop2)))
        .isEmpty();
  }

  @Test
  public void onlyOneCoordinateWithinOriginBound_generatesNotice() {
    GtfsStop equatorStop1 = createLocation(2, "eq1", GtfsLocationType.STOP, 0.0, BASE_LON, null);
    GtfsStop equatorStop2 =
        createLocation(3, "eq2", GtfsLocationType.STOP, 0.00001, BASE_LON, null);
    GtfsStop meridianStop1 = createLocation(4, "pm1", GtfsLocationType.STOP, BASE_LAT, 0.0, null);
    GtfsStop meridianStop2 = createLocation(5, "pm2", GtfsLocationType.STOP, CLOSE_LAT, 0.0, null);

    assertThat(
            generateNotices(
                ImmutableList.of(equatorStop1, equatorStop2, meridianStop1, meridianStop2)))
        .containsExactly(
            new StopsTooCloseNotice(equatorStop1, equatorStop2),
            new StopsTooCloseNotice(meridianStop1, meridianStop2));
  }

  @Test
  public void otherLocationTypesClose_noNotice() {
    GtfsStop entrance =
        createLocation(2, "entrance1", GtfsLocationType.ENTRANCE, BASE_LAT, BASE_LON, "station1");
    GtfsStop genericNode =
        createLocation(3, "node1", GtfsLocationType.GENERIC_NODE, CLOSE_LAT, BASE_LON, "station1");
    GtfsStop boardingArea =
        createLocation(4, "boarding1", GtfsLocationType.BOARDING_AREA, BASE_LAT, BASE_LON, "stop1");
    GtfsStop stop = createLocation(5, "stop1", GtfsLocationType.STOP, BASE_LAT, BASE_LON, null);

    assertThat(generateNotices(ImmutableList.of(entrance, genericNode, boardingArea, stop)))
        .isEmpty();
  }

  @Test
  public void largeStopTable_usesSpatialIndexAndFindsClosePairs() {
    List<GtfsStop> stops = new ArrayList<>();
    for (int i = 0; i < 200; i++) {
      stops.add(
          createLocation(
              i + 2,
              "stop_" + i,
              GtfsLocationType.STOP,
              BASE_LAT + i * 0.001,
              BASE_LON + i * 0.001,
              null));
    }
    GtfsStop closeStop =
        createLocation(202, "close_stop", GtfsLocationType.STOP, CLOSE_LAT, BASE_LON, null);
    stops.add(closeStop);

    assertThat(generateNotices(stops))
        .containsExactly(new StopsTooCloseNotice(stops.get(0), closeStop));
  }

  @Test
  public void multipleNoticesInSameFeed_emitsExpectedNotices() {
    GtfsStop station1 =
        createLocation(2, "station1", GtfsLocationType.STATION, BASE_LAT, BASE_LON, null);
    GtfsStop station2 =
        createLocation(3, "station2", GtfsLocationType.STATION, CLOSE_LAT, BASE_LON, null);
    GtfsStop stop1 =
        createLocation(4, "stop1", GtfsLocationType.STOP, BASE_LAT, BASE_LON, "station1");

    assertThat(generateNotices(ImmutableList.of(station1, station2, stop1)))
        .containsExactly(
            new StationsTooCloseNotice(station1, station2),
            new StopTooCloseToNonParentStationNotice(stop1, station2));
  }
}
