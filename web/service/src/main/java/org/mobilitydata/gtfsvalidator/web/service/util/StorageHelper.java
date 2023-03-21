package org.mobilitydata.gtfsvalidator.web.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Helper class for interacting with GCS.
 */
public class StorageHelper {
  static final String JOB_INFO_BUCKET_NAME = "gtfs-validator-results";
  static final String JOB_FILENAME_PREFIX = "job";
  static final String JOB_FILENAME_SUFFIX = ".json";
  static final String JOB_FILENAME = JOB_FILENAME_PREFIX + JOB_FILENAME_SUFFIX;
  public static final String TEMP_FOLDER_NAME = "gtfs-validator-temp";
  static final String USER_UPLOAD_BUCKET_NAME = "gtfs-validator-user-uploads";
  static final String RESULTS_BUCKET_NAME = "gtfs-validator-results";
  static final String FILE_NAME = "gtfs-job.zip";

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

  /**
   * Creates job metadata, serializes to JSON and saves it to GCS.
   *
   * @param jobId
   * @param countryCode
   * @throws Exception
   */
  public void saveJobMetaData(String jobId, String countryCode) throws Exception {
    try {
      var jobInfoPath = getJobInfoPath(jobId);
      var jobBlobId = BlobId.of(JOB_INFO_BUCKET_NAME, jobInfoPath);
      var jobBlobInfo = BlobInfo.newBuilder(jobBlobId).setContentType("application/json").build();
      var om = new ObjectMapper();
      var jobMetadata = new JobMetadata(jobId, countryCode);
      var json = om.writeValueAsString(jobMetadata);
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
  public String getJobCountryCode(String jobId) {
    try {
      var jobInfoPath = getJobInfoPath(jobId);
      var jobBlobId = BlobId.of(JOB_INFO_BUCKET_NAME, jobInfoPath);
      Blob blob = storage.get(jobBlobId);
      var json = new String(blob.getContent());
      logger.info("Loading job metadata: " + json);

      var objectMapper = new ObjectMapper();
      JobMetadata jobMetaData = objectMapper.readValue(json, JobMetadata.class);
      return jobMetaData.getCountryCode();
    } catch (Exception exc) {
      logger.error("Error could not load remote file, using default country code", exc);
      return "";
    }
  }

  /**
   * Saves a file from a URL to GCS at a job-specific path.
   *
   * @param jobId
   * @param url
   * @throws Exception
   */
  public void saveJobFileFromUrl(String jobId, String url) throws Exception {
    // Read file into memory
    var urlInputStream = new BufferedInputStream(new URL(url).openStream());

    // Upload to GCS
    var blobId = BlobId.of(USER_UPLOAD_BUCKET_NAME, jobId + "/" + jobId + ".zip");
    var mimeType = "application/zip";
    var blobInfo = BlobInfo.newBuilder(blobId).setContentType(mimeType).build();
    var fileBytes = urlInputStream.readAllBytes();
    storage.create(blobInfo, fileBytes);
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
   * Copies the uploaded feed file from GCS to a local temp directory
   * and returns the file.
   *
   * @param jobId
   * @param fileName
   * @return
   * @throws IOException
   */
  public File createTempFile(String jobId, String fileName) throws IOException {
    var tempDir = Files.createTempDirectory(StorageHelper.TEMP_FOLDER_NAME).toFile();

    var inputResource =
        applicationContext.getResource("gs://" + USER_UPLOAD_BUCKET_NAME + "/" + fileName);
    var inputFile = inputResource.getInputStream();

    var tempFile = File.createTempFile(jobId, ".zip", tempDir);
    var output = new FileOutputStream(tempFile);
    inputFile.transferTo(output);
    return tempFile;
  }

  /**
   * Uploads the validation report files to GCS.
   *
   * @param jobId
   * @param outputPath
   * @throws IOException
   */
  public void uploadFilesToStorage(String jobId, File outputPath) throws IOException {
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
  }
}
