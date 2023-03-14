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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunner;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;
import org.mobilitydata.gtfsvalidator.util.VersionResolver;
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
  @Autowired private ApplicationContext applicationContext;

  static final String USER_UPLOAD_BUCKET_NAME = "gtfs-validator-user-uploads";
  static final String RESULTS_BUCKET_NAME = "gtfs-validator-results";
  static final String JOB_INFO_BUCKET_NAME = "gtfs-validator-results";
  static final String FILE_NAME = "gtfs-job.zip";
  static final String JOB_FILENAME_PREFIX = "job";
  static final String JOB_FILENAME_SUFFIX = ".json";
  static final String JOB_FILENAME = JOB_FILENAME_PREFIX + JOB_FILENAME_SUFFIX;
  static final String COUNTRY_CODE_KEY = "country-code";
  static final String DEFAULT_COUNTRY_CODE = "US";
  static final String TEMP_FOLDER_NAME = "gtfs-validator-temp";

  @Getter(AccessLevel.PROTECTED)
  @Setter(AccessLevel.PROTECTED)
  @Autowired
  private Storage storage;

  public static String getJobInfoPath(String jobId) {
    return jobId + "/" + JOB_FILENAME;
  }

  public File getRemoteFile(String remoteLocation, String localPrefix, String localSuffix)
      throws Exception {
    try {
      var tempDir = Files.createTempDirectory(TEMP_FOLDER_NAME).toFile();

      var inputResource = applicationContext.getResource(remoteLocation);
      var inputFile = inputResource.getInputStream();

      var tempFile = File.createTempFile(localPrefix, localSuffix, tempDir);
      var output = new FileOutputStream(tempFile);
      inputFile.transferTo(output);

      return tempFile;
    } catch (Exception exc) {
      logger.error("Error could not load remote file:", exc);
      throw exc;
    }
  }

  public void setJobCountryCode(String jobId, String countryCode) throws Exception {
    try {
      var jobInfoPath = getJobInfoPath(jobId);
      var jobBlobId = BlobId.of(JOB_INFO_BUCKET_NAME, jobInfoPath);
      var jobBlobInfo = BlobInfo.newBuilder(jobBlobId).setContentType("application/json").build();
      var om = new ObjectMapper();
      var jobNode = om.createObjectNode();
      jobNode.put(COUNTRY_CODE_KEY, countryCode);
      var writer = om.writer();
      var fileBytes = writer.writeValueAsBytes(jobNode);
      storage.create(jobBlobInfo, fileBytes);
    } catch (Exception exc) {
      logger.error("Error setting country code", exc);
      throw exc;
    }
  }

  public String getJobCountryCode(String jobId) {
    try {
      var jobInfoPath = getJobInfoPath(jobId);
      var jobInfoFile =
          getRemoteFile(
              "gs://" + JOB_INFO_BUCKET_NAME + "/" + jobInfoPath,
              JOB_FILENAME_PREFIX,
              JOB_FILENAME_SUFFIX);
      var fileBytes = Files.readAllBytes(Paths.get(jobInfoFile.getPath()));
      var objectMapper = new ObjectMapper();
      var jobNode = objectMapper.readTree(fileBytes);
      var countryCode = jobNode.get(COUNTRY_CODE_KEY).textValue();
      return countryCode;
    } catch (Exception exc) {
      logger.error("Error could not load remote file, using default country code", exc);
      // Use default
      return DEFAULT_COUNTRY_CODE;
    }
  }

  public void handleUrlUpload(String jobId, String url) throws Exception {
    // Read file into memory
    var urlInputStream = new BufferedInputStream(new URL(url).openStream());

    // Upload to GCS
    var blobId = BlobId.of(USER_UPLOAD_BUCKET_NAME, jobId + "/" + jobId + ".zip");
    var mimeType = "application/zip";
    var blobInfo = BlobInfo.newBuilder(blobId).setContentType(mimeType).build();
    var fileBytes = urlInputStream.readAllBytes();
    storage.create(blobInfo, fileBytes);
  }

  public URL generateUniqueUploadUrl(String jobId) {
    var blobInfo =
        BlobInfo.newBuilder(BlobId.of(USER_UPLOAD_BUCKET_NAME, jobId + "/" + FILE_NAME)).build();

    // Generate Signed URL
    Map<String, String> extensionHeaders = new HashMap<>();
    extensionHeaders.put("Content-Type", "application/octet-stream");

    URL url =
        getStorage()
            .signUrl(
                blobInfo,
                15,
                TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                Storage.SignUrlOption.withExtHeaders(extensionHeaders),
                Storage.SignUrlOption.withV4Signature());
    return url;
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/create-job")
  public String createJob(@RequestBody CreateJobBody body) {
    try {
      final var jobId = UUID.randomUUID().toString();
      var countryCode = DEFAULT_COUNTRY_CODE;

      if (body != null && !body.getCountryCode().isEmpty()) {
        countryCode = body.getCountryCode();
        setJobCountryCode(jobId, countryCode);
      }

      // Check to see if this request has a url
      if (body != null && body.getUrl() != null && !body.getUrl().isEmpty()) {
        handleUrlUpload(jobId, body.getUrl());
        return "{\"jobId\": \"" + jobId + "\"}";
      }
      // If no URL is provided, then we generate a unique url for the client to upload the GTFS file
      URL url = generateUniqueUploadUrl(jobId);

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
      tempDir = Files.createTempDirectory(TEMP_FOLDER_NAME).toFile();

      var inputResource =
          applicationContext.getResource("gs://" + USER_UPLOAD_BUCKET_NAME + "/" + inputFilename);
      var inputFile = inputResource.getInputStream();

      var tempFile = File.createTempFile(jobId, ".zip", tempDir);
      var output = new FileOutputStream(tempFile);
      inputFile.transferTo(output);

      var countryCode = getJobCountryCode(jobId);

      var runner = new ValidationRunner(new VersionResolver());
      var outputPath = new File(tempDir.toPath().toString() + jobId);
      var config =
          ValidationRunnerConfig.builder()
              .setGtfsSource(tempFile.toURI())
              .setOutputDirectory(outputPath.toPath())
              .setCountryCode(CountryCode.forStringOrUnknown(countryCode))
              .build();
      runner.run(config);

      var directoryListing = outputPath.listFiles();
      if (directoryListing != null) {
        for (var reportFile : directoryListing) {
          var blobId = BlobId.of(RESULTS_BUCKET_NAME, jobId + "/" + reportFile.getName());
          var mimeType = Files.probeContentType(reportFile.toPath());
          var blobInfo = BlobInfo.newBuilder(blobId).setContentType(mimeType).build();
          var fileBytes = Files.readAllBytes(Paths.get(reportFile.getPath()));
          storage.create(blobInfo, fileBytes);
        }
      }

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
