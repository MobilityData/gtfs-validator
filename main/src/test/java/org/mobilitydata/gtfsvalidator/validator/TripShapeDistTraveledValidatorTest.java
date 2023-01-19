/*
 * Copyright 2023 Google LLC
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
import org.mobilitydata.gtfsvalidator.validator.TripShapeDistTraveledValidator.TripWithPartialShapeDistTraveledNotice;
import org.mobilitydata.gtfsvalidator.validator.TripShapeDistTraveledValidator.TripWithShapeDistTraveledButNoShapeDistancesNotice;
import org.mobilitydata.gtfsvalidator.validator.TripShapeDistTraveledValidator.TripWithShapeDistTraveledButNoShapeNotice;

@RunWith(JUnit4.class)
public final class TripShapeDistTraveledValidatorTest {
  private static List<ValidationNotice> generateNotices(
      List<GtfsTrip> trips, List<GtfsStopTime> stopTimes, List<GtfsShape> shapes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new TripShapeDistTraveledValidator(GtfsTripTableContainer.forEntities(trips, noticeContainer),
        GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer),
        GtfsShapeTableContainer.forEntities(shapes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void tripWithShapeDistTraveledButNoShape_yieldsNotice() {
    GtfsTrip trip = new GtfsTrip.Builder().setCsvRowNumber(2).setTripId("trip1").build();

    assertThat(generateNotices(ImmutableList.of(trip),
                   ImmutableList.of(new GtfsStopTime.Builder()
                                        .setTripId(trip.tripId())
                                        .setStopSequence(1)
                                        .setShapeDistTraveled(1.0)
                                        .build()),
                   ImmutableList.of()))
        .containsExactly(new TripWithShapeDistTraveledButNoShapeNotice(trip));
  }

  @Test
  public void tripWithShapeDistTraveledButNoShapeDistances_yieldsNotice() {
    GtfsTrip trip =
        new GtfsTrip.Builder().setCsvRowNumber(2).setTripId("trip1").setShapeId("shape1").build();

    assertThat(
        generateNotices(ImmutableList.of(trip),
            ImmutableList.of(new GtfsStopTime.Builder()
                                 .setCsvRowNumber(2)
                                 .setTripId(trip.tripId())
                                 .setStopSequence(1)
                                 .setShapeDistTraveled(1.0)
                                 .build()),
            ImmutableList.of(
                new GtfsShape.Builder().setCsvRowNumber(2).setShapeId(trip.shapeId()).build())))
        .containsExactly(new TripWithShapeDistTraveledButNoShapeDistancesNotice(trip));
  }

  @Test
  public void tripWithPartialShapeDistTraveled_yieldsNotice() {
    GtfsTrip trip =
        new GtfsTrip.Builder().setCsvRowNumber(2).setTripId("trip1").setShapeId("shape1").build();

    assertThat(generateNotices(ImmutableList.of(trip),
                   ImmutableList.of(new GtfsStopTime.Builder()
                                        .setCsvRowNumber(2)
                                        .setTripId(trip.tripId())
                                        .setStopSequence(1)
                                        .setShapeDistTraveled(1.0)
                                        .build(),
                       new GtfsStopTime.Builder()
                           .setCsvRowNumber(3)
                           .setTripId(trip.tripId())
                           .setStopSequence(2)
                           .build()),
                   ImmutableList.of(new GtfsShape.Builder()
                                        .setCsvRowNumber(2)
                                        .setShapeId(trip.shapeId())
                                        .setShapeDistTraveled(1.0)
                                        .build())))
        .containsExactly(new TripWithPartialShapeDistTraveledNotice(trip));
  }

  @Test
  public void tripNoShapeDistTraveled_yieldsNoNotice() {
    GtfsTrip trip =
        new GtfsTrip.Builder().setCsvRowNumber(2).setTripId("trip1").setShapeId("shape1").build();

    assertThat(
        generateNotices(ImmutableList.of(trip),
            ImmutableList.of(new GtfsStopTime.Builder()
                                 .setCsvRowNumber(2)
                                 .setTripId(trip.tripId())
                                 .setStopSequence(1)
                                 .build()),
            ImmutableList.of(
                new GtfsShape.Builder().setCsvRowNumber(2).setShapeId(trip.shapeId()).build())))
        .isEmpty();
  }

  @Test
  public void tripWithFullShapeDistTraveled_yieldsNoNotice() {
    GtfsTrip trip =
        new GtfsTrip.Builder().setCsvRowNumber(2).setTripId("trip1").setShapeId("shape1").build();

    assertThat(generateNotices(ImmutableList.of(trip),
                   ImmutableList.of(new GtfsStopTime.Builder()
                                        .setCsvRowNumber(2)
                                        .setTripId(trip.tripId())
                                        .setStopSequence(1)
                                        .setShapeDistTraveled(1.0)
                                        .build(),
                       new GtfsStopTime.Builder()
                           .setCsvRowNumber(3)
                           .setTripId(trip.tripId())
                           .setStopSequence(2)
                           .setShapeDistTraveled(2.0)
                           .build()),
                   ImmutableList.of(new GtfsShape.Builder()
                                        .setCsvRowNumber(2)
                                        .setShapeId(trip.shapeId())
                                        .setShapeDistTraveled(1.0)
                                        .build())))
        .isEmpty();
  }

  @Test
  public void tripWithMissingFirstShapeDistTraveled_yieldsNoNotice() {
    GtfsTrip trip =
        new GtfsTrip.Builder().setCsvRowNumber(2).setTripId("trip1").setShapeId("shape1").build();

    assertThat(generateNotices(ImmutableList.of(trip),
                   ImmutableList.of(new GtfsStopTime.Builder()
                                        .setCsvRowNumber(2)
                                        .setTripId(trip.tripId())
                                        .setStopSequence(1)
                                        .build(),
                       new GtfsStopTime.Builder()
                           .setCsvRowNumber(3)
                           .setTripId(trip.tripId())
                           .setStopSequence(2)
                           .setShapeDistTraveled(2.0)
                           .build()),
                   ImmutableList.of(new GtfsShape.Builder()
                                        .setCsvRowNumber(2)
                                        .setShapeId(trip.shapeId())
                                        .setShapeDistTraveled(1.0)
                                        .build())))
        .isEmpty();
  }
}
