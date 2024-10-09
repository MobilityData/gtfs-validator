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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.mobilitydata.gtfsvalidator.io.ValidationReportDeserializer;
import org.mobilitydata.gtfsvalidator.performance.MemoryUsage;

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
  private final Double validationTimeSeconds;
  private List<MemoryUsage> memoryUsageRecords;

  /**
   * Public constructor needed for deserialization by {@code ValidationReportDeserializer}. Only
   * stores information for error {@code NoticeReport}.
   *
   * @param noticeReports set of {@code NoticeReport}s
   */
  public ValidationReport(Set<NoticeReport> noticeReports) {
    this(noticeReports, null, null);
  }

  /**
   * Public constructor needed for deserialization by {@code ValidationReportDeserializer}. Only
   * stores information for error {@code NoticeReport}.
   *
   * @param noticeReports set of {@code NoticeReport}s
   * @param validationTimeSeconds the time taken to validate the GTFS dataset
   */
  public ValidationReport(
      Set<NoticeReport> noticeReports,
      Double validationTimeSeconds,
      List<MemoryUsage> memoryUsageRecords) {
    this.notices = Collections.unmodifiableSet(noticeReports);
    this.validationTimeSeconds = validationTimeSeconds;
    this.memoryUsageRecords = memoryUsageRecords;
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

  public Set<NoticeReport> getNotices() {
    return notices;
  }

  public Double getValidationTimeSeconds() {
    return validationTimeSeconds;
  }

  public List<MemoryUsage> getMemoryUsageRecords() {
    return memoryUsageRecords;
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
      return this.notices.equals(otherReport.notices);
    }
    return false;
  }
}
