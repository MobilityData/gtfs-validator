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
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.util.shape.ShapePoints.ShapePoint;

@RunWith(JUnit4.class)
public class ShapePointsTest {
  @Rule public final Expect expect = Expect.create();

  private static final double DOUBLE_PRECISION = 0.1;

  private static final Correspondence<ShapePoint, ShapePoint> APPROX_SAME_POINT =
      Correspondence.from((p1, p2) -> p1.approxEquals(p2, DOUBLE_PRECISION), "same shape point");

  private static final Correspondence<StopToShapeMatch, StopToShapeMatch> APPROX_SAME_MATCH =
      Correspondence.from((m1, m2) -> m1.approxEquals(m2, DOUBLE_PRECISION), "same match");

  private static GtfsShape createGtfsShape(
      double latDegrees, double lngDegrees, Double shapeDistTraveled) {
    return new GtfsShape.Builder()
        .setShapePtLat(latDegrees)
        .setShapePtLon(lngDegrees)
        .setShapeDistTraveled(shapeDistTraveled)
        .build();
  }

  @Test
  public void fromGtfsShape_noUserDistance() {
    ImmutableList<GtfsShape> gtfsShapes =
        ImmutableList.of(
            createGtfsShape(47.3778615, 8.5381339, null),
            createGtfsShape(47.3771448, 8.5376943, null),
            createGtfsShape(47.3680534, 8.5361065, null));
    ShapePoints shapePoints = ShapePoints.fromGtfsShape(gtfsShapes);
    expect
        .that(shapePoints.getPoints())
        .comparingElementsUsing(APPROX_SAME_POINT)
        .containsExactly(
            new ShapePoint(0, 0, gtfsShapes.get(0).shapePtLatLon().toPoint()),
            new ShapePoint(86.294368, 0, gtfsShapes.get(1).shapePtLatLon().toPoint()),
            new ShapePoint(1104.2600400, 0, gtfsShapes.get(2).shapePtLatLon().toPoint()));
  }

  @Test
  public void fromGtfsShape_withUserDistance() {
    ImmutableList<GtfsShape> gtfsShapes =
        ImmutableList.of(
            createGtfsShape(47.3778615, 8.5381339, 0.0),
            createGtfsShape(47.3771448, 8.5376943, 86.0),
            createGtfsShape(47.3680534, 8.5361065, 1104.0));
    ShapePoints shapePoints = ShapePoints.fromGtfsShape(gtfsShapes);
    expect
        .that(shapePoints.getPoints())
        .comparingElementsUsing(APPROX_SAME_POINT)
        .containsExactly(
            new ShapePoint(0, 0, gtfsShapes.get(0).shapePtLatLon().toPoint()),
            new ShapePoint(86.294368, 86.0, gtfsShapes.get(1).shapePtLatLon().toPoint()),
            new ShapePoint(1104.2600400, 1104.0, gtfsShapes.get(2).shapePtLatLon().toPoint()));
  }

  @Test
  public void fromGtfsShape_withDecreasingUserDistance() {
    ImmutableList<GtfsShape> gtfsShapes =
        ImmutableList.of(
            createGtfsShape(47.3778615, 8.5381339, 0.0),
            createGtfsShape(47.3771448, 8.5376943, 86.0),
            createGtfsShape(47.3680534, 8.5361065, 70.0));
    ShapePoints shapePoints = ShapePoints.fromGtfsShape(gtfsShapes);
    expect
        .that(shapePoints.getPoints())
        .comparingElementsUsing(APPROX_SAME_POINT)
        .containsExactly(
            new ShapePoint(0, 0, gtfsShapes.get(0).shapePtLatLon().toPoint()),
            new ShapePoint(86.294368, 86.0, gtfsShapes.get(1).shapePtLatLon().toPoint()),
            new ShapePoint(1104.2600400, 86.0, gtfsShapes.get(2).shapePtLatLon().toPoint()));
  }

  @Test
  public void hasUserDistance() {
    expect.that(new ShapePoints(ImmutableList.of()).hasUserDistance()).isFalse();
    expect
        .that(
            new ShapePoints(ImmutableList.of(new ShapePoint(0, 0, new S2Point())))
                .hasUserDistance())
        .isFalse();
    expect
        .that(
            new ShapePoints(
                    ImmutableList.of(
                        new ShapePoint(0, 0, new S2Point()), new ShapePoint(1, 1, new S2Point())))
                .hasUserDistance())
        .isTrue();
    expect
        .that(
            new ShapePoints(
                    ImmutableList.of(
                        new ShapePoint(0, 0, new S2Point()),
                        new ShapePoint(1, 1, new S2Point()),
                        new ShapePoint(2, 0, new S2Point())))
                .hasUserDistance())
        .isFalse();
  }

