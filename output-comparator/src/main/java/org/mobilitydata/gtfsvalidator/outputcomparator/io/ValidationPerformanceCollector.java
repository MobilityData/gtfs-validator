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
import org.mobilitydata.gtfsvalidator.outputcomparator.model.FeedRun;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.report.ValidationPerformance;
import org.mobilitydata.gtfsvalidator.performance.ValidationPerformanceMetrics;

/**
 * Compares the performance of the reference and latest validators over all the datasets of an
 * acceptance test run.
 *
 * <p>Each metric is compared per dataset as the ratio latest / reference, so that every dataset
 * weighs the same whatever its size. The datasets are grouped by the size of their zip file, which
 * does not depend on either run, because the few largest feeds have the noisiest timings and would
 * otherwise dominate any average. For each group, the geometric mean of the ratios is reported with
 * a bootstrap 95% confidence interval, and a change is only flagged when the whole interval is
 * beyond the metric's threshold.
 *
 * <p>The acceptance tests reuse the reference report of an unchanged feed from an earlier run, made
 * on another machine. The metrics that depend on the machine are only compared on the datasets
 * whose reports were both produced in the same job.
 */
public class ValidationPerformanceCollector {

  /**
   * Upper bounds, in MB of zip file, of the dataset size groups. The bounds above 50 MB fall in
   * gaps between the sizes of the acceptance test feeds, so that a feed rarely moves to another
   * group.
   */
  static final long[] SIZE_GROUP_UPPER_BOUNDS_MB = {10, 50, 100, 200, 500};

  /** Group of the datasets whose size is unknown, because their download failed. */
  static final int UNKNOWN_SIZE_GROUP = -1;

  private static final double BYTES_PER_MB = 1_000_000;

  /** Datasets faster than this are left out of the per-dataset table: JVM noise dominates them. */
  static final double MIN_SECONDS_FOR_DATASET_TABLE = 2;

  static final int MAX_DATASETS_IN_TABLE = 10;

  /** A group needs this many datasets for its confidence interval to mean anything. */
  static final int MIN_DATASETS_PER_GROUP = 5;

  private static final int BOOTSTRAP_RESAMPLES = 2000;
  private static final long BOOTSTRAP_SEED = 20251015L;

  /** A metric compared between the reference and latest reports. */
  enum Metric {
    WALL_TIME(
        "Wall time",
        "Elapsed time of the validation, without reading the feed. It also moves with other load"
            + " on the machine.",
        0.05,
        true),
    CPU_TIME(
        "CPU time",
        "Processor time of the threads doing the validation, without garbage collection. Steadier"
            + " than the wall time.",
        0.05,
        true),
    ALLOCATED_MEMORY(
        "Allocated memory",
        "Total memory the validation allocated. It is nearly identical between runs, so even small"
            + " changes are meaningful.",
        0.01,
        false),
    // When the collector runs depends on the speed and number of processors.
    PEAK_HEAP_AFTER_GC(
        "Peak heap after GC",
        "Largest memory in use right after a garbage collection. Only measured when a collection"
            + " ran, which is mostly on larger feeds.",
        0.05,
        true),
    PEAK_LIVE_HEAP(
        "Peak live heap",
        "Largest memory still in use at the end of a validation step, measured on every feed. The"
            + " best indicator of how much memory a feed needs.",
        0.02,
        false);

    final String label;

    /** What the metric measures, for the readers of the report. */
    final String description;

    /**
     * Relative change the whole confidence interval must exceed for the change to be flagged.
     * Allocations barely vary between runs, so their threshold is lower than the timings'.
     */
    final double threshold;

    /** Whether the metric varies between machines, so that both reports must come from one job. */
    final boolean machineDependent;

    Metric(String label, String description, double threshold, boolean machineDependent) {
      this.label = label;
      this.description = description;
      this.threshold = threshold;
      this.machineDependent = machineDependent;
    }
  }

  /** Measurements of one dataset. A metric is absent when either report lacks it. */
  static class DatasetPerformance {
    final String datasetId;
    final Double referenceValidationTimeSeconds;
    final Double latestValidationTimeSeconds;
    final Long zipBytes;
    final boolean referenceReused;
    final Map<Metric, double[]> referenceAndLatest = new LinkedHashMap<>();

    DatasetPerformance(
        String datasetId,
        Double referenceValidationTimeSeconds,
        Double latestValidationTimeSeconds,
        FeedRun feedRun) {
      this.datasetId = datasetId;
      this.referenceValidationTimeSeconds = referenceValidationTimeSeconds;
      this.latestValidationTimeSeconds = latestValidationTimeSeconds;
      this.zipBytes = feedRun.getZipBytes();
      this.referenceReused = feedRun.isMasterReused();
    }

