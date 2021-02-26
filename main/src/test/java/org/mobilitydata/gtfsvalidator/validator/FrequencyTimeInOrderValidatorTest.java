/*
 * Copyright 2020 Google LLC, MobilityData IO
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StartAndEndTimeEqualNotice;
import org.mobilitydata.gtfsvalidator.notice.StartAndEndTimeOutOfOrderNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequency;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequencyTableLoader;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@RunWith(JUnit4.class)
public class FrequencyTimeInOrderValidatorTest {
  private static GtfsFrequency createFrequency(
      long csvRowNumber, GtfsTime startTime, GtfsTime endTime, int headwaysSecs, String tripId) {
    return new GtfsFrequency.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStartTime(startTime)
        .setTripId(tripId)
        .setEndTime(endTime)
        .setHeadwaySecs(headwaysSecs)
        .build();
  }

  @Test
  public void startTimeBeforeEndTimeShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    FrequencyTimeInOrderValidator underTest = new FrequencyTimeInOrderValidator();
    underTest.validate(
        createFrequency(
            2,
            GtfsTime.fromSecondsSinceMidnight(55),
            GtfsTime.fromSecondsSinceMidnight(900),
            30,
            "trip id value"),
        noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void startTimeAfterEndTimeShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    FrequencyTimeInOrderValidator underTest = new FrequencyTimeInOrderValidator();
    underTest.validate(
        createFrequency(
            0,
            GtfsTime.fromSecondsSinceMidnight(55),
            GtfsTime.fromSecondsSinceMidnight(40),
            30,
            "trip id value"),
        noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new StartAndEndTimeOutOfOrderNotice(
                GtfsFrequencyTableLoader.FILENAME,
                "trip id value",
                0,
                GtfsTime.fromSecondsSinceMidnight(55),
                GtfsTime.fromSecondsSinceMidnight(40)));
  }

  @Test
  public void startTimeEqualToEndTimeShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    FrequencyTimeInOrderValidator underTest = new FrequencyTimeInOrderValidator();
    underTest.validate(
        createFrequency(
            0,
            GtfsTime.fromSecondsSinceMidnight(55),
            GtfsTime.fromSecondsSinceMidnight(55),
            30,
            "trip id value"),
        noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new StartAndEndTimeEqualNotice(
                GtfsFrequencyTableLoader.FILENAME,
                "trip id value",
                0,
                GtfsTime.fromSecondsSinceMidnight(55)));
  }
}
