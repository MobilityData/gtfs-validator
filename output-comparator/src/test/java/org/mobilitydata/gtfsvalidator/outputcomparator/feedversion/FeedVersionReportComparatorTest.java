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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.model.NoticeReport;
import org.mobilitydata.gtfsvalidator.model.ValidationReport;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.outputcomparator.feedversion.FeedVersionDiff.Kind;
import org.mobilitydata.gtfsvalidator.outputcomparator.feedversion.FeedVersionDiff.Status;
import org.mobilitydata.gtfsvalidator.outputcomparator.feedversion.RunMetadata.Evidence;

public class FeedVersionReportComparatorTest {
  private static final String OLD_SHA = "a".repeat(64);
  private static final String NEW_SHA = "b".repeat(64);
  private static final Evidence VERIFIED =
      new Evidence("synthetic-fixture", true, true, true, true, true, true);
  private final FeedVersionReportComparator comparator = new FeedVersionReportComparator();

  @Test
  public void comparesFullCountsAndSortsChanges() {
    ValidationReport before =
        report(
            notice("busy", SeverityLevel.ERROR, 9, 10),
            notice("gone", SeverityLevel.WARNING, 3, 1, 2, 3),
            notice("shift", SeverityLevel.WARNING, 4, 7),
            notice("same", SeverityLevel.INFO, 2, 3, 4));
    ValidationReport after =
        report(
            notice("same", SeverityLevel.INFO, 2, 30, 40),
            notice("shift", SeverityLevel.ERROR, 4, 17),
            notice("busy", SeverityLevel.ERROR, 5, 100),
            notice("added", SeverityLevel.ERROR, 2, 11));

    FeedVersionDiff diff = comparator.compare(before, metadata(OLD_SHA), after, metadata(NEW_SHA));

    assertThat(diff.status()).isEqualTo(Status.COMPARABLE);
    assertThat(diff.changes()).hasSize(5);
    assertThat(diff.changes().stream().map(FeedVersionDiff.NoticeChange::code).toList())
        .containsExactly("added", "busy", "shift", "gone", "shift")
        .inOrder();
    assertThat(diff.changes().stream().map(FeedVersionDiff.NoticeChange::kind).toList())
        .containsExactly(Kind.ADDED, Kind.DECREASED, Kind.ADDED, Kind.REMOVED, Kind.REMOVED)
        .inOrder();
    assertThat(diff.changes().get(1).before()).isEqualTo(9);
    assertThat(diff.changes().get(1).after()).isEqualTo(5);
    assertThat(diff.changes().get(1).delta()).isEqualTo(-4);
    assertThat(diff.changes().get(1).beforeSamplesTruncated()).isTrue();
    assertThat(diff.changes().get(1).afterSamplesTruncated()).isTrue();
    assertThat(diff.changes().get(0).beforeSamplesTruncated()).isFalse();
    assertThat(diff.changes().get(0).afterSamplesTruncated()).isTrue();

    JsonObject json = JsonParser.parseString(diff.toJson()).getAsJsonObject();
    assertThat(json.get("schemaVersion").getAsInt()).isEqualTo(1);
    assertThat(json.get("status").getAsString()).isEqualTo("comparable");
    assertThat(json.getAsJsonArray("changes").get(1).getAsJsonObject().get("delta").getAsLong())
        .isEqualTo(-4);
    assertThat(json.getAsJsonArray("changes").get(1).getAsJsonObject().get("kind").getAsString())
        .isEqualTo("decreased");
    FeedVersionDiff reordered =
        comparator.compare(
            report(
                notice("same", SeverityLevel.INFO, 2, 3, 4),
                notice("shift", SeverityLevel.WARNING, 4, 7),
                notice("gone", SeverityLevel.WARNING, 3, 1, 2, 3),
                notice("busy", SeverityLevel.ERROR, 9, 10)),
            metadata(OLD_SHA),
            report(
                notice("added", SeverityLevel.ERROR, 2, 11),
                notice("busy", SeverityLevel.ERROR, 5, 100),
                notice("shift", SeverityLevel.ERROR, 4, 17),
                notice("same", SeverityLevel.INFO, 2, 30, 40)),
            metadata(NEW_SHA));
    assertThat(reordered.toJson()).isEqualTo(diff.toJson());
  }

