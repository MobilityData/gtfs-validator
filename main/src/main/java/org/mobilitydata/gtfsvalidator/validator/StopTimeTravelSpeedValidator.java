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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.geometry.S2LatLng;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import java.util.List;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
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

/**
 * Validates that transit vehicles do not travel too fast between consecutive and between far stops.
 *
 * <p>Max allowed speed depends on route type, so trains are OK to move faster than buses.
 */
@GtfsValidator
public class StopTimeTravelSpeedValidator extends FileValidator {
  private static final HashFunction HASH_FUNCTION = Hashing.farmHashFingerprint64();

  private final GtfsRouteTableContainer routeTable;
  private final GtfsTripTableContainer tripTable;
  private final GtfsStopTimeTableContainer stopTimeTable;
  private final GtfsStopTableContainer stopTable;

  @Inject
  StopTimeTravelSpeedValidator(
      GtfsRouteTableContainer routeTable,
      GtfsTripTableContainer tripTable,
      GtfsStopTimeTableContainer stopTimeTable,
      GtfsStopTableContainer stopTable) {
    this.routeTable = routeTable;
    this.tripTable = tripTable;
    this.stopTimeTable = stopTimeTable;
    this.stopTable = stopTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    final ListMultimap<Long, List<GtfsStopTime>> tripsByHash = ArrayListMultimap.create();
    for (List<GtfsStopTime> stopTimes : Multimaps.asMap(stopTimeTable.byTripIdMap()).values()) {
      tripsByHash.put(
          tripFprint(tripTable.byTripId(stopTimes.get(0).tripId()), stopTimes), stopTimes);
    }

    for (List<List<GtfsStopTime>> trips : Multimaps.asMap(tripsByHash).values()) {
      final List<GtfsStopTime> stopTimes = trips.get(0);
      // All trips belong to the same route.
      final GtfsRoute route =
          routeTable.byRouteId(tripTable.byTripId(stopTimes.get(0).tripId()).routeId());
      final double maxSpeedKph = getMaxVehicleSpeedKph(route.routeType());
      final double[] distancesKm = findDistancesKmBetweenStops(stopTimes);

      validateConsecutiveStops(trips, distancesKm, maxSpeedKph, noticeContainer);
      validateFarStops(trips, distancesKm, maxSpeedKph, noticeContainer);
    }
  }

  /**
   * Returns an array of distances between consecutive stops, in km.
   *
   * <p>{@code distancesKm[i]} equals to distance between stops corresponding to stop times i, i +
   * 1.
   */
  private double[] findDistancesKmBetweenStops(List<GtfsStopTime> stopTimes) {
    double[] distancesKm = new double[stopTimes.size() - 1];
    S2LatLng currLatLng = getStopLatLng(stopTable, stopTimes.get(0).stopId());
    for (int i = 0; i < distancesKm.length; ++i) {
      S2LatLng nextLatLng = getStopLatLng(stopTable, stopTimes.get(i + 1).stopId());
      distancesKm[i] = S2Earth.getDistanceKm(currLatLng, nextLatLng);
      currLatLng = nextLatLng;
    }
    return distancesKm;
  }

  /**
   * Validates travel speed between far stops for all trips that belong to the same route and visit
   * the same stops at the same times.
   *
   * <p>If there is a fast travel detected, then exactly one notice is issued for each trip.
   */
  private void validateFarStops(
      List<List<GtfsStopTime>> trips,
      double[] distancesKm,
      double maxSpeedKph,
      NoticeContainer noticeContainer) {
    final List<GtfsStopTime> stopTimes = trips.get(0);

    for (int endIdx = 0; endIdx < stopTimes.size(); ++endIdx) {
      final GtfsStopTime endStopTime = stopTimes.get(endIdx);
      if (!endStopTime.hasArrivalTime()) {
        continue;
      }
      final GtfsStop endStop = stopTable.byStopId(endStopTime.stopId());
      // distanceToEndIdx stores the distance between stops endIdx and startIdx for all startIdx <
      // endIdx, computed as the sum of straight-line distances between consecutive stops.
      double distanceToEndIdx = 0;
      for (int startIdx = endIdx - 1; startIdx >= 0; --startIdx) {
        final GtfsStopTime startStopTime = stopTimes.get(startIdx);
        distanceToEndIdx += distancesKm[startIdx];
        if (!startStopTime.hasDepartureTime()) {
          continue;
        }
        final double speedKph =
            getSpeedKphBetweenStops(distanceToEndIdx, startStopTime, endStopTime);
        if (speedKph <= maxSpeedKph) {
          continue;
        }
        GtfsStop startStop = stopTable.byStopId(startStopTime.stopId());
        // Give a notice if they are too far apart.
        if (distanceToEndIdx > MAX_DISTANCE_OVER_MAX_SPEED_IN_KMS) {
          // Issue one notice per each trip.
          for (List<GtfsStopTime> stopsOnTrip : trips) {
            noticeContainer.addValidationNotice(
                new FastTravelBetweenFarStopsNotice(
                    tripTable.byTripId(stopsOnTrip.get(0).tripId()),
                    stopsOnTrip.get(startIdx),
                    startStop,
                    stopsOnTrip.get(endIdx),
                    endStop,
                    speedKph,
                    distanceToEndIdx));
          }
          // Report at most once for each trip.
          return;
        }
      }
    }
  }

