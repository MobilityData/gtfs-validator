package org.mobilitydata.gtfsvalidator.app.gui;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableMap;
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
    assertThat(config.outputDirectory()).isEqualTo(Path.of("/path/to/output"));
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
    assertThat(config.outputDirectory()).isEqualTo(Path.of("/path/to/output"));
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

  @Test
  public void testHttpHeadersPassedToConfig() throws URISyntaxException {
    app.setGtfsSource("http://transit/gtfs.zip");
    app.setOutputDirectory(Path.of("/path/to/output"));
    app.setHttpHeaders("Authorization: Bearer token123\nUser-Agent: my-app/2.0");

    app.getValidateButtonForTesting().doClick();

    verify(runner).run(configCaptor.capture(), Mockito.same(app));

    ValidationRunnerConfig config = configCaptor.getValue();
    assertThat(config.httpHeaders())
        .isEqualTo(
            ImmutableMap.of(
                "Authorization", "Bearer token123",
                "User-Agent", "my-app/2.0"));
  }

  @Test
  public void testParseHttpHeaders_empty() {
    assertThat(GtfsValidatorApp.parseHttpHeaders("")).isEmpty();
    assertThat(GtfsValidatorApp.parseHttpHeaders("   \n  \n")).isEmpty();
  }

  @Test
  public void testParseHttpHeaders_duplicateKeyKeepsLast() {
    ImmutableMap<String, String> result =
        GtfsValidatorApp.parseHttpHeaders(
            "Authorization: Bearer first\nAuthorization: Bearer second");
    assertThat(result).isEqualTo(ImmutableMap.of("Authorization", "Bearer second"));
  }

  @Test
  public void testParseHttpHeaders_valueContainsColon() {
    ImmutableMap<String, String> result =
        GtfsValidatorApp.parseHttpHeaders("X-Endpoint: http://trace.example.com/id");
    assertThat(result).isEqualTo(ImmutableMap.of("X-Endpoint", "http://trace.example.com/id"));
  }

  @Test
  public void testNoHttpHeadersGivesEmptyMap() throws URISyntaxException {
    app.setGtfsSource("http://transit/gtfs.zip");
    app.setOutputDirectory(Path.of("/path/to/output"));

    app.getValidateButtonForTesting().doClick();

    verify(runner).run(configCaptor.capture(), Mockito.same(app));
    assertThat(configCaptor.getValue().httpHeaders()).isEmpty();
  }

  // --- Validation tests ---

  @Test
  public void testValidateHttpHeadersText_validHeaders() {
    assertThat(GtfsValidatorApp.validateHttpHeadersText("")).isNull();
    assertThat(GtfsValidatorApp.validateHttpHeadersText("   \n  ")).isNull();
    assertThat(GtfsValidatorApp.validateHttpHeadersText("Authorization: Bearer token")).isNull();
    assertThat(
            GtfsValidatorApp.validateHttpHeadersText(
                "Authorization: Bearer token\nUser-Agent: app/1.0"))
        .isNull();
  }

  @Test
  public void testValidateHttpHeadersText_missingColon() {
    // Value-only line (the bug that was reported)
    String error =
        GtfsValidatorApp.validateHttpHeadersText(
            "Basic YXBpQG1vYmlsaXR5ZGF0YS5vcmc6cWN2M3RoaypOWk00cGFmX3V5YQ==");
    assertThat(error).isNotNull();
    assertThat(error).contains("Basic YXBpQG1vYmlsaXR5ZGF0YS5vcmc6cWN2M3RoaypOWk00cGFmX3V5YQ==");
  }

  @Test
  public void testValidateHttpHeadersText_colonAtStart() {
    // Colon at position 0 → name is empty → invalid
    String error = GtfsValidatorApp.validateHttpHeadersText(": value");
    assertThat(error).isNotNull();
  }

  @Test
  public void testInvalidHeaderDisablesValidateButton() {
    app.setGtfsSource("http://transit/gtfs.zip");
    app.setOutputDirectory(Path.of("/path/to/output"));
    // Valid so far — button should be enabled
    assertThat(app.getValidateButtonForTesting().isEnabled()).isTrue();

    // Set an invalid header — button should become disabled
    app.setHttpHeaders("Basic YXBpQG1vYmlsaXR5ZGF0YS5vcmc6cWN2M3RoaypOWk00cGFmX3V5YQ==");
    assertThat(app.getValidateButtonForTesting().isEnabled()).isFalse();

    // Fix the header — button re-enabled
    app.setHttpHeaders(
        "Authorization: Basic YXBpQG1vYmlsaXR5ZGF0YS5vcmc6cWN2M3RoaypOWk00cGFmX3V5YQ==");
    assertThat(app.getValidateButtonForTesting().isEnabled()).isTrue();
  }
}
