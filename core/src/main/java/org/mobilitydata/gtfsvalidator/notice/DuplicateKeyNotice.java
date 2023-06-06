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

import static org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRef.FILE_REQUIREMENTS;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import javax.annotation.Nullable;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.UrlRef;

/**
 * Duplicated entity.
 *
 * <p>The values of the given key and rows are duplicates.
 */
@GtfsValidationNotice(
    severity = ERROR,
    sections = @SectionRefs(FILE_REQUIREMENTS),
    urls = {
      @UrlRef(
          label = "Original Python validator implementation",
          url = "https://github.com/google/transitfeed")
    })
public class DuplicateKeyNotice extends ValidationNotice {

  /** The name of the faulty file */
  private final String filename;

  /** The row of the first occurrence. */
  private final long oldCsvRowNumber;

  /** The row of the other occurrence. */
  private final long newCsvRowNumber;

  /** Composite key's first field name. */
  private final String fieldName1;

  /** Composite key's first value. */
  private final Object fieldValue1;

  /** Composite key's second field name. */
  @Nullable private final String fieldName2;

  /** Composite key's second value. */
  @Nullable private final Object fieldValue2;

  public DuplicateKeyNotice(
      String filename,
      long oldCsvRowNumber,
      long newCsvRowNumber,
      String fieldName1,
      Object fieldValue1) {
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
    super();
    this.filename = filename;
    this.oldCsvRowNumber = oldCsvRowNumber;
    this.newCsvRowNumber = newCsvRowNumber;
    this.fieldName1 = fieldName1;
    this.fieldValue1 = fieldValue1;
    this.fieldName2 = fieldName2;
    this.fieldValue2 = fieldValue2;
  }
}
