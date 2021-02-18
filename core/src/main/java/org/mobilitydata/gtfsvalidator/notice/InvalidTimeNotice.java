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
 * A field cannot be parsed as time.
 *
 * <p>Time must be in the {@code HH:MM:SS} or {@code H:MM:SS} format.
 *
 * <p>Example: {@code 14:30:00} for 2:30PM or {@code 25:35:00} for 1:35AM on the next day.
 */
public class InvalidTimeNotice extends ValidationNotice {

  public InvalidTimeNotice(
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
    return "invalid_time";
  }
}
