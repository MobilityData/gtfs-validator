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
 * The values of the given key and rows of one table cannot be found a values of the given key in
 * another table.
 *
 * <p>This is the case when a foreign key of one table references a non-existing value in its
 * original table.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class ForeignKeyViolationNotice extends ValidationNotice {
  private final String childFilename;
  private final String childFieldName;
  private final String parentFilename;
  private final String parentFieldName;
  private final String fieldValue;
  private final long csvRowNumber;

  public ForeignKeyViolationNotice(
      String childFilename,
      String childFieldName,
      String parentFilename,
      String parentFieldName,
      String fieldValue,
      long csvRowNumber) {
    super(SeverityLevel.ERROR);
    this.childFilename = childFilename;
    this.childFieldName = childFieldName;
    this.parentFilename = parentFilename;
    this.parentFieldName = parentFieldName;
    this.fieldValue = fieldValue;
    this.csvRowNumber = csvRowNumber;
  }
}
