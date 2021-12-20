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
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

  private static final String NEW_NOTICES_TYPE_FOLDER_NAME = "new-notices-type";
  private static final String REFERENCE_JSON = "reference.json";
  private static final String LATEST_JSON = "latest.json";
  private static final String ACCEPTANCE_REPORT_JSON = "acceptance_report.json";
  private static final String NO_NEW_NOTICE_FOLDER_NAME = "no-new-notice";
  private static final String GTFS_LATEST_VERSIONS_JSON = "gtfs_latest_versions.json";
  private static final String ACCEPTANCE_TEST_REPORT_FOLDER_NAME = "acceptance-test-report";
  private static final String SOURCE_INFO_FOLDER_NAME = "source-info";
  @Rule public final TemporaryFolder tmpDir = new TemporaryFolder();

  private static final Gson GSON =
      new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

  private void writeFile(JsonObject fileData, String folderName, String sourceInfo, String filename)
      throws IOException {
    if (!Files.exists(tmpDir.getRoot().toPath().resolve(folderName).resolve(sourceInfo))) {
      tmpDir.newFolder(folderName, sourceInfo);
    }
    Files.write(
        tmpDir.getRoot().toPath().resolve(folderName).resolve(sourceInfo).resolve(filename),
        GSON.toJson(fileData).getBytes(StandardCharsets.UTF_8));
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
        NO_NEW_NOTICE_FOLDER_NAME,
        "source-id-1",
        LATEST_JSON);
    writeFile(
        referenceNoticeContainer.exportJson(referenceNoticeContainer.getValidationNotices()),
        NO_NEW_NOTICE_FOLDER_NAME,
        "source-id-1",
        REFERENCE_JSON);

    JsonObject sourceUrlJsonObject = new JsonObject();
    sourceUrlJsonObject.addProperty("source-id-1", "url1");
    sourceUrlJsonObject.addProperty("source-id-2", "url2");
    sourceUrlJsonObject.addProperty("source-id-3", "url3");
    writeFile(sourceUrlJsonObject, SOURCE_INFO_FOLDER_NAME, "all", GTFS_LATEST_VERSIONS_JSON);

    String[] argv = {
      "--report_directory",
      tmpDir.getRoot().toPath().resolve(NO_NEW_NOTICE_FOLDER_NAME).toString(),
      "--new_error_threshold",
      "1",
      "--reference_report_name",
      REFERENCE_JSON,
      "--latest_report_name",
      LATEST_JSON,
      "--percent_invalid_datasets_threshold",
      "1",
      "--output_base",
      tmpDir
          .getRoot()
          .toPath()
          .resolve(NO_NEW_NOTICE_FOLDER_NAME)
          .resolve(ACCEPTANCE_TEST_REPORT_FOLDER_NAME)
          .toString(),
      "--source_urls",
      tmpDir
          .getRoot()
          .toPath()
          .resolve(SOURCE_INFO_FOLDER_NAME)
          .resolve("all")
          .resolve(GTFS_LATEST_VERSIONS_JSON)
          .toString(),
    };
    Main.main(argv);
    assertThat(
            retrieveAcceptanceReportString(
                tmpDir
                    .getRoot()
                    .toPath()
                    .resolve(NO_NEW_NOTICE_FOLDER_NAME)
                    .resolve(ACCEPTANCE_TEST_REPORT_FOLDER_NAME)
                    .resolve(ACCEPTANCE_REPORT_JSON)))
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
        NEW_NOTICES_TYPE_FOLDER_NAME,
        "source-id-1",
        LATEST_JSON);
    writeFile(
        referenceNoticeContainer.exportJson(referenceNoticeContainer.getValidationNotices()),
        NEW_NOTICES_TYPE_FOLDER_NAME,
        "source-id-1",
        REFERENCE_JSON);

    latestNoticeContainer.addValidationNotice(
        new InvalidEmailNotice("filename", 4, "field name", "field value"));
    latestNoticeContainer.addValidationNotice(
        new DuplicateKeyNotice("filename", 8, 10, "field name 1", "field value1"));

    writeFile(
        latestNoticeContainer.exportJson(latestNoticeContainer.getValidationNotices()),
        NEW_NOTICES_TYPE_FOLDER_NAME,
        "source-id-2",
        LATEST_JSON);
    writeFile(
        referenceNoticeContainer.exportJson(referenceNoticeContainer.getValidationNotices()),
        NEW_NOTICES_TYPE_FOLDER_NAME,
        "source-id-2",
        REFERENCE_JSON);

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
        NEW_NOTICES_TYPE_FOLDER_NAME,
        "source-id-3",
        LATEST_JSON);
    writeFile(
        referenceNoticeContainer.exportJson(referenceNoticeContainer.getValidationNotices()),
        NEW_NOTICES_TYPE_FOLDER_NAME,
        "source-id-3",
        REFERENCE_JSON);

    JsonObject sourceUrlJsonObject = new JsonObject();
    sourceUrlJsonObject.addProperty("source-id-1", "url1");
    sourceUrlJsonObject.addProperty("source-id-2", "url2");
    sourceUrlJsonObject.addProperty("source-id-3", "url3");
    writeFile(sourceUrlJsonObject, SOURCE_INFO_FOLDER_NAME, "all", GTFS_LATEST_VERSIONS_JSON);

    String[] argv = {
      "--report_directory",
      tmpDir.getRoot().toPath().resolve(NEW_NOTICES_TYPE_FOLDER_NAME).toString(),
      "--new_error_threshold",
      "1",
      "--reference_report_name",
      REFERENCE_JSON,
      "--latest_report_name",
      LATEST_JSON,
      "--percent_invalid_datasets_threshold",
      "1",
      "--output_base",
      tmpDir
          .getRoot()
          .toPath()
          .resolve(NEW_NOTICES_TYPE_FOLDER_NAME)
          .resolve(ACCEPTANCE_TEST_REPORT_FOLDER_NAME)
          .toString(),
      "--source_urls",
      tmpDir
          .getRoot()
          .toPath()
          .resolve(SOURCE_INFO_FOLDER_NAME)
          .resolve("all")
          .resolve(GTFS_LATEST_VERSIONS_JSON)
          .toString(),
    };

    SystemLambda.catchSystemExit(() -> Main.main(argv));

    assertThat(
            retrieveAcceptanceReportString(
                tmpDir
                    .getRoot()
                    .toPath()
                    .resolve(NEW_NOTICES_TYPE_FOLDER_NAME)
                    .resolve(ACCEPTANCE_TEST_REPORT_FOLDER_NAME)
                    .resolve(ACCEPTANCE_REPORT_JSON)))
        .isEqualTo(
            "{\"newErrors\":[{\"noticeCode\":\"duplicate_key\",\"affectedSourcesCount"
                + "\":2,\"affectedSources\":[{\"sourceId\":\"source-id-2\",\"sourceUrl\":\"url2"
                + "\"},{\"sourceId\":\"source-id-3\",\"sourceUrl\":\"url3\"}]},{\"noticeCode\":"
                + "\"invalid_currency\",\"affectedSourcesCount\":1,\"affectedSources\":[{"
                + "\"sourceId\":\"source-id-3\",\"sourceUrl\":\"url3\"}]},{\"noticeCode\":"
                + "\"invalid_email\",\"affectedSourcesCount\":2,\"affectedSources\":[{\"sourceId"
                + "\":\"source-id-2\",\"sourceUrl\":\"url2\"},{\"sourceId\":\"source-id-3\","
                + "\"sourceUrl\":\"url3\"}]},{\"noticeCode\":\"point_near_pole\","
                + "\"affectedSourcesCount\":1,\"affectedSources\":[{\"sourceId\":\"source-id-3\","
                + "\"sourceUrl\":\"url3\"}]}]}");
  }

  @Test
  public void acceptanceReportTestShouldBeExported() {
    JsonObject reportData = new JsonObject();
    reportData.addProperty("newErrors", "sample string value");
    Main.exportAcceptanceTestReport(reportData, tmpDir.getRoot().toString());
    assertThat(tmpDir.getRoot().toPath().resolve(ACCEPTANCE_REPORT_JSON).toFile().exists())
        .isTrue();
  }
}