  /**
   * Validates travel speed between consecutive stops for all trips that belong to the same route
   * and visit the same stops at the same times.
   *
   * <p>If there is a fast travel detected, then a separate notice is issued for each trip.
   */
  private void validateConsecutiveStops(
      List<List<GtfsStopTime>> trips,
      double[] distancesKm,
      double maxSpeedKph,
      NoticeContainer noticeContainer) {
    final List<GtfsStopTime> stopTimes = trips.get(0);

    for (int i = 0; i < distancesKm.length; ++i) {
      final GtfsStopTime stopTime1 = stopTimes.get(i);
      final GtfsStopTime stopTime2 = stopTimes.get(i + 1);
      if (!(stopTime1.hasDepartureTime() && stopTime2.hasArrivalTime())) {
        continue;
      }
      final double distanceKm = distancesKm[i];
      final double speedKph = getSpeedKphBetweenStops(distanceKm, stopTime1, stopTime2);
      if (speedKph <= maxSpeedKph) {
        continue;
      }
      final GtfsStop stop1 = stopTable.byStopId(stopTime1.stopId());
      final GtfsStop stop2 = stopTable.byStopId(stopTime2.stopId());
      // Issue one notice per each trip.
      for (List<GtfsStopTime> stopsOnTrip : trips) {
        noticeContainer.addValidationNotice(
            new FastTravelBetweenConsecutiveStopsNotice(
                tripTable.byTripId(stopsOnTrip.get(0).tripId()),
                stopsOnTrip.get(i),
                stop1,
                stopsOnTrip.get(i + 1),
                stop2,
                speedKph,
                distanceKm));
      }
    }
  }

  @VisibleForTesting
  static double getSpeedKphBetweenStops(
      double distanceKm, GtfsStopTime stopTime1, GtfsStopTime stopTime2) {
    return distanceKm
        * NUM_SECONDS_PER_HOUR
        / getTimeBetweenStops(
            stopTime2.arrivalTime().getSecondsSinceMidnight(),
            stopTime1.departureTime().getSecondsSinceMidnight());
  }

  private static int getTimeBetweenStops(int arrivalTime, int departureTime) {
    int timeBetweenStops = arrivalTime - departureTime;
    if (timeBetweenStops <= 0) {
      // Avoid travel back in time and division by zero.
      return NUM_SECONDS_PER_MINUTE;
    }
    if (arrivalTime % NUM_SECONDS_PER_MINUTE == 0 && departureTime % NUM_SECONDS_PER_MINUTE == 0) {
      // Many scheduling systems only output times at the minute resolution,
      // which means there can be up to 30 seconds of error in the time.  If
      // we detect minute-resolution times, we add an extra minute to times as
      // an error buffer.
      timeBetweenStops += NUM_SECONDS_PER_MINUTE;
    }
    return timeBetweenStops;
  }

  /**
   * The maximum distance between any two stops for which it is allowed to go over the speed limit.
   */
  private static final double MAX_DISTANCE_OVER_MAX_SPEED_IN_KMS = 10.0;

  private static final int NUM_SECONDS_PER_MINUTE = 60;
  private static final int NUM_SECONDS_PER_HOUR = 3600;

  /**
   * Returns a fingerprint of all trip data that is relevant for validation of far stops: route id,
   * stop ids and arrival and departure times.
   */
  private static long tripFprint(GtfsTrip trip, List<GtfsStopTime> stopTimes) {
    Hasher hasher =
        HASH_FUNCTION
            .newHasher()
            .putInt(trip.routeId().length())
            .putUnencodedChars(trip.routeId())
            .putInt(stopTimes.size());
    for (GtfsStopTime stopTime : stopTimes) {
      hasher
          .putInt(stopTime.stopId().length())
          .putUnencodedChars(stopTime.stopId())
          .putInt(stopTime.arrivalTime().getSecondsSinceMidnight())
          .putInt(stopTime.departureTime().getSecondsSinceMidnight());
    }
    return hasher.hash().asLong();
  }

