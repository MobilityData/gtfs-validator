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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import com.google.common.geometry.S1Angle;
import com.google.common.geometry.S2ClosestPointQuery;
import com.google.common.geometry.S2Point;
import com.google.common.geometry.S2PointIndex;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.util.S2Earth;

/**
 * Validates that stops and stations in `stops.txt` are not closer than 2 meters to each other,
 * excluding points near the origin or poles.
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link StationsTooCloseNotice}
 *   <li>{@link StopsTooCloseNotice}
 *   <li>{@link StopTooCloseToNonParentStationNotice}
 * </ul>
 */
@GtfsValidator
public class StopStationDistanceValidator extends FileValidator {

  static final double DISTANCE_THRESHOLD_METERS = 2.0;
  private static final double QUERY_BUFFER_METERS = 2.1;
  private static final double ORIGIN_THRESHOLD_DEGREES = 1.0;
  private static final double POLE_LAT_THRESHOLD_DEGREES = 89.0;
  private static final S1Angle MAX_QUERY_DISTANCE =
      S1Angle.radians(QUERY_BUFFER_METERS / S2Earth.getRadiusMeters());

  private final GtfsStopTableContainer stopTable;

  @Inject
  StopStationDistanceValidator(GtfsStopTableContainer stopTable) {
    this.stopTable = stopTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    List<GtfsStop> locations = new ArrayList<>();
    for (GtfsStop stop : stopTable.getEntities()) {
      if (isStopOrStationWithCoordinates(stop)) {
        locations.add(stop);
      }
    }
    if (locations.size() < 2) {
      return;
    }

    S2PointIndex<Integer> index = new S2PointIndex<>();
    S2Point[] points = new S2Point[locations.size()];
    for (int i = 0; i < locations.size(); i++) {
      S2Point point = locations.get(i).stopLatLon().toPoint();
      points[i] = point;
      index.add(point, i);
    }

    S2ClosestPointQuery<Integer> query = new S2ClosestPointQuery<>(index);
    query.setMaxDistance(MAX_QUERY_DISTANCE);

    List<S2ClosestPointQuery.Result<Integer>> results = new ArrayList<>();
    List<Integer> neighborIndices = new ArrayList<>();
    for (int i = 0; i < locations.size(); i++) {
      GtfsStop loc1 = locations.get(i);
      results.clear();
      query.findClosestPoints(results, points[i]);
      neighborIndices.clear();
      for (S2ClosestPointQuery.Result<Integer> result : results) {
        int j = result.entry().data();
        if (j > i) {
          neighborIndices.add(j);
        }
      }
      Collections.sort(neighborIndices);
      for (int j : neighborIndices) {
        GtfsStop loc2 = locations.get(j);
        double distance = S2Earth.getDistanceMeters(loc1.stopLatLon(), loc2.stopLatLon());
        if (distance < DISTANCE_THRESHOLD_METERS) {
          validatePair(loc1, loc2, noticeContainer);
        }
      }
    }
  }

  private static boolean isStopOrStationWithCoordinates(GtfsStop stop) {
    return stop.hasStopLatLon()
        && !isNearOriginOrPole(stop)
        && (stop.locationType() == GtfsLocationType.STOP
            || stop.locationType() == GtfsLocationType.STATION);
  }

  // Matches the bounds used for PointNearOriginNotice and PointNearPoleNotice.
  private static boolean isNearOriginOrPole(GtfsStop stop) {
    return (Math.abs(stop.stopLat()) <= ORIGIN_THRESHOLD_DEGREES
            && Math.abs(stop.stopLon()) <= ORIGIN_THRESHOLD_DEGREES)
        || Math.abs(stop.stopLat()) >= POLE_LAT_THRESHOLD_DEGREES;
  }

