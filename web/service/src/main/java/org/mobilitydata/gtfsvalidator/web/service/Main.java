package org.mobilitydata.gtfsvalidator.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@SpringBootApplication
@RestController
public class Main {

  static final String userUploadBucketName = "gtfs-validator-user-uploads";
  static final String resultsBucketName = "gtfs-validator-results";
  static final String jobInfoBucketName = "gtfs-validator-results";
  final String fileName = "gtfs-job.zip";
  static final String jobFilenamePrefix = "job";
  static final String jobFilenameSuffix = ".json";
  static final String jobFilename = jobFilenamePrefix + jobFilenameSuffix;
  static final String countryCodeKey = "country-code";
  static final String defaultCountryCode = "US";
  static final String tempFolderName = "gtfs-validator-temp";

  @Getter(AccessLevel.PROTECTED)
  @Setter(AccessLevel.PROTECTED)
  @Autowired
  private Storage storage;

  private static ConfigurableApplicationContext applicationContext;

  public static void main(String[] args) {
    applicationContext = SpringApplication.run(Main.class, args);
  }

  public static String getJobInfoPath(String jobId) {
    return jobId + "/" + jobFilename;
  }

  public static File getRemoteFile(String remoteLocation, String localPrefix, String localSuffix)
      throws Exception {
    try {
      var tempDir = Files.createTempDirectory(tempFolderName).toFile();

      var inputResource = applicationContext.getResource(remoteLocation);
      var inputFile = inputResource.getInputStream();

      var tempFile = File.createTempFile(localPrefix, localSuffix, tempDir);
      var output = new FileOutputStream(tempFile);
      inputFile.transferTo(output);

      return tempFile;
    } catch (Exception exc) {
      System.out.println("Error could not load remote file:" + exc.toString());
      throw exc;
    }
  }

  public void setJobCountryCode(String jobId, String countryCode) throws Exception {
    try {
      var jobInfoPath = getJobInfoPath(jobId);
      var jobBlobId = BlobId.of(jobInfoBucketName, jobInfoPath);
      var jobBlobInfo = BlobInfo.newBuilder(jobBlobId).setContentType("application/json").build();
      var om = new ObjectMapper();
      var jobNode = om.createObjectNode();
      jobNode.put(countryCodeKey, countryCode);
      var writer = om.writer();
      var fileBytes = writer.writeValueAsBytes(jobNode);
      storage.create(jobBlobInfo, fileBytes);
    } catch (Exception exc) {
      System.out.println("Error setting country code:" + exc.toString());
      throw new Error("Could not set job country code");
    }
  }

  public static String getJobCountryCode(String jobId) {
    try {
      var jobInfoPath = getJobInfoPath(jobId);
      var jobInfoFile =
          getRemoteFile(
              "gs://" + jobInfoBucketName + "/" + jobInfoPath,
              jobFilenamePrefix,
              jobFilenameSuffix);
      var fileBytes = Files.readAllBytes(Paths.get(jobInfoFile.getPath()));
      var objectMapper = new ObjectMapper();
      var jobNode = objectMapper.readTree(fileBytes);
      var countryCode = jobNode.get(countryCodeKey).textValue();
      return countryCode;
    } catch (Exception exc) {
      System.out.println(
          "Error could not load remote file, using default country code:" + exc.toString());
      // Use default
      return defaultCountryCode;
    }
  }

  @CrossOrigin(origins = "*")
  @PostMapping(value = "/create-job")
  public String createJob(@RequestBody CreateJobBody body) {
    try {
      final var jobId = UUID.randomUUID().toString();
      var countryCode = defaultCountryCode;

      if (body != null && !body.getCountryCode().isEmpty()) {
        countryCode = body.getCountryCode();
      }

      if (body != null && body.getUrl() != null && !body.getUrl().isEmpty()) {
        // Read file into memory
        var url = body.getUrl();
        var urlInputStream = new BufferedInputStream(new URL(url).openStream());

        // Upload to GCS
        var blobId = BlobId.of(userUploadBucketName, jobId + "/" + jobId + ".zip");
        var mimeType = "application/zip";
        var blobInfo = BlobInfo.newBuilder(blobId).setContentType(mimeType).build();
        var fileBytes = urlInputStream.readAllBytes();
        storage.create(blobInfo, fileBytes);
        setJobCountryCode(jobId, countryCode);
        return "{\"jobId\": \"" + jobId + "\"}";
      } else {
        var blobInfo =
            BlobInfo.newBuilder(BlobId.of(userUploadBucketName, jobId + "/" + fileName)).build();

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

        setJobCountryCode(jobId, countryCode);
        return "{\"jobId\": \"" + jobId + "\", \"url\": \"" + url.toString() + "\"}";
      }
    } catch (Exception exc) {
      System.out.println("Error:" + exc.toString());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error", exc);
    }
  }

  @PostMapping("/run-validator")
  public ResponseEntity runValidator(@RequestBody Body body) {
    try {
      var message = body.getMessage();
      if (message == null) {
        var msg = "Bad Request: invalid Pub/Sub message format";
        System.out.println(msg);
        return new ResponseEntity(msg, HttpStatus.BAD_REQUEST);
      }

      var data = new String(Base64.getDecoder().decode(message.getData()));

      var map = new ObjectMapper();
      var node = map.readTree(data);

      var inputFilename = node.get("name").textValue();
      var jobId = inputFilename.split("/")[0];
      var tempDir = Files.createTempDirectory(tempFolderName).toFile();

      var inputResource =
          applicationContext.getResource("gs://" + userUploadBucketName + "/" + inputFilename);
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
          System.out.println(
              ("Writing to : "
                  + "gs://"
                  + resultsBucketName
                  + "/"
                  + jobId
                  + "/"
                  + reportFile.getName()));

          var blobId = BlobId.of(resultsBucketName, jobId + "/" + reportFile.getName());
          var mimeType = Files.probeContentType(reportFile.toPath());
          var blobInfo = BlobInfo.newBuilder(blobId).setContentType(mimeType).build();
          var fileBytes = Files.readAllBytes(Paths.get(reportFile.getPath()));
          storage.create(blobInfo, fileBytes);
        }
      }

      tempDir.delete();
      return new ResponseEntity(HttpStatus.OK);
    } catch (Exception exc) {
      System.out.println("Error:" + exc.toString());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error", exc);
    }
  }
}
