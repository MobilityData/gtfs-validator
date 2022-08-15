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
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.mobilitydata.gtfsvalidator.model.ValidationReport;
import org.mobilitydata.gtfsvalidator.outputcomparator.io.ChangedNoticesCollector;
import org.mobilitydata.gtfsvalidator.outputcomparator.io.CorruptedSourcesCollector;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.SourceUrlContainer;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.report.AcceptanceReport;

public class Main {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  static final String ACCEPTANCE_REPORT_JSON = "acceptance_report.json";
  private static final int IO_EXCEPTION_EXIT_CODE = 1;
  private static final int COMPARISON_FAILURE_EXIT_CODE = 2;
  private static final Gson GSON =
      new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

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
      logger.atSevere().withCause(ioException).log("Error reading report directory");
      System.exit(IO_EXCEPTION_EXIT_CODE);
    }
    List<File> reportDirs =
        reportDirectory.stream().filter(File::isDirectory).collect(Collectors.toList());

    SourceUrlContainer sourceUrlContainer = null;
    try {
      sourceUrlContainer = new SourceUrlContainer(args.getSourceUrlPath());
    } catch (IOException ioException) {
      logger.atSevere().withCause(ioException).log("Error loading source url container");
      System.exit(IO_EXCEPTION_EXIT_CODE);
    }

    ChangedNoticesCollector newErrors =
        new ChangedNoticesCollector(
            args.getNewErrorThreshold(), args.getPercentInvalidDatasetsThreshold());
    ChangedNoticesCollector droppedErrors =
        new ChangedNoticesCollector(
            args.getNewErrorThreshold(), args.getPercentInvalidDatasetsThreshold());
    CorruptedSourcesCollector corruptedSources =
        new CorruptedSourcesCollector(args.getPercentCorruptedSourcesThreshold());

    for (File file : reportDirs) {
      String sourceId = file.getName();
      // check if file.getName is sourceId aka check that we do not iterate over a directory created
      // by github
      if (!sourceUrlContainer.hasSourceId(sourceId)) {
        continue;
      }
      corruptedSources.addSource();
      String sourceUrl = sourceUrlContainer.getUrlForSourceId(sourceId);

      Path referenceReportPath = file.toPath().resolve(args.getReferenceValidationReportName());
      Path latestReportPath = file.toPath().resolve(args.getLatestValidationReportName());
      // in case a validation report does not exist for a sourceId we add the sourceId to
      // the list of corrupted sources
      if (!(referenceReportPath.toFile().exists() && latestReportPath.toFile().exists())) {
        corruptedSources.addCorruptedSource(sourceId);
        continue;
      }
      ValidationReport referenceReport;
      ValidationReport latestReport;
      try {
        referenceReport = ValidationReport.fromPath(referenceReportPath);
        latestReport = ValidationReport.fromPath(latestReportPath);
      } catch (IOException ioException) {
        logger.atSevere().withCause(ioException).log("Error reading validation reports");
        // in case a file is corrupted, add the sourceId to the list of corrupted sources
        corruptedSources.addCorruptedSource(sourceId);
        continue;
      }
      newErrors.compareValidationReports(sourceId, sourceUrl, referenceReport, latestReport);
      droppedErrors.compareValidationReports(sourceId, sourceUrl, latestReport, referenceReport);
    }

    AcceptanceReport report =
        AcceptanceReport.create(
            newErrors.getChangedNotices(),
            droppedErrors.getChangedNotices(),
            corruptedSources.toReport());
    exportReport(report, args.getOutputBase(), ACCEPTANCE_REPORT_JSON);

    boolean failure =
        newErrors.isAboveThreshold()
            || droppedErrors.isAboveThreshold()
            || corruptedSources.isAboveThreshold();
    System.out.print(generateLogString(failure, newErrors, droppedErrors, corruptedSources));

    if (failure) {
      System.exit(COMPARISON_FAILURE_EXIT_CODE);
    }
  }

  /**
   * Exports acceptance test reports.
   *
   * @param report the acceptance test report
   * @param outputBase the name of the directory used to save files
   */
  @VisibleForTesting
  public static void exportReport(AcceptanceReport report, String outputBase, String filename) {
    new File(outputBase).mkdirs();
    try {
      Files.write(
          Paths.get(outputBase, filename), GSON.toJson(report).getBytes(StandardCharsets.UTF_8));
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
   */
  private static String generateLogString(
      boolean failure,
      ChangedNoticesCollector newErrors,
      ChangedNoticesCollector droppedErrors,
      CorruptedSourcesCollector corruptedSources) {
    StringBuilder b = new StringBuilder();
    String status = failure ? "❌ Invalid acceptance test." : "✅ Rule acceptance tests passed.";
    b.append(status).append('\n');
    b.append("New Errors: ").append(newErrors.generateLogString()).append('\n');
    b.append("Dropped Errors: ").append(droppedErrors.generateLogString()).append('\n');
    b.append(corruptedSources.generateLogString()).append("\n");
    b.append(status).append('\n');
    return b.toString();
  }
}
