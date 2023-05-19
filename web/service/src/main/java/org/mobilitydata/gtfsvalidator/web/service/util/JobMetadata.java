package org.mobilitydata.gtfsvalidator.web.service.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/** Metadata about a validation job, saved as json to GCS. */
@Data
public class JobMetadata {
  private String jobId;
  private String countryCode;
  private JobStatus status;
  private String errorMessage;

  @JsonCreator
  public JobMetadata(
          @JsonProperty("jobId") String jobId,
          @JsonProperty("countryCode") String countryCode,
          @JsonProperty("status") JobStatus status,
          @JsonProperty("errorMessage") String errorMessage) {
    this.jobId = jobId;
    this.countryCode = countryCode;
    this.status = status;
    this.errorMessage = errorMessage;
  }

  public JobMetadata(String jobId, String countryCode, JobStatus status) {
    this(jobId, countryCode, status, null);
  }

  public JobMetadata(String jobId, String countryCode) {
    this(jobId, countryCode, JobStatus.PENDING, null);
  }

  public JobMetadata(String jobId) {
    this(jobId, null, JobStatus.PENDING, null);
  }
}
