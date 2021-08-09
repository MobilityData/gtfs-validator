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

/**
 * A field contains a malformed URL.
 *
 * <p>Definitions for valid URLs are quite vague. We perform strict validation in the upstream using
 * the Apache Common UrlValidator.
 *
 * <p><a href="https://github.com/google/transit/blob/master/gtfs/spec/en/reference.md">GTFS
 * reference</a> describes the requirements in the following way.
 *
 * <p><i>A fully qualified URL that includes http:// or https://, and any special characters in the
 * URL must be correctly escaped. See the following <a
 * href="http://www.w3.org/Addressing/URL/4_URI_Recommentations.html">URI recommentations</a> for a
 * description of how to create fully qualified URL values.</i>
 *
 * <p>However, some production feeds may use certain characters without escaping and those URL may
 * be still openable in modern browsers.
 */
public class InvalidUrlNotice extends ValidationNotice {
  private final String filename;
  private final long csvRowNumber;
  private final String fieldName;
  private final String fieldValue;

  /**
   * Constructs a notice with given severity. This constructor may be used by users that want to
   * lower the priority to {@code WARNING}.
   */
  public InvalidUrlNotice(
      String filename,
      long csvRowNumber,
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
  public InvalidUrlNotice(String filename, long csvRowNumber, String fieldName, String fieldValue) {
    this(filename, csvRowNumber, fieldName, fieldValue, SeverityLevel.ERROR);
  }
}
