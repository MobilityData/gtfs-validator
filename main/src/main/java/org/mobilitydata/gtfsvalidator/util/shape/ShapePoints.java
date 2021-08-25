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
import com.google.common.geometry.S2;
import com.google.common.geometry.S2EdgeUtil;
import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2Point;
import java.util.ArrayList;
import java.util.List;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.util.MathUtil;

/**
 * Models a GTFS shape, which is a sequence of lat/lng points, annotated with distances.
 *
 * <p>Each point has two distance values.
 *
 * <ul>
 *   <li>A "user distance", an arbitrary (positive and non-decreasing) parameterization passed in
 *       with each point as {@code shape_dist_traveled}.
 *   <li>A "geo distance" measuring the distance along the shape on the earth's surface by starting
 *       at the first point, adding the straight-line (or rather, great circle) distance for each
 *       pair of points.
 * </ul>
 */
public class ShapePoints {

  private final List<ShapePoint> points;

  public ShapePoints(List<ShapePoint> points) {
    this.points = points;
  }

  public static ShapePoints fromGtfsShape(List<GtfsShape> gtfsPoints) {
    List<ShapePoint> points = new ArrayList<>(gtfsPoints.size());
    double geoDistance = 0.0;
    double userDistance = 0.0;
    for (int i = 0; i < gtfsPoints.size(); ++i) {
      GtfsShape currPoint = gtfsPoints.get(i);
      if (i > 0) {
        geoDistance +=
            Math.max(
                0.0,
                getDistanceMeters(
                    gtfsPoints.get(i - 1).shapePtLatLon(), currPoint.shapePtLatLon()));
      }
      userDistance = Math.max(userDistance, currPoint.shapeDistTraveled());
      points.add(new ShapePoint(geoDistance, userDistance, currPoint.shapePtLatLon().toPoint()));
    }
    return new ShapePoints(points);
  }

  public List<ShapePoint> getPoints() {
    return points;
  }

  public boolean hasUserDistance() {
    return !points.isEmpty() && Iterables.getLast(points).hasUserDistance();
  }

  /** Tells if there are no points. */
  public boolean isEmpty() {
    return points.isEmpty();
  }

  /** Matches the closest location on the shape for the specified geo location. */
  public StopToShapeMatch matchFromLocation(S2Point location) {
    StopToShapeMatch match = new StopToShapeMatch();

    // Special case when a shape only has a single point
    if (points.size() == 1) {
      final S2Point closestPoint = points.get(0).location;
      match.keepBestMatch(closestPoint, getDistanceMeters(location, closestPoint), 0);
    }

    for (int i = 0; i + 1 < points.size(); ++i) {
      final ShapePoint left = points.get(i);
      final ShapePoint right = points.get(i + 1);
      final S2Point closestPoint =
          S2EdgeUtil.getClosestPoint(location, left.location, right.location);
      match.keepBestMatch(closestPoint, getDistanceMeters(location, closestPoint), i);
    }
    if (match.hasBestMatch()) {
      fillLocationMatch(match);
    }
    return match;
  }

  /**
   * Matches the best shape point location with the specified "user distance".
   *
   * <p>The search for a match starts from the specified shape point index.
   */
  public StopToShapeMatch matchFromUserDist(double userDist, int startIndex, S2Point stopLocation) {
    return interpolate(getVertexDistFromUserDist(userDist, startIndex), stopLocation);
  }

  /**
   * Matches the best shape location(s) for the specified geo location.
   *
   * <p>Returns an empty list if no matches were found within the specified max distance from the
   * shape.
   */
  public List<StopToShapeMatch> matchesFromLocation(S2Point location, double maxDistanceFromShape) {
    List<StopToShapeMatch> matches = new ArrayList<>();
    StopToShapeMatch localMatch = new StopToShapeMatch();

    // When a stop is near a complex portion of a shape (loops, lots of turns,
    // etc), there will always be one "closest" match, but there may be multiple
    // "close enough" matches that we need to consider, especially when agencies
    // are a bit sloppy with their shapes and station locations.  As such, our
    // matching algorithm looks for all local minimums in the distance between
    // the stop and the shape when those minimums are within our
    // maxDistanceFromShape threshold.
    //
    // We achieve this by keeping our best localMatch at all times.  If we
    // detect an inflection point where the shape moved away and then came back
    // in between our previous match and current match, we store the previous
    // best localMatch to the matches list and reset the localMatch.
    double distanceToEndOfPreviousSegment = Double.POSITIVE_INFINITY;
    boolean previousSegmentGettingFurtherAway = false;

    for (int i = 0; i + 1 < points.size(); ++i) {
      final ShapePoint left = points.get(i);
      final ShapePoint right = points.get(i + 1);
      final S2Point closestPoint =
          S2EdgeUtil.getClosestPoint(location, left.location, right.location);
      final double geoDistanceToShape = getDistanceMeters(location, closestPoint);

      if (geoDistanceToShape <= maxDistanceFromShape) {
        // If the previous segment was getting further away from the stop but our
        // current segment is getting closer, we save the previous local minimum
        // match and reset the match.
        if (previousSegmentGettingFurtherAway
            && geoDistanceToShape < distanceToEndOfPreviousSegment
            && localMatch.hasBestMatch()) {
          matches.add(new StopToShapeMatch(localMatch));
          localMatch.clearBestMatch();
        }
        // We are within the minimum distance threshold, so track the current
        // best local match.
        localMatch.keepBestMatch(closestPoint, geoDistanceToShape, i);
      } else if (localMatch.hasBestMatch()) {
        // We had a good match from the stop to the shape, but the shape is now
        // moving away from the stop, so add the local match to the set of
        // possible matches.
        matches.add(new StopToShapeMatch(localMatch));
        localMatch.clearBestMatch();
      }

      distanceToEndOfPreviousSegment = getDistanceMeters(location, right.location);
      previousSegmentGettingFurtherAway = distanceToEndOfPreviousSegment > geoDistanceToShape;
    }

    // Add any pending local match to the list of potential matches.
    if (localMatch.hasBestMatch()) {
      matches.add(localMatch);
    }

    // Update geoDistance for matches.
    for (int i = 0; i < matches.size(); ++i) {
      fillLocationMatch(matches.get(i));
    }

    return matches;
  }

