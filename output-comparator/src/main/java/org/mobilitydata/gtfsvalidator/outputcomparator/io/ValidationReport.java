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

package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/**
 * Used to deserialize validation report. This represents a validation report as a list of {@code
 * NoticeSummary} which provides information about each notice generated during a GTFS dataset
 * validation. This objects stores both notices and error codes from a list of {@code
 * NoticeSummary}. Error codes are cached at construction in this object in order to facilitate
 * quick comparison between reports.
 */
public class ValidationReport {

  private static final Gson GSON =
      new GsonBuilder()
          .registerTypeAdapter(ValidationReport.class, new ValidationReportDeserializer())
          .serializeNulls()
          .serializeSpecialFloatingPointValues()
          .create();
  private final Set<NoticeSummary> notices;
  private final ImmutableSet<String> errorCodes;

  ValidationReport(Set<NoticeSummary> notices, ImmutableSet<String> errorCodes) {
    this.notices = notices;
    this.errorCodes = errorCodes;
  }

  /**
   * Creates a {@code ValidationReport} from a {@code Path}
   *
   * @param path the path to the json file
   * @return the {@code ValidationReport} that contains the {@code ValidationReport} related to the
   * json file whose path was passed as parameter.
   */
  public static ValidationReport fromPath(Path path) throws IOException {
    try (BufferedReader reader = Files.newBufferedReader(path)) {
      return GSON.fromJson(reader, ValidationReport.class);
    }
  }

  /**
   * Creates a {@code ValidationReport} from a json string.
   *
   * @param jsonString the json string
   * @return the {@code ValidationReport} that contains the {@code ValidationReport} related to the
   * json string passed as parameter.
   */
  public static ValidationReport fromJsonString(String jsonString) {
    return GSON.fromJson(jsonString, ValidationReport.class);
  }

  /**
   * Returns the list of {@code NoticeSummary} of this {@code ValidationReport}.
   *
   * @return the list of {@code NoticeSummary} of this {@code ValidationReport}.
   */
  public Set<NoticeSummary> getNotices() {
    return notices;
  }

  /**
   * Returns the immutable and ordered set of error codes contained in this {@code
   * ValidationReport}
   *
   * @return the immutable and ordered set of error codes contained in this {@code ValidationReport}
   */
  public ImmutableSet<String> getErrorCodes() {
    return errorCodes;
  }

  /**
   * Compares two validation reports: returns true if they contain the same set of error codes.
   *
   * @param otherValidationReport the other {@code ValidationReport}.
   * @return true if the two {@code ValidationReport} contain the same set of error codes, false
   * otherwise.
   */
  public boolean hasSameErrorCodes(ValidationReport otherValidationReport) {
    return getErrorCodes().equals(otherValidationReport.getErrorCodes());
  }

  /**
   * Returns the number of new error codes introduced by the other {@code ValidationReport} passed
   * as parameter, e.g. if this {@code ValidationReport} has the following error codes:
   *
   * <ul>
   *   <li>invalid_phone_number;
   *   <li>number_out_of_range;
   * </ul>
   * <p>
   * and the other {@code ValidationReport} has the following error codes:
   *
   * <ul>
   *   <li>invalid_phone_number;
   *   <li>number_out_of_range;
   *   <li>invalid_email_address;
   *   <li>invalid_url;
   * </ul>
   *
   * <p>then this methods returns 2 as it contains two new errors codes (invalid_email_address,
   * invalid_url) not present in this {@code ValidationReport}
   *
   * @param other the other {@code ValidationReport}
   * @return the number of new error codes introduced by the other {@code ValidationReport} passed
   * as parameter.
   */
  public int getNewErrorCount(ValidationReport other) {
    return Sets.difference(other.getErrorCodes(), getErrorCodes()).size();
  }

  /**
   * Determines if two validation reports are equal regardless of the order of the fields in the set
   * of {@code NoticeSummary}.
   *
   * @param other the other {@code ValidationReport}.
   * @return true if both validation reports are equal regardless of the order of the fields in the
   * set of {@code NoticeSummary}.
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof ValidationReport) {
      ValidationReport otherReport = (ValidationReport) other;
      return getNotices().equals(otherReport.getNotices());
    }
    return false;
  }
}
