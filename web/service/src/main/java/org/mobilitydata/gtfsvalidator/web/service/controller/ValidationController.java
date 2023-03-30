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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import java.io.*;
import java.net.URL;
import java.util.*;
import org.mobilitydata.gtfsvalidator.web.service.util.JobMetadata;
import org.mobilitydata.gtfsvalidator.web.service.util.StorageHelper;
import org.mobilitydata.gtfsvalidator.web.service.util.ValidationHandler;
import org.mobilitydata.gtfsvalidator.web.service.util.ValidationJobMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ValidationController {

  private final Logger logger = LoggerFactory.getLogger(ValidationController.class);

  @Autowired private StorageHelper storageHelper;

  /**
   * Creates a new job id and returns it to the client. If a url is provided, the file is downloaded
   * from the url and saved to GCS. If no url is provided, a unique url is generated for the client
   * to upload the GTFS file.
   */
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/create-job", produces = "application/json", consumes = "application/json")
  public CreateJobResponse createJob(@RequestBody CreateJobRequest body) {
    try {
      final var jobId = UUID.randomUUID().toString();
      if (body != null && !Strings.isNullOrEmpty(body.getCountryCode())) {
        storageHelper.saveJobMetaData(new JobMetadata(jobId, body.getCountryCode()));
      }

      // Check to see if this request has a url
      if (body != null && !Strings.isNullOrEmpty(body.getUrl())) {
        storageHelper.saveJobFileFromUrl(jobId, body.getUrl());
        return new CreateJobResponse(jobId, null);
      }
      // If no URL is provided, then we generate a unique url for the client to upload the GTFS file
      URL url = storageHelper.generateUniqueUploadUrl(jobId);

      return new CreateJobResponse(jobId, url.toString());
    } catch (Exception exc) {
      logger.error("Error", exc);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error", exc);
    }
  }

  /**
   * Runs the validator on the GTFS file associated with the job id. The GTFS file is downloaded
   * from GCS, extracted locally, validated, and the results are uploaded to GCS.
   */
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
      var handler = new ValidationHandler();

      ValidationJobMetaData jobData = getFeedFileMetaData(message);
      var jobId = jobData.getJobId();
      var fileName = jobData.getFileName();

      var countryCode = storageHelper.getJobMetaData(jobId).getCountryCode();

      // copy the file from GCS to a temp directory
      File tempFile = storageHelper.copyFromStorageToTempFile(jobId, fileName);

      tempDir = tempFile.getParentFile();
      var outputPath = new File(tempDir.toPath() + jobId);
      // extracts feed files from zip to temp output directory, validates, and returns
      // the path to the output directory
      handler.validateFeed(tempFile, outputPath, countryCode);

      // upload the extracted files and the validation results from outputPath to GCS
      storageHelper.uploadFilesToStorage(jobId, outputPath);

      // delete the temp directory
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

  /**
   * Extracts the job ID and input file name from the Pub/Sub message.
   *
   * @param message
   * @return
   * @throws JsonProcessingException
   */
  private ValidationJobMetaData getFeedFileMetaData(GoogleCloudPubsubMessage.Message message)
      throws JsonProcessingException {
    var data = new String(Base64.getDecoder().decode(message.getData()));

    var map = new ObjectMapper();
    var node = map.readTree(data);

    var inputFilename = node.get("name").textValue();
    var jobId = inputFilename.split("/")[0];
    return new ValidationJobMetaData(jobId, inputFilename);
  }
}
