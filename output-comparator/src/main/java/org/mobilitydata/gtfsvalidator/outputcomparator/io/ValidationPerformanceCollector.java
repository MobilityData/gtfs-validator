package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import org.mobilitydata.gtfsvalidator.model.ValidationReport;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.report.ValidationPerformance;
import org.mobilitydata.gtfsvalidator.performance.ValidationPerformanceMetrics;

/**
 * Compares the performance of the reference and latest validators over all the datasets of an
 * acceptance test run.
 *
 * <p>Each metric is compared per dataset as the ratio latest / reference, so that every dataset
 * weighs the same whatever its size. The datasets are grouped by size, using the reference
 * validation time, because the few largest feeds have the noisiest timings and would otherwise
 * dominate any average. For each group, the geometric mean of the ratios is reported with a
 * bootstrap 95% confidence interval, and a change is only flagged when the whole interval is beyond
 * the metric's threshold.
 */
public class ValidationPerformanceCollector {

  /** Upper bounds, in seconds of reference validation time, of the dataset size groups. */
  static final double[] SIZE_GROUP_UPPER_BOUNDS_SECONDS = {2, 20, 120};

  /** Datasets faster than this are left out of the per-dataset table: JVM noise dominates them. */
  static final double MIN_SECONDS_FOR_DATASET_TABLE = 2;

  static final int MAX_DATASETS_IN_TABLE = 10;

  /** A group needs this many datasets for its confidence interval to mean anything. */
  static final int MIN_DATASETS_PER_GROUP = 5;

  private static final int BOOTSTRAP_RESAMPLES = 2000;
  private static final long BOOTSTRAP_SEED = 20251015L;

  /** A metric compared between the reference and latest reports. */
  enum Metric {
    WALL_TIME("Wall time", 0.05),
    CPU_TIME("CPU time", 0.05),
    ALLOCATED_MEMORY("Allocated memory", 0.01),
    PEAK_HEAP_AFTER_GC("Peak heap after GC", 0.05),
    PEAK_LIVE_HEAP("Peak live heap", 0.02);

    final String label;

    /**
     * Relative change the whole confidence interval must exceed for the change to be flagged.
     * Allocations barely vary between runs, so their threshold is lower than the timings'.
     */
    final double threshold;

    Metric(String label, double threshold) {
      this.label = label;
      this.threshold = threshold;
    }
  }

  /** Measurements of one dataset. A metric is absent when either report lacks it. */
  static class DatasetPerformance {
    final String datasetId;
    final Double referenceValidationTimeSeconds;
    final Double latestValidationTimeSeconds;
    final Map<Metric, double[]> referenceAndLatest = new LinkedHashMap<>();

    DatasetPerformance(
        String datasetId,
        Double referenceValidationTimeSeconds,
        Double latestValidationTimeSeconds) {
      this.datasetId = datasetId;
      this.referenceValidationTimeSeconds = referenceValidationTimeSeconds;
      this.latestValidationTimeSeconds = latestValidationTimeSeconds;
    }

    void put(Metric metric, Number reference, Number latest) {
      if (reference != null && latest != null && reference.doubleValue() > 0) {
        referenceAndLatest.put(
            metric, new double[] {reference.doubleValue(), latest.doubleValue()});
      }
    }

    Double reference(Metric metric) {
      double[] values = referenceAndLatest.get(metric);
      return values == null ? null : values[0];
    }

    /** Returns latest / reference, or {@code null} when the metric is absent. */
    Double ratio(Metric metric) {
      double[] values = referenceAndLatest.get(metric);
      return values == null ? null : values[1] / values[0];
    }
  }

  private final List<DatasetPerformance> datasets = new ArrayList<>();

