/*
 * Copyright 2020 Google LLC, MobilityData IO 2021
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

import com.google.common.collect.ImmutableSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StartAndEndDateOutOfOrderNotice;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.util.CalendarUtilTest;

public class CalendarServiceDateValidatorTest {
  static final Set<DayOfWeek> weekDays =
      ImmutableSet.of(
          DayOfWeek.MONDAY,
          DayOfWeek.TUESDAY,
          DayOfWeek.WEDNESDAY,
          DayOfWeek.THURSDAY,
          DayOfWeek.FRIDAY);

  @Test
  public void startDateBeforeEndDateShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    CalendarServiceDateValidator underTest = new CalendarServiceDateValidator();

    underTest.validate(
        CalendarUtilTest.createGtfsCalendar(
            "WEEK", LocalDate.of(2021, 1, 4), LocalDate.of(2021, 4, 10), weekDays),
        noticeContainer);
    assertThat(noticeContainer.getValidationNotices().isEmpty());
  }

  @Test
  public void startDateAfterEndDateShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    CalendarServiceDateValidator underTest = new CalendarServiceDateValidator();

    underTest.validate(
        CalendarUtilTest.createGtfsCalendar(
            "WEEK", LocalDate.of(2021, 1, 14), LocalDate.of(2021, 1, 10), weekDays),
        noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new StartAndEndDateOutOfOrderNotice(
                "calendar.txt",
                "WEEK",
                0,
                GtfsDate.fromLocalDate(LocalDate.of(2021, 1, 14)),
                GtfsDate.fromLocalDate(LocalDate.of(2021, 1, 10))));
  }

  @Test
  public void sameStartAndEndDateShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    CalendarServiceDateValidator underTest = new CalendarServiceDateValidator();

    underTest.validate(
        CalendarUtilTest.createGtfsCalendar(
            "WEEK", LocalDate.of(2021, 1, 4), LocalDate.of(2021, 1, 4), weekDays),
        noticeContainer);
    assertThat(noticeContainer.getValidationNotices().isEmpty());
  }
}
