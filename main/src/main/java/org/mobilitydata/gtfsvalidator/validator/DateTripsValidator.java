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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.util.CalendarUtil;

/**
 * Validates that trip data exists for the next 7 days. Dates with the majority trips per day should
 * be included for at least the next 7 day period. see
 * https://github.com/MobilityData/gtfs-validator/issues/886#issuecomment-832237225
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link TripDataShouldBeValidForNext7DaysNotice}.
 * </ul>
 */
@GtfsValidator
public class DateTripsValidator extends FileValidator {

  private final GtfsCalendarDateTableContainer calendarDateTable;
  private final GtfsCalendarTableContainer calendarTable;
  private final GtfsFeedInfoTableContainer feedInfoTable;
  private final GtfsTripTableContainer tripContainer;
  private final CurrentDateTime currentDateTime;

  @Inject
  DateTripsValidator(
      CurrentDateTime currentDateTime,
      GtfsCalendarDateTableContainer calendarDateTable,
      GtfsCalendarTableContainer calendarTable,
      GtfsFeedInfoTableContainer feedInfoTable,
      GtfsTripTableContainer tripContainer) {
    this.currentDateTime = currentDateTime;
    this.feedInfoTable = feedInfoTable;
    this.calendarTable = calendarTable;
    this.calendarDateTable = calendarDateTable;
    this.tripContainer = tripContainer;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    GtfsDate minStartDate = null;
    GtfsDate maxEndDate = null;

    GtfsDate serviceWindowStartDate = null;
    GtfsDate serviceWindowEndDate = null;

    LocalDate now = currentDateTime.getNow().toLocalDate();
    GtfsDate currentDate = GtfsDate.fromLocalDate(now);

    // Attempt to get Start Date and End Date from feedInfo
    GtfsFeedInfo entity = this.feedInfoTable.getSingleEntity().get();
    if (entity.hasFeedStartDate()) {
      if (minStartDate == null || entity.feedEndDate().isBefore(minStartDate)) {
        minStartDate = entity.feedStartDate();
      }
    }
    if (entity.hasFeedEndDate()) {
      if (maxEndDate == null || entity.feedEndDate().isAfter(maxEndDate)) {
        maxEndDate = entity.feedEndDate();
      }
    }

    final Map<String, SortedSet<LocalDate>> servicePeriodMap =
        CalendarUtil.servicePeriodToServiceDatesMap(
            CalendarUtil.buildServicePeriodMap(calendarTable, calendarDateTable));

    for (var serviceDates : servicePeriodMap.values()) {
      var firstServiceDate = serviceDates.first();
      if (minStartDate == null || firstServiceDate.isBefore(minStartDate.getLocalDate())) {
        minStartDate = GtfsDate.fromLocalDate(serviceDates.first());
      }
      if (maxEndDate == null || serviceDates.last().isAfter(maxEndDate.getLocalDate())) {
        maxEndDate = GtfsDate.fromLocalDate(serviceDates.last());
      }
    }

    if (minStartDate != null && maxEndDate != null) {
      Map<GtfsDate, Integer> tripsPerDate = new HashMap<>();
      final var dates =
          minStartDate
              .getLocalDate()
              .datesUntil(maxEndDate.getLocalDate().plusDays(1))
              .collect(Collectors.toList());

      int maxTripsPerDay = 0;
      for (var date : dates) {
        int tripsForDate = 0;
        for (var entry : servicePeriodMap.entrySet()) {
          if (entry.getValue().contains(date)) {
            var trips = tripContainer.byServiceId(entry.getKey());
            tripsForDate += trips.size();
          }
        }
        if (tripsForDate > maxTripsPerDay) {
          maxTripsPerDay = tripsForDate;
        }
        tripsPerDate.put(GtfsDate.fromLocalDate(date), tripsForDate);
      }

      for (var entry : tripsPerDate.entrySet()) {
        if (entry.getValue() >= 0.75 * maxTripsPerDay) {
          if (serviceWindowStartDate == null || entry.getKey().isBefore(serviceWindowStartDate)) {
            serviceWindowStartDate = entry.getKey();
          }
          if (serviceWindowEndDate == null || entry.getKey().isAfter(serviceWindowEndDate)) {
            serviceWindowEndDate = entry.getKey();
          }
        }
      }
    }

    GtfsDate currentDatePlusSevenDays = GtfsDate.fromLocalDate(now.plusDays(7));

    if (serviceWindowStartDate != null) {
      if (serviceWindowStartDate.isAfter(currentDate)
          || serviceWindowEndDate.isBefore(currentDatePlusSevenDays)) {
        noticeContainer.addValidationNotice(
            new TripDataShouldBeValidForNext7DaysNotice(
                currentDate, serviceWindowStartDate, serviceWindowEndDate));
        return;
      }
    }
  }

  static class TripDataShouldBeValidForNext7DaysNotice extends ValidationNotice {
    private final GtfsDate currentDate;
    private final GtfsDate serviceWindowStartDate;
    private final GtfsDate serviceWindowEndDate;

    TripDataShouldBeValidForNext7DaysNotice(
        GtfsDate currentDate, GtfsDate serviceWindowStartDate, GtfsDate serviceWindowEndDate) {
      super(SeverityLevel.WARNING);
      this.currentDate = currentDate;
      this.serviceWindowStartDate = serviceWindowStartDate;
      this.serviceWindowEndDate = serviceWindowEndDate;
    }
  }
}
