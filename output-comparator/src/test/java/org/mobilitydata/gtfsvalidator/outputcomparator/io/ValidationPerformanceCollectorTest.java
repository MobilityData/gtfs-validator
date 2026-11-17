package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import static com.google.common.truth.Truth.assertThat;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.model.ValidationReport;
import org.mobilitydata.gtfsvalidator.outputcomparator.io.ValidationPerformanceCollector.GroupSummary;
import org.mobilitydata.gtfsvalidator.outputcomparator.io.ValidationPerformanceCollector.Metric;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.FeedRun;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.report.ValidationPerformance;
import org.mobilitydata.gtfsvalidator.performance.ValidationPerformanceMetrics;

public class ValidationPerformanceCollectorTest {

  private static ValidationReport report(
      double inputSeconds, double validationSeconds, long allocatedBytes, Long peakHeapBytes) {
    Map<String, Double> phases = new LinkedHashMap<>();
    phases.put(ValidationPerformanceMetrics.PHASE_INPUT, inputSeconds);
    phases.put(ValidationPerformanceMetrics.PHASE_LOAD_TABLES, validationSeconds);
    return new ValidationReport(
        Collections.emptySet(),
        inputSeconds + validationSeconds,
        null,
        new ValidationPerformanceMetrics(
            phases, validationSeconds, validationSeconds, allocatedBytes, peakHeapBytes, 0L, 0.0));
  }

  private static ValidationReport reportWithLiveHeap(long allocatedBytes, long liveHeapBytes) {
    return new ValidationReport(
        Collections.emptySet(),
        1.0,
        null,
        new ValidationPerformanceMetrics(
            Map.of(ValidationPerformanceMetrics.PHASE_LOAD_TABLES, 1.0),
            Map.of(ValidationPerformanceMetrics.PHASE_LOAD_TABLES, liveHeapBytes),
            1.0,
            1.0,
            allocatedBytes,
            null,
            0L,
            0.0));
  }

  private static ValidationReport legacyReport(double validationTimeSeconds) {
    return new ValidationReport(Collections.emptySet(), validationTimeSeconds, null);
  }

  /** Returns the collapsible section of the report for one metric, or an empty string. */
  private static String section(String log, String metricLabel) {
    int start = log.indexOf("<strong>" + metricLabel + "</strong>");
    return start < 0 ? "" : log.substring(start, log.indexOf("</details>", start));
  }

  /** A feed of the given zip size whose reports were both produced in the same job. */
  private static FeedRun feed(double megabytes) {
    return new FeedRun((long) (megabytes * 1_000_000), false);
  }

  /** A feed of the given zip size whose reference report was reused from an earlier run. */
  private static FeedRun reusedFeed(double megabytes) {
    return new FeedRun((long) (megabytes * 1_000_000), true);
  }

  @Test
  public void noChange_isNotFlagged() {
    ValidationPerformanceCollector collector = new ValidationPerformanceCollector();
    for (int i = 0; i < 20; i++) {
      // Timing noise of a few percent in both directions, identical allocations.
      double noise = i % 2 == 0 ? 1.03 : 0.97;
      collector.compareValidationReports(
          "feed-" + i,
          report(0.1, 1, 1_000_000, null),
          report(0.1, noise, 1_000_000, null),
          feed(1));
    }

    String log = collector.generateLogString();

    assertThat(log).contains("No significant performance change detected.");
    assertThat(section(log, "Allocated memory"))
        .contains("| < 10 MB | 20 | +0.0% | +0.0% [+0.0%, +0.0%] | - |");
  }

  @Test
  public void allocationRegressionInOneSizeGroup_isFlaggedForThatGroupOnly() {
    ValidationPerformanceCollector collector = new ValidationPerformanceCollector();
    for (int i = 0; i < 10; i++) {
      collector.compareValidationReports(
          "small-" + i, report(0, 1, 1_000_000, null), report(0, 1, 1_000_000, null), feed(1));
      collector.compareValidationReports(
          "medium-" + i, report(0, 10, 1_000_000, null), report(0, 10, 1_200_000, null), feed(30));
    }

    String log = collector.generateLogString();

    assertThat(log)
        .contains(
            "- Regression **Allocated memory** for feeds of 10 MB to 50 MB: +20.0% [+20.0%,"
                + " +20.0%]");
    assertThat(section(log, "Allocated memory"))
        .contains("| < 10 MB | 10 | +0.0% | +0.0% [+0.0%, +0.0%] | - |");
    assertThat(log).doesNotContain("No significant performance change detected.");
  }

