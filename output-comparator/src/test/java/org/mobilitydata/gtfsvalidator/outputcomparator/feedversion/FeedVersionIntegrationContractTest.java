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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mobilitydata.gtfsvalidator.outputcomparator.feedversion.FeedVersionDiff.Status;
import org.mobilitydata.gtfsvalidator.outputcomparator.feedversion.VerifiedReportLoader.FileRef;
import org.mobilitydata.gtfsvalidator.outputcomparator.feedversion.VerifiedReportLoader.InputSpec;
import org.mobilitydata.gtfsvalidator.outputcomparator.feedversion.VerifiedReportLoader.LoadedRun;

/** Synthetic capture examples; the placeholder JAR is never executed. */
public class FeedVersionIntegrationContractTest {
  private static final String ROUTES = "route_id,route_short_name\nR1,10\n";
  private static final Map<String, String> OPTIONS = Map.of("-svu", "true");
  @Rule public final TemporaryFolder folder = new TemporaryFolder();
  private final VerifiedReportLoader loader = new VerifiedReportLoader();
  private final FeedVersionReportComparator comparator = new FeedVersionReportComparator();

  @Test
  public void changedGtfsValuesWithEqualCountsAreNotTheSameFeed() throws IOException {
    byte[] beforeZip = zip(ROUTES, "publication");
    byte[] afterZip = zip(ROUTES.replace("R1,10", "R1,11"), "publication");
    assertThat(memberBytes(beforeZip)).isNotEqualTo(memberBytes(afterZip));
    LoadedRun before = load(beforeZip, "2026-10-01", OPTIONS, 3);
    LoadedRun after = load(afterZip, "2026-10-01", OPTIONS, 3);

    FeedVersionDiff diff = compare(before, after);

    assertThat(diff.status()).isEqualTo(Status.COMPARABLE);
    assertThat(diff.changes()).isEmpty();
    assertThat(diff.before().feedSha256()).isNotEqualTo(diff.after().feedSha256());
  }

  @Test
  public void repackagedZipIsStillDifferentInputEvenWithIdenticalMemberBytes() throws IOException {
    byte[] beforeZip = zip(ROUTES, "original packaging");
    byte[] afterZip = zip(ROUTES, "new packaging");
    assertThat(memberBytes(beforeZip)).isEqualTo(memberBytes(afterZip));
    LoadedRun before = load(beforeZip, "2026-10-01", OPTIONS, 3);
    LoadedRun after = load(afterZip, "2026-10-01", OPTIONS, 3);

    FeedVersionDiff diff = compare(before, after);

    assertThat(diff.status()).isEqualTo(Status.COMPARABLE);
    assertThat(diff.changes()).isEmpty();
    assertThat(diff.before().feedSha256()).isNotEqualTo(diff.after().feedSha256());
    JsonObject json = JsonParser.parseString(diff.toJson()).getAsJsonObject();
    assertThat(json.getAsJsonObject("before").get("feedSha256").getAsString())
        .isEqualTo(before.metadata().feedSha256());
    assertThat(json.getAsJsonObject("after").get("feedSha256").getAsString())
        .isEqualTo(after.metadata().feedSha256());
    assertThat(json.has("contentEquivalent")).isFalse();
  }

  @Test
  public void publicationSpecificDatesDoNotBecomeControlledComparison() throws IOException {
    LoadedRun before = load(zip(ROUTES, "A"), "2026-09-01", OPTIONS, 3);
    LoadedRun after = load(zip(ROUTES, "B"), "2026-10-01", OPTIONS, 5);

    FeedVersionDiff diff = compare(before, after);

    assertThat(diff.status()).isEqualTo(Status.INCOMPATIBLE_METHOD);
    assertThat(diff.reasons()).containsExactly("validationDate");
    assertThat(diff.changes()).isEmpty();
    assertThat(diff.diagnosticChanges()).isEmpty();
  }