  @Test
  public void sameZipIsNotAnotherFeedVersion() {
    FeedVersionDiff diff =
        comparator.compare(
            report(notice("x", SeverityLevel.ERROR, 1, 1)),
            metadata(OLD_SHA),
            report(notice("x", SeverityLevel.ERROR, 2, 1)),
            metadata(OLD_SHA));

    assertThat(diff.status()).isEqualTo(Status.SAME_FEED);
    assertThat(diff.changes()).isEmpty();
    assertThat(diff.diagnosticChanges()).hasSize(1);
    assertThat(diff.diagnosticChanges().get(0).delta()).isEqualTo(1);
    JsonObject json = JsonParser.parseString(diff.toJson()).getAsJsonObject();
    assertThat(json.getAsJsonArray("changes")).isEmpty();
    assertThat(json.getAsJsonArray("diagnosticChanges")).hasSize(1);
  }

  @Test
  public void differentSourceAndMethodReturnNoOrdinaryChanges() {
    ValidationReport empty = report();
    RunMetadata baseline = metadata(OLD_SHA);
    assertThat(
            comparator
                .compare(
                    empty,
                    baseline,
                    empty,
                    metadata(NEW_SHA, "other", "8.0.1", "2026-09-25", "HU", Map.of("-svu", "true")))
                .status())
        .isEqualTo(Status.DIFFERENT_SOURCE);

    RunMetadata changedJar = withJarSha(metadata(NEW_SHA), "f".repeat(64));
    assertThat(comparator.compare(empty, baseline, empty, changedJar).status())
        .isEqualTo(Status.INCOMPATIBLE_METHOD);
    assertThat(
            comparator
                .compare(
                    empty,
                    baseline,
                    empty,
                    metadata(
                        NEW_SHA,
                        "synthetic-feed",
                        "8.0.2",
                        "2026-09-25",
                        "HU",
                        Map.of("-svu", "true")))
                .status())
        .isEqualTo(Status.INCOMPATIBLE_METHOD);
    assertThat(
            comparator
                .compare(
                    empty,
                    baseline,
                    empty,
                    metadata(
                        NEW_SHA,
                        "synthetic-feed",
                        "8.0.1",
                        "2026-09-26",
                        "HU",
                        Map.of("-svu", "true")))
                .status())
        .isEqualTo(Status.INCOMPATIBLE_METHOD);
    assertThat(
            comparator
                .compare(
                    empty,
                    baseline,
                    empty,
                    metadata(
                        NEW_SHA,
                        "synthetic-feed",
                        "8.0.1",
                        "2026-09-25",
                        "AT",
                        Map.of("-svu", "true")))
                .status())
        .isEqualTo(Status.INCOMPATIBLE_METHOD);
    FeedVersionDiff optionsChanged =
        comparator.compare(
            empty,
            baseline,
            empty,
            metadata(
                NEW_SHA, "synthetic-feed", "8.0.1", "2026-09-25", "HU", Map.of("-svu", "false")));
    assertThat(optionsChanged.status()).isEqualTo(Status.INCOMPATIBLE_METHOD);
    assertThat(optionsChanged.reasons()).containsExactly("validationOptions");
    assertThat(optionsChanged.changes()).isEmpty();
  }

  @Test
  public void incompleteProvenanceIsUnverified() {
    ValidationReport empty = report();
    RunMetadata baseline = metadata(OLD_SHA);
    RunMetadata noProof = withEvidence(metadata(NEW_SHA), null);
    FeedVersionDiff diff = comparator.compare(empty, baseline, empty, noProof);
    assertThat(diff.status()).isEqualTo(Status.UNVERIFIED_INPUT);
    assertThat(diff.reasons()).containsExactly("after.evidence");
    assertThat(diff.changes()).isEmpty();
    RunMetadata noOptions =
        metadata(NEW_SHA, "synthetic-feed", "8.0.1", "2026-09-25", "HU", Map.of());
    FeedVersionDiff missingOptions = comparator.compare(empty, baseline, empty, noOptions);
    assertThat(missingOptions.status()).isEqualTo(Status.UNVERIFIED_INPUT);
    assertThat(missingOptions.reasons()).containsExactly("after.validationOptions");
    assertThat(comparator.compare(empty, null, empty, metadata(NEW_SHA)).status())
        .isEqualTo(Status.UNVERIFIED_INPUT);
    assertThat(
            comparator
                .compare(
                    empty,
                    baseline,
                    empty,
                    withEvidence(
                        metadata(NEW_SHA),
                        new Evidence("fixture", true, true, false, true, true, true)))
                .status())
        .isEqualTo(Status.UNVERIFIED_INPUT);
  }

