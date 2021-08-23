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
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.mobilitydata.gtfsvalidator.util.shape.StopPoints.StopPoint;

/**
 * A strategy class for matching the stops for a GTFS trip to a GTFS shape.
 *
 * <p>The matcher supports both:
 *
 * <ul>
 *   <li>user distance - the {@code shape_dist_traveled} values, if specified;
 *   <li>geo distance - closest-point matching strategy.
 * </ul>
 *
 * *
 */
public class StopToShapeMatcher {

  private final StopToShapeMatcherSettings settings;

  public StopToShapeMatcher(StopToShapeMatcherSettings settings) {
    this.settings = settings;
  }

  /** Attempts to match using user distance values, i.e. {@code shape_dist_traveled} fields. */
  public MatchResult matchUsingUserDistance(StopPoints stopPoints, ShapePoints shapePoints) {
    List<StopToShapeMatch> matches = new ArrayList<>();
    List<Problem> problems = new ArrayList<>();
    if (stopPoints.isEmpty() || shapePoints.isEmpty()) {
      return new MatchResult(matches, problems);
    }

    List<List<StopToShapeMatch>> potentialMatches = new ArrayList<>();
    int searchFromIndex = 0;
    for (StopPoint stopPoint : stopPoints.getPoints()) {
      List<StopToShapeMatch> matchesForStop;
      if (!stopPoint.hasUserDistance()) {
        matchesForStop = computePotentialMatchesUsingGeoDistance(shapePoints, stopPoint, problems);
        if (matchesForStop.isEmpty()) {
          return new MatchResult(matches, problems);
        }
      } else {
        final StopToShapeMatch match =
            shapePoints.matchFromUserDist(
                stopPoint.getUserDistance(), searchFromIndex, stopPoint.getLocation());
        matchesForStop = new ArrayList<>();
        matchesForStop.add(match);
        searchFromIndex = match.getIndex();
      }

      potentialMatches.add(matchesForStop);
    }
    matches = findBestMatches(stopPoints, potentialMatches, problems);
    if (!matches.isEmpty()
        && !isValidStopsToShapeMatchFromUserDistance(stopPoints, matches, problems)) {
      matches.clear();
    }
    return new MatchResult(matches, problems);
  }

  /** Attempts to match using a closest-point strategy. */
  public MatchResult matchUsingGeoDistance(StopPoints stopPoints, ShapePoints shapePoints) {
    List<StopToShapeMatch> matches = new ArrayList<>();
    List<Problem> problems = new ArrayList<>();
    if (stopPoints.isEmpty() || shapePoints.isEmpty()) {
      return new MatchResult(matches, problems);
    }

    List<List<StopToShapeMatch>> potentialMatches = new ArrayList<>(stopPoints.size());
    boolean ok = true;
    for (StopPoints.StopPoint point : stopPoints.getPoints()) {
      List<StopToShapeMatch> matchesForStop =
          computePotentialMatchesUsingGeoDistance(shapePoints, point, problems);
      potentialMatches.add(matchesForStop);
      ok &= !matchesForStop.isEmpty();
    }
    if (!ok) {
      return new MatchResult(matches, problems);
    }
    matches = findBestMatches(stopPoints, potentialMatches, problems);
    return new MatchResult(matches, problems);
  }

  /**
   * Given a list of potential closest-point matches, finds an assignment of matches whose
   * geoDistance values are always increasing along the shape that also minimizes the
   * geoDistanceToShape value for each stop. Returns an empty list if no feasible match was found.
   */
  private static List<StopToShapeMatch> findBestMatches(
      StopPoints stopPoints,
      List<List<StopToShapeMatch>> potentialMatches,
      List<Problem> problems) {
    // The idea: at this point, we have a set of potential matches for each stop
    // and we want to find a feasible assignment (aka geoDistance always
    // increasing) with a minimal score (aka minimize the sum of
    // geoDistanceToShape).  To do this, we iteratively construct feasible
    // assignments, starting with the first stop and moving down the sequence.
    // For the first stop, each potential match is a feasible assignment.  For
    // each subsequent stop, we consider each of the stop's potential matches.
    // We try to pair the match with an assignment from the previous stops to
    // construct a new assignment that is feasible.  When multiple feasible
    // assignments are found, we keep the best scoring assignment for the match.
    // If a match doesn't have any feasible assignments, it is pruned from the
    // assignment set.  If NONE of the matches for a stop have a feasible
    // assignment, then no match is possible and we throw an error.

    // We start off with a single empty assignment from which our best
    // incremental assignments will grow.
    List<Assignment> partialAssignments = new ArrayList<>();
    partialAssignments.add(new Assignment());

    List<StopToShapeMatch> matches = new ArrayList<>();
    for (int index = 0; index < potentialMatches.size(); ++index) {
      List<Assignment> nextAssignments =
          constructBestIncrementalAssignments(potentialMatches.get(index), partialAssignments);
      if (nextAssignments.isEmpty()) {
        problems.add(
            constructOutOfOrderError(stopPoints, potentialMatches, index, partialAssignments));
        return matches;
      }
      partialAssignments = nextAssignments;
    }

    final List<Integer> bestAssignment =
        Collections.min(partialAssignments, Comparator.comparing(Assignment::getScore))
            .getAssignment();
    for (int index = 0; index < potentialMatches.size(); ++index) {
      matches.add(potentialMatches.get(index).get(bestAssignment.get(index)));
    }
    return matches;
  }

