/*
 * Copyright 2020-2022 Google LLC, MobilityData IO
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

package org.mobilitydata.gtfsvalidator.runner;

import com.google.common.flogger.FluentLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;
import org.mobilitydata.gtfsvalidator.input.GtfsInput;
import org.mobilitydata.gtfsvalidator.notice.IOError;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.URISyntaxError;
import org.mobilitydata.gtfsvalidator.report.HtmlReportGenerator;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedLoader;
import org.mobilitydata.gtfsvalidator.util.VersionInfo;
import org.mobilitydata.gtfsvalidator.util.VersionResolver;
import org.mobilitydata.gtfsvalidator.validator.ClassGraphDiscovery;
import org.mobilitydata.gtfsvalidator.validator.DefaultValidatorProvider;
import org.mobilitydata.gtfsvalidator.validator.ValidationContext;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

/** The main entry point for running the validator against a GTFS input. */
public class ValidationRunner {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private static final String GTFS_ZIP_FILENAME = "gtfs.zip";

  private final VersionResolver versionResolver;

  public enum Status {
    // Indicates validation successfully completed, but doesn't imply the
    // feed itself is valid.
    SUCCESS,
    // Indicates validation did not successfully complete, with exceptions
    // caught and written to the system errors JSON output.
    SYSTEM_ERRORS,
    // Indicates validation did not successfully complete, with exceptions
    // caught and written only to console logging.
    EXCEPTION
  }

  public ValidationRunner(VersionResolver versionResolver) {
    this.versionResolver = versionResolver;
  }

  public Status run(ValidationRunnerConfig config) {
    VersionInfo versionInfo = versionResolver.getVersionInfoWithTimeout(Duration.ofSeconds(5));
    logger.atInfo().log("VersionInfo: %s", versionInfo);
    if (versionInfo.updateAvailable()) {
      logger.atInfo().log("A new version of the validator is available!");
    }

    ValidatorLoader validatorLoader = null;
    try {
      validatorLoader =
          ValidatorLoader.createForClasses(
              ClassGraphDiscovery.discoverValidatorsInDefaultPackage());
    } catch (ValidatorLoaderException e) {
      logger.atSevere().withCause(e).log("Cannot load validator classes");
      return Status.EXCEPTION;
    }
    GtfsFeedLoader feedLoader = new GtfsFeedLoader(ClassGraphDiscovery.discoverTables());

    logger.atInfo().log("validation config:\n%s", config);
    logger.atInfo().log("validators:\n%s", validatorLoader.listValidators());

    final long startNanos = System.nanoTime();
    // Input.
    feedLoader.setNumThreads(config.numThreads());
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFeedContainer feedContainer;
    GtfsInput gtfsInput = null;
    try {
      gtfsInput = createGtfsInput(config);
    } catch (IOException e) {
      logger.atSevere().withCause(e).log("Cannot load GTFS feed");
      noticeContainer.addSystemError(new IOError(e));
    } catch (URISyntaxException e) {
      logger.atSevere().withCause(e).log("Syntax error in URI");
      noticeContainer.addSystemError(new URISyntaxError(e));
    }
    if (gtfsInput == null) {
      exportReport(noticeContainer, config, versionInfo);
      if (!noticeContainer.getSystemErrors().isEmpty()) {
        return Status.SYSTEM_ERRORS;
      } else {
        return Status.EXCEPTION;
      }
    }
    ValidationContext validationContext =
        ValidationContext.builder()
            .setCountryCode(config.countryCode())
            .setCurrentDateTime(new CurrentDateTime(ZonedDateTime.now(ZoneId.systemDefault())))
            .build();
    try {
      feedContainer =
          loadAndValidate(
              validatorLoader, feedLoader, noticeContainer, gtfsInput, validationContext);
    } catch (InterruptedException e) {
      logger.atSevere().withCause(e).log("Validation was interrupted");
      return Status.EXCEPTION;
    }
    closeGtfsInput(gtfsInput, noticeContainer);

    // Output
    exportReport(noticeContainer, config, versionInfo);
    printSummary(startNanos, feedContainer);
    return Status.SUCCESS;
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
      StringBuilder b = new StringBuilder();
      b.append(" ----------------------------------------- \n");
      b.append("|       !!!    PARSING FAILED    !!!      |\n");
      b.append("|   Most validators were never invoked.   |\n");
      b.append("|   Please see report.json for details.   |\n");
      b.append(" ----------------------------------------- \n");
      logger.atSevere().log(b.toString());
    }
    logger.atInfo().log("Validation took %.3f seconds%n", (endNanos - startNanos) / 1e9);
    logger.atInfo().log(feedContainer.tableTotals());
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

  private static Gson createGson(boolean pretty) {
    GsonBuilder builder = new GsonBuilder();
    if (pretty) {
      builder.setPrettyPrinting();
    }
    return builder.create();
  }

  /** Generates and exports reports for both validation notices and system errors reports. */
  public static void exportReport(
      NoticeContainer noticeContainer, ValidationRunnerConfig config, VersionInfo versionInfo) {
    if (!Files.exists(config.outputDirectory())) {
      try {
        Files.createDirectories(config.outputDirectory());
      } catch (IOException ex) {
        logger.atSevere().withCause(ex).log(
            "Error creating output directory: %s", config.outputDirectory());
      }
    }
    Gson gson = createGson(config.prettyJson());
    HtmlReportGenerator generator = new HtmlReportGenerator();
    try {
      Files.write(
          config.outputDirectory().resolve(config.validationReportFileName()),
          gson.toJson(noticeContainer.exportValidationNotices()).getBytes(StandardCharsets.UTF_8));
      generator.generateReport(
          noticeContainer,
          config,
          versionInfo,
          config.outputDirectory().resolve(config.htmlReportFileName()));
      Files.write(
          config.outputDirectory().resolve(config.systemErrorsReportFileName()),
          gson.toJson(noticeContainer.exportSystemErrors()).getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      logger.atSevere().withCause(e).log("Cannot store report files");
    }
  }

  /**
   * Creates a {@code GtfsInput}
   *
   * @param config used to retrieve information needed to the creation of the {@code GtfsInput}
   * @return the {@code GtfsInput} generated after
   * @throws IOException in case of error while loading a file
   * @throws URISyntaxException in case of error in the {@code URL} syntax
   */
  public static GtfsInput createGtfsInput(ValidationRunnerConfig config)
      throws IOException, URISyntaxException {
    URI source = config.gtfsSource();
    if (source.getScheme().equals("file")) {
      return GtfsInput.createFromPath(Paths.get(source));
    }

    if (config.storageDirectory().isEmpty()) {
      return GtfsInput.createFromUrlInMemory(source.toURL());
    } else {
      return GtfsInput.createFromUrl(
          source.toURL(), config.storageDirectory().get().resolve(GTFS_ZIP_FILENAME));
    }
  }
}
