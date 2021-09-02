/*
 * Copyright 2021 MobilityData IO
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

package org.mobilitydata.gtfsvalidator.springboot;

import static org.mobilitydata.gtfsvalidator.cli.Main.createGtfsInput;
import static org.mobilitydata.gtfsvalidator.cli.Main.exportReport;
import static org.mobilitydata.gtfsvalidator.cli.Main.printSummary;

import com.beust.jcommander.JCommander;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import com.google.common.flogger.FluentLogger;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import org.mobilitydata.gtfsvalidator.cli.Arguments;
import org.mobilitydata.gtfsvalidator.cli.CliParametersAnalyzer;
import org.mobilitydata.gtfsvalidator.cli.Main;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;
import org.mobilitydata.gtfsvalidator.input.GtfsInput;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedLoader;
import org.mobilitydata.gtfsvalidator.validator.ValidationContext;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;
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
  static final String VALIDATION_REPORT_BUCKET_NAME_ENV_VAR = "VALIDATION_REPORT_BUCKET";
  private static final String DEFAULT_OUTPUT_BASE = "output";
  private static final String DEFAULT_NUM_THREADS = "8";
  private static final String DEFAULT_BUCKET_LOCATION = "US";
  private static final String MESSAGE_JSON_KEY = "message";
  private static final String STATUS_JSON_KEY = "status";
  private static final String DEFAULT_COUNTRY_CODE = "ZZ";

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
   * <a href=https://cloud.google.com/storage/docs/json_api/v1/status-codes>Possible HTTP status
   * codes</a>
   *
   * @param output_base Base directory to store the outputs
   * @param threads Number of threads to use
   * @param country_code Country code of the feed, e.g., `nl`. It must be a two-letter country code
   *     (ISO 3166-1 alpha-2)")
   * @param url The fully qualified URL to download GTFS archive
   * @param validation_report_name The name of the validation report including .json extension.
   * @param dataset_id the id of the dataset validated
   * @param commit_sha the commit SHA
   * @return the {@code ResponseEntity} that contains information about the response's {@code
   *     HttpStatus} and a message (as a {@code String}) that gives more information about success
   *     or failure. <a href=https://cloud.google.com/storage/docs/json_api/v1/status-codes>Possible
   *     HTTP status codes</a>
   */
  @GetMapping(value = "/", produces = "application/json; charset=UTF-8")
  @ResponseBody
  public ResponseEntity<HashMap<String, String>> run(
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

    Arguments args =
        queryParametersToArguments(
            output_base,
            threads,
            country_code,
            url,
            validation_report_name,
            system_error_report_name);
    StringBuilder messageBuilder = new StringBuilder();
    HttpStatus status;
    final long startNanos = System.nanoTime();

    // this should not happen
    if (!CliParametersAnalyzer.isValid(args)) {
      status = HttpStatus.BAD_REQUEST;
      messageBuilder.append(
          "Bad request. Please check query parameters and execution logs for more information.\n");
      return generateResponse(messageBuilder, status);
    }
    ValidatorLoader validatorLoader;
    GtfsInput gtfsInput;
    GtfsFeedContainer feedContainer;
    NoticeContainer noticeContainer = new NoticeContainer();
    try {
      validatorLoader = new ValidatorLoader();
    } catch (ValidatorLoaderException e) {
      return generateResponse(
          messageBuilder.append(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    GtfsFeedLoader feedLoader = new GtfsFeedLoader();
    try {
      gtfsInput = createGtfsInput(args);
    } catch (IOException | URISyntaxException ioException) {
      logger.atSevere().withCause(ioException);
      return generateResponseAndExportReport(
          messageBuilder.append(ioException.getMessage()),
          HttpStatus.BAD_REQUEST,
          noticeContainer,
          args);
    }
    ValidationContext validationContext =
        ValidationContext.builder()
            .setCountryCode(
                CountryCode.forStringOrUnknown(
                    args.getCountryCode() == null ? CountryCode.ZZ : args.getCountryCode()))
            .setCurrentDateTime(new CurrentDateTime(ZonedDateTime.now(ZoneId.systemDefault())))
            .build();

    try {
      feedContainer =
          Main.loadAndValidate(
              validatorLoader, feedLoader, noticeContainer, gtfsInput, validationContext);
    } catch (InterruptedException e) {
      logger.atSevere().withCause(e).log("Validation was interrupted");
      messageBuilder.append("Internal error. Please execution logs for more information.\n");
      return generateResponse(
          messageBuilder.append(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    Main.closeGtfsInput(gtfsInput, noticeContainer);
    printSummary(startNanos, feedContainer);
    messageBuilder.append("Execution of the validator was successful.\n");
    exportReport(noticeContainer, args);
    return generateResponse(
        messageBuilder,
        pushValidationReportToCloudStorage(commit_sha, dataset_id, args, messageBuilder));
  }

  /**
   * Converts query parameters to {@code Argument}.
   *
   * @param output_base Base directory to store the outputs
   * @param threads Number of threads to use
   * @param country_code Country code of the feed, e.g., `nl`. It must be a two-letter country code
   *     (ISO 3166-1 alpha-2)").
   * @param url The fully qualified URL to download GTFS archive
   * @param validation_report_name The name of the validation report including .json extension.
   * @param system_error_report_name The name of the system error report including .json extension.
   * @return the {@code Argument} instance generated from the query parameters passed.
   */
  private Arguments queryParametersToArguments(
      String output_base,
      String threads,
      String country_code,
      String url,
      String validation_report_name,
      String system_error_report_name) {
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
    return args;
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
      String commitSha, String datasetId, Arguments args, StringBuilder messageBuilder) {
    // Instantiates a client
    Storage storage = StorageOptions.getDefaultInstance().getService();
    HttpStatus status = HttpStatus.OK;
    try {
      Bucket commitBucket =
          storage.get(
              System.getenv(GtfsValidatorController.VALIDATION_REPORT_BUCKET_NAME_ENV_VAR),
              Storage.BucketGetOption.fields());

      if (commitBucket == null) {
        commitBucket =
            storage.create(
                BucketInfo.newBuilder(System.getenv(VALIDATION_REPORT_BUCKET_NAME_ENV_VAR))
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
      try (WriteChannel writer = storage.writer(blobInfo)) {
        writer.write(
            ByteBuffer.wrap(
                Files.readAllBytes(
                    Paths.get(
                        String.format(
                            "%s/%s", args.getOutputBase(), args.getValidationReportName())))));
      }
      messageBuilder.append(
          String.format(
              "Validation report successfully uploaded to %s/%s/%s/%s.\n",
              System.getenv(VALIDATION_REPORT_BUCKET_NAME_ENV_VAR),
              commitSha,
              datasetId,
              args.getValidationReportName()));
    } catch (NullPointerException nullPointerException) {
      status = HttpStatus.PRECONDITION_FAILED;
      messageBuilder.append(
          String.format(
              "Environment variable not provided: %s.\n", VALIDATION_REPORT_BUCKET_NAME_ENV_VAR));
      logger.atSevere().log(nullPointerException.getMessage());
    } catch (StorageException storageException) {
      status = HttpStatus.valueOf(storageException.getCode());
      messageBuilder.append(
          String.format(
              "Failure to upload validation report. %s\n", storageException.getMessage()));
      logger.atSevere().log(storageException.getMessage());
    } catch (IOException ioException) {
      status = HttpStatus.BAD_REQUEST;
      messageBuilder.append(
          String.format(
              "Failure to find validation report. Could not find %s/%s",
              args.getOutputBase(), args.getValidationReportName()));
      logger.atSevere().log(ioException.getMessage());
    } finally {
      buildResponseBody(messageBuilder, status);
    }
    return status;
  }

  /**
   * Generates response body and exports validation report
   *
   * @param messageBuilder additional information about the request' status
   * @param status the status of the request after execution
   * @param noticeContainer the {@code NoticeContainer} to extract {@code Notice}s from
   * @param args the {@code Arguments} to use for report export
   * @return the {@code ResponseEntity} that corresponds to the request
   */
  private static ResponseEntity<HashMap<String, String>> generateResponseAndExportReport(
      StringBuilder messageBuilder,
      HttpStatus status,
      NoticeContainer noticeContainer,
      Arguments args) {
    exportReport(noticeContainer, args);
    return generateResponse(messageBuilder, status);
  }

  /**
   * Generates response body
   *
   * @param messageBuilder the message to be displayed
   * @param status the status of the request after execution
   * @return the {@code ResponseEntity} that corresponds to the request
   */
  private static ResponseEntity<HashMap<String, String>> generateResponse(
      StringBuilder messageBuilder, HttpStatus status) {
    return new ResponseEntity<>(buildResponseBody(messageBuilder, status), status);
  }

  /**
   * Builds the response body as a {@code HashMap<String, String>} that contains information about
   * the request' status and additional information as a message.
   *
   * @param messageBuilder additional information about the request as amessage
   * @param status the request's status
   * @return the response body as a {@code HashMap<String, String>} that contains information about
   *     the request' status and additional information as a message.
   */
  private static HashMap<String, String> buildResponseBody(
      StringBuilder messageBuilder, HttpStatus status) {
    HashMap<String, String> root = new HashMap<>();
    root.put(MESSAGE_JSON_KEY, messageBuilder.toString());
    root.put(STATUS_JSON_KEY, status.toString());
    return root;
  }
}