  /**
   * Returns coordinates of the stop. If they are missing, coordinates of the parent are used.
   *
   * <p>Returns (0, 0) if no coordinates are found in parent chain. That case is reported in another
   * validator.
   */
  static S2LatLng getStopLatLng(GtfsStopTableContainer stopTable, String stopId) {
    for (; ; ) {
      GtfsStop stop = stopTable.byStopId(stopId);
      if (stop.hasStopLatLon()) {
        return stop.stopLatLon();
      }
      if (stop.hasParentStation()) {
        stopId = stop.parentStation();
      } else {
        return S2LatLng.CENTER;
      }
    }
  }

  /** Returns a speed threshold (km/h) for a given vehicle type. */
  private static double getMaxVehicleSpeedKph(GtfsRouteType routeType) {
    switch (routeType) {
      case LIGHT_RAIL:
        // The Houston METRORail can reach speeds of 100 km/h.
        return 100;
      case RAIL:
        // A maglev bullet train can reach speeds of 500 km/h.
        return 500;
      case SUBWAY:
      case MONORAIL:
      case BUS:
      case TROLLEYBUS:
        return 150;
      case FERRY:
        return 80;
      default:
        return 50;
    }
  }

  /** Describes a trip where the transit vehicle moves too fast between two consecutive stops. */
  static class FastTravelBetweenConsecutiveStopsNotice extends ValidationNotice {
    private final long tripCsvRowNumber;
    private final String tripId;
    private final String routeId;
    private final double speedKph;
    private final double distanceKm;
    private final long csvRowNumber1;
    private final int stopSequence1;
    private final String stopId1;
    private final String stopName1;
    private final GtfsTime departureTime1;
    private final long csvRowNumber2;
    private final int stopSequence2;
    private final String stopId2;
    private final String stopName2;
    private final GtfsTime arrivalTime2;

    FastTravelBetweenConsecutiveStopsNotice(
        GtfsTrip trip,
        GtfsStopTime stopTime1,
        GtfsStop stop1,
        GtfsStopTime stopTime2,
        GtfsStop stop2,
        double speedKph,
        double distanceKm) {
      super(SeverityLevel.WARNING);
      this.tripCsvRowNumber = trip.csvRowNumber();
      this.tripId = trip.tripId();
      this.routeId = trip.routeId();
      this.speedKph = speedKph;
      this.distanceKm = distanceKm;
      this.csvRowNumber1 = stopTime1.csvRowNumber();
      this.stopSequence1 = stopTime1.stopSequence();
      this.stopId1 = stopTime1.stopId();
      this.stopName1 = stop1.stopName();
      this.departureTime1 = stopTime1.departureTime();
      this.csvRowNumber2 = stopTime2.csvRowNumber();
      this.stopSequence2 = stopTime2.stopSequence();
      this.stopId2 = stopTime2.stopId();
      this.stopName2 = stop2.stopName();
      this.arrivalTime2 = stopTime2.arrivalTime();
    }
  }

  /**
   * Describes a trip where the transit vehicle moves too fast between two far stops.
   *
   * <p>This normally indicates a more serious problem than too fast travel between consecutive
   * stops.
   */
  static class FastTravelBetweenFarStopsNotice extends ValidationNotice {
    private final long tripCsvRowNumber;
    private final String tripId;
    private final String routeId;
    private final double speedKph;
    private final double distanceKm;
    private final long csvRowNumber1;
    private final int stopSequence1;
    private final String stopId1;
    private final String stopName1;
    private final GtfsTime departureTime1;
    private final long csvRowNumber2;
    private final int stopSequence2;
    private final String stopId2;
    private final String stopName2;
    private final GtfsTime arrivalTime2;

    FastTravelBetweenFarStopsNotice(
        GtfsTrip trip,
        GtfsStopTime stopTime1,
        GtfsStop stop1,
        GtfsStopTime stopTime2,
        GtfsStop stop2,
        double speedKph,
        double distanceKm) {
      super(SeverityLevel.WARNING);
      this.tripCsvRowNumber = trip.csvRowNumber();
      this.tripId = trip.tripId();
      this.routeId = trip.routeId();
      this.speedKph = speedKph;
      this.distanceKm = distanceKm;
      this.csvRowNumber1 = stopTime1.csvRowNumber();
      this.stopSequence1 = stopTime1.stopSequence();
      this.stopId1 = stopTime1.stopId();
      this.stopName1 = stop1.stopName();
      this.departureTime1 = stopTime1.departureTime();
      this.csvRowNumber2 = stopTime2.csvRowNumber();
      this.stopSequence2 = stopTime2.stopSequence();
      this.stopId2 = stopTime2.stopId();
      this.stopName2 = stop2.stopName();
      this.arrivalTime2 = stopTime2.arrivalTime();
    }
  }
}