  /**
   * Small feeds never trigger a collection, so only the live heap can show a memory improvement on
   * them, even when allocations slightly increase.
   */
  @Test
  public void eachMetric_hasItsOwnSectionWithDescription() {
    ValidationPerformanceCollector collector = new ValidationPerformanceCollector();
    for (int i = 0; i < 10; i++) {
      collector.compareValidationReports(
          "medium-" + i, report(0, 10, 1_000_000, null), report(0, 10, 1_200_000, null), feed(30));
    }

    String log = collector.generateLogString();

    assertThat(log)
        .contains(
            "<summary><strong>Allocated memory</strong>: Regression for 10 MB to 50 MB</summary>");
    assertThat(log)
        .contains("<summary><strong>Wall time</strong>: no significant change</summary>");
    assertThat(section(log, "Allocated memory"))
        .contains(
            "Total memory the validation allocated. It is nearly identical between runs, so even"
                + " small changes are meaningful. Flagged beyond 1%.");
    assertThat(section(log, "Allocated memory"))
        .contains("| Feed size (zip) | Feeds | Median change |");
  }

  @Test
  public void liveHeapImprovementWithoutCollections_isFlagged() {
    ValidationPerformanceCollector collector = new ValidationPerformanceCollector();
    for (int i = 0; i < 10; i++) {
      collector.compareValidationReports(
          "feed-" + i,
          reportWithLiveHeap(1_000_000, 100_000_000),
          reportWithLiveHeap(1_020_000, 60_000_000),
          feed(1));
    }

    String log = collector.generateLogString();

    assertThat(log)
        .contains("- Improvement **Peak live heap** for feeds of < 10 MB: -40.0% [-40.0%, -40.0%]");
    assertThat(log).contains("- Regression **Allocated memory** for feeds of < 10 MB: +2.0%");
  }

  @Test
  public void wallTime_excludesInputPhase() {
    ValidationPerformanceCollector collector = new ValidationPerformanceCollector();
    for (int i = 0; i < 5; i++) {
      // The latest run spent 5 more seconds downloading the feed, the validation took the same.
      collector.compareValidationReports(
          "feed-" + i, report(1, 30, 1_000_000, 500L), report(6, 30, 1_000_000, 500L), feed(75));
    }

    String log = collector.generateLogString();

    assertThat(section(log, "Wall time"))
        .contains("| 50 MB to 100 MB | 5 | +0.0% | +0.0% [+0.0%, +0.0%] | - |");
    assertThat(log).contains("| feed-0 | 75.0 | 30.00 | 30.00 | +0.0% | +0.0% | +0.0% | +0.0% |");
  }

  @Test
  public void reusedReference_comparesOnlyMachineIndependentMetrics() {
    ValidationPerformanceCollector collector = new ValidationPerformanceCollector();
    for (int i = 0; i < 6; i++) {
      collector.compareValidationReports(
          "fresh-" + i, report(0, 10, 1_000_000, 500L), report(0, 10, 1_000_000, 500L), feed(20));
    }
    for (int i = 0; i < 4; i++) {
      // The reference ran on a slower machine: only the timings and the heap after GC differ.
      collector.compareValidationReports(
          "reused-" + i,
          report(0, 15, 1_000_000, 900L),
          report(0, 10, 1_000_000, 500L),
          reusedFeed(20));
    }

    String log = collector.generateLogString();

    assertThat(log).contains("No significant performance change detected.");
    assertThat(log)
        .contains(
            "Wall time, CPU time and Peak heap after GC are only compared on the 6 feeds validated"
                + " by both versions in the same job. The other 4 feeds reused");
    assertThat(section(log, "Wall time")).contains("| 10 MB to 50 MB | 6 | +0.0% |");
    assertThat(section(log, "Peak heap after GC")).contains("| 10 MB to 50 MB | 6 | +0.0% |");
    assertThat(section(log, "Allocated memory")).contains("| 10 MB to 50 MB | 10 | +0.0% |");
    assertThat(log).doesNotContain("reused-0 |");
  }

  @Test
  public void everyReferenceReused_saysTimingsAreNotCompared() {
    ValidationPerformanceCollector collector = new ValidationPerformanceCollector();
    for (int i = 0; i < 5; i++) {
      collector.compareValidationReports(
          "reused-" + i,
          report(0, 15, 1_000_000, 900L),
          report(0, 10, 1_000_000, 500L),
          reusedFeed(20));
    }

    String log = collector.generateLogString();

    assertThat(log)
        .contains(
            "Wall time, CPU time and Peak heap after GC are not compared: every feed reused the"
                + " reference report");
    assertThat(section(log, "Allocated memory")).contains("| 10 MB to 50 MB | 5 | +0.0% |");
    assertThat(log).doesNotContain("<strong>Wall time</strong>");
  }

  @Test
  public void sameJobReferences_doNotMentionReuse() {
    ValidationPerformanceCollector collector = new ValidationPerformanceCollector();
    collector.compareValidationReports(
        "feed-a", report(0, 10, 1_000_000, 500L), report(0, 10, 1_000_000, 500L), feed(1));

    assertThat(collector.generateLogString()).doesNotContain("reused");
  }

  @Test
  public void legacyReports_compareValidationTimeOnly() {
    ValidationPerformanceCollector collector = new ValidationPerformanceCollector();
    collector.compareValidationReports("feed-a", legacyReport(10), legacyReport(12), feed(1));
    collector.compareValidationReports(
        "feed-b", legacyReport(150), report(0, 150, 1, null), feed(600));

    String log = collector.generateLogString();

    assertThat(log).contains("Only the validation time is compared");
    assertThat(section(log, "Wall time")).contains("| < 10 MB | 1 | +20.0% |");
    assertThat(section(log, "Wall time")).contains("| >= 500 MB | 1 | +0.0% |");
    assertThat(section(log, "CPU time")).doesNotContain("| >= 500 MB");
  }

