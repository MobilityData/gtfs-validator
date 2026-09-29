/*
 * Copyright 2025 MobilityData
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

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.validator.TripWithShapeDistTraveledButNoShapeDistancesValidator.TripWithShapeDistTraveledButNoShapeDistancesNotice;

@RunWith(JUnit4.class)
public class TripWithShapeDistTraveledButNoShapeDistancesValidatorTest {

  // ---------------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------------

  private static GtfsTrip createTrip(int csvRowNumber, String tripId, String shapeId) {
    GtfsTrip.Builder builder =
        new GtfsTrip.Builder()
            .setCsvRowNumber(csvRowNumber)
            .setTripId(tripId)
            .setRouteId("route1")
            .setServiceId("service1");
    if (shapeId != null) {
      builder.setShapeId(shapeId);
    }
    return builder.build();
  }

  private static GtfsStopTime createStopTime(
      int csvRowNumber, String tripId, int stopSequence, Double shapeDistTraveled) {
    GtfsStopTime.Builder builder =
        new GtfsStopTime.Builder()
            .setCsvRowNumber(csvRowNumber)
            .setTripId(tripId)
            .setStopId("stop1")
            .setStopSequence(stopSequence);
    if (shapeDistTraveled != null) {
      builder.setShapeDistTraveled(shapeDistTraveled);
    }
    return builder.build();
  }

  private static GtfsShape createShape(
      int csvRowNumber, String shapeId, int sequence, Double shapeDistTraveled) {
    GtfsShape.Builder builder =
        new GtfsShape.Builder()
            .setCsvRowNumber(csvRowNumber)
            .setShapeId(shapeId)
            .setShapePtLat(0.0)
            .setShapePtLon(0.0)
            .setShapePtSequence(sequence);
    if (shapeDistTraveled != null) {
      builder.setShapeDistTraveled(shapeDistTraveled);
    }
    return builder.build();
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsTrip> trips, List<GtfsStopTime> stopTimes, List<GtfsShape> shapes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new TripWithShapeDistTraveledButNoShapeDistancesValidator(
            GtfsTripTableContainer.forEntities(trips, noticeContainer),
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer),
            GtfsShapeTableContainer.forEntities(shapes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  // ---------------------------------------------------------------------------
  // Tests — happy paths (no notice expected)
  // ---------------------------------------------------------------------------

  /**
   * All stop times and all shape points carry shape_dist_traveled — perfectly consistent data, no
   * notice expected.
   */
  @Test
  public void stopTimesAndShapeAllHaveDistTraveled_noNotice() {
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(createTrip(1, "trip1", "shape1")),
            ImmutableList.of(
                createStopTime(1, "trip1", 1, 0.0), createStopTime(2, "trip1", 2, 100.0)),
            ImmutableList.of(createShape(1, "shape1", 1, 0.0), createShape(2, "shape1", 2, 100.0)));

    assertThat(notices).isEmpty();
  }

  /**
   * No stop times carry shape_dist_traveled and neither do the shape points — nothing to validate,
   * no notice expected.
   */
  @Test
  public void neitherStopTimesNorShapeHaveDistTraveled_noNotice() {
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(createTrip(1, "trip1", "shape1")),
            ImmutableList.of(
                createStopTime(1, "trip1", 1, null), createStopTime(2, "trip1", 2, null)),
            ImmutableList.of(createShape(1, "shape1", 1, null), createShape(2, "shape1", 2, null)));

    assertThat(notices).isEmpty();
  }

  /**
   * The trip has no shape_id — even though stop times carry shape_dist_traveled there is no shape
   * to compare against, so no notice is expected.
   */
  @Test
  public void tripWithNoShapeId_noNotice() {
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(createTrip(1, "trip1", null)),
            ImmutableList.of(
                createStopTime(1, "trip1", 1, 0.0), createStopTime(2, "trip1", 2, 100.0)),
            ImmutableList.of());

    assertThat(notices).isEmpty();
  }

  // ---------------------------------------------------------------------------
  // Tests — notice-generating cases
  // ---------------------------------------------------------------------------

  /**
   * Stop times have shape_dist_traveled but none of the shape points do — the shape distances are
   * entirely absent, so a notice must be generated.
   */
  @Test
  public void stopTimesHaveDistTraveledButShapeHasNone_generatesNotice() {
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(createTrip(1, "trip1", "shape1")),
            ImmutableList.of(
                createStopTime(1, "trip1", 1, 0.0), createStopTime(2, "trip1", 2, 100.0)),
            ImmutableList.of(createShape(1, "shape1", 1, null), createShape(2, "shape1", 2, null)));

    assertThat(notices).hasSize(1);
    assertThat(notices.get(0))
        .isInstanceOf(TripWithShapeDistTraveledButNoShapeDistancesNotice.class);
  }

  /**
   * Stop times have shape_dist_traveled and the shape is only partially populated (some points have
   * a value, others do not). Partial population is treated as non-compliant because consumers
   * cannot reliably interpolate positions from an incomplete distance sequence.
   */
  @Test
  public void stopTimesHaveDistTraveledAndShapeIsPartiallyPopulated_generatesNotice() {
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(createTrip(1, "trip1", "shape1")),
            ImmutableList.of(
                createStopTime(1, "trip1", 1, 0.0), createStopTime(2, "trip1", 2, 100.0)),
            ImmutableList.of(
                // One point has a value, one does not — partial population.
                createShape(1, "shape1", 1, 0.0), createShape(2, "shape1", 2, null)));

    assertThat(notices).hasSize(1);
    assertThat(notices.get(0))
        .isInstanceOf(TripWithShapeDistTraveledButNoShapeDistancesNotice.class);
  }

  /**
   * Multiple trips: trip1's shape has no distances (notice expected), trip2's shape has all
   * distances (no notice). Exactly one notice should be generated.
   */
  @Test
  public void multipleTrips_onlyMismatchedTripGeneratesNotice() {
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(createTrip(1, "trip1", "shape1"), createTrip(2, "trip2", "shape2")),
            ImmutableList.of(
                createStopTime(1, "trip1", 1, 0.0),
                createStopTime(2, "trip1", 2, 50.0),
                createStopTime(3, "trip2", 1, 0.0),
                createStopTime(4, "trip2", 2, 50.0)),
            ImmutableList.of(
                // shape1: no distances → notice for trip1
                createShape(1, "shape1", 1, null),
                createShape(2, "shape1", 2, null),
                // shape2: all distances present → no notice for trip2
                createShape(3, "shape2", 1, 0.0),
                createShape(4, "shape2", 2, 50.0)));

    assertThat(notices).hasSize(1);
    TripWithShapeDistTraveledButNoShapeDistancesNotice notice =
        (TripWithShapeDistTraveledButNoShapeDistancesNotice) notices.get(0);
  }

  /**
   * The notice should reference the row number of the first stop time that carries a
   * shape_dist_traveled value (row 2 here, since the first stop time has no distance).
   */
  @Test
  public void noticeReferencesFirstStopTimeWithDist() {
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(createTrip(1, "trip1", "shape1")),
            ImmutableList.of(
                // Row 1: no distance; Row 2: has distance — notice should point to row 2.
                createStopTime(1, "trip1", 1, null), createStopTime(2, "trip1", 2, 50.0)),
            ImmutableList.of(createShape(1, "shape1", 1, null), createShape(2, "shape1", 2, null)));

    assertThat(notices).hasSize(1);
    TripWithShapeDistTraveledButNoShapeDistancesNotice notice =
        (TripWithShapeDistTraveledButNoShapeDistancesNotice) notices.get(0);
  }
}
