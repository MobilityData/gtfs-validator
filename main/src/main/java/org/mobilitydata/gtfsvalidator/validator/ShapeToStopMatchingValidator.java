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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Multimaps;
import com.google.common.geometry.S2LatLng;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.util.shape.Problem;
import org.mobilitydata.gtfsvalidator.util.shape.Problem.ProblemType;
import org.mobilitydata.gtfsvalidator.util.shape.ShapePoints;
import org.mobilitydata.gtfsvalidator.util.shape.StopPoints;
import org.mobilitydata.gtfsvalidator.util.shape.StopToShapeMatcher;
import org.mobilitydata.gtfsvalidator.util.shape.StopToShapeMatcherSettings;

/**
 * Validates that stops match to shapes properly, mostly focusing on making sure that the stop
 * sequences for trips that reference the shape can be properly matched to the shape.
 */
@GtfsValidator
public class ShapeToStopMatchingValidator extends FileValidator {

  private static final HashFunction HASH_FUNCTION = Hashing.farmHashFingerprint64();

  private final GtfsStopTableContainer stopTable;

  private final GtfsTripTableContainer tripTable;

  private final GtfsRouteTableContainer routeTable;

  private final GtfsStopTimeTableContainer stopTimeTable;

  private final GtfsShapeTableContainer shapeTable;

  private final StopToShapeMatcher stopToShapeMatcher;

  @Inject
  ShapeToStopMatchingValidator(
      GtfsStopTableContainer stopTable,
      GtfsTripTableContainer tripTable,
      GtfsRouteTableContainer routeTable,
      GtfsStopTimeTableContainer stopTimeTable,
      GtfsShapeTableContainer shapeTable) {
    this(
        stopTable,
        tripTable,
        routeTable,
        stopTimeTable,
        shapeTable,
        new StopToShapeMatcher(new StopToShapeMatcherSettings()));
  }

  @VisibleForTesting
  ShapeToStopMatchingValidator(
      GtfsStopTableContainer stopTable,
      GtfsTripTableContainer tripTable,
      GtfsRouteTableContainer routeTable,
      GtfsStopTimeTableContainer stopTimeTable,
      GtfsShapeTableContainer shapeTable,
      StopToShapeMatcher stopToShapeMatcher) {
    this.stopTable = stopTable;
    this.tripTable = tripTable;
    this.routeTable = routeTable;
    this.stopTimeTable = stopTimeTable;
    this.shapeTable = shapeTable;
    this.stopToShapeMatcher = stopToShapeMatcher;
  }

  private static long tripHash(List<GtfsStopTime> stopTimes) {
    Hasher hasher = HASH_FUNCTION.newHasher().putInt(stopTimes.size());
    for (GtfsStopTime stopTime : stopTimes) {
      hasher
          .putInt(stopTime.stopId().length())
          .putUnencodedChars(stopTime.stopId())
          .putDouble(stopTime.shapeDistTraveled());
    }
    return hasher.hash().asLong();
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    if (stopTable.getEntities().isEmpty()
        || tripTable.getEntities().isEmpty()
        || stopTimeTable.getEntities().isEmpty()
        || shapeTable.getEntities().isEmpty()) {
      return;
    }
    for (List<GtfsShape> gtfsShapePoints : Multimaps.asMap(shapeTable.byShapeIdMap()).values()) {
      List<GtfsTrip> trips = tripTable.byShapeId(gtfsShapePoints.get(0).shapeId());
      if (trips.isEmpty()) {
        continue;
      }
      final ShapePoints shapePoints = ShapePoints.fromGtfsShape(gtfsShapePoints);
      // Report each stop that is too far from shape only once, even if there are multiple trips
      // that visit it.
      Set<Long> processedTripHashes = new HashSet<>();
      Set<String> reportedStopIds = new HashSet<>();
      for (GtfsTrip trip : trips) {
        List<GtfsStopTime> stopTimes = stopTimeTable.byTripId(trip.tripId());
        if (!processedTripHashes.add(tripHash(stopTimes))) {
          continue;
        }
        Optional<GtfsRoute> route = routeTable.byRouteId(trip.routeId());
        if (route.isEmpty()) {
          // Broken reference is reported in another rule.
          continue;
        }
        final StopPoints stopPoints =
            StopPoints.fromStopTimes(
                stopTimes, stopTable, StopPoints.routeTypeToStationSize(route.get().routeType()));
        reportProblems(
            trip,
            stopToShapeMatcher.matchUsingGeoDistance(stopPoints, shapePoints).getProblems(),
            MatchingDistance.GEO,
            reportedStopIds,
            noticeContainer);
        if (stopPoints.hasUserDistance() && shapePoints.hasUserDistance()) {
          reportProblems(
              trip,
              stopToShapeMatcher.matchUsingUserDistance(stopPoints, shapePoints).getProblems(),
              MatchingDistance.USER,
              reportedStopIds,
              noticeContainer);
        }
      }
    }
  }

