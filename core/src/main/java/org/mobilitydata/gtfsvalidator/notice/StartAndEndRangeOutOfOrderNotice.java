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
import org.mobilitydata.gtfsvalidator.annotation.NoticeExport;

/**
 * Start and end range fields are out of order for a certain GTFS entity.
 *
 * <p>Example: {@code start_date &gt; end_date} for {@code calendar.txt}.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class StartAndEndRangeOutOfOrderNotice extends ValidationNotice {

  @NoticeExport
  public StartAndEndRangeOutOfOrderNotice(
      String filename,
      long csvRowNumber,
      String entityId,
      String startFieldName,
      String startValue,
      String endFieldName,
      String endValue) {
    super(
        new ImmutableMap.Builder<String, Object>()
            .put("filename", filename)
            .put("csvRowNumber", csvRowNumber)
            .put("entityId", entityId)
            .put("startFieldName", startFieldName)
            .put("startValue", startValue)
            .put("endFieldName", endFieldName)
            .put("endValue", endValue)
            .build(),
        SeverityLevel.ERROR);
  }

  public StartAndEndRangeOutOfOrderNotice(
      String filename,
      long csvRowNumber,
      String startFieldName,
      String startValue,
      String endFieldName,
      String endValue) {
    super(
        new ImmutableMap.Builder<String, Object>()
            .put("filename", filename)
            .put("csvRowNumber", csvRowNumber)
            .put("startFieldName", startFieldName)
            .put("startValue", startValue)
            .put("endFieldName", endFieldName)
            .put("endValue", endValue)
            .build(),
        SeverityLevel.ERROR);
  }

  @Override
  public String getCode() {
    return "start_and_end_range_out_of_order";
  }
}
