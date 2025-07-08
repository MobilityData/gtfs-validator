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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;
import static org.mobilitydata.gtfsvalidator.util.StopUtil.getStopOrParentLatLng;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.geometry.S2LatLng;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import java.util.*;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.UrlRef;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.util.S2Earth;
import org.mobilitydata.gtfsvalidator.util.StopUtil;

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
    final ListMultimap<Long, TripAndStopTimes> tripsByHash = ArrayListMultimap.create();
    for (List<GtfsStopTime> stopTimes : Multimaps.asMap(stopTimeTable.byTripIdMap()).values()) {
      tripTable
          .byTripId(stopTimes.get(0).tripId())
          .map(trip -> new TripAndStopTimes(trip, stopTimes))
          .ifPresent(
              tripAndStopTimes -> tripsByHash.put(tripAndStopTimes.tripFprint(), tripAndStopTimes));
    }
    for (List<TripAndStopTimes> trips : Multimaps.asMap(tripsByHash).values()) {
      final TripAndStopTimes tripAndStopTimes = trips.get(0);
      // All trips belong to the same route.
      final Optional<GtfsRoute> route = routeTable.byRouteId(tripAndStopTimes.getTrip().routeId());
      if (route.isEmpty()) {
        // Broken reference is reported in another rule.
        continue;
      }
      final double maxSpeedKph = getMaxVehicleSpeedKph(route.get().routeType());
      final double[] distancesKm =
          findDistancesKmBetweenStops(tripAndStopTimes.getStopTimes(), stopTable);
      validateConsecutiveStops(trips, maxSpeedKph, noticeContainer);
      validateFarStops(trips, distancesKm, maxSpeedKph, noticeContainer);
    }
  }

  /** A GTFS trip and all its stop times. */
  private static class TripAndStopTimes {

    private final GtfsTrip trip;

    private final List<GtfsStopTime> stopTimes;

    public TripAndStopTimes(GtfsTrip trip, List<GtfsStopTime> stopTimes) {
      this.trip = trip;
      this.stopTimes = stopTimes;
    }

    public GtfsTrip getTrip() {
      return trip;
    }

    public List<GtfsStopTime> getStopTimes() {
      return stopTimes;
    }

    /**
     * Returns a fingerprint of all trip data that is relevant for validation of far stops: route
     * id, stop ids and arrival and departure times.
     */
    public long tripFprint() {
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
  }

  /**
   * Returns an array of distances between consecutive stops, in km.
   *
   * <p>{@code distancesKm[i]} equals to distance between stops corresponding to stop times i, i +
   * 1.
   */
  @VisibleForTesting
  static double[] findDistancesKmBetweenStops(
      List<GtfsStopTime> stopTimes, GtfsStopTableContainer stopTable) {
    double[] distancesKm = new double[stopTimes.size() - 1];
    S2LatLng currLatLng = getStopOrParentLatLng(stopTable, stopTimes.get(0).stopId());
    for (int i = 0; i < distancesKm.length; ++i) {
      Optional<S2LatLng> maybeNextLatLng =
          StopUtil.getOptionalStopOrParentLatLng(stopTable, stopTimes.get(i + 1).stopId());
      if (maybeNextLatLng.isPresent()) {
        S2LatLng nextLatLng = maybeNextLatLng.get();
        distancesKm[i] = S2Earth.getDistanceKm(currLatLng, nextLatLng);
        currLatLng = nextLatLng;
      } else {
        distancesKm[i] = 0;
      }
    }
    return distancesKm;
  }

  private Optional<Double> getDistanceKm(GtfsStopTime start, GtfsStopTime end) {
    if (!start.hasDepartureTime() || !end.hasArrivalTime()) {
      return Optional.empty();
    }

    Optional<S2LatLng> maybeFirstLatLng =
        StopUtil.getOptionalStopOrParentLatLng(stopTable, start.stopId());
    Optional<S2LatLng> maybeSecondLatLng =
        StopUtil.getOptionalStopOrParentLatLng(stopTable, end.stopId());

    if (maybeFirstLatLng.isEmpty() || maybeSecondLatLng.isEmpty()) {
      return Optional.empty();
    }

    double distanceKm = S2Earth.getDistanceKm(maybeFirstLatLng.get(), maybeSecondLatLng.get());
    return Optional.of(distanceKm);
  }

  /**
   * Validates travel speed between far stops for all trips that belong to the same route and visit
   * the same stops at the same times.
   *
   * <p>If there is a fast travel detected, then exactly one notice is issued for each trip.
   */
  private void validateFarStops(
      List<TripAndStopTimes> trips,
      double[] distancesKm,
      double maxSpeedKph,
      NoticeContainer noticeContainer) {
    final List<GtfsStopTime> stopTimes = trips.get(0).getStopTimes();
    for (int endIdx = 0; endIdx < stopTimes.size(); ++endIdx) {
      final GtfsStopTime endStopTime = stopTimes.get(endIdx);
      if (!endStopTime.hasArrivalTime()) {
        continue;
      }
      final Optional<GtfsStop> endStop = stopTable.byStopId(endStopTime.stopId());
      if (endStop.isEmpty()) {
        // Broken reference is reported in another rule.
        return;
      }
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
        Optional<GtfsStop> startStop = stopTable.byStopId(startStopTime.stopId());
        if (startStop.isEmpty()) {
          // Broken reference is reported in another rule.
          return;
        }
        // Give a notice if they are too far apart.
        if (distanceToEndIdx > MAX_DISTANCE_OVER_MAX_SPEED_IN_KMS) {
          // Issue one notice per each trip.
          for (TripAndStopTimes trip : trips) {
            noticeContainer.addValidationNotice(
                new FastTravelBetweenFarStopsNotice(
                    trip.getTrip(),
                    trip.getStopTimes().get(startIdx),
                    startStop.get(),
                    trip.getStopTimes().get(endIdx),
                    endStop.get(),
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
      List<TripAndStopTimes> trips, double maxSpeedKph, NoticeContainer noticeContainer) {
    final List<GtfsStopTime> stopTimes = trips.get(0).getStopTimes();
    GtfsStopTime start = stopTimes.get(0);
    for (int i = 0; i < stopTimes.size() - 1; ++i) {
      GtfsStopTime end = stopTimes.get(i + 1);

      Optional<Double> maybeDistanceKm = getDistanceKm(start, end);
      // We couldn't calculate the distance, for instance because one of the stops is
      // actually a GeoJSON location and doesn't have a specific latitude and longitude.
      // We try comparing with the next stop instead.
      if (maybeDistanceKm.isEmpty()) {
        continue;
      }

      double distanceKm = maybeDistanceKm.get();
      double speedKph = getSpeedKphBetweenStops(distanceKm, start, end);

      if (speedKph > maxSpeedKph) {
        final Optional<GtfsStop> stop1 = stopTable.byStopId(start.stopId());
        final Optional<GtfsStop> stop2 = stopTable.byStopId(end.stopId());
        // This should always evaluate to true since we check whether both stops exist
        // in `getDistanceAndSpeed`; this is just here as a precaution.
        if (stop1.isPresent() && stop2.isPresent()) {
          // Issue one notice for each trip.
          for (TripAndStopTimes trip : trips) {
            noticeContainer.addValidationNotice(
                new FastTravelBetweenConsecutiveStopsNotice(
                    trip.getTrip(), start, stop1.get(), end, stop2.get(), speedKph, distanceKm));
          }
        }
      }

      start = end;
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
      case CABLE_TRAM:
        // The average speed of a cable car is 15 km/h. Add some safety gap.
        return 30;
      case AERIAL_LIFT:
      case FUNICULAR:
        // Fast aerial tramways operate at 43 km/h. Add some safety gap.
        return 50;
      default:
        // High speed threshold for unknown/unsupported vehicle types.
        return 200;
    }
  }

  /**
   * A transit vehicle moves too fast between two consecutive stops.
   *
   * <p>The speed threshold depends on route type:
   *
   * <table style="width: auto; table-layout: auto;">
   *      <tr>
   *        <th>Route type</th>
   *        <th>Description</th>
   *        <th>Threshold, km/h</th>
   *      </tr>
   *      <tr>
   *        <td>0</td>
   *        <td>Light rail</td>
   *        <td>100</td>
   *      </tr>
   *      <tr>
   *        <td>1</td>
   *        <td>Subway</td>
   *        <td>150</td>
   *      </tr>
   *      <tr>
   *        <td>2</td>
   *        <td>Rail</td>
   *        <td>500</td>
   *      </tr>
   *      <tr>
   *        <td>3</td>
   *        <td>Bus</td>
   *        <td>150</td>
   *      </tr>
   *      <tr>
   *        <td>4</td>
   *        <td>Ferry</td>
   *        <td>80</td>
   *      </tr>
   *      <tr>
   *        <td>5</td>
   *        <td>Cable tram</td>
   *        <td>30</td>
   *      </tr>
   *      <tr>
   *        <td>6</td>
   *        <td>Aerial lift</td>
   *        <td>50</td>
   *      </tr>
   *      <tr>
   *        <td>7</td>
   *        <td>Funicular</td>
   *        <td>50</td>
   *      </tr>
   *      <tr>
   *        <td>11</td>
   *        <td>Trolleybus</td>
   *        <td>150</td>
   *      </tr>
   *      <tr>
   *        <td>12</td>
   *        <td>Monorail</td>
   *        <td>150</td>
   *      </tr>
   *      <tr>
   *        <td>-</td>
   *        <td>Unknown</td>
   *        <td>200</td>
   *      </tr>
   *    </table>
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files =
          @FileRefs({
            GtfsRouteSchema.class,
            GtfsStopSchema.class,
            GtfsStopTimeSchema.class,
            GtfsTripSchema.class
          }),
      urls = {
        @UrlRef(
            label = "Original Python validator implementation",
            url = "https://github.com/google/transitfeed")
      })
  static class FastTravelBetweenConsecutiveStopsNotice extends ValidationNotice {

    /** The row number of the problematic trip. */
    private final long tripCsvRowNumber;

    /** `trip_id` of the problematic trip. */
    private final String tripId;

    /** `route_id` of the problematic trip. */
    private final String routeId;

    /** Travel speed (km/h). */
    private final double speedKph;

    /** Distance between stops (km). */
    private final double distanceKm;

    /** The row number of the first stop time. */
    private final int csvRowNumber1;

    /** `stop_sequence` of the first stop. */
    private final int stopSequence1;

    /** `stop_id` of the first stop. */
    private final String stopId1;

    /** `stop_name` of the first stop. */
    private final String stopName1;

    /** `departure_time` of the first stop. */
    private final GtfsTime departureTime1;

    /** The row number of the second stop time. */
    private final int csvRowNumber2;

    /** `stop_sequence` of the second stop. */
    private final int stopSequence2;

    /** `stop_id` of the second stop. */
    private final String stopId2;

    /** `stop_name` of the second stop. */
    private final String stopName2;

    /** `arrival_time` of the second stop. */
    private final GtfsTime arrivalTime2;

    FastTravelBetweenConsecutiveStopsNotice(
        GtfsTrip trip,
        GtfsStopTime stopTime1,
        GtfsStop stop1,
        GtfsStopTime stopTime2,
        GtfsStop stop2,
        double speedKph,
        double distanceKm) {
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
   * A transit vehicle moves too fast between two far stops.
   *
   * <p>Two stops are considered "far" if they are more than 10 km apart. This normally indicates a
   * more serious problem than too fast travel between consecutive stops.
   *
   * <p>The speed threshold depends on route type and are the same as
   * `fast_travel_between_consecutive_stops`.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files =
          @FileRefs({
            GtfsRouteSchema.class,
            GtfsStopSchema.class,
            GtfsStopTimeSchema.class,
            GtfsTripSchema.class
          }),
      urls = {
        @UrlRef(
            label = "Original Python validator implementation",
            url = "https://github.com/google/transitfeed")
      })
  static class FastTravelBetweenFarStopsNotice extends ValidationNotice {

    /** The row number of the problematic trip. */
    private final long tripCsvRowNumber;

    /** `trip_id` of the problematic trip. */
    private final String tripId;

    /** `route_id` of the problematic trip. */
    private final String routeId;

    /** Travel speed (km/h). */
    private final double speedKph;

    /** Distance between stops (km). */
    private final double distanceKm;

    /** The row number of the first stop time. */
    private final int csvRowNumber1;

    /** `stop_sequence` of the first stop. */
    private final int stopSequence1;

    /** `stop_id` of the first stop. */
    private final String stopId1;

    /** `stop_name` of the first stop. */
    private final String stopName1;

    /** `departure_time` of the first stop. */
    private final GtfsTime departureTime1;

    /** The row number of the second stop time. */
    private final int csvRowNumber2;

    /** `stop_sequence` of the second stop. */
    private final int stopSequence2;

    /** `stop_id` of the second stop. */
    private final String stopId2;

    /** `stop_name` of the second stop. */
    private final String stopName2;

    /** `arrival_time` of the second stop. */
    private final GtfsTime arrivalTime2;

    FastTravelBetweenFarStopsNotice(
        GtfsTrip trip,
        GtfsStopTime stopTime1,
        GtfsStop stop1,
        GtfsStopTime stopTime2,
        GtfsStop stop2,
        double speedKph,
        double distanceKm) {
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