  public void compareValidationReports(
      String sourceId, ValidationReport referenceReport, ValidationReport latestReport) {
    DatasetPerformance dataset =
        new DatasetPerformance(
            sourceId,
            referenceReport.getValidationTimeSeconds(),
            latestReport.getValidationTimeSeconds());
    ValidationPerformanceMetrics reference = referenceReport.getPerformanceMetrics();
    ValidationPerformanceMetrics latest = latestReport.getPerformanceMetrics();
    if (reference != null
        && latest != null
        && reference.getValidationWallTimeSeconds() != null
        && latest.getValidationWallTimeSeconds() != null) {
      // Excludes the input phase, which includes the download time when validating a URL.
      dataset.put(
          Metric.WALL_TIME,
          reference.getValidationWallTimeSeconds(),
          latest.getValidationWallTimeSeconds());
    } else {
      // A report from a validator version that does not have the performance metrics.
      dataset.put(
          Metric.WALL_TIME,
          referenceReport.getValidationTimeSeconds(),
          latestReport.getValidationTimeSeconds());
    }
    if (reference != null && latest != null) {
      dataset.put(Metric.CPU_TIME, reference.getCpuTimeSeconds(), latest.getCpuTimeSeconds());
      dataset.put(
          Metric.ALLOCATED_MEMORY, reference.getAllocatedBytes(), latest.getAllocatedBytes());
      dataset.put(
          Metric.PEAK_HEAP_AFTER_GC,
          reference.getPeakHeapAfterGcBytes(),
          latest.getPeakHeapAfterGcBytes());
      dataset.put(
          Metric.PEAK_LIVE_HEAP, reference.getPeakLiveHeapBytes(), latest.getPeakLiveHeapBytes());
    }
    datasets.add(dataset);
  }

  public String generateLogString() {
    StringBuilder b = new StringBuilder();
    b.append("### ⏱️ Performance Assessment\n\n");

    List<String[]> groupRows = new ArrayList<>();
    List<String> flagged = new ArrayList<>();
    for (Metric metric : Metric.values()) {
      for (int group = 0; group <= SIZE_GROUP_UPPER_BOUNDS_SECONDS.length; group++) {
        List<Double> logRatios = new ArrayList<>();
        for (DatasetPerformance dataset : datasets) {
          Double ratio = dataset.ratio(metric);
          if (ratio != null && ratio > 0 && sizeGroupOf(dataset) == group) {
            logRatios.add(Math.log(ratio));
          }
        }
        if (logRatios.isEmpty()) {
          continue;
        }
        GroupSummary summary = summarize(logRatios);
        String verdict = verdict(metric, summary);
        if (!verdict.isEmpty()) {
          flagged.add(
              String.format(
                  Locale.ROOT,
                  "%s **%s** for feeds of %s: %s %s",
                  verdict,
                  metric.label,
                  sizeGroupLabel(group),
                  formatChange(summary.geometricMean),
                  formatInterval(summary)));
        }
        groupRows.add(
            new String[] {
              metric.label,
              sizeGroupLabel(group),
              String.valueOf(logRatios.size()),
              formatChange(summary.median),
              formatChange(summary.geometricMean) + " " + formatInterval(summary),
              verdict.isEmpty() ? "-" : "**" + verdict + "**"
            });
      }
    }

    if (groupRows.isEmpty()) {
      b.append("No performance data to compare.\n");
      return b.toString();
    }
    if (flagged.isEmpty()) {
      b.append("No significant performance change detected.\n\n");
    } else {
      for (String line : flagged) {
        b.append("- ").append(line).append('\n');
      }
      b.append('\n');
    }
    if (datasets.stream().noneMatch(d -> d.ratio(Metric.ALLOCATED_MEMORY) != null)) {
      b.append(
          "Only the validation time is compared: the reference or latest validator does not"
              + " report the detailed performance metrics.\n\n");
    }

    b.append("<details>\n")
        .append("<summary><strong>Changes by feed size</strong></summary>\n\n")
        .append(
            "Each feed is compared with itself (latest / reference) and the feeds are grouped by"
                + " their reference validation time, so that the largest feeds do not dominate."
                + " A change is flagged only when its whole 95% confidence interval is beyond the"
                + " threshold: ")
        .append(thresholdsDescription())
        .append(".\n\n")
        .append(
            "| Metric | Feed size (reference time) | Feeds | Median change |"
                + " Geometric mean change [95% CI] | Flag |\n")
        .append("|---|---|---|---|---|---|\n");
    for (String[] row : groupRows) {
      b.append("| ").append(String.join(" | ", row)).append(" |\n");
    }
    b.append("</details>\n\n");

    appendLargestChanges(b);
    return b.toString();
  }

