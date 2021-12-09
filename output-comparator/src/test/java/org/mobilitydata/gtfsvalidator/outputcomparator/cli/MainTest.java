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

package org.mobilitydata.gtfsvalidator.outputcomparator.cli;

import static com.google.common.truth.Truth.assertThat;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.DuplicateKeyNotice;
import org.mobilitydata.gtfsvalidator.notice.EmptyColumnNameNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidCurrencyNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidEmailNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.PointNearPoleNotice;

@RunWith(JUnit4.class)
public class MainTest {
  @Rule
  public final TemporaryFolder tmpDir = new TemporaryFolder();


  private static final Gson GSON =
      new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

  private static void writeFile(JsonObject fileData, String outputBase, String filename)
      throws IOException {
    new File(outputBase).mkdirs();
    Files.write(
        Paths.get(outputBase, filename), GSON.toJson(fileData).getBytes(StandardCharsets.UTF_8));
  }

  private static String retrieveAcceptanceReportString(Path path) throws IOException {
    return Files.readString(path);
  }

  @Test
  public void noNewNoticeType_generatesEmptyReport() throws IOException {
    NoticeContainer referenceNoticeContainer = new NoticeContainer();
    referenceNoticeContainer.addValidationNotice(new MissingRequiredFileNotice("some file"));
    referenceNoticeContainer.addValidationNotice(new EmptyColumnNameNotice("other file", 4));

    NoticeContainer latestNoticeContainer = new NoticeContainer();
    latestNoticeContainer.addValidationNotice(new MissingRequiredFileNotice("sample file"));
    latestNoticeContainer.addValidationNotice(new MissingRequiredFileNotice("other file"));
    latestNoticeContainer.addValidationNotice(new EmptyColumnNameNotice("filename", 5));

    writeFile(
        latestNoticeContainer.exportJson(latestNoticeContainer.getValidationNotices()),
        "src/test/resources/reports/no-new-notices/source-id-1",
        "latest.json");
    writeFile(
        referenceNoticeContainer.exportJson(referenceNoticeContainer.getValidationNotices()),
        "src/test/resources/reports/no-new-notices/source-id-1",
        "reference.json");

    String[] argv = {
      "--report_directory", "src/test/resources/reports/no-new-notices",
      "--new_error_threshold", "1",
      "--reference_report_name", "reference.json",
      "--latest_report_name", "latest.json",
      "--percent_invalid_datasets_threshold", "1",
      "--output_base", "src/test/resources/output/no-new-notices/acceptance-test-report",
      "--source_urls", "src/test/resources/gtfs_latest_versions.json"
    };
    Main.main(argv);
    assertThat(
            retrieveAcceptanceReportString(
                Path.of(
                    "src/test/resources/output/no-new-notices/acceptance-test-report/acceptance_report.json")))
        .isEqualTo("{\"newErrors\":[]}");
  }

