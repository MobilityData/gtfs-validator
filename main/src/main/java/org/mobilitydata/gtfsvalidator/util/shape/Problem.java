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

import javax.annotation.Nullable;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;

/** Specifies the detail of a stop-to-shape matching issue. */
public class Problem {

  private final ProblemType type;
  private final GtfsStopTime stopTime;
  private final StopToShapeMatch match;
  private final int matchCount;
  private final GtfsStopTime prevStopTime;
  private final StopToShapeMatch prevMatch;

  private Problem(
      ProblemType type,
      GtfsStopTime stopTime,
      StopToShapeMatch match,
      int matchCount,
      GtfsStopTime prevStopTime,
      StopToShapeMatch prevMatch) {
    this.type = type;
    this.stopTime = stopTime;
    this.match = match;
    this.matchCount = matchCount;
    this.prevStopTime = prevStopTime;
    this.prevMatch = prevMatch;
  }

  static Problem createStopTooFarFromShape(GtfsStopTime stopTime, StopToShapeMatch match) {
    return new Problem(ProblemType.STOP_TOO_FAR_FROM_SHAPE, stopTime, match, 0, null, null);
  }

  static Problem createStopHasTooManyMatches(
      GtfsStopTime stopTime, StopToShapeMatch match, int matchCount) {
    return new Problem(
        ProblemType.STOP_HAS_TOO_MANY_MATCHES, stopTime, match, matchCount, null, null);
  }

  static Problem createStopMatchOutOfOrder(
      GtfsStopTime stopTime,
      StopToShapeMatch match,
      GtfsStopTime prevStopTime,
      StopToShapeMatch prevMatch) {
    return new Problem(
        ProblemType.STOPS_MATCH_OUT_OF_ORDER, stopTime, match, 0, prevStopTime, prevMatch);
  }

  public ProblemType getType() {
    return type;
  }

  public GtfsStopTime getStopTime() {
    return stopTime;
  }

  public StopToShapeMatch getMatch() {
    return match;
  }

  public int getMatchCount() {
    return matchCount;
  }

  @Nullable
  public GtfsStopTime getPrevStopTime() {
    return prevStopTime;
  }

  @Nullable
  public StopToShapeMatch getPrevMatch() {
    return prevMatch;
  }

  @SuppressWarnings("ObjectToString")
  @Override
  public String toString() {
    return "Problem{"
        + "type="
        + type
        + ", stopTime="
        + stopTime
        + ", match="
        + match
        + ", matchCount="
        + matchCount
        + ", prevStopTime="
        + prevStopTime
        + ", prevMatch="
        + prevMatch
        + '}';
  }

  public boolean approxEquals(Problem that, double maxError) {
    return this.type.equals(that.type)
        && this.stopTime == that.stopTime
        && this.match.approxEquals(that.match, maxError)
        && this.matchCount == that.matchCount
        && this.prevStopTime == that.prevStopTime
        && ((this.prevMatch == null && that.prevMatch == null)
            || (this.prevMatch != null
                && that.prevMatch != null
                && this.prevMatch.approxEquals(that.prevMatch, maxError)));
  }
  /** Identifies particular types of stop-to-shape matching issues. */
  public enum ProblemType {
    /**
     * When a stop is more than our max threshold distance away from the shape. See
     * maxDistanceFromStopToShapeInMeters.
     */
    STOP_TOO_FAR_FROM_SHAPE,

    /**
     * When a stop has way too many nearby matches to the shape. See
     * potentialMatchesForStopProblemThreshold.
     */
    STOP_HAS_TOO_MANY_MATCHES,

    /** When two stops match the shape in a different order than their stop_sequence order. */
    STOPS_MATCH_OUT_OF_ORDER
  }
}
