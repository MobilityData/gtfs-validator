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
import org.mobilitydata.gtfsvalidator.table.GtfsLevel;
import org.mobilitydata.gtfsvalidator.table.GtfsLevelTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.TableStatus;
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
    return createLocation(
        csvRowNumber, stopId, locationType, stopLat, stopLon, parentStation, null);
  }

  private static GtfsStop createLocation(
      int csvRowNumber,
      String stopId,
      GtfsLocationType locationType,
      @Nullable Double stopLat,
      @Nullable Double stopLon,
      @Nullable String parentStation,
      @Nullable String levelId) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStopId(stopId)
        .setLocationType(locationType)
        .setStopLat(stopLat)
        .setStopLon(stopLon)
        .setParentStation(parentStation)
        .setLevelId(levelId)
        .build();
  }

  private static GtfsLevel createLevel(int csvRowNumber, String levelId, double levelIndex) {
    return new GtfsLevel.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setLevelId(levelId)
        .setLevelIndex(levelIndex)
        .build();
  }

  private static List<ValidationNotice> generateNotices(List<GtfsStop> stops) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new StopStationDistanceValidator(
            GtfsStopTableContainer.forEntities(stops, noticeContainer),
            GtfsLevelTableContainer.forStatus(TableStatus.MISSING_FILE))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsStop> stops, List<GtfsLevel> levels) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new StopStationDistanceValidator(
            GtfsStopTableContainer.forEntities(stops, noticeContainer),
            GtfsLevelTableContainer.forEntities(levels, noticeContainer))
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

  @Test
  public void noticesIncludeCsvRowNumbers() {
    GtfsStop station1 =
        createLocation(10, "station1", GtfsLocationType.STATION, BASE_LAT, BASE_LON, null);
    GtfsStop station2 =
        createLocation(20, "station2", GtfsLocationType.STATION, CLOSE_LAT, BASE_LON, null);
    GtfsStop stop1 = createLocation(30, "stop1", GtfsLocationType.STOP, FAR_LAT, BASE_LON, null);
    GtfsStop stop2 =
        createLocation(40, "stop2", GtfsLocationType.STOP, FAR_LAT + 0.00001, BASE_LON, null);
    GtfsStop nonChildStop =
        createLocation(50, "stop3", GtfsLocationType.STOP, BASE_LAT, BASE_LON, "other_station");

    String stationsJson = new StationsTooCloseNotice(station1, station2).toString();
    assertThat(stationsJson).contains("\"csvRowNumber1\":10");
    assertThat(stationsJson).contains("\"csvRowNumber2\":20");

    String stopsJson = new StopsTooCloseNotice(stop1, stop2).toString();
    assertThat(stopsJson).contains("\"csvRowNumber1\":30");
    assertThat(stopsJson).contains("\"csvRowNumber2\":40");

    String stopStationJson =
        new StopTooCloseToNonParentStationNotice(nonChildStop, station1).toString();
    assertThat(stopStationJson).contains("\"stopCsvRowNumber\":50");
    assertThat(stopStationJson).contains("\"stationCsvRowNumber\":10");
  }

  @Test
  public void levelsFileMissing_withDifferentLevelIds_generatesNotice() {
    GtfsStop stop1 =
        createLocation(2, "stop1", GtfsLocationType.STOP, BASE_LAT, BASE_LON, "station1", "level0");
    GtfsStop stop2 =
        createLocation(
            3, "stop2", GtfsLocationType.STOP, CLOSE_LAT, BASE_LON, "station1", "level_neg2");

    assertThat(generateNotices(ImmutableList.of(stop1, stop2)))
        .containsExactly(new StopsTooCloseNotice(stop1, stop2));
  }

  @Test
  public void sameStation_differentLevels_noNotice() {
    ImmutableList<GtfsLevel> levels =
        ImmutableList.of(
            createLevel(2, "ground", 0.0),
            createLevel(3, "sub1", -1.0),
            createLevel(4, "sub2", -2.0),
            createLevel(5, "elev1", 1.0),
            createLevel(6, "elev2", 2.0));
    GtfsStop streetStop =
        createLocation(
            2, "stop_ground", GtfsLocationType.STOP, BASE_LAT, BASE_LON, "station1", "ground");
    GtfsStop sub1Stop =
        createLocation(
            3, "stop_sub1", GtfsLocationType.STOP, CLOSE_LAT, BASE_LON, "station1", "sub1");
    GtfsStop sub2Stop =
        createLocation(
            4, "stop_sub2", GtfsLocationType.STOP, BASE_LAT, BASE_LON, "station1", "sub2");
    GtfsStop elev1Stop =
        createLocation(
            5, "stop_elev1", GtfsLocationType.STOP, BASE_LAT, BASE_LON, "station1", "elev1");
    GtfsStop elev2Stop =
        createLocation(
            6, "stop_elev2", GtfsLocationType.STOP, CLOSE_LAT, BASE_LON, "station1", "elev2");

    assertThat(
            generateNotices(
                ImmutableList.of(streetStop, sub1Stop, sub2Stop, elev1Stop, elev2Stop), levels))
        .isEmpty();
  }

  @Test
  public void sameStation_sameLevel_generatesNotice() {
    ImmutableList<GtfsLevel> levels =
        ImmutableList.of(createLevel(2, "sub1_a", -1.0), createLevel(3, "sub1_b", -1.0));
    GtfsStop stop1 =
        createLocation(2, "stop1", GtfsLocationType.STOP, BASE_LAT, BASE_LON, "station1", "sub1_a");
    GtfsStop stop2 =
        createLocation(
            3, "stop2", GtfsLocationType.STOP, CLOSE_LAT, BASE_LON, "station1", "sub1_b");

    assertThat(generateNotices(ImmutableList.of(stop1, stop2), levels))
        .containsExactly(new StopsTooCloseNotice(stop1, stop2));
  }

  @Test
  public void differentStations_nonNegativeAndNegativeLevels_noNotice() {
    ImmutableList<GtfsLevel> levels =
        ImmutableList.of(
            createLevel(2, "ground", 0.0),
            createLevel(3, "elev1", 1.0),
            createLevel(4, "sub2", -2.0));
    GtfsStop groundStopStationA =
        createLocation(2, "stop1", GtfsLocationType.STOP, BASE_LAT, BASE_LON, "stationA", "ground");
    GtfsStop undergroundStopStationB =
        createLocation(3, "stop2", GtfsLocationType.STOP, CLOSE_LAT, BASE_LON, "stationB", "sub2");
    GtfsStop elevatedStandaloneStop =
        createLocation(4, "stop3", GtfsLocationType.STOP, FAR_LAT, BASE_LON, null, "elev1");
    GtfsStop undergroundStandaloneStop =
        createLocation(
            5, "stop4", GtfsLocationType.STOP, FAR_LAT + 0.00001, BASE_LON, null, "sub2");

    assertThat(
            generateNotices(
                ImmutableList.of(
                    groundStopStationA,
                    undergroundStopStationB,
                    elevatedStandaloneStop,
                    undergroundStandaloneStop),
                levels))
        .isEmpty();
  }

  @Test
  public void differentStations_nonPositiveAndPositiveLevels_noNotice() {
    ImmutableList<GtfsLevel> levels =
        ImmutableList.of(createLevel(2, "ground", 0.0), createLevel(3, "elev1", 1.0));
    GtfsStop groundStopStationA =
        createLocation(2, "stop1", GtfsLocationType.STOP, BASE_LAT, BASE_LON, "stationA", "ground");
    GtfsStop elevatedStopStationB =
        createLocation(3, "stop2", GtfsLocationType.STOP, CLOSE_LAT, BASE_LON, "stationB", "elev1");

    assertThat(generateNotices(ImmutableList.of(groundStopStationA, elevatedStopStationB), levels))
        .isEmpty();
  }

  @Test
  public void differentStations_bothUndergroundOrBothOverground_generatesNotice() {
    ImmutableList<GtfsLevel> levels =
        ImmutableList.of(
            createLevel(2, "sub1", -1.0),
            createLevel(3, "sub2", -2.0),
            createLevel(4, "ground", 0.0),
            createLevel(5, "elev1", 1.0),
            createLevel(6, "elev2", 2.0));
    // Different stations, both strictly negative (-1 and -2).
    GtfsStop subStopA =
        createLocation(2, "subA", GtfsLocationType.STOP, BASE_LAT, BASE_LON, "stationA", "sub1");
    GtfsStop subStopB =
        createLocation(3, "subB", GtfsLocationType.STOP, CLOSE_LAT, BASE_LON, "stationB", "sub2");
    // Standalone stops (no parent_station), both strictly negative (-1 and -2): must NOT be treated
    // as belonging to the same station.
    GtfsStop standaloneSub1 =
        createLocation(
            4, "standaloneSub1", GtfsLocationType.STOP, BASE_LAT + 0.001, BASE_LON, null, "sub1");
    GtfsStop standaloneSub2 =
        createLocation(
            5, "standaloneSub2", GtfsLocationType.STOP, BASE_LAT + 0.00101, BASE_LON, null, "sub2");
    // Different stations, both at ground level (0 and 0).
    GtfsStop groundStopA =
        createLocation(
            6, "groundA", GtfsLocationType.STOP, BASE_LAT + 0.002, BASE_LON, "stationA", "ground");
    GtfsStop groundStopB =
        createLocation(
            7,
            "groundB",
            GtfsLocationType.STOP,
            BASE_LAT + 0.00201,
            BASE_LON,
            "stationB",
            "ground");
    // Different stations, both strictly positive (1 and 2).
    GtfsStop elevStopA =
        createLocation(
            8, "elevA", GtfsLocationType.STOP, BASE_LAT + 0.003, BASE_LON, "stationA", "elev1");
    GtfsStop elevStopB =
        createLocation(
            9, "elevB", GtfsLocationType.STOP, BASE_LAT + 0.00301, BASE_LON, "stationB", "elev2");

    assertThat(
            generateNotices(
                ImmutableList.of(
                    subStopA,
                    subStopB,
                    standaloneSub1,
                    standaloneSub2,
                    groundStopA,
                    groundStopB,
                    elevStopA,
                    elevStopB),
                levels))
        .containsExactly(
            new StopsTooCloseNotice(subStopA, subStopB),
            new StopsTooCloseNotice(standaloneSub1, standaloneSub2),
            new StopsTooCloseNotice(groundStopA, groundStopB),
            new StopsTooCloseNotice(elevStopA, elevStopB));
  }

  @Test
  public void stackedStationsAndStops_separatedByLevels_noNotice() {
    ImmutableList<GtfsLevel> levels =
        ImmutableList.of(
            createLevel(2, "ground", 0.0),
            createLevel(3, "sub1", -1.0),
            createLevel(4, "sub2", -2.0));
    GtfsStop overgroundStation =
        createLocation(
            2, "station_tgv", GtfsLocationType.STATION, BASE_LAT, BASE_LON, null, "ground");
    GtfsStop undergroundStation =
        createLocation(
            3, "station_metro", GtfsLocationType.STATION, CLOSE_LAT, BASE_LON, null, "sub1");
    GtfsStop metroStop =
        createLocation(
            4, "stop_metro", GtfsLocationType.STOP, BASE_LAT, BASE_LON, "station_metro", "sub2");

    assertThat(
            generateNotices(
                ImmutableList.of(overgroundStation, undergroundStation, metroStop), levels))
        .isEmpty();
  }

  @Test
  public void missingOrUnresolvedLevelId_generatesNotice() {
    ImmutableList<GtfsLevel> levels =
        ImmutableList.of(createLevel(2, "ground", 0.0), createLevel(3, "sub2", -2.0));
    GtfsStop stopWithLevel =
        createLocation(2, "stop1", GtfsLocationType.STOP, BASE_LAT, BASE_LON, "station1", "ground");
    GtfsStop stopWithoutLevel =
        createLocation(3, "stop2", GtfsLocationType.STOP, CLOSE_LAT, BASE_LON, "station1", null);
    GtfsStop stopWithValidLevel =
        createLocation(4, "stop3", GtfsLocationType.STOP, FAR_LAT, BASE_LON, "station1", "sub2");
    GtfsStop stopWithUnknownLevel =
        createLocation(
            5,
            "stop4",
            GtfsLocationType.STOP,
            FAR_LAT + 0.00001,
            BASE_LON,
            "station1",
            "unknown_level");

    assertThat(
            generateNotices(
                ImmutableList.of(
                    stopWithLevel, stopWithoutLevel, stopWithValidLevel, stopWithUnknownLevel),
                levels))
        .containsExactly(
            new StopsTooCloseNotice(stopWithLevel, stopWithoutLevel),
            new StopsTooCloseNotice(stopWithValidLevel, stopWithUnknownLevel));
  }
}
