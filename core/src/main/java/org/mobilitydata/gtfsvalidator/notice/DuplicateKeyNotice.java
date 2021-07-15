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
 * The values of the given key and rows are duplicates.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class DuplicateKeyNotice extends ValidationNotice {
  public DuplicateKeyNotice(
      String filename,
      long oldCsvRowNumber,
      long newCsvRowNumber,
      String fieldName1,
      Object fieldValue1) {
    super(
        ImmutableMap.of(
            "filename",
            filename,
            "oldCsvRowNumber",
            oldCsvRowNumber,
            "newCsvRowNumber",
            newCsvRowNumber,
            "fieldName1",
            fieldName1,
            "fieldValue1",
            fieldValue1),
        SeverityLevel.ERROR);
  }

  @NoticeExport
  public DuplicateKeyNotice(
      String filename,
      long oldCsvRowNumber,
      long newCsvRowNumber,
      String fieldName1,
      Object fieldValue1,
      String fieldName2,
      Object fieldValue2) {
    super(
        new ImmutableMap.Builder<String, Object>()
            .put("filename", filename)
            .put("oldCsvRowNumber", oldCsvRowNumber)
            .put("newCsvRowNumber", newCsvRowNumber)
            .put("fieldName1", fieldName1)
            .put("fieldValue1", fieldValue1)
            .put("fieldName2", fieldName2)
            .put("fieldValue2", fieldValue2)
            .build(),
        SeverityLevel.ERROR);
  }
}