  /**
   * Finds the best point along the shape with the specified user distance value.
   *
   * <p>The search is started from the shape point with the specified index.
   */
  private VertexDist getVertexDistFromUserDist(double userDist, int startIndex) {
    int previousIndex = startIndex;
    int nextIndex;
    for (nextIndex = startIndex;
        nextIndex < points.size() && userDist >= points.get(nextIndex).userDistance;
        ++nextIndex) {
      previousIndex = nextIndex;
    }
    // If point hits outside the bounds of the shape, return 0 for fraction.
    if (nextIndex <= 0 || previousIndex + 1 >= points.size()) {
      return new VertexDist(previousIndex, 0.0);
    }
    // Cut hits between two vertices, calculate at what fraction.
    final double prevDistance = points.get(previousIndex).userDistance;
    final double nextDistance = points.get(nextIndex).userDistance;
    // If the vertices are very close, we might get too much numerical instability, just return 0
    // for fraction.
    if (MathUtil.nearByFractionOrMargin(prevDistance, nextDistance)) {
      return new VertexDist(previousIndex, 0.0);
    }
    return new VertexDist(previousIndex, (userDist - prevDistance) / (nextDistance - prevDistance));
  }

  /**
   * Generates a StopToShapeMatch for the given shape location specified by the VertexDist.
   *
   * <p>Note that geoDistanceToShape will NOT be set for the match.
   */
  private StopToShapeMatch interpolate(VertexDist vertexDist, S2Point stopLocation) {
    final int previousIndex = vertexDist.index;
    final ShapePoint previousPoint = points.get(previousIndex);
    final ShapePoint nextPoint =
        previousIndex + 1 == points.size() ? previousPoint : points.get(previousIndex + 1);
    final double fraction = vertexDist.fraction;
    // The s2 interpolation doesn't work if the points are identical.
    final S2Point matchLocation =
        S2.approxEquals(previousPoint.location, nextPoint.location)
            ? previousPoint.location
            : S2EdgeUtil.interpolate(fraction, previousPoint.location, nextPoint.location);
    return new StopToShapeMatch(
        previousIndex,
        previousPoint.userDistance
            + fraction * (nextPoint.userDistance - previousPoint.userDistance),
        previousPoint.geoDistance + fraction * (nextPoint.geoDistance - previousPoint.geoDistance),
        getDistanceMeters(stopLocation, matchLocation),
        matchLocation);
  }

  /** Fills out additional fields, like {@code geoDistance}, for a location match. */
  private void fillLocationMatch(StopToShapeMatch match) {
    ShapePoint shapePoint = points.get(match.getIndex());
    match.setGeoDistance(
        shapePoint.geoDistance + getDistanceMeters(match.getLocation(), shapePoint.location));
    match.setUserDistance(0.0);
  }

  /** Describes a single point of a shape. */
  public static class ShapePoint {

    private final double geoDistance;
    private final double userDistance;
    private final S2Point location;

    public ShapePoint(double geoDistance, double userDistance, S2Point location) {
      this.geoDistance = geoDistance;
      this.userDistance = userDistance;
      this.location = location;
    }

    boolean hasUserDistance() {
      return userDistance > 0.0;
    }

    public S2LatLng getLocationLatLng() {
      return new S2LatLng(location);
    }

    @Override
    public String toString() {
      return "ShapePoint{"
          + "geoDistance="
          + geoDistance
          + ", userDistance="
          + userDistance
          + ", location="
          + location
          + '}';
    }

    public boolean approxEquals(ShapePoint that, double maxError) {
      return Math.abs(this.userDistance - that.userDistance) < maxError
          && Math.abs(this.geoDistance - that.geoDistance) < maxError
          && this.getLocationLatLng().approxEquals(that.getLocationLatLng(), maxError);
    }
  }

  /**
   * A representation used internally for a point along the shape, at {@code fraction} between
   * {@code vertex[index]} and {@code vertex[index + 1]}.
   */
  private static class VertexDist {

    /** The shape point index. */
    final int index;
    /** The fraction of distance between the index shape point and the next point. */
    final double fraction;

    public VertexDist(int index, double fraction) {
      this.index = index;
      this.fraction = fraction;
    }
  }
}
