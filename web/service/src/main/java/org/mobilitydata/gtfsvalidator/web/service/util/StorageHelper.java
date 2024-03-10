package org.mobilitydata.gtfsvalidator.web.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.mobilitydata.gtfsvalidator.util.HttpGetUtil;
import org.mobilitydata.gtfsvalidator.web.service.controller.ValidationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/** Helper class for interacting with GCS. */
@Component
public class StorageHelper {
  public String JOB_INFO_BUCKET_NAME =
      System.getenv().getOrDefault("JOB_INFO_BUCKET_NAME", "gtfs-validator-results");

  static final String JOB_FILENAME_PREFIX = "job";
  static final String JOB_FILENAME_SUFFIX = ".json";
  public static final String JOB_FILENAME = JOB_FILENAME_PREFIX + JOB_FILENAME_SUFFIX;
  public static final String TEMP_FOLDER_NAME =
      System.getenv().getOrDefault("TEMP_FOLDER_NAME", "gtfs-validator-temp");
  static final String USER_UPLOAD_BUCKET_NAME =
      System.getenv().getOrDefault("USER_UPLOAD_BUCKET_NAME", "gtfs-validator-user-uploads");
  static final String RESULTS_BUCKET_NAME =
      System.getenv().getOrDefault("RESULTS_BUCKET_NAME", "gtfs-validator-results");
  static final String FILE_NAME = "gtfs-job.zip";

  private static final Logger logger = LoggerFactory.getLogger(StorageHelper.class);

  private Storage storage;
  private ApplicationContext applicationContext;

  // constructor
  public StorageHelper(Storage storage, ApplicationContext applicationContext) {
    this.storage = storage;
    this.applicationContext = applicationContext;
  }

  public String createNewJobId() {
    return UUID.randomUUID().toString();
  }

  public static String getJobInfoPath(String jobId) {
    return jobId + "/" + JOB_FILENAME;
  }

  /**
   * Creates job metadata, serializes to JSON and saves it to GCS.
   *
   * @param metadata
   * @throws Exception
   */
  public void saveJobMetadata(JobMetadata metadata) throws Exception {
    try {
      String jobId = metadata.getJobId();
      var jobInfoPath = getJobInfoPath(jobId);
      var jobBlobId = BlobId.of(JOB_INFO_BUCKET_NAME, jobInfoPath);
      var jobBlobInfo = BlobInfo.newBuilder(jobBlobId).setContentType("application/json").build();
      var om = new ObjectMapper();
      var json = om.writeValueAsString(metadata);
      logger.info("Saving job metadata: " + json);
      storage.create(jobBlobInfo, json.getBytes());
    } catch (Exception exc) {
      logger.error("Error setting country code", exc);
      throw exc;
    }
  }

  /**
   * Loads job metadata from GCS.
   *
   * @param jobId
   * @return
   */
  public JobMetadata getJobMetadata(String jobId) {
    try {
      var jobInfoPath = getJobInfoPath(jobId);
      var jobBlobId = BlobId.of(JOB_INFO_BUCKET_NAME, jobInfoPath);
      Blob blob = storage.get(jobBlobId);
      var json = new String(blob.getContent());
      logger.info("Loading job metadata: " + json);

      var objectMapper = new ObjectMapper();
      JobMetadata jobMetadata = objectMapper.readValue(json, JobMetadata.class);
      return jobMetadata;
    } catch (Exception exc) {
      logger.error("Error could not load remote file, using default country code", exc);
      return new JobMetadata(jobId, "");
    }
  }

  /**
   * Saves a file from a URL to GCS at a job-specific path.
   *
   * @param jobId
   * @param url
   * @param validatorVersion
   * @throws Exception
   */
  public void saveJobFileFromUrl(String jobId, String url, String validatorVersion)
      throws Exception {
    var blobId = BlobId.of(USER_UPLOAD_BUCKET_NAME, jobId + "/" + FILE_NAME);
    var blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/zip").build();
    URL signedURL =
        storage.signUrl(
            blobInfo, 1, TimeUnit.HOURS, Storage.SignUrlOption.httpMethod(HttpMethod.POST));
    try (WriteChannel writer = storage.writer(signedURL)) {
      OutputStream outputStream = Channels.newOutputStream(writer);
      HttpGetUtil.loadFromUrl(new URL(url), outputStream, validatorVersion);
    }
  }

  /** Generates a job-specific signed URL for uploading a file to GCS. */
  public URL generateUniqueUploadUrl(String jobId) {
    var blobInfo =
        BlobInfo.newBuilder(BlobId.of(USER_UPLOAD_BUCKET_NAME, jobId + "/" + FILE_NAME)).build();

    // Generate Signed URL
    Map<String, String> extensionHeaders = new HashMap<>();
    extensionHeaders.put("Content-Type", "application/octet-stream");

    URL url =
        storage.signUrl(
            blobInfo,
            15,
            TimeUnit.MINUTES,
            Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
            Storage.SignUrlOption.withExtHeaders(extensionHeaders),
            Storage.SignUrlOption.withV4Signature());
    return url;
  }

  /**
   * Copies the uploaded feed file from GCS to a local temp directory and returns the file.
   *
   * @param jobId
   * @param fileName
   * @return
   * @throws IOException
   */
  public File downloadFeedFileFromStorage(String jobId, String fileName) throws IOException {
    var tempDir = Files.createTempDirectory(StorageHelper.TEMP_FOLDER_NAME).toFile();

    var inputResource =
        applicationContext.getResource("gs://" + USER_UPLOAD_BUCKET_NAME + "/" + fileName);

    var tempFile = File.createTempFile(jobId, ".zip", tempDir);
    var output = new FileOutputStream(tempFile);
    inputResource.getInputStream().transferTo(output);
    return tempFile;
  }

  /**
   * Uploads the validation report files to GCS.
   *
   * @param jobId
   * @param outputPath
   * @throws IOException
   */
  public void uploadFilesToStorage(String jobId, Path outputPath) throws IOException {
    var directoryListing = outputPath.toFile().listFiles();
    if (directoryListing != null) {
      for (var reportFile : directoryListing) {
        if (reportFile.isDirectory()) {
          continue;
        }
        var blobId = BlobId.of(RESULTS_BUCKET_NAME, jobId + "/" + reportFile.getName());
        var mimeType = Files.probeContentType(reportFile.toPath());
        var blobInfo = BlobInfo.newBuilder(blobId).setContentType(mimeType).build();
        var fileBytes = Files.readAllBytes(Paths.get(reportFile.getPath()));
        storage.create(blobInfo, fileBytes);
      }
    }
  }

  public Path createOutputFolderForJob(String jobId) throws IOException {
    return Files.createTempDirectory(StorageHelper.TEMP_FOLDER_NAME + jobId);
  }

  private static final String executionResultFile = "execution_result.json";

  public void writeExecutionResultFile(
      ValidationController.ExecutionResult executionResult, Path outputPath) {
    if (outputPath == null) {
      logger.error("Error: outputPath is null, cannot write execution result file");
      return;
    }
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    Path executionResultPath = outputPath.resolve(executionResultFile);
    try {
      logger.info("Writing executionResult file to " + executionResultFile);
      Files.write(
          executionResultPath, gson.toJson(executionResult).getBytes(StandardCharsets.UTF_8));
      logger.info(executionResultFile + " file written successfully");
    } catch (IOException e) {
      logger.error("Error writing to file " + executionResultFile);
      e.printStackTrace();
    }
  }
}
