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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import java.time.LocalDate;
import java.util.*;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.UrlRef;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.input.DateForValidation;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.util.CalendarUtil;

@GtfsValidator
public class ExpiredCalendarValidator extends FileValidator {

  private final GtfsCalendarTableContainer calendarTable;

  private final GtfsCalendarDateTableContainer calendarDateTable;

  private final DateForValidation dateForValidation;

  @Inject
  ExpiredCalendarValidator(
      DateForValidation dateForValidation,
      GtfsCalendarTableContainer calendarTable,
      GtfsCalendarDateTableContainer calendarDateTable) {
    this.dateForValidation = dateForValidation;
    this.calendarTable = calendarTable;
    this.calendarDateTable = calendarDateTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    final Map<String, SortedSet<LocalDate>> servicePeriodMap =
        CalendarUtil.servicePeriodToServiceDatesMap(
            CalendarUtil.buildServicePeriodMap(calendarTable, calendarDateTable));
    for (var serviceId : servicePeriodMap.keySet()) {
      SortedSet<LocalDate> serviceDates = servicePeriodMap.get(serviceId);
      if (!serviceDates.isEmpty() && serviceDates.last().isBefore(dateForValidation.getDate())) {
        int csvRowNumber;
        if (calendarTable.byServiceId(serviceId).isPresent()) {
          csvRowNumber = calendarTable.byServiceId(serviceId).get().csvRowNumber();
        } else {
          //          Taking the first of the calendar dates for the service id.
          //          This is the case of foreign key violation or calendar.txt not provided.
          Optional<GtfsCalendarDate> firstCalendarDate =
              calendarDateTable.byServiceId(serviceId).stream()
                  .min(Comparator.comparingInt(GtfsCalendarDate::csvRowNumber));
          csvRowNumber = firstCalendarDate.map(GtfsCalendarDate::csvRowNumber).orElse(0);
        }
        noticeContainer.addValidationNotice(new ExpiredCalendarNotice(csvRowNumber, serviceId));
      }
    }
  }

  /**
   * Dataset should not contain date ranges for services that have already expired.
   *
   * <p>This warning takes into account the `calendar_dates.txt` file as well as the `calendar.txt`
   * file.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      urls = {
        @UrlRef(
            label = "Dataset Publishing & General Practices",
            url = "https://gtfs.org/schedule/reference/#dataset-publishing-general-practices")
      })
  static class ExpiredCalendarNotice extends ValidationNotice {

    /** The row of the faulty record. */
    private final int csvRowNumber;

    /** The service id of the faulty record. */
    private final String serviceId;

    ExpiredCalendarNotice(int csvRowNumber, String serviceId) {
      this.csvRowNumber = csvRowNumber;
      this.serviceId = serviceId;
    }
  }
}
