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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class CloudTrigger implements BackgroundFunction<Message> {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  @Override
  public void accept(Message message, Context context) {
    if (message != null && message.getData() != null) {
      Map<String, String> attributes = message.getAttributes();
      String jarName = attributes.get("jarName");
      String datasetUrl = attributes.get("datasetUrl");
      String outputBase = attributes.get("outputBase");

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
