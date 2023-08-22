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

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsTimeframe;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.validator.TimeframeStartAndEndTimeValidator.TimeframeOnlyStartOrEndTimeSpecifiedNotice;
import org.mobilitydata.gtfsvalidator.validator.TimeframeStartAndEndTimeValidator.TimeframeStartOrEndTimeGreaterThanTwentyFourHoursNotice;

@RunWith(JUnit4.class)
public class TimeframeStartAndEndTimeValidatorTest {

  @Test
  public void testExplicitFullDayInterval() {
    assertThat(
            validate(
                new GtfsTimeframe.Builder()
                    .setStartTime(GtfsTime.fromString("00:00:00"))
                    .setEndTime(GtfsTime.fromString("24:00:00"))
                    .build()))
        .isEmpty();
  }

  @Test
  public void testImplicitFullDayInterval() {
    assertThat(validate(new GtfsTimeframe.Builder().build())).isEmpty();
  }

  @Test
  public void testBeyondTwentyFourHours() {
    assertThat(
            validate(
                new GtfsTimeframe.Builder()
                    .setCsvRowNumber(2)
                    .setStartTime(GtfsTime.fromString("00:00:00"))
                    .setEndTime(GtfsTime.fromString("24:00:01"))
                    .build()))
        .containsExactly(
            new TimeframeStartOrEndTimeGreaterThanTwentyFourHoursNotice(
                2, "end_time", GtfsTime.fromString("24:00:01")));
  }

  @Test
  public void testOnlyStartTimeSpecified() {
    assertThat(
            validate(
                new GtfsTimeframe.Builder()
                    .setStartTime(GtfsTime.fromString("00:00:00"))
                    .setCsvRowNumber(2)
                    .build()))
        .containsExactly(new TimeframeOnlyStartOrEndTimeSpecifiedNotice(2));
  }

  @Test
  public void testOnlyEndTimeSpecified() {
    assertThat(
            validate(
                new GtfsTimeframe.Builder()
                    .setEndTime(GtfsTime.fromString("10:00:00"))
                    .setCsvRowNumber(2)
                    .build()))
        .containsExactly(new TimeframeOnlyStartOrEndTimeSpecifiedNotice(2));
  }

  private List<ValidationNotice> validate(GtfsTimeframe timeframe) {
    NoticeContainer container = new NoticeContainer();
    new TimeframeStartAndEndTimeValidator().validate(timeframe, container);
    return container.getValidationNotices();
  }
}
