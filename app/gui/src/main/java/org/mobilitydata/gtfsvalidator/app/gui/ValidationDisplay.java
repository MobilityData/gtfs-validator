package org.mobilitydata.gtfsvalidator.app.gui;

import com.google.common.flogger.FluentLogger;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import javax.swing.JOptionPane;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunner;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;

/** Provides methods to display the results of validation to the user. */
class ValidationDisplay {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  void handleResult(ValidationRunnerConfig config, ValidationRunner.Status status) {
    if (status == ValidationRunner.Status.EXCEPTION) {
      handleError();
    }

    Path outputDirectory = config.outputDirectory();
    Path reportPath = outputDirectory.resolve(config.validationReportFileName());
    if (status == ValidationRunner.Status.SYSTEM_ERRORS) {
      reportPath = outputDirectory.resolve(config.systemErrorsReportFileName());
    }

    try {
      Desktop.getDesktop().browse(reportPath.toUri());
    } catch (IOException ex) {
      logger.atSevere().withCause(ex).log("Error opening browser");
      System.exit(-1);
    }
  }

  void handleError(Throwable ex) {
    logger.atSevere().withCause(ex).log("Error running validation");
    handleError();
  }

  void handleError() {
    JOptionPane.showMessageDialog(
        null,
        "A non-recoverable error occurred during validation.",
        "ERROR",
        JOptionPane.ERROR_MESSAGE);
    System.exit(-1);
  }
}
