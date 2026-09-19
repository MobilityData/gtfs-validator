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

import com.google.common.annotations.VisibleForTesting;
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
import org.mobilitydata.gtfsvalidator.model.ValidationReport;

/**
 * Container for validation notices (errors and warnings).
 *
 * <p>This class is not intentionally not thread-safe to increase performance. Each thread has it's
 * own NoticeContainer, and after execution is complete the results are merged.
 */
public class NoticeContainer {
  /**
   * Limit on the amount notices of the same type and severity.
   *
   * <p>Only {@code MAX_EXPORTS_PER_NOTICE_TYPE_AND_SEVERITY} notices per type and severity end up
   * in a validation report, so retaining orders of magnitude more of them only costs memory.
   */
  @VisibleForTesting static final int MAX_VALIDATION_NOTICES_TYPE_AND_SEVERITY = 2_000;

  /**
   * Limit on the total amount of stored validation notices.
   *
   * <p>This is a measure to prevent OOM in the rare case when each row in a large file (such as
   * stop_times.txt or shapes.txt) produces a notice. Since this case is rare, we just introduce a
   * total limit on the amount of notices instead of counting amount of notices of each type.
   *
   * <p>Note that system errors are not limited since we don't expect to have a lot of them.
   */
  @VisibleForTesting static final int MAX_TOTAL_VALIDATION_NOTICES = 200_000;

  /** Limit on the amount of exported notices */
  private static final int MAX_EXPORTS_PER_NOTICE_TYPE_AND_SEVERITY = 1_000;

  private final int maxTotalValidationNotices;
  private final int maxValidationNoticesPerTypeAndSeverity;
  private final int maxExportsPerNoticeTypeAndSeverity;
  private final List<ResolvedNotice<ValidationNotice>> validationNotices = new ArrayList<>();
  private final List<ResolvedNotice<SystemError>> systemErrors = new ArrayList<>();
  private final Map<String, Integer> noticesCountPerTypeAndSeverity = new HashMap<>();

  /**
   * Amount of notices per type and severity actually kept in {@code validationNotices}.
   *
   * <p>This is deliberately separate from {@code noticesCountPerTypeAndSeverity}: the latter counts
   * every notice ever added, including the ones dropped once a limit is reached, and is what the
   * validation report exports as {@code totalNotices}.
   */
  private final Map<String, Integer> retainedNoticesCountPerTypeAndSeverity = new HashMap<>();

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
    if (resolved.isWarning()) {
      hasValidationWarnings = true;
    }

    updateNoticeCount(resolved);
    if (canRetain(resolved)) {
      validationNotices.add(resolved);
    }
  }

  /**
   * Tells whether the given notice fits in this container, and books the slot if it does.
   *
   * <p>This is the single place where the retention limits are enforced, so that notices added one
   * by one and notices merged from another container are treated the same way.
   */
  private boolean canRetain(ResolvedNotice<?> notice) {
    String mappingKey = notice.getMappingKey();
    int retained = retainedNoticesCountPerTypeAndSeverity.getOrDefault(mappingKey, 0);
    if (retained >= maxValidationNoticesPerTypeAndSeverity) {
      return false;
    }
    // A notice type is described in the exported report only if at least one of its notices was
    // retained, so the first notice of a type and severity is kept even once the total limit is
    // reached: dropping it would hide the type, and its exact count, from the report entirely.
    if (retained > 0 && validationNotices.size() >= maxTotalValidationNotices) {
      return false;
    }
    retainedNoticesCountPerTypeAndSeverity.put(mappingKey, retained + 1);
    return true;
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
   * is merged into the global container when the thread finishes. It is also how row-by-row parsing
   * notices reach the container of a table: {@code CsvFileLoader} merges one container per row.
   *
   * <p>The retention limits of this container are applied to the merged notices, so a feed where
   * every row of a large file yields a notice cannot grow the container without bound. Notice
   * counts are merged in full regardless of retention, so the {@code totalNotices} reported for
   * each notice type stays exact.
   *
   * @param otherContainer a container to take the notices from
   */
  public void addAll(NoticeContainer otherContainer) {
    for (Entry<String, Integer> entry : otherContainer.noticesCountPerTypeAndSeverity.entrySet()) {
      int count = noticesCountPerTypeAndSeverity.getOrDefault(entry.getKey(), 0);
      noticesCountPerTypeAndSeverity.put(entry.getKey(), count + entry.getValue());
    }
    for (ResolvedNotice<ValidationNotice> notice : otherContainer.validationNotices) {
      if (canRetain(notice)) {
        validationNotices.add(notice);
      }
    }
    systemErrors.addAll(otherContainer.systemErrors);
    hasValidationErrors |= otherContainer.hasValidationErrors;
    hasValidationWarnings |= otherContainer.hasValidationWarnings;
  }

  /**
   * Empties this container so that it can be used again.
   *
   * <p>This is meant for the row-by-row parsing loop, which merges one container per row into the
   * container of the table: allocating a container per row of a file with millions of rows is a
   * significant share of the garbage produced while loading a feed. {@link #addAll} copies the
   * notices into the destination container, so resetting the source afterwards is safe.
   */
  public void reset() {
    validationNotices.clear();
    systemErrors.clear();
    noticesCountPerTypeAndSeverity.clear();
    retainedNoticesCountPerTypeAndSeverity.clear();
    hasValidationErrors = false;
    hasValidationWarnings = false;
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

  /**
   * Returns how many notices of the given code and severity were added to this container.
   *
   * <p>This counts every notice the container was given, including the ones it did not retain, and
   * is the number the validation report exports as {@code totalNotices}. It is therefore what a
   * report should show, rather than the size of {@link #getResolvedValidationNotices()}.
   */
  public int getNoticeCount(String code, SeverityLevel severityLevel) {
    return noticesCountPerTypeAndSeverity.getOrDefault(
        ResolvedNotice.mappingKey(code, severityLevel), 0);
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

  public <T extends Notice> ValidationReport createValidationReport(
      List<ResolvedNotice<T>> notices) {
    return ValidationReportDeserializer.createValidationReport(
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