  /** Appends the feeds whose wall time changed the most, with all their metrics. */
  private void appendLargestChanges(StringBuilder b) {
    List<DatasetPerformance> candidates = new ArrayList<>();
    for (DatasetPerformance dataset : datasets) {
      Double referenceTime = dataset.reference(Metric.WALL_TIME);
      if (referenceTime != null && referenceTime >= MIN_SECONDS_FOR_DATASET_TABLE) {
        candidates.add(dataset);
      }
    }
    if (candidates.isEmpty()) {
      return;
    }
    candidates.sort(
        Comparator.comparingDouble(
                (DatasetPerformance d) -> Math.abs(Math.log(d.ratio(Metric.WALL_TIME))))
            .reversed());
    b.append("<details>\n")
        .append("<summary><strong>Largest wall time changes per feed</strong></summary>\n\n")
        .append(
            String.format(
                Locale.ROOT,
                "Feeds with a reference time of at least %.0f s. A single feed's timing can vary"
                    + " by more than 20%% between runs, so look for changes that also show in CPU"
                    + " time or allocated memory.\n\n",
                MIN_SECONDS_FOR_DATASET_TABLE))
        .append("| Dataset ID | Reference time (s) | Latest time (s) |");
    for (Metric metric : Metric.values()) {
      b.append(' ').append(metric.label).append(" |");
    }
    b.append("\n|---|---|---|");
    for (int i = 0; i < Metric.values().length; i++) {
      b.append("---|");
    }
    b.append('\n');
    for (DatasetPerformance dataset :
        candidates.subList(0, Math.min(MAX_DATASETS_IN_TABLE, candidates.size()))) {
      double[] times = dataset.referenceAndLatest.get(Metric.WALL_TIME);
      b.append(
          String.format(
              Locale.ROOT, "| %s | %.2f | %.2f |", dataset.datasetId, times[0], times[1]));
      for (Metric metric : Metric.values()) {
        Double ratio = dataset.ratio(metric);
        b.append(' ').append(ratio == null ? "N/A" : formatChange(ratio)).append(" |");
      }
      b.append('\n');
    }
    b.append("</details>\n");
  }

  public List<ValidationPerformance> toReport() {
    List<ValidationPerformance> affectedSources = new ArrayList<>();
    for (DatasetPerformance dataset : datasets) {
      if (dataset.referenceValidationTimeSeconds == null) {
        continue;
      }
      double referenceTime = dataset.referenceValidationTimeSeconds;
      double latestTime =
          dataset.latestValidationTimeSeconds == null
              ? Double.NaN
              : dataset.latestValidationTimeSeconds;
      affectedSources.add(
          ValidationPerformance.create(
              dataset.datasetId, referenceTime, latestTime, latestTime - referenceTime));
    }
    return affectedSources;
  }

  /** Summary of the log ratios of one metric for one size group. */
  static class GroupSummary {
    final int count;
    final double median;
    final double geometricMean;
    final double lowerBound;
    final double upperBound;

    GroupSummary(
        int count, double median, double geometricMean, double lowerBound, double upperBound) {
      this.count = count;
      this.median = median;
      this.geometricMean = geometricMean;
      this.lowerBound = lowerBound;
      this.upperBound = upperBound;
    }
  }

