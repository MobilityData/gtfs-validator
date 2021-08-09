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

import javax.annotation.Nullable;

/**
 * The values of the given key and rows are duplicates.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class DuplicateKeyNotice extends ValidationNotice {
  private String filename;
  private long oldCsvRowNumber;
  private long newCsvRowNumber;
  private String fieldName1;
  private Object fieldValue1;
  @Nullable private String fieldName2;
  @Nullable private Object fieldValue2;

  public DuplicateKeyNotice(
      String filename,
      long oldCsvRowNumber,
      long newCsvRowNumber,
      String fieldName1,
      Object fieldValue1) {
    super(SeverityLevel.ERROR);
    this.filename = filename;
    this.oldCsvRowNumber = oldCsvRowNumber;
    this.newCsvRowNumber = newCsvRowNumber;
    this.fieldName1 = fieldName1;
    this.fieldValue1 = fieldValue1;
    this.fieldName2 = null;
    this.fieldValue2 = null;
  }

  public DuplicateKeyNotice(
      String filename,
      long oldCsvRowNumber,
      long newCsvRowNumber,
      String fieldName1,
      Object fieldValue1,
      String fieldName2,
      Object fieldValue2) {
    super(SeverityLevel.ERROR);
    this.filename = filename;
    this.oldCsvRowNumber = oldCsvRowNumber;
    this.newCsvRowNumber = newCsvRowNumber;
    this.fieldName1 = fieldName1;
    this.fieldValue1 = fieldValue1;
    this.fieldName2 = fieldName2;
    this.fieldValue2 = fieldValue2;
  }
}
