package org.mobilitydata.gtfsvalidator.web.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class StorageHelper {
  static final String JOB_INFO_BUCKET_NAME = "gtfs-validator-results";
  static final String JOB_FILENAME_PREFIX = "job";
  static final String JOB_FILENAME_SUFFIX = ".json";
  static final String JOB_FILENAME = JOB_FILENAME_PREFIX + JOB_FILENAME_SUFFIX;
  static final String COUNTRY_CODE_KEY = "country-code";
  public static final String TEMP_FOLDER_NAME = "gtfs-validator-temp";

  private final Logger logger = LoggerFactory.getLogger(StorageHelper.class);
  private Storage storage;
  private ApplicationContext applicationContext;

  // constructor
  public StorageHelper(Storage storage, ApplicationContext applicationContext) {
    this.storage = storage;
    this.applicationContext = applicationContext;
  }

  public static String getJobInfoPath(String jobId) {
    return jobId + "/" + JOB_FILENAME;
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
      return jobNode.get(COUNTRY_CODE_KEY).textValue();
    } catch (Exception exc) {
      logger.error("Error could not load remote file, using default country code", exc);
      return "";
    }
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
}
