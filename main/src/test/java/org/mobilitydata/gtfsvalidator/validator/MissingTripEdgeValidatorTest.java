/*
 * Copyright 2021 MobilityData IO
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
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.validator.MissingTripEdgeValidator.MissingTripEdgeNotice;

public class MissingTripEdgeValidatorTest {
  public static GtfsStopTime createStopTime(
      long csvRowNumber,
      String tripId,
      GtfsTime arrivalTime,
      GtfsTime departureTime,
      int stopSequence) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId(tripId)
        .setArrivalTime(arrivalTime)
        .setDepartureTime(departureTime)
        .setStopSequence(stopSequence)
        .setStopId("stop id")
        .build();
  }

  public static GtfsTrip createTrip(long csvRowNumber, String tripId) {
    return new GtfsTrip.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setRouteId("route id value")
        .setTripId(tripId)
        .setServiceId("service id value")
        .build();
  }

  private static List<ValidationNotice> generateNotices(List<GtfsStopTime> stopTimes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new MissingTripEdgeValidator(GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void tripWithFirstStopMissingArrivalTimeShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStopTime(
                        2, "trip id value", null, GtfsTime.fromSecondsSinceMidnight(23), 1),
                    createStopTime(4, "trip id value", null, null, 2),
                    createStopTime(5, "trip id value", null, null, 3),
                    createStopTime(
                        3,
                        "trip id value",
                        GtfsTime.fromSecondsSinceMidnight(28),
                        GtfsTime.fromSecondsSinceMidnight(35),
                        4))))
        .containsExactly(new MissingTripEdgeNotice(2, 1, "trip id value", "arrival_time"));
  }

  @Test
  public void tripWithFirstStopMissingDepartureTimeShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStopTime(
                        2, "trip id value", GtfsTime.fromSecondsSinceMidnight(23), null, 1),
                    createStopTime(4, "trip id value", null, null, 2),
                    createStopTime(5, "trip id value", null, null, 3),
                    createStopTime(
                        3,
                        "trip id value",
                        GtfsTime.fromSecondsSinceMidnight(28),
                        GtfsTime.fromSecondsSinceMidnight(35),
                        5))))
        .containsExactly(new MissingTripEdgeNotice(2, 1, "trip id value", "departure_time"));
  }

  @Test
  public void tripWithLastStopMissingArrivalTimeShouldGenerateNotices() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStopTime(
                        2,
                        "trip id value",
                        GtfsTime.fromSecondsSinceMidnight(23),
                        GtfsTime.fromSecondsSinceMidnight(46),
                        1),
                    createStopTime(4, "trip id value", null, null, 2),
                    createStopTime(5, "trip id value", null, null, 3),
                    createStopTime(
                        10, "trip id value", null, GtfsTime.fromSecondsSinceMidnight(456), 5))))
        .containsExactly(new MissingTripEdgeNotice(10, 5, "trip id value", "arrival_time"));
  }

  @Test
  public void tripWithLastStopMissingDepartureTimeShouldGenerateNotices() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStopTime(
                        2,
                        "trip id value",
                        GtfsTime.fromSecondsSinceMidnight(23),
                        GtfsTime.fromSecondsSinceMidnight(46),
                        1),
                    createStopTime(4, "trip id value", null, null, 2),
                    createStopTime(5, "trip id value", null, null, 3),
                    createStopTime(
                        10, "trip id value", GtfsTime.fromSecondsSinceMidnight(456), null, 5))))
        .containsExactly(new MissingTripEdgeNotice(10, 5, "trip id value", "departure_time"));
  }

  @Test
  public void tripWithValidEdgesShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStopTime(
                        2,
                        "trip id value",
                        GtfsTime.fromSecondsSinceMidnight(23),
                        GtfsTime.fromSecondsSinceMidnight(46),
                        1),
                    createStopTime(4, "trip id value", null, null, 2),
                    createStopTime(5, "trip id value", null, null, 3),
                    createStopTime(
                        10,
                        "trip id value",
                        GtfsTime.fromSecondsSinceMidnight(456),
                        GtfsTime.fromSecondsSinceMidnight(3556467),
                        4))))
        .isEmpty();
  }
}
