package org.mobilitydata.gtfsvalidator.app.gui;

import com.google.common.flogger.FluentLogger;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunner;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;

/**
 * Effectively a wrapper around {@link ValidationRunner} that provides support for running
 * validation in a separate thread (aka don't block the UI thread) and showing a progress dialog
 * during execution.
 */
class MonitoredValidationRunner {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  // We run validation on separate thread so as not to block the UI thread.
  private final Executor executor = Executors.newSingleThreadExecutor();

  private final ValidationRunner runner;
  private final ValidationDisplay display;
  private final ResourceBundle bundle;

  MonitoredValidationRunner(ValidationRunner runner, ValidationDisplay display) {
    this.runner = runner;
    this.display = display;
    this.bundle = ResourceBundle.getBundle(MonitoredValidationRunner.class.getName());
  }

  void run(ValidationRunnerConfig config, JFrame parentFrame) {
    // We disable the primary UI while validation is running.
    parentFrame.setEnabled(false);

    JDialog progressDialog = createProgressDialog(parentFrame);
    progressDialog.setVisible(true);

    executor.execute(
        () -> {
          try {
            ValidationRunner.Status status = runner.run(config);

            progressDialog.setVisible(false);
            parentFrame.setEnabled(true);

            display.handleResult(config, status);

          } catch (Throwable ex) {
            // Make sure the dialog is out of the way before we display an error
            // dialog.
            progressDialog.setVisible(false);
            parentFrame.setEnabled(true);

            display.handleError(ex);
          }
        });
  }

  private JDialog createProgressDialog(JFrame parent) {
    JDialog dialog = new JDialog(parent, bundle.getString("validation"));

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    dialog.add(panel);

    JLabel label = new JLabel(bundle.getString("validation_is_running"));
    label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    panel.add(label);

    dialog.pack();
    dialog.setLocationRelativeTo(parent);
    return dialog;
  }
}
