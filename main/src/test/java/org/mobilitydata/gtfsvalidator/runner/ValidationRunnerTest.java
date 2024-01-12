package org.mobilitydata.gtfsvalidator.runner;

import static org.junit.Assert.assertThrows;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.CountryCode;

@RunWith(JUnit4.class)
public class ValidationRunnerTest {

  private static ValidationRunnerConfig buildConfig(String gtfsDirectory) {
    ValidationRunnerConfig.Builder config = ValidationRunnerConfig.builder();
    config.setGtfsSource(Path.of(gtfsDirectory).toUri());
    config.setOutputDirectory(Path.of(""));
    config.setNumThreads(1);
    config.setCountryCode(CountryCode.forStringOrUnknown(""));
    return config.build();
  }

  @Test
  public void createGtfsInput_WindowsPath_valid() throws IOException, URISyntaxException {
    ValidationRunnerConfig config =
        buildConfig("C:\\projects\\gtfs-validator\\non-existent-file.zip");

    // We are testing path parsing here only. We expect a FileNotFoundException but NOT a
    // InvalidPathException. This should catch issues such as #1158.
    assertThrows(
        FileNotFoundException.class, () -> ValidationRunner.createGtfsInput(config, "1.1.0"));
  }

  @Test
  public void createGtfsInput_LinuxPath_valid() throws IOException, URISyntaxException {
    ValidationRunnerConfig config = buildConfig("/Users/me/gtfs-validator/non-existent-file.zip");

    // We are testing path parsing here only. We expect a FileNotFoundException but NOT a
    // InvalidPathException. This should catch issues such as #1158.
    assertThrows(
        FileNotFoundException.class, () -> ValidationRunner.createGtfsInput(config, "1.1.0"));
  }
}