  /**
   * Creates a STOPS_MATCH_OUT_OF_ORDER problem for the current and previous stop, identified by
   * index when a incremental assignment was not possible because there was no pair of feasible
   * matches for two stops (aka geoDistance order was not maintained). The best scoring match for
   * the current and prev stop are used to summarize the mismatch.
   */
  private static Problem constructOutOfOrderError(
      StopPoints stopPoints,
      List<List<StopToShapeMatch>> potentialMatches,
      int index,
      List<Assignment> prevAssignments) {
    final StopToShapeMatch match =
        Collections.min(
            potentialMatches.get(index),
            Comparator.comparing(StopToShapeMatch::getGeoDistanceToShape));
    final List<Integer> prevAssignment =
        Collections.min(prevAssignments, Comparator.comparing(Assignment::getScore))
            .getAssignment();
    final StopToShapeMatch prevMatch =
        potentialMatches.get(index - 1).get(Iterables.getLast(prevAssignment));
    return Problem.createStopMatchOutOfOrderProblem(
        stopPoints.get(index).getStopTime(),
        match,
        stopPoints.get(index - 1).getStopTime(),
        prevMatch);
  }

  /**
   * Given a set of potential matches for a stop and a list of valid assignments through the
   * previous stop, finds the best-scoring feasible assignment for each match and returns them.
   */
  private static List<Assignment> constructBestIncrementalAssignments(
      final List<StopToShapeMatch> potentialMatches, final List<Assignment> prevAssignments) {
    List<Assignment> nextAssignments = new ArrayList<>();
    for (int i = 0; i < potentialMatches.size(); ++i) {
      final StopToShapeMatch match = potentialMatches.get(i);
      int bestIndex = -1;
      double bestScore = Double.POSITIVE_INFINITY;
      for (int prevI = 0; prevI < prevAssignments.size(); ++prevI) {
        final Assignment prev = prevAssignments.get(prevI);
        if (prev.getMaxGeoDistance() > match.getGeoDistance()) {
          continue;
        }
        if (prev.getScore() < bestScore) {
          bestIndex = prevI;
          bestScore = prev.getScore();
        }
      }
      if (bestIndex != -1) {
        final Assignment partial = prevAssignments.get(bestIndex);
        nextAssignments.add(
            new Assignment(
                new ImmutableList.Builder<Integer>().addAll(partial.getAssignment()).add(i).build(),
                partial.getScore() + match.getGeoDistanceToShape(),
                match.getGeoDistance()));
      }
    }
    return nextAssignments;
  }

  /**
   * Computes potential closest-point matches for the given stop-point that might make good
   * candidates for shape matches.
   *
   * <p>Returns an empty list if there was a problem computing potential matches.
   */
  private List<StopToShapeMatch> computePotentialMatchesUsingGeoDistance(
      ShapePoints shapePoints, StopPoint stopPoint, List<Problem> problems) {
    final double maxDistance =
        settings.getMaxDistanceFromStopToShapeInMeters()
            * (stopPoint.isLargeStation() ? settings.getLargeStationDistanceMultiplier() : 1);
    List<StopToShapeMatch> matchesForStop =
        shapePoints.matchesFromLocation(stopPoint.getLocation(), maxDistance);
    if (matchesForStop.isEmpty()) {
      final StopToShapeMatch match = shapePoints.matchFromLocation(stopPoint.getLocation());
      if (match.getGeoDistanceToShape() > maxDistance) {
        problems.add(Problem.createStopTooFarFromShapeProblem(stopPoint.getStopTime(), match));
      }
      return matchesForStop;
    }
    if (matchesForStop.size() > settings.getPotentialMatchesForStopProblemThreshold()) {
      problems.add(
          Problem.createStopHasTooManyMatchesProblem(
              stopPoint.getStopTime(),
              Collections.min(
                  matchesForStop, Comparator.comparing(StopToShapeMatch::getGeoDistanceToShape)),
              matchesForStop.size()));
    }
    return matchesForStop;
  }

  /** Checks for errors in the match, such as stops that are too far from the shape. */
  private boolean isValidStopsToShapeMatchFromUserDistance(
      StopPoints stopPoints, List<StopToShapeMatch> matches, List<Problem> problems) {
    boolean valid = true;
    for (int i = 0; i < matches.size(); ++i) {
      final StopPoints.StopPoint stopPoint = stopPoints.get(i);
      final StopToShapeMatch match = matches.get(i);
      if (match.getGeoDistanceToShape() > settings.getMaxDistanceFromStopToShapeInMeters()) {
        problems.add(Problem.createStopTooFarFromShapeProblem(stopPoint.getStopTime(), match));
        valid = false;
      }
    }
    return valid;
  }

  /** Represents result of stops-to-shape matching. */
  public static class MatchResult {

    private final List<StopToShapeMatch> matches;
    private final List<Problem> problems;

    public MatchResult(List<StopToShapeMatch> matches, List<Problem> problems) {
      this.matches = matches;
      this.problems = problems;
    }

    /**
     * Returns a list of matches, one per stop.
     *
     * <p>If matching failed, returns an empty list.
     */
    public List<StopToShapeMatch> getMatches() {
      return matches;
    }

    /** Returns a list of encountered problems. */
    public List<Problem> getProblems() {
      return problems;
    }
  }
}
