package org.mobilitydata.gtfsvalidator.runner;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.RuntimeExceptionInValidatorError;
import org.mobilitydata.gtfsvalidator.reportsummary.model.FeedMetadata;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.util.VersionInfo;

@RunWith(JUnit4.class)
public class ValidationRunnerTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private static ValidationRunnerConfig buildConfig(String gtfsDirectory) {
    ValidationRunnerConfig.Builder config = ValidationRunnerConfig.builder();
    config.setGtfsSource(Path.of(gtfsDirectory).toUri());
    config.setOutputDirectory(Path.of(""));
    config.setNumThreads(1);
    config.setCountryCode(CountryCode.forStringOrUnknown(""));
    config.setStdoutOutput(false);
    return config.build();
  }

  @Test
  public void createGtfsInput_WindowsPath_valid() throws IOException, URISyntaxException {
    ValidationRunnerConfig config =
        buildConfig("C:\\projects\\gtfs-validator\\non-existent-file.zip");

    // We are testing path parsing here only. We expect a FileNotFoundException but NOT a
    // InvalidPathException. This should catch issues such as #1158.
    assertThrows(
        FileNotFoundException.class, () -> ValidationRunner.createGtfsInput(config, "1.1.0"));
  }

  @Test
  public void createGtfsInput_LinuxPath_valid() throws IOException, URISyntaxException {
    ValidationRunnerConfig config = buildConfig("/Users/me/gtfs-validator/non-existent-file.zip");

    // We are testing path parsing here only. We expect a FileNotFoundException but NOT a
    // InvalidPathException. This should catch issues such as #1158.
    assertThrows(
        FileNotFoundException.class, () -> ValidationRunner.createGtfsInput(config, "1.1.0"));
  }

  /**
   * The reports are serialized straight to their file instead of through an intermediate string, so
   * this covers the encoding of the produced files.
   */
  @Test
  public void exportReport_writesUtf8EncodedJsonReports() throws IOException {
    // A non-ASCII name, so that the encoding of the written files is covered.
    String filename = "stazioné.txt";
    Path outputDirectory = temporaryFolder.newFolder().toPath();
    ValidationRunnerConfig config =
        ValidationRunnerConfig.builder()
            .setGtfsSource(Path.of("feed.zip").toUri())
            .setOutputDirectory(outputDirectory)
            .setCountryCode(CountryCode.forStringOrUnknown(""))
            .build();
    NoticeContainer noticeContainer = new NoticeContainer();
    noticeContainer.addValidationNotice(new MissingRequiredFieldNotice(filename, 1, "field"));
    noticeContainer.addSystemError(
        new RuntimeExceptionInValidatorError(
            "FaultyValidator", new IllegalStateException(filename)));

    ValidationRunner.exportReport(
        FeedMetadata.from(
            new GtfsFeedContainer(ImmutableList.of()), ImmutableSet.of("stop_times.txt")),
        noticeContainer,
        config,
        VersionInfo.empty());

    String validationReport =
        Files.readString(
            outputDirectory.resolve(config.validationReportFileName()), StandardCharsets.UTF_8);
    assertThat(JsonParser.parseString(validationReport).isJsonObject()).isTrue();
    assertThat(validationReport).contains(filename);
    assertThat(
            Files.readString(
                outputDirectory.resolve(config.systemErrorsReportFileName()),
                StandardCharsets.UTF_8))
        .isEqualTo(new Gson().toJson(noticeContainer.exportSystemErrors()));
  }

  @Test
  public void builderShouldDefaultStdoutOutputToFalse() {
    ValidationRunnerConfig config =
        ValidationRunnerConfig.builder()
            .setGtfsSource(Path.of("/tmp/nonexistent.zip").toUri())
            .setOutputDirectory(Path.of("out"))
            .build();

    assertFalse(config.stdoutOutput());
  }
}
