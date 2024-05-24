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
import org.mobilitydata.gtfsvalidator.outputcomparator.cli.ValidationReportComparator.Result;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.SourceUrlContainer;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.report.AcceptanceReport;

public class Main {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  static final String ACCEPTANCE_REPORT_JSON = "acceptance_report.json";
  static final String ACCEPTANCE_REPORT_SUMMARY_MD = "acceptance_report_summary.md";
  private static final int IO_EXCEPTION_EXIT_CODE = 1;
  private static final int COMPARISON_FAILURE_EXIT_CODE = 2;
  private static final Gson GSON =
      new GsonBuilder()
          .serializeNulls()
          .disableHtmlEscaping()
          .serializeSpecialFloatingPointValues()
          .create();

  public static void main(String[] argv) {
    int rc = run(argv);
    if (rc != 0) {
      System.exit(rc);
    }
  }

  public static int run(String[] argv) {
    Arguments args = new Arguments();
    new JCommander(args).parse(argv);
    List<File> reportDirectory = null;
    try {
      reportDirectory =
          Files.list(Paths.get(args.getReportDirectory()))
              .map(Path::toFile)
              .collect(Collectors.toList());
      if (reportDirectory.isEmpty()) {
        logger.atSevere().log(
            "Specified directory is empty, cannot generate acceptance tests report.");
        return 0;
      }
    } catch (IOException ioException) {
      logger.atSevere().withCause(ioException).log("Error reading report directory");
      return IO_EXCEPTION_EXIT_CODE;
    }
    List<File> reportDirs =
        reportDirectory.stream().filter(File::isDirectory).collect(Collectors.toList());

    SourceUrlContainer sourceUrlContainer = null;
    try {
      sourceUrlContainer = new SourceUrlContainer(args.getSourceUrlPath());
    } catch (IOException ioException) {
      logger.atSevere().withCause(ioException).log("Error loading source url container");
      return IO_EXCEPTION_EXIT_CODE;
    }

    ValidationReportComparator comparator = new ValidationReportComparator();
    Result result = comparator.compareValidationRuns(args, reportDirs, sourceUrlContainer);

    if (!(new File(args.getOutputBase()).mkdirs())) {
      logger.atSevere().log("Error creating output base directory: " + args.getOutputBase());
    }

    exportAcceptanceReport(result.report(), args.getOutputBase());

    System.out.print(result.reportSummary());
    exportReportSummary(result.reportSummary(), args.getOutputBase());

    if (result.failure()) {
      return COMPARISON_FAILURE_EXIT_CODE;
    }

    return 0;
  }

  /**
   * Exports acceptance test reports.
   *
   * @param report the acceptance test report
   * @param outputBase the name of the directory used to save files
   */
  private static void exportAcceptanceReport(AcceptanceReport report, String outputBase) {
    try {
      Files.write(
          Paths.get(outputBase, ACCEPTANCE_REPORT_JSON),
          GSON.toJson(report).getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      logger.atSevere().withCause(e).log("Cannot store acceptance test report file");
    }
  }

  /**
   * Exports acceptance test report summary text.
   *
   * @param reportSummary the acceptance test report summary string
   * @param outputBase the name of the directory used to save files
   */
  private static void exportReportSummary(String reportSummary, String outputBase) {
    try {
      Files.write(
          Paths.get(outputBase, ACCEPTANCE_REPORT_SUMMARY_MD),
          reportSummary.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      logger.atSevere().withCause(e).log("Cannot store acceptance test report summary file");
    }
  }
}
