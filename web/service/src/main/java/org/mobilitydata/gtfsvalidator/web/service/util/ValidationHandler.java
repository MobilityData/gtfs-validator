package org.mobilitydata.gtfsvalidator.web.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Base64;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunner;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;
import org.mobilitydata.gtfsvalidator.util.VersionResolver;
import org.mobilitydata.gtfsvalidator.web.service.controller.GoogleCloudPubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationHandler {
  private final Logger logger = LoggerFactory.getLogger(ValidationHandler.class);

  public File validateFeed(File tempFile, String jobId, String countryCode) {
    var runner = new ValidationRunner(new VersionResolver());
    var tempDir = tempFile.getParentFile();
    var outputPath = new File(tempDir.toPath() + jobId);
    var configBuilder =
        ValidationRunnerConfig.builder()
            .setGtfsSource(tempFile.toURI())
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

  public ValidationJobMetaData getFeedFileMetaData(GoogleCloudPubsubMessage.Message message)
      throws JsonProcessingException {
    var data = new String(Base64.getDecoder().decode(message.getData()));

    var map = new ObjectMapper();
    var node = map.readTree(data);

    var inputFilename = node.get("name").textValue();
    var jobId = inputFilename.split("/")[0];
    return new ValidationJobMetaData(jobId, inputFilename);
  }
}