  /**
   * Summarizes log ratios as ratios: the median, the geometric mean and a percentile bootstrap 95%
   * confidence interval of the geometric mean. The seed is fixed so that the same reports always
   * produce the same comment.
   */
  static GroupSummary summarize(List<Double> logRatios) {
    double[] values = logRatios.stream().mapToDouble(Double::doubleValue).sorted().toArray();
    int n = values.length;
    double median = n % 2 == 1 ? values[n / 2] : (values[n / 2 - 1] + values[n / 2]) / 2.0;
    double[] resampledMeans = new double[BOOTSTRAP_RESAMPLES];
    Random random = new Random(BOOTSTRAP_SEED);
    for (int i = 0; i < BOOTSTRAP_RESAMPLES; i++) {
      double sum = 0;
      for (int j = 0; j < n; j++) {
        sum += values[random.nextInt(n)];
      }
      resampledMeans[i] = sum / n;
    }
    Arrays.sort(resampledMeans);
    return new GroupSummary(
        n,
        Math.exp(median),
        Math.exp(Arrays.stream(values).average().orElse(0)),
        Math.exp(resampledMeans[(int) Math.floor(0.025 * (BOOTSTRAP_RESAMPLES - 1))]),
        Math.exp(resampledMeans[(int) Math.ceil(0.975 * (BOOTSTRAP_RESAMPLES - 1))]));
  }

  /** Returns "Regression", "Improvement" or an empty string when nothing is flagged. */
  static String verdict(Metric metric, GroupSummary summary) {
    if (summary.count < MIN_DATASETS_PER_GROUP) {
      return "";
    }
    if (summary.lowerBound > 1 + metric.threshold) {
      return "Regression";
    }
    if (summary.upperBound < 1 - metric.threshold) {
      return "Improvement";
    }
    return "";
  }

  static int sizeGroupOf(DatasetPerformance dataset) {
    Double referenceTime = dataset.reference(Metric.WALL_TIME);
    if (referenceTime == null) {
      referenceTime = dataset.referenceValidationTimeSeconds;
    }
    if (referenceTime == null) {
      return -1;
    }
    int group = 0;
    while (group < SIZE_GROUP_UPPER_BOUNDS_SECONDS.length
        && referenceTime >= SIZE_GROUP_UPPER_BOUNDS_SECONDS[group]) {
      group++;
    }
    return group;
  }

  static String sizeGroupLabel(int group) {
    Function<Double, String> format = seconds -> String.format(Locale.ROOT, "%.0f s", seconds);
    if (group == 0) {
      return "< " + format.apply(SIZE_GROUP_UPPER_BOUNDS_SECONDS[0]);
    }
    if (group == SIZE_GROUP_UPPER_BOUNDS_SECONDS.length) {
      return ">= " + format.apply(SIZE_GROUP_UPPER_BOUNDS_SECONDS[group - 1]);
    }
    return format.apply(SIZE_GROUP_UPPER_BOUNDS_SECONDS[group - 1])
        + " to "
        + format.apply(SIZE_GROUP_UPPER_BOUNDS_SECONDS[group]);
  }

  private static String thresholdsDescription() {
    List<String> thresholds = new ArrayList<>();
    for (Metric metric : Metric.values()) {
      thresholds.add(String.format(Locale.ROOT, "%s %.0f%%", metric.label, metric.threshold * 100));
    }
    return String.join(", ", thresholds);
  }

  private static String formatChange(double ratio) {
    double percent = (ratio - 1) * 100;
    // Avoids "-0.0%" for changes that round to zero.
    return String.format(Locale.ROOT, "%+.1f%%", Math.abs(percent) < 0.05 ? 0 : percent);
  }

  private static String formatInterval(GroupSummary summary) {
    return "[" + formatChange(summary.lowerBound) + ", " + formatChange(summary.upperBound) + "]";
  }
}
