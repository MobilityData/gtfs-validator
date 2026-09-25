/*
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

package org.mobilitydata.gtfsvalidator.outputcomparator.feedversion;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mobilitydata.gtfsvalidator.outputcomparator.feedversion.VerifiedReportLoader.FileRef;
import org.mobilitydata.gtfsvalidator.outputcomparator.feedversion.VerifiedReportLoader.InputSpec;

public class VerifiedReportLoaderTest {
  private static final String VALID_REPORT =
      """
      {"summary":{"validatorVersion":"8.0.1","dateForValidation":"2026-09-25",\
      "countryCode":"HU","threads":1},"notices":[{"code":"example","severity":"ERROR",\
      "totalNotices":3,"sampleNotices":[{"csvRowNumber":2}]}]}
      """;
  private static final String EMPTY_SYSTEM_ERRORS = "{\"notices\":[]}";
  private static final String SKIP_WARNING =
      "Some validators were skipped because the GTFS files they rely on could not be parsed";

  @Rule public final TemporaryFolder folder = new TemporaryFolder();
  private final VerifiedReportLoader loader = new VerifiedReportLoader();

  @Test
  public void loadsValidatedBytesWithTruthfulEvidence() throws IOException {
    InputSpec input =
        fixture(VALID_REPORT, "{\"notices\":[],\"unused\":[],\"count\":0,\"flag\":false}");

    VerifiedReportLoader.LoadedRun loaded = loader.load(input);

    assertThat(loaded.report().getNotices()).hasSize(1);
    assertThat(loaded.report().getNotices().iterator().next().getTotalNotices()).isEqualTo(3);
    assertThat(loaded.metadata().feedSha256()).isEqualTo(input.feedZip().expectedSha256());
    assertThat(loaded.metadata().reportSha256()).isEqualTo(input.reportJson().expectedSha256());
    assertThat(loaded.metadata().evidence().complete()).isTrue();
    assertThat(loaded.metadata().systemErrorsEmpty()).isTrue();
  }

  @Test
  public void rejectsExactDuplicateGroupBeforeBuildingSet() throws IOException {
    JsonObject report = JsonParser.parseString(VALID_REPORT).getAsJsonObject();
    JsonArray groups = report.getAsJsonArray("notices");
    groups.add(groups.get(0).deepCopy());

    reject(fixture(report.toString(), EMPTY_SYSTEM_ERRORS), "duplicate notice group");
  }

  @Test
  public void rejectsArbitraryNoticeCodeWithoutEchoingIt() throws IOException {
    InputSpec input =
        fixture(
            VALID_REPORT.replace("\"code\":\"example\"", "\"code\":\"private-token\""),
            EMPTY_SYSTEM_ERRORS);
    try {
      loader.load(input);
      fail("arbitrary notice code should be rejected");
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessageThat().contains("notice code");
      assertThat(expected).hasMessageThat().doesNotContain("private-token");
    }
  }

  @Test
  public void rejectsDuplicateJsonKeysBeforeTheyCanOverwriteReportValues() throws IOException {
    reject(
        fixture(
            VALID_REPORT.replace("{\"summary\":", "{\"summary\":{},\"summary\":"),
            EMPTY_SYSTEM_ERRORS),
        "duplicate JSON key");
    reject(
        fixture(
            VALID_REPORT.replace(
                "\"validatorVersion\":\"8.0.1\"",
                "\"validatorVersion\":\"0\",\"validatorVersion\":\"8.0.1\""),
            EMPTY_SYSTEM_ERRORS),
        "duplicate JSON key");
    reject(
        fixture(
            VALID_REPORT.replace("\"totalNotices\":3", "\"totalNotices\":100,\"totalNotices\":3"),
            EMPTY_SYSTEM_ERRORS),
        "duplicate JSON key");
    reject(
        fixture(
            VALID_REPORT.replace("\"csvRowNumber\":2", "\"csvRowNumber\":2,\"csvRowNumber\":3"),
            EMPTY_SYSTEM_ERRORS),
        "duplicate JSON key");
    reject(fixture(VALID_REPORT, "{\"notices\":[],\"notices\":[]}"), "duplicate JSON key");
  }

  @Test
  public void rejectsLenientJsonSyntax() throws IOException {
    reject(
        fixture(
            VALID_REPORT.replace("\"code\":\"example\"", "code:\"example\""), EMPTY_SYSTEM_ERRORS),
        "report.json is malformed");
    reject(fixture(VALID_REPORT + " // comment", EMPTY_SYSTEM_ERRORS), "report.json is malformed");
  }

  @Test
  public void rejectsSummaryMismatchAndMalformedJson() throws IOException {
    reject(
        fixture(
            VALID_REPORT.replace("\"countryCode\":\"HU\"", "\"countryCode\":\"AT\""),
            EMPTY_SYSTEM_ERRORS),
        "summary.countryCode");
    reject(
        fixture(VALID_REPORT.replace("\"threads\":1", "\"threads\":2"), EMPTY_SYSTEM_ERRORS),
        "summary.threads");
    reject(fixture("{bad json", EMPTY_SYSTEM_ERRORS), "report.json is malformed");
  }

  @Test
  public void checksExplicitThreadOptionAgainstSummary() throws IOException {
    InputSpec input =
        fixture(VALID_REPORT.replace("\"threads\":1", "\"threads\":2"), EMPTY_SYSTEM_ERRORS);
    Map<String, String> matching =
        Map.of("-c", "HU", "-d", "2026-09-25", "-svu", "true", "--threads", "2");
    assertThat(loader.load(withOptions(input, matching)).metadata().validationOptions())
        .containsEntry("--threads", "2");
    Map<String, String> mismatched =
        Map.of("-c", "HU", "-d", "2026-09-25", "-svu", "true", "--threads", "3");
    reject(withOptions(input, mismatched), "summary.threads");
  }

  @Test
  public void rejectsActualHashMismatchForEveryFile() throws IOException {
    InputSpec input = fixture(VALID_REPORT, EMPTY_SYSTEM_ERRORS);
    reject(
        new InputSpec(
            input.sourceId(),
            input.observedAtUtc(),
            input.provenanceId(),
            input.validatorVersion(),
            input.validationDate(),
            input.countryCode(),
            input.validationOptions(),
            input.exitCode(),
            new FileRef(input.feedZip().path(), "0".repeat(64)),
            input.validatorJar(),
            input.reportJson(),
            input.systemErrorsJson(),
            input.stdoutLog(),
            input.stderrLog()),
        "feed ZIP SHA256 mismatch");
    reject(
        new InputSpec(
            input.sourceId(),
            input.observedAtUtc(),
            input.provenanceId(),
            input.validatorVersion(),
            input.validationDate(),
            input.countryCode(),
            input.validationOptions(),
            input.exitCode(),
            input.feedZip(),
            input.validatorJar(),
            new FileRef(input.reportJson().path(), "0".repeat(64)),
            input.systemErrorsJson(),
            input.stdoutLog(),
            input.stderrLog()),
        "report.json SHA256 mismatch");
    reject(
        new InputSpec(
            input.sourceId(),
            input.observedAtUtc(),
            input.provenanceId(),
            input.validatorVersion(),
            input.validationDate(),
            input.countryCode(),
            input.validationOptions(),
            input.exitCode(),
            input.feedZip(),
            new FileRef(input.validatorJar().path(), "0".repeat(64)),
            input.reportJson(),
            input.systemErrorsJson(),
            input.stdoutLog(),
            input.stderrLog()),
        "validator JAR SHA256 mismatch");
    reject(
        new InputSpec(
            input.sourceId(),
            input.observedAtUtc(),
            input.provenanceId(),
            input.validatorVersion(),
            input.validationDate(),
            input.countryCode(),
            input.validationOptions(),
            input.exitCode(),
            input.feedZip(),
            input.validatorJar(),
            input.reportJson(),
            new FileRef(input.systemErrorsJson().path(), "0".repeat(64)),
            input.stdoutLog(),
            input.stderrLog()),
        "system_errors.json SHA256 mismatch");
    InputSpec changedStdout = fixture(VALID_REPORT, EMPTY_SYSTEM_ERRORS);
    Files.writeString(changedStdout.stdoutLog().path(), "changed", StandardCharsets.UTF_8);
    reject(changedStdout, "validator stdout SHA256 mismatch");
    InputSpec changedStderr = fixture(VALID_REPORT, EMPTY_SYSTEM_ERRORS);
    Files.writeString(changedStderr.stderrLog().path(), "changed", StandardCharsets.UTF_8);
    reject(changedStderr, "validator stderr SHA256 mismatch");
  }

  @Test
  public void rejectsKnownSkippedValidatorsAndMissingCompletionMarker() throws IOException {
    reject(fixture(VALID_REPORT, EMPTY_SYSTEM_ERRORS, "", ""), "completion marker");
    reject(
        fixture(VALID_REPORT, EMPTY_SYSTEM_ERRORS, SKIP_WARNING, "Validation took 0.123 seconds"),
        "skipped rules");
    reject(
        fixture(
            VALID_REPORT,
            EMPTY_SYSTEM_ERRORS,
            "",
            SKIP_WARNING + "\nValidation took 0.123 seconds"),
        "skipped rules");
  }

  @Test
  public void rejectsSystemErrorsAndNonzeroExitCode() throws IOException {
    reject(fixture(VALID_REPORT, "{\"notices\":[{\"message\":\"failure\"}]}"), "notices");
    reject(fixture(VALID_REPORT, "{\"notices\":[],\"fatal\":\"failure\"}"), "nonempty field");
    InputSpec input = fixture(VALID_REPORT, EMPTY_SYSTEM_ERRORS);
    reject(
        new InputSpec(
            input.sourceId(),
            input.observedAtUtc(),
            input.provenanceId(),
            input.validatorVersion(),
            input.validationDate(),
            input.countryCode(),
            input.validationOptions(),
            1,
            input.feedZip(),
            input.validatorJar(),
            input.reportJson(),
            input.systemErrorsJson(),
            input.stdoutLog(),
            input.stderrLog()),
        "exit code");
  }

  @Test
  public void rejectsMalformedCountsAndSamples() throws IOException {
    reject(
        fixture(
            VALID_REPORT.replace("\"totalNotices\":3", "\"totalNotices\":3.0"),
            EMPTY_SYSTEM_ERRORS),
        "totalNotices");
    reject(
        fixture(
            VALID_REPORT.replace("\"totalNotices\":3", "\"totalNotices\":3e0"),
            EMPTY_SYSTEM_ERRORS),
        "totalNotices");
    reject(
        fixture(
            VALID_REPORT.replace(
                "\"sampleNotices\":[{\"csvRowNumber\":2}]", "\"sampleNotices\":[2]"),
            EMPTY_SYSTEM_ERRORS),
        "sampleNotices");
  }

  @Test
  public void requiresExplicitOptionsAndProvenance() throws IOException {
    InputSpec input = fixture(VALID_REPORT, EMPTY_SYSTEM_ERRORS);
    try {
      new InputSpec(
          input.sourceId(),
          input.observedAtUtc(),
          input.provenanceId(),
          input.validatorVersion(),
          input.validationDate(),
          input.countryCode(),
          Map.of(),
          input.exitCode(),
          input.feedZip(),
          input.validatorJar(),
          input.reportJson(),
          input.systemErrorsJson(),
          input.stdoutLog(),
          input.stderrLog());
      fail("empty validationOptions should be rejected");
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessageThat().contains("validationOptions");
    }
    try {
      new InputSpec(
          input.sourceId(),
          input.observedAtUtc(),
          " ",
          input.validatorVersion(),
          input.validationDate(),
          input.countryCode(),
          input.validationOptions(),
          input.exitCode(),
          input.feedZip(),
          input.validatorJar(),
          input.reportJson(),
          input.systemErrorsJson(),
          input.stdoutLog(),
          input.stderrLog());
      fail("blank provenanceId should be rejected");
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessageThat().contains("provenanceId");
    }
  }

  private InputSpec fixture(String report, String systemErrors) throws IOException {
    return fixture(report, systemErrors, "", "Validation took 0.123 seconds");
  }

  private static InputSpec withOptions(InputSpec input, Map<String, String> options) {
    return new InputSpec(
        input.sourceId(),
        input.observedAtUtc(),
        input.provenanceId(),
        input.validatorVersion(),
        input.validationDate(),
        input.countryCode(),
        options,
        input.exitCode(),
        input.feedZip(),
        input.validatorJar(),
        input.reportJson(),
        input.systemErrorsJson(),
        input.stdoutLog(),
        input.stderrLog());
  }

  private InputSpec fixture(String report, String systemErrors, String stdout, String stderr)
      throws IOException {
    Path directory = folder.newFolder().toPath();
    return new InputSpec(
        "synthetic-feed",
        "2026-09-25T12:00:00Z",
        "synthetic-provenance",
        "8.0.1",
        "2026-09-25",
        "HU",
        Map.of("-c", "HU", "-d", "2026-09-25", "-svu", "true"),
        0,
        file(directory, "feed.zip", "synthetic GTFS ZIP"),
        file(directory, "validator.jar", "synthetic JAR"),
        file(directory, "report.json", report),
        file(directory, "system_errors.json", systemErrors),
        file(directory, "validator.stdout.log", stdout),
        file(directory, "validator.stderr.log", stderr));
  }

  private FileRef file(Path directory, String name, String content) throws IOException {
    Path path = directory.resolve(name);
    byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
    Files.write(path, bytes);
    try {
      return new FileRef(
          path, HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)));
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException(exception);
    }
  }

  private void reject(InputSpec input, String expectedMessage) throws IOException {
    try {
      loader.load(input);
      fail("invalid input should be rejected");
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessageThat().contains(expectedMessage);
    }
  }
}
