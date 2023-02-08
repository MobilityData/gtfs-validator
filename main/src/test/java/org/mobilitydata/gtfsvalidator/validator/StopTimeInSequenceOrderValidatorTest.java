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
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.validator.StopTimeInSequenceOrderValidator.TripWithOutOfOrderArrivalTimeNotice;
import org.mobilitydata.gtfsvalidator.validator.StopTimeInSequenceOrderValidator.TripWithOutOfOrderDepartureTimeNotice;
import org.mobilitydata.gtfsvalidator.validator.StopTimeInSequenceOrderValidator.TripWithOutOfOrderShapeDistTraveledNotice;

@RunWith(JUnit4.class)
public final class StopTimeInSequenceOrderValidatorTest {
  private static List<ValidationNotice> generateNotices(List<GtfsStopTime> stopTimes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new StopTimeInSequenceOrderValidator(
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void outOfOrder_yieldsNotice() {
    ImmutableList<GtfsStopTime> stopTimes =
        ImmutableList.of(
            new GtfsStopTime.Builder()
                .setCsvRowNumber(2)
                .setTripId("trip1")
                .setStopSequence(1)
                .setDepartureTime(GtfsTime.fromString("08:00:00"))
                .setArrivalTime(GtfsTime.fromString("08:00:00"))
                .setShapeDistTraveled(10.0)
                .build(),
            new GtfsStopTime.Builder()
                .setCsvRowNumber(3)
                .setTripId("trip1")
                .setStopSequence(2)
                .setDepartureTime(GtfsTime.fromString("06:00:00"))
                .setArrivalTime(GtfsTime.fromString("06:00:00"))
                .setShapeDistTraveled(5.0)
                .build());
    assertThat(generateNotices(stopTimes))
        .containsExactly(
            new TripWithOutOfOrderArrivalTimeNotice(stopTimes.get(1)),
            new TripWithOutOfOrderDepartureTimeNotice(stopTimes.get(1)),
            new TripWithOutOfOrderShapeDistTraveledNotice(stopTimes.get(1)));
  }

  @Test
  public void inOrder_yieldsNoNotice() {
    ImmutableList<GtfsStopTime> stopTimes =
        ImmutableList.of(
            new GtfsStopTime.Builder()
                .setCsvRowNumber(2)
                .setTripId("trip1")
                .setStopSequence(1)
                .setDepartureTime(GtfsTime.fromString("08:00:00"))
                .setArrivalTime(GtfsTime.fromString("08:00:00"))
                .setShapeDistTraveled(10.0)
                .build(),
            new GtfsStopTime.Builder()
                .setCsvRowNumber(3)
                .setTripId("trip1")
                .setStopSequence(2)
                .setDepartureTime(GtfsTime.fromString("09:00:00"))
                .setArrivalTime(GtfsTime.fromString("09:00:00"))
                .setShapeDistTraveled(20.0)
                .build());
    assertThat(generateNotices(stopTimes)).isEmpty();
  }

  @Test
  public void skippedValues_yieldsNoNotice() {
    ImmutableList<GtfsStopTime> stopTimes =
        ImmutableList.of(
            new GtfsStopTime.Builder()
                .setCsvRowNumber(2)
                .setTripId("trip1")
                .setStopSequence(1)
                .setDepartureTime(GtfsTime.fromString("08:00:00"))
                .setArrivalTime(GtfsTime.fromString("08:00:00"))
                .setShapeDistTraveled(10.0)
                .build(),
            new GtfsStopTime.Builder()
                .setCsvRowNumber(3)
                .setTripId("trip1")
                .setStopSequence(2)
                .build(),
            new GtfsStopTime.Builder()
                .setCsvRowNumber(4)
                .setTripId("trip1")
                .setStopSequence(3)
                .setDepartureTime(GtfsTime.fromString("09:00:00"))
                .setArrivalTime(GtfsTime.fromString("09:00:00"))
                .setShapeDistTraveled(20.0)
                .build());
    assertThat(generateNotices(stopTimes)).isEmpty();
  }
}