  private static void validatePair(GtfsStop loc1, GtfsStop loc2, NoticeContainer noticeContainer) {
    if (loc1.locationType() == GtfsLocationType.STATION
        && loc2.locationType() == GtfsLocationType.STATION) {
      noticeContainer.addValidationNotice(new StationsTooCloseNotice(loc1, loc2));
    } else if (loc1.locationType() == GtfsLocationType.STOP
        && loc2.locationType() == GtfsLocationType.STOP) {
      noticeContainer.addValidationNotice(new StopsTooCloseNotice(loc1, loc2));
    } else {
      GtfsStop stop = loc1.locationType() == GtfsLocationType.STOP ? loc1 : loc2;
      GtfsStop station = loc1.locationType() == GtfsLocationType.STATION ? loc1 : loc2;
      if (!stop.parentStation().equals(station.stopId())) {
        noticeContainer.addValidationNotice(
            new StopTooCloseToNonParentStationNotice(stop, station));
      }
    }
  }

  /**
   * Stations are too close to each other.
   *
   * <p>They are less than 2 meters apart.
   */
  @GtfsValidationNotice(severity = WARNING, files = @FileRefs(GtfsStopSchema.class))
  static class StationsTooCloseNotice extends ValidationNotice {

    /** The `stops.stop_id` of the first station. */
    private final String stopId1;

    /** The `stops.stop_lat` of the first station. */
    private final double stopLat1;

    /** The `stops.stop_lon` of the first station. */
    private final double stopLon1;

    /** The `stops.stop_id` of the second station. */
    private final String stopId2;

    /** The `stops.stop_lat` of the second station. */
    private final double stopLat2;

    /** The `stops.stop_lon` of the second station. */
    private final double stopLon2;

    StationsTooCloseNotice(GtfsStop station1, GtfsStop station2) {
      this.stopId1 = station1.stopId();
      this.stopLat1 = station1.stopLat();
      this.stopLon1 = station1.stopLon();
      this.stopId2 = station2.stopId();
      this.stopLat2 = station2.stopLat();
      this.stopLon2 = station2.stopLon();
    }
  }

  /**
   * Stops are too close to each other.
   *
   * <p>They are less than 2 meters apart.
   */
  @GtfsValidationNotice(severity = WARNING, files = @FileRefs(GtfsStopSchema.class))
  static class StopsTooCloseNotice extends ValidationNotice {

    /** The `stops.stop_id` of the first stop. */
    private final String stopId1;

    /** The `stops.stop_lat` of the first stop. */
    private final double stopLat1;

    /** The `stops.stop_lon` of the first stop. */
    private final double stopLon1;

    /** The `stops.stop_id` of the second stop. */
    private final String stopId2;

    /** The `stops.stop_lat` of the second stop. */
    private final double stopLat2;

    /** The `stops.stop_lon` of the second stop. */
    private final double stopLon2;

    StopsTooCloseNotice(GtfsStop stop1, GtfsStop stop2) {
      this.stopId1 = stop1.stopId();
      this.stopLat1 = stop1.stopLat();
      this.stopLon1 = stop1.stopLon();
      this.stopId2 = stop2.stopId();
      this.stopLat2 = stop2.stopLat();
      this.stopLon2 = stop2.stopLon();
    }
  }

  /**
   * A stop is too close to a station that is not its parent station.
   *
   * <p>They are less than 2m apart. Potential reasons for this notice could be either a stop with
   * incorrect coordinates, or a child stop of the station that does not have `parent_station`
   * assigned.
   */
  @GtfsValidationNotice(severity = WARNING, files = @FileRefs(GtfsStopSchema.class))
  static class StopTooCloseToNonParentStationNotice extends ValidationNotice {

    /** The `stops.stop_id` of the stop. */
    private final String stopId;

    /** The `stops.stop_lat` of the stop. */
    private final double stopLat;

    /** The `stops.stop_lon` of the stop. */
    private final double stopLon;

    /** The `stops.stop_id` of the station. */
    private final String stationId;

    /** The `stops.stop_lat` of the station. */
    private final double stationLat;

    /** The `stops.stop_lon` of the station. */
    private final double stationLon;

    StopTooCloseToNonParentStationNotice(GtfsStop stop, GtfsStop station) {
      this.stopId = stop.stopId();
      this.stopLat = stop.stopLat();
      this.stopLon = stop.stopLon();
      this.stationId = station.stopId();
      this.stationLat = station.stopLat();
      this.stationLon = station.stopLon();
    }
  }
}
