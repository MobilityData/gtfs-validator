/*
 * Copyright 2023 Jarvus Innovations LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mobilitydata.gtfsvalidator.web.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunner;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;
import org.mobilitydata.gtfsvalidator.util.VersionResolver;
import org.mobilitydata.gtfsvalidator.web.service.util.StorageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ValidationController {

  private final Logger logger = LoggerFactory.getLogger(ValidationController.class);

  @Getter(AccessLevel.PROTECTED)
  @Setter(AccessLevel.PROTECTED)
  @Autowired
  private Storage storage;

  @Autowired private ApplicationContext applicationContext;

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/create-job")
  public String createJob(@RequestBody CreateJobBody body) {
    try {
      final var jobId = UUID.randomUUID().toString();
      var countryCode = "";
      StorageHelper storageHelper = new StorageHelper(storage, applicationContext);

      if (body != null && !body.getCountryCode().isEmpty()) {
        countryCode = body.getCountryCode();
        storageHelper.saveJobMetaData(jobId, countryCode);
      }

      // Check to see if this request has a url
      if (body != null && body.getUrl() != null && !body.getUrl().isEmpty()) {
        storageHelper.handleUrlUpload(jobId, body.getUrl());
        return "{\"jobId\": \"" + jobId + "\"}";
      }
      // If no URL is provided, then we generate a unique url for the client to upload the GTFS file
      URL url = storageHelper.generateUniqueUploadUrl(jobId);

      return "{\"jobId\": \"" + jobId + "\", \"url\": \"" + url.toString() + "\"}";
    } catch (Exception exc) {
      logger.error("Error", exc);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error", exc);
    }
  }

  @PostMapping("/run-validator")
  public ResponseEntity runValidator(
      @RequestBody GoogleCloudPubsubMessage googleCloudPubsubMessage) {
    File tempDir = null;
    try {
      var message = googleCloudPubsubMessage.getMessage();
      if (message == null) {
        var msg = "Bad Request: invalid Pub/Sub message format";
        return new ResponseEntity(msg, HttpStatus.BAD_REQUEST);
      }

      var data = new String(Base64.getDecoder().decode(message.getData()));

      var map = new ObjectMapper();
      var node = map.readTree(data);

      var inputFilename = node.get("name").textValue();
      var jobId = inputFilename.split("/")[0];

      StorageHelper storageHelper = new StorageHelper(storage, applicationContext);
      var countryCode = storageHelper.getJobCountryCode(jobId);
      logger.info("Country code: " + countryCode);

      File tempFile = storageHelper.createTempFile(jobId, inputFilename);

      var runner = new ValidationRunner(new VersionResolver());
      tempDir = tempFile.getParentFile();
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

      storageHelper.uploadFilesToStorage(jobId, outputPath);

      tempDir.delete();
      return new ResponseEntity(HttpStatus.OK);
    } catch (Exception exc) {
      logger.error("Error", exc);
      if (tempDir != null) {
        tempDir.delete();
      }
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error", exc);
    }
  }

  @PostMapping("/error")
  public ResponseEntity Error() {
    try {
      throw new Exception("Exception message");
    } catch (Exception exc) {
      logger.error("Error", exc);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error", exc);
    }
  }
}
