/*
 * Copyright 2026 MobilityData IO
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

package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.mobilitydata.gtfsvalidator.util.VersionResolver;
import org.mobilitydata.gtfsvalidator.validator.StopServesConflictingModesValidator.StopServesConflictingModesNotice;

public class StopServesConflictingModesReportTest {
  private static final String NOTICE_CODE = "stop_serves_conflicting_modes";

  @Rule public final TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void exportsTheNoticeCodeSeverityAndThreeStringFields() {
    StopServesConflictingModesNotice notice =
        new StopServesConflictingModesNotice("S", "bus, rail", "RAIL, BUS");

    assertThat(notice.getCode()).isEqualTo(NOTICE_CODE);
    assertThat(ValidationNotice.getDefaultSeverityLevel(StopServesConflictingModesNotice.class))
        .isEqualTo(SeverityLevel.WARNING);

    JsonObject sample = notice.toJsonTree().getAsJsonObject();
    assertThat(sample.keySet()).containsExactly("stopId", "routeIds", "routeTypes");
    assertThat(sample.get("stopId").getAsString()).isEqualTo("S");
    assertThat(sample.get("routeIds").getAsString()).isEqualTo("bus, rail");
    assertThat(sample.get("routeTypes").getAsString()).isEqualTo("RAIL, BUS");
    for (String field : sample.keySet()) {
      assertThat(sample.get(field).getAsJsonPrimitive().isString()).isTrue();
    }

    NoticeContainer notices = new NoticeContainer();
    notices.addValidationNotice(notice);
    JsonObject group = findNoticeGroup(notices.exportValidationNotices());
    assertThat(group).isNotNull();
    assertThat(group.get("severity").getAsString()).isEqualTo("WARNING");
    assertThat(group.get("totalNotices").getAsInt()).isEqualTo(1);
    assertThat(group.getAsJsonArray("sampleNotices").size()).isEqualTo(1);
    assertThat(group.getAsJsonArray("sampleNotices").get(0)).isEqualTo(sample);
  }

  @Test
  public void discoversTheValidatorAndReportsOnlyTheConflictingStop() throws Exception {
    Path feedDirectory =
        Path.of(
            Objects.requireNonNull(getClass().getResource("/stop_serves_conflicting_modes/feed"))
                .toURI());
    Path outputDirectory = temporaryFolder.newFolder("report").toPath();
    ValidationRunnerConfig config =
        ValidationRunnerConfig.builder()
            .setGtfsSource(feedDirectory.toUri())
            .setOutputDirectory(outputDirectory)
            .setDateForValidation(LocalDate.of(2026, 10, 28))
            .setNumThreads(1)
            .setSkipValidatorUpdate(true)
            .build();

    assertThat(new ValidationRunner(new VersionResolver(ApplicationType.CLI)).run(config))
        .isEqualTo(ValidationRunner.Status.SUCCESS);

    JsonObject report =
        JsonParser.parseString(
                Files.readString(outputDirectory.resolve("report.json"), StandardCharsets.UTF_8))
            .getAsJsonObject();
    JsonObject group = findNoticeGroup(report);
    assertThat(group).isNotNull();
    assertThat(group.get("severity").getAsString()).isEqualTo("WARNING");
    assertThat(group.get("totalNotices").getAsInt()).isEqualTo(1);
    JsonArray samples = group.getAsJsonArray("sampleNotices");
    assertThat(samples.size()).isEqualTo(1);
    assertThat(samples.get(0).getAsJsonObject())
        .isEqualTo(
            JsonParser.parseString(
                    "{\"stopId\":\"C\",\"routeIds\":\"bus, rail\",\"routeTypes\":\"RAIL, BUS\"}")
                .getAsJsonObject());

    JsonObject errors =
        JsonParser.parseString(
                Files.readString(
                    outputDirectory.resolve("system_errors.json"), StandardCharsets.UTF_8))
            .getAsJsonObject();
    assertThat(errors.getAsJsonArray("notices").size()).isEqualTo(0);
    assertThat(Files.readString(outputDirectory.resolve("report.html"), StandardCharsets.UTF_8))
        .contains(NOTICE_CODE);
  }

  private static JsonObject findNoticeGroup(JsonObject report) {
    JsonObject result = null;
    for (JsonElement element : report.getAsJsonArray("notices")) {
      JsonObject group = element.getAsJsonObject();
      if (NOTICE_CODE.equals(group.get("code").getAsString())) {
        assertThat(result).isNull();
        result = group;
      }
    }
    return result;
  }
}
