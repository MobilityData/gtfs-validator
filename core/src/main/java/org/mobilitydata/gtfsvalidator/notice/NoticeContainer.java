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

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.mobilitydata.gtfsvalidator.io.ValidationReportDeserializer;

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
  private final List<ResolvedNotice<ValidationNotice>> validationNotices = new ArrayList<>();
  private final List<ResolvedNotice<SystemError>> systemErrors = new ArrayList<>();
  private final Map<String, Integer> noticesCountPerTypeAndSeverity = new HashMap<>();
  private boolean hasValidationErrors = false;
  private boolean hasValidationWarnings = false;

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
    // TODO: This would be the spot to add customization of notice severity levels in the future.
    SeverityLevel severity = ValidationNotice.getDefaultSeverityLevel(notice.getClass());
    addValidationNoticeWithSeverity(notice, severity);
  }

  public void addValidationNoticeWithSeverity(
      ValidationNotice notice, SeverityLevel severityLevel) {
    ResolvedNotice<ValidationNotice> resolved = new ResolvedNotice<>(notice, severityLevel);
    if (resolved.isError()) {
      hasValidationErrors = true;
    }
    if (resolved.isWarning()) (
      hasValidationWarnings = true;
    }

    updateNoticeCount(resolved);
    if (validationNotices.size() >= maxTotalValidationNotices
        || noticesCountPerTypeAndSeverity.get(resolved.getMappingKey())
            > maxValidationNoticesPerTypeAndSeverity) {
      return;
    }
    validationNotices.add(resolved);
  }

  public <T extends ValidationNotice> NoticeContainer addValidationNotices(Iterable<T> notices) {
    for (T notice : notices) {
      addValidationNotice(notice);
    }
    return this;
  }

  /** Adds a new system error to the container. */
  public void addSystemError(SystemError error) {
    ResolvedNotice<SystemError> resolved = new ResolvedNotice<>(error, SeverityLevel.ERROR);
    updateNoticeCount(resolved);
    systemErrors.add(resolved);
  }

  /**
   * Updates the count of notices per type and severity.
   *
   * @param notice the {@code Notice} whose count should be updated
   */
  private void updateNoticeCount(ResolvedNotice notice) {
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
    hasValidationWarnings |= otherContainer.hasValidationWarnings;
    for (Entry<String, Integer> entry : otherContainer.noticesCountPerTypeAndSeverity.entrySet()) {
      int count = noticesCountPerTypeAndSeverity.getOrDefault(entry.getKey(), 0);
      noticesCountPerTypeAndSeverity.put(entry.getKey(), count + entry.getValue());
    }
  }

  /** Tells if this container has any {@code ValidationNotice} that is an error. */
  public boolean hasValidationErrors() {
    return hasValidationErrors;
  }

  /** Tells if this container has any {@code ValidationNotice} that is a warning. */
  public boolean hasValidationWarnings() {
    return hasValidationWarnings;
  }

  public List<ResolvedNotice<ValidationNotice>> getResolvedValidationNotices() {
    return validationNotices;
  }

  /** Returns a list of all validation notices in the container. */
  public List<ValidationNotice> getValidationNotices() {
    return Lists.transform(validationNotices, ResolvedNotice::getContext);
  }

  /** Returns a list of all system errors in the container. */
  public List<SystemError> getSystemErrors() {
    return Lists.transform(systemErrors, ResolvedNotice::getContext);
  }

  /** Exports all validation notices as JSON. */
  public JsonObject exportValidationNotices() {
    return exportJson(validationNotices);
  }

  /** Exports all system errors as JSON. */
  public JsonObject exportSystemErrors() {
    return exportJson(systemErrors);
  }

  public <T extends Notice> JsonObject exportJson(List<ResolvedNotice<T>> notices) {
    return ValidationReportDeserializer.serialize(
        notices, maxExportsPerNoticeTypeAndSeverity, noticesCountPerTypeAndSeverity);
  }

  public static <T extends Notice>
      ListMultimap<String, ResolvedNotice<T>> groupNoticesByTypeAndSeverity(
          List<ResolvedNotice<T>> notices) {
    ListMultimap<String, ResolvedNotice<T>> noticesByType =
        MultimapBuilder.treeKeys().arrayListValues().build();
    for (ResolvedNotice<T> notice : notices) {
      noticesByType.put(notice.getMappingKey(), notice);
    }
    return noticesByType;
  }
}
