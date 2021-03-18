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
import com.google.common.collect.ImmutableSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDate;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer.TableStatus;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.util.CalendarUtilTest;
import org.mobilitydata.gtfsvalidator.validator.MissingCalendarAndCalendarDateValidator.MissingCalendarAndCalendarDateFilesNotice;

public class MissingCalendarAndCalendarDateValidatorTest {
  static final Set<DayOfWeek> weekDays =
      ImmutableSet.of(
          DayOfWeek.MONDAY,
          DayOfWeek.TUESDAY,
          DayOfWeek.WEDNESDAY,
          DayOfWeek.THURSDAY,
          DayOfWeek.FRIDAY);

  private static GtfsCalendarTableContainer createCalendarTable(
      NoticeContainer noticeContainer, List<GtfsCalendar> entities) {
    return GtfsCalendarTableContainer.forEntities(entities, noticeContainer);
  }

  public static GtfsCalendarDate createCalendarDate(
      long csvRowNumber, String serviceId, GtfsDate date, int exceptionType) {
    return new GtfsCalendarDate.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setServiceId(serviceId)
        .setDate(date)
        .setExceptionType(exceptionType)
        .build();
  }

  private static GtfsCalendarDateTableContainer createCalendarDateTable(
      NoticeContainer noticeContainer, List<GtfsCalendarDate> entities) {
    return GtfsCalendarDateTableContainer.forEntities(entities, noticeContainer);
  }

  private static List<ValidationNotice> generateNotices(
      GtfsCalendarTableContainer calendarTable, GtfsCalendarDateTableContainer calendarDateTable) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new MissingCalendarAndCalendarDateValidator(calendarTable, calendarDateTable)
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsCalendar> calendars, List<GtfsCalendarDate> calendarDates) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new MissingCalendarAndCalendarDateValidator(
            GtfsCalendarTableContainer.forEntities(calendars, noticeContainer),
            GtfsCalendarDateTableContainer.forEntities(calendarDates, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void bothFilesProvidedShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    CalendarUtilTest.createGtfsCalendar(
                        "WEEK", LocalDate.of(2021, 1, 4), LocalDate.of(2021, 4, 10), weekDays)),
                ImmutableList.of(createCalendarDate(2, "WEEK", GtfsDate.fromEpochDay(24354), 2))))
        .isEmpty();
  }

  @Test
  public void calendarOnlyProvidedShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    CalendarUtilTest.createGtfsCalendar(
                        "WEEK", LocalDate.of(2021, 1, 4), LocalDate.of(2021, 4, 10), weekDays)),
                ImmutableList.of()))
        .isEmpty();
  }

  @Test
  public void calendarDateOnlyProvidedShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(),
                ImmutableList.of(createCalendarDate(2, "WEEK", GtfsDate.fromEpochDay(24354), 2))))
        .isEmpty();
  }

  @Test
  public void bothMissingFilesShouldGenerateNotice() {
    assertThat(
            generateNotices(
                new GtfsCalendarTableContainer(TableStatus.MISSING_FILE),
                new GtfsCalendarDateTableContainer(TableStatus.MISSING_FILE)))
        .containsExactly(new MissingCalendarAndCalendarDateFilesNotice());
  }
}
