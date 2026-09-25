/*
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

package org.mobilitydata.gtfsvalidator.outputcomparator.feedversion;

import com.google.gson.JsonElement;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.mobilitydata.gtfsvalidator.model.NoticeReport;
import org.mobilitydata.gtfsvalidator.model.ValidationReport;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.outputcomparator.feedversion.FeedVersionDiff.Kind;
import org.mobilitydata.gtfsvalidator.outputcomparator.feedversion.FeedVersionDiff.NoticeChange;
import org.mobilitydata.gtfsvalidator.outputcomparator.feedversion.FeedVersionDiff.RunSummary;
import org.mobilitydata.gtfsvalidator.outputcomparator.feedversion.FeedVersionDiff.Status;

/**
 * Compares two versions of one feed validated by the same method. This is independent of the
 * acceptance-test comparator, which compares two validator versions against the same feed.
 *
 * <p>The caller must verify file hashes, report summaries, and system errors before setting the
 * corresponding {@link RunMetadata.Evidence} flags. The core {@code ValidationReport} deserializer
 * stores notices in a set, so a loader must also reject exact duplicate JSON groups before
 * deserialization; duplicates still visible in the set are rejected here. This API does no I/O.
 */
public final class FeedVersionReportComparator {
  private static final Comparator<GroupKey> GROUP_ORDER =
      Comparator.comparingInt((GroupKey key) -> severityOrder(key.severity()))
          .thenComparing(GroupKey::code);

  private static int severityOrder(SeverityLevel severity) {
    return switch (severity) {
      case ERROR -> 0;
      case WARNING -> 1;
      case INFO -> 2;
    };
  }

  public FeedVersionDiff compare(
      ValidationReport beforeReport,
      RunMetadata before,
      ValidationReport afterReport,
      RunMetadata after) {
    List<String> invalid = new ArrayList<>();
    List<String> unverified = new ArrayList<>();
    inspectMetadata(before, "before", invalid, unverified);
    inspectMetadata(after, "after", invalid, unverified);
    Map<GroupKey, GroupCount> beforeGroups = inspectReport(beforeReport, "before", invalid);
    Map<GroupKey, GroupCount> afterGroups = inspectReport(afterReport, "after", invalid);

    if (!invalid.isEmpty()) {
      invalid.sort(String::compareTo);
      return result(Status.INVALID_INPUT, invalid, before, after, List.of());
    }
    if (!unverified.isEmpty()) {
      unverified.sort(String::compareTo);
      return result(Status.UNVERIFIED_INPUT, unverified, before, after, List.of());
    }
    if (parseUtc(before.observedAtUtc()).isAfter(parseUtc(after.observedAtUtc()))) {
      return result(Status.INVALID_INPUT, List.of("observedAtUtc.order"), before, after, List.of());
    }
    if (!before.sourceId().equals(after.sourceId())) {
      return result(Status.DIFFERENT_SOURCE, List.of("sourceId"), before, after, List.of());
    }

    List<String> methodChanges = new ArrayList<>();
    if (!before.validatorVersion().equals(after.validatorVersion())) {
      methodChanges.add("validatorVersion");
    }
    if (!before.validatorSha256().equals(after.validatorSha256())) {
      methodChanges.add("validatorSha256");
    }
    if (!before.validationDate().equals(after.validationDate())) {
      methodChanges.add("validationDate");
    }
    if (!before.countryCode().equals(after.countryCode())) {
      methodChanges.add("countryCode");
    }
    if (!before.validationOptions().equals(after.validationOptions())) {
      methodChanges.add("validationOptions");
    }
    if (!methodChanges.isEmpty()) {
      return result(Status.INCOMPATIBLE_METHOD, methodChanges, before, after, List.of());
    }
    List<NoticeChange> changes = countChanges(beforeGroups, afterGroups);
    if (before.feedSha256().equals(after.feedSha256())) {
      return new FeedVersionDiff(
          1,
          Status.SAME_FEED,
          List.of("feedSha256"),
          RunSummary.from(before),
          RunSummary.from(after),
          List.of(),
          changes);
    }
    return result(Status.COMPARABLE, List.of(), before, after, changes);
  }

  private static List<NoticeChange> countChanges(
      Map<GroupKey, GroupCount> beforeGroups, Map<GroupKey, GroupCount> afterGroups) {
    Set<GroupKey> keys = new TreeSet<>(GROUP_ORDER);
    keys.addAll(beforeGroups.keySet());
    keys.addAll(afterGroups.keySet());
    List<NoticeChange> changes = new ArrayList<>();
    for (GroupKey key : keys) {
      GroupCount oldGroup = beforeGroups.get(key);
      GroupCount newGroup = afterGroups.get(key);
      long oldCount = oldGroup == null ? 0 : oldGroup.total();
      long newCount = newGroup == null ? 0 : newGroup.total();
      if (oldCount == newCount) {
        continue;
      }
      Kind kind =
          oldGroup == null
              ? Kind.ADDED
              : newGroup == null
                  ? Kind.REMOVED
                  : newCount > oldCount ? Kind.INCREASED : Kind.DECREASED;
      changes.add(
          new NoticeChange(
              key.code(),
              key.severity(),
              oldCount,
              newCount,
              newCount - oldCount,
              kind,
              oldGroup != null && oldGroup.truncated(),
              newGroup != null && newGroup.truncated()));
    }
    return changes;
  }

