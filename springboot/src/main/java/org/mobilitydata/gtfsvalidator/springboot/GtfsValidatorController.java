package org.mobilitydata.gtfsvalidator.springboot;

import com.beust.jcommander.JCommander;
import com.github.stefanbirkner.systemlambda.SystemLambda;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Runs the validation through a {@code SpringBoot} interface triggered by incoming HTTP requests.
 * Provides methods to push the validation report to Google Cloud Storage.
 */
@RestController
public class GtfsValidatorController {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private static final Gson GSON =
      new GsonBuilder()
          .serializeNulls()
          .serializeSpecialFloatingPointValues()
          .create();
  private static final String VALIDATION_REPORT_BUCKET_NAME_ENV_VAR = "VALIDATION_REPORT_BUCKET";
  private static final String VALIDATION_REPORT_BUCKET_NAME =
      System.getenv(VALIDATION_REPORT_BUCKET_NAME_ENV_VAR);

  private static final String DEFAULT_OUTPUT_BASE = "output";
  private static final String DEFAULT_NUM_THREADS = "8";
  private static final String DEFAULT_BUCKET_LOCATION = "US";
  private static final String PROPERTIES_JSON_KEY = "properties";
  private static final String MESSAGE_JSON_KEY = "message";
  private static final String DEFAULT_COUNTRY_CODE = "ZZ";
  private static final StringBuilder messageBuilder = new StringBuilder();

  /**
   * Runs {@code org.mobilitydata.gtfsvalidator.cli.Main.main} with the arguments extracted via the
   * query parameters. Returns the the {@code ResponseEntity} that contains information about the
   * response's {@code HttpStatus} and a message (as a {@code String}) that gives more information
   * about success or failure. Please note that this method requires authentication to be setup
   * prior to execution i.e. environment variable GOOGLE_APPLICATION_CREDENTIALS AND
   * VALIDATION_REPORT_BUCKET have to be defined.
   *
   * <p>Please check:
   *
   * <ul>
   *   <li><a href=https://cloud.google.com/storage/docs/naming-buckets>this documentation for
   *       bucket naming guidelines</a>
   *   <li><a
   *       href=https://cloud.google.com/docs/authentication/getting-started#setting_the_environment_variable>this
   *       documentation for authentication procedure</a>
   * </ul>
   *
   * @param output_base Base directory to store the outputs
   * @param threads Number of threads to use
   * @param country_code Country code of the feed, e.g., `nl`. It must be a two-letter country code
   *     (ISO 3166-1 alpha-2)")
   * @param url Fully qualified URL to download GTFS archive
   * @param validation_report_name The name of the validation report including .json extension.
   * @param dataset_id the id of the dataset validated
   * @param commit_sha the commit SHA
   * @return the {@code ResponseEntity} that contains information about the response's {@code
   *     HttpStatus} and a message (as a {@code String}) that gives more information about success
   *     or failure.
   */
  @GetMapping(value = "/", produces = "application/json; charset=UTF-8")
  @ResponseBody
  public ResponseEntity<String> run(
      @RequestParam(required = false, defaultValue = DEFAULT_OUTPUT_BASE) String output_base,
      @RequestParam(required = false, defaultValue = DEFAULT_NUM_THREADS) String threads,
      @RequestParam(required = false, defaultValue = DEFAULT_COUNTRY_CODE) String country_code,
      @RequestParam() String url,
      @RequestParam(required = false, defaultValue = Arguments.VALIDATION_REPORT_NAME_JSON)
          String validation_report_name,
      @RequestParam(required = false, defaultValue = Arguments.SYSTEM_ERRORS_REPORT_NAME_JSON)
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
    JsonObject root = new JsonObject();
    JsonObject properties = new JsonObject();
    root.add(PROPERTIES_JSON_KEY, properties);
    messageBuilder.setLength(0);
    HttpStatus status;
    try {
      int exitCode = SystemLambda.catchSystemExit(() -> Main.main(argv));
      if (exitCode != 0) {
        messageBuilder.append(
            "Internal error. Please check execution logs for more information.\n");
        root.getAsJsonObject(PROPERTIES_JSON_KEY)
            .addProperty(MESSAGE_JSON_KEY, messageBuilder.toString());
        status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(GSON.toJson(root), status);
      }
    } catch (AssertionError assertionError) {
      messageBuilder.append("Execution of the validator was successful.\n");
    } catch (Exception exception) {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
      root.getAsJsonObject(PROPERTIES_JSON_KEY)
          .addProperty(MESSAGE_JSON_KEY, messageBuilder.append(exception.getMessage()).toString());
      logger.atSevere().log(exception.getMessage());
      return new ResponseEntity<>(GSON.toJson(root), status);
    }
    status =
        pushValidationReportToCloudStorage(
            VALIDATION_REPORT_BUCKET_NAME, commit_sha, dataset_id, args, messageBuilder, root);
    return new ResponseEntity<>(GSON.toJson(root), status);
  }

