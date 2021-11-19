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

import static org.mobilitydata.gtfsvalidator.notice.Notice.GSON;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import org.mobilitydata.gtfsvalidator.model.NoticeReport;
import org.mobilitydata.gtfsvalidator.model.ValidationReportDeserializer;

/**
 * Container for validation notices (errors and warnings).
 *
 * <p>This class is not intentionally not thread-safe to increase performance. Each thread has it's
 * own NoticeContainer, and after execution is complete the results are merged.
 */
public class NoticeContainer {
  /** Limit on the amount notices of the same type and severity. */
  private static final int MAX_VALIDATION_NOTICES_TYPE_AND_SEVERITY = 100_000;

  /**
   * Limit on the total amount of stored validation notices.
   *
   * <p>This is a measure to prevent OOM in the rare case when each row in a large file (such as
   * stop_times.txt or shapes.txt) produces a notice. Since this case is rare, we just introduce a
   * total limit on the amount of notices instead of counting amount of notices of each type.
   *
   * <p>Note that system errors are not limited since we don't expect to have a lot of them.
   */
  private static final int MAX_TOTAL_VALIDATION_NOTICES = 10_000_000;

  /** Limit on the amount of exported notices */
  private static final int MAX_EXPORTS_PER_NOTICE_TYPE_AND_SEVERITY = 1_000;

  private final int maxTotalValidationNotices;
  private final int maxValidationNoticesPerTypeAndSeverity;
  private final int maxExportsPerNoticeTypeAndSeverity;
  private final List<ValidationNotice> validationNotices = new ArrayList<>();
  private final List<SystemError> systemErrors = new ArrayList<>();
  private final Map<String, Integer> noticesCountPerTypeAndSeverity = new HashMap<>();
  private transient boolean hasValidationErrors = false;

  /**
   * Used to specify limits on amount of notices in this {@code NoticeContainer}.
   *
   * @param maxTotalValidationNotices limit on the total amount of {@code Notice}s stored in this
   *     {@code NoticeContainer}
   * @param maxValidationNoticePerTypeAndSeverity limit on the amount of {@code Notice}s of same
   *     type and severity stored in this {@code NoticeContainer}
   * @param maxExportPerNoticeTypeAndSeverity limit on the amount of {@code Notice}s exported from
   *     this {@code NoticeContainer}
   */
  public NoticeContainer(
      int maxTotalValidationNotices,
      int maxValidationNoticePerTypeAndSeverity,
      int maxExportPerNoticeTypeAndSeverity) {
    this.maxTotalValidationNotices = maxTotalValidationNotices;
    this.maxValidationNoticesPerTypeAndSeverity = maxValidationNoticePerTypeAndSeverity;
    this.maxExportsPerNoticeTypeAndSeverity = maxExportPerNoticeTypeAndSeverity;
  }

  /** Used if no constant is provided: limits on amount of notices are set using class constants. */
  public NoticeContainer() {
    this(
        MAX_TOTAL_VALIDATION_NOTICES,
        MAX_VALIDATION_NOTICES_TYPE_AND_SEVERITY,
        MAX_EXPORTS_PER_NOTICE_TYPE_AND_SEVERITY);
  }

  /** Adds a new validation notice to the container (if there is capacity). */
  public void addValidationNotice(ValidationNotice notice) {
    if (notice.isError()) {
      hasValidationErrors = true;
    }
    updateNoticeCount(notice);
    if (validationNotices.size() >= maxTotalValidationNotices
        || noticesCountPerTypeAndSeverity.get(notice.getMappingKey())
            > maxValidationNoticesPerTypeAndSeverity) {
      return;
    }
    validationNotices.add(notice);
  }

  /** Adds a new system error to the container. */
  public void addSystemError(SystemError error) {
    updateNoticeCount(error);
    systemErrors.add(error);
  }

  /**
   * Updates the count of notices per type and severity.
   *
   * @param notice the {@code Notice} whose count should be updated
   */
  private void updateNoticeCount(Notice notice) {
    int count = noticesCountPerTypeAndSeverity.getOrDefault(notice.getMappingKey(), 0);
    noticesCountPerTypeAndSeverity.put(notice.getMappingKey(), count + 1);
  }

