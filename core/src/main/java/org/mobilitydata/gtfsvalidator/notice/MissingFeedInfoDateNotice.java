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

/**
 * Even though `feed_info.start_date` and `feed_info.end_date` are optional, if one field is
 * provided the second one should also be provided.
 *
 * <p>Severity: {@code SeverityLevel.WARNING}
 */
public class MissingFeedInfoDateNotice extends ValidationNotice {
  public MissingFeedInfoDateNotice(long csvRowNumber, String fieldName) {
    super(
        ImmutableMap.of("csvRowNumber", csvRowNumber, "fieldName", fieldName),
        SeverityLevel.WARNING);
  }

  @Override
  public String getCode() {
    return "missing_feed_info_date";
  }
}
