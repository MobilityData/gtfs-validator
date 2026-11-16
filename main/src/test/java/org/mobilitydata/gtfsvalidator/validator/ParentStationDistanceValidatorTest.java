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
import org.mobilitydata.gtfsvalidator.util.S2Earth;
import org.mobilitydata.gtfsvalidator.validator.ParentStationDistanceValidator.ChildStopFarFromParentStationNotice;
import org.mobilitydata.gtfsvalidator.validator.ParentStationDistanceValidator.ChildStopTooFarFromParentStationNotice;

@RunWith(JUnit4.class)
public class ParentStationDistanceValidatorTest {

  private static final double BASE_LAT = 45.5017;
  private static final double BASE_LON = -73.5673;
  // ~556m north of BASE_LAT (<= 1km).
  private static final double WITHIN_1KM_LAT = BASE_LAT + 0.005;
  // ~2.22km north of BASE_LAT (> 1km and <= 10km).
  private static final double OVER_1KM_LAT = BASE_LAT + 0.02;
  // ~13.34km north of BASE_LAT (> 10km).
  private static final double OVER_10KM_LAT = BASE_LAT + 0.12;

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
    new ParentStationDistanceValidator(GtfsStopTableContainer.forEntities(stops, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void childStopWithin1Km_noNotice() {
    GtfsStop station =
        createLocation(2, "station1", GtfsLocationType.STATION, BASE_LAT, BASE_LON, null);
    GtfsStop childStop =
        createLocation(3, "stop1", GtfsLocationType.STOP, WITHIN_1KM_LAT, BASE_LON, "station1");

    assertThat(generateNotices(ImmutableList.of(station, childStop))).isEmpty();
  }

  @Test
  public void childStopBetween1KmAnd10Km_generatesFarInfoNotice() {
    GtfsStop station =
        createLocation(2, "station1", GtfsLocationType.STATION, BASE_LAT, BASE_LON, null);
    GtfsStop childStop =
        createLocation(3, "stop1", GtfsLocationType.STOP, OVER_1KM_LAT, BASE_LON, "station1");
    double expectedDistance =
        S2Earth.getDistanceMeters(childStop.stopLatLon(), station.stopLatLon());

    assertThat(generateNotices(ImmutableList.of(station, childStop)))
        .containsExactly(
            new ChildStopFarFromParentStationNotice(childStop, station, expectedDistance));
  }

  @Test
  public void childStopOver10Km_generatesTooFarWarningNoticeOnly() {
    GtfsStop station =
        createLocation(2, "station1", GtfsLocationType.STATION, BASE_LAT, BASE_LON, null);
    GtfsStop childStop =
        createLocation(3, "stop1", GtfsLocationType.STOP, OVER_10KM_LAT, BASE_LON, "station1");
    double expectedDistance =
        S2Earth.getDistanceMeters(childStop.stopLatLon(), station.stopLatLon());

    assertThat(generateNotices(ImmutableList.of(station, childStop)))
        .containsExactly(
            new ChildStopTooFarFromParentStationNotice(childStop, station, expectedDistance));
  }

  @Test
  public void thresholdBoundaries_respectStrictGreaterThanComparison() {
    double lat1KmBelow =
        Math.nextDown(
            Math.toDegrees(
                ParentStationDistanceValidator.FAR_THRESHOLD_METERS / S2Earth.getRadiusMeters()));
    double lat1KmAbove =
        Math.nextUp(
            Math.toDegrees(
                ParentStationDistanceValidator.FAR_THRESHOLD_METERS / S2Earth.getRadiusMeters()));
    double lat10KmBelow =
        Math.nextDown(
            Math.toDegrees(
                ParentStationDistanceValidator.TOO_FAR_THRESHOLD_METERS
                    / S2Earth.getRadiusMeters()));
    double lat10KmAbove =
        Math.nextUp(
            Math.toDegrees(
                ParentStationDistanceValidator.TOO_FAR_THRESHOLD_METERS
                    / S2Earth.getRadiusMeters()));

    GtfsStop station = createLocation(2, "station1", GtfsLocationType.STATION, 0.0, 0.0, null);
    GtfsStop stopAtOrBelow1Km =
        createLocation(3, "stop_1km_le", GtfsLocationType.STOP, lat1KmBelow, 0.0, "station1");
    GtfsStop stopJustAbove1Km =
        createLocation(4, "stop_1km_gt", GtfsLocationType.STOP, lat1KmAbove, 0.0, "station1");
    GtfsStop stopAtOrBelow10Km =
        createLocation(5, "stop_10km_le", GtfsLocationType.STOP, lat10KmBelow, 0.0, "station1");
    GtfsStop stopJustAbove10Km =
        createLocation(6, "stop_10km_gt", GtfsLocationType.STOP, lat10KmAbove, 0.0, "station1");

    assertThat(
            generateNotices(
                ImmutableList.of(
                    station,
                    stopAtOrBelow1Km,
                    stopJustAbove1Km,
                    stopAtOrBelow10Km,
                    stopJustAbove10Km)))
        .containsExactly(
            new ChildStopFarFromParentStationNotice(
                stopJustAbove1Km,
                station,
                S2Earth.getDistanceMeters(stopJustAbove1Km.stopLatLon(), station.stopLatLon())),
            new ChildStopFarFromParentStationNotice(
                stopAtOrBelow10Km,
                station,
                S2Earth.getDistanceMeters(stopAtOrBelow10Km.stopLatLon(), station.stopLatLon())),
            new ChildStopTooFarFromParentStationNotice(
                stopJustAbove10Km,
                station,
                S2Earth.getDistanceMeters(stopJustAbove10Km.stopLatLon(), station.stopLatLon())));
  }

  @Test
  public void missingParentOrCoordinates_noNotice() {
    GtfsStop station =
        createLocation(2, "station1", GtfsLocationType.STATION, BASE_LAT, BASE_LON, null);
    GtfsStop stationWithoutCoords =
        createLocation(3, "station2", GtfsLocationType.STATION, null, null, null);
    GtfsStop stopWithoutParent =
        createLocation(4, "stop1", GtfsLocationType.STOP, OVER_10KM_LAT, BASE_LON, null);
    GtfsStop stopWithUnknownParent =
        createLocation(5, "stop2", GtfsLocationType.STOP, OVER_10KM_LAT, BASE_LON, "missing");
    GtfsStop stopWithoutCoords =
        createLocation(6, "stop3", GtfsLocationType.STOP, null, BASE_LON, "station1");
    GtfsStop stopWithCoordlessParent =
        createLocation(7, "stop4", GtfsLocationType.STOP, OVER_10KM_LAT, BASE_LON, "station2");

    assertThat(
            generateNotices(
                ImmutableList.of(
                    station,
                    stationWithoutCoords,
                    stopWithoutParent,
                    stopWithUnknownParent,
                    stopWithoutCoords,
                    stopWithCoordlessParent)))
        .isEmpty();
  }

  @Test
  public void nonStopChildOrNonStationParent_noNotice() {
    GtfsStop station =
        createLocation(2, "station1", GtfsLocationType.STATION, BASE_LAT, BASE_LON, null);
    GtfsStop nonStationParent =
        createLocation(3, "parent_stop", GtfsLocationType.STOP, BASE_LAT, BASE_LON, null);
    GtfsStop entrance =
        createLocation(
            4, "entrance1", GtfsLocationType.ENTRANCE, OVER_10KM_LAT, BASE_LON, "station1");
    GtfsStop genericNode =
        createLocation(
            5, "node1", GtfsLocationType.GENERIC_NODE, OVER_10KM_LAT, BASE_LON, "station1");
    GtfsStop boardingArea =
        createLocation(
            6, "boarding1", GtfsLocationType.BOARDING_AREA, OVER_10KM_LAT, BASE_LON, "parent_stop");
    GtfsStop stopWithNonStationParent =
        createLocation(7, "stop1", GtfsLocationType.STOP, OVER_10KM_LAT, BASE_LON, "parent_stop");

    assertThat(
            generateNotices(
                ImmutableList.of(
                    station,
                    nonStationParent,
                    entrance,
                    genericNode,
                    boardingArea,
                    stopWithNonStationParent)))
        .isEmpty();
  }
}