  @Test
  public void newNoticeTypes_generatesNonEmptyReport() throws Exception {
    NoticeContainer referenceNoticeContainer = new NoticeContainer();
    referenceNoticeContainer.addValidationNotice(new MissingRequiredFileNotice("some file"));
    referenceNoticeContainer.addValidationNotice(new EmptyColumnNameNotice("other file", 4));

    NoticeContainer latestNoticeContainer = new NoticeContainer();
    latestNoticeContainer.addValidationNotice(new MissingRequiredFileNotice("sample file"));
    latestNoticeContainer.addValidationNotice(new MissingRequiredFileNotice("other file"));
    latestNoticeContainer.addValidationNotice(new EmptyColumnNameNotice("filename", 5));

    writeFile(
        latestNoticeContainer.exportJson(latestNoticeContainer.getValidationNotices()),
        "src/test/resources/reports/new-notices-type/source-id-1",
        "latest.json");
    writeFile(
        referenceNoticeContainer.exportJson(referenceNoticeContainer.getValidationNotices()),
        "src/test/resources/reports/new-notices-type/source-id-1",
        "reference.json");

    latestNoticeContainer.addValidationNotice(
        new InvalidEmailNotice("filename", 4, "field name", "field value"));
    latestNoticeContainer.addValidationNotice(
        new DuplicateKeyNotice("filename", 8, 10, "field name 1", "field value1"));

    writeFile(
        latestNoticeContainer.exportJson(latestNoticeContainer.getValidationNotices()),
        "src/test/resources/reports/new-notices-type/source-id-2",
        "latest.json");
    writeFile(
        referenceNoticeContainer.exportJson(referenceNoticeContainer.getValidationNotices()),
        "src/test/resources/reports/new-notices-type/source-id-2",
        "reference.json");

    latestNoticeContainer.addValidationNotice(
        new InvalidCurrencyNotice("filename", 4, "field name", "field value"));
    latestNoticeContainer.addValidationNotice(
        new PointNearPoleNotice(
            "filename",
            1L,
            "entity id value",
            "latitude field name",
            0,
            "longitude field name",
            0));

    writeFile(
        latestNoticeContainer.exportJson(latestNoticeContainer.getValidationNotices()),
        "src/test/resources/reports/new-notices-type/source-id-3",
        "latest.json");
    writeFile(
        referenceNoticeContainer.exportJson(referenceNoticeContainer.getValidationNotices()),
        "src/test/resources/reports/new-notices-type/source-id-3",
        "reference.json");

    String[] argv = {
      "--report_directory", "src/test/resources/reports/new-notices-type",
      "--new_error_threshold", "1",
      "--reference_report_name", "reference.json",
      "--latest_report_name", "latest.json",
      "--percent_invalid_datasets_threshold", "1",
      "--output_base", "src/test/resources/output/new-notices-type/acceptance-test-report",
      "--source_urls", "src/test/resources/gtfs_latest_versions.json"
    };
    SystemLambda.catchSystemExit(() -> Main.main(argv));
    assertThat(
            retrieveAcceptanceReportString(
                Path.of(
                    "src/test/resources/output/new-notices-type/acceptance-test-report/acceptance_report.json")))
        .isEqualTo(
            "{\"newErrors\":[{\"duplicate_key\":{\"affectedSourcesCount\":2,"
                + "\"affectedSources\":[{\"source_id\":\"source-id-2\",\"source_url\":\"url2\"},{"
                + "\"source_id\":\"source-id-3\",\"source_url\":\"url3\"}],\"countPerSource\":[{"
                + "\"source-id-2\":1},{\"source-id-3\":1}]}},{\"invalid_currency\":{"
                + "\"affectedSourcesCount\":1,\"affectedSources\":[{\"source_id\":\"source-id-3\","
                + "\"source_url\":\"url3\"}],\"countPerSource\":[{\"source-id-3\":1}]}},{"
                + "\"invalid_email\":{\"affectedSourcesCount\":2,\"affectedSources\":[{\"source_id\":"
                + "\"source-id-2\",\"source_url\":\"url2\"},{\"source_id\":\"source-id-3\","
                + "\"source_url\":\"url3\"}],\"countPerSource\":[{\"source-id-2\":1},{\"source-id-3"
                + "\":1}]}},{\"point_near_pole\":{\"affectedSourcesCount\":1,\"affectedSources\":[{"
                + "\"source_id\":\"source-id-3\",\"source_url\":\"url3\"}],\"countPerSource\":[{"
                + "\"source-id-3\":1}]}}]}");
  }

  @Test
  public void acceptanceReportTestShouldBeExported() {
    JsonObject reportData = new JsonObject();
    reportData.addProperty("newErrors", "sample string value");
    Main.exportAcceptanceTestReport(reportData, tmpDir.getRoot().toString());
    assertThat(tmpDir.getRoot().toPath().resolve("acceptance_report.json").toFile().exists()).isTrue();
  }
}
