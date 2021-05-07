/*
 * Copyright 2020 MobilityData IO
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

package org.mobilitydata.gtfsvalidator.outputcomparator.util;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Used to deserialize a validation report. This represents a validation report as a list of {@code
 * NoticeAggregate} which provides information about each notice generated during a GTFS dataset
 * validation.
 */
public class ValidationReport {
  private static final Gson GSON = new Gson();
  private final List<NoticeAggregate> notices;

  private ValidationReport(List<NoticeAggregate> notices) {
    this.notices = notices;
  }

  public static ValidationReport fromReader(BufferedReader reader) {
    return GSON.fromJson(reader, ValidationReport.class);
  }

  /**
   * Exports the integration test report (map of String, Object) as json.
   *
   * @param integrationReportData integration report content.
   * @param outputBase base path to output.
   * @param integrationReportName integration report name.
   * @throws IOException if an I/O error occurs writing to or creating the file.
   */
  public static void exportIntegrationReportAsJson(
      ImmutableMap<String, Object> integrationReportData,
      String outputBase,
      String integrationReportName)
      throws IOException {
    Gson gson = new GsonBuilder().serializeNulls().create();
    Files.write(
        Paths.get(outputBase, integrationReportName),
        gson.toJson(integrationReportData).getBytes(StandardCharsets.UTF_8));
  }

  public List<NoticeAggregate> getNotices() {
    return Collections.unmodifiableList(notices);
  }

  /**
   * Return the sorted set of error codes in a validation report.
   *
   * @return the sorted set of error codes in a validation report.
   */
  public Set<String> getErrorCodes() {
    Set<String> errorCodes = new TreeSet<>();
    for (NoticeAggregate noticeAggregate : getNotices()) {
      if (noticeAggregate.isError()) {
        errorCodes.add(noticeAggregate.getCode());
      }
    }
    return errorCodes;
  }

  /**
   * Compares two validation reports: returns true if they contain the same set of error codes.
   *
   * @param otherValidationReport the other {@code ValidationReport}.
   * @return true if thw two {@code ValidationReport} contain the same set of error codes, false
   *     otherwise.
   */
  public boolean hasSameErrorCodes(ValidationReport otherValidationReport) {
    return getErrorCodes().equals(otherValidationReport.getErrorCodes());
  }

  /**
   * Compares two validation reports: returns the number of new error codes introduces in the
   * validation report provided as parameter.
   *
   * @param otherValidationReport the other {@code ValidationReport}.
   * @return the number of new error codes introduces in the * validation report provided as
   *     parameter.
   */
  public int getNewErrorCount(ValidationReport otherValidationReport) {
    return (int)
        otherValidationReport.getErrorCodes().stream()
            .filter(errorCode -> !getErrorCodes().contains(errorCode))
            .count();
  }

  /**
   * Determines if two validation reports are equal regardless of the order of the fields in the
   * list of {@code NoticeAggregate}.
   *
   * @param other the other {@code ValidationReport}.
   * @return true if both validation reports are equal regardless of the order of the fields in the
   *     list of {@code NoticeAggregate}.
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof ValidationReport) {
      return getNotices().equals(((ValidationReport) other).getNotices());
    }
    return false;
  }
}
