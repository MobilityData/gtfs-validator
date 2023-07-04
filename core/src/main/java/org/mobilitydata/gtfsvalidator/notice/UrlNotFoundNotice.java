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

import static org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRef.FILED_TYPES;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRefs;

/**
 * A field contains an inaccessible URL.
 *
 * <p>This notice is triggered if a URL field is inaccessible i.e. returns a 404 status code.
 */
@GtfsValidationNotice(severity = WARNING, sections = @SectionRefs(FILED_TYPES))
public class UrlNotFoundNotice extends ValidationNotice {

  /** The name of the faulty file. */
  private final String filename;

  /** The row of the faulty record. */
  private final int csvRowNumber;

  /** Faulty record's field name. */
  private final String fieldName;

  /** Faulty value. */
  private final String fieldValue;

  public UrlNotFoundNotice(String filename, int csvRowNumber, String fieldName, String fieldValue) {
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
  }
}
