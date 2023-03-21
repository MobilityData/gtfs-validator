package org.mobilitydata.gtfsvalidator.web.service.util;

/**
 * Metadata about a validation job, saved as json to GCS.
 */
public class JobMetadata {
  private String jobId;
  private String countryCode;

  public JobMetadata() {}

  public JobMetadata(String jobId, String countryCode) {
    this.jobId = jobId;
    this.countryCode = countryCode;
  }

  public String getJobId() {
    return jobId;
  }

  public void setJobId(String jobId) {
    this.countryCode = countryCode;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }
}