  @Test
  public void failedDownload_isGroupedAsUnknownSize() {
    ValidationPerformanceCollector collector = new ValidationPerformanceCollector();
    collector.compareValidationReports(
        "feed-a",
        report(0, 10, 1_000_000, 500L),
        report(0, 10, 1_000_000, 500L),
        new FeedRun(null, false));

    String log = collector.generateLogString();

    assertThat(section(log, "Wall time")).contains("| unknown size | 1 | +0.0% |");
    assertThat(log).contains("| feed-a | N/A | 10.00 | 10.00 |");
  }

  @Test
  public void noData_reportsIt() {
    assertThat(new ValidationPerformanceCollector().generateLogString())
        .contains("No performance data to compare.");
  }

  @Test
  public void toReport_usesValidationTimeSeconds() {
    ValidationPerformanceCollector collector = new ValidationPerformanceCollector();
    collector.compareValidationReports("feed-a", legacyReport(10), legacyReport(12), feed(1));

    List<ValidationPerformance> performances = collector.toReport();

    assertThat(performances)
        .containsExactly(ValidationPerformance.create("feed-a", 10.0, 12.0, 2.0));
  }

  @Test
  public void summarize_computesMedianGeometricMeanAndInterval() {
    GroupSummary summary =
        ValidationPerformanceCollector.summarize(
            List.of(Math.log(1.0), Math.log(1.1), Math.log(1.2), Math.log(1.3), Math.log(1.4)));

    assertThat(summary.count).isEqualTo(5);
    assertThat(summary.median).isWithin(1e-9).of(1.2);
    assertThat(summary.geometricMean).isWithin(1e-3).of(1.1915);
    assertThat(summary.lowerBound).isAtLeast(1.0);
    assertThat(summary.lowerBound).isLessThan(summary.geometricMean);
    assertThat(summary.upperBound).isGreaterThan(summary.geometricMean);
    assertThat(summary.upperBound).isAtMost(1.4);
  }

  @Test
  public void verdict_requiresEnoughDatasetsAndWholeIntervalBeyondThreshold() {
    assertThat(
            ValidationPerformanceCollector.verdict(
                Metric.WALL_TIME, new GroupSummary(4, 1.5, 1.5, 1.4, 1.6)))
        .isEmpty();
    assertThat(
            ValidationPerformanceCollector.verdict(
                Metric.WALL_TIME, new GroupSummary(5, 1.5, 1.5, 1.4, 1.6)))
        .isEqualTo("Regression");
    assertThat(
            ValidationPerformanceCollector.verdict(
                Metric.WALL_TIME, new GroupSummary(5, 1.06, 1.06, 1.04, 1.08)))
        .isEmpty();
    assertThat(
            ValidationPerformanceCollector.verdict(
                Metric.ALLOCATED_MEMORY, new GroupSummary(5, 1.03, 1.03, 1.02, 1.04)))
        .isEqualTo("Regression");
    assertThat(
            ValidationPerformanceCollector.verdict(
                Metric.WALL_TIME, new GroupSummary(5, 0.8, 0.8, 0.7, 0.9)))
        .isEqualTo("Improvement");
  }

  @Test
  public void sizeGroupLabel_coversAllGroups() {
    assertThat(ValidationPerformanceCollector.sizeGroupLabel(0)).isEqualTo("< 10 MB");
    assertThat(ValidationPerformanceCollector.sizeGroupLabel(1)).isEqualTo("10 MB to 50 MB");
    assertThat(ValidationPerformanceCollector.sizeGroupLabel(2)).isEqualTo("50 MB to 100 MB");
    assertThat(ValidationPerformanceCollector.sizeGroupLabel(3)).isEqualTo("100 MB to 200 MB");
    assertThat(ValidationPerformanceCollector.sizeGroupLabel(4)).isEqualTo("200 MB to 500 MB");
    assertThat(ValidationPerformanceCollector.sizeGroupLabel(5)).isEqualTo(">= 500 MB");
    assertThat(ValidationPerformanceCollector.sizeGroupLabel(-1)).isEqualTo("unknown size");
  }

  @Test
  public void sizeGroupOf_usesZipSize() {
    assertThat(sizeGroupOf(feed(9.9))).isEqualTo(0);
    assertThat(sizeGroupOf(feed(10))).isEqualTo(1);
    assertThat(sizeGroupOf(feed(151))).isEqualTo(3);
    assertThat(sizeGroupOf(feed(292))).isEqualTo(4);
    assertThat(sizeGroupOf(feed(1933))).isEqualTo(5);
  }

  private static int sizeGroupOf(FeedRun feedRun) {
    return ValidationPerformanceCollector.sizeGroupOf(
        new ValidationPerformanceCollector.DatasetPerformance("feed", 1.0, 1.0, feedRun));
  }
}
