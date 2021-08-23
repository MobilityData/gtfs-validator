/*
 * Copyright 2020 Google LLC, MobilityData IO
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

package org.mobilitydata.gtfsvalidator.cli;

import com.beust.jcommander.Parameter;

/** Command-line arguments for GTFS Validator CLI. */
public class Arguments {

  public static final String VALIDATION_REPORT_DEFAULT_NAME_JSON = "report.json";
  public static final String SYSTEM_ERRORS_REPORT_DEFAULT_NAME = "system_errors.json";

  @Parameter(
      names = {"-i", "--input"},
      description = "Location of the input GTFS ZIP or unarchived directory")
  private String input;

  @Parameter(
      names = {"-o", "--output_base"},
      description = "Base directory to store the outputs",
      required = true)
  private String outputBase;

  @Parameter(
      names = {"-t", "--threads"},
      description = "Number of threads to use")
  private int numThreads = 1;

  @Parameter(
      names = {"-f", "--feed_name"},
      description = "Deprecated: please use '-c' or '-country_code' instead.")
  private String feedName;

  @Parameter(
      names = {"-c", "--country_code"},
      description =
          "Country code of the feed, e.g., `nl`. "
              + "It must be a two-letter country code (ISO 3166-1 alpha-2)")
  private String countryCode;

  @Parameter(
      names = {"-u", "--url"},
      description = "Fully qualified URL to download GTFS archive")
  private String url;

  @Parameter(
      names = {"-s", "--storage_directory"},
      description =
          "Target path where to store the GTFS archive "
              + "downloaded from network (if not provided, the ZIP will be stored in memory)")
  private String storageDirectory;

  @Parameter(
      names = {"-v", "--validation_report_name"},
      description = "The name of the validation report including .json extension.")
  private String validationReportName;

  @Parameter(
      names = {"-e", "--system_errors_report_name"},
      description = "The name of the system errors report including .json extension.")
  private String systemErrorsReportName;

  @Parameter(
      names = {"-h", "--help"},
      description = "Print help",
      help = true)
  private boolean help = false;

  @Parameter(
      names = {"-p", "--pretty"},
      description = "Pretty json output")
  private boolean pretty = false;

  @Parameter(
      names = {"-n", "--export_notices_schema"},
      description = "Export notices schema")
  private boolean exportNoticeSchema = false;

  public String getFeedName() {
    return feedName;
  }

  public String getInput() {
    return input;
  }

  public String getOutputBase() {
    return outputBase;
  }

  public int getNumThreads() {
    return numThreads;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public String getUrl() {
    return url;
  }

  public String getStorageDirectory() {
    return storageDirectory;
  }

  public String getValidationReportName() {
    if (validationReportName == null) {
      return VALIDATION_REPORT_DEFAULT_NAME_JSON;
    }
    return validationReportName;
  }

  public String getSystemErrorsReportName() {
    if (systemErrorsReportName == null) {
      return SYSTEM_ERRORS_REPORT_DEFAULT_NAME;
    }
    return systemErrorsReportName;
  }

  public boolean getHelp() {
    return help;
  }

  public boolean getPretty() {
    return pretty;
  }

  public boolean getExportNoticeSchema() {
    return exportNoticeSchema;
  }

  public boolean abortAfterNoticeSchemaExport() {
    return input == null
        && countryCode == null
        && url == null
        && storageDirectory == null
        && validationReportName == null
        && systemErrorsReportName == null;
  }
}
