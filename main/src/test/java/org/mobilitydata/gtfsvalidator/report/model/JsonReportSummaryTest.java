package org.mobilitydata.gtfsvalidator.report.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.report.JsonReportGenerator;
import org.mobilitydata.gtfsvalidator.report.JsonReportSummary;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;
import org.mobilitydata.gtfsvalidator.util.VersionInfo;

@RunWith(JUnit4.class)
public class JsonReportSummaryTest {

  private static VersionInfo versionInfo =
      VersionInfo.create(Optional.of("1.0"), Optional.of("1.1"));

  private static Gson gson = new Gson();

  private static JsonReportSummary generateJsonReportSummary() throws Exception {
    JsonReportGenerator reportGenerator = new JsonReportGenerator();

    FeedMetadata feedMetaData;
    ValidationRunnerConfig.Builder builder = ValidationRunnerConfig.builder();
    builder.setCountryCode(CountryCode.forStringOrUnknown("GB"));
    builder.setGtfsSource(new URI("file:///some/dataset/filename"));
    builder.setHtmlReportFileName("/some/html/filaname");
    builder.setOutputDirectory(Path.of("/some/output/direrctory"));
    builder.setNumThreads(1);
    builder.setPrettyJson(true);
    builder.setSystemErrorsReportFileName("/some/error/filename");
    builder.setValidationReportFileName("/some/report/filename");

    ValidationRunnerConfig config = builder.build();

    JsonReportSummary reportSummary = new JsonReportSummary(null, config, versionInfo, "now");

    return reportSummary;
  }

  private static ValidationRunnerConfig generateValidationRunnerConfig() throws Exception {

    ValidationRunnerConfig.Builder builder = ValidationRunnerConfig.builder();
    builder.setCountryCode(CountryCode.forStringOrUnknown("GB"));
    builder.setGtfsSource(new URI("file:///some/dataset/filename"));
    builder.setHtmlReportFileName("/some/html/filename");
    builder.setOutputDirectory(Path.of("/some/output/directory"));
    builder.setNumThreads(1);
    builder.setPrettyJson(true);
    builder.setSystemErrorsReportFileName("/some/error/filename");
    builder.setValidationReportFileName("/some/report/filename");

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
    feedMetadata.feedInfo = Map.of("key1", "value1", "key2", "value2");
    feedMetadata.counts = Map.of("count1", 1, "count2", 2);
    feedMetadata.specFeatures = Map.of("Feature1", false, "Feature2", true);
    return feedMetadata;
  }

  @Test
  public void noFeedMetadataWithConfigTest() throws Exception {

    JsonReportSummary reportSummary =
        new JsonReportSummary(null, generateValidationRunnerConfig(), versionInfo, "now");

    String qaz = gson.toJson(reportSummary);
    String expected =
        "{\"validatorVersion\":\"1.0\","
            + "\"validatedAt\":\"now\","
            + "\"gtfsInput\":\"file:///some/dataset/filename\","
            + "\"threads\":1,"
            + "\"outputDirectory\":\"/some/output/directory\","
            + "\"systemErrorsReportName\":\"/some/error/filename\","
            + "\"validationReportName\":\"/some/report/filename\","
            + "\"htmlReportName\":\"/some/html/filename\","
            + "\"countryCode\":\"GB\"}";

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
            + "\"gtfsInput\":\"file:///some/dataset/filename\","
            + "\"threads\":1,"
            + "\"outputDirectory\":\"/some/output/directory\","
            + "\"systemErrorsReportName\":\"/some/error/filename\","
            + "\"validationReportName\":\"/some/report/filename\","
            + "\"htmlReportName\":\"/some/html/filename\","
            + "\"countryCode\":\"GB\","
            + "\"feedInfo\":{\"key1\":\"value1\",\"key2\":\"value2\"},"
            + "\"agencies\":["
            + "{\"name\":\"agency1\",\"url\":\"some URL 1\",\"phone\":\"phone1\",\"email\":\"email1\"},"
            + "{\"name\":\"agency1\",\"url\":\"some URL 1\",\"phone\":\"phone1\",\"email\":\"email1\"}],"
            + "\"files\":[\"file1\",\"file2\"],"
            + "\"counts\":{\"count1\":1,\"count2\":2},"
            + "\"gtfsComponents\":[\"Feature2\"]}";

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
            + "\"feedInfo\":{\"key1\":\"value1\",\"key2\":\"value2\"},"
            + "\"agencies\":["
            + "{\"name\":\"agency1\",\"url\":\"some URL 1\",\"phone\":\"phone1\",\"email\":\"email1\"},"
            + "{\"name\":\"agency1\",\"url\":\"some URL 1\",\"phone\":\"phone1\",\"email\":\"email1\"}],"
            + "\"files\":[\"file1\",\"file2\"],"
            + "\"counts\":{\"count1\":1,\"count2\":2},"
            + "\"gtfsComponents\":[\"Feature2\"]}";

    assertEquals(JsonParser.parseString(expected), gson.toJsonTree(reportSummary));
  }
}
