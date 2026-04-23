package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.mobilitydata.gtfsvalidator.model.ValidationReport;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.report.ValidationPerformance;
import org.mobilitydata.gtfsvalidator.performance.MemoryUsage;

public class ValidationPerformanceCollector {

  public static final String MEMORY_PIVOT_KEY =
      "org.mobilitydata.gtfsvalidator.table.GtfsFeedLoader.loadAndValidate";
  private final Map<String, Double> referenceTimes;
  private final Map<String, Double> latestTimes;
  private final List<DatasetMemoryUsage> datasetsMemoryUsageNoReference;
  private final List<DatasetMemoryUsage> datasetsMemoryUsageWithReference;

  public ValidationPerformanceCollector() {
    this.referenceTimes = new HashMap<>();
    this.latestTimes = new HashMap<>();
    this.datasetsMemoryUsageNoReference = new ArrayList<>();
    this.datasetsMemoryUsageWithReference = new ArrayList<>();
  }

  public void addReferenceTime(String sourceId, Double time) {
    referenceTimes.put(sourceId, time);
  }

  public void addLatestTime(String sourceId, Double time) {
    latestTimes.put(sourceId, time);
  }

  private Double computeAverage(Collection<Double> times) {
    return times.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
  }

  private Double computeMedian(Collection<Double> times) {
    if (times.isEmpty()) {
      return Double.NaN;
    }
    int size = times.size();
    List<Double> sortedTimes = new ArrayList<>(times);
    Collections.sort(sortedTimes);
    Double median;
    if (size % 2 == 0) {
      median = (sortedTimes.get(size / 2 - 1) + sortedTimes.get(size / 2)) / 2.0;
    } else {
      median = sortedTimes.get(size / 2);
    }
    return median;
  }

  private Double computeStandardDeviation(Collection<Double> times) {
    double mean = computeAverage(times);
    return Math.sqrt(
        times.stream().mapToDouble(time -> Math.pow(time - mean, 2)).average().orElse(Double.NaN));
  }

  private Double computeMax(Collection<Double> times) {
    return times.stream().mapToDouble(Double::doubleValue).max().orElse(Double.NaN);
  }

  private Double computeMin(Collection<Double> times) {
    return times.stream().mapToDouble(Double::doubleValue).min().orElse(Double.NaN);
  }

  private String formatMetrics(
      String metric,
      String datasetId,
      Double reference,
      Double latest,
      Function<Double, String> render) {
    String diff;
    if (reference.isNaN() || latest.isNaN()) {
      diff = "N/A";
    } else {
      double difference = latest - reference;
      String arrow = difference > 0 ? "⬆️+" : "⬇️";
      diff = String.format("%s%s", arrow, render.apply(difference));
    }
    return String.format(
        "| %s | %s | %s | %s | %s |\n",
        metric, datasetId, render.apply(reference), render.apply(latest), diff);
  }

  private static String getMemoryDiff(Long reference, Long latest) {
    String diff;
    if (reference == null || latest == null) {
      diff = "N/A";
    } else {
      long difference = latest - reference;
      if (difference == 0) {
        return "-";
      }
      String arrow = difference > 0 ? "⬆️+" : "⬇️";
      diff = String.format("%s%s", arrow, MemoryUsage.convertToHumanReadableMemory(difference));
    }
    return diff;
  }

