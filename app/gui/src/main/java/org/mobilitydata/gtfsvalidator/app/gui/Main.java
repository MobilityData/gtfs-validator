package org.mobilitydata.gtfsvalidator.app.gui;

import com.google.common.flogger.FluentLogger;
import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  public static void main(String[] args) {
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
