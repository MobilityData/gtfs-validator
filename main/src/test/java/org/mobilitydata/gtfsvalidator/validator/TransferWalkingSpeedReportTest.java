package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Objects;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.runner.ApplicationType;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunner;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.util.VersionResolver;
import org.mobilitydata.gtfsvalidator.validator.TransferWalkingSpeedValidator.TransferWalkingSpeedTooFastNotice;

public class TransferWalkingSpeedReportTest {

  private static final String NOTICE_CODE = "transfer_walking_speed_too_fast";
  private static final double EXPECTED_DISTANCE_METERS = 111.19510117748394;

  @Rule public final TemporaryFolder temporaryFolder = new TemporaryFolder();

  // R01: Check the notice fields, types, severity and JSON export.
  @Test
  public void testNoticeSerialization() {
    GtfsTransfer transfer =
        new GtfsTransfer.Builder()
            .setCsvRowNumber(7)
            .setFromStopId("A")
            .setToStopId("B")
            .setMinTransferTime(30)
            .build();

    TransferWalkingSpeedTooFastNotice notice =
        new TransferWalkingSpeedTooFastNotice(transfer, EXPECTED_DISTANCE_METERS);

    assertThat(notice.getCode()).isEqualTo(NOTICE_CODE);

    assertThat(ValidationNotice.getDefaultSeverityLevel(TransferWalkingSpeedTooFastNotice.class))
        .isEqualTo(SeverityLevel.INFO);

    JsonObject sample = notice.toJsonTree().getAsJsonObject();

    assertThat(sample.get("csvRowNumber").getAsInt()).isEqualTo(7);
    assertThat(sample.get("fromStopId").getAsString()).isEqualTo("A");
    assertThat(sample.get("toStopId").getAsString()).isEqualTo("B");
    assertThat(sample.get("minTransferTime").getAsInt()).isEqualTo(30);

    assertThat(sample.get("stopDistance").getAsDouble())
        .isWithin(1e-6)
        .of(EXPECTED_DISTANCE_METERS);

    assertThat(sample.get("csvRowNumber").getAsJsonPrimitive().isNumber()).isTrue();
    assertThat(sample.get("fromStopId").getAsJsonPrimitive().isString()).isTrue();
    assertThat(sample.get("toStopId").getAsJsonPrimitive().isString()).isTrue();
    assertThat(sample.get("minTransferTime").getAsJsonPrimitive().isNumber()).isTrue();
    assertThat(sample.get("stopDistance").getAsJsonPrimitive().isNumber()).isTrue();

    NoticeContainer notices = new NoticeContainer();
    notices.addValidationNotice(notice);

    JsonObject exported = notices.exportValidationNotices();

    JsonObject group = findNoticeGroup(exported);

    assertThat(group).isNotNull();
    assertThat(group.get("severity").getAsString()).isEqualTo("INFO");
    assertThat(group.get("totalNotices").getAsInt()).isEqualTo(1);

    JsonArray samples = group.getAsJsonArray("sampleNotices");

    assertThat(samples.size()).isEqualTo(1);
    assertThat(samples.get(0).getAsJsonObject()).isEqualTo(sample);
  }

  // R02: Run the real GTFS Validator and inspect its generated report.
  @Test
  public void testRealGtfsFeedProducesExpectedReport() throws Exception {
    Path feedDirectory =
        Paths.get(
            Objects.requireNonNull(getClass().getResource("/transfer_walking_speed_too_fast/feed"))
                .toURI());

    Path outputDirectory = temporaryFolder.newFolder("report").toPath();

    ValidationRunnerConfig config =
        ValidationRunnerConfig.builder()
            .setGtfsSource(feedDirectory.toUri())
            .setOutputDirectory(outputDirectory)
            .setDateForValidation(LocalDate.of(2026, 10, 21))
            .setNumThreads(1)
            .setSkipValidatorUpdate(true)
            .build();

    ValidationRunner.Status status =
        new ValidationRunner(new VersionResolver(ApplicationType.CLI)).run(config);

    assertThat(status).isEqualTo(ValidationRunner.Status.SUCCESS);

    Path reportPath = outputDirectory.resolve("report.json");

    assertThat(Files.exists(reportPath)).isTrue();

    JsonObject report =
        JsonParser.parseString(Files.readString(reportPath, StandardCharsets.UTF_8))
            .getAsJsonObject();

    JsonObject group = findNoticeGroup(report);

    // The new rule must be discovered and executed by the real runner.
    assertThat(group).isNotNull();
    assertThat(group.get("severity").getAsString()).isEqualTo("INFO");

    // Only the 30-second transfer should trigger the rule.
    assertThat(group.get("totalNotices").getAsInt()).isEqualTo(1);

    JsonArray samples = group.getAsJsonArray("sampleNotices");

    assertThat(samples.size()).isEqualTo(1);

    JsonObject sample = samples.get(0).getAsJsonObject();

    assertThat(sample.get("csvRowNumber").getAsInt()).isEqualTo(2);
    assertThat(sample.get("fromStopId").getAsString()).isEqualTo("A");
    assertThat(sample.get("toStopId").getAsString()).isEqualTo("B");
    assertThat(sample.get("minTransferTime").getAsInt()).isEqualTo(30);

    assertThat(sample.get("stopDistance").getAsDouble())
        .isWithin(1e-6)
        .of(EXPECTED_DISTANCE_METERS);

    // The validation run should not produce system errors.
    Path errorsPath = outputDirectory.resolve("system_errors.json");

    assertThat(Files.exists(errorsPath)).isTrue();

    JsonObject errors =
        JsonParser.parseString(Files.readString(errorsPath, StandardCharsets.UTF_8))
            .getAsJsonObject();

    assertThat(errors.getAsJsonArray("notices").size()).isEqualTo(0);
  }

  // Locate the target notice without depending on other rule results.
  private static JsonObject findNoticeGroup(JsonObject report) {
    JsonArray groups = report.getAsJsonArray("notices");

    if (groups == null) {
      return null;
    }

    JsonObject result = null;

    for (JsonElement element : groups) {
      JsonObject group = element.getAsJsonObject();

      if (NOTICE_CODE.equals(group.get("code").getAsString())) {
        // There should only be one group for this notice code.
        assertThat(result).isNull();
        result = group;
      }
    }

    return result;
  }
}
