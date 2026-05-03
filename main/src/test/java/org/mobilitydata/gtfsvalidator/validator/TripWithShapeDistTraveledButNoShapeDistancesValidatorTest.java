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

  /**
   * Stop times have shape_dist_traveled and the shape also has shape_dist_traveled — no notice
   * expected.
   */
  @Test
  public void stopTimesAndShapeBothHaveDistTraveled_noNotice() {
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(createTrip(1, "trip1", "shape1")),
            ImmutableList.of(
                createStopTime(1, "trip1", 1, 0.0), createStopTime(2, "trip1", 2, 100.0)),
            ImmutableList.of(createShape(1, "shape1", 1, 0.0), createShape(2, "shape1", 2, 100.0)));

    assertThat(notices).isEmpty();
  }

  /** Stop times have shape_dist_traveled but the shape has none — a notice should be generated. */
  @Test
  public void stopTimesHaveDistTraveledButShapeDoesNot_generatesNotice() {
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

  /** Stop times have no shape_dist_traveled and neither does the shape — no notice expected. */
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
   * Trip has no shape_id — even if stop times have shape_dist_traveled, no notice expected because
   * there is no shape to compare against.
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

  /**
   * Only some shape points have shape_dist_traveled — since at least one shape point has a value,
   * no notice should be generated.
   */
  @Test
  public void someShapePointsHaveDistTraveled_noNotice() {
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(createTrip(1, "trip1", "shape1")),
            ImmutableList.of(
                createStopTime(1, "trip1", 1, 0.0), createStopTime(2, "trip1", 2, 100.0)),
            ImmutableList.of(
                createShape(1, "shape1", 1, null), createShape(2, "shape1", 2, 100.0)));

    assertThat(notices).isEmpty();
  }

  /** Multiple trips: one has the mismatch, the other does not — only one notice expected. */
  @Test
  public void multipleTrips_onlyMismatchedTripGeneratesNotice() {
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(createTrip(1, "trip1", "shape1"), createTrip(2, "trip2", "shape2")),
            ImmutableList.of(
                // trip1 stop times have dist, shape1 does not → notice
                createStopTime(1, "trip1", 1, 0.0),
                createStopTime(2, "trip1", 2, 50.0),
                // trip2 stop times have dist, shape2 also does → no notice
                createStopTime(3, "trip2", 1, 0.0),
                createStopTime(4, "trip2", 2, 50.0)),
            ImmutableList.of(
                createShape(1, "shape1", 1, null),
                createShape(2, "shape1", 2, null),
                createShape(3, "shape2", 1, 0.0),
                createShape(4, "shape2", 2, 50.0)));

    assertThat(notices).hasSize(1);
    assertThat(notices.get(0))
        .isInstanceOf(TripWithShapeDistTraveledButNoShapeDistancesNotice.class);
  }
}
