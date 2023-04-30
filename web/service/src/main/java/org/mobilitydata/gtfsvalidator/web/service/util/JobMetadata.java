package org.mobilitydata.gtfsvalidator.web.service.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Metadata about a validation job, saved as json to GCS. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobMetadata {
  private String jobId;
  private String countryCode;
  private JobStatus status;
  private String errorMessage;

  public JobMetadata(String jobId) {
    this.jobId = jobId;
    this.countryCode = null;
    this.status = JobStatus.PENDING;
    this.errorMessage = null;
  }

  public JobMetadata(String jobId, String countryCode) {
    this.jobId = jobId;
    this.countryCode = countryCode;
    this.status = JobStatus.PENDING;
    this.errorMessage = null;
  }

  public JobMetadata(String jobId, String countryCode, JobStatus status) {
    this.jobId = jobId;
    this.countryCode = countryCode;
    this.status = status;
    this.errorMessage = null;
  }
}
