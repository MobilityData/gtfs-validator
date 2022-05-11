/*
 * Copyright 2022 Google LLC
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
package org.mobilitydata.gtfsvalidator.app.gui;

import com.google.common.flogger.FluentLogger;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * The main entry point for the GUI application.
 *
 * <p>Compared to the CLI jar, this entry point is designed to be packaged as a native application
 * to be run directly by the user.
 *
 * <p>TODO(#1134): Follow up work will add a minimal UI for selecting the input GTFS and potentially
 * the output directory.
 */
public class Main {
  // We use `~/GtfsValidator` as the default output directory for reports and
  // logs if the user has not specified an explicit directory.  If this name
  // is updated, make sure to update `logging.properties` as well.
  private static final String DEFAULT_OUTPUT_DIRECTORY_NAME = "GtfsValidator";

  static {
    // Attempt to create the default output directory, as we'll use it for
    // the output directory of the file-based logger below.
    File outputDirectory = getDefaultOutputDirectory().toFile();
    if (!outputDirectory.exists()) {
      outputDirectory.mkdir();
    }

    try (InputStream inputStream = Main.class.getResourceAsStream("/logging.properties")) {
      LogManager.getLogManager().readConfiguration(inputStream);
    } catch (IOException e) {
      Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
      Logger.getAnonymousLogger().severe(e.getMessage());
    }
  }

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  public static void main(String[] args) {
    logger.atInfo().log("gtfs-validator: start");

    // On Windows, if you drag a file onto the application shortcut, it will
    // execute the app with the file as the first command-line argument.  This
    // doesn't appear to work on Mac OS.
    if (args.length != 1) {
      logger.atSevere().log("No GTFS input specified - args=%d", args.length);
      System.exit(-1);
    } else {
      run(args[0]);
    }

    logger.atInfo().log("gtfs-validator: exit");
  }

  private static void run(String path) {
    // TODO(#1135): Refactor this code to call GTFS validation code directly
    // instead of constructing artifical command-line args and calling cli.Main.
    Path workingDirectory = getDefaultOutputDirectory();

    List<String> cliArgs = new ArrayList<>();
    cliArgs.add("-i");
    cliArgs.add(path);
    cliArgs.add("-o");
    cliArgs.add(workingDirectory.toString());
    cliArgs.add("--pretty");

    org.mobilitydata.gtfsvalidator.cli.Main.main(cliArgs.toArray(new String[] {}));

    Path reportPath = workingDirectory.resolve("report.json");

    try {
      Desktop.getDesktop().browse(reportPath.toUri());
    } catch (IOException ex) {
      logger.atSevere().withCause(ex).log("Error opening browser");
      System.exit(-1);
    }
  }

  /**
   * Try to return `~/GtfsValidator` as the default output directory for reports and logs if we are
   * able to resolve the user's home directory. We do not attempt to create this directory if it
   * does not exist.
   *
   * <p>If we are not able to resolve the user's home directory, we fall back to a temporary
   * directory.
   */
  private static Path getDefaultOutputDirectory() {
    String path = System.getProperty("user.home");
    if (path != null) {
      return Path.of(path, DEFAULT_OUTPUT_DIRECTORY_NAME);
    }

    // If for some reason the user home directory cannot be resolved, we fall
    // back to a temporary directory.
    Path workingDirectory = null;
    try {
      workingDirectory = Files.createTempDirectory("GtfsValidator");
    } catch (IOException ex) {
      logger.atSevere().withCause(ex).log("Error creating working directory");
      System.exit(-1);
    }
    return workingDirectory;
  }
}
