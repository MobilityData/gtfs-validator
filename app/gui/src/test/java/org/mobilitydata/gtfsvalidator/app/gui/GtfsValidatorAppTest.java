package org.mobilitydata.gtfsvalidator.app.gui;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.mockito.Mockito.verify;

import java.awt.GraphicsEnvironment;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import javax.swing.JButton;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

public class GtfsValidatorAppTest {

  @Rule public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

  @Mock private MonitoredValidationRunner runner;
  @Mock private ValidationDisplay display;

  @Captor private ArgumentCaptor<ValidationRunnerConfig> configCaptor;

  private GtfsValidatorApp app;

  @Before
  public void before() {
    // This test uses Swing and AWT classes that will fail if we are in a headless environment,
    // so we skip the test if that's the case.
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());

    this.app = new GtfsValidatorApp(runner, display);
    app.constructUI();
  }

  @Test
  public void testValidationButton() {
    // Button should be initially disabled.
    JButton button = app.getValidateButtonForTesting();
    assertThat(button.isEnabled()).isFalse();

    app.setGtfsSource("/path/to/gtfs.zip");
    app.setOutputDirectory(Path.of("/path/to/output"));

    assertThat(button.isEnabled()).isTrue();
  }

  @Test
  public void testValidationConfig() throws URISyntaxException {
    app.setGtfsSource("http://transit/gtfs.zip");
    app.setOutputDirectory(Path.of("/path/to/output"));

    app.getValidateButtonForTesting().doClick();

    verify(runner).run(configCaptor.capture(), Mockito.same(app));

    ValidationRunnerConfig config = configCaptor.getValue();
    assertThat(config.gtfsSource()).isEqualTo(new URI("http://transit/gtfs.zip"));
    assertThat(config.outputDirectory()).hasValue(Path.of("/path/to/output"));
    assertThat(config.numThreads()).isEqualTo(1);
    assertThat(config.countryCode().isUnknown()).isTrue();
  }

  @Test
  public void testValidationConfigWithAdvancedOptions() throws URISyntaxException {
    app.setGtfsSource("http://transit/gtfs.zip");
    app.setOutputDirectory(Path.of("/path/to/output"));
    app.setNumThreads(5);
    app.setCountryCode("US");

    app.getValidateButtonForTesting().doClick();

    verify(runner).run(configCaptor.capture(), Mockito.same(app));

    ValidationRunnerConfig config = configCaptor.getValue();
    assertThat(config.gtfsSource()).isEqualTo(new URI("http://transit/gtfs.zip"));
    assertThat(config.outputDirectory()).hasValue(Path.of("/path/to/output"));
    assertThat(config.numThreads()).isEqualTo(5);
    assertThat(config.countryCode().getCountryCode()).isEqualTo("US");
  }

  @Test
  public void testPreValidationCallback() throws URISyntaxException {
    app.setGtfsSource("http://transit/gtfs.zip");
    app.setOutputDirectory(Path.of("/path/to/output"));

    Runnable callback = Mockito.mock(Runnable.class);
    app.addPreValidationCallback(callback);

    app.getValidateButtonForTesting().doClick();

    verify(callback).run();
  }
}
