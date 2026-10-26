package org.mobilitydata.gtfsvalidator.runner;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
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
import org.mobilitydata.gtfsvalidator.table.GtfsEntityContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedLoader;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
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
   * The feed container is released before reports are generated, so the summary is printed from the
   * table totals captured beforehand. Both overloads must log exactly the same thing.
   */
  @Test
  public void printSummary_tableTotalsOverload_logsSameOutputAsFeedContainerOverload() {
    GtfsFeedContainer feedContainer =
        new GtfsFeedContainer(
            ImmutableList.<GtfsEntityContainer<?, ?>>of(
                GtfsStopTimeTableContainer.forEntities(ImmutableList.of(), new NoticeContainer())));
    FeedMetadata feedMetadata = FeedMetadata.from(feedContainer, ImmutableSet.of());
    feedMetadata.validationTimeSeconds = 1.5;
    GtfsFeedLoader loader = new GtfsFeedLoader(ImmutableList.of());
    ValidationRunnerConfig config = buildConfig("/tmp/nonexistent.zip");

    List<String> fromTableTotals =
        captureLoggedMessages(
            () ->
                ValidationRunner.printSummary(
                    feedMetadata, feedContainer.tableTotalsText(), loader, config));
    List<String> fromFeedContainer =
        captureLoggedMessages(
            () -> ValidationRunner.printSummary(feedMetadata, feedContainer, loader, config));

    assertThat(fromTableTotals).contains("stop_times.txt\t0");
    assertThat(fromFeedContainer).isEqualTo(fromTableTotals);
  }

  /** Captures the messages logged by {@link ValidationRunner} while {@code runnable} runs. */
  private static List<String> captureLoggedMessages(Runnable runnable) {
    List<String> messages = new ArrayList<>();
    Handler handler =
        new Handler() {
          @Override
          public void publish(LogRecord record) {
            messages.add(record.getMessage());
          }

          @Override
          public void flush() {}

          @Override
          public void close() {}
        };
    Logger logger = Logger.getLogger(ValidationRunner.class.getName());
    Level previousLevel = logger.getLevel();
    boolean previousUseParentHandlers = logger.getUseParentHandlers();
    logger.setLevel(Level.INFO);
    logger.setUseParentHandlers(false);
    logger.addHandler(handler);
    try {
      runnable.run();
    } finally {
      logger.removeHandler(handler);
      logger.setUseParentHandlers(previousUseParentHandlers);
      logger.setLevel(previousLevel);
    }
    return messages;
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

  /**
   * The report is buffered on its way to stdout, so this covers a report larger than the buffer,
   * the trailing newline, and the encoding of System.out.
   */
  @Test
  public void exportReport_stdoutOutput_writesTheWholeJsonReport() {
    String filename = "stazioné.txt";
    ValidationRunnerConfig config =
        ValidationRunnerConfig.builder()
            .setGtfsSource(Path.of("feed.zip").toUri())
            .setOutputDirectory(Path.of("."))
            .setCountryCode(CountryCode.forStringOrUnknown(""))
            .setStdoutOutput(true)
            .build();
    NoticeContainer noticeContainer = new NoticeContainer();
    for (int i = 1; i <= 1_000; i++) {
      noticeContainer.addValidationNotice(
          new MissingRequiredFieldNotice(filename, i, String.format("field_%04d", i)));
    }

    ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    PrintStream originalStdout = System.out;
    System.setOut(new PrintStream(stdout, true, StandardCharsets.UTF_8));
    try {
      ValidationRunner.exportReport(
          FeedMetadata.from(
              new GtfsFeedContainer(ImmutableList.of()), ImmutableSet.of("stop_times.txt")),
          noticeContainer,
          config,
          VersionInfo.empty());
    } finally {
      System.setOut(originalStdout);
    }

    String output = stdout.toString(StandardCharsets.UTF_8);
    assertThat(output.length()).isGreaterThan(1 << 16);
    assertThat(output).endsWith("}" + System.lineSeparator());
    JsonObject notice =
        JsonParser.parseString(output)
            .getAsJsonObject()
            .getAsJsonArray("notices")
            .get(0)
            .getAsJsonObject();
    assertThat(notice.get("totalNotices").getAsInt()).isEqualTo(1_000);
    assertThat(notice.getAsJsonArray("sampleNotices")).hasSize(1_000);
    assertThat(output).contains(filename);
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
