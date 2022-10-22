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
import org.mobilitydata.gtfsvalidator.notice.ForeignKeyViolationNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDate;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

/**
 * Validates that service_id field in "trips.txt" references a valid service_id in "calendar.txt" or
 * "calendar_date.txt".
 *
 * <p>Generated notice: {@link ForeignKeyViolationNotice}.
 */
@GtfsValidator
public class GtfsTripServiceIdForeignKeyValidator extends FileValidator {
  private final GtfsTripTableContainer tripContainer;
  private final GtfsCalendarTableContainer calendarContainer;
  private final GtfsCalendarDateTableContainer calendarDateContainer;

  @Inject
  GtfsTripServiceIdForeignKeyValidator(
      GtfsTripTableContainer tripContainer,
      GtfsCalendarTableContainer calendarContainer,
      GtfsCalendarDateTableContainer calendarDateContainer) {
    this.tripContainer = tripContainer;
    this.calendarContainer = calendarContainer;
    this.calendarDateContainer = calendarDateContainer;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsTrip trip : tripContainer.getEntities()) {
      String childKey = trip.serviceId();
      if (!hasReferencedKey(childKey, calendarContainer, calendarDateContainer)) {
        noticeContainer.addValidationNotice(
            new ForeignKeyViolationNotice(
                GtfsTrip.FILENAME,
                GtfsTrip.SERVICE_ID_FIELD_NAME,
                GtfsCalendar.FILENAME + " or " + GtfsCalendarDate.FILENAME,
                GtfsCalendar.SERVICE_ID_FIELD_NAME,
                childKey,
                trip.csvRowNumber()));
      }
    }
  }

  private boolean hasReferencedKey(
      String childKey,
      GtfsCalendarTableContainer calendarContainer,
      GtfsCalendarDateTableContainer calendarDateContainer) {
    return calendarContainer.byServiceId(childKey).isPresent()
        || !calendarDateContainer.byServiceId(childKey).isEmpty();
  }
}
