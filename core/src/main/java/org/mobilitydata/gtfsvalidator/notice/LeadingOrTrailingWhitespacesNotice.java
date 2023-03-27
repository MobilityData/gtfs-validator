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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRefs;

/**
 * The value in CSV file has leading or trailing whitespaces.
 *
 * <p>This notice is emitted for values protected with double quotes since whitespaces for
 * non-protected values are trimmed automatically by CSV parser.
 *
 * <p>This is a warning in the upstream validator.
 *
 * <p>GTFS Validator strips whitespaces from protected values. We do not see any use case when such
 * a whitespace may be needed. On the other hand, some real-world feeds use trailing whitespaces for
 * some values and omit them for the others. This is causing the largest problem when a primary key
 * and a foreign key differ just by a whitespace: it is clear that they are intended to be the same,
 * that is why we always strip whitespaces.
 */
@GtfsValidationNotice(severity = WARNING, sections = @SectionRefs({"file-requirements"}))
public class LeadingOrTrailingWhitespacesNotice extends ValidationNotice {

  // The name of the faulty file.
  private final String filename;

  // The row of the faulty record.
  private final int csvRowNumber;

  // Faulty record's field name.
  private final String fieldName;

  // Faulty value.
  private final String fieldValue;

  public LeadingOrTrailingWhitespacesNotice(
      String filename, int csvRowNumber, String fieldName, String fieldValue) {
    super(SeverityLevel.WARNING);
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
  }
}
