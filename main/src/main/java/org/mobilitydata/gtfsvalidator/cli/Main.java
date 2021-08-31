/*
 * Copyright 2020-2021 Google LLC, MobilityData IO
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

package org.mobilitydata.gtfsvalidator.cli;

import com.beust.jcommander.JCommander;
import com.google.common.base.Strings;
import com.google.common.flogger.FluentLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;
import org.mobilitydata.gtfsvalidator.input.GtfsInput;
import org.mobilitydata.gtfsvalidator.notice.IOError;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.NoticeSchemaGenerator;
import org.mobilitydata.gtfsvalidator.notice.URISyntaxError;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedLoader;
import org.mobilitydata.gtfsvalidator.validator.DefaultValidatorProvider;
import org.mobilitydata.gtfsvalidator.validator.ValidationContext;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

/** The main entry point for GTFS Validator CLI. */
public class Main {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private static final String GTFS_ZIP_FILENAME = "gtfs.zip";
  private static final String NOTICE_SCHEMA_JSON = "notice_schema.json";

  public static void main(String[] argv) {
    Arguments args = parseArguments(argv);
    if (args == null) {
      System.exit(1);
    }

    ValidatorLoader validatorLoader = null;
    try {
      validatorLoader = new ValidatorLoader();
    } catch (ValidatorLoaderException e) {
      logger.atSevere().withCause(e).log("Cannot load validator classes");
      System.exit(1);
    }
    GtfsFeedLoader feedLoader = new GtfsFeedLoader();

    System.out.println("Country code: " + args.getCountryCode());
    System.out.println("Input: " + args.getInput());
    System.out.println("URL: " + args.getUrl());
    System.out.println("Output: " + args.getOutputBase());
    System.out.println("Path to archive storage directory: " + args.getStorageDirectory());
    System.out.println("Table loaders: " + feedLoader.listTableLoaders());
    System.out.println("Validators:");
    System.out.println(validatorLoader.listValidators());

    final long startNanos = System.nanoTime();
    // Input.
    feedLoader.setNumThreads(args.getNumThreads());
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFeedContainer feedContainer;
    GtfsInput gtfsInput = null;
    try {
      gtfsInput = createGtfsInput(args);
    } catch (IOException e) {
      logger.atSevere().withCause(e).log("Cannot load GTFS feed");
      noticeContainer.addSystemError(new IOError(e));
    } catch (URISyntaxException e) {
      logger.atSevere().withCause(e).log("Syntax error in URI");
      noticeContainer.addSystemError(new URISyntaxError(e));
    }
    if (gtfsInput == null) {
      exportReport(noticeContainer, args);
      System.exit(1);
    }
    ValidationContext validationContext =
        ValidationContext.builder()
            .setCountryCode(
                CountryCode.forStringOrUnknown(
                    args.getCountryCode() == null ? CountryCode.ZZ : args.getCountryCode()))
            .setCurrentDateTime(new CurrentDateTime(ZonedDateTime.now(ZoneId.systemDefault())))
            .build();
    try {
      feedContainer =
          loadAndValidate(
              validatorLoader, feedLoader, noticeContainer, gtfsInput, validationContext);
    } catch (InterruptedException e) {
      logger.atSevere().withCause(e).log("Validation was interrupted");
      System.exit(1);
      return;
    }
    closeGtfsInput(gtfsInput, noticeContainer);

    // Output
    exportReport(noticeContainer, args);
    printSummary(startNanos, feedContainer);
  }

  /**
   * Prints validation metadata.
   *
   * @param startNanos start time as nanoseconds
   * @param feedContainer the {@code GtfsFeedContainer}
   */
  public static void printSummary(long startNanos, GtfsFeedContainer feedContainer) {
    final long endNanos = System.nanoTime();
    if (!feedContainer.isParsedSuccessfully()) {
      System.out.println(" ----------------------------------------- ");
      System.out.println("|       !!!    PARSING FAILED    !!!      |");
      System.out.println("|   Most validators were never invoked.   |");
      System.out.println("|   Please see report.json for details.   |");
      System.out.println(" ----------------------------------------- ");
    }
    System.out.printf("Validation took %.3f seconds%n", (endNanos - startNanos) / 1e9);
    System.out.println(feedContainer.tableTotals());
  }

  /**
   * Closes a {@code GtfsInput}. Yields {@code IOError} if the {@code GtfsInput} could not be
   * closed.
   *
   * @param gtfsInput the {@code GtfsInput} to close
   * @param noticeContainer the {@code NoticeContainer} that will contain the {@code IOError} if the
   *     {@code GtfsInput} could not be closed.
   */
  public static void closeGtfsInput(GtfsInput gtfsInput, NoticeContainer noticeContainer) {
    try {
      gtfsInput.close();
    } catch (IOException e) {
      logger.atSevere().withCause(e).log("Cannot close GTFS input");
      noticeContainer.addSystemError(new IOError(e));
    }
  }

