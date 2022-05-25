package org.mobilitydata.gtfsvalidator.app.gui;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.prefs.Preferences;

/**
 * Supports loading and saving application preferences from a persistent {@link Preferences} backend
 * across application runs.
 */
public class GtfsValidatorPreferences {

  private static final String KEY_GTFS_SOURCE = "gtfs_source";
  private static final String KEY_OUTPUT_DIRECTORY = "output_directory";
  private static final String KEY_NUM_THREADS = "num_threads";
  private static final String KEY_COUNTRY_CODE = "country_code";

  private final Preferences prefs;

  public GtfsValidatorPreferences() {
    this(Preferences.userNodeForPackage(GtfsValidatorPreferences.class));
  }

  GtfsValidatorPreferences(Preferences prefs) {
    this.prefs = prefs;
  }

  public void loadPreferences(GtfsValidatorApp app) {
    loadStringSetting(KEY_GTFS_SOURCE, app::setGtfsSource);
    loadPathSetting(KEY_OUTPUT_DIRECTORY, app::setOutputDirectory);
    loadIntSetting(KEY_NUM_THREADS, app::setNumThreads);
    loadStringSetting(KEY_COUNTRY_CODE, app::setCountryCode);
  }

  public void savePreferences(GtfsValidatorApp app) {
    saveStringSetting(app::getGtfsSource, KEY_GTFS_SOURCE);
    saveStringSetting(app::getOutputDirectory, KEY_OUTPUT_DIRECTORY);
    saveIntSetting(app::getNumThreads, KEY_NUM_THREADS);
    saveStringSetting(app::getCountryCode, KEY_COUNTRY_CODE);
  }

  private void loadStringSetting(String key, Consumer<String> setter) {
    String value = prefs.get(key, "");
    if (!value.isBlank()) {
      setter.accept(value);
    }
  }

  private void loadIntSetting(String key, Consumer<Integer> setter) {
    loadStringSetting(key, (value) -> setter.accept(Integer.parseInt(value)));
  }

  private void loadPathSetting(String key, Consumer<Path> setter) {
    loadStringSetting(key, (value) -> setter.accept(Path.of(value)));
  }

  private void saveStringSetting(Supplier<String> getter, String key) {
    String value = getter.get();
    if (!value.isBlank()) {
      prefs.put(key, value);
    }
  }

  private void saveIntSetting(Supplier<Integer> getter, String key) {
    saveStringSetting(
        () -> {
          Integer value = getter.get();
          if (value == null || value == 0) {
            return "";
          }
          return value.toString();
        },
        key);
  }
}