  private void reportProblems(
      GtfsTrip trip,
      List<Problem> problems,
      MatchingDistance matchingDistance,
      Set<String> reportedStopIds,
      NoticeContainer noticeContainer) {
    for (Problem problem : problems) {
      if (problem.getType().equals(ProblemType.STOP_TOO_FAR_FROM_SHAPE)
          && !reportedStopIds.add(problem.getStopTime().stopId())) {
        // Ignore stops already reported before.
        continue;
      }
      noticeContainer.addValidationNotice(convertProblemToNotice(trip, problem, matchingDistance));
    }
  }

  private ValidationNotice convertProblemToNotice(
      GtfsTrip trip, Problem problem, MatchingDistance matchingDistance) {
    switch (problem.getType()) {
      case STOP_TOO_FAR_FROM_SHAPE:
        return matchingDistance.equals(MatchingDistance.GEO)
            ? new StopTooFarFromShapeNotice(
                trip,
                problem.getStopTime(),
                stopNameForStopTime(problem.getStopTime()),
                problem.getMatch().getLocationLatLng(),
                problem.getMatch().getGeoDistanceToShape())
            : new StopTooFarFromShapeUsingUserDistanceNotice(
                trip,
                problem.getStopTime(),
                stopNameForStopTime(problem.getStopTime()),
                problem.getMatch().getLocationLatLng(),
                problem.getMatch().getGeoDistanceToShape());
      case STOPS_MATCH_OUT_OF_ORDER:
        return new StopsMatchShapeOutOfOrderNotice(
            trip,
            problem.getStopTime(),
            stopNameForStopTime(problem.getStopTime()),
            problem.getMatch().getLocationLatLng(),
            problem.getPrevStopTime(),
            stopNameForStopTime(problem.getPrevStopTime()),
            problem.getPrevMatch().getLocationLatLng());
      default:
        // STOP_HAS_TOO_MANY_MATCHES
        return new StopHasTooManyMatchesForShapeNotice(
            trip,
            problem.getStopTime(),
            stopNameForStopTime(problem.getStopTime()),
            problem.getMatch().getLocationLatLng(),
            problem.getMatchCount());
    }
  }

  private String stopNameForStopTime(GtfsStopTime stopTime) {
    return stopTable.byStopId(stopTime.stopId()).map(GtfsStop::stopName).orElse("");
  }

  private enum MatchingDistance {

    /**
     * The distance along the shape on the earth's surface by starting at the first point, adding
     * the straight-line (or rather, great circle) distance for each pair of points.
     */
    GEO,
    /**
     * User distance is an arbitrary (positive and non-decreasing) parameterization passed in with
     * each point as {@code shape_dist_traveled}.
     */
    USER
  }

  /**
   * Describes a stop entry that has many potential matches to the trip's path of travel, as defined
   * by the shape entry in {@code shapes.txt}.
   *
   * <p>This potentially indicates a problem with the location of the stop or the path of the shape.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @FileRefs({GtfsTripSchema.class, GtfsStopTimeSchema.class, GtfsStopSchema.class}))
  static class StopHasTooManyMatchesForShapeNotice extends ValidationNotice {

    /** The row number of the faulty record from `trips.txt`. */
    private final long tripCsvRowNumber;

    /** The id of the shape that is referred to. */
    private final String shapeId;

    /** The id of the trip that is referred to. */
    private final String tripId;

    /** The row number of the faulty record from `stop_times.txt`. */
    private final long stopTimeCsvRowNumber;

    /** The id of the stop that is referred to. */
    private final String stopId;

    /** The name of the stop that is referred to. */
    private final String stopName;

    /** Latitude and longitude pair of the location. */
    private final S2LatLng match;

    /** The number of matches for the stop that is referred to. */
    private final int matchCount;

