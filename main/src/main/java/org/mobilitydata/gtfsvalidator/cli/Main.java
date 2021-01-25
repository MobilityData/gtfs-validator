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
import org.mobilitydata.gtfsvalidator.input.GtfsFeedName;
import org.mobilitydata.gtfsvalidator.input.GtfsInput;
import org.mobilitydata.gtfsvalidator.notice.IOError;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ThreadInterruptedError;
import org.mobilitydata.gtfsvalidator.notice.URISyntaxError;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedLoader;
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

    GtfsFeedName feedName = GtfsFeedName.parseString(args.getFeedName());
    System.out.println("Feed name: " + feedName.getCountryFirstName());
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
      logger.atSevere().withCause(e).log("Syntax error in URL");
      noticeContainer.addSystemError(new URISyntaxError(e.getMessage()));
    } catch (InterruptedException e) {
      logger.atSevere().withCause(e).log("Interrupted thread");
      noticeContainer.addSystemError(new ThreadInterruptedError(e.getMessage()));
    }
    if (gtfsInput == null) {
      exportReport(args.getOutputBase(), noticeContainer);
      return;
    }
    feedContainer =
        feedLoader.loadAndValidate(gtfsInput, feedName, validatorLoader, noticeContainer);

    // Output
    exportReport(args.getOutputBase(), noticeContainer);
    final long endNanos = System.nanoTime();
    System.out.printf("Validation took %.3f seconds%n", (endNanos - startNanos) / 1e9);
    System.out.println(feedContainer.tableTotals());
  }

  /** Generates and exports reports for both validation notices and system errors reports. */
  private static void exportReport(final String outputBase, final NoticeContainer noticeContainer) {
    new File(outputBase).mkdirs();
    try {
      Files.write(
          Paths.get(outputBase, "report.json"),
          noticeContainer.exportValidationNotices().getBytes(StandardCharsets.UTF_8));
      Files.write(
          Paths.get(outputBase, "system_errors.json"),
          noticeContainer.exportSystemErrors().getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      logger.atSevere().withCause(e).log("Cannot store report files");
    }
  }
}
