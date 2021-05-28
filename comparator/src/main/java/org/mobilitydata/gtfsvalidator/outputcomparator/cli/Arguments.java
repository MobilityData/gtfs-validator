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

/** Command-line arguments for GTFS Validator CLI. */
public class Arguments {

  @Parameter(
      names = {"-o", "--output_base"},
      description = "Base directory to store the outputs",
      required = true)
  private String outputBase;

  @Parameter(
      names = {"-t", "--threshold"},
      description = "Number of new errors threshold",
      required = true)
  private int threshold;

  @Parameter(
      names = {"-r", "--reference_report_name"},
      description = "Name of the reference valdation report",
      required = true)
  private String referenceValidationReportName;

  @Parameter(
      names = {"-l", "--latest_report_name"},
      description = "Name of the latest validation report",
      required = true)
  private String latestValidationReportName;

  public String getOutputBase() {
    return outputBase;
  }

  public int getThreshold() {
    return threshold;
  }

  public String getReferenceValidationReportName() {
    return referenceValidationReportName;
  }

  public String getLatestValidationReportName() {
    return latestValidationReportName;
  }
}
