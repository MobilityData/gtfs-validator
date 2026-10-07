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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mobilitydata.gtfsvalidator.model.NoticeReport;
import org.mobilitydata.gtfsvalidator.model.ValidationReport;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.outputcomparator.feedversion.RunMetadata.Evidence;

/**
 * Loads one raw validator report after checking its input files and method metadata. The caller
 * supplies provenance and the complete set of result-affecting CLI options; neither can be inferred
 * from report JSON. This loader checks that a provenance ID is present, but cannot authenticate it
 * or establish that the supplied option map is exhaustive. Captured output logs are checked for the
 * known v8.0.1 warning about validators skipped after GTFS parsing errors; this does not prove that
 * every validator ran.
 */
public final class VerifiedReportLoader {
  /** A file and the digest recorded independently when the validation run was captured. */
  public record FileRef(Path path, String expectedSha256) {
    public FileRef {
      if (path == null || expectedSha256 == null || !expectedSha256.matches("[0-9a-f]{64}")) {
        throw new IllegalArgumentException("file path and lowercase expected SHA256 are required");
      }
    }
  }

  /** Explicit run facts; this is independent of any local manifest format. */
  public record InputSpec(
      String sourceId,
      String observedAtUtc,
      String provenanceId,
      String validatorVersion,
      String validationDate,
      String countryCode,
      Map<String, String> validationOptions,
      int exitCode,
      FileRef feedZip,
      FileRef validatorJar,
      FileRef reportJson,
      FileRef systemErrorsJson,
      FileRef stdoutLog,
      FileRef stderrLog) {
    public InputSpec {
      requireText(sourceId, "sourceId");
      requireText(observedAtUtc, "observedAtUtc");
      requireText(provenanceId, "provenanceId");
      requireText(validatorVersion, "validatorVersion");
      requireText(validationDate, "validationDate");
      requireText(countryCode, "countryCode");
      if (feedZip == null
          || validatorJar == null
          || reportJson == null
          || systemErrorsJson == null
          || stdoutLog == null
          || stderrLog == null) {
        throw new IllegalArgumentException("all six file references are required");
      }
      if (validationOptions == null || validationOptions.isEmpty()) {
        throw new IllegalArgumentException("explicit validationOptions are required");
      }
      for (Map.Entry<String, String> option : validationOptions.entrySet()) {
        requireText(option.getKey(), "validationOptions key");
        requireText(option.getValue(), "validationOptions value");
      }
      validationOptions = Map.copyOf(validationOptions);
      try {
        if (!OffsetDateTime.parse(observedAtUtc).getOffset().equals(ZoneOffset.UTC)) {
          throw new IllegalArgumentException("observedAtUtc must be UTC");
        }
        if (!LocalDate.parse(validationDate).toString().equals(validationDate)) {
          throw new IllegalArgumentException("validationDate must use ISO format");
        }
      } catch (DateTimeParseException exception) {
        throw new IllegalArgumentException("invalid observedAtUtc or validationDate", exception);
      }
    }
  }

  public record LoadedRun(ValidationReport report, RunMetadata metadata) {}

