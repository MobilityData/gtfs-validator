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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.UrlRef;

/**
 * The values in the given column of the input rows are out of range.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
@GtfsValidationNotice(
    severity = ERROR,
    sections = @SectionRefs({"file-requirements", "field-types"}),
    urls = {
      @UrlRef(
          label = "Original Python validator implementation",
          url = "https://github.com/google/transitfeed")
    })
public class NumberOutOfRangeNotice extends ValidationNotice {

  // The name of the faulty file.
  private final String filename;

  // The row of the faulty record.
  private final int csvRowNumber;

  // The name of the faulty field.
  private final String fieldName;

  // The type of the faulty field.
  private final String fieldType;

  // Faulty value.
  private final Object fieldValue;

  public NumberOutOfRangeNotice(
      String filename, int csvRowNumber, String fieldName, String fieldType, Object fieldValue) {
    super(SeverityLevel.ERROR);
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.fieldName = fieldName;
    this.fieldType = fieldType;
    this.fieldValue = fieldValue;
  }
}
