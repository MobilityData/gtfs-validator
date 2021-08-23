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
import org.mobilitydata.gtfsvalidator.validator.StopTimeIncreasingDistanceValidator.DecreasingOrEqualStopTimeDistanceNotice;

public class StopTimeIncreasingDistanceValidatorTest {
  public static GtfsStopTime createStopTime(
      long csvRowNumber, String tripId, String stopId, int stopSequence, double shapeDistTraveled) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId(tripId)
        .setStopId(stopId)
        .setStopSequence(stopSequence)
        .setShapeDistTraveled(shapeDistTraveled)
        .build();
  }

  private static List<ValidationNotice> generateNotices(List<GtfsStopTime> stopTimes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new StopTimeIncreasingDistanceValidator(
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void increasingDistanceAlongShapeShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStopTime(1, "first trip", "s0", 2, 10.0d),
                    createStopTime(2, "first trip", "s1", 42, 45.0d),
                    createStopTime(3, "first trip", "s2", 46, 64.0d))))
        .isEmpty();
  }

  @Test
  public void lastShapeWithDecreasingDistanceAlongShapeShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStopTime(1, "first trip", "s0", 2, 10.0d),
                    createStopTime(2, "first trip", "s1", 42, 45.0d),
                    createStopTime(3, "first trip", "s2", 46, 4.0d))))
        .containsExactly(
            new DecreasingOrEqualStopTimeDistanceNotice("first trip", 3, 4.0d, 46, 2, 45.0d, 42));
  }

  @Test
  public void twoShapesWithTheSameDistanceShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStopTime(1, "first trip", "s0", 2, 10.0d),
                    createStopTime(2, "first trip", "s1", 42, 45.0d),
                    createStopTime(3, "first trip", "s2", 46, 45.0d))))
        .containsExactly(
            new DecreasingOrEqualStopTimeDistanceNotice("first trip", 3, 45.0d, 46, 2, 45.0d, 42));
  }

  @Test
  public void oneIntermediateShapeWithDecreasingDistanceAlongShapeShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStopTime(1, "first trip", "s0", 2, 10.0d),
                    createStopTime(2, "first trip", "s1", 42, 8.6d),
                    createStopTime(3, "first trip", "s2", 46, 46.0d))))
        .containsExactly(
            new DecreasingOrEqualStopTimeDistanceNotice("first trip", 2, 8.6d, 42, 1, 10.0d, 2));
  }
}
