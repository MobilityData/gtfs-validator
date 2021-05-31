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

import com.beust.jcommander.JCommander;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.flogger.FluentLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.mobilitydata.gtfsvalidator.outputcomparator.io.ValidationReport;

public class Main {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private static final String ACCEPTANCE_REPORT_JSON = "acceptance_report.json";

  public static void main(String[] argv) {
    Arguments args = new Arguments();
    new JCommander(args).parse(argv);
    File[] reportDirectory = new File(args.getReportDirectory()).listFiles();
    if (reportDirectory == null) {
      logger.atSevere().log("Specified output is not a directory, or an I/O error occurred");
      return;
    }
    if (reportDirectory.length == 0) {
      logger.atSevere().log(
          "Specified directory is empty, cannot generate acceptance tests report.");
      return;
    }
    ImmutableMap.Builder<String, Integer> mapBuilder = new Builder<>();
    int badDatasetCount = 0;
    List<File> reportDirs =
        Arrays.stream(reportDirectory).filter(File::isDirectory).collect(Collectors.toList());

    int totalDatasetCount = reportDirs.size();

    for (File file : reportDirs) {
      try {
        ValidationReport referenceReport =
            ValidationReport.fromPath(
                file.toPath().resolve(args.getReferenceValidationReportName()));
        ValidationReport latestReport =
            ValidationReport.fromPath(file.toPath().resolve(args.getLatestValidationReportName()));

        if (referenceReport.hasSameErrorCodes(latestReport)) {
          continue;
        }
        if (referenceReport.equals(latestReport)) {
          continue;
        }
        int newErrorCount = referenceReport.getNewErrorCount(latestReport);
        mapBuilder.put(file.getName(), newErrorCount);
        if (newErrorCount >= args.getThreshold()) {
          badDatasetCount += 1;
        }
      } catch (FileNotFoundException e) {
        logger.atSevere().withCause(e).log(String.format("No file found at %s.", file.getPath()));
        System.exit(1);
      } catch (IOException e) {
        logger.atSevere().withCause(e);
        System.exit(1);
      }
    }
    try {
      exportAcceptanceReport(mapBuilder.build(), args.getReportDirectory());
    } catch (IOException ioException) {
      logger.atSevere().withCause(ioException).log(
          String.format("Error while writing file at: %s", args.getReportDirectory()));
      System.exit(1);
    }
    isNewRuleValid(badDatasetCount, totalDatasetCount, args.getAcceptanceCriteria());
  }

  /**
   * Exports the acceptance test report (map of String, Object) as json.
   *
   * @param acceptanceReportData acceptance report content.
   * @param outputBase base path to output.
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
   * Exits on non-zero code 2 if the ratio badDatasetCount/totalDatasetCount exceeds the threshold
   * defined as acceptance criteria.
   *
   * @param badDatasetCount the number of nw invalid datasets
   * @param totalDatasetCount the number of datasets to be tested
   * @param threshold the acceptance criteria
   */
  private static void isNewRuleValid(int badDatasetCount, int totalDatasetCount, double threshold) {
    System.out.printf(
        "%d out of %d datasets (~%.2f %%)  are invalid due to new implementation.%n",
        badDatasetCount, totalDatasetCount, 100.0 * badDatasetCount / totalDatasetCount);
    if (100.0 * badDatasetCount / totalDatasetCount >= threshold) {
      System.out.printf(
          "The percentage of new invalid datasets exceeds the defined threshold (%.2f %% > %.2f %%).%n",
          100.0 * badDatasetCount / totalDatasetCount, threshold);
      System.exit(2);
    }
    System.out.printf(
        "Percentage of new invalid datasets is inferior to the defined threshold (%.2f %% < %.2f %%)",
        100.0 * badDatasetCount / totalDatasetCount, threshold);
  }
}
