/*
 * Copyright 2023 Google LLC
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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsTimeframe;
import org.mobilitydata.gtfsvalidator.table.GtfsTimeframeSchema;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * Validates the `start_time` and `end_time` values from `timeframes.txt`, checking that either both
 * are present or neither. Also checks that no value is greater than 24-hours.
 */
@GtfsValidator
public class TimeframeStartAndEndTimeValidator extends SingleEntityValidator<GtfsTimeframe> {

  private static final GtfsTime TWENTY_FOUR_HOURS = GtfsTime.fromHourMinuteSecond(24, 0, 0);

  @Override
  public void validate(GtfsTimeframe entity, NoticeContainer noticeContainer) {
    if (entity.hasStartTime() ^ entity.hasEndTime()) {
      noticeContainer.addValidationNotice(
          new TimeframeOnlyStartOrEndTimeSpecifiedNotice(entity.csvRowNumber()));
    }
    if (entity.hasStartTime() && entity.startTime().isAfter(TWENTY_FOUR_HOURS)) {
      noticeContainer.addValidationNotice(
          new TimeframeStartOrEndTimeGreaterThanTwentyFourHoursNotice(
              entity.csvRowNumber(), GtfsTimeframe.START_TIME_FIELD_NAME, entity.startTime()));
    }
    if (entity.hasEndTime() && entity.endTime().isAfter(TWENTY_FOUR_HOURS)) {
      noticeContainer.addValidationNotice(
          new TimeframeStartOrEndTimeGreaterThanTwentyFourHoursNotice(
              entity.csvRowNumber(), GtfsTimeframe.END_TIME_FIELD_NAME, entity.endTime()));
    }
  }

  /**
   * A row from `timeframes.txt` was found with only one of `start_time` and `end_time` specified.
   *
   * <p>Either both must be specified or neither must be specified.
   */
  @GtfsValidationNotice(severity = ERROR, files = @FileRefs(GtfsTimeframeSchema.class))
  static class TimeframeOnlyStartOrEndTimeSpecifiedNotice extends ValidationNotice {

    /** The row number for the faulty record. */
    private final int csvRowNumber;

    public TimeframeOnlyStartOrEndTimeSpecifiedNotice(int csvRowNumber) {
      this.csvRowNumber = csvRowNumber;
    }
  }

  /** A time in `timeframes.txt` is greater than `24:00:00`. */
  @GtfsValidationNotice(severity = ERROR, files = @FileRefs(GtfsTimeframeSchema.class))
  static class TimeframeStartOrEndTimeGreaterThanTwentyFourHoursNotice extends ValidationNotice {
    /** The row number for the faulty record. */
    private final int csvRowNumber;
    /** The time field name for the faulty record. */
    private final String fieldName;
    /** The invalid time value. */
    private final GtfsTime time;

    TimeframeStartOrEndTimeGreaterThanTwentyFourHoursNotice(
        int csvRowNumber, String fieldName, GtfsTime time) {
      this.csvRowNumber = csvRowNumber;
      this.fieldName = fieldName;
      this.time = time;
    }
  }
}