  public LoadedRun load(InputSpec input) throws IOException {
    if (input == null) {
      throw new IllegalArgumentException("InputSpec is required");
    }
    if (input.exitCode() != 0) {
      throw new IllegalArgumentException("validator exit code must be zero");
    }

    String feedSha = checkedFileHash(input.feedZip(), "feed ZIP");
    String validatorSha = checkedFileHash(input.validatorJar(), "validator JAR");
    byte[] reportBytes = Files.readAllBytes(input.reportJson().path());
    String reportSha = checkedBytesHash(reportBytes, input.reportJson(), "report.json");
    byte[] systemBytes = Files.readAllBytes(input.systemErrorsJson().path());
    String systemSha =
        checkedBytesHash(systemBytes, input.systemErrorsJson(), "system_errors.json");
    byte[] stdoutBytes = Files.readAllBytes(input.stdoutLog().path());
    checkedBytesHash(stdoutBytes, input.stdoutLog(), "validator stdout");
    byte[] stderrBytes = Files.readAllBytes(input.stderrLog().path());
    checkedBytesHash(stderrBytes, input.stderrLog(), "validator stderr");

    JsonObject reportJson = parseObject(reportBytes, "report.json");
    JsonObject summary = objectMember(reportJson, "summary", "report.json");
    checkSummary(summary, input);
    List<ValidatedGroup> groups = checkGroups(reportJson);
    checkSystemErrors(parseObject(systemBytes, "system_errors.json"));
    checkValidatorLogs(stdoutBytes, stderrBytes);

    Set<NoticeReport> notices = new LinkedHashSet<>();
    for (ValidatedGroup group : groups) {
      notices.add(new NoticeReport(group.code(), group.severity(), group.total(), group.samples()));
    }
    ValidationReport report = new ValidationReport(notices);
    Evidence evidence = new Evidence(input.provenanceId(), true, true, true, true, true, true);
    RunMetadata metadata =
        new RunMetadata(
            input.sourceId(),
            input.observedAtUtc(),
            feedSha,
            input.validatorVersion(),
            validatorSha,
            reportSha,
            systemSha,
            input.validationDate(),
            input.countryCode(),
            input.validationOptions(),
            true,
            true,
            evidence);
    return new LoadedRun(report, metadata);
  }

  private static String checkedFileHash(FileRef ref, String label) throws IOException {
    MessageDigest digest = digest();
    try (InputStream stream = Files.newInputStream(ref.path())) {
      byte[] buffer = new byte[8192];
      int read;
      while ((read = stream.read(buffer)) != -1) {
        digest.update(buffer, 0, read);
      }
    }
    String actual = HexFormat.of().formatHex(digest.digest());
    if (!actual.equals(ref.expectedSha256())) {
      throw new IllegalArgumentException(label + " SHA256 mismatch");
    }
    return actual;
  }

  private static String checkedBytesHash(byte[] bytes, FileRef ref, String label) {
    String actual = HexFormat.of().formatHex(digest().digest(bytes));
    if (!actual.equals(ref.expectedSha256())) {
      throw new IllegalArgumentException(label + " SHA256 mismatch");
    }
    return actual;
  }

  private static MessageDigest digest() {
    try {
      return MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("SHA-256 is unavailable", exception);
    }
  }

