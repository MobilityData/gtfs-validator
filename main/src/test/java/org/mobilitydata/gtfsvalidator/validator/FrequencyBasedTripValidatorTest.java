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
import org.mobilitydata.gtfsvalidator.table.GtfsFrequency;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.validator.FrequencyBasedTripValidator.InvalidFrequencyBasedTripNotice;

public class FrequencyBasedTripValidatorTest {

  public static GtfsStopTime createStopTime(
      long csvRowNumber, String tripId, int stopSequence, Integer timepoint) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId(tripId)
        .setStopSequence(stopSequence)
        .setStopId("stop id value")
        .setTimepoint(timepoint)
        .build();
  }

  public static GtfsFrequency createFrequency(long csvRowNumber, String tripId, Integer exactTime) {
    return new GtfsFrequency.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId(tripId)
        .setStartTime(GtfsTime.fromSecondsSinceMidnight(4450))
        .setEndTime(GtfsTime.fromSecondsSinceMidnight(14450))
        .setExactTimes(exactTime)
        .build();
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsFrequency> frequencies, List<GtfsStopTime> stopTimes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new FrequencyBasedTripValidator(
            GtfsFrequencyTableContainer.forEntities(frequencies, noticeContainer),
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void timepoint_one_exactTimes_zero_generatesNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createFrequency(1, "t0", 0)),
                ImmutableList.of(createStopTime(0, "t1", 0, 1), createStopTime(2, "t0", 1, 1))))
        .containsExactly(new InvalidFrequencyBasedTripNotice("t0", 1));
  }

  @Test
  public void timepoint_one_multipleExactTimes_zero_generatesNotices() {
    assertThat(
            generateNotices(
                ImmutableList.of(createFrequency(1, "t0", 0), createFrequency(3, "t0", 0)),
                ImmutableList.of(createStopTime(0, "t0", 0, 1), createStopTime(2, "t0", 1, 1))))
        .containsExactly(
            new InvalidFrequencyBasedTripNotice("t0", 1),
            new InvalidFrequencyBasedTripNotice("t0", 3));
  }

  @Test
  public void timepoint_one_exactTimes_one_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createFrequency(1, "trip id value", 1)),
                ImmutableList.of(createStopTime(0, "t0", 0, 1), createStopTime(2, "t0", 1, 1))))
        .isEmpty();
  }

  @Test
  public void timepoint_one_exactTimes_empty_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createFrequency(1, "trip id value", null)),
                ImmutableList.of(createStopTime(0, "t0", 0, 1), createStopTime(2, "t0", 1, 1))))
        .isEmpty();
  }

  @Test
  public void timepoint_zero_exactTimes_zero_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createFrequency(1, "trip id value", 0)),
                ImmutableList.of(createStopTime(0, "t0", 0, 0), createStopTime(2, "t0", 1, 0))))
        .isEmpty();
  }

  @Test
  public void timepoint_zero_exactTimes_empty_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createFrequency(1, "trip id value", null)),
                ImmutableList.of(createStopTime(0, "t0", 0, 0), createStopTime(2, "t0", 1, 0))))
        .isEmpty();
  }

  @Test
  public void timepoint_zero_exactTimes_one_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createFrequency(1, "trip id value", 1)),
                ImmutableList.of(createStopTime(0, "t0", 0, 0), createStopTime(2, "t0", 1, 0))))
        .isEmpty();
  }
}
