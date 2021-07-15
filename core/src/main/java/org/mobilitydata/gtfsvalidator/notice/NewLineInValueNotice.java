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
import org.mobilitydata.gtfsvalidator.annotation.NoticeExport;

/**
 * A value in CSV file has a new line or carriage return.
 *
 * <p>This error is usually found when the CSV file does not close double quotes properly, so the
 * next line is considered as a continuation of the previous line.
 *
 * <p>Example. The following file was intended to have fields "f11", "f12", "f21", "f22", but it
 * actually parses as two fields: "f11", "f12\nf21,\"f22\"".
 *
 * <pre>
 *   f11,"f12
 *   f21,"f22"
 * </pre>
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class NewLineInValueNotice extends ValidationNotice {

  @NoticeExport
  public NewLineInValueNotice(
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
}
