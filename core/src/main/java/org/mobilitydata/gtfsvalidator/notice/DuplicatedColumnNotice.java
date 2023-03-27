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
 * The input file CSV header has the same column name repeated.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
@GtfsValidationNotice(
    severity = ERROR,
    sections = @SectionRefs({"file-requirements"}),
    urls = {
      @UrlRef(
          label = "Original Python validator implementation",
          url = "https://github.com/google/transitfeed")
    })
public class DuplicatedColumnNotice extends ValidationNotice {

  // The name of the faulty file.
  private final String filename;

  // The name of the faulty field.
  private final String fieldName;

  // Index of the first occurrence.
  private final int firstIndex;

  // Index of the other occurrence.
  private final int secondIndex;

  public DuplicatedColumnNotice(
      String filename, String fieldName, int firstIndex, int secondIndex) {
    super(SeverityLevel.ERROR);
    this.filename = filename;
    this.fieldName = fieldName;
    this.firstIndex = firstIndex;
    this.secondIndex = secondIndex;
  }
}
