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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.ForeignKeyError;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDate;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.util.CalendarUtilTest;

public class GtfsTripServiceIdForeignKeyValidatorTest {
  static final Set<DayOfWeek> weekDays =
      ImmutableSet.of(
          DayOfWeek.MONDAY,
          DayOfWeek.TUESDAY,
          DayOfWeek.WEDNESDAY,
          DayOfWeek.THURSDAY,
          DayOfWeek.FRIDAY);

  private static GtfsTripTableContainer createTripTable(
      NoticeContainer noticeContainer, List<GtfsTrip> entities) {
    return GtfsTripTableContainer.forEntities(entities, noticeContainer);
  }

  public static GtfsTrip createTrip(
      long csvRowNumber, String routeId, String serviceId, String tripId, String shapeId) {
    return new GtfsTrip.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setRouteId(routeId)
        .setServiceId(serviceId)
        .setTripId(tripId)
        .setShapeId(shapeId)
        .build();
  }

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
      List<GtfsCalendar> calendars, List<GtfsTrip> trips, List<GtfsCalendarDate> calendarDates) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new GtfsTripServiceIdForeignKeyValidator(
            GtfsTripTableContainer.forEntities(trips, noticeContainer),
            GtfsCalendarTableContainer.forEntities(calendars, noticeContainer),
            GtfsCalendarDateTableContainer.forEntities(calendarDates, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void tripServiceIdInCalendarTableShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    CalendarUtilTest.createGtfsCalendar(
                        "WEEK", LocalDate.of(2021, 1, 14), LocalDate.of(2021, 1, 24), weekDays)),
                ImmutableList.of(createTrip(1, "route id", "WEEK", "trip id", "shape id")),
                ImmutableList.of()))
        .isEmpty();
  }

  @Test
  public void tripServiceIdInCalendarDateTableShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(),
                ImmutableList.of(createTrip(1, "route id", "WEEK", "trip id", "shape id")),
                ImmutableList.of(createCalendarDate(2, "WEEK", GtfsDate.fromEpochDay(24354), 2))))
        .isEmpty();
  }

  @Test
  public void tripServiceIdNotInDataShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(),
                ImmutableList.of(createTrip(1, "route id", "WEEK", "trip id", "shape id")),
                ImmutableList.of()))
        .containsExactly(
            new ForeignKeyError(
                "trips.txt",
                "service_id",
                "calendar.txt or calendar_dates.txt",
                "service_id",
                "WEEK",
                1));
  }
}
