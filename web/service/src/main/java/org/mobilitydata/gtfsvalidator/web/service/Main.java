package org.mobilitydata.gtfsvalidator.web.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.*;
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

  @Getter(AccessLevel.PROTECTED)
  @Setter(AccessLevel.PROTECTED)
  @Autowired
  private Storage storage;

  private static ConfigurableApplicationContext applicationContext;

  public static void main(String[] args) {
    applicationContext = SpringApplication.run(Main.class, args);
  }

  @CrossOrigin(origins = "*")
  @GetMapping(value = "/upload-url")
  public String getUploadUrl() {
    final var uniqueID = UUID.randomUUID().toString();
    final String bucketName = "gtfs-validator-user-uploads";
    final String fileName = "gtfs-job.zip";

    BlobInfo blobInfo =
        BlobInfo.newBuilder(BlobId.of(bucketName, uniqueID + "/" + fileName)).build();

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

    return "{\"jobId\": \"" + uniqueID + "\", \"url\": \"" + url.toString() + "\"}";
  }

  @PostMapping("/run-validator")
  public ResponseEntity runValidator(@RequestBody Body body) {
    try {
      Body.Message message = body.getMessage();
      if (message == null) {
        String msg = "Bad Request: invalid Pub/Sub message format";
        System.out.println(msg);
        return new ResponseEntity(msg, HttpStatus.BAD_REQUEST);
      }

      var data = new String(Base64.getDecoder().decode(message.getData()));

      ObjectMapper map = new ObjectMapper();
      JsonNode node = map.readTree(data);

      var inputFilename = node.get("name").textValue();
      var jobId = inputFilename.split("/")[0];
      System.out.println("jobId: " + jobId);
      var tempDir = Files.createTempDirectory("gtfs-validator-user-upload").toFile();

      var inputResource =
          applicationContext.getResource("gs://gtfs-validator-user-uploads/" + inputFilename);
      var inputFile = inputResource.getInputStream();

      var tempFile = File.createTempFile(jobId, ".zip", tempDir);
      var output = new FileOutputStream(tempFile);
      inputFile.transferTo(output);

      var runner = new ValidationRunner(new VersionResolver());
      var outputPath = new File(tempDir.toPath().toString() + jobId);
      var config =
          ValidationRunnerConfig.builder()
              .setGtfsSource(tempFile.toURI())
              .setOutputDirectory(outputPath.toPath())
              .build();
      runner.run(config);

      var directoryListing = outputPath.listFiles();
      if (directoryListing != null) {
        for (var reportFile : directoryListing) {
          System.out.println(
              ("Writing to : "
                  + "gs://gtfs-validator-results/"
                  + jobId
                  + "/"
                  + reportFile.getName()));

          BlobId blobId = BlobId.of("gtfs-validator-results", jobId + "/" + reportFile.getName());
          String mimeType = Files.probeContentType(reportFile.toPath());
          BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(mimeType).build();
          byte[] fileBytes = Files.readAllBytes(Paths.get(reportFile.getPath()));
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
