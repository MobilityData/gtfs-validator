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

package org.mobilitydata.gtfsvalidator.model;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.mobilitydata.gtfsvalidator.io.ValidationReportDeserializer;

/**
 * Used to (de)serialize a {@code NoticeContainer}. This represents a validation report as a list of
 * {@code NoticeReport}. This {@code ValidationReport} stores both notices and error codes from a
 * list of {@code NoticeReport}. Error codes are cached at construction in this {@code
 * ValidationReport} in order to facilitate quick comparison between reports.
 */
public class ValidationReport {

  private static final Gson GSON =
      new GsonBuilder()
          .registerTypeAdapter(ValidationReport.class, new ValidationReportDeserializer())
          .serializeNulls()
          .serializeSpecialFloatingPointValues()
          .create();
  private final Set<NoticeReport> notices;
  private final transient Map<String, NoticeReport> errorNoticeByCode;

  /**
   * Public constructor needed for deserialization by {@code ValidationReportDeserializer}. Only
   * stores information for error {@code NoticeReport}.
   *
   * @param noticeReports set of {@code NoticeReport}s
   */
  public ValidationReport(Set<NoticeReport> noticeReports) {
    this.notices = Collections.unmodifiableSet(noticeReports);
    Map<String, NoticeReport> errorNoticeByCode = new HashMap<>();
    for (NoticeReport noticeReport : noticeReports) {
      if (noticeReport.isError()) {
        errorNoticeByCode.put(noticeReport.getCode(), noticeReport);
      }
    }
    this.errorNoticeByCode = errorNoticeByCode;
  }

  /**
   * Creates a {@code ValidationReport} from a {@code Path}. Used for deserialization.
   *
   * @param path the path to the json file
   * @return the {@code ValidationReport} that contains the {@code ValidationReport} related to the
   *     json file whose path was passed as parameter.
   */
  public static ValidationReport fromPath(Path path) throws IOException {
    try (BufferedReader reader = Files.newBufferedReader(path)) {
      return GSON.fromJson(reader, ValidationReport.class);
    }
  }

  /** Returns all error notice reports {@code NoticeReport} of this {@code ValidationReport}. */
  public Set<NoticeReport> getErrorNotices() {
    return notices;
  }

  /**
   * Returns the error {@code NoticeReport} related to the {@code Notice} whose notice code has been
   * provided as parameter. If the requested notice code is not present in this {@code
   * ValidationReport}, null is returned.
   *
   * @param noticeCode the notice code related to the {@code NoticeReport} to be returned
   */
  @Nullable
  public NoticeReport getErrorNoticeReportByNoticeCode(String noticeCode) {
    return errorNoticeByCode.get(noticeCode);
  }

  /**
   * Compares two validation reports: returns true if they contain the same set of error codes.
   *
   * @param otherValidationReport the other {@code ValidationReport}.
   * @return true if the two {@code ValidationReport} contain the same set of error codes, false
   *     otherwise.
   */
  public boolean hasSameErrorCodes(ValidationReport otherValidationReport) {
    return this.errorNoticeByCode.keySet().equals(otherValidationReport.errorNoticeByCode.keySet());
  }

  /**
   * Returns the listing of new error codes introduced by the other {@code ValidationReport} passed
   * as parameter, e.g. if this {@code ValidationReport} has the following error codes:
   *
   * <ul>
   *   <li>invalid_phone_number;
   *   <li>number_out_of_range;
   * </ul>
   *
   * <p>and the other {@code ValidationReport} has the following error codes:
   *
   * <ul>
   *   <li>invalid_phone_number;
   *   <li>number_out_of_range;
   *   <li>invalid_email_address;
   *   <li>invalid_url;
   * </ul>
   *
   * <p>then this returns a {@code Set} that contains the two new errors codes
   * (invalid_email_address, invalid_url) not present in this {@code ValidationReport}
   *
   * @param other the other {@code ValidationReport}
   * @return the listing of new error codes introduced by the other {@code ValidationReport} passed
   *     as parameter.
   */
  public Set<String> getNewErrorsListing(ValidationReport other) {
    return Sets.difference(other.errorNoticeByCode.keySet(), this.errorNoticeByCode.keySet());
  }

  /**
   * Determines if two validation reports are equal regardless of the order of the fields in the set
   * of {@code NoticeReport}.
   *
   * @param other the other {@code ValidationReport}.
   * @return true if both validation reports are equal regardless of the order of the fields in the
   *     set of {@code NoticeReport}.
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof ValidationReport) {
      ValidationReport otherReport = (ValidationReport) other;
      return getErrorNotices().equals(otherReport.getErrorNotices());
    }
    return false;
  }
}
