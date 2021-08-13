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

/** Models a feasible assignment of potential StopToShapeMatch matches. */
class Assignment {

  /**
   * Indices in to a {@code List<List<StopToShapeMatch>>} list, identifying the assigned
   * StopToShapeMatch for each stop in the assignment.
   */
  private final ImmutableList<Integer> assignment;

  /* The sum of all geoDistanceToShape values for assigned matches. A lower score is better. */
  private final double score;

  /** The geoDistance value of the last StopToShapeMatch in the assignment. */
  private final double maxGeoDistance;

  public Assignment(ImmutableList<Integer> assignment, double score, double maxGeoDistance) {
    this.assignment = assignment;
    this.score = score;
    this.maxGeoDistance = maxGeoDistance;
  }

  public Assignment() {
    this(ImmutableList.of(), 0.0, 0.0);
  }

  public ImmutableList<Integer> getAssignment() {
    return assignment;
  }

  public double getScore() {
    return score;
  }

  public double getMaxGeoDistance() {
    return maxGeoDistance;
  }
}
