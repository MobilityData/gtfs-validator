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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

@RunWith(JUnit4.class)
public class ExpiredCalendarValidatorTest {

  static final Set<DayOfWeek> weekDays =
      ImmutableSet.of(
          DayOfWeek.MONDAY,
          DayOfWeek.TUESDAY,
          DayOfWeek.WEDNESDAY,
          DayOfWeek.THURSDAY,
          DayOfWeek.FRIDAY);
  private static final ZonedDateTime TEST_NOW =
      ZonedDateTime.of(2021, 1, 1, 14, 30, 0, 0, ZoneOffset.UTC);

  @Test
  public void calendarEndDateOneDayAgoShouldGenerateNotice() {
    NoticeContainer container = new NoticeContainer();

    List<GtfsCalendar> calendars =
        ImmutableList.of(
            new GtfsCalendar.Builder()
                .setCsvRowNumber(2)
                .setServiceId("WEEK")
                .setStartDate(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().minusDays(7)))
                .setEndDate(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().minusDays(1)))
                .setMonday(1)
                .setTuesday(1)
                .setWednesday(1)
                .setThursday(1)
                .setFriday(1)
                .build());
    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(calendars, container);

    var dateTable = new GtfsCalendarDateTableContainer(GtfsTableContainer.TableStatus.EMPTY_FILE);
    new ExpiredCalendarValidator(new CurrentDateTime(TEST_NOW), calendarTable, dateTable)
        .validate(container);
    assertThat(container.getValidationNotices())
        .containsExactly(new ExpiredCalendarValidator.ExpiredCalendarNotice(2, "WEEK"));
  }

  @Test
  public void calendarEndDateTodayShouldNotGenerateNotice() {
    NoticeContainer container = new NoticeContainer();

    List<GtfsCalendar> calendars =
        ImmutableList.of(
            new GtfsCalendar.Builder()
                .setCsvRowNumber(2)
                .setServiceId("WEEK")
                .setStartDate(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().minusDays(7)))
                .setEndDate(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate()))
                .setMonday(1)
                .setTuesday(1)
                .setWednesday(1)
                .setThursday(1)
                .setFriday(1)
                .build());
    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(calendars, container);

    var dateTable = new GtfsCalendarDateTableContainer(GtfsTableContainer.TableStatus.EMPTY_FILE);
    new ExpiredCalendarValidator(new CurrentDateTime(TEST_NOW), calendarTable, dateTable)
        .validate(container);
    assertThat(container.getValidationNotices()).isEmpty();
  }

  @Test
  public void calendarEndDateOneDayFromNowShouldNotGenerateNotice() {
    NoticeContainer container = new NoticeContainer();

    List<GtfsCalendar> calendars =
        ImmutableList.of(
            new GtfsCalendar.Builder()
                .setCsvRowNumber(2)
                .setServiceId("WEEK")
                .setStartDate(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().minusDays(7)))
                .setEndDate(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().plusDays(1)))
                .setMonday(1)
                .setTuesday(1)
                .setWednesday(1)
                .setThursday(1)
                .setFriday(1)
                .build());
    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(calendars, container);

    var dateTable = new GtfsCalendarDateTableContainer(GtfsTableContainer.TableStatus.EMPTY_FILE);
    new ExpiredCalendarValidator(new CurrentDateTime(TEST_NOW), calendarTable, dateTable)
        .validate(container);
    assertThat(container.getValidationNotices()).isEmpty();
  }

  @Test
  public void calendarEndDateOneDayAgoButExtendedByAddedCalendarDateTableShouldNotGenerateNotice() {
    NoticeContainer container = new NoticeContainer();

    List<GtfsCalendar> calendars =
        ImmutableList.of(
            new GtfsCalendar.Builder()
                .setCsvRowNumber(2)
                .setServiceId("WEEK")
                .setStartDate(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().minusDays(7)))
                .setEndDate(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().minusDays(1)))
                .setMonday(1)
                .setTuesday(1)
                .setWednesday(1)
                .setThursday(1)
                .setFriday(1)
                .build());
    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(calendars, container);

    var dateTable =
        GtfsCalendarDateTableContainer.forEntities(
            ImmutableList.of(
                new GtfsCalendarDate.Builder()
                    .setCsvRowNumber(2)
                    .setServiceId("WEEK")
                    .setDate(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate()))
                    .setExceptionType(GtfsCalendarDateExceptionType.SERVICE_ADDED)
                    .build()),
            container);
    new ExpiredCalendarValidator(new CurrentDateTime(TEST_NOW), calendarTable, dateTable)
        .validate(container);
    assertThat(container.getValidationNotices()).isEmpty();
  }

  @Test
  public void calendarEndDateTodayButShortenedByRemovedCalendarDateShouldGenerateNotice() {
    NoticeContainer container = new NoticeContainer();

    List<GtfsCalendar> calendars =
        ImmutableList.of(
            new GtfsCalendar.Builder()
                .setCsvRowNumber(2)
                .setServiceId("WEEK")
                .setStartDate(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().minusDays(7)))
                .setEndDate(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate()))
                .setMonday(1)
                .setTuesday(1)
                .setWednesday(1)
                .setThursday(1)
                .setFriday(1)
                .build());
    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(calendars, container);

    var dateTable =
        GtfsCalendarDateTableContainer.forEntities(
            ImmutableList.of(
                new GtfsCalendarDate.Builder()
                    .setCsvRowNumber(2)
                    .setServiceId("WEEK")
                    .setDate(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate()))
                    .setExceptionType(GtfsCalendarDateExceptionType.SERVICE_REMOVED)
                    .build()),
            container);
    new ExpiredCalendarValidator(new CurrentDateTime(TEST_NOW), calendarTable, dateTable)
        .validate(container);
    assertThat(container.getValidationNotices())
        .containsExactly(new ExpiredCalendarValidator.ExpiredCalendarNotice(2, "WEEK"));
  }

  @Test
  public void calendarWithNoDaysShouldNotGenerateNotice() {
    NoticeContainer container = new NoticeContainer();

    List<GtfsCalendar> calendars =
        ImmutableList.of(
            new GtfsCalendar.Builder()
                .setCsvRowNumber(2)
                .setServiceId("WEEK")
                .setStartDate(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().minusDays(7)))
                .setEndDate(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate()))
                .build());

    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(calendars, container);
    var dateTable = new GtfsCalendarDateTableContainer(GtfsTableContainer.TableStatus.EMPTY_FILE);
    new ExpiredCalendarValidator(new CurrentDateTime(TEST_NOW), calendarTable, dateTable)
        .validate(container);
    assertThat(container.getValidationNotices()).isEmpty();
  }
}
