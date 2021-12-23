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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.mobilitydata.gtfsvalidator.model.ValidationReport;
import org.mobilitydata.gtfsvalidator.outputcomparator.io.NoticeComparisonReport;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.SourceUrlContainer;

public class Main {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  static final String ACCEPTANCE_REPORT_JSON = "acceptance_report.json";
  static final String SOURCES_CORRUPTION_REPORT_JSON = "sources_corruption_report.json";
  private static final int IO_EXCEPTION_EXIT_CODE = 1;
  private static final int INVALID_NEW_RULE_EXIT_CODE = 2;
  static final int TOO_MANY_CORRUPTED_SOURCES_EXIT_CODE = 3;
  private static final Gson GSON =
      new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
  private static final String NOTICE_CODE = "noticeCode";
  private static final String AFFECTED_SOURCES_COUNT = "affectedSourcesCount";
  private static final String AFFECTED_SOURCES = "affectedSources";
  private static final String NEW_ERRORS = "newErrors";
  private static final String CORRUPTED_SOURCES = "corruptedSources";
  private static final String TEST_STATUS = "status";
  private static final String CORRUPTED_SOURCES_COUNT = "corruptedSourcesCount";
  private static final String MAX_PERCENTAGE_CORRUPTED_SOURCES = "maxPercentageCorruptedSources";
  private static final String VALID = "valid";
  private static final String INVALID = "invalid";
  private static final String SOURCE_ID_COUNT = "sourceIdCount";

