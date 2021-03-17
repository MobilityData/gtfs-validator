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

import com.google.common.collect.ImmutableMap;

/**
 * Start and end range fields are equal for a certain GTFS entity.
 *
 * <p>Example: {@code start_time == end_time} for {@code frequencies.txt}.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class StartAndEndRangeEqualNotice extends ValidationNotice {

  public StartAndEndRangeEqualNotice(
      String filename, long csvRowNumber, String entityId, String start, String end) {
    super(
        ImmutableMap.of(
            "filename", filename,
            "csvRowNumber", csvRowNumber,
            "entityId", entityId,
            "start", start,
            "end", end),
        SeverityLevel.ERROR);
  }

  public StartAndEndRangeEqualNotice(String filename, long csvRowNumber, String start, String end) {
    super(
        ImmutableMap.of(
            "filename", filename,
            "csvRowNumber", csvRowNumber,
            "start", start,
            "end", end),
        SeverityLevel.ERROR);
  }

  @Override
  public String getCode() {
    return "start_and_end_range_equal";
  }
}
