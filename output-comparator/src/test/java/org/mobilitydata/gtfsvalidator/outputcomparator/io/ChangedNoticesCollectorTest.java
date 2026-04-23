package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.model.NoticeReport;
import org.mobilitydata.gtfsvalidator.model.ValidationReport;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.report.AffectedSource;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.report.ChangedNotice;

@RunWith(JUnit4.class)
public class ChangedNoticesCollectorTest {

  @Test
  public void testBasicFunctionality() {
    ChangedNoticesCollector collector = new ChangedNoticesCollector(SeverityLevel.ERROR, 1, 25f);

    // First source has no change in errors.
    collector.compareValidationReports(
        "source-a",
        "source-a-url",
        report(ImmutableMap.of("error_a", 1)),
        report(ImmutableMap.of("error_a", 1)));

    assertThat(collector.isAboveThreshold()).isFalse();
    assertThat(collector.computeInvalidDatasetPercentage()).isWithin(1f).of(0f);
    assertThat(collector.getChangedNotices()).isEmpty();
    assertThat(collector.generateLogString("Test"))
        .isEqualTo(
            "<details>\n"
                + "<summary><strong>Test</strong> (0 out of 1 datasets, ~0%) ✅</summary>\n"
                + "<p>No changes were detected due to the code change.</p>\n"
                + "</details>");

    // Second source has an additional error.
    collector.compareValidationReports(
        "source-b",
        "source-b-url",
        report(ImmutableMap.of("error_a", 1)),
        report(ImmutableMap.of("error_a", 1, "error_b", 2)));

    assertThat(collector.isAboveThreshold()).isTrue();
    assertThat(collector.computeInvalidDatasetPercentage()).isWithin(1f).of(50f);
    assertThat(collector.getChangedNotices())
        .containsExactly(
            new ChangedNotice("error_b")
                .addAffectedSource(AffectedSource.create("source-b", "source-b-url", 2)));
    assertThat(collector.generateLogString("Test"))
        .isEqualTo(
            "<details>\n"
                + "<summary><strong>Test</strong> (1 out of 2 datasets, ~50%) ❌</summary>\n"
                + "<p>Details of new errors due to code change, which is above the provided threshold of 25%.</p>\n"
                + "\n"
                + "| Dataset | Notice Code |\n"
                + "|---------|-------------|\n"
                + "| source-b | error_b |\n"
                + "</details>");
  }

  @Test
  public void testInvalidErrorThreshold() {
    // Note the error threshold of 2.
    ChangedNoticesCollector collector = new ChangedNoticesCollector(SeverityLevel.ERROR, 2, 25f);

    // Only one new error.
    collector.compareValidationReports(
        "source-a",
        "source-a-url",
        report(ImmutableMap.of("error_a", 1)),
        report(ImmutableMap.of("error_a", 1, "error_b", 5)));

    assertThat(collector.isAboveThreshold()).isFalse();
    assertThat(collector.computeInvalidDatasetPercentage()).isWithin(1f).of(0f);
    // We still note the changed notice, even if it isn't enough to mark the dataset as invalid.
    assertThat(collector.getChangedNotices())
        .containsExactly(
            new ChangedNotice("error_b")
                .addAffectedSource(AffectedSource.create("source-a", "source-a-url", 5)));
    assertThat(collector.generateLogString("Test"))
        .isEqualTo(
            "<details>\n"
                + "<summary><strong>Test</strong> (0 out of 1 datasets, ~0%) ✅</summary>\n"
                + "<p>Details of new errors due to code change, which is less than the provided threshold of 25%.</p>\n"
                + "\n"
                + "| Dataset | Notice Code |\n"
                + "|---------|-------------|\n"
                + "| source-a | error_b |\n"
                + "</details>");
  }

  private ValidationReport report(ImmutableMap<String, Integer> error_codes_and_counts) {
    Set<NoticeReport> notices = new HashSet<>();
    for (Map.Entry<String, Integer> entry : error_codes_and_counts.entrySet()) {
      notices.add(
          new NoticeReport(
              entry.getKey(), SeverityLevel.ERROR, entry.getValue(), new ArrayList<>()));
    }
    return new ValidationReport(notices);
  }
}
