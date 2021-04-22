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
 * A field contains a malformed phone number.
 *
 * <p>Definitions for valid phone numbers are quite vague. We perform strict validation in the
 * upstream using the Google libphonenumber.
 *
 * <p><a href="https://github.com/google/transit/blob/master/gtfs/spec/en/reference.md">GTFS
 * reference</a> does not provide any special requirements or standards.
 */
public class InvalidPhoneNumberNotice extends ValidationNotice {

  /**
   * Constructs a notice with given severity. This constructor may be used by users that want to
   * lower the priority to {@code WARNING}.
   */
  public InvalidPhoneNumberNotice(
      String filename,
      long csvRowNumber,
      String fieldName,
      String fieldValue,
      SeverityLevel severityLevel) {
    super(
        ImmutableMap.of(
            "filename",
            filename,
            "csvRowNumber",
            csvRowNumber,
            "fieldName",
            fieldName,
            "fieldValue",
            fieldValue),
        severityLevel);
  }

  /** Constructs a notice with the default severity {@code ERROR}. */
  public InvalidPhoneNumberNotice(
      String filename, long csvRowNumber, String fieldName, String fieldValue) {
    this(filename, csvRowNumber, fieldName, fieldValue, SeverityLevel.ERROR);
  }
}
