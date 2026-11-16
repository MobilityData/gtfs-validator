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
  private String originalGtfsSource;

  public JobMetadata(String jobId, String countryCode) {
    this(jobId, countryCode, null);
  }
}