  /**
   * Pushes validation report to Google Cloud Storage. Returns the {@code HttpStatus} of the
   * validation report storage process. Requires authentication to be set prior execution i.e.
   * environment variables GOOGLE_APPLICATION_CREDENTIALS and VALIDATION_REPORT_BUCKET have to be
   * defined.
   *
   * <p>Please check:
   *
   * <ul>
   *   <li><a href=https://cloud.google.com/storage/docs/naming-buckets>this documentation for
   *       bucket naming guidelines</a>
   *   <li><a
   *       href=https://cloud.google.com/docs/authentication/getting-started#setting_the_environment_variable>this
   *       documentation for authentication procedure</a>
   * </ul>
   *
   * @param commitSha the commit SHA
   * @param datasetId the id of the dataset
   * @param args the {@code Argument} generated from the query parameters
   * @return the {@code HttpStatus} of the validation report storage process
   */
  private HttpStatus pushValidationReportToCloudStorage(
      String bucketName,
      String commitSha,
      String datasetId,
      Arguments args,
      StringBuilder messageBuilder,
      JsonObject root) {
    // Instantiates a client
    Storage storage = StorageOptions.getDefaultInstance().getService();
    HttpStatus status;
    try {
      Bucket commitBucket = storage.get(bucketName, Storage.BucketGetOption.fields());

      if (commitBucket == null) {
        commitBucket =
            storage.create(
                BucketInfo.newBuilder(bucketName)
                    .setStorageClass(StorageClass.STANDARD)
                    .setLocation(DEFAULT_BUCKET_LOCATION)
                    .build());
        logger.atInfo().log("Bucket %s created.", commitBucket.getName());
      }
      BlobId blobId =
          BlobId.of(
              commitBucket.getName(),
              String.format("%s/%s/%s", commitSha, datasetId, args.getValidationReportName()));
      BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
      storage.create(
          blobInfo,
          Files.readAllBytes(
              Paths.get(
                  String.format("%s/%s", args.getOutputBase(), args.getValidationReportName()))));
      status = HttpStatus.OK;
      messageBuilder.append(
          String.format(
              "Validation report successfully uploaded to %s/%s/%s/%s\"\n",
              bucketName, commitSha, datasetId, args.getValidationReportName()));
    } catch (StorageException storageException) {
      status = HttpStatus.valueOf(storageException.getCode());
      messageBuilder.append(
          String.format(
              "%s - Failure to upload validation report: %s",
              storageException.getCode(), storageException.getMessage()));
      logger.atSevere().log(storageException.getMessage());
    } catch (IOException ioException) {
      status = HttpStatus.NOT_FOUND;
      messageBuilder.append(
          String.format(
              "%s - Failure to upload validation report. Could not find %s/%s/%s/%s",
              HttpStatus.NOT_FOUND.value(),
              bucketName,
              commitSha,
              datasetId,
              args.getValidationReportName()));
      logger.atSevere().log(ioException.getMessage());
    } finally {
      root.getAsJsonObject(PROPERTIES_JSON_KEY)
          .addProperty(MESSAGE_JSON_KEY, messageBuilder.toString());
    }
    return status;
  }
}
