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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;

/**
 * A Swing-based GUI application for configuring the inputs and configuration options of the
 * validator.
 */
public class GtfsValidatorApp extends JFrame {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private static final Dimension VERTICAL_GAP = new Dimension(0, 40);

  private static final Font BOLD_FONT = createBoldFont();

  private final JTextField gtfsInputField = new JTextField();
  private final JTextField outputDirectoryField = new JTextField();

  private final JButton validateButton = new JButton();

  private final JPanel advancedOptionsPanel = new JPanel();

  private final JSpinner numThreadsSpinner = new JSpinner();
  private final JTextField countryCodeField = new JTextField("", 3);

  private final MonitoredValidationRunner validationRunner;
  private final ValidationDisplay validationDisplay;

  public GtfsValidatorApp(
      MonitoredValidationRunner validationRunner, ValidationDisplay validationDisplay) {
    super("GTFS Schedule Validator");
    this.validationRunner = validationRunner;
    this.validationDisplay = validationDisplay;
  }

  public void setGtfsSource(String source) {
    gtfsInputField.setText(source);
  }

  public void setOutputDirectory(Path outputDirectory) {
    outputDirectoryField.setText(outputDirectory.toString());
  }

  public void setNumThreads(int numThreads) {
    numThreadsSpinner.setValue(numThreads);
  }

  public void setCountryCode(String countryCode) {
    countryCodeField.setText(countryCode);
  }

  void constructUI() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    getContentPane().add(panel);

    constructGtfsInputSection(panel);
    panel.add(Box.createRigidArea(VERTICAL_GAP));
    constructOutputDirectorySection(panel);
    panel.add(Box.createRigidArea(VERTICAL_GAP));
    constructAdvancedOptionsPanel(panel);
    constructValidateButton(panel);

    // Ensure everything is left-aligned in the main application panel.
    for (Component c : panel.getComponents()) {
      JComponent j = (JComponent) c;
      j.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
  }

  private void constructGtfsInputSection(JPanel parent) {
    // GTFS Input Section
    parent.add(createLabelWithFont("GTFS Input:", BOLD_FONT));

    gtfsInputField
        .getDocument()
        .addDocumentListener(documentChangeListener(this::updateValidationButtonStatus));
    parent.add(gtfsInputField);

    JButton chooseGtfsInputButton = new JButton("Choose Local File...");
    parent.add(chooseGtfsInputButton);
    chooseGtfsInputButton.addActionListener(
        (e) -> {
          showGtfsInputFileChooser();
        });
    parent.add(new JLabel("You can select a ZIP file, a directory, or a URL."));
  }