  /**
   * Adds all validation notices and system errors from another container.
   *
   * <p>This is useful for multithreaded validation: each thread has its own notice container which
   * is merged into the global container when the thread finishes. Please note that the final {@code
   * NoticeContainer} may contain more than the maximum amount of {@code ValidationNotice} allowed
   * by {@code NoticeContainer#MAX_TOTAL_VALIDATION_NOTICES} and {@code
   * NoticeContainer#MAX_VALIDATION_NOTICES_TYPE_AND_SEVERITY}.
   *
   * @param otherContainer a container to take the notices from
   */
  public void addAll(NoticeContainer otherContainer) {
    validationNotices.addAll(otherContainer.validationNotices);
    systemErrors.addAll(otherContainer.systemErrors);
    hasValidationErrors |= otherContainer.hasValidationErrors;
    for (Entry<String, Integer> entry : otherContainer.noticesCountPerTypeAndSeverity.entrySet()) {
      int count = noticesCountPerTypeAndSeverity.getOrDefault(entry.getKey(), 0);
      noticesCountPerTypeAndSeverity.put(entry.getKey(), count + entry.getValue());
    }
  }

  /** Tells if this container has any {@code ValidationNotice} that is an error. */
  public boolean hasValidationErrors() {
    return hasValidationErrors;
  }

  /** Returns a list of all validation notices in the container. */
  public List<ValidationNotice> getValidationNotices() {
    return Collections.unmodifiableList(validationNotices);
  }

  /** Returns a list of all system errors in the container. */
  public List<SystemError> getSystemErrors() {
    return Collections.unmodifiableList(systemErrors);
  }

  /** Exports all validation notices as JSON. */
  public JsonObject exportValidationNotices() {
    return exportJson(validationNotices);
  }

  /** Exports all system errors as JSON. */
  public JsonObject exportSystemErrors() {
    return exportJson(systemErrors);
  }

  public <T extends Notice> JsonObject exportJson(List<T> notices) {
    return GSON.toJsonTree(
            ValidationReport.fromNoticeCollection(
                notices, maxExportsPerNoticeTypeAndSeverity, noticesCountPerTypeAndSeverity))
        .getAsJsonObject();
  }

  @VisibleForTesting
  static <T extends Notice> ListMultimap<String, T> groupNoticesByTypeAndSeverity(List<T> notices) {
    ListMultimap<String, T> noticesByType = MultimapBuilder.treeKeys().arrayListValues().build();
    for (T notice : notices) {
      noticesByType.put(notice.getMappingKey(), notice);
    }
    return noticesByType;
  }

  /**
   * Used to (de)serialize a {@code NoticeContainer}. This represents a validation report as a list
   * of {@code NoticeReport} which provides information about each notice generated during a GTFS
   * dataset validation. This objects stores both notices and error codes from a list of {@code
   * NoticeReport}. Error codes are cached at construction in this object in order to facilitate
   * quick comparison between reports.
   */
  public static class ValidationReport {

    private static final Gson GSON =
        new GsonBuilder()
            .registerTypeAdapter(ValidationReport.class, new ValidationReportDeserializer())
            .serializeNulls()
            .serializeSpecialFloatingPointValues()
            .create();
    private final Set<NoticeReport> notices;
    private final transient Map<String, NoticeReport> noticesMap;
    private final transient Set<String> errorCodes;

    /**
     * Public constructor needed for deserialization by {@code ValidationReportDeserializer}
     *
     * @param notices set of {@code Notice}s
     * @param errorCodes set of error codes
     */
    public ValidationReport(Set<NoticeReport> notices, Set<String> errorCodes) {
      this.notices = notices;
      this.errorCodes = errorCodes;
      Map<String, NoticeReport> noticesMap = new HashMap<>();
      for (NoticeReport noticeReport : notices) {
        noticesMap.put(noticeReport.getCode(), noticeReport);
      }
      this.noticesMap = noticesMap;
    }