  private static final ShapePoints TEST_SHAPE_POINTS =
      ShapePoints.fromGtfsShape(
          ImmutableList.of(
              createGtfsShape(47.365399, 8.525138, 0.0),
              createGtfsShape(47.366013, 8.524972, 1.0),
              createGtfsShape(47.366073, 8.525384, 2.0),
              createGtfsShape(47.364120, 8.525886, 3.0),
              createGtfsShape(47.364046, 8.525559, 4.0),
              createGtfsShape(47.364376, 8.525376, 5.0),
              createGtfsShape(47.364976, 8.525258, 6.0),
              createGtfsShape(47.365016, 8.525666, 7.0),
              createGtfsShape(47.365103, 8.525650, 8.0),
              createGtfsShape(47.365143, 8.526015, 9.0)));

  private static S2Point toS2Point(double lat, double lng) {
    return S2LatLng.fromDegrees(lat, lng).toPoint();
  }

  private void expectApproxEqual(StopToShapeMatch m1, StopToShapeMatch m2) {
    expect
        .withMessage(m1.toString() + " = " + m2.toString())
        .that(m1.approxEquals(m2, DOUBLE_PRECISION))
        .isTrue();
  }

  @Test
  public void matchFromUserDist() {

    expectApproxEqual(
        TEST_SHAPE_POINTS.matchFromUserDist(0.5, 0, toS2Point(47.365399, 8.525138)),
        new StopToShapeMatch(0, 0.5, 34.704520, 34.704520288, toS2Point(47.365706, 8.525055)));
    expectApproxEqual(
        TEST_SHAPE_POINTS.matchFromUserDist(1.0, 0, toS2Point(47.365399, 8.525138)),
        new StopToShapeMatch(1, 1.0, 69.4090405, 69.4090405, toS2Point(47.366013, 8.524972)));
    expectApproxEqual(
        TEST_SHAPE_POINTS.matchFromUserDist(2.6, 0, toS2Point(47.365399, 8.525138)),
        new StopToShapeMatch(2, 2.6, 233.405868, 69.010379, toS2Point(47.3649012, 8.525685)));
    expectApproxEqual(
        TEST_SHAPE_POINTS.matchFromUserDist(25.0, 0, toS2Point(47.365399, 8.525138)),
        new StopToShapeMatch(9, 9.0, 522.693893, 71.924027, toS2Point(47.365143, 8.526015)));
  }

  @Test
  public void matchFromLocation() {

    expectApproxEqual(
        TEST_SHAPE_POINTS.matchFromLocation(toS2Point(47.365728, 8.525080)),
        new StopToShapeMatch(0, 0, 36.771658, 2.292657, toS2Point(47.3657243, 8.5250501)));
  }

  @Test
  public void matchesFromLocation() {

    expect
        .that(TEST_SHAPE_POINTS.matchesFromLocation(toS2Point(47.365728, 8.525080), 20.0))
        .comparingElementsUsing(APPROX_SAME_MATCH)
        .containsExactly(new StopToShapeMatch(0, 0, 36.8, 2.3, toS2Point(47.3657243, 8.5250501)));
    expect
        .that(TEST_SHAPE_POINTS.matchesFromLocation(toS2Point(47.365992, 8.525013), 20.0))
        .comparingElementsUsing(APPROX_SAME_MATCH)
        .containsExactly(
            new StopToShapeMatch(0, 0.0, 66.5, 2.6, toS2Point(47.3659878, 8.5249788)),
            new StopToShapeMatch(1, 0.0, 71.9, 2.9, toS2Point(47.3660178, 8.5250048)));
    expect
        .that(TEST_SHAPE_POINTS.matchesFromLocation(toS2Point(47.365007, 8.525647), 20.0))
        .comparingElementsUsing(APPROX_SAME_MATCH)
        .containsExactly(
            new StopToShapeMatch(2, 0.0, 221.3, 0.8, toS2Point(47.3650083, 8.5256577)),
            new StopToShapeMatch(6, 0.0, 483.5, 0.8, toS2Point(47.3650140, 8.5256455)));
  }
}