  private void showGtfsInputFileChooser() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    chooser.setFileFilter(new GtfsZipsAndDirectoriesFileFilter());
    int result = chooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      File selectedFile = chooser.getSelectedFile();
      gtfsInputField.setText(selectedFile.toString());
    }
  }

  private static class GtfsZipsAndDirectoriesFileFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
      return f.isDirectory() || f.getName().endsWith(".zip");
    }

    @Override
    public String getDescription() {
      return "GTFS ZIPs and Directories";
    }
  }

  private void constructOutputDirectorySection(JPanel parent) {
    // Output Directory Section
    parent.add(createLabelWithFont("Output Directory:", BOLD_FONT));

    outputDirectoryField
        .getDocument()
        .addDocumentListener(documentChangeListener(this::updateValidationButtonStatus));
    parent.add(outputDirectoryField);

    JButton chooseOutputDirectoryButton = new JButton("Choose Output Directory...");
    chooseOutputDirectoryButton.addActionListener(
        (e) -> {
          showOutputDirectoryChooser();
        });
    parent.add(chooseOutputDirectoryButton);
    parent.add(new JLabel("The validation report will be written here."));
  }

  private void showOutputDirectoryChooser() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int result = chooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      File selectedFile = chooser.getSelectedFile();
      outputDirectoryField.setText(selectedFile.toString());
    }
  }

  private void constructAdvancedOptionsPanel(JPanel parent) {
    advancedOptionsPanel.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 0, 20, 0),
            BorderFactory.createTitledBorder("Advanced Options")));
    parent.add(advancedOptionsPanel);

    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    advancedOptionsPanel.add(panel);

    GridBagConstraints labelConstraints = new GridBagConstraints();
    labelConstraints.gridx = 0;
    labelConstraints.anchor = GridBagConstraints.NORTHWEST;
    labelConstraints.ipadx = 10;

    GridBagConstraints fieldConstraints = new GridBagConstraints();
    fieldConstraints.gridx = 1;
    fieldConstraints.anchor = GridBagConstraints.NORTHEAST;

    labelConstraints.gridy = 0;
    panel.add(new JLabel("Number of threads:"), labelConstraints);

    fieldConstraints.gridy = 0;
    panel.add(numThreadsSpinner, fieldConstraints);
    numThreadsSpinner.setValue(1);

    labelConstraints.gridy = 1;
    panel.add(new JLabel("Country Code:"), labelConstraints);

    fieldConstraints.gridy = 1;
    panel.add(countryCodeField, fieldConstraints);

    advancedOptionsPanel.setVisible(false);
  }

  private void constructValidateButton(JPanel panel) {
    JPanel validateButtonPanel = new JPanel();
    validateButtonPanel.setLayout(new BoxLayout(validateButtonPanel, BoxLayout.LINE_AXIS));
    panel.add(validateButtonPanel);

    JButton advancedButton = new JButton("Advanced");
    advancedButton.addActionListener(
        (e) -> {
          advancedOptionsPanel.setVisible(!advancedOptionsPanel.isVisible());
          pack();
        });
    validateButtonPanel.add(advancedButton);

    validateButtonPanel.add(Box.createHorizontalGlue());

    validateButton.setText("Validate");
    updateValidationButtonStatus();
    validateButton.addActionListener((e) -> runValidation());
    validateButtonPanel.add(validateButton);
  }

  private void updateValidationButtonStatus() {
    validateButton.setEnabled(isValidationReadyToRun());
  }

  private boolean isValidationReadyToRun() {
    if (gtfsInputField.getText().isBlank()) {
      return false;
    }
    if (outputDirectoryField.getText().isBlank()) {
      return false;
    }
    return true;
  }

  private void runValidation() {
    try {
      ValidationRunnerConfig config = buildConfig();
      validationRunner.run(config, this);
    } catch (Throwable ex) {
      validationDisplay.handleError(ex);
    }
  }

  private ValidationRunnerConfig buildConfig() throws URISyntaxException {
    ValidationRunnerConfig.Builder config = ValidationRunnerConfig.builder();
    config.setPrettyJson(true);

    String gtfsInput = gtfsInputField.getText();
    if (gtfsInput.isBlank()) {
      throw new IllegalStateException("gtfsInputField is blank");
    }
    if (gtfsInput.startsWith("http")) {
      config.setGtfsSource(new URI(gtfsInput));
    } else {
      config.setGtfsSource(Path.of(gtfsInput).toUri());
    }

    String outputDirectory = outputDirectoryField.getText();
    if (outputDirectory.isBlank()) {
      throw new IllegalStateException("outputDirectoryField is blank");
    }
    config.setOutputDirectory(Path.of(outputDirectory));

    Object numThreads = numThreadsSpinner.getValue();
    if (numThreads instanceof Integer) {
      config.setNumThreads((Integer) numThreads);
    }

    String countryCode = countryCodeField.getText();
    if (!countryCode.isBlank()) {
      config.setCountryCode(CountryCode.forStringOrUnknown(countryCode));
    }

    return config.build();
  }

  private static Font createBoldFont() {
    JLabel label = new JLabel();
    Font baseFont = label.getFont();
    return baseFont.deriveFont(baseFont.getStyle() | Font.BOLD);
  }

  private JLabel createLabelWithFont(String labelText, Font font) {
    JLabel label = new JLabel(labelText);
    label.setFont(font);
    return label;
  }

  JButton getValidateButtonForTesting() {
    return validateButton;
  }

  private DocumentListener documentChangeListener(Runnable action) {
    return new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        action.run();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        action.run();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        action.run();
      }
    };
  }
}
