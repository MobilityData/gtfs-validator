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

  private String filename;
  private long csvRowNumber;
  private String fieldName;
  private String fieldValue;

  public NewLineInValueNotice(
      String filename, long csvRowNumber, String fieldName, String fieldValue) {
    super(SeverityLevel.ERROR);
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
  }
}
