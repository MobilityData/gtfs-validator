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
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

@GtfsValidator
public class CalendarValidator extends FileValidator {
  private final GtfsCalendarTableContainer calendarTable;
  private final CurrentDateTime currentDateTime;

  @Inject
  CalendarValidator(CurrentDateTime currentDateTime, GtfsCalendarTableContainer calendarTable) {
    this.currentDateTime = currentDateTime;
    this.calendarTable = calendarTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    LocalDate now = currentDateTime.getNow().toLocalDate();
    GtfsDate currentDate = GtfsDate.fromLocalDate(now);

    for (var calendar : calendarTable.getEntities()) {
      if (calendar.endDate().isBefore(currentDate)) {
        noticeContainer.addValidationNotice(
            new CalendarValidator.CalendarValidatorExpiredCalendarNotice());
      }
    }
  }

  static class CalendarValidatorExpiredCalendarNotice extends ValidationNotice {
    CalendarValidatorExpiredCalendarNotice() {
      super(SeverityLevel.WARNING);
    }
  }
}
