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

import com.google.common.collect.ImmutableMap;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.SchemaExport;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;

/**
 * Validates at least one of the following files is provided: `calendar.txt` and
 * `calendar_dates.txt`.
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link MissingCalendarAndCalendarDateFilesNotice}.
 * </ul>
 */
@GtfsValidator
public class MissingCalendarAndCalendarDateValidator extends FileValidator {
  private final GtfsCalendarTableContainer calendarTable;

  private final GtfsCalendarDateTableContainer calendarDateTable;

  @Inject
  MissingCalendarAndCalendarDateValidator(
      GtfsCalendarTableContainer calendarTable, GtfsCalendarDateTableContainer calendarDateTable) {
    this.calendarTable = calendarTable;
    this.calendarDateTable = calendarDateTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    if (calendarTable.isMissingFile() && calendarDateTable.isMissingFile()) {
      noticeContainer.addValidationNotice(new MissingCalendarAndCalendarDateFilesNotice());
    }
  }

  /**
   * GTFS files `calendar.txt` and `calendar_dates.txt` cannot be missing from the GTFS archive.
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  static class MissingCalendarAndCalendarDateFilesNotice extends ValidationNotice {
    @SchemaExport
    MissingCalendarAndCalendarDateFilesNotice() {
      super(ImmutableMap.of(), SeverityLevel.ERROR);
    }
  }
}
