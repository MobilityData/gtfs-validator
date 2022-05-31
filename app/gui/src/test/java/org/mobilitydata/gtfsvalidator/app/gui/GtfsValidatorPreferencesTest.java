package org.mobilitydata.gtfsvalidator.app.gui;

import static com.google.common.truth.Truth.assertThat;

import java.nio.file.Path;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

@RunWith(JUnit4.class)
public class GtfsValidatorPreferencesTest {

  @Rule public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

  @Mock private MonitoredValidationRunner runner;
  @Mock private ValidationDisplay display;

  @Test
  public void testEndToEnd() {
    {
      GtfsValidatorApp source = new GtfsValidatorApp(runner, display);
      source.setGtfsSource("http://gtfs.org/gtfs.zip");
      source.setOutputDirectory(Path.of("/tmp/gtfs"));
      source.setNumThreads(3);
      source.setCountryCode("CA");

      GtfsValidatorPreferences prefs = new GtfsValidatorPreferences();
      prefs.savePreferences(source);
    }

    {
      GtfsValidatorPreferences prefs = new GtfsValidatorPreferences();
      GtfsValidatorApp dest = new GtfsValidatorApp(runner, display);
      prefs.loadPreferences(dest);

      assertThat(dest.getGtfsSource()).isEqualTo("http://gtfs.org/gtfs.zip");
      assertThat(dest.getOutputDirectory()).isEqualTo("/tmp/gtfs");
      assertThat(dest.getNumThreads()).isEqualTo(3);
      assertThat(dest.getCountryCode()).isEqualTo("CA");
    }
  }
}
