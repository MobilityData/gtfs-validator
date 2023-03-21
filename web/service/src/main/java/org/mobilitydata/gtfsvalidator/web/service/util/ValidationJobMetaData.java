package org.mobilitydata.gtfsvalidator.web.service.util;

/** Metadata about a validation job, extracted from the PubSub message. */
public class ValidationJobMetaData {
  private final String jobId;
  private final String fileName;

  public ValidationJobMetaData(String jobId, String fileName) {
    this.jobId = jobId;
    this.fileName = fileName;
  }

  public String getFileName() {
    return fileName;
  }

  public String getJobId() {
    return jobId;
  }
}