  public String generateLogString() {
    StringBuilder b = new StringBuilder();
    b.append("### ⏱️ Performance Assessment\n")
        .append("\n")
        .append("<details>\n")
        .append("<summary><strong>📈 Validation Time</strong></summary>\n")
        .append(
            "<p>Assess the performance in terms of seconds taken for the validation process.</p>\n")
        .append("\n")
        .append(
            "| Time Metric                      | Dataset ID        | Reference (s)  | Latest (s)     | Difference (s) |\n")
        .append(
            "|-----------------------------|-------------------|----------------|----------------|----------------|\n");

    List<String> warnings = new ArrayList<>();
    List<Double> allReferenceTimes = new ArrayList<>();
    List<Double> allLatestTimes = new ArrayList<>();
    Set<String> allKeys = new HashSet<>(referenceTimes.keySet());
    allKeys.addAll(latestTimes.keySet());

    for (String groupId : allKeys) {
      Double referenceTimes = this.referenceTimes.getOrDefault(groupId, Double.NaN);
      Double latestTimes = this.latestTimes.getOrDefault(groupId, Double.NaN);

      if (Double.isNaN(referenceTimes) || Double.isNaN(latestTimes)) {
        warnings.add(groupId);
        continue;
      }

      allReferenceTimes.add(referenceTimes);
      allLatestTimes.add(latestTimes);
    }

    generatePerformanceMetricsLog(
        referenceTimes, latestTimes, b, value -> String.format("%.2f", value));

    // Add warning message for feeds that are missing validation times either in reference or latest
    if (!warnings.isEmpty()) {
      b.append("#### ⚠️ Warnings\n")
          .append("\n")
          .append(
              "The following dataset IDs are missing validation times either in reference or latest:\n")
          .append(String.join(", ", warnings))
          .append("\n\n");
    }

    b.append("</details>\n\n");

    if (datasetsMemoryUsageWithReference.size() > 0) {
      Map<String, Double> referenceMemoryUsageById =
          datasetsMemoryUsageWithReference.stream()
              .filter(
                  datasetMemoryUsage ->
                      datasetMemoryUsage.getReferenceUsedMemoryByKey().get(MEMORY_PIVOT_KEY)
                          != null)
              .collect(
                  Collectors.toMap(
                      DatasetMemoryUsage::getDatasetId,
                      datasetMemoryUsage ->
                          datasetMemoryUsage
                              .getReferenceUsedMemoryByKey()
                              .get(MEMORY_PIVOT_KEY)
                              .doubleValue()));
      Map<String, Double> latestMemoryUsageById =
          datasetsMemoryUsageWithReference.stream()
              .filter(
                  datasetMemoryUsage ->
                      datasetMemoryUsage.getLatestUsedMemoryByKey().get(MEMORY_PIVOT_KEY) != null)
              .collect(
                  Collectors.toMap(
                      DatasetMemoryUsage::getDatasetId,
                      datasetMemoryUsage ->
                          datasetMemoryUsage
                              .getLatestUsedMemoryByKey()
                              .get(MEMORY_PIVOT_KEY)
                              .doubleValue()));

      b.append("<details>\n");
      b.append("<summary><strong>📜 Memory Consumption</strong></summary>\n\n");

      b.append(
              "| Metric                      | Dataset ID        | Reference (s)  | Latest (s)     | Difference (s) |\n")
          .append(
              "|-----------------------------|-------------------|----------------|----------------|----------------|\n");

      generatePerformanceMetricsLog(
          referenceMemoryUsageById,
          latestMemoryUsageById,
          b,
          ValidationPerformanceCollector::convertToHumanReadableMemory);
      b.append("</details>\n");
    }
    return b.toString();
  }

  private void generatePerformanceMetricsLog(
      Map<String, Double> references,
      Map<String, Double> latests,
      StringBuilder b,
      Function<Double, String> render) {
    PerformanceMetrics performanceMetrics = computeMetrics(references, latests);
    if (!references.isEmpty() && !latests.isEmpty()) {
      b.append(
              formatMetrics(
                  "Average",
                  "--",
                  performanceMetrics.avgReference,
                  performanceMetrics.avgLatest,
                  render))
          .append(
              formatMetrics(
                  "Median",
                  "--",
                  performanceMetrics.medianReference,
                  performanceMetrics.medianLatest,
                  render))
          .append(
              formatMetrics(
                  "Standard Deviation",
                  "--",
                  performanceMetrics.stdDevReference,
                  performanceMetrics.stdDevLatest,
                  render));
    }

    if (!references.isEmpty()) {
      Double minLatest = latests.getOrDefault(performanceMetrics.minReferenceId, Double.NaN);
      Double maxLatest = latests.getOrDefault(performanceMetrics.maxReferenceId, Double.NaN);
      b.append(
              formatMetrics(
                  "Minimum in References Reports",
                  performanceMetrics.minReferenceId,
                  performanceMetrics.minReference,
                  minLatest,
                  render))
          .append(
              formatMetrics(
                  "Maximum in Reference Reports",
                  performanceMetrics.maxReferenceId,
                  performanceMetrics.maxReference,
                  maxLatest,
                  render));
    }

    if (!latests.isEmpty()) {
      Double minReference = references.getOrDefault(performanceMetrics.minLatestId, Double.NaN);
      Double maxReference = references.getOrDefault(performanceMetrics.maxLatestId, Double.NaN);

      b.append(
              formatMetrics(
                  "Minimum in Latest Reports",
                  performanceMetrics.minLatestId,
                  minReference,
                  performanceMetrics.minLatest,
                  render))
          .append(
              formatMetrics(
                  "Maximum in Latest Reports",
                  performanceMetrics.maxLatestId,
                  maxReference,
                  performanceMetrics.maxLatest,
                  render));
    }
  }

