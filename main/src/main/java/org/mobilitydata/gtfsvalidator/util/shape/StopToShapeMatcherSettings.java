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

/** Thresholds for {@code StopToShapeMatcher}. */
public class StopToShapeMatcherSettings {

  public static final double DEFAULT_MAX_DISTANCE_FROM_STOP_TO_SHAPE_IN_METERS = 200.0;
  public static final double DEFAULT_LARGE_STATION_DISTANCE_MULTIPLIER = 4.0;
  public static final int DEFAULT_POTENTIAL_MATCHES_FOR_STOP_PROBLEM_THRESHOLD = 20;

  /**
   * A stop to shape matching will be considered invalid if the matched stop is more than the
   * specified distance from the shape.
   */
  private double maxDistanceFromStopToShapeInMeters =
      DEFAULT_MAX_DISTANCE_FROM_STOP_TO_SHAPE_IN_METERS;

  /**
   * A large station (e.g. main train station) requires a bigger threshold, therefore the
   * maxDistanceFromStopToShapeInMeters is multiplied by this multiplier.
   */
  private double largeStationDistanceMultiplier = DEFAULT_LARGE_STATION_DISTANCE_MULTIPLIER;

  /**
   * When computing potential matches for a stop, a problem will be generated if the number of
   * potential matches is greater than this threshold. Note that this doesn't necessarily mean a
   * match can't be found.
   */
  private int potentialMatchesForStopProblemThreshold =
      DEFAULT_POTENTIAL_MATCHES_FOR_STOP_PROBLEM_THRESHOLD;

  public double getMaxDistanceFromStopToShapeInMeters() {
    return maxDistanceFromStopToShapeInMeters;
  }

  public void setMaxDistanceFromStopToShapeInMeters(double maxDistanceFromStopToShapeInMeters) {
    this.maxDistanceFromStopToShapeInMeters = maxDistanceFromStopToShapeInMeters;
  }

  public double getLargeStationDistanceMultiplier() {
    return largeStationDistanceMultiplier;
  }

  public int getPotentialMatchesForStopProblemThreshold() {
    return potentialMatchesForStopProblemThreshold;
  }

  public void setPotentialMatchesForStopProblemThreshold(
      int potentialMatchesForStopProblemThreshold) {
    this.potentialMatchesForStopProblemThreshold = potentialMatchesForStopProblemThreshold;
  }
}
