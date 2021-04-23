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
 * The value in CSV file has leading or trailing whitespaces.
 *
 * <p>This notice is emitted for values protected with double quotes since whitespaces for
 * non-protected values are trimmed automatically by CSV parser.
 *
 * <p>This is an error in the upstream validator but GTFS consumers can patch it to be a warning if
 * they have feeds that give leading or trailing whitespaces.
 *
 * <p>GTFS Validator strips whitespaces from protected values. We do not see any use case when such
 * a whitespace may be needed. On the other hand, some real-world feeds use trailing whitespaces for
 * some values and omit them for the others. This is causing the largest problem when a primary key
 * and a foreign key differ just by a whitespace: it is clear that they are intended to be the same,
 * that is why we always strip whitespaces.
 */
public class LeadingOrTrailingWhitespacesNotice extends ValidationNotice {

  /**
   * Constructs a notice with given severity. This constructor may be used by users that want to
   * lower the priority to {@code WARNING}.
   */
  public LeadingOrTrailingWhitespacesNotice(
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
  public LeadingOrTrailingWhitespacesNotice(
      String filename, long csvRowNumber, String fieldName, String fieldValue) {
    this(filename, csvRowNumber, fieldName, fieldValue, SeverityLevel.ERROR);
  }
}
