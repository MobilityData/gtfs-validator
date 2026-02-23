/*
 * Copyright 2021 Google LLC
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
package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import javax.annotation.Nullable;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

/**
 * Two date or time fields are equal.
 *
 * <p>The fields `frequencies.start_date` and `frequencies.end_date` have been found equal in
 * `frequencies.txt`. The GTFS spec is currently unclear how this case should be handled (e.g., is
 * it a trip that circulates once?). It is recommended to use a trip not defined via frequencies.txt
 * for this case.
 */
@GtfsValidationNotice(severity = ERROR)
public class StartAndEndRangeEqualNotice extends ValidationNotice {

  /** The name of the faulty file. */
  private final String filename;

  /** The row number of the faulty record. */
  private final int csvRowNumber;

  /** The id of the faulty entity. */
  @Nullable private final String entityId;

  /** The start value's field name. */
  private final String startFieldName;

  /** The end value's field name. */
  private final String endFieldName;

  /** The faulty value. */
  private final String value;

  public StartAndEndRangeEqualNotice(
      String filename,
      int csvRowNumber,
      String entityId,
      String startFieldName,
      String endFieldName,
      String value) {
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.entityId = entityId;
    this.startFieldName = startFieldName;
    this.endFieldName = endFieldName;
    this.value = value;
  }

  public StartAndEndRangeEqualNotice(
      String filename, int csvRowNumber, String startFieldName, String endFieldName, String value) {
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.entityId = null;
    this.startFieldName = startFieldName;
    this.endFieldName = endFieldName;
    this.value = value;
  }
}
