package org.mobilitydata.gtfsvalidator.web.service.util;

import java.io.File;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunner;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;
import org.mobilitydata.gtfsvalidator.util.VersionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Helper class for validating GTFS feeds. */
public class ValidationHandler {
  private final Logger logger = LoggerFactory.getLogger(ValidationHandler.class);

  /**
   * Validates the GTFS feed zip file, and stores the results in a local temp directory using the
   * job ID as the directory name.
   *
   * @param feedFile
   * @param jobId
   * @param countryCode
   * @return the path to the temp directory containing the validation results
   */
  public File validateFeed(File feedFile, String jobId, String countryCode) {
    var runner = new ValidationRunner(new VersionResolver());
    var tempDir = feedFile.getParentFile();
    var outputPath = new File(tempDir.toPath() + jobId);
    var configBuilder =
        ValidationRunnerConfig.builder()
            .setGtfsSource(feedFile.toURI())
            .setOutputDirectory(outputPath.toPath());
    if (!countryCode.isEmpty()) {
      var country = CountryCode.forStringOrUnknown(countryCode);
      logger.info("setting country code: " + country.getCountryCode());
      configBuilder.setCountryCode(CountryCode.forStringOrUnknown(countryCode));
    }
    var config = configBuilder.build();
    runner.run(config);
    return outputPath;
  }
}
