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
 * The values in the given column of the input rows are out of range.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class NumberOutOfRangeNotice extends ValidationNotice {
  private final String filename;
  private final long csvRowNumber;
  private final String fieldName;
  private final String fieldType;
  private final Object fieldValue;

  public NumberOutOfRangeNotice(
      String filename, long csvRowNumber, String fieldName, String fieldType, Object fieldValue) {
    super(SeverityLevel.ERROR);
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.fieldName = fieldName;
    this.fieldType = fieldType;
    this.fieldValue = fieldValue;
  }
}
