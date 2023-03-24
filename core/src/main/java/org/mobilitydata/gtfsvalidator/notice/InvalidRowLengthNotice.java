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
 * A row in the input file has a different number of values than specified by the CSV header.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class InvalidRowLengthNotice extends ValidationNotice {

  // The name of the faulty file.
  private final String filename;

  // The row of the faulty record.
  private final int csvRowNumber;

  // The length of the faulty record.
  private final int rowLength;

  // The number of column in the faulty file.
  private final int headerCount;

  public InvalidRowLengthNotice(String filename, int csvRowNumber, int rowLength, int headerCount) {
    super(SeverityLevel.ERROR);
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.rowLength = rowLength;
    this.headerCount = headerCount;
  }
}
