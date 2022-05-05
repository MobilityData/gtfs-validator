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

import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

/**
 * A Swing-based GUI application for configuring the inputs and configuration options of the
 * validator.
 */
public class GtfsValidatorApp extends JFrame {
  private static final Dimension VERTICAL_GAP = new Dimension(0, 40);

  private static final Font BOLD_FONT = createBoldFont();

  private final JTextField gtfsInputField = new JTextField();
  private final JTextField outputDirectoryField = new JTextField();

  private final JPanel advancedOptionsPanel = new JPanel();

  public GtfsValidatorApp() {
    super("GTFS Schedule Validator");
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
    int rc = chooser.showOpenDialog(this);
    if (rc == JFileChooser.APPROVE_OPTION) {
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
    int rc = chooser.showOpenDialog(this);
    if (rc == JFileChooser.APPROVE_OPTION) {
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

    advancedOptionsPanel.add(new JLabel("Number of threads:"));

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

    JButton validateButton = new JButton("Validate");
    validateButtonPanel.add(validateButton);
  }

  private static Font createBoldFont() {
    JLabel label = new JLabel();
    Font f = label.getFont();
    return f.deriveFont(f.getStyle() | Font.BOLD);
  }

  private JLabel createLabelWithFont(String labelText, Font font) {
    JLabel l = new JLabel(labelText);
    l.setFont(font);
    return l;
  }
}
