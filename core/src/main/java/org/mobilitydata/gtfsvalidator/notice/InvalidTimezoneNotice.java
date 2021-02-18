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
 * A field cannot be parsed as a timezone.
 *
 * <p>Timezones are defined at <a href="https://www.iana.org/time-zones">www.iana.org</a>. Timezone
 * names never contain the space character but may contain an underscore. Refer to <a
 * href="http://en.wikipedia.org/wiki/List_of_tz_zones">Wikipedia</a> for a list of valid values.
 *
 * <p>Example: {@code Asia/Tokyo}, {@code America/Los_Angeles} or {@code Africa/Cairo}.
 */
public class InvalidTimezoneNotice extends ValidationNotice {

  public InvalidTimezoneNotice(
      String filename, long csvRowNumber, String fieldName, String fieldValue) {
    super(
        ImmutableMap.of(
            "filename",
            filename,
            "csvRowNumber",
            csvRowNumber,
            "fieldName",
            fieldName,
            "fieldValue",
            fieldValue),
        SeverityLevel.ERROR);
  }

  @Override
  public String getCode() {
    return "invalid_timezone";
  }
}
