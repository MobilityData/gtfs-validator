package org.mobilitydata.gtfsvalidator.web.service.util;

import java.io.File;
import java.nio.file.Path;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunner;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Helper class for validating GTFS feeds. */
@Component
@RequiredArgsConstructor
public class ValidationHandler {
  @Autowired private final ValidationRunner runner;
  private final Logger logger = LoggerFactory.getLogger(ValidationHandler.class);

  /**
   * Validates the GTFS feed zip file, and stores the results in a local temp directory using the
   * job ID as the directory name.
   *
   * @param feedFile
   * @param outputPath
   * @param countryCode
   */
  public void validateFeed(@NonNull File feedFile, @NonNull Path outputPath, String countryCode)
      throws Exception {
    var configBuilder =
        ValidationRunnerConfig.builder()
            .setGtfsSource(feedFile.toURI())
            .setOutputDirectory(outputPath)
            .setSkipValidatorUpdate(
                true); // skipValidatorUpdate is true to prevent remote version checks and forces
    // use of the JAR manifest version.
    if (!countryCode.isEmpty()) {
      var country = CountryCode.forStringOrUnknown(countryCode);
      logger.debug("setting country code: {}", country.getCountryCode());
      configBuilder.setCountryCode(CountryCode.forStringOrUnknown(countryCode));
    }
    var config = configBuilder.build();
    ValidationRunner.Status status = runner.run(config);
    if (status != ValidationRunner.Status.SUCCESS) {
      logger.error("Validation failed");
      throw new Exception("Validation failed");
    }
  }
}
