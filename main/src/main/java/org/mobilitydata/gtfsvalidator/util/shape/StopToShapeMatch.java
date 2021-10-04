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

import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2Point;
import org.mobilitydata.gtfsvalidator.util.S2Earth;

/** Describes how a stop is matched to a location on a shape. */
public class StopToShapeMatch {

  /** The index of the shape point in the shape sequence. */
  private int index = 0;
  /**
   * The user-specified distance from the start of the shape to the matched shape location. This
   * value will be zero if not specified in the data.
   */
  private double userDistance = 0.0;
  /** The geo distance from the start of the shape to the matched shape location. */
  private double geoDistance = 0.0;
  /** The geo distance from the stop to the matched shape location. */
  private double geoDistanceToShape = 0.0;
  /** The location of the best match on the shape for the stop. */
  private S2Point location = new S2Point();

  public StopToShapeMatch() {
    clearBestMatch();
  }

  public StopToShapeMatch(StopToShapeMatch that) {
    this.index = that.index;
    this.userDistance = that.userDistance;
    this.geoDistance = that.geoDistance;
    this.geoDistanceToShape = that.geoDistanceToShape;
    this.location = that.location;
  }

  public StopToShapeMatch(
      int index,
      double userDistance,
      double geoDistance,
      double geoDistanceToShape,
      S2Point location) {
    this.index = index;
    this.userDistance = userDistance;
    this.geoDistance = geoDistance;
    this.geoDistanceToShape = geoDistanceToShape;
    this.location = location;
  }

  public int getIndex() {
    return index;
  }

  public void setUserDistance(double userDistance) {
    this.userDistance = userDistance;
  }

  public double getGeoDistance() {
    return geoDistance;
  }

  public void setGeoDistance(double geoDistance) {
    this.geoDistance = geoDistance;
  }

  public double getGeoDistanceToShape() {
    return geoDistanceToShape;
  }

  public S2Point getLocation() {
    return location;
  }

  public S2LatLng getLocationLatLng() {
    return new S2LatLng(location);
  }

  @Override
  public String toString() {
    return "StopToShapeMatch{"
        + "index="
        + index
        + ", userDistance="
        + userDistance
        + ", geoDistance="
        + geoDistance
        + ", geoDistanceToShape="
        + geoDistanceToShape
        + ", location="
        + new S2LatLng(location).toStringDegrees()
        + '}';
  }

  public boolean approxEquals(StopToShapeMatch that, double maxError) {
    return this.index == that.index
        && Math.abs(this.userDistance - that.userDistance) < maxError
        && Math.abs(this.geoDistance - that.geoDistance) < maxError
        && Math.abs(this.geoDistanceToShape - that.geoDistanceToShape) < maxError
        && S2Earth.getDistanceMeters(this.location, that.location) < maxError;
  }
  /**
   * Sets geoDistanceToShape to +infinity.
   *
   * <p>After a call to {@code clearBestMatch()}, {@code hasBestMatch()} will return false.
   */
  public void clearBestMatch() {
    geoDistanceToShape = Double.POSITIVE_INFINITY;
  }

  /**
   * If the specified {@code geoDistanceToShape} is lower than the current one, then the values of
   * {@code this} will be updated with the specified {@code location}, {@code index} and {@code
   * geoDistanceToShape}.
   */
  public void keepBestMatch(S2Point location, double geoDistanceToShape, int index) {
    if (geoDistanceToShape < this.geoDistanceToShape) {
      this.geoDistanceToShape = geoDistanceToShape;
      this.location = location;
      this.index = index;
    }
  }

  /** Returns true if the match has been specified, indicating a best match is available. */
  public boolean hasBestMatch() {
    return geoDistanceToShape != Double.POSITIVE_INFINITY;
  }
}