  public void compareValidationReports(
      String sourceId, ValidationReport referenceReport, ValidationReport latestReport) {
    if (referenceReport.getValidationTimeSeconds() != null) {
      addReferenceTime(sourceId, referenceReport.getValidationTimeSeconds());
    }
    if (latestReport.getValidationTimeSeconds() != null) {
      addLatestTime(sourceId, latestReport.getValidationTimeSeconds());
    }

    compareValidationReportMemoryUsage(sourceId, referenceReport, latestReport);
  }

  private void compareValidationReportMemoryUsage(
      String sourceId, ValidationReport referenceReport, ValidationReport latestReport) {
    DatasetMemoryUsage datasetMemoryUsage =
        new DatasetMemoryUsage(
            sourceId,
            referenceReport.getMemoryUsageRecords(),
            latestReport.getMemoryUsageRecords());
    if (referenceReport.getMemoryUsageRecords() != null
        && referenceReport.getMemoryUsageRecords().size() > 0
        && latestReport.getMemoryUsageRecords() != null
        && latestReport.getMemoryUsageRecords().size() > 0) {
      datasetsMemoryUsageWithReference.add(datasetMemoryUsage);
    } else {
      datasetsMemoryUsageNoReference.add(datasetMemoryUsage);
    }
  }

  public List<ValidationPerformance> toReport() {
    List<ValidationPerformance> affectedSources = new ArrayList<>();
    for (String sourceId : referenceTimes.keySet()) {
      Double referenceTime = referenceTimes.getOrDefault(sourceId, Double.NaN);
      Double latestTime = latestTimes.getOrDefault(sourceId, Double.NaN);
      if (!(referenceTime.isNaN() && latestTime.isNaN())) {
        affectedSources.add(
            ValidationPerformance.create(
                sourceId, referenceTime, latestTime, latestTime - referenceTime));
      }
    }
    return affectedSources;
  }

  private PerformanceMetrics computeMetrics(
      Map<String, Double> referencesById, Map<String, Double> latestsById) {
    Collection<Double> allReferences = referencesById.values();
    Collection<Double> allLatest = latestsById.values();
    PerformanceMetrics performanceMetrics = new PerformanceMetrics();
    if (!allReferences.isEmpty() && !allLatest.isEmpty()) {
      performanceMetrics.avgReference = computeAverage(allReferences);
      performanceMetrics.avgLatest = computeAverage(allLatest);
      performanceMetrics.medianReference = computeMedian(allReferences);
      performanceMetrics.medianLatest = computeMedian(allLatest);
      performanceMetrics.stdDevReference = computeStandardDeviation(allReferences);
      performanceMetrics.stdDevLatest = computeStandardDeviation(allLatest);
    }

    if (!allReferences.isEmpty()) {
      performanceMetrics.minReference = computeMin(allReferences);
      performanceMetrics.minReferenceId =
          referencesById.entrySet().stream()
              .filter(entry -> Objects.equals(entry.getValue(), performanceMetrics.minReference))
              .map(Map.Entry::getKey)
              .findFirst()
              .orElse("N/A");

      performanceMetrics.maxReference = computeMax(allReferences);
      performanceMetrics.maxReferenceId =
          referencesById.entrySet().stream()
              .filter(entry -> Objects.equals(entry.getValue(), performanceMetrics.maxReference))
              .map(Map.Entry::getKey)
              .findFirst()
              .orElse("N/A");
    }

    if (!allLatest.isEmpty()) {
      performanceMetrics.minLatest = computeMin(allLatest);
      performanceMetrics.minLatestId =
          latestsById.entrySet().stream()
              .filter(entry -> Objects.equals(entry.getValue(), performanceMetrics.minLatest))
              .map(Map.Entry::getKey)
              .findFirst()
              .orElse("N/A");

      performanceMetrics.maxLatest = computeMax(allLatest);
      performanceMetrics.maxLatestId =
          latestsById.entrySet().stream()
              .filter(entry -> Objects.equals(entry.getValue(), performanceMetrics.maxLatest))
              .map(Map.Entry::getKey)
              .findFirst()
              .orElse("N/A");
    }
    return performanceMetrics;
  }

  private static String convertToHumanReadableMemory(Double bytes) {
    //      Ignoring the decimals in bytes
    return MemoryUsage.convertToHumanReadableMemory(bytes.longValue());
  }
}

class PerformanceMetrics {
  Double minReference;
  String minReferenceId;
  Double maxReference;
  Double minLatest;
  Double maxLatest;
  String minLatestId;
  String maxLatestId;
  String maxReferenceId;
  Double avgReference;
  Double avgLatest;
  Double medianReference;
  Double medianLatest;
  Double stdDevReference;
  Double stdDevLatest;
}
