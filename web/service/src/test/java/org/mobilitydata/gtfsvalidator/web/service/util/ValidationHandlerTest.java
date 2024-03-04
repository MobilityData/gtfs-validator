package org.mobilitydata.gtfsvalidator.web.service.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunner;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ActiveProfiles("test")
@SpringBootTest
@ContextConfiguration
public class ValidationHandlerTest {
  @MockBean private ValidationRunner runner;
  @Captor ArgumentCaptor<ValidationRunnerConfig> configCaptor;

  @Test
  public void testValidationHandlerRunnerSuccessNoCountryCode() throws Exception {
    var handler = new ValidationHandler(runner);
    Path mockOutputPath = mock(Path.class);
    File mockFeedFile = mock(File.class);
    URI feedFileURI = URI.create("file://fake/path/to.zip");
    String countryCode = "";

    doReturn(feedFileURI).when(mockFeedFile).toURI();
    doReturn(ValidationRunner.Status.SUCCESS).when(runner).run(any(ValidationRunnerConfig.class));

    handler.validateFeed(mockFeedFile, mockOutputPath, countryCode);

    verify(runner, times(1)).run(configCaptor.capture());
    var config = configCaptor.getValue();
    assert config.gtfsSource().equals(feedFileURI);
    assert config.outputDirectory().equals(mockOutputPath);
    assert config.countryCode().equals(CountryCode.forStringOrUnknown(countryCode));
  }

  @Test
  public void testValidationHandlerRunnerSuccessWithCountryCode() throws Exception {
    var handler = new ValidationHandler(runner);
    Path mockOutputPath = mock(Path.class);
    File mockFeedFile = mock(File.class);
    URI feedFileURI = URI.create("file://fake/path/to.zip");
    String countryCode = "US";

    doReturn(feedFileURI).when(mockFeedFile).toURI();
    doReturn(ValidationRunner.Status.SUCCESS).when(runner).run(any(ValidationRunnerConfig.class));

    handler.validateFeed(mockFeedFile, mockOutputPath, countryCode);

    verify(runner, times(1)).run(configCaptor.capture());
    var config = configCaptor.getValue();
    assert config.gtfsSource().equals(feedFileURI);
    assert config.outputDirectory().equals(mockOutputPath);
    assert config.countryCode().equals(CountryCode.forStringOrUnknown(countryCode));
  }

  @Test()
  public void testValidationHandlerRunnerExceptionStatus() throws Exception {
    var handler = new ValidationHandler(runner);
    Path mockOutputPath = mock(Path.class);
    File mockFeedFile = mock(File.class);
    URI feedFileURI = URI.create("file://fake/path/to.zip");
    String countryCode = "US";

    doReturn(feedFileURI).when(mockFeedFile).toURI();
    doReturn(ValidationRunner.Status.EXCEPTION).when(runner).run(any(ValidationRunnerConfig.class));
    Exception exception =
        assertThrows(
            Exception.class,
            () -> {
              handler.validateFeed(mockFeedFile, mockOutputPath, countryCode);
            });
    assertEquals("Validation failed", exception.getMessage());

    verify(runner, times(1)).run(configCaptor.capture());
    var config = configCaptor.getValue();
    assert config.gtfsSource().equals(feedFileURI);
    assert config.outputDirectory().equals(mockOutputPath);
    assert config.countryCode().equals(CountryCode.forStringOrUnknown(countryCode));
  }

  @Test()
  public void testValidationHandlerRunnerSystemErrorsStatus() throws Exception {
    var handler = new ValidationHandler(runner);
    Path mockOutputPath = mock(Path.class);
    File mockFeedFile = mock(File.class);
    URI feedFileURI = URI.create("file://fake/path/to.zip");
    String countryCode = "US";

    doReturn(feedFileURI).when(mockFeedFile).toURI();
    doReturn(ValidationRunner.Status.SYSTEM_ERRORS)
        .when(runner)
        .run(any(ValidationRunnerConfig.class));
    Exception exception =
        assertThrows(
            Exception.class,
            () -> {
              handler.validateFeed(mockFeedFile, mockOutputPath, countryCode);
            });
    assertEquals("Validation failed", exception.getMessage());

    verify(runner, times(1)).run(configCaptor.capture());
    var config = configCaptor.getValue();
    assert config.gtfsSource().equals(feedFileURI);
    assert config.outputDirectory().equals(mockOutputPath);
    assert config.countryCode().equals(CountryCode.forStringOrUnknown(countryCode));
  }
}