  @Test
  public void jsonDoesNotDiscloseAttestedStringsOrValidationOptions() {
    Map<String, String> options = Map.of("--http_header", "Authorization: Bearer private-token");
    RunMetadata before =
        withEvidence(
            metadata(OLD_SHA, "private-source?key=hidden", "8.0.1", "2026-09-25", "HU", options),
            new Evidence("private-provenance", true, true, true, true, true, true));
    RunMetadata after =
        withEvidence(
            metadata(NEW_SHA, "private-source?key=hidden", "8.0.1", "2026-09-25", "HU", options),
            new Evidence("private-provenance", true, true, true, true, true, true));

    FeedVersionDiff diff = comparator.compare(report(), before, report(), after);
    assertThat(diff.status()).isEqualTo(Status.COMPARABLE);
    String json = diff.toJson();
    assertThat(json).contains(OLD_SHA);
    assertThat(json).doesNotContain("private-token");
    assertThat(json).doesNotContain("private-source");
    assertThat(json).doesNotContain("private-provenance");
    assertThat(json).doesNotContain("--http_header");
    String genericJson = new Gson().toJson(diff);
    assertThat(genericJson).doesNotContain("private-token");
    assertThat(genericJson).doesNotContain("private-source");
    assertThat(genericJson).doesNotContain("private-provenance");
    assertThat(genericJson).doesNotContain("--http_header");
  }

  @Test
  public void invalidMetadataAndReportsCannotProduceChanges() {
    ValidationReport empty = report();
    RunMetadata baseline = metadata(OLD_SHA);
    assertThat(comparator.compare(empty, baseline, null, metadata(NEW_SHA)).status())
        .isEqualTo(Status.INVALID_INPUT);
    assertThat(
            comparator
                .compare(empty, baseline, empty, withFeedSha(metadata(NEW_SHA), "not-a-sha"))
                .status())
        .isEqualTo(Status.INVALID_INPUT);
    assertThat(
            comparator
                .compare(empty, baseline, empty, withExecution(metadata(NEW_SHA), false, true))
                .status())
        .isEqualTo(Status.INVALID_INPUT);
    assertThat(
            comparator
                .compare(empty, baseline, empty, withExecution(metadata(NEW_SHA), true, false))
                .status())
        .isEqualTo(Status.INVALID_INPUT);
    assertThat(
            comparator
                .compare(
                    empty,
                    baseline,
                    empty,
                    withObservedAt(metadata(NEW_SHA), "2026-09-24T00:00:00Z"))
                .status())
        .isEqualTo(Status.INVALID_INPUT);
  }

  @Test
  public void duplicateKeysAndMalformedTotalsAreInvalid() {
    ValidationReport duplicate =
        report(notice("x", SeverityLevel.WARNING, 1, 1), notice("x", SeverityLevel.WARNING, 2, 1));
    FeedVersionDiff diff =
        comparator.compare(duplicate, metadata(OLD_SHA), report(), metadata(NEW_SHA));
    assertThat(diff.status()).isEqualTo(Status.INVALID_INPUT);
    assertThat(diff.reasons()).containsExactly("before.notices.duplicate");
    assertThat(diff.changes()).isEmpty();
    FeedVersionDiff unsafeCode =
        comparator.compare(
            report(notice("Authorization: private-token", SeverityLevel.ERROR, 1)),
            metadata(OLD_SHA),
            report(),
            metadata(NEW_SHA));
    assertThat(unsafeCode.status()).isEqualTo(Status.INVALID_INPUT);
    assertThat(unsafeCode.toJson()).doesNotContain("private-token");
    assertThat(
            comparator
                .compare(
                    report(notice("x", SeverityLevel.ERROR, 0)),
                    metadata(OLD_SHA),
                    report(),
                    metadata(NEW_SHA))
                .status())
        .isEqualTo(Status.INVALID_INPUT);
    assertThat(
            comparator
                .compare(
                    report(notice("x", SeverityLevel.ERROR, 1, 1, 2)),
                    metadata(OLD_SHA),
                    report(),
                    metadata(NEW_SHA))
                .status())
        .isEqualTo(Status.INVALID_INPUT);
  }

