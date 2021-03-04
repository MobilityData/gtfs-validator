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
 * The values of the given key and rows are duplicates.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class DuplicateKeyError extends ValidationNotice {
  public DuplicateKeyError(
      String filename,
      long oldCsvRowNumber,
      long newCsvRowNumber,
      String fieldName,
      Object fieldValue) {
    super(
        ImmutableMap.of(
            "filename",
            filename,
            "oldCsvRowNumber",
            oldCsvRowNumber,
            "newCsvRowNumber",
            newCsvRowNumber,
            fieldName,
            fieldValue),
        SeverityLevel.ERROR);
  }

  public DuplicateKeyError(
      String filename,
      long oldCsvRowNumber,
      long newCsvRowNumber,
      String fieldName1,
      Object fieldValue1,
      String fieldName2,
      Object fieldValue2) {
    super(
        ImmutableMap.of(
            "filename",
            filename,
            "oldCsvRowNumber",
            oldCsvRowNumber,
            "newCsvRowNumber",
            newCsvRowNumber,
            fieldName1,
            fieldValue1,
            fieldName2,
            fieldValue2),
        SeverityLevel.ERROR);
  }
}