    /**
     * Creates a {@code ValidationReport} from a {@code Path}. Used for deserialization.
     *
     * @param path the path to the json file
     * @return the {@code ValidationReport} that contains the {@code ValidationReport} related to
     *     the json file whose path was passed as parameter.
     */
    public static ValidationReport fromPath(Path path) throws IOException {
      try (BufferedReader reader = Files.newBufferedReader(path)) {
        return GSON.fromJson(reader, ValidationReport.class);
      }
    }

    /**
     * Creates a {@code ValidationReport} from a json string. Used for deserialization in tests.
     *
     * @param jsonString the json string
     * @return the {@code ValidationReport} that contains the {@code ValidationReport} related to
     *     the json string passed as parameter.
     */
    public static ValidationReport fromJsonString(String jsonString) {
      return GSON.fromJson(jsonString, ValidationReport.class);
    }

    public static <T extends Notice> ValidationReport fromNoticeCollection(
        List<T> notices,
        int maxExportsPerNoticeTypeAndSeverity,
        Map<String, Integer> noticesCountPerTypeAndSeverity) {
      Set<NoticeReport> noticeReports = new LinkedHashSet<>();
      Gson gson = new Gson();
      Set<String> errorCodes = new TreeSet<>();
      Type contextType = new TypeToken<Map<String, Object>>() {}.getType();
      for (Collection<T> noticesOfType :
          NoticeContainer.groupNoticesByTypeAndSeverity(notices).asMap().values()) {
        T firstNotice = noticesOfType.iterator().next();
        if (firstNotice.isError()) {
          errorCodes.add(firstNotice.getCode());
        }
        List<LinkedTreeMap<String, Object>> contexts = new ArrayList<>();
        int i = 0;
        for (T notice : noticesOfType) {
          ++i;
          if (i > maxExportsPerNoticeTypeAndSeverity) {
            // Do not export too many notices for this type.
            break;
          }
          contexts.add(gson.fromJson(notice.getContext(), contextType));
        }
        noticeReports.add(
            new NoticeReport(
                firstNotice.getCode(),
                firstNotice.getSeverityLevel(),
                noticesCountPerTypeAndSeverity.get(firstNotice.getMappingKey()),
                contexts));
      }
      return new ValidationReport(noticeReports, errorCodes);
    }
    /**
     * Returns the list of {@code NoticeReport} of this {@code ValidationReport}.
     *
     * @return the list of {@code NoticeReport} of this {@code ValidationReport}.
     */
    public Set<NoticeReport> getNotices() {
      return notices;
    }

    public NoticeReport getNoticeByCode(String noticeCode) {
      return noticesMap.get(noticeCode);
    }

    /**
     * Returns the immutable and ordered set of error codes contained in this {@code
     * ValidationReport}
     *
     * @return the immutable and ordered set of error codes contained in this {@code
     *     ValidationReport}
     */
    public Set<String> getErrorCodes() {
      return errorCodes;
    }

    /**
     * Compares two validation reports: returns true if they contain the same set of error codes.
     *
     * @param otherValidationReport the other {@code ValidationReport}.
     * @return true if the two {@code ValidationReport} contain the same set of error codes, false
     *     otherwise.
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
     * <p>then this methods returns 2 as it contains two new errors codes (invalid_email_address,
     * invalid_url) not present in this {@code ValidationReport}
     *
     * @param other the other {@code ValidationReport}
     * @return the number of new error codes introduced by the other {@code ValidationReport} passed
     *     as parameter.
     */
    public int getNewErrorCount(ValidationReport other) {
      return getNewErrorsListing(other).size();
    }

    /**
     * Returns the listing of new error codes introduced by the other {@code ValidationReport}
     * passed as parameter, e.g. if this {@code ValidationReport} has the following error codes:
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
     * @return the listing of new error codes introduced by the other {@code ValidationReport}
     *     passed as parameter.
     */
    public Set<String> getNewErrorsListing(ValidationReport other) {
      return Sets.difference(other.getErrorCodes(), getErrorCodes());
    }

    /**
     * Determines if two validation reports are equal regardless of the order of the fields in the
     * set of {@code NoticeReport}.
     *
     * @param other the other {@code ValidationReport}.
     * @return true if both validation reports are equal regardless of the order of the fields in
     *     the set of {@code NoticeReport}.
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
}