  @Test
  public void resultConstructorRejectsArbitraryStrings() {
    try {
      new FeedVersionDiff(
          1, Status.COMPARABLE, List.of("private/path"), null, null, List.of(), List.of());
      fail("arbitrary reason should be rejected");
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessageThat().contains("reasons");
    }
    try {
      new FeedVersionDiff.NoticeChange(
          "Authorization: private-token", SeverityLevel.ERROR, 0, 1, 1, Kind.ADDED, false, false);
      fail("arbitrary notice code should be rejected");
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessageThat().contains("notice code");
    }
  }

  private static ValidationReport report(NoticeReport... notices) {
    return new ValidationReport(new LinkedHashSet<>(Arrays.asList(notices)));
  }

  private static NoticeReport notice(String code, SeverityLevel severity, int total, int... rows) {
    List<JsonElement> samples = new ArrayList<>();
    for (int row : rows) {
      JsonObject sample = new JsonObject();
      sample.addProperty("csvRowNumber", row);
      samples.add(sample);
    }
    return new NoticeReport(code, severity, total, samples);
  }

  private static RunMetadata metadata(String feedSha) {
    return metadata(feedSha, "synthetic-feed", "8.0.1", "2026-09-25", "HU", Map.of("-svu", "true"));
  }

  private static RunMetadata metadata(
      String feedSha,
      String source,
      String version,
      String date,
      String country,
      Map<String, String> options) {
    return new RunMetadata(
        source,
        "2026-09-25T00:00:00Z",
        feedSha,
        version,
        "c".repeat(64),
        "d".repeat(64),
        "e".repeat(64),
        date,
        country,
        options,
        true,
        true,
        VERIFIED);
  }

  private static RunMetadata withJarSha(RunMetadata original, String jarSha) {
    return new RunMetadata(
        original.sourceId(),
        original.observedAtUtc(),
        original.feedSha256(),
        original.validatorVersion(),
        jarSha,
        original.reportSha256(),
        original.systemErrorsSha256(),
        original.validationDate(),
        original.countryCode(),
        original.validationOptions(),
        original.executionSucceeded(),
        original.systemErrorsEmpty(),
        original.evidence());
  }

  private static RunMetadata withEvidence(RunMetadata original, Evidence evidence) {
    return new RunMetadata(
        original.sourceId(),
        original.observedAtUtc(),
        original.feedSha256(),
        original.validatorVersion(),
        original.validatorSha256(),
        original.reportSha256(),
        original.systemErrorsSha256(),
        original.validationDate(),
        original.countryCode(),
        original.validationOptions(),
        original.executionSucceeded(),
        original.systemErrorsEmpty(),
        evidence);
  }

  private static RunMetadata withFeedSha(RunMetadata original, String feedSha) {
    return new RunMetadata(
        original.sourceId(),
        original.observedAtUtc(),
        feedSha,
        original.validatorVersion(),
        original.validatorSha256(),
        original.reportSha256(),
        original.systemErrorsSha256(),
        original.validationDate(),
        original.countryCode(),
        original.validationOptions(),
        original.executionSucceeded(),
        original.systemErrorsEmpty(),
        original.evidence());
  }

  private static RunMetadata withExecution(
      RunMetadata original, boolean success, boolean errorsEmpty) {
    return new RunMetadata(
        original.sourceId(),
        original.observedAtUtc(),
        original.feedSha256(),
        original.validatorVersion(),
        original.validatorSha256(),
        original.reportSha256(),
        original.systemErrorsSha256(),
        original.validationDate(),
        original.countryCode(),
        original.validationOptions(),
        success,
        errorsEmpty,
        original.evidence());
  }

  private static RunMetadata withObservedAt(RunMetadata original, String observedAt) {
    return new RunMetadata(
        original.sourceId(),
        observedAt,
        original.feedSha256(),
        original.validatorVersion(),
        original.validatorSha256(),
        original.reportSha256(),
        original.systemErrorsSha256(),
        original.validationDate(),
        original.countryCode(),
        original.validationOptions(),
        original.executionSucceeded(),
        original.systemErrorsEmpty(),
        original.evidence());
  }
}
