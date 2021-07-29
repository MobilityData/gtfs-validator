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

import com.beust.jcommander.JCommander;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.flogger.FluentLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.mobilitydata.gtfsvalidator.outputcomparator.io.ValidationReport;

public class Main {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private static final String ACCEPTANCE_REPORT_JSON = "acceptance_report.json";
  private static final int IO_EXCEPTION_EXIT_CODE = 1;
  private static final int INVALID_NEW_RULE_EXIT_CODE = 2;

  public static void main(String[] argv) {
    Arguments args = new Arguments();
    new JCommander(args).parse(argv);
    List<File> reportDirectory = null;
    try {
      reportDirectory = Files.list(Paths.get(args.getReportDirectory()))
          .map(Path::toFile).collect(Collectors.toList());
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
    if (reportDirectory == null) {
      logger.atSevere().log("Specified output is not a directory, or an I/O error occurred");
      return;
    }
    if (reportDirectory.isEmpty()) {
      logger.atSevere().log(
          "Specified directory is empty, cannot generate acceptance tests report.");
      return;
    }
    ImmutableMap.Builder<String, Integer> mapBuilder = new Builder<>();
    int badDatasetCount = 0;

    List<File> reportDirs = reportDirectory.stream().filter(File::isDirectory)
        .collect(Collectors.toList());

    int totalDatasetCount = reportDirs.size();

    try {
      for (File file : reportDirs) {
        int newErrorCount = 0;
        ValidationReport referenceReport =
            ValidationReport.fromPath(
                file.toPath().resolve(args.getReferenceValidationReportName()));
        ValidationReport latestReport =
            ValidationReport.fromPath(file.toPath().resolve(args.getLatestValidationReportName()));

        if (referenceReport.hasSameErrorCodes(latestReport)) {
          mapBuilder.put(file.getName(), newErrorCount);
          continue;
        }
        newErrorCount = referenceReport.getNewErrorCount(latestReport);
        mapBuilder.put(file.getName(), newErrorCount);
        if (newErrorCount >= args.getNewErrorThreshold()) {
          ++badDatasetCount;
        }
      }
      exportAcceptanceReport(mapBuilder.build(), args.getReportDirectory());
      checkRuleValidity(badDatasetCount, totalDatasetCount,
          args.getPercentInvalidDatasetsThreshold());
    } catch (IOException e) {
      logger.atSevere().withCause(e);
      System.exit(IO_EXCEPTION_EXIT_CODE);
    }
  }

  /**
   * Exports the acceptance test report (map of String, Object) as json.
   *
   * @param acceptanceReportData acceptance report content.
   * @param outputBase           base path to output.
   * @throws IOException if an I/O error occurs writing to or creating the file.
   */
  private static void exportAcceptanceReport(
      ImmutableMap<String, Integer> acceptanceReportData, String outputBase) throws IOException {
    Gson gson = new GsonBuilder().serializeNulls().create();
    Files.write(
        Paths.get(outputBase, ACCEPTANCE_REPORT_JSON),
        gson.toJson(acceptanceReportData).getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Exits on non-zero code 2 if the ratio badDatasetCount/totalDatasetCount is greater than or
   * equal to the threshold defined as acceptance criteria.
   *
   * @param badDatasetCount   the number of new invalid datasets
   * @param totalDatasetCount the number of datasets to be tested
   * @param threshold         the acceptance criteria
   */
  private static void checkRuleValidity(int badDatasetCount, int totalDatasetCount,
      double threshold) {
    double invalidDatasetRatio = 100.0 * badDatasetCount / totalDatasetCount;
    StringBuilder builder = new StringBuilder();
    builder
        .append(
            String.format("%d out of %d datasets (~%.2f %%) are invalid due to code change, ",
                badDatasetCount, totalDatasetCount, invalidDatasetRatio));
    if (invalidDatasetRatio >= threshold) {
      builder.append(String.format(
          "which is greater than or equal to the provided threshold of %.2f %%.%n"
              + "❌ Rule acceptance tests failed.%n", threshold));
      System.out.println(builder);
      System.exit(INVALID_NEW_RULE_EXIT_CODE);
    }
    builder.append(String.format(
        "which is less than the provided threshold of %.2f %%.%n"
            + "✅ Rule acceptance tests passed.", threshold));
    System.out.println(builder);
  }
}