    StopHasTooManyMatchesForShapeNotice(
        GtfsTrip trip, GtfsStopTime stopTime, String stopName, S2LatLng location, int matchCount) {
      super(SeverityLevel.WARNING);
      this.tripCsvRowNumber = trip.csvRowNumber();
      this.shapeId = trip.shapeId();
      this.tripId = trip.tripId();
      this.stopTimeCsvRowNumber = stopTime.csvRowNumber();
      this.stopId = stopTime.stopId();
      this.stopName = stopName;
      this.match = location;
      this.matchCount = matchCount;
    }
  }

  /**
   * Describes a stop time entry that is a large distance away from the location of the shape in
   * {@code shapes.txt} as defined by {@code shape_dist_traveled} values.
   *
   * <p>This potentially indicates a problem with the location of the stop or the use of {@code
   * shape_dist_traveled} values.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files =
          @FileRefs({
            GtfsTripSchema.class,
            GtfsStopTimeSchema.class,
            GtfsStopSchema.class,
            GtfsStopTimeSchema.class
          }))
  static class StopTooFarFromShapeUsingUserDistanceNotice extends ValidationNotice {

    /** The row number of the faulty record from `trips.txt`. */
    private final long tripCsvRowNumber;

    /** The id of the shape that is referred to. */
    private final String shapeId;

    /** The id of the trip that is referred to. */
    private final String tripId;

    /** The row number of the faulty record from `stop_times.txt`. */
    private final long stopTimeCsvRowNumber;

    /** The id of the stop that is referred to. */
    private final String stopId;

    /** The name of the stop that is referred to. */
    private final String stopName;

    /** Latitude and longitude pair of the location. */
    private final S2LatLng match;

    /** Distance from stop to shape. */
    private final double geoDistanceToShape;

    StopTooFarFromShapeUsingUserDistanceNotice(
        GtfsTrip trip,
        GtfsStopTime stopTime,
        String stopName,
        S2LatLng location,
        double geoDistanceToShape) {
      super(SeverityLevel.WARNING);
      this.tripCsvRowNumber = trip.csvRowNumber();
      this.shapeId = trip.shapeId();
      this.tripId = trip.tripId();
      this.stopTimeCsvRowNumber = stopTime.csvRowNumber();
      this.stopId = stopTime.stopId();
      this.stopName = stopName;
      this.match = location;
      this.geoDistanceToShape = geoDistanceToShape;
    }
  }

  /**
   * Describes a stop time entry that is a large distance away from the trip's path of travel, as
   * defined by the shape entry in {@code shapes.txt}.
   *
   * <p>This potentially indicates a problem with the location of the stop or the path of the shape.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @FileRefs({GtfsStopTimeSchema.class, GtfsStopSchema.class, GtfsTripSchema.class}),
      bestPractices = @FileRefs(GtfsShapeSchema.class))
  static class StopTooFarFromShapeNotice extends ValidationNotice {

    /** The row number of the faulty record from `trips.txt`. */
    private final long tripCsvRowNumber;

    /** The id of the shape that is referred to. */
    private final String shapeId;

    /** The id of the trip that is referred to. */
    private final String tripId;

    /** The row number of the faulty record from `stop_times.txt`. */
    private final long stopTimeCsvRowNumber;

    /** The id of the stop that is referred to. */
    private final String stopId;

    /** The name of the stop that is referred to. */
    private final String stopName;

    /** Latitude and longitude pair of the location. */
    private final S2LatLng match;

    /** Distance from stop to shape. */
    private final double geoDistanceToShape;

    StopTooFarFromShapeNotice(
        GtfsTrip trip,
        GtfsStopTime stopTime,
        String stopName,
        S2LatLng location,
        double geoDistanceToShape) {
      super(SeverityLevel.WARNING);
      this.tripCsvRowNumber = trip.csvRowNumber();
      this.shapeId = trip.shapeId();
      this.tripId = trip.tripId();
      this.stopTimeCsvRowNumber = stopTime.csvRowNumber();
      this.stopId = stopTime.stopId();
      this.stopName = stopName;
      this.match = location;
      this.geoDistanceToShape = geoDistanceToShape;
    }
  }

  /**
   * Describes two stop entries in {@code stop_times.txt} that are different than their
   * arrival-departure order as defined by the shape in the {@code shapes.txt} file.
   *
   * <p>This could indicate a problem with the location of the stops, the path of the shape, or the
   * sequence of the stops for their trip.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @FileRefs({GtfsTripSchema.class, GtfsStopTimeSchema.class, GtfsStopSchema.class}))
  static class StopsMatchShapeOutOfOrderNotice extends ValidationNotice {

    /** The row number of the faulty record from `trips.txt`. */
    private final long tripCsvRowNumber;

    /** The id of the shape that is referred to. */
    private final String shapeId;

    /** The id of the trip that is referred to. */
    private final String tripId;

    /** The row number of the first faulty record from `stop_times.txt`. */
    private final long stopTimeCsvRowNumber1;

    /** The id of the first stop that is referred to. */
    private final String stopId1;

    /** The name of the first stop that is referred to. */
    private final String stopName1;

    /** Latitude and longitude pair of the first matching location. */
    private final S2LatLng match1;

    /** The row number of the second faulty record from `stop_times.txt`. */
    private final long stopTimeCsvRowNumber2;

    /** The id of the second stop that is referred to. */
    private final String stopId2;

    /** The name of the second stop that is referred to. */
    private final String stopName2;

    /** Latitude and longitude pair of the second matching location. */
    private final S2LatLng match2;

    public StopsMatchShapeOutOfOrderNotice(
        GtfsTrip trip,
        GtfsStopTime stopTime1,
        String stopName1,
        S2LatLng location1,
        GtfsStopTime stopTime2,
        String stopName2,
        S2LatLng location2) {
      super(SeverityLevel.WARNING);
      this.tripCsvRowNumber = trip.csvRowNumber();
      this.shapeId = trip.shapeId();
      this.tripId = trip.tripId();
      this.stopTimeCsvRowNumber1 = stopTime1.csvRowNumber();
      this.stopId1 = stopTime1.stopId();
      this.stopName1 = stopName1;
      this.match1 = location1;
      this.stopTimeCsvRowNumber2 = stopTime2.csvRowNumber();
      this.stopId2 = stopTime2.stopId();
      this.stopName2 = stopName2;
      this.match2 = location2;
    }
  }
}