  public static void main(String[] argv) {
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
        return;
      }
    } catch (IOException ioException) {
      logger.atSevere().withCause(ioException);
      System.exit(IO_EXCEPTION_EXIT_CODE);
    }
    int badDatasetCount = 0;
    int sourceIdCount = 0;
    List<String> corruptedSources = new ArrayList<>();
    List<File> reportDirs =
        reportDirectory.stream().filter(File::isDirectory).collect(Collectors.toList());
    Map<String, NoticeComparisonReport> acceptanceTestReportMap = new TreeMap<>();

    SourceUrlContainer sourceUrlContainer = null;
    try {
      sourceUrlContainer = new SourceUrlContainer(args.getSourceUrlPath());
    } catch (IOException ioException) {
      logger.atSevere().withCause(ioException);
      System.exit(IO_EXCEPTION_EXIT_CODE);
    }

    for (File file : reportDirs) {
      String sourceId = file.getName();
      // check if file.getName is sourceId aka check that we do not iterate over a directory created
      // by github
      if (!sourceUrlContainer.hasSourceId(sourceId)) {
        continue;
      }
      sourceIdCount++;
      Path referenceReportPath = file.toPath().resolve(args.getReferenceValidationReportName());
      Path latestReportPath = file.toPath().resolve(args.getLatestValidationReportName());
      // in case a validation report does not exist for a sourceId we add the sourceId to
      // the list of corrupted sources
      if (!(referenceReportPath.toFile().exists() && latestReportPath.toFile().exists())) {
        corruptedSources.add(sourceId);
        continue;
      }
      ValidationReport referenceReport;
      ValidationReport latestReport;
      try {
        referenceReport = ValidationReport.fromPath(referenceReportPath);
        latestReport = ValidationReport.fromPath(latestReportPath);
      } catch (IOException ioException) {
        logger.atSevere().withCause(ioException);
        // in case a file is corrupted, add the sourceId to the list of corrupted sources
        corruptedSources.add(sourceId);
        continue;
      }
      if (referenceReport.hasSameErrorCodes(latestReport)) {
        continue;
      }
      for (String noticeCode : referenceReport.getNewErrorsListing(latestReport)) {
        NoticeComparisonReport noticeComparisonReport =
            acceptanceTestReportMap.getOrDefault(noticeCode, new NoticeComparisonReport());
        acceptanceTestReportMap.putIfAbsent(noticeCode, noticeComparisonReport);
        noticeComparisonReport.update(
            sourceId,
            latestReport.getErrorNoticeReportByNoticeCode(noticeCode).getTotalNotices(),
            sourceUrlContainer);
      }
      if (referenceReport.getNewErrorsListing(latestReport).size() >= args.getNewErrorThreshold()) {
        ++badDatasetCount;
      }
    }
    exportReport(
        generateAcceptanceTestReport(acceptanceTestReportMap),
        args.getOutputBase(),
        ACCEPTANCE_REPORT_JSON);
    checkRuleValidity(
        corruptedSources,
        badDatasetCount,
        sourceIdCount,
        args.getPercentInvalidDatasetsThreshold(),
        args.getPercentCorruptedSourcesThreshold(),
        args.getOutputBase(),
        sourceIdCount);
  }

  /**
   * Generates acceptance test report.
   *
   * @param acceptanceTestReportData acceptance test data (mapped by sourceId, {@code NoticeStat})
   * @return the {@code JsonObject} representation of the acceptance test report
   */
  public static JsonObject generateAcceptanceTestReport(
      Map<String, NoticeComparisonReport> acceptanceTestReportData) {
    JsonObject root = new JsonObject();
    JsonArray jsonNotices = new JsonArray();
    root.add(NEW_ERRORS, jsonNotices);

    for (String noticeCode : acceptanceTestReportData.keySet()) {
      JsonObject noticeStatJson = new JsonObject();
      jsonNotices.add(noticeStatJson);
      JsonObject noticeContext = acceptanceTestReportData.get(noticeCode).toJson();
      noticeStatJson.addProperty(NOTICE_CODE, noticeCode);
      noticeStatJson.add(AFFECTED_SOURCES_COUNT, noticeContext.get(AFFECTED_SOURCES_COUNT));
      noticeStatJson.add(AFFECTED_SOURCES, noticeContext.get(AFFECTED_SOURCES));
    }
    return root;
  }

  /**
   * Generates file corruption report.
   *
   * @param corruptedSources list of corrupted sourceIds
   * @return the {@code JsonObject} representation of the file corruption report
   */
  private static JsonObject generateFileCorruptionReport(List<String> corruptedSources) {
    JsonObject root = new JsonObject();
    JsonArray jsonCorruptedSources = new JsonArray();
    root.add(CORRUPTED_SOURCES, jsonCorruptedSources);
    for (String sourceId : corruptedSources) {
      jsonCorruptedSources.add(sourceId);
    }
    return root;
  }

  /**
   * Exports acceptance test reports.
   *
   * @param data the JSON representation of the acceptance test report
   * @param outputBase the name of the directory used to save files
   */
  @VisibleForTesting
  public static void exportReport(JsonObject data, String outputBase, String filename) {
    new File(outputBase).mkdirs();
    try {
      Files.write(
          Paths.get(outputBase, filename), GSON.toJson(data).getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      logger.atSevere().withCause(e).log("Cannot store acceptance test report file");
    }
  }

  /**
   * Exits on non-zero code {@code Main#INVALID_NEW_RULE_EXIT_CODE} if the ratio
   * badDatasetCount/totalDatasetCount is greater than or equal to the threshold defined as
   * acceptance criteria; or if the number of corrupted files is greater or equal to the limit set
   * by the user.
   *
   * @param corruptedSources corrupted source ids
   * @param badDatasetCount the number of new invalid datasets
   * @param totalDatasetCount the number of datasets to be tested
   * @param threshold the acceptance criteria
   * @param percentCorruptedSourcesThreshold the maximum percentage of corrupted source ids
   */
  private static void checkRuleValidity(
      List<String> corruptedSources,
      int badDatasetCount,
      int totalDatasetCount,
      double threshold,
      float percentCorruptedSourcesThreshold,
      String outputBase,
      int sourceIdCount) {
    double invalidDatasetRatio = 100.0 * badDatasetCount / totalDatasetCount;
    double corruptedFilesRatio = 100.0 * corruptedSources.size() / totalDatasetCount;
    StringBuilder builder = new StringBuilder();
    JsonObject jsonCorruptedSources = generateFileCorruptionReport(corruptedSources);
    jsonCorruptedSources.addProperty(SOURCE_ID_COUNT, sourceIdCount);
    if (corruptedFilesRatio >= percentCorruptedSourcesThreshold) {
      builder.append(
          String.format(
              " ❌ Invalid acceptance test. %d out of %d sources (~%.2f %%) are corrupted, which "
                  + "is greater than or equal to the provided threshold of %.2f. Details about"
                  + " the corrupted sources: %s.",
              corruptedSources.size(),
              totalDatasetCount,
              corruptedFilesRatio,
              percentCorruptedSourcesThreshold,
              corruptedSources));
      jsonCorruptedSources.addProperty(TEST_STATUS, INVALID);
      jsonCorruptedSources.addProperty(CORRUPTED_SOURCES_COUNT, corruptedSources.size());
      jsonCorruptedSources.addProperty(
          MAX_PERCENTAGE_CORRUPTED_SOURCES, percentCorruptedSourcesThreshold);
      exportReport(jsonCorruptedSources, outputBase, SOURCES_CORRUPTION_REPORT_JSON);
      System.out.println(builder);
      System.exit(TOO_MANY_CORRUPTED_SOURCES_EXIT_CODE);
    }
    builder.append(
        String.format(
            "%d out of %d datasets (~%.2f %%) are invalid due to code change, ",
            badDatasetCount, totalDatasetCount, invalidDatasetRatio));
    jsonCorruptedSources.addProperty(TEST_STATUS, VALID);
    jsonCorruptedSources.addProperty(CORRUPTED_SOURCES_COUNT, corruptedSources.size());
    jsonCorruptedSources.addProperty(
        MAX_PERCENTAGE_CORRUPTED_SOURCES, percentCorruptedSourcesThreshold);
    exportReport(jsonCorruptedSources, outputBase, SOURCES_CORRUPTION_REPORT_JSON);
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
                + "✅ Rule acceptance tests passed",
            threshold));
    System.out.println(builder);
  }
}
