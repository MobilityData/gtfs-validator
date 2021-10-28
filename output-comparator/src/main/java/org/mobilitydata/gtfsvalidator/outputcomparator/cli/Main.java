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
import com.google.common.annotations.VisibleForTesting;
import com.google.common.flogger.FluentLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.mobilitydata.gtfsvalidator.outputcomparator.io.NoticeStat;
import org.mobilitydata.gtfsvalidator.outputcomparator.io.ValidationReport;

public class Main {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private static final String ACCEPTANCE_REPORT_JSON = "acceptance_report.json";
  private static final int IO_EXCEPTION_EXIT_CODE = 1;
  private static final int INVALID_NEW_RULE_EXIT_CODE = 2;
  private static final Gson GSON = new GsonBuilder().serializeNulls().create();

  public static void main(String[] argv) {
    Arguments args = new Arguments();
    new JCommander(args).parse(argv);
    List<File> reportDirectory = null;
    try {
      reportDirectory =
          Files.list(Paths.get(args.getReportDirectory()))
              .map(Path::toFile)
              .collect(Collectors.toList());
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
    int badDatasetCount = 0;

    List<File> reportDirs =
        reportDirectory.stream().filter(File::isDirectory).collect(Collectors.toList());

    int totalDatasetCount = reportDirs.size();
    Map<String, NoticeStat> acceptanceTestReportMap = new TreeMap<>();

    try {
      for (File file : reportDirs) {
        int newErrorCount;
        ValidationReport referenceReport =
            ValidationReport.fromPath(
                file.toPath().resolve(args.getReferenceValidationReportName()));
        ValidationReport latestReport =
            ValidationReport.fromPath(file.toPath().resolve(args.getLatestValidationReportName()));
        if (referenceReport.hasSameErrorCodes(latestReport)) {
          continue;
        }
        for (String noticeCode : referenceReport.getNewErrorsListing(latestReport)) {
          NoticeStat noticeStat =
              acceptanceTestReportMap.getOrDefault(noticeCode, NoticeStat.newInstance());
          acceptanceTestReportMap.putIfAbsent(noticeCode, noticeStat);
          noticeStat.update(file.getName(), latestReport.getNoticeByCode(noticeCode).getCount());
        }

        newErrorCount = referenceReport.getNewErrorCount(latestReport);
        if (newErrorCount >= args.getNewErrorThreshold()) {
          ++badDatasetCount;
        }
      }
      exportAcceptanceTestReport(generateAcceptanceTestReport(acceptanceTestReportMap), args);
      checkRuleValidity(
          badDatasetCount, totalDatasetCount, args.getPercentInvalidDatasetsThreshold());
    } catch (IOException e) {
      logger.atSevere().withCause(e);
      System.exit(IO_EXCEPTION_EXIT_CODE);
    }
  }

  /**
   * Generates acceptance test report.
   *
   * @param acceptanceTestReportData acceptance test data (mapped by datasetId, {@code NoticeStat})
   * @return the {@code JsonObject} representation of the acceptance test report
   */
  @VisibleForTesting
  public static JsonObject generateAcceptanceTestReport(
      Map<String, NoticeStat> acceptanceTestReportData) {
    JsonObject root = new JsonObject();
    JsonArray jsonNotices = new JsonArray();
    root.add("newErrors", jsonNotices);

    for (String noticeCode : acceptanceTestReportData.keySet()) {
      JsonObject noticeStatJson = new JsonObject();
      jsonNotices.add(noticeStatJson);
      noticeStatJson.add(noticeCode, acceptanceTestReportData.get(noticeCode).toJson());
    }
    return root;
  }

  /**
   * Exports acceptance test reports.
   *
   * @param acceptanceTestReportData the JSON representation of the acceptance test report
   * @param arguments the {@code Arguments} used to retrieve the path to save files
   */
  private static void exportAcceptanceTestReport(
      JsonObject acceptanceTestReportData, Arguments arguments) {
    new File(arguments.getOutputBase()).mkdirs();
    try {
      Files.write(
          Paths.get(arguments.getOutputBase(), ACCEPTANCE_REPORT_JSON),
          GSON.toJson(acceptanceTestReportData).getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      logger.atSevere().withCause(e).log("Cannot store acceptance test report file");
    }
  }

  /**
   * Exits on non-zero code 2 if the ratio badDatasetCount/totalDatasetCount is greater than or
   * equal to the threshold defined as acceptance criteria.
   *
   * @param badDatasetCount the number of new invalid datasets
   * @param totalDatasetCount the number of datasets to be tested
   * @param threshold the acceptance criteria
   */
  private static void checkRuleValidity(
      int badDatasetCount, int totalDatasetCount, double threshold) {
    double invalidDatasetRatio = 100.0 * badDatasetCount / totalDatasetCount;
    StringBuilder builder = new StringBuilder();
    builder.append(
        String.format(
            "%d out of %d datasets (~%.2f %%) are invalid due to code change, ",
            badDatasetCount, totalDatasetCount, invalidDatasetRatio));
    if (invalidDatasetRatio >= threshold) {
      builder.append(
          String.format(
              "which is greater than or equal to the provided threshold of %.2f %%.%n"
                  + "❌ Rule acceptance tests failed.%n",
              threshold));
      System.out.println(builder);
      System.exit(INVALID_NEW_RULE_EXIT_CODE);
    }
    builder.append(
        String.format(
            "which is less than the provided threshold of %.2f %%.%n"
                + "✅ Rule acceptance tests passed.",
            threshold));
    System.out.println(builder);
  }
}
