/*
 * Copyright 2021 MobilityData IO
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
import java.nio.file.Path;
import java.util.Optional;

/** Command-line arguments for output-comparator CLI. */
public class Arguments {

  @Parameter(
      names = {"-d", "--report_directory"},
      description = "Directory where reports are stored.",
      required = true)
  private String reportDirectory;

  @Parameter(
      names = {"-n", "--new_error_threshold"},
      description =
          "Number of new errors per datasets. A dataset is considered newly invalid if "
              + "the number of new errors exceeds this threshold.",
      required = true)
  private int newErrorThreshold;

  @Parameter(
      names = {"-r", "--reference_report_name"},
      description = "Name of the reference validation report.")
  private String referenceValidationReportName;

  @Parameter(
      names = {"-l", "--latest_report_name"},
      description = "Name of the latest validation report.")
  private String latestValidationReportName;

  @Parameter(
      names = {"-p", "--percent_invalid_datasets_threshold"},
      description =
          "Maximum percentage of new invalid datasets. Passing a '10' value would mean that no "
              + "more than 10% of datasets should generate new error types.",
      required = true)
  private float percentInvalidDatasetsThreshold;

  @Parameter(
      names = {"-o", "--output_base"},
      description = "Base directory to store the outputs.",
      required = true)
  private String outputBase;

  @Parameter(
      names = {"-s", "--source_urls"},
      description = "Path to source urls.",
      required = true)
  private String sourceUrlPath;

  @Parameter(
      names = {"-c", "--percent_corrupted_sources"},
      description = "Maximum percentage of corrupted sources. ",
      required = true)
  private float percentCorruptedSourcesThreshold;

  @Parameter(
      names = {"--run_id"},
      description = "Id of the run from GitHub workflow.",
      required = false)
  private String runId;

  @Parameter(
      names = {"--commit_sha"},
      description = "SHA fingerprint of the GitHub commit.",
      required = false)
  private String commitSha;

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

  public float getPercentInvalidDatasetsThreshold() {
    return percentInvalidDatasetsThreshold;
  }

  public String getOutputBase() {
    return outputBase;
  }

  public Path getSourceUrlPath() {
    return Path.of(sourceUrlPath);
  }

  public float getPercentCorruptedSourcesThreshold() {
    return percentCorruptedSourcesThreshold;
  }

  public Optional<String> getRunId() {
    return Optional.ofNullable(runId);
  }

  public Optional<String> getCommitSha() {
    return Optional.ofNullable(commitSha);
  }
}
