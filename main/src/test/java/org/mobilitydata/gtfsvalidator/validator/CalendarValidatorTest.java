/*
 * Copyright 2021 Jarvus Innovations LLC
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
import com.google.common.collect.ImmutableSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.util.CalendarUtilTest;

@RunWith(JUnit4.class)
public class CalendarValidatorTest {

  static final Set<DayOfWeek> weekDays =
      ImmutableSet.of(
          DayOfWeek.MONDAY,
          DayOfWeek.TUESDAY,
          DayOfWeek.WEDNESDAY,
          DayOfWeek.THURSDAY,
          DayOfWeek.FRIDAY);
  private static final ZonedDateTime TEST_NOW =
      ZonedDateTime.of(2021, 1, 1, 14, 30, 0, 0, ZoneOffset.UTC);

  private GtfsCalendar createCalendar(LocalDate calendarEndDate) {
    return CalendarUtilTest.createGtfsCalendar(
        "WEEK", LocalDate.of(2021, 1, 14), calendarEndDate, weekDays);
  }

  private List<ValidationNotice> validateCalendar(GtfsCalendar calendar) {
    NoticeContainer container = new NoticeContainer();

    List<GtfsCalendar> calendars = ImmutableList.of(calendar);
    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(calendars, container);

    new CalendarValidator(new CurrentDateTime(TEST_NOW), calendarTable).validate(container);
    return container.getValidationNotices();
  }

  @Test
  public void calendarEndDateOneDayAgoShouldGenerateNotice() {
    assertThat(validateCalendar(createCalendar(TEST_NOW.toLocalDate().minusDays(1))))
        .containsExactly(new CalendarValidator.CalendarValidatorExpiredCalendarNotice());
  }

  @Test
  public void calendarEndDateOneDayFromNowShouldNotGenerateNotice() {
    assertThat(validateCalendar(createCalendar(TEST_NOW.toLocalDate().plusDays(1)))).isEmpty();
  }
}
