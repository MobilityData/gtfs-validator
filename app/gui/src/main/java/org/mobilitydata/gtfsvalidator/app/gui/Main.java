package org.mobilitydata.gtfsvalidator.app.gui;

import com.google.common.flogger.FluentLogger;
import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * The main entry point for the GUI application.
 *
 * Compared to the CLI jar, this entry point is designed to be packaged as a
 * native application to be run directly by the user.
 *
 * TODO(bdferris): Follow up work will add a minimal UI for selecting the input
 * GTFS and potentially the output directory.
 */
public class Main {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  public static void main(String[] args) {
    // On Windows, if you drag a file onto the application shortcut, it will
    // execute the app with the file as the first command-line argument.  This
    // doesn't appear to work on Mac OS.
    if (args.length != 1) {
      System.exit(-1);
    }

    Path workingDirectory = null;
    try {
      workingDirectory = Files.createTempDirectory("GtfsValidator");
    } catch (IOException ex) {
      logger.atSevere().withCause(ex).log("Error creating working directory");
      System.exit(-1);
    }

    List<String> cliArgs = new ArrayList<>();
    cliArgs.add("-i");
    cliArgs.add(args[0]);
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
}
