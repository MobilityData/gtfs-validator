/*
 * Copyright 2023 Jarvus Innovations LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mobilitydata.gtfsvalidator.web.service.controller;

/**
 * A JSON request to create a new job. All fields are optional. If url is specified, it will be used
 * to download the GTFS feed, otherwise a signed url will be returned for uploading a file to GCS.
 */
public class CreateJobRequest {

  private String countryCode;
  private String url;

  public CreateJobRequest() {}

  public CreateJobRequest(String countryCode, String url) {
    this.countryCode = countryCode;
    this.url = url;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public String getUrl() {
    return url;
  }
}
