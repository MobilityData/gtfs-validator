package org.mobilitydata.gtfsvalidator.outputcomparator.cli;

import com.beust.jcommander.JCommander;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.flogger.FluentLogger;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import org.mobilitydata.gtfsvalidator.outputcomparator.util.ReportUtil;

public class Main {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private static final String REFERENCE_JSON = "reference.json";
  private static final String LATEST_JSON = "latest.json";
  private static final String INTEGRATION_REPORT_JSON = "integration_report.json";

  public static void main(String[] argv) {
    ComparatorArguments args = new ComparatorArguments();
    new JCommander(args).parse(argv);
    File[] outputDirectory = new File(args.getOutputBase()).listFiles();
    if (outputDirectory == null) {
      logger.atSevere().log("Empty output directory");
      return;
    }
    ImmutableMap.Builder<String, Object> mapBuilder = new Builder<>();
    float counter = 0;
    float agencyCount = Arrays.stream(outputDirectory).filter(File::isDirectory).count();

    for (File file : outputDirectory) {
      if (file.isDirectory()) {
        try {
          JsonElement referenceReport =
              JsonParser.parseReader(
                  new JsonReader(
                      new FileReader(
                          Paths.get(file.getPath()).resolve(REFERENCE_JSON).toString())));
          JsonElement latestReport =
              JsonParser.parseReader(
                  new JsonReader(
                      new FileReader(Paths.get(file.getPath()).resolve(LATEST_JSON).toString())));
          if (ReportUtil.areReportsEqual(referenceReport, latestReport)) {
            return;
          }
          Map<String, Integer> referenceReportErrorListing =
              ReportUtil.getErrorEntries(referenceReport);
          Map<String, Integer> latestReportErrorListing = ReportUtil.getErrorEntries(latestReport);
          if (ReportUtil.containSameErrors(referenceReportErrorListing, latestReportErrorListing)) {
            return;
          }
          int newErrorCount =
              ReportUtil.getNewErrorCount(referenceReportErrorListing, latestReportErrorListing);
          mapBuilder.put(file.getName(), newErrorCount);
          if (newErrorCount >= args.getValidityThreshold()) {
            counter += 1;
            }
        } catch (FileNotFoundException e) {
          logger.atSevere().withCause(e).log(e.getMessage());
        }
      }
    }
    try {
      ReportUtil.exportJson(mapBuilder.build(), args.getOutputBase(), INTEGRATION_REPORT_JSON);
      System.out.printf(
          "%.2f %% of datasets are invalid due to new implementation%n",
          100 * counter / agencyCount);
    } catch (IOException ioException) {
      logger.atSevere().withCause(ioException).log("Could not write integration test report");
    }
  }
}
