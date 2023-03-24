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

import javax.annotation.Nullable;

/**
 * Start and end range fields are out of order for a certain GTFS entity.
 *
 * <p>Example: {@code start_date &gt; end_date} for {@code calendar.txt}.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class StartAndEndRangeOutOfOrderNotice extends ValidationNotice {

  // The name of the faulty file.
  private final String filename;

  // The row number of the faulty record.
  private final int csvRowNumber;

  // The faulty service id.
  @Nullable private final String entityId;

  // The start value's field name.
  private final String startFieldName;

  // The start value.
  private final String startValue;

  // The end value's field name.
  private final String endFieldName;

  // The end value.
  private final String endValue;

  public StartAndEndRangeOutOfOrderNotice(
      String filename,
      int csvRowNumber,
      String entityId,
      String startFieldName,
      String startValue,
      String endFieldName,
      String endValue) {
    super(SeverityLevel.ERROR);
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.entityId = entityId;
    this.startFieldName = startFieldName;
    this.startValue = startValue;
    this.endFieldName = endFieldName;
    this.endValue = endValue;
  }

  public StartAndEndRangeOutOfOrderNotice(
      String filename,
      int csvRowNumber,
      String startFieldName,
      String startValue,
      String endFieldName,
      String endValue) {
    super(SeverityLevel.ERROR);
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.entityId = null;
    this.startFieldName = startFieldName;
    this.startValue = startValue;
    this.endFieldName = endFieldName;
    this.endValue = endValue;
  }
}
