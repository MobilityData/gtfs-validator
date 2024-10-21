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

public class StopTimesShapeDistTraveledPresenceValidatorTest {
  public static GtfsStopTime createStopTime(
      int csvRowNumber,
      String tripId,
      String stopId,
      String locationGroupId,
      String locationId,
      int stopSequence,
      double shapeDistTraveled) {
    var builder =
        new GtfsStopTime.Builder()
            .setCsvRowNumber(csvRowNumber)
            .setTripId(tripId)
            .setStopSequence(stopSequence)
            .setShapeDistTraveled(shapeDistTraveled);
    if (stopId != null) {
      builder.setStopId(stopId);
    }
    if (locationGroupId != null) {
      builder.setLocationGroupId(locationGroupId);
    }
    if (locationId != null) {
      builder.setLocationId(locationId);
    }

    return builder.build();
  }

  private static List<ValidationNotice> generateNotices(List<GtfsStopTime> stopTimes) {
    NoticeContainer noticeContainer = new NoticeContainer();

    var validator = new StopTimesShapeDistTraveledPresenceValidator();
    for (var stopTime : stopTimes) {
      validator.validate(stopTime, noticeContainer);
    }
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void locationWithShapeDistanceShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStopTime(1, "first trip", null, "loc1", null, 2, 10.0d),
                    createStopTime(2, "first trip", null, null, "loc2", 42, 45.0d),
                    createStopTime(3, "first trip", "stop1", null, null, 46, 64.0d))))
        .containsExactly(
            new StopTimesShapeDistTraveledPresenceValidator.ForbiddenShapeDistTraveledNotice(
                1, "first trip", "loc1", "", 10.0d),
            new StopTimesShapeDistTraveledPresenceValidator.ForbiddenShapeDistTraveledNotice(
                2, "first trip", "", "loc2", 45.0d));
  }
}
