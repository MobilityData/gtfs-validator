/*
 * Copyright 2023 Google LLC, MobilityData IO
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
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.ForeignKeyViolationNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDate;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTimeframe;
import org.mobilitydata.gtfsvalidator.table.GtfsTimeframeTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.util.CalendarUtilTest;

public class TimeframeServiceIdForeignKeyValidatorTest {

  @Test
  public void timeframeServiceIdInCalendarTableShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(new GtfsTimeframe.Builder().setServiceId("WEEK").build()),
                ImmutableList.of(
                    CalendarUtilTest.createGtfsCalendar(
                        "WEEK",
                        LocalDate.of(2021, 1, 14),
                        LocalDate.of(2021, 1, 24),
                        ImmutableSet.of(
                            DayOfWeek.MONDAY,
                            DayOfWeek.TUESDAY,
                            DayOfWeek.WEDNESDAY,
                            DayOfWeek.THURSDAY,
                            DayOfWeek.FRIDAY))),
                ImmutableList.of()))
        .isEmpty();
  }

  @Test
  public void tripServiceIdInCalendarDateTableShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(new GtfsTimeframe.Builder().setServiceId("WEEK").build()),
                ImmutableList.of(),
                ImmutableList.of(
                    new GtfsCalendarDate.Builder()
                        .setCsvRowNumber(2)
                        .setServiceId("WEEK")
                        .setDate(GtfsDate.fromEpochDay(24354))
                        .setExceptionType(2)
                        .build())))
        .isEmpty();
  }

  @Test
  public void tripServiceIdNotInDataShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    new GtfsTimeframe.Builder().setServiceId("WEEK").setCsvRowNumber(1).build()),
                ImmutableList.of(),
                ImmutableList.of()))
        .containsExactly(
            new ForeignKeyViolationNotice(
                "timeframes.txt",
                "service_id",
                "calendar.txt or calendar_dates.txt",
                "service_id",
                "WEEK",
                1));
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsTimeframe> timeframes,
      List<GtfsCalendar> calendars,
      List<GtfsCalendarDate> calendarDates) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new TimeframeServiceIdForeignKeyValidator(
            GtfsTimeframeTableContainer.forEntities(timeframes, noticeContainer),
            GtfsCalendarTableContainer.forEntities(calendars, noticeContainer),
            GtfsCalendarDateTableContainer.forEntities(calendarDates, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }
}
