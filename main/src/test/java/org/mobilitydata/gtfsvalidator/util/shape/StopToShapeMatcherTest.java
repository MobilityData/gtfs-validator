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
import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2Point;
import com.google.common.truth.Correspondence;
import com.google.common.truth.Expect;
import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.util.shape.StopPoints.StationSize;
import org.mobilitydata.gtfsvalidator.util.shape.StopToShapeMatcher.MatchResult;

@RunWith(JUnit4.class)
public class StopToShapeMatcherTest {
  @Rule public final Expect expect = Expect.create();

  private static final double DOUBLE_PRECISION = 0.1;

  private static final Correspondence<StopToShapeMatch, StopToShapeMatch> APPROX_SAME_MATCH =
      Correspondence.from((m1, m2) -> m1.approxEquals(m2, DOUBLE_PRECISION), "same match");
  private static final Correspondence<Problem, Problem> APPROX_SAME_PROBLEM =
      Correspondence.from((p1, p2) -> p1.approxEquals(p2, DOUBLE_PRECISION), "same problem");

  private static GtfsShape createGtfsShape(
      int ptSequence, double latDegrees, double lngDegrees, Double shapeDistTraveled) {
    return new GtfsShape.Builder()
        .setCsvRowNumber(ptSequence + 2)
        .setShapePtSequence(ptSequence)
        .setShapePtLat(latDegrees)
        .setShapePtLon(lngDegrees)
        .setShapeDistTraveled(shapeDistTraveled)
        .build();
  }

  private static ShapePoints createShapePoints() {
    return ShapePoints.fromGtfsShape(
        ImmutableList.of(
            createGtfsShape(0, 47.365399, 8.525138, 0.0),
            createGtfsShape(1, 47.366013, 8.524972, 1.0),
            createGtfsShape(2, 47.366073, 8.525384, 2.0),
            createGtfsShape(3, 47.364120, 8.525886, 3.0),
            createGtfsShape(4, 47.364046, 8.525559, 4.0),
            createGtfsShape(5, 47.364376, 8.525376, 5.0),
            createGtfsShape(6, 47.364976, 8.525258, 6.0),
            createGtfsShape(7, 47.365016, 8.525666, 7.0),
            createGtfsShape(8, 47.365103, 8.525650, 8.0),
            createGtfsShape(9, 47.365143, 8.526015, 9.0)));
  }

  private static String numberToStopId(int number) {
    return "s" + number;
  }

