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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunner;
import org.mobilitydata.gtfsvalidator.util.VersionResolver;

/**
 * The main entry point for the GUI application.
 *
 * <p>Compared to the CLI jar, this entry point is designed to be packaged as a native application
 * to be run directly by the user.
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
    Thread.setDefaultUncaughtExceptionHandler(new LogUncaughtExceptionHandler());

    logger.atInfo().log("gtfs-validator: start");
    SwingUtilities.invokeLater(() -> createAndShowGUI(args));
    logger.atInfo().log("gtfs-validator: exit");
  }

  private static void createAndShowGUI(String[] args) {
    Thread.currentThread().setUncaughtExceptionHandler(new LogUncaughtExceptionHandler());

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      logger.atSevere().withCause(e).log("Error setting system look-and-feel");
    }

    VersionResolver resolver = new VersionResolver();
    ValidationDisplay display = new ValidationDisplay();
    MonitoredValidationRunner runner =
        new MonitoredValidationRunner(new ValidationRunner(resolver), display);
    GtfsValidatorApp app = new GtfsValidatorApp(runner, display);
    app.constructUI();

    GtfsValidatorPreferences prefs = new GtfsValidatorPreferences();
    prefs.loadPreferences(app);
    // We save preferences each time validation is run.
    app.addPreValidationCallback(
        () -> {
          prefs.savePreferences(app);
        });

    // Check to see if there is a new version of the app available.
    resolver.addCallback(
        (versionInfo) -> {
          if (versionInfo.updateAvailable()) {
            SwingUtilities.invokeLater(() -> app.showNewVersionAvailable());
          }
        });

    // On Windows, if you drag a file onto the application shortcut, it will
    // execute the app with the file as the first command-line argument.  This
    // doesn't appear to work on Mac OS.
    if (args.length == 1) {
      app.setGtfsSource(args[0]);
    }

    // We set a default output directory as a fallback if the user didn't
    // have one previously set.
    if (app.getOutputDirectory().isBlank()) {
      app.setOutputDirectory(getDefaultOutputDirectory());
    }

    app.pack();
    app.setMinimumSize(app.getSize());

    // This causes the application window to center in the screen.
    app.setLocationRelativeTo(null);
    app.setVisible(true);
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

  /**
   * We introduce a catch-all for uncaught exceptions to make sure they make it into our application
   * logs.
   */
  public static class LogUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    public void uncaughtException(Thread thread, Throwable thrown) {
      logger.atSevere().withCause(thrown).log("Uncaught application exception");
    }
  }
}
