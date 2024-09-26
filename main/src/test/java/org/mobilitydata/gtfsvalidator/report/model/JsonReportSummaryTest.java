package org.mobilitydata.gtfsvalidator.report.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.report.JsonReportSummary;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;
import org.mobilitydata.gtfsvalidator.util.VersionInfo;

@RunWith(JUnit4.class)
public class JsonReportSummaryTest {

  private static VersionInfo versionInfo =
      VersionInfo.create(Optional.of("1.0"), Optional.of("1.1"));

  private static Gson gson = new Gson();

  private static ValidationRunnerConfig generateValidationRunnerConfig() throws Exception {

    ValidationRunnerConfig.Builder builder = ValidationRunnerConfig.builder();

    builder.setCountryCode(CountryCode.forStringOrUnknown("GB"));
    builder.setGtfsSource(new URI("some_dataset_filename"));
    builder.setHtmlReportFileName("some_html_filename");
    builder.setOutputDirectory(Path.of("some_output_directory"));
    builder.setNumThreads(1);
    builder.setPrettyJson(true);
    builder.setSystemErrorsReportFileName("some_error_filename");
    builder.setValidationReportFileName("some_report_filename");
    builder.setDateForValidation(LocalDate.parse("2020-01-02"));

    return builder.build();
  }

  private static FeedMetadata generateFeedMetaData() {
    FeedMetadata feedMetadata = mock(FeedMetadata.class);
    when(feedMetadata.getFilenames()).thenReturn(ImmutableSortedSet.of("file1", "file2"));
    new AgencyMetadata("agency1", "some URL 1", "phone1", "email1");
    feedMetadata.agencies =
        new ArrayList<>(
            Arrays.asList(
                new AgencyMetadata("agency1", "some URL 1", "phone1", "email1"),
                new AgencyMetadata("agency1", "some URL 1", "phone1", "email1")));
    feedMetadata.feedInfo =
        Map.of(
            FeedMetadata.FEED_INFO_PUBLISHER_NAME,
            "value1",
            FeedMetadata.FEED_INFO_PUBLISHER_URL,
            "value2",
            FeedMetadata.FEED_INFO_FEED_CONTACT_EMAIL,
            "me@foo.com",
            FeedMetadata.FEED_INFO_SERVICE_WINDOW,
            "2024-01-02 to 2024-11-03",
            "Illegal Key",
            "Some Value" // Should not be present in the resulting GSON
            );
    feedMetadata.counts =
        Map.of(
            FeedMetadata.COUNTS_SHAPES,
            1,
            FeedMetadata.COUNTS_TRIPS,
            2,
            "Illegal Key",
            3 // Should not be present in the resulting GSON
            );
    feedMetadata.specFeatures = Map.of("Feature1", false, "Feature2", true);
    feedMetadata.validationTimeSeconds = 100.0;
    return feedMetadata;
  }

  @Test
  public void noFeedMetadataWithConfigTest() throws Exception {

    JsonReportSummary reportSummary =
        new JsonReportSummary(null, generateValidationRunnerConfig(), versionInfo, "now");

    String expected =
        "{\"validatorVersion\":\"1.0\","
            + "\"validatedAt\":\"now\","
            + "\"gtfsInput\":\"some_dataset_filename\","
            + "\"threads\":1,"
            + "\"outputDirectory\":\"some_output_directory\","
            + "\"systemErrorsReportName\":\"some_error_filename\","
            + "\"validationReportName\":\"some_report_filename\","
            + "\"htmlReportName\":\"some_html_filename\","
            + "\"countryCode\":\"GB\","
            + "\"dateForValidation\":\"2020-01-02\"}";

    assertEquals(JsonParser.parseString(expected), gson.toJsonTree(reportSummary));
  }

  @Test
  public void withFeedMetadataWithConfigTest() throws Exception {

    FeedMetadata feedMetadata = generateFeedMetaData();
    JsonReportSummary reportSummary =
        new JsonReportSummary(feedMetadata, generateValidationRunnerConfig(), versionInfo, "now");

    String expected =
        "{\"validatorVersion\":\"1.0\","
            + "\"validatedAt\":\"now\","
            + "\"gtfsInput\":\"some_dataset_filename\","
            + "\"threads\":1,"
            + "\"outputDirectory\":\"some_output_directory\","
            + "\"systemErrorsReportName\":\"some_error_filename\","
            + "\"validationReportName\":\"some_report_filename\","
            + "\"htmlReportName\":\"some_html_filename\","
            + "\"countryCode\":\"GB\","
            + "\"dateForValidation\":\"2020-01-02\","
            + "\"feedInfo\":{\"publisherName\":\"value1\",\"publisherUrl\":\"value2\",\"feedEmail\":\"me@foo.com\",\"feedServiceWindow\":\"2024-01-02 to 2024-11-03\"},"
            + "\"validationTimeSeconds\":100.0,"
            + "\"agencies\":["
            + "{\"name\":\"agency1\",\"url\":\"some URL 1\",\"phone\":\"phone1\",\"email\":\"email1\"},"
            + "{\"name\":\"agency1\",\"url\":\"some URL 1\",\"phone\":\"phone1\",\"email\":\"email1\"}],"
            + "\"files\":[\"file1\",\"file2\"],"
            + "\"counts\":{\"Shapes\":1,\"Trips\":2},"
            + "\"gtfsFeatures\":[\"Feature2\"]}";

    assertEquals(JsonParser.parseString(expected), gson.toJsonTree(reportSummary));
  }

  @Test
  public void withFeedMetadataNoConfigTest() throws Exception {

    FeedMetadata feedMetadata = generateFeedMetaData();
    JsonReportSummary reportSummary = new JsonReportSummary(feedMetadata, null, versionInfo, "now");

    String expected =
        "{\"validatorVersion\":\"1.0\","
            + "\"validatedAt\":\"now\","
            + "\"threads\":0,"
            + "\"feedInfo\":{\"publisherName\":\"value1\",\"publisherUrl\":\"value2\",\"feedEmail\":\"me@foo.com\",\"feedServiceWindow\":\"2024-01-02 to 2024-11-03\"},"
            + "\"validationTimeSeconds\":100.0,"
            + "\"agencies\":["
            + "{\"name\":\"agency1\",\"url\":\"some URL 1\",\"phone\":\"phone1\",\"email\":\"email1\"},"
            + "{\"name\":\"agency1\",\"url\":\"some URL 1\",\"phone\":\"phone1\",\"email\":\"email1\"}],"
            + "\"files\":[\"file1\",\"file2\"],"
            + "\"counts\":{\"Shapes\":1,\"Trips\":2},"
            + "\"gtfsFeatures\":[\"Feature2\"]}";

    assertEquals(JsonParser.parseString(expected), gson.toJsonTree(reportSummary));
  }
}