  private static GtfsStop createStop(int number, double stopLat, double stopLon) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(2 + number)
        .setStopId(numberToStopId(number))
        .setStopName("Stop " + number)
        .setStopLat(stopLat)
        .setStopLon(stopLon)
        .build();
  }

  private static List<GtfsStop> createStops(List<S2LatLng> points) {
    List<GtfsStop> stops = new ArrayList<>(points.size());
    for (int i = 0; i < points.size(); ++i) {
      stops.add(createStop(i, points.get(i).latDegrees(), points.get(i).lngDegrees()));
    }
    return stops;
  }

  private static List<GtfsStopTime> createStopTimes(Double[] shapeDistTraveled) {
    List<GtfsStopTime> stopTimes = new ArrayList<>(shapeDistTraveled.length);
    for (int i = 0; i < shapeDistTraveled.length; ++i) {
      stopTimes.add(
          new GtfsStopTime.Builder()
              .setCsvRowNumber(i + 2)
              .setStopId(numberToStopId(i))
              .setStopSequence(i)
              .setShapeDistTraveled(shapeDistTraveled[i])
              .build());
    }
    return stopTimes;
  }

  private static S2Point toS2Point(double lat, double lng) {
    return S2LatLng.fromDegrees(lat, lng).toPoint();
  }

  private static StopPoints createStopPoints(
      Double[] shapeDistTraveled, List<S2LatLng> stopPoints) {
    return createStopPoints(shapeDistTraveled, stopPoints, StationSize.SMALL);
  }

  private static StopPoints createStopPoints(
      Double[] shapeDistTraveled, List<S2LatLng> stopPoints, StationSize stationSize) {
    return StopPoints.fromStopTimes(
        createStopTimes(shapeDistTraveled),
        GtfsStopTableContainer.forEntities(createStops(stopPoints), new NoticeContainer()),
        stationSize);
  }

  private static StopToShapeMatcher createMatcher() {
    StopToShapeMatcherSettings settings = new StopToShapeMatcherSettings();
    settings.setMaxDistanceFromStopToShapeInMeters(150.0);
    return new StopToShapeMatcher(settings);
  }

  @Test
  public void matchUsingUserDistance_fullUserDistances() {
    StopPoints stopPoints =
        createStopPoints(
            new Double[] {0.2, 2.4, 6.8},
            ImmutableList.of(
                S2LatLng.fromDegrees(47.365478, 8.525152),
                S2LatLng.fromDegrees(47.365051, 8.525622),
                S2LatLng.fromDegrees(47.364991, 8.525632)));
    final MatchResult matchResult =
        createMatcher().matchUsingUserDistance(stopPoints, createShapePoints());

    expect.that(matchResult.getProblems()).isEmpty();
    expect
        .that(matchResult.getMatches())
        .comparingElementsUsing(APPROX_SAME_MATCH)
        .containsExactly(
            new StopToShapeMatch(0, 0.2, 13.8, 6.0, toS2Point(47.3655218, 8.5251048)),
            new StopToShapeMatch(2, 2.4, 189.3, 26.9, toS2Point(47.3652918, 8.5255848)),
            new StopToShapeMatch(6, 6.8, 478.9, 4.1, toS2Point(47.3650080, 8.5255844)));
  }

  @Test
  public void matchUsingUserDistance_partialUserDistances() {
    StopPoints stopPoints =
        createStopPoints(
            new Double[] {0.2, null, 6.8},
            ImmutableList.of(
                S2LatLng.fromDegrees(47.365478, 8.525152),
                S2LatLng.fromDegrees(47.365061, 8.525682),
                S2LatLng.fromDegrees(47.364991, 8.525632)));
    final MatchResult matchResult =
        createMatcher().matchUsingUserDistance(stopPoints, createShapePoints());

    expect.that(matchResult.getProblems()).isEmpty();
    expect
        .that(matchResult.getMatches())
        .comparingElementsUsing(APPROX_SAME_MATCH)
        .containsExactly(
            new StopToShapeMatch(0, 0.2, 13.8, 6.0, toS2Point(47.3655218, 8.5251048)),
            new StopToShapeMatch(2, 0, 215.9, 2.8, toS2Point(47.3650567, 8.5256452)),
            new StopToShapeMatch(6, 6.8, 478.9, 4.1, toS2Point(47.3650080, 8.5255844)));
  }

  @Test
  public void matchUsingUserDistance_stopTooFarAway() {
    StopPoints stopPoints =
        createStopPoints(
            new Double[] {0.2, 2.4, 6.8},
            ImmutableList.of(
                S2LatLng.fromDegrees(47.365478, 8.525152),
                S2LatLng.fromDegrees(47.366375, 8.527333),
                S2LatLng.fromDegrees(47.364991, 8.525632)));
    final MatchResult matchResult =
        createMatcher().matchUsingUserDistance(stopPoints, createShapePoints());

    expect
        .that(matchResult.getProblems())
        .comparingElementsUsing(APPROX_SAME_PROBLEM)
        .containsExactly(
            Problem.createStopTooFarFromShapeProblem(
                stopPoints.get(1).getStopTime(),
                new StopToShapeMatch(2, 2.4, 189.3, 178.4, toS2Point(47.3652918, 8.5255848))));
    expect.that(matchResult.getMatches()).isEmpty();
  }

  @Test
  public void matchUsingGeoDistance_normal() {
    StopPoints stopPoints =
        createStopPoints(
            new Double[] {null, null, null},
            ImmutableList.of(
                S2LatLng.fromDegrees(47.365478, 8.525152),
                S2LatLng.fromDegrees(47.364169, 8.525843),
                S2LatLng.fromDegrees(47.364991, 8.525632)));
    final MatchResult matchResult =
        createMatcher().matchUsingGeoDistance(stopPoints, createShapePoints());

    expect.that(matchResult.getProblems()).isEmpty();
    expect
        .that(matchResult.getMatches())
        .comparingElementsUsing(APPROX_SAME_MATCH)
        .containsExactly(
            new StopToShapeMatch(0, 0.0, 8.5, 2.6, toS2Point(47.3654738, 8.5251178)),
            new StopToShapeMatch(2, 0.0, 315.7, 2.2, toS2Point(47.3641725, 8.5258725)),
            new StopToShapeMatch(6, 0.0, 482.2, 2.4, toS2Point(47.3650122, 8.5256275)));
  }

  @Test
  public void matchUsingGeoDistance_tooManyMatches() {
    StopPoints stopPoints =
        createStopPoints(
            new Double[] {null}, ImmutableList.of(S2LatLng.fromDegrees(47.365033, 8.525638)));
    StopToShapeMatcherSettings settings = new StopToShapeMatcherSettings();
    settings.setMaxDistanceFromStopToShapeInMeters(20.0);
    settings.setPotentialMatchesForStopProblemThreshold(2);
    final MatchResult matchResult =
        new StopToShapeMatcher(settings).matchUsingGeoDistance(stopPoints, createShapePoints());

    expect
        .that(matchResult.getProblems())
        .comparingElementsUsing(APPROX_SAME_PROBLEM)
        .containsExactly(
            Problem.createStopHasTooManyMatchesProblem(
                stopPoints.get(0).getStopTime(),
                new StopToShapeMatch(2, 0.0, 218.4, 1.0, toS2Point(47.3650345, 8.5256509)),
                3));
    expect.that(matchResult.getMatches()).hasSize(1);
  }

  @Test
  public void matchUsingGeoDistance_outOfOrder() {
    StopPoints stopPoints =
        createStopPoints(
            new Double[] {null, null},
            ImmutableList.of(
                S2LatLng.fromDegrees(47.364107, 8.525701),
                S2LatLng.fromDegrees(47.366026, 8.525189)));
    StopToShapeMatcherSettings settings = new StopToShapeMatcherSettings();
    settings.setMaxDistanceFromStopToShapeInMeters(20.0);
    final MatchResult matchResult =
        new StopToShapeMatcher(settings).matchUsingGeoDistance(stopPoints, createShapePoints());

    expect
        .that(matchResult.getProblems())
        .comparingElementsUsing(APPROX_SAME_PROBLEM)
        .containsExactly(
            Problem.createStopMatchOutOfOrderProblem(
                stopPoints.get(1).getStopTime(),
                new StopToShapeMatch(1, 0.0, 85.7, 2.0, toS2Point(47.3660438, 8.5251834)),
                stopPoints.get(0).getStopTime(),
                new StopToShapeMatch(3, 0.0, 335.2, 3.0, toS2Point(47.3640810, 8.5257138))));
    expect.that(matchResult.getMatches()).isEmpty();
  }

  @Test
  public void matchUsingGeoDistance_stopFarAway() {
    StopPoints stopPoints =
        createStopPoints(
            new Double[] {null, null, null},
            ImmutableList.of(
                S2LatLng.fromDegrees(47.365478, 8.525152),
                S2LatLng.fromDegrees(47.366375, 8.527333),
                S2LatLng.fromDegrees(47.364991, 8.525632)));
    final MatchResult matchResult =
        createMatcher().matchUsingGeoDistance(stopPoints, createShapePoints());

    expect
        .that(matchResult.getProblems())
        .comparingElementsUsing(APPROX_SAME_PROBLEM)
        .containsExactly(
            Problem.createStopTooFarFromShapeProblem(
                stopPoints.get(1).getStopTime(),
                new StopToShapeMatch(1, 0.0, 101.1, 150.6, toS2Point(47.3660730, 8.5253840))));
    expect.that(matchResult.getMatches()).isEmpty();
  }

  @Test
  public void matchUsingGeoDistance_largeStation() {
    ImmutableList<S2LatLng> gtfsStopsPoints =
        ImmutableList.of(
            S2LatLng.fromDegrees(47.365478, 8.525152),
            S2LatLng.fromDegrees(47.364169, 8.525843),
            S2LatLng.fromDegrees(47.365748, 8.528508));

    MatchResult matchResult =
        createMatcher()
            .matchUsingGeoDistance(
                createStopPoints(
                    new Double[] {null, null, null}, gtfsStopsPoints, StationSize.LARGE),
                createShapePoints());

    expect.that(matchResult.getMatches()).isNotEmpty();

    matchResult =
        createMatcher()
            .matchUsingGeoDistance(
                createStopPoints(
                    new Double[] {null, null, null}, gtfsStopsPoints, StationSize.SMALL),
                createShapePoints());

    expect.that(matchResult.getMatches()).isEmpty();
  }
}
