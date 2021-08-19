package org.mobilitydata.gtfsvalidator.springboot;

import com.beust.jcommander.JCommander;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageOptions;
import com.google.common.flogger.FluentLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.mobilitydata.gtfsvalidator.cli.Arguments;
import org.mobilitydata.gtfsvalidator.cli.Main;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
/**
 * Holds the responsability to run the validation through a {@code SpringBoot} interface. Provides
 * methods to push the validation report to a google cloud storage.
 */
public class GtfsValidatorController {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private static final Gson DEFAULT_GSON =
      new GsonBuilder()
          .serializeNulls()
          .setPrettyPrinting()
          .serializeSpecialFloatingPointValues()
          .create();
  private static final String VALIDATION_REPORT_BUCKET = "VALIDATION_REPORT_BUCKET";
  private static final String DEFAULT_OUTPUT_BASE = "output";
  private static final String DEFAULT_NUM_THREADS = "8";
  private static final String DEFAULT_COUNTRY_CODE = "null";

  @GetMapping("/")
  @ResponseBody
  /**
   * Runs {@code org.mobilitydata.gtfsvalidator.cli.Main.main} with the arguments extracted via the
   * query parameters. Returns the status of the validation and validation report storage. Requires
   * authentification to be set prior execution i.e. environment variable
   * GOOGLE_APPLICATION_CREDENTIALS has to be defined.
   */
  public String run(
      @RequestParam(required = false, defaultValue = DEFAULT_OUTPUT_BASE) String output_base,
      @RequestParam(required = false, defaultValue = DEFAULT_NUM_THREADS) String threads,
      @RequestParam(required = false, defaultValue = DEFAULT_COUNTRY_CODE) String country_code,
      @RequestParam() String url,
      @RequestParam(required = false, defaultValue = Arguments.VALIDATION_REPORT_DEFAULT_NAME_JSON)
          String validation_report_name,
      @RequestParam(required = false, defaultValue = Arguments.SYSTEM_ERRORS_REPORT_DEFAULT_NAME)
          String system_error_report_name,
      @RequestParam(required = false, defaultValue = "dataset id value") String dataset_id,
      @RequestParam(required = false, defaultValue = "commit sha value") String commit_sha) {

    final String[] argv = {
        "-o", output_base,
        "-t", threads,
        "-c", country_code,
        "-u", url,
        "-v", validation_report_name,
        "-e", system_error_report_name
    };
    Arguments args = new Arguments();
    JCommander jCommander = new JCommander(args);
    jCommander.parse(argv);
    Main.main(argv);
    return pushValidationReportToCloudStorage(commit_sha, dataset_id, args);
  }

  /**
   * Pushes validation report to google cloud storage. Returns the status of the validation and
   * validation report storage. Requires authentification to be set prior execution i.e. environment
   * variables GOOGLE_APPLICATION_CREDENTIALS and VALIDATION_REPORT_BUCKET have to be defined.
   *
   * @param commitSha the commit SHA
   * @param datasetId the id of the dataset
   * @param args      the {@code Argument} generated from the query parameters
   * @return the status of the validation and validation report storage
   */
  private String pushValidationReportToCloudStorage(
      String commitSha, String datasetId, Arguments args) {
    // Instantiates a client
    Storage storage = StorageOptions.getDefaultInstance().getService();
    StringBuilder builder = new StringBuilder();
    JsonObject root = new JsonObject();
    JsonObject properties = new JsonObject();
    root.add("properties", properties);

    Bucket commitBucket = storage
        .get(System.getenv(VALIDATION_REPORT_BUCKET), Storage.BucketGetOption.fields());
    if (commitBucket == null) {
      commitBucket =
          storage.create(
              BucketInfo.newBuilder(commitSha)
                  .setStorageClass(StorageClass.STANDARD)
                  .setLocation("US")
                  .build());
      logger.atInfo().log("Bucket %s created.", commitBucket.getName());
    }
    BlobId blobId =
        BlobId.of(
            commitBucket.getName(),
            String.format("%s/%s/%s", commitSha, datasetId, args.getValidationReportName()));
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    try {
      storage.create(
          blobInfo,
          Files.readAllBytes(
              Paths.get(
                  String.format("%s/%s", args.getOutputBase(), args.getValidationReportName()))));
      builder.append("Execution fo the validator was successful.");
    } catch (IOException ioException) {
      builder.append(
          "Something is off: either validator execution failed or validation report could not be uploaded.");
    } finally {
      properties.addProperty("message", builder.toString());
    }
    return DEFAULT_GSON.toJson(root);
  }
}
