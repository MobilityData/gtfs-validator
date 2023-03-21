package org.mobilitydata.gtfsvalidator.web.service.controller;

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
