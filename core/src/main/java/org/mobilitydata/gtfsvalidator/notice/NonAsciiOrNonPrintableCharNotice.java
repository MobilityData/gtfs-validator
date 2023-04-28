/*
 * Copyright 2021 MobilityData IO
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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.UrlRef;

/**
 * ID value contains something different from printable ASCII characters.
 *
 * <p>An ID field value is an internal ID, not intended to be shown to riders, and is a sequence of
 * any UTF-8 characters. Using only printable ASCII characters is recommended.
 *
 * <p>Severity: {@code SeverityLevel.WARNING}
 */
@GtfsValidationNotice(
    severity = WARNING,
    urls = {
      @UrlRef(
          label = "Original Python validator implementation",
          url = "https://github.com/google/transitfeed")
    })
public class NonAsciiOrNonPrintableCharNotice extends ValidationNotice {

  // Name of the faulty file.
  private final String filename;

  // Row number of the faulty record.
  private final int csvRowNumber;

  // Name of the column where the error occurred.
  private final String columnName;

  // Faulty value.
  private final String fieldValue;

  public NonAsciiOrNonPrintableCharNotice(
      String filename, int csvRowNumber, String columnName, String fieldValue) {
    super(SeverityLevel.WARNING);
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.columnName = columnName;
    this.fieldValue = fieldValue;
  }
}
