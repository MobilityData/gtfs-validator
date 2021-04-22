/*
 * Copyright 2020 Google LLC, MobilityData IO
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
import org.mobilitydata.gtfsvalidator.notice.ThreadInterruptedError;
import org.mobilitydata.gtfsvalidator.notice.URISyntaxError;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedLoader;
import org.mobilitydata.gtfsvalidator.validator.ValidationContext;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;

/** The main entry point for GTFS Validator CLI. */
public class Main {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  public static void main(String[] argv) {
    Arguments args = new Arguments();
    CliParametersAnalyzer cliParametersAnalyzer = new CliParametersAnalyzer();
    new JCommander(args).parse(argv);
    if (!cliParametersAnalyzer.isValid(args)) {
      System.exit(1);
    }

    ValidatorLoader validatorLoader = new ValidatorLoader();
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
      if (args.getInput() == null) {
        if (Strings.isNullOrEmpty(args.getStorageDirectory())) {
          gtfsInput = GtfsInput.createFromUrlInMemory(new URL(args.getUrl()));
        } else {
          gtfsInput = GtfsInput.createFromUrl(new URL(args.getUrl()), args.getStorageDirectory());
        }
      } else {
        gtfsInput = GtfsInput.createFromPath(Paths.get(args.getInput()));
      }
    } catch (IOException e) {
      logger.atSevere().withCause(e).log("Cannot load GTFS feed");
      noticeContainer.addSystemError(new IOError(e.getMessage()));
    } catch (URISyntaxException e) {
      logger.atSevere().withCause(e).log("Syntax error in URI");
      noticeContainer.addSystemError(new URISyntaxError(e.getMessage()));
    } catch (InterruptedException e) {
      logger.atSevere().withCause(e).log("Interrupted thread");
      noticeContainer.addSystemError(new ThreadInterruptedError(e.getMessage()));
    }
    if (gtfsInput == null) {
      exportReport(noticeContainer, args);
      return;
    }
    ValidationContext validationContext =
        ValidationContext.builder()
            .setCountryCode(
                CountryCode.forStringOrUnknown(
                    args.getCountryCode() == null ? CountryCode.ZZ : args.getCountryCode()))
            .setNow(ZonedDateTime.now(ZoneId.systemDefault()))
            .build();
    feedContainer =
        feedLoader.loadAndValidate(gtfsInput, validationContext, validatorLoader, noticeContainer);
    try {
      gtfsInput.close();
    } catch (IOException e) {
      logger.atSevere().withCause(e).log("Cannot close GTFS input");
      noticeContainer.addSystemError(new IOError(e.getMessage()));
    }

    // Output
    exportReport(noticeContainer, args);
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

  /** Generates and exports reports for both validation notices and system errors reports. */
  private static void exportReport(final NoticeContainer noticeContainer, final Arguments args) {
    new File(args.getOutputBase()).mkdirs();
    try {
      Files.write(
          Paths.get(args.getOutputBase(), args.getValidationReportName()),
          noticeContainer.exportValidationNotices().getBytes(StandardCharsets.UTF_8));
      Files.write(
          Paths.get(args.getOutputBase(), args.getSystemErrorsReportName()),
          noticeContainer.exportSystemErrors().getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      logger.atSevere().withCause(e).log("Cannot store report files");
    }
  }
}
