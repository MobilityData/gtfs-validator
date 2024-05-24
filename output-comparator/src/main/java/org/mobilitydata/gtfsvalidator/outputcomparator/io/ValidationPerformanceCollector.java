package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import java.util.*;
import org.mobilitydata.gtfsvalidator.model.ValidationReport;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.report.ValidationPerformance;

public class ValidationPerformanceCollector {

  private final Map<String, Double> referenceTimes;
  private final Map<String, Double> latestTimes;

  public ValidationPerformanceCollector() {
    this.referenceTimes = new HashMap<>();
    this.latestTimes = new HashMap<>();
  }

  public void addReferenceTime(String sourceId, Double time) {
    referenceTimes.put(sourceId, time);
  }

  public void addLatestTime(String sourceId, Double time) {
    latestTimes.put(sourceId, time);
  }

  private Double computeAverage(List<Double> times) {
    return times.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
  }

  private Double computeMedian(List<Double> times) {
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

  private Double computeStandardDeviation(List<Double> times) {
    double mean = computeAverage(times);
    return Math.sqrt(
        times.stream().mapToDouble(time -> Math.pow(time - mean, 2)).average().orElse(Double.NaN));
  }

  private Double computeMax(List<Double> times) {
    return times.stream().mapToDouble(Double::doubleValue).max().orElse(Double.NaN);
  }

  private Double computeMin(List<Double> times) {
    return times.stream().mapToDouble(Double::doubleValue).min().orElse(Double.NaN);
  }

  private String formatMetrics(String metric, String datasetId, Double reference, Double latest) {
    String diff;
    if (reference.isNaN() || latest.isNaN()) {
      diff = "N/A";
    } else {
      double difference = latest - reference;
      String arrow = difference > 0 ? "⬆️+" : "⬇️";
      diff = String.format("%s%.2f", arrow, difference);
    }
    return String.format(
        "| %s | %s | %.2f | %.2f | %s |\n", metric, datasetId, reference, latest, diff);
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

    if (!allReferenceTimes.isEmpty() && !allLatestTimes.isEmpty()) {
      Double avgReference = computeAverage(allReferenceTimes);
      Double avgLatest = computeAverage(allLatestTimes);
      Double medianReference = computeMedian(allReferenceTimes);
      Double medianLatest = computeMedian(allLatestTimes);
      Double stdDevReference = computeStandardDeviation(allReferenceTimes);
      Double stdDevLatest = computeStandardDeviation(allLatestTimes);

      b.append(formatMetrics("Average", "--", avgReference, avgLatest))
          .append(formatMetrics("Median", "--", medianReference, medianLatest))
          .append(formatMetrics("Standard Deviation", "--", stdDevReference, stdDevLatest));
    }

    if (!allReferenceTimes.isEmpty()) {
      Double minReference = computeMin(allReferenceTimes);
      String minReferenceId =
          referenceTimes.entrySet().stream()
              .filter(entry -> Objects.equals(entry.getValue(), minReference))
              .map(Map.Entry::getKey)
              .findFirst()
              .orElse("N/A");

      Double maxReference = computeMax(allReferenceTimes);
      String maxReferenceId =
          referenceTimes.entrySet().stream()
              .filter(entry -> Objects.equals(entry.getValue(), maxReference))
              .map(Map.Entry::getKey)
              .findFirst()
              .orElse("N/A");

      Double minLatest = latestTimes.getOrDefault(minReferenceId, Double.NaN);
      Double maxLatest = latestTimes.getOrDefault(maxReferenceId, Double.NaN);

      b.append(
              formatMetrics(
                  "Minimum in References Reports", minReferenceId, minReference, minLatest))
          .append(
              formatMetrics(
                  "Maximum in Reference Reports", maxReferenceId, maxReference, maxLatest));
    }

    if (!allLatestTimes.isEmpty()) {
      Double minLatest = computeMin(allLatestTimes);
      String minLatestId =
          latestTimes.entrySet().stream()
              .filter(entry -> Objects.equals(entry.getValue(), minLatest))
              .map(Map.Entry::getKey)
              .findFirst()
              .orElse("N/A");

      Double maxLatest = computeMax(allLatestTimes);
      String maxLatestId =
          latestTimes.entrySet().stream()
              .filter(entry -> Objects.equals(entry.getValue(), maxLatest))
              .map(Map.Entry::getKey)
              .findFirst()
              .orElse("N/A");

      Double minReference = referenceTimes.getOrDefault(minLatestId, Double.NaN);
      Double maxReference = referenceTimes.getOrDefault(maxLatestId, Double.NaN);

      b.append(formatMetrics("Minimum in Latest Reports", minLatestId, minReference, minLatest))
          .append(formatMetrics("Maximum in Latest Reports", maxLatestId, maxReference, maxLatest));
    }

    if (!warnings.isEmpty()) {
      b.append("#### ⚠️ Warnings\n")
          .append("\n")
          .append(
              "The following dataset IDs are missing validation times either in reference or latest:\n")
          .append(String.join(", ", warnings))
          .append("\n\n");
    }
    b.append("</details>\n\n");

    return b.toString();
  }

  public void compareValidationReports(
      String sourceId, ValidationReport referenceReport, ValidationReport latestReport) {
    if (referenceReport.getValidationTimeSeconds() != null) {
      addReferenceTime(sourceId, referenceReport.getValidationTimeSeconds());
    }
    if (latestReport.getValidationTimeSeconds() != null) {
      addLatestTime(sourceId, latestReport.getValidationTimeSeconds());
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
}
