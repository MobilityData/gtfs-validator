/*
 * Copyright 2020 Google LLC
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

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StartAndEndDateOutOfOrderNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableLoader;

/**
 * Validates that start_date &lt;= end_date for all rows in "calendar.txt".
 *
 * <p>Generated notice: {@link StartAndEndDateOutOfOrderNotice}.
 */
@GtfsValidator
public class CalendarServiceDateValidator extends SingleEntityValidator<GtfsCalendar> {

  @Override
  public void validate(GtfsCalendar calendar, NoticeContainer noticeContainer) {
    if (calendar.hasStartDate()
        && calendar.hasEndDate()
        && calendar.startDate().isAfter(calendar.endDate())) {
      noticeContainer.addValidationNotice(
          new StartAndEndDateOutOfOrderNotice(
              GtfsCalendarTableLoader.FILENAME,
              calendar.serviceId(),
              calendar.csvRowNumber(),
              calendar.startDate(),
              calendar.endDate()));
    }
  }
}
