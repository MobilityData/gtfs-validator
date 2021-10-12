package org.mobilitydata.gtfsvalidator.cloudwrapper;

import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.flogger.FluentLogger;
import com.google.events.cloud.pubsub.v1.Message;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class CloudTrigger implements BackgroundFunction<Message> {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  @Override
  public void accept(Message message, Context context) {
    if (message != null && message.getData() != null) {
      String messageString =
          new String(
              Base64.getDecoder().decode(message.getData().getBytes(StandardCharsets.UTF_8)),
              StandardCharsets.UTF_8);
      JsonObject gson = new Gson().fromJson(messageString, JsonObject.class);
      String jarName = gson.get("jarName").getAsString();
      String datasetUrl = gson.get("datasetUrl").getAsString();
      String outputBase = gson.get("outputBase").getAsString();

      // Retrieve jar from GCS
      String projectId = "gfs-validator-320318";
      String bucketName = "gtfs-validator-jars";
      String destFilePath = ".";
      Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
      Blob blob = storage.get(BlobId.of(bucketName, jarName));
      blob.downloadTo(Paths.get(destFilePath));

      try {
        // Execute validator's jar as a shell command
        Runtime.getRuntime()
            .exec(String.format("java -jar %s -u %s -o %s", jarName, datasetUrl, outputBase));
        // Upload validation report to GCS
        BlobId blobId = BlobId.of("gtfs-validator-reports", "report.json");
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, Files.readAllBytes(Paths.get("./report.json")));
        logger.atInfo().log("Validation report from from %s successfully uploaded to bucket!");
      } catch (IOException ioException) {
        logger.atSevere().log(ioException.getMessage());
      }
    } else {
      logger.atSevere().log("Missing message or message data");
    }
    return;
  }
}
