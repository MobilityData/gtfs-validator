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

/**
 * A field contains a malformed phone number.
 *
 * <p>This rule uses the
 * [PhoneNumberUtil](https://www.javadoc.io/doc/com.googlecode.libphonenumber/libphonenumber/8.4.1/com/google/i18n/phonenumbers/PhoneNumberUtil.html)
 * class to validate a phone number based on a country code. If no country code is provided in the
 * parameters used to run the validator, this notice won't be emitted.
 */
@GtfsValidationNotice(severity = ERROR, sections = @SectionRefs(FILED_TYPES))
public class InvalidPhoneNumberNotice extends ValidationNotice {

  /** The name of the faulty file. */
  private final String filename;

  /** The row of the faulty record. */
  private final int csvRowNumber;

  /** Faulty record's field name. */
  private final String fieldName;

  /** Faulty value. */
  private final String fieldValue;

  /**
   * Constructs a notice with given severity. This constructor may be used by users that want to
   * lower the priority to {@code WARNING}.
   */
  public InvalidPhoneNumberNotice(
      String filename,
      int csvRowNumber,
      String fieldName,
      String fieldValue,
      SeverityLevel severityLevel) {
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
  }

  /** Constructs a notice with the default severity {@code ERROR}. */
  public InvalidPhoneNumberNotice(
      String filename, int csvRowNumber, String fieldName, String fieldValue) {
    this(filename, csvRowNumber, fieldName, fieldValue, SeverityLevel.ERROR);
  }
}
