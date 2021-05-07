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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.mobilitydata.gtfsvalidator.outputcomparator.util.ValidationReport;

public class Main {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private static final String REFERENCE_JSON = "report.json";
  private static final String LATEST_JSON = "latest.json";
  private static final String INTEGRATION_REPORT_JSON = "integration_report.json";

  public static void main(String[] argv) throws IOException {
    ComparatorArguments args = new ComparatorArguments();
    new JCommander(args).parse(argv);
    File[] outputDirectory = new File(args.getOutputBase()).listFiles();
    if (outputDirectory == null) {
      logger.atSevere().log("Specified output is not a directory");
      return;
    }
    if (outputDirectory.length == 0) {
      logger.atSevere().log(
          "Specified directory is empty, cannot generate integration tests report.");
      return;
    }
    ImmutableMap.Builder<String, Object> mapBuilder = new Builder<>();
    int badDatasetCount = 0;
    double totalDatasetCount = Arrays.stream(outputDirectory).filter(File::isDirectory).count();

    for (File file : outputDirectory) {
      if (!file.isDirectory()) {
        continue;
      }
      try (BufferedReader referenceReportReader =
              Files.newBufferedReader(Paths.get(file.getPath()).resolve(REFERENCE_JSON));
          BufferedReader latestReportReader =
              Files.newBufferedReader(Paths.get(file.getPath()).resolve(LATEST_JSON))) {
        ValidationReport referenceReport = ValidationReport.fromReader(referenceReportReader);
        ValidationReport latestReport = ValidationReport.fromReader(latestReportReader);
        if (referenceReport.equals(latestReport)) {
          continue;
        }
        if (referenceReport.hasSameErrorCodes(latestReport)) {
          continue;
        }
        int newErrorCount = referenceReport.getNewErrorCount(latestReport);
        mapBuilder.put(file.getName(), newErrorCount);
        if (newErrorCount >= args.getValidityThreshold()) {
          badDatasetCount += 1;
        }
      } catch (FileNotFoundException e) {
        logger.atSevere().withCause(e).log(String.format("No file found at %s.", file.getPath()));
        System.exit(1);
      }
    }
    ValidationReport.exportIntegrationReportAsJson(
        mapBuilder.build(), args.getOutputBase(), INTEGRATION_REPORT_JSON);
    System.out.printf(
        "%.2f %% of datasets are invalid due to new implementation%n",
        100 * badDatasetCount / totalDatasetCount);
  }
}
