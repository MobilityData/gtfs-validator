package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import org.mobilitydata.gtfsvalidator.performance.MemoryUsage;

public class DatasetMemoryUsage {

  private String datasetId;
  private String key;
  private MemoryUsage referenceMemoryUsage;
  private MemoryUsage latestMemoryUsage;

  public DatasetMemoryUsage(
      String datasetId, MemoryUsage referenceMemoryUsage, MemoryUsage latestMemoryUsage) {
    this.datasetId = datasetId;
    this.key = referenceMemoryUsage != null ? referenceMemoryUsage.getKey() : null;
    if (key == null) {
      this.key = latestMemoryUsage.getKey() != null ? latestMemoryUsage.getKey() : null;
    }
    this.referenceMemoryUsage = referenceMemoryUsage;
    this.latestMemoryUsage = latestMemoryUsage;
  }

  public String getDatasetId() {
    return datasetId;
  }

  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  public MemoryUsage getReferenceMemoryUsage() {
    return referenceMemoryUsage;
  }

  public void setReferenceMemoryUsage(MemoryUsage referenceMemoryUsage) {
    this.referenceMemoryUsage = referenceMemoryUsage;
  }

  public MemoryUsage getLatestMemoryUsage() {
    return latestMemoryUsage;
  }

  public void setLatestMemoryUsage(MemoryUsage latestMemoryUsage) {
    this.latestMemoryUsage = latestMemoryUsage;
  }

  public String getKey() {
    return key;
  }
}
