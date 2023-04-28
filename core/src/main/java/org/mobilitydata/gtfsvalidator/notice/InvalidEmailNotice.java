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
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.UrlRef;

/**
 * A field contains a malformed email address.
 *
 * <p>Definitions for valid emails are quite vague. We perform strict validation in the upstream
 * using the Apache Commons EmailValidator.
 *
 * <p><a href="https://github.com/google/transit/blob/master/gtfs/spec/en/reference.md">GTFS
 * reference</a> does not provide any special requirements or standards.
 */
@GtfsValidationNotice(
    severity = ERROR,
    sections = @SectionRefs(FILED_TYPES),
    urls = {
      @UrlRef(
          label = "Apache Commons EmailValidator",
          url =
              "https://commons.apache.org/proper/commons-validator/apidocs/org/apache/commons/validator/routines/EmailValidator.html")
    })
public class InvalidEmailNotice extends ValidationNotice {

  // The name of the faulty file.
  private final String filename;

  // The row of the faulty record.
  private final int csvRowNumber;

  // Faulty record's field name.
  private final String fieldName;

  // Faulty value.
  private final String fieldValue;

  /**
   * Constructs a notice with given severity. This constructor may be used by users that want to
   * lower the priority to {@code WARNING}.
   */
  public InvalidEmailNotice(
      String filename,
      int csvRowNumber,
      String fieldName,
      String fieldValue,
      SeverityLevel severityLevel) {
    super(severityLevel);
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
  }

  /** Constructs a notice with the default severity {@code ERROR}. */
  public InvalidEmailNotice(
      String filename, int csvRowNumber, String fieldName, String fieldValue) {
    this(filename, csvRowNumber, fieldName, fieldValue, SeverityLevel.ERROR);
  }
}