  @Test
  public void callerCanRequireMatchingRuntimeContext() throws IOException {
    Map<String, String> linux =
        Map.of("-svu", "true", "context.os", "Linux", "context.arch", "x86_64");
    Map<String, String> windows =
        Map.of("-svu", "true", "context.os", "Windows", "context.arch", "x86_64");
    LoadedRun before = load(zip(ROUTES, "A"), "2026-10-01", linux, 3);
    LoadedRun matching = load(zip(ROUTES, "B"), "2026-10-01", linux, 5);
    LoadedRun different = load(zip(ROUTES, "B"), "2026-10-01", windows, 5);
    assertThat(compare(before, matching).status()).isEqualTo(Status.COMPARABLE);

    FeedVersionDiff diff = compare(before, different);

    assertThat(diff.status()).isEqualTo(Status.INCOMPATIBLE_METHOD);
    assertThat(diff.reasons()).containsExactly("validationOptions");
    assertThat(diff.changes()).isEmpty();
  }

  @Test
  public void repeatedZipKeepsCountDifferencesInDiagnostics() throws IOException {
    byte[] sameZip = zip(ROUTES, "same bytes");
    LoadedRun before = load(sameZip, "2026-10-01", OPTIONS, 3);
    LoadedRun after = load(sameZip, "2026-10-01", OPTIONS, 5);

    FeedVersionDiff diff = compare(before, after);

    assertThat(diff.status()).isEqualTo(Status.SAME_FEED);
    assertThat(diff.changes()).isEmpty();
    assertThat(diff.diagnosticChanges()).hasSize(1);
    assertThat(diff.diagnosticChanges().get(0).delta()).isEqualTo(2);
  }

  private FeedVersionDiff compare(LoadedRun before, LoadedRun after) {
    return comparator.compare(before.report(), before.metadata(), after.report(), after.metadata());
  }

  private LoadedRun load(byte[] zip, String date, Map<String, String> options, int count)
      throws IOException {
    Path directory = folder.newFolder().toPath();
    String report =
        """
        {"summary":{"validatorVersion":"8.0.1","dateForValidation":"%s",\
        "countryCode":"HU","threads":1},"notices":[{"code":"example_rule",\
        "severity":"WARNING","totalNotices":%d,"sampleNotices":[]}]}
        """
            .formatted(date, count);
    return loader.load(
        new InputSpec(
            "synthetic-feed",
            "2026-10-01T00:00:00Z",
            "synthetic-capture",
            "8.0.1",
            date,
            "HU",
            options,
            0,
            file(directory, "feed.zip", zip),
            file(
                directory,
                "validator.jar",
                "synthetic placeholder JAR".getBytes(StandardCharsets.UTF_8)),
            file(directory, "report.json", report.getBytes(StandardCharsets.UTF_8)),
            file(
                directory,
                "system_errors.json",
                "{\"notices\":[]}".getBytes(StandardCharsets.UTF_8)),
            file(directory, "stdout.log", new byte[0]),
            file(
                directory,
                "stderr.log",
                "Validation took 0.001 seconds".getBytes(StandardCharsets.UTF_8))));
  }

  private static FileRef file(Path directory, String name, byte[] bytes) throws IOException {
    Path path = directory.resolve(name);
    Files.write(path, bytes);
    try {
      return new FileRef(
          path, HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)));
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException(exception);
    }
  }

  private static byte[] zip(String routes, String comment) throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (ZipOutputStream zip = new ZipOutputStream(bytes)) {
      zip.setComment(comment);
      ZipEntry entry = new ZipEntry("routes.txt");
      entry.setTime(0);
      zip.putNextEntry(entry);
      zip.write(routes.getBytes(StandardCharsets.UTF_8));
      zip.closeEntry();
    }
    return bytes.toByteArray();
  }

  private static byte[] memberBytes(byte[] zip) throws IOException {
    try (ZipInputStream input = new ZipInputStream(new ByteArrayInputStream(zip))) {
      assertThat(input.getNextEntry().getName()).isEqualTo("routes.txt");
      return input.readAllBytes();
    }
  }
}