  private static FeedVersionDiff result(
      Status status,
      List<String> reasons,
      RunMetadata before,
      RunMetadata after,
      List<NoticeChange> changes) {
    return new FeedVersionDiff(
        1, status, reasons, RunSummary.from(before), RunSummary.from(after), changes, List.of());
  }

  private static void inspectMetadata(
      RunMetadata metadata, String side, List<String> invalid, List<String> unverified) {
    if (metadata == null) {
      unverified.add(side + ".metadata");
      return;
    }
    requireText(metadata.sourceId(), side + ".sourceId", unverified);
    requireText(metadata.observedAtUtc(), side + ".observedAtUtc", unverified);
    requireText(metadata.validatorVersion(), side + ".validatorVersion", unverified);
    requireText(metadata.countryCode(), side + ".countryCode", unverified);
    checkSha(metadata.feedSha256(), side + ".feedSha256", invalid, unverified);
    checkSha(metadata.validatorSha256(), side + ".validatorSha256", invalid, unverified);
    checkSha(metadata.reportSha256(), side + ".reportSha256", invalid, unverified);
    checkSha(metadata.systemErrorsSha256(), side + ".systemErrorsSha256", invalid, unverified);
    if (missing(metadata.observedAtUtc())) {
      // Already reported as unverified.
    } else if (parseUtc(metadata.observedAtUtc()) == null) {
      invalid.add(side + ".observedAtUtc");
    }
    if (missing(metadata.validationDate())) {
      unverified.add(side + ".validationDate");
    } else {
      try {
        if (!LocalDate.parse(metadata.validationDate())
            .toString()
            .equals(metadata.validationDate())) {
          invalid.add(side + ".validationDate");
        }
      } catch (DateTimeParseException exception) {
        invalid.add(side + ".validationDate");
      }
    }
    if (metadata.validationOptions() == null || metadata.validationOptions().isEmpty()) {
      unverified.add(side + ".validationOptions");
    } else {
      for (Map.Entry<String, String> option : metadata.validationOptions().entrySet()) {
        if (missing(option.getKey()) || missing(option.getValue())) {
          invalid.add(side + ".validationOptions");
          break;
        }
      }
    }
    if (!metadata.executionSucceeded()) {
      invalid.add(side + ".executionSucceeded");
    }
    if (!metadata.systemErrorsEmpty()) {
      invalid.add(side + ".systemErrorsEmpty");
    }
    if (metadata.evidence() == null || !metadata.evidence().complete()) {
      unverified.add(side + ".evidence");
    }
  }

  private static void requireText(String value, String field, List<String> unverified) {
    if (missing(value)) {
      unverified.add(field);
    }
  }

  private static boolean missing(String value) {
    return value == null || value.isBlank();
  }

  private static void checkSha(
      String value, String field, List<String> invalid, List<String> unverified) {
    if (missing(value)) {
      unverified.add(field);
    } else if (!value.matches("[0-9a-f]{64}")) {
      invalid.add(field);
    }
  }

  private static OffsetDateTime parseUtc(String value) {
    try {
      OffsetDateTime parsed = OffsetDateTime.parse(value);
      return parsed.getOffset().equals(ZoneOffset.UTC) ? parsed : null;
    } catch (DateTimeParseException exception) {
      return null;
    }
  }

  private static Map<GroupKey, GroupCount> inspectReport(
      ValidationReport report, String side, List<String> invalid) {
    Map<GroupKey, GroupCount> groups = new HashMap<>();
    if (report == null || report.getNotices() == null) {
      invalid.add(side + ".report");
      return groups;
    }
    for (NoticeReport notice : report.getNotices()) {
      if (notice == null
          || !validNoticeCode(notice.getCode())
          || notice.getSeverity() == null
          || notice.getTotalNotices() <= 0
          || notice.getSampleNotices() == null) {
        invalid.add(side + ".notices.malformed");
        continue;
      }
      List<JsonElement> samples = notice.getSampleNotices();
      if (samples.size() > notice.getTotalNotices()
          || samples.stream().anyMatch(sample -> sample == null || !sample.isJsonObject())) {
        invalid.add(side + ".notices.samples");
        continue;
      }
      GroupKey key = new GroupKey(notice.getCode(), notice.getSeverity());
      if (groups.putIfAbsent(
              key,
              new GroupCount(notice.getTotalNotices(), samples.size() < notice.getTotalNotices()))
          != null) {
        invalid.add(side + ".notices.duplicate");
      }
    }
    return groups;
  }

  private record GroupKey(String code, SeverityLevel severity) {}

  private static boolean validNoticeCode(String code) {
    return code != null && code.matches("[a-z][a-z0-9_]{0,127}");
  }

  private record GroupCount(int total, boolean truncated) {}
}
