package org.mobilitydata.gtfsvalidator.web.service.util;

import java.io.File;
import java.nio.file.Path;
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
  public void validateFeed(File feedFile, Path outputPath, String countryCode) {
    var configBuilder =
        ValidationRunnerConfig.builder()
            .setGtfsSource(feedFile.toURI())
            .setOutputDirectory(outputPath);
    if (!countryCode.isEmpty()) {
      var country = CountryCode.forStringOrUnknown(countryCode);
      logger.info("setting country code: " + country.getCountryCode());
      configBuilder.setCountryCode(CountryCode.forStringOrUnknown(countryCode));
    }
    var config = configBuilder.build();
    runner.run(config);
  }
}
