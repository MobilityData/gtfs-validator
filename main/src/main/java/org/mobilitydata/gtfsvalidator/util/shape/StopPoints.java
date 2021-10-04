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

import static org.mobilitydata.gtfsvalidator.util.S2Earth.getDistanceMeters;

import com.google.common.collect.Iterables;
import com.google.common.geometry.S2Point;
import java.util.ArrayList;
import java.util.List;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteType;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.util.StopUtil;

/**
 * Models a sequence of stop locations, as determined by a GTFS trip. Like shape points, each stop
 * point potentially includes an arbitrary "user distance" value indicating the stops location on
 * its shape.
 */
public class StopPoints {

  private final List<StopPoint> points;

  public StopPoints(List<StopPoint> points) {
    this.points = points;
  }

  /**
   * Size of a station.
   *
   * <p>It affects thresholds used for stop matching: a larger station has a larger threshold.
   */
  public enum StationSize {
    SMALL,
    LARGE
  };

  public static StopPoints fromStopTimes(
      List<GtfsStopTime> stopTimes, GtfsStopTableContainer stopTable, StationSize stationSize) {
    List<StopPoint> points = new ArrayList<>(stopTimes.size());
    for (GtfsStopTime stopTime : stopTimes) {
      // Agency shapes often do not extend till the very end of the track, especially for train
      // stations. Although this is a data issue that agencies should fix, we would like to be more
      // tolerant and not drop such shapes completely.
      boolean firstOrLastStop = points.isEmpty() || points.size() == stopTimes.size() - 1;
      points.add(
          new StopPoint(
              StopUtil.getStopOrParentLatLng(stopTable, stopTime.stopId()).toPoint(),
              stopTime.shapeDistTraveled(),
              stopTime,
              stationSize.equals(StationSize.LARGE) && firstOrLastStop));
    }
    return new StopPoints(points);
  }

  public static StationSize routeTypeToStationSize(GtfsRouteType routeType) {
    return routeType.equals(GtfsRouteType.RAIL) ? StationSize.LARGE : StationSize.SMALL;
  }

  public List<StopPoint> getPoints() {
    return points;
  }

  public StopPoint get(int i) {
    return points.get(i);
  }

  public int size() {
    return points.size();
  }

  public boolean hasUserDistance() {
    return !points.isEmpty() && Iterables.getLast(points).hasUserDistance();
  }

  public boolean isEmpty() {
    return points.isEmpty();
  }

  /** A single stop location in the sequence. */
  public static class StopPoint {

    /** The location of the stop. */
    private final S2Point location;
    /** The user distance along the shape for the stop, if specified. */
    private final double userDistance;
    /** CSV data table row for the stop_times.txt entry for this stop. */
    private final GtfsStopTime stopTime;
    /**
     * True if this station is treated as large, so a higher distance threshold is used for
     * matching.
     */
    private final boolean isLargeStation;

    public StopPoint(
        S2Point location, double userDistance, GtfsStopTime stopTime, boolean isLargeStation) {
      this.location = location;
      this.userDistance = userDistance;
      this.stopTime = stopTime;
      this.isLargeStation = isLargeStation;
    }

    boolean hasUserDistance() {
      return userDistance > 0.0;
    }

    public S2Point getLocation() {
      return location;
    }

    public double getUserDistance() {
      return userDistance;
    }

    public GtfsStopTime getStopTime() {
      return stopTime;
    }

    public boolean isLargeStation() {
      return isLargeStation;
    }

    @Override
    public String toString() {
      return "StopPoint{"
          + "location="
          + location
          + ", userDistance="
          + userDistance
          + ", stopTime=("
          + stopTime.tripId()
          + ","
          + stopTime.stopSequence()
          + ","
          + stopTime.stopId()
          + ")"
          + ", isLargeStation="
          + isLargeStation
          + '}';
    }

    public boolean approxEquals(StopPoint that, double maxError) {
      return getDistanceMeters(this.location, that.location) < maxError
          && Math.abs(this.userDistance - that.userDistance) < maxError
          && this.stopTime == that.stopTime
          && this.isLargeStation == that.isLargeStation;
    }
  }
}