  /**
   * Loads and validates GTFS feeds
   *
   * @param validatorLoader the {@code ValidatorLoader} used in the process
   * @param feedLoader the {@code GtfsFeedLoader} used in the process
   * @param noticeContainer the {@code NoticeContainer} that will contain {@code Notice}s related to
   *     the GTFS feed
   * @param gtfsInput the source of data
   * @param validationContext the {@code ValidationContext} do be used during validation
   * @return the {@code GtfsFeedContainer} used in the validation process
   * @throws InterruptedException if validation process was interrupted
   */
  public static GtfsFeedContainer loadAndValidate(
      ValidatorLoader validatorLoader,
      GtfsFeedLoader feedLoader,
      NoticeContainer noticeContainer,
      GtfsInput gtfsInput,
      ValidationContext validationContext)
      throws InterruptedException {
    GtfsFeedContainer feedContainer;
    feedContainer =
        feedLoader.loadAndValidate(
            gtfsInput,
            new DefaultValidatorProvider(validationContext, validatorLoader),
            noticeContainer);
    return feedContainer;
  }

  /**
   * Performs parsing and sanity checks on CLI arguments.
   *
   * @param argv the CLI arguments
   * @return the {@code Arguments} generated after parsing the command line
   */
  private static Arguments parseArguments(String[] argv) {
    Arguments args = new Arguments();
    JCommander jCommander = new JCommander(args);
    jCommander.parse(argv);
    if (args.getHelp()) {
      jCommander.usage();
      System.out.println(
          "⚠️ Note that parameters marked with an asterisk (*) in the help menu are mandatory.");
      return null;
    }
    if (args.getExportNoticeSchema()) {
      exportNoticeSchema(args);
    }
    if (args.abortAfterNoticeSchemaExport()) {
      return null;
    }
    if (!CliParametersAnalyzer.isValid(args)) {
      return null;
    }
    return args;
  }

  private static Gson createGson(boolean pretty) {
    GsonBuilder builder = new GsonBuilder();
    if (pretty) {
      builder.setPrettyPrinting();
    }
    return builder.create();
  }

  /** Generates and exports reports for both validation notices and system errors reports. */
  public static void exportReport(final NoticeContainer noticeContainer, final Arguments args) {
    new File(args.getOutputBase()).mkdirs();
    Gson gson = createGson(args.getPretty());
    try {
      Files.write(
          Paths.get(args.getOutputBase(), args.getValidationReportName()),
          gson.toJson(noticeContainer.exportValidationNotices()).getBytes(StandardCharsets.UTF_8));
      Files.write(
          Paths.get(args.getOutputBase(), args.getSystemErrorsReportName()),
          gson.toJson(noticeContainer.exportSystemErrors()).getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      logger.atSevere().withCause(e).log("Cannot store report files");
    }
  }

  private static void exportNoticeSchema(final Arguments args) {
    new File(args.getOutputBase()).mkdirs();
    Gson gson = createGson(args.getPretty());
    try {
      Files.write(
          Paths.get(args.getOutputBase(), NOTICE_SCHEMA_JSON),
          gson.toJson(
              NoticeSchemaGenerator.jsonSchemaForPackages(
                  NoticeSchemaGenerator.DEFAULT_NOTICE_PACKAGES))
              .getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      logger.atSevere().withCause(e).log("Cannot store notice schema file");
    }
  }

  /**
   * Creates a {@code GtfsInput}
   *
   * @param args the {@code Argument} to be used to retrieve information needed to the creation of
   *     the {@code GtfsInput}
   * @return the {@code GtfsInput} generated after
   * @throws IOException in case of error while loading a file
   * @throws URISyntaxException in case of error in the {@code URL} syntax
   */
  public static GtfsInput createGtfsInput(Arguments args) throws IOException, URISyntaxException {
    if (args.getInput() == null) {
      if (Strings.isNullOrEmpty(args.getStorageDirectory())) {
        return GtfsInput.createFromUrlInMemory(new URL(args.getUrl()));
      } else {
        return GtfsInput.createFromUrl(
            new URL(args.getUrl()), Paths.get(args.getStorageDirectory(), GTFS_ZIP_FILENAME));
      }
    } else {
      return GtfsInput.createFromPath(Paths.get(args.getInput()));
    }
  }
}