    void put(Metric metric, Number reference, Number latest) {
      if (metric.machineDependent && referenceReused) {
        return;
      }
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

  /**
   * @param feedRun how the acceptance tests validated the feed: its size picks the dataset's group,
   *     and a reused reference report leaves out the metrics that depend on the machine
   */
  public void compareValidationReports(
      String sourceId,
      ValidationReport referenceReport,
      ValidationReport latestReport,
      FeedRun feedRun) {
    DatasetPerformance dataset =
        new DatasetPerformance(
            sourceId,
            referenceReport.getValidationTimeSeconds(),
            latestReport.getValidationTimeSeconds(),
            feedRun);
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

    Map<Metric, List<String[]>> groupRows = new LinkedHashMap<>();
    Map<Metric, List<String>> groupVerdicts = new LinkedHashMap<>();
    List<String> flagged = new ArrayList<>();
    for (Metric metric : Metric.values()) {
      List<String[]> rows = new ArrayList<>();
      List<String> verdicts = new ArrayList<>();
      for (int group : sizeGroups()) {
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
          verdicts.add(verdict + " for " + sizeGroupLabel(group));
        }
        rows.add(
            new String[] {
              sizeGroupLabel(group),
              String.valueOf(logRatios.size()),
              formatChange(summary.median),
              formatChange(summary.geometricMean) + " " + formatInterval(summary),
              verdict.isEmpty() ? "-" : "**" + verdict + "**"
            });
      }
      if (!rows.isEmpty()) {
        groupRows.put(metric, rows);
        groupVerdicts.put(metric, verdicts);
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
    long reusedCount = datasets.stream().filter(d -> d.referenceReused).count();
    if (reusedCount == datasets.size()) {
      b.append(
          String.format(
              Locale.ROOT,
              "%s are not compared: every feed reused the reference report of an earlier run on"
                  + " another machine. Run the workflow manually with reuse_master_reports off to"
                  + " compare them.\n\n",
              machineDependentLabels()));
    } else if (reusedCount > 0) {
      b.append(
          String.format(
              Locale.ROOT,
              "%s are only compared on the %d feeds validated by both versions in the same job."
                  + " The other %d feeds reused the reference report of an earlier run on another"
                  + " machine.\n\n",
              machineDependentLabels(),
              datasets.size() - reusedCount,
              reusedCount));
    }

    b.append(
        "Each feed is compared with itself (latest / reference), and the feeds are grouped by the"
            + " size of their zip file so that the largest feeds do not dominate. A change is"
            + " flagged only when its whole 95% confidence interval is beyond the metric's"
            + " threshold.\n\n");
    for (Map.Entry<Metric, List<String[]>> entry : groupRows.entrySet()) {
      Metric metric = entry.getKey();
      List<String> verdicts = groupVerdicts.get(metric);
      b.append("<details>\n")
          .append("<summary><strong>")
          .append(metric.label)
          .append("</strong>: ")
          .append(verdicts.isEmpty() ? "no significant change" : String.join(", ", verdicts))
          .append("</summary>\n\n")
          .append(metric.description)
          .append(String.format(Locale.ROOT, " Flagged beyond %.0f%%.\n\n", metric.threshold * 100))
          .append(
              "| Feed size (zip) | Feeds | Median change | Geometric mean change [95% CI] | Flag"
                  + " |\n")
          .append("|---|---|---|---|---|\n");
      for (String[] row : entry.getValue()) {
        b.append("| ").append(String.join(" | ", row)).append(" |\n");
      }
      b.append("</details>\n\n");
    }

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
        .append("| Dataset ID | Size (MB) | Reference time (s) | Latest time (s) |");
    for (Metric metric : Metric.values()) {
      b.append(' ').append(metric.label).append(" |");
    }
    b.append("\n|---|---|---|---|");
    for (int i = 0; i < Metric.values().length; i++) {
      b.append("---|");
    }
    b.append('\n');
    for (DatasetPerformance dataset :
        candidates.subList(0, Math.min(MAX_DATASETS_IN_TABLE, candidates.size()))) {
      double[] times = dataset.referenceAndLatest.get(Metric.WALL_TIME);
      b.append(
          String.format(
              Locale.ROOT,
              "| %s | %s | %.2f | %.2f |",
              dataset.datasetId,
              dataset.zipBytes == null
                  ? "N/A"
                  : String.format(Locale.ROOT, "%.1f", dataset.zipBytes / BYTES_PER_MB),
              times[0],
              times[1]));
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

  /** The size groups in display order, the unknown size last. */
  static List<Integer> sizeGroups() {
    List<Integer> groups = new ArrayList<>();
    for (int group = 0; group <= SIZE_GROUP_UPPER_BOUNDS_MB.length; group++) {
      groups.add(group);
    }
    groups.add(UNKNOWN_SIZE_GROUP);
    return groups;
  }

  static int sizeGroupOf(DatasetPerformance dataset) {
    if (dataset.zipBytes == null) {
      return UNKNOWN_SIZE_GROUP;
    }
    int group = 0;
    while (group < SIZE_GROUP_UPPER_BOUNDS_MB.length
        && dataset.zipBytes >= SIZE_GROUP_UPPER_BOUNDS_MB[group] * BYTES_PER_MB) {
      group++;
    }
    return group;
  }

  static String sizeGroupLabel(int group) {
    if (group == UNKNOWN_SIZE_GROUP) {
      return "unknown size";
    }
    Function<Long, String> format = mb -> String.format(Locale.ROOT, "%d MB", mb);
    if (group == 0) {
      return "< " + format.apply(SIZE_GROUP_UPPER_BOUNDS_MB[0]);
    }
    if (group == SIZE_GROUP_UPPER_BOUNDS_MB.length) {
      return ">= " + format.apply(SIZE_GROUP_UPPER_BOUNDS_MB[group - 1]);
    }
    return format.apply(SIZE_GROUP_UPPER_BOUNDS_MB[group - 1])
        + " to "
        + format.apply(SIZE_GROUP_UPPER_BOUNDS_MB[group]);
  }

  private static String machineDependentLabels() {
    List<String> labels = new ArrayList<>();
    for (Metric metric : Metric.values()) {
      if (metric.machineDependent) {
        labels.add(metric.label);
      }
    }
    return String.join(", ", labels.subList(0, labels.size() - 1))
        + " and "
        + labels.get(labels.size() - 1);
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
