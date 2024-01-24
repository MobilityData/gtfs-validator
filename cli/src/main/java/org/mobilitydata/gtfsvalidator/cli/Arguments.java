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
import com.google.common.flogger.FluentLogger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;

/** Command-line arguments for GTFS Validator CLI. */
public class Arguments {

  private FluentLogger logger = FluentLogger.forEnclosingClass();

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
      names = {"-c", "--country_code"},
      description =
          "Country code of the feed, e.g., `nl`. "
              + "It must be a two-letter country code (ISO 3166-1 alpha-2)")
  private String countryCode;

  @Parameter(
      names = {"-d", "--date"},
      description =
          "Date to simulate when validating, in ISO_LOCAL_DATE format like "
              + "'2001-01-30'. By default, the current date is used. "
              + "This option can be used to debug rules like feed expiration.")
  private String dateString;

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
      names = {"-r", "--html_report_name"},
      description = "The name of the HTML validation report including .html extension.")
  private String htmlReportName;

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

  ValidationRunnerConfig toConfig() throws URISyntaxException {
    ValidationRunnerConfig.Builder builder = ValidationRunnerConfig.builder();
    if (input != null) {
      builder.setGtfsSource(Path.of(input).toUri());
    } else if (url != null) {
      builder.setGtfsSource(new URI(url));
      if (storageDirectory != null) {
        builder.setStorageDirectory(Path.of(storageDirectory));
      }
    }
    if (outputBase != null) {
      builder.setOutputDirectory(Path.of(outputBase));
    }
    if (countryCode != null) {
      builder.setCountryCode(CountryCode.forStringOrUnknown(countryCode));
    }
    if (dateString != null) {
      builder.setDateForValidation(LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE));
    }
    if (validationReportName != null) {
      builder.setValidationReportFileName(validationReportName);
    }
    if (htmlReportName != null) {
      builder.setHtmlReportFileName(htmlReportName);
    }
    if (systemErrorsReportName != null) {
      builder.setSystemErrorsReportFileName(systemErrorsReportName);
    }
    builder.setNumThreads(numThreads);
    builder.setPrettyJson(pretty);
    return builder.build();
  }

  public String getOutputBase() {
    return outputBase;
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

  /** @return true if CLI parameter combination is legal, otherwise return false */
  public boolean validate() {
    if (getExportNoticeSchema() && abortAfterNoticeSchemaExport()) {
      return true;
    }

    if (input == null && url == null) {
      logger.atSevere().log(
          "One of the two following CLI parameter must be provided: '--input' and '--url'");
      return false;
    }
    if (input != null && url != null) {
      logger.atSevere().log(
          "The two following CLI parameters cannot be provided at the same time:"
              + " '--input' and '--url'");
      return false;
    }
    if (storageDirectory != null && url == null) {
      logger.atSevere().log(
          "CLI parameter '--storage_directory' must not be provided if '--url' is not provided");
      return false;
    }

    return true;
  }

  public boolean abortAfterNoticeSchemaExport() {
    return input == null && url == null;
  }
}
