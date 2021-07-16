/*
 * Copyright 2020 MobilityData IO
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

package org.mobilitydata.gtfsvalidator.outputcomparator.cli;

import com.beust.jcommander.Parameter;

/** Command-line arguments for output-comparator CLI. */
public class Arguments {

  @Parameter(
      names = {"-d", "--report_directory"},
      description = "Directory where reports are stored.",
      required = true)
  private String reportDirectory;

  @Parameter(
      names = {"-n", "--new_error_threshold"},
      description = "Number of new errors per datasets",
      required = true)
  private int newErrorThreshold;

  @Parameter(
      names = {"-r", "--reference_report_name"},
      description = "Name of the reference validation report")
  private String referenceValidationReportName;

  @Parameter(
      names = {"-l", "--latest_report_name"},
      description = "Name of the latest validation report")
  private String latestValidationReportName;

  @Parameter(
      names = {"-p", "--percent_invalid_datasets_threshold"},
      description = "Maximum percentage of new invalid datasets.",
      required = true)
  private double percentInvalidDatasetsThreshold;

  public String getReportDirectory() {
    return reportDirectory;
  }

  public int getNewErrorThreshold() {
    return newErrorThreshold;
  }

  public String getReferenceValidationReportName() {
    return referenceValidationReportName;
  }

  public String getLatestValidationReportName() {
    return latestValidationReportName;
  }

  public double getPercentInvalidDatasetsThreshold() {
    return percentInvalidDatasetsThreshold;
  }
}
