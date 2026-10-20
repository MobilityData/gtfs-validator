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

  private static ValidationReport legacyReport(double validationTimeSeconds) {
    return new ValidationReport(Collections.emptySet(), validationTimeSeconds, null);
  }

  @Test
  public void noChange_isNotFlagged() {
    ValidationPerformanceCollector collector = new ValidationPerformanceCollector();
    for (int i = 0; i < 20; i++) {
      // Timing noise of a few percent in both directions, identical allocations.
      double noise = i % 2 == 0 ? 1.03 : 0.97;
      collector.compareValidationReports(
          "feed-" + i, report(0.1, 1, 1_000_000, null), report(0.1, noise, 1_000_000, null));
    }

    String log = collector.generateLogString();

    assertThat(log).contains("No significant performance change detected.");
    assertThat(log)
        .contains("| Allocated memory | < 2 s | 20 | +0.0% | +0.0% [+0.0%, +0.0%] | - |");
  }

  @Test
  public void allocationRegressionInOneSizeGroup_isFlaggedForThatGroupOnly() {
    ValidationPerformanceCollector collector = new ValidationPerformanceCollector();
    for (int i = 0; i < 10; i++) {
      collector.compareValidationReports(
          "small-" + i, report(0, 1, 1_000_000, null), report(0, 1, 1_000_000, null));
      collector.compareValidationReports(
          "medium-" + i, report(0, 10, 1_000_000, null), report(0, 10, 1_200_000, null));
    }

    String log = collector.generateLogString();

    assertThat(log)
        .contains(
            "- Regression **Allocated memory** for feeds of 2 s to 20 s: +20.0% [+20.0%, +20.0%]");
    assertThat(log)
        .contains("| Allocated memory | < 2 s | 10 | +0.0% | +0.0% [+0.0%, +0.0%] | - |");
    assertThat(log).doesNotContain("No significant performance change detected.");
  }

  @Test
  public void wallTime_excludesInputPhase() {
    ValidationPerformanceCollector collector = new ValidationPerformanceCollector();
    for (int i = 0; i < 5; i++) {
      // The latest run spent 5 more seconds downloading the feed, the validation took the same.
      collector.compareValidationReports(
          "feed-" + i, report(1, 30, 1_000_000, 500L), report(6, 30, 1_000_000, 500L));
    }

    String log = collector.generateLogString();

    assertThat(log)
        .contains("| Wall time | 20 s to 120 s | 5 | +0.0% | +0.0% [+0.0%, +0.0%] | - |");
    assertThat(log).contains("| feed-0 | 30.00 | 30.00 | +0.0% | +0.0% | +0.0% | +0.0% |");
  }

  @Test
  public void legacyReports_compareValidationTimeOnly() {
    ValidationPerformanceCollector collector = new ValidationPerformanceCollector();
    collector.compareValidationReports("feed-a", legacyReport(10), legacyReport(12));
    collector.compareValidationReports("feed-b", legacyReport(150), report(0, 150, 1, null));

    String log = collector.generateLogString();

    assertThat(log).contains("Only the validation time is compared");
    assertThat(log).contains("| Wall time | 2 s to 20 s | 1 | +20.0% |");
    assertThat(log).contains("| Wall time | >= 120 s | 1 | +0.0% |");
    assertThat(log).doesNotContain("| CPU time | >= 120 s");
  }

  @Test
  public void noData_reportsIt() {
    assertThat(new ValidationPerformanceCollector().generateLogString())
        .contains("No performance data to compare.");
  }

  @Test
  public void toReport_usesValidationTimeSeconds() {
    ValidationPerformanceCollector collector = new ValidationPerformanceCollector();
    collector.compareValidationReports("feed-a", legacyReport(10), legacyReport(12));

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
    assertThat(ValidationPerformanceCollector.sizeGroupLabel(0)).isEqualTo("< 2 s");
    assertThat(ValidationPerformanceCollector.sizeGroupLabel(1)).isEqualTo("2 s to 20 s");
    assertThat(ValidationPerformanceCollector.sizeGroupLabel(2)).isEqualTo("20 s to 120 s");
    assertThat(ValidationPerformanceCollector.sizeGroupLabel(3)).isEqualTo(">= 120 s");
  }
}
