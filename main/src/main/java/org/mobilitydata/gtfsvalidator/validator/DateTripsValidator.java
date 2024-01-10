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
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.SortedSet;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.input.DateForValidation;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.util.CalendarUtil;
import org.mobilitydata.gtfsvalidator.util.TripCalendarUtil;

/**
 * Validates that trip data exists for the next 7 days. Dates with the majority trips per day should
 * be included for at least the next 7 day period. see
 * https://github.com/MobilityData/gtfs-validator/issues/886#issuecomment-832237225
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link TripCoverageNotActiveForNext7DaysNotice}.
 * </ul>
 */
@GtfsValidator
public class DateTripsValidator extends FileValidator {

  private final GtfsCalendarDateTableContainer calendarDateTable;

  private final GtfsCalendarTableContainer calendarTable;

  private final GtfsTripTableContainer tripContainer;

  private final GtfsFrequencyTableContainer frequencyTable;

  private final DateForValidation dateForValidation;

  @Inject
  DateTripsValidator(
      DateForValidation dateForValidation,
      GtfsCalendarDateTableContainer calendarDateTable,
      GtfsCalendarTableContainer calendarTable,
      GtfsTripTableContainer tripContainer,
      GtfsFrequencyTableContainer frequencyTable) {
    this.dateForValidation = dateForValidation;
    this.calendarTable = calendarTable;
    this.calendarDateTable = calendarDateTable;
    this.tripContainer = tripContainer;
    this.frequencyTable = frequencyTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    final Map<String, SortedSet<LocalDate>> servicePeriodMap =
        CalendarUtil.servicePeriodToServiceDatesMap(
            CalendarUtil.buildServicePeriodMap(calendarTable, calendarDateTable));
    NavigableMap<LocalDate, Integer> tripCounts =
        TripCalendarUtil.countTripsForEachServiceDate(
            servicePeriodMap, tripContainer, frequencyTable);
    Optional<TripCalendarUtil.DateInterval> majorityServiceDates =
        TripCalendarUtil.computeMajorityServiceCoverage(tripCounts);
    LocalDate currentDatePlusSevenDays = dateForValidation.getDate().plusDays(7);
    if (!majorityServiceDates.isEmpty()) {
      LocalDate serviceWindowStartDate = majorityServiceDates.get().startDate();
      LocalDate serviceWindowEndDate = majorityServiceDates.get().endDate();
      if (serviceWindowStartDate.isAfter(dateForValidation.getDate())
          || serviceWindowEndDate.isBefore(currentDatePlusSevenDays)) {
        noticeContainer.addValidationNotice(
            new TripCoverageNotActiveForNext7DaysNotice(
                GtfsDate.fromLocalDate(dateForValidation.getDate()),
                GtfsDate.fromLocalDate(serviceWindowStartDate),
                GtfsDate.fromLocalDate(serviceWindowEndDate)));
      }
    }
  }

  /**
   * Trips data should be valid for at least the next seven days.
   *
   * <p>This notice is triggered if the date range where a significant number of trips are running
   * ends in less than 7 days.
   */
  @GtfsValidationNotice(severity = WARNING)
  static class TripCoverageNotActiveForNext7DaysNotice extends ValidationNotice {

    /** Current date (YYYYMMDD format). */
    private final GtfsDate currentDate;

    /** The start date of the majority service window. */
    private final GtfsDate serviceWindowStartDate;

    /** The end date of the majority service window. */
    private final GtfsDate serviceWindowEndDate;

    TripCoverageNotActiveForNext7DaysNotice(
        GtfsDate currentDate, GtfsDate serviceWindowStartDate, GtfsDate serviceWindowEndDate) {
      this.currentDate = currentDate;
      this.serviceWindowStartDate = serviceWindowStartDate;
      this.serviceWindowEndDate = serviceWindowEndDate;
    }
  }
}
