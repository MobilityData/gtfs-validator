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

package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

/**
 * Two {@code GtfsDate} are out of order
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class StartAndEndDateOutOfOrderNotice extends ValidationNotice {
  public StartAndEndDateOutOfOrderNotice(
      String filename, long csvRowNumber, GtfsDate startDate, GtfsDate endDate) {
    super(
        ImmutableMap.of(
            "filename",
            filename,
            "csvRowNumber",
            csvRowNumber,
            "startDate",
            startDate.toYYYYMMDD(),
            "endDate",
            endDate.toYYYYMMDD()),
        SeverityLevel.ERROR);
  }

  public StartAndEndDateOutOfOrderNotice(
      String filename, String entityId, long csvRowNumber, GtfsDate startDate, GtfsDate endDate) {
    super(
        ImmutableMap.of(
            "filename",
            filename,
            "csvRowNumber",
            csvRowNumber,
            "entityId",
            entityId,
            "startDate",
            startDate.toYYYYMMDD(),
            "endDate",
            endDate.toYYYYMMDD()),
        SeverityLevel.ERROR);
  }

  @Override
  public String getCode() {
    return "start_and_end_date_out_of_order";
  }
}
