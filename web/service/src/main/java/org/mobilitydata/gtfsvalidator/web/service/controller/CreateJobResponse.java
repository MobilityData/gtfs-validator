package org.mobilitydata.gtfsvalidator.web.service.controller;

/**
 * A JSON response to a request to create a new job.
 *
 * <p>The jobId can be used to trigger a validation job.
 *
 * <p>If a url was provided in the request, the url field will be null, otherwise it will be a
 * signed url for uploading a GTFS feed to GCS.
 */
public class CreateJobResponse {
  private final String jobId;
  private final String url;

  public CreateJobResponse(String jobId, String url) {
    this.jobId = jobId;
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public String getJobId() {
    return jobId;
  }
}
