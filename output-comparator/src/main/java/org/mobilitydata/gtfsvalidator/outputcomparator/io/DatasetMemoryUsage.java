package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import com.google.common.flogger.FluentLogger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.mobilitydata.gtfsvalidator.performance.MemoryUsage;

/**
 * Represents memory usage information for a dataset. This class contains the information associated
 * with the memory usage of a dataset when running the validation process.
 */
public class DatasetMemoryUsage {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private String datasetId;
  private List<MemoryUsage> referenceMemoryUsage;
  private List<MemoryUsage> latestMemoryUsage;
  private Map<String, Long> referenceUsedMemoryByKey = Collections.unmodifiableMap(new HashMap<>());
  private Map<String, Long> latestUsedMemoryByKey = Collections.unmodifiableMap(new HashMap<>());

  public DatasetMemoryUsage(
      String datasetId,
      List<MemoryUsage> referenceMemoryUsage,
      List<MemoryUsage> latestMemoryUsage) {
    this.datasetId = datasetId;
    this.referenceMemoryUsage = referenceMemoryUsage;
    this.latestMemoryUsage = latestMemoryUsage;
    if (referenceMemoryUsage != null) {
      this.referenceUsedMemoryByKey =
          referenceMemoryUsage.stream()
              .collect(
                  Collectors.toUnmodifiableMap(
                      MemoryUsage::getKey,
                      MemoryUsage::usedMemory,
                      (existing, replacement) -> {
                        logger.atWarning().log(
                            "Duplicate key found in referenceMemoryUsage: " + existing);
                        return existing;
                      }));
    }
    if (latestMemoryUsage != null) {
      this.latestUsedMemoryByKey =
          latestMemoryUsage.stream()
              .collect(
                  Collectors.toUnmodifiableMap(
                      MemoryUsage::getKey,
                      MemoryUsage::usedMemory,
                      (existing, replacement) -> {
                        logger.atWarning().log(
                            "Duplicate key found in latestMemoryUsage: " + existing);
                        return existing;
                      }));
    }
  }

  public String getDatasetId() {
    return datasetId;
  }

  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  public List<MemoryUsage> getReferenceMemoryUsage() {
    return referenceMemoryUsage;
  }

  public void setReferenceMemoryUsage(List<MemoryUsage> referenceMemoryUsage) {
    this.referenceMemoryUsage = referenceMemoryUsage;
  }

  public List<MemoryUsage> getLatestMemoryUsage() {
    return latestMemoryUsage;
  }

  public void setLatestMemoryUsage(List<MemoryUsage> latestMemoryUsage) {
    this.latestMemoryUsage = latestMemoryUsage;
  }

  public Map<String, Long> getReferenceUsedMemoryByKey() {
    return referenceUsedMemoryByKey;
  }

  public Map<String, Long> getLatestUsedMemoryByKey() {
    return latestUsedMemoryByKey;
  }
}