  private static JsonObject parseObject(byte[] bytes, String label) {
    try {
      String json = StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(bytes)).toString();
      JsonReader reader = new JsonReader(new StringReader(json));
      JsonElement parsed = readJsonValue(reader);
      if (reader.peek() != JsonToken.END_DOCUMENT) {
        throw new IllegalArgumentException(label + " has trailing content");
      }
      if (parsed.isJsonObject()) {
        return parsed.getAsJsonObject();
      }
    } catch (IOException | NumberFormatException | IllegalStateException exception) {
      throw new IllegalArgumentException(label + " is malformed", exception);
    }
    throw new IllegalArgumentException(label + " must be an object");
  }

  private static JsonElement readJsonValue(JsonReader reader) throws IOException {
    return switch (reader.peek()) {
      case BEGIN_OBJECT -> {
        JsonObject object = new JsonObject();
        Set<String> names = new HashSet<>();
        reader.beginObject();
        while (reader.hasNext()) {
          String name = reader.nextName();
          if (!names.add(name)) {
            throw new IllegalArgumentException("duplicate JSON key");
          }
          object.add(name, readJsonValue(reader));
        }
        reader.endObject();
        yield object;
      }
      case BEGIN_ARRAY -> {
        JsonArray array = new JsonArray();
        reader.beginArray();
        while (reader.hasNext()) {
          array.add(readJsonValue(reader));
        }
        reader.endArray();
        yield array;
      }
      case STRING -> new JsonPrimitive(reader.nextString());
      case NUMBER -> new JsonPrimitive(new RawJsonNumber(reader.nextString()));
      case BOOLEAN -> new JsonPrimitive(reader.nextBoolean());
      case NULL -> {
        reader.nextNull();
        yield JsonNull.INSTANCE;
      }
      default -> throw new IllegalArgumentException("unexpected JSON token");
    };
  }

  private static JsonObject objectMember(JsonObject parent, String key, String label) {
    JsonElement member = parent.get(key);
    if (member == null || !member.isJsonObject()) {
      throw new IllegalArgumentException(label + "." + key + " must be an object");
    }
    return member.getAsJsonObject();
  }

  private static void checkSummary(JsonObject summary, InputSpec input) {
    checkStringMember(summary, "validatorVersion", input.validatorVersion(), "summary");
    checkStringMember(summary, "dateForValidation", input.validationDate(), "summary");
    checkStringMember(summary, "countryCode", input.countryCode(), "summary");
    checkThreads(summary, input.validationOptions());
  }

  private static void checkThreads(JsonObject summary, Map<String, String> options) {
    JsonElement raw = summary.get("threads");
    if (raw == null
        || !raw.isJsonPrimitive()
        || !raw.getAsJsonPrimitive().isNumber()
        || !raw.getAsString().matches("[1-9][0-9]*")) {
      throw new IllegalArgumentException("summary.threads must be a positive integer");
    }
    String shortOption = options.get("-t");
    String longOption = options.get("--threads");
    if (shortOption != null && longOption != null && !shortOption.equals(longOption)) {
      throw new IllegalArgumentException("conflicting thread options");
    }
    String requested = longOption != null ? longOption : shortOption;
    int expected = requested == null ? 1 : positiveInt(requested, "thread option");
    if (positiveInt(raw.getAsString(), "summary.threads") != expected) {
      throw new IllegalArgumentException("summary.threads disagrees with InputSpec");
    }
  }

  private static int positiveInt(String value, String label) {
    if (!value.matches("[1-9][0-9]*")) {
      throw new IllegalArgumentException(label + " must be a positive integer");
    }
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException exception) {
      throw new IllegalArgumentException(label + " exceeds the integer range", exception);
    }
  }

  private static void checkStringMember(
      JsonObject object, String key, String expected, String label) {
    JsonElement value = object.get(key);
    if (value == null
        || !value.isJsonPrimitive()
        || !value.getAsJsonPrimitive().isString()
        || !expected.equals(value.getAsString())) {
      throw new IllegalArgumentException(label + "." + key + " disagrees with InputSpec");
    }
  }

  private static List<ValidatedGroup> checkGroups(JsonObject report) {
    JsonElement raw = report.get("notices");
    if (raw == null || !raw.isJsonArray()) {
      throw new IllegalArgumentException("report.json.notices must be an array");
    }
    List<ValidatedGroup> groups = new ArrayList<>();
    Set<GroupKey> seen = new HashSet<>();
    for (JsonElement element : raw.getAsJsonArray()) {
      if (!element.isJsonObject()) {
        throw new IllegalArgumentException("report.json notice must be an object");
      }
      JsonObject notice = element.getAsJsonObject();
      String code = nonemptyStringMember(notice, "code");
      if (!code.matches("[a-z][a-z0-9_]{0,127}")) {
        throw new IllegalArgumentException("notice code must be lowercase snake case");
      }
      String severityText = nonemptyStringMember(notice, "severity");
      SeverityLevel severity;
      try {
        severity = SeverityLevel.valueOf(severityText);
      } catch (IllegalArgumentException exception) {
        throw new IllegalArgumentException("unsupported notice severity", exception);
      }
      JsonElement totalElement = notice.get("totalNotices");
      if (totalElement == null
          || !totalElement.isJsonPrimitive()
          || !totalElement.getAsJsonPrimitive().isNumber()
          || !totalElement.getAsString().matches("[1-9][0-9]*")) {
        throw new IllegalArgumentException("totalNotices must be a positive integer");
      }
      int total;
      try {
        total = Integer.parseInt(totalElement.getAsString());
      } catch (NumberFormatException exception) {
        throw new IllegalArgumentException("totalNotices exceeds the model limit", exception);
      }
      JsonElement sampleElement = notice.get("sampleNotices");
      if (sampleElement == null || !sampleElement.isJsonArray()) {
        throw new IllegalArgumentException("sampleNotices must be an array of objects");
      }
      JsonArray rawSamples = sampleElement.getAsJsonArray();
      if (rawSamples.size() > total) {
        throw new IllegalArgumentException("sampleNotices exceeds totalNotices");
      }
      List<JsonElement> samples = new ArrayList<>();
      for (JsonElement sample : rawSamples) {
        if (!sample.isJsonObject()) {
          throw new IllegalArgumentException("sampleNotices must be an array of objects");
        }
        samples.add(sample);
      }
      if (!seen.add(new GroupKey(code, severity))) {
        throw new IllegalArgumentException("duplicate notice group");
      }
      groups.add(new ValidatedGroup(code, severity, total, samples));
    }
    return groups;
  }

  private static String nonemptyStringMember(JsonObject object, String key) {
    JsonElement value = object.get(key);
    if (value == null
        || !value.isJsonPrimitive()
        || !value.getAsJsonPrimitive().isString()
        || value.getAsString().isBlank()) {
      throw new IllegalArgumentException(key + " must be a nonempty string");
    }
    return value.getAsString();
  }

  private static void checkSystemErrors(JsonObject system) {
    JsonElement notices = system.get("notices");
    if (notices == null || !notices.isJsonArray() || notices.getAsJsonArray().size() != 0) {
      throw new IllegalArgumentException("system_errors.json notices must be an empty array");
    }
    for (Map.Entry<String, JsonElement> field : system.entrySet()) {
      if (!field.getKey().equals("notices") && !isEmptyValue(field.getValue())) {
        throw new IllegalArgumentException("system_errors.json has a nonempty field");
      }
    }
  }

  private static void checkValidatorLogs(byte[] stdoutBytes, byte[] stderrBytes) {
    String skippedWarning =
        "Some validators were skipped because the GTFS files they rely on could not be parsed";
    if (containsAscii(stdoutBytes, skippedWarning) || containsAscii(stderrBytes, skippedWarning)) {
      throw new IllegalArgumentException("validator skipped rules after GTFS parsing errors");
    }
    if (!containsAscii(stdoutBytes, "Validation took ")
        && !containsAscii(stderrBytes, "Validation took ")) {
      throw new IllegalArgumentException("validator completion marker is missing from output logs");
    }
  }

  private static boolean containsAscii(byte[] input, String pattern) {
    byte[] target = pattern.getBytes(StandardCharsets.US_ASCII);
    outer:
    for (int start = 0; start <= input.length - target.length; start++) {
      for (int position = 0; position < target.length; position++) {
        if (input[start + position] != target[position]) {
          continue outer;
        }
      }
      return true;
    }
    return false;
  }

  private static boolean isEmptyValue(JsonElement value) {
    if (value == null || value.isJsonNull()) {
      return true;
    }
    if (value.isJsonArray()) {
      return value.getAsJsonArray().size() == 0;
    }
    if (value.isJsonObject()) {
      return value.getAsJsonObject().size() == 0;
    }
    if (value.getAsJsonPrimitive().isBoolean()) {
      return !value.getAsBoolean();
    }
    if (value.getAsJsonPrimitive().isNumber()) {
      return value.getAsDouble() == 0.0;
    }
    return value.getAsString().isEmpty();
  }

  private static void requireText(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(field + " is required");
    }
  }

  /** Keeps the JSON number spelling so integer-only report fields cannot accept exponent syntax. */
  private static final class RawJsonNumber extends Number {
    private final String raw;

    private RawJsonNumber(String raw) {
      this.raw = raw;
    }

    @Override
    public int intValue() {
      return new BigDecimal(raw).intValue();
    }

    @Override
    public long longValue() {
      return new BigDecimal(raw).longValue();
    }

    @Override
    public float floatValue() {
      return Float.parseFloat(raw);
    }

    @Override
    public double doubleValue() {
      return Double.parseDouble(raw);
    }

    @Override
    public String toString() {
      return raw;
    }
  }

  private record GroupKey(String code, SeverityLevel severity) {}

  private record ValidatedGroup(
      String code, SeverityLevel severity, int total, List<JsonElement> samples) {}
}
