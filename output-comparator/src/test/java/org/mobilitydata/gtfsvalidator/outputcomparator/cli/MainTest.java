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
import static org.mobilitydata.gtfsvalidator.outputcomparator.cli.Main.ACCEPTANCE_REPORT_JSON;
import static org.mobilitydata.gtfsvalidator.outputcomparator.cli.Main.SOURCES_CORRUPTION_REPORT_JSON;

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
import org.hamcrest.Matchers;
import org.hamcrest.MatcherAssert;
import org.mobilitydata.gtfsvalidator.notice.DuplicateKeyNotice;
import org.mobilitydata.gtfsvalidator.notice.EmptyColumnNameNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidCurrencyNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidEmailNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.PointNearPoleNotice;
import uk.org.webcompere.systemstubs.SystemStubs; // catchSystemExit() for JDK 16 and newer.

@RunWith(JUnit4.class)
public class MainTest {

  private static final String NEW_NOTICES_TYPE_FOLDER_NAME = "new-notices-type";
  private static final String REFERENCE_JSON = "reference.json";
  private static final String LATEST_JSON = "latest.json";
  private static final String NO_NEW_NOTICE_FOLDER_NAME = "no-new-notice";
  private static final String GTFS_LATEST_VERSIONS_JSON = "gtfs_latest_versions.json";
  private static final String ACCEPTANCE_TEST_REPORT_FOLDER_NAME = "acceptance-test-report";
  private static final String SOURCE_INFO_FOLDER_NAME = "source-info";
  @Rule public final TemporaryFolder tmpDir = new TemporaryFolder();

  private static final Gson GSON =
      new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

  private void writeFile(JsonObject fileData, Path path) throws IOException {
    Path parentDir = path.getParent();
    if (!Files.exists(parentDir)) {
      Files.createDirectories(parentDir);
    }
    Files.write(path, GSON.toJson(fileData).getBytes(StandardCharsets.UTF_8));
  }

  private static String retrieveReportString(Path path) throws IOException {
    return Files.readString(path);
  }

  private Path resolve(String... filenames) {
    Path result = tmpDir.getRoot().toPath();
    for (String filename : filenames) {
      result = result.resolve(filename);
    }
    return result;
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
        resolve(NO_NEW_NOTICE_FOLDER_NAME, "source-id-1", LATEST_JSON));
    writeFile(
        referenceNoticeContainer.exportJson(referenceNoticeContainer.getValidationNotices()),
        resolve(NO_NEW_NOTICE_FOLDER_NAME, "source-id-1", REFERENCE_JSON));

    JsonObject sourceUrlJsonObject = new JsonObject();
    sourceUrlJsonObject.addProperty("source-id-1", "url1");
    sourceUrlJsonObject.addProperty("source-id-2", "url2");
    sourceUrlJsonObject.addProperty("source-id-3", "url3");
    writeFile(
        sourceUrlJsonObject, resolve(SOURCE_INFO_FOLDER_NAME, "all", GTFS_LATEST_VERSIONS_JSON));

    String[] argv = {
      "--percent_corrupted_sources",
      "5",
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
      resolve(NO_NEW_NOTICE_FOLDER_NAME, ACCEPTANCE_TEST_REPORT_FOLDER_NAME).toString(),
      "--source_urls",
      resolve(SOURCE_INFO_FOLDER_NAME, "all", GTFS_LATEST_VERSIONS_JSON).toString(),
    };
    Main.main(argv);
    assertThat(
            retrieveReportString(
                resolve(
                    NO_NEW_NOTICE_FOLDER_NAME,
                    ACCEPTANCE_TEST_REPORT_FOLDER_NAME,
                    ACCEPTANCE_REPORT_JSON)))
        .isEqualTo("{\"newErrors\":[]}");
    assertThat(
            retrieveReportString(
                resolve(
                    NO_NEW_NOTICE_FOLDER_NAME,
                    ACCEPTANCE_TEST_REPORT_FOLDER_NAME,
                    SOURCES_CORRUPTION_REPORT_JSON)))
        .isEqualTo(
            "{\"corruptedSources\":[],\"sourceIdCount\":1,\"status\":\"valid\",\"corruptedSourcesCount\":0,"
                + "\"maxPercentageCorruptedSources\":5.0}");
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
        resolve(NEW_NOTICES_TYPE_FOLDER_NAME, "source-id-1", LATEST_JSON));
    writeFile(
        referenceNoticeContainer.exportJson(referenceNoticeContainer.getValidationNotices()),
        resolve(NEW_NOTICES_TYPE_FOLDER_NAME, "source-id-1", REFERENCE_JSON));

    latestNoticeContainer.addValidationNotice(
        new InvalidEmailNotice("filename", 4, "field name", "field value"));
    latestNoticeContainer.addValidationNotice(
        new DuplicateKeyNotice("filename", 8, 10, "field name 1", "field value1"));
    latestNoticeContainer.addValidationNotice(
        new DuplicateKeyNotice("other filename", 9, 11, "field name 1", "field value1"));

    writeFile(
        latestNoticeContainer.exportJson(latestNoticeContainer.getValidationNotices()),
        resolve(NEW_NOTICES_TYPE_FOLDER_NAME, "source-id-2", LATEST_JSON));
    writeFile(
        referenceNoticeContainer.exportJson(referenceNoticeContainer.getValidationNotices()),
        resolve(NEW_NOTICES_TYPE_FOLDER_NAME, "source-id-2", REFERENCE_JSON));

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
    latestNoticeContainer.addValidationNotice(
        new DuplicateKeyNotice("some filename", 9, 11, "field name 1", "field value1"));

    writeFile(
        latestNoticeContainer.exportJson(latestNoticeContainer.getValidationNotices()),
        resolve(NEW_NOTICES_TYPE_FOLDER_NAME, "source-id-3", LATEST_JSON));
    writeFile(
        referenceNoticeContainer.exportJson(referenceNoticeContainer.getValidationNotices()),
        resolve(NEW_NOTICES_TYPE_FOLDER_NAME, "source-id-3", REFERENCE_JSON));

    JsonObject sourceUrlJsonObject = new JsonObject();
    sourceUrlJsonObject.addProperty("source-id-1", "url1");
    sourceUrlJsonObject.addProperty("source-id-2", "url2");
    sourceUrlJsonObject.addProperty("source-id-3", "url3");
    writeFile(
        sourceUrlJsonObject, resolve(SOURCE_INFO_FOLDER_NAME, "all", GTFS_LATEST_VERSIONS_JSON));

    String[] argv = {
      "--percent_corrupted_sources",
      "5",
      "--report_directory",
      resolve(NEW_NOTICES_TYPE_FOLDER_NAME).toString(),
      "--new_error_threshold",
      "1",
      "--reference_report_name",
      REFERENCE_JSON,
      "--latest_report_name",
      LATEST_JSON,
      "--percent_invalid_datasets_threshold",
      "1",
      "--output_base",
      resolve(NEW_NOTICES_TYPE_FOLDER_NAME, ACCEPTANCE_TEST_REPORT_FOLDER_NAME).toString(),
      "--source_urls",
      resolve(SOURCE_INFO_FOLDER_NAME, "all", GTFS_LATEST_VERSIONS_JSON).toString(),
    };

    SystemStubs.catchSystemExit(() -> Main.main(argv));

    assertThat(
            retrieveReportString(
                resolve(
                    NEW_NOTICES_TYPE_FOLDER_NAME,
                    ACCEPTANCE_TEST_REPORT_FOLDER_NAME,
                    ACCEPTANCE_REPORT_JSON)))
        .isEqualTo(
            "{\"newErrors\":[{\"noticeCode\":\"duplicate_key\",\"affectedSourcesCount"
                + "\":2,\"affectedSources\":[{\"sourceId\":\"source-id-2\",\"sourceUrl\":\"url2"
                + "\",\"count\":2},{\"sourceId\":\"source-id-3\",\"sourceUrl\":\"url3\",\"count"
                + "\":3}]},{\"noticeCode\":\"invalid_currency\",\"affectedSourcesCount\":1,"
                + "\"affectedSources\":[{\"sourceId\":\"source-id-3\",\"sourceUrl\":\"url3\","
                + "\"count\":1}]},{\"noticeCode\":\"invalid_email\",\"affectedSourcesCount\":2,"
                + "\"affectedSources\":[{\"sourceId\":\"source-id-2\",\"sourceUrl\":\"url2\","
                + "\"count\":1},{\"sourceId\":\"source-id-3\",\"sourceUrl\":\"url3\",\"count"
                + "\":1}]},{\"noticeCode\":\"point_near_pole\",\"affectedSourcesCount\":1,"
                + "\"affectedSources\":[{\"sourceId\":\"source-id-3\",\"sourceUrl\":\"url3\","
                + "\"count\":1}]}]}");
    assertThat(
            retrieveReportString(
                resolve(
                    NEW_NOTICES_TYPE_FOLDER_NAME,
                    ACCEPTANCE_TEST_REPORT_FOLDER_NAME,
                    SOURCES_CORRUPTION_REPORT_JSON)))
        .isEqualTo(
            "{\"corruptedSources\":[],\"sourceIdCount\":3,\"status\":\"valid\",\"corruptedSourcesCount\":0,"
                + "\"maxPercentageCorruptedSources\":5.0}");
  }

  @Test
  public void acceptanceReportTestShouldBeExported() {
    JsonObject reportData = new JsonObject();
    reportData.addProperty("newErrors", "sample string value");
    Main.exportReport(reportData, tmpDir.getRoot().toString(), ACCEPTANCE_REPORT_JSON);
    assertThat(resolve(ACCEPTANCE_REPORT_JSON).toFile().exists()).isTrue();
  }

  @Test
  public void tooManyCorruptedSource_invalidTest() throws Exception {
    NoticeContainer referenceNoticeContainer = new NoticeContainer();
    referenceNoticeContainer.addValidationNotice(new MissingRequiredFileNotice("some file"));
    referenceNoticeContainer.addValidationNotice(new EmptyColumnNameNotice("other file", 4));

    NoticeContainer latestNoticeContainer = new NoticeContainer();
    latestNoticeContainer.addValidationNotice(new MissingRequiredFileNotice("sample file"));
    latestNoticeContainer.addValidationNotice(new MissingRequiredFileNotice("other file"));
    latestNoticeContainer.addValidationNotice(new EmptyColumnNameNotice("filename", 5));

    writeFile(
        latestNoticeContainer.exportJson(latestNoticeContainer.getValidationNotices()),
        resolve(NEW_NOTICES_TYPE_FOLDER_NAME, "source-id-1", LATEST_JSON));
    writeFile(
        referenceNoticeContainer.exportJson(referenceNoticeContainer.getValidationNotices()),
        resolve(NEW_NOTICES_TYPE_FOLDER_NAME, "source-id-1", REFERENCE_JSON));

    latestNoticeContainer.addValidationNotice(
        new InvalidEmailNotice("filename", 4, "field name", "field value"));
    latestNoticeContainer.addValidationNotice(
        new DuplicateKeyNotice("filename", 8, 10, "field name 1", "field value1"));

    writeFile(
        latestNoticeContainer.exportJson(latestNoticeContainer.getValidationNotices()),
        resolve(NEW_NOTICES_TYPE_FOLDER_NAME, "source-id-2", LATEST_JSON));
    writeFile(
        referenceNoticeContainer.exportJson(referenceNoticeContainer.getValidationNotices()),
        resolve(NEW_NOTICES_TYPE_FOLDER_NAME, "source-id-2", REFERENCE_JSON));

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
        resolve(NEW_NOTICES_TYPE_FOLDER_NAME, "source-id-3", LATEST_JSON));
    writeFile(
        referenceNoticeContainer.exportJson(referenceNoticeContainer.getValidationNotices()),
        resolve(NEW_NOTICES_TYPE_FOLDER_NAME, "source-id-3", REFERENCE_JSON));

    JsonObject sourceUrlJsonObject = new JsonObject();
    sourceUrlJsonObject.addProperty("source-id-1", "url1");
    sourceUrlJsonObject.addProperty("source-id-2", "url2");
    sourceUrlJsonObject.addProperty("source-id-3", "url3");
    sourceUrlJsonObject.addProperty("source-id-4", "url4");
    sourceUrlJsonObject.addProperty("source-id-5", "url5");
    writeFile(
        sourceUrlJsonObject, resolve(SOURCE_INFO_FOLDER_NAME, "all", GTFS_LATEST_VERSIONS_JSON));

    String[] argv = {
      "--percent_corrupted_sources",
      "2",
      "--report_directory",
      resolve(NEW_NOTICES_TYPE_FOLDER_NAME).toString(),
      "--new_error_threshold",
      "1",
      "--reference_report_name",
      REFERENCE_JSON,
      "--latest_report_name",
      LATEST_JSON,
      "--percent_invalid_datasets_threshold",
      "1",
      "--output_base",
      resolve(NEW_NOTICES_TYPE_FOLDER_NAME, ACCEPTANCE_TEST_REPORT_FOLDER_NAME).toString(),
      "--source_urls",
      resolve(SOURCE_INFO_FOLDER_NAME, "all", GTFS_LATEST_VERSIONS_JSON).toString()
    };

    writeFile(
        latestNoticeContainer.exportJson(latestNoticeContainer.getValidationNotices()),
        resolve(NEW_NOTICES_TYPE_FOLDER_NAME, "source-id-4", "invalid_latest.json"));
    writeFile(
        referenceNoticeContainer.exportJson(referenceNoticeContainer.getValidationNotices()),
        resolve(NEW_NOTICES_TYPE_FOLDER_NAME, "source-id-4", "invalid_reference.json"));

    writeFile(
        latestNoticeContainer.exportJson(latestNoticeContainer.getValidationNotices()),
        resolve(NEW_NOTICES_TYPE_FOLDER_NAME, "source-id-5", "latest.json"));

    int exitCode = SystemStubs.catchSystemExit(() -> Main.main(argv));

    assertThat(exitCode).isEqualTo(Main.TOO_MANY_CORRUPTED_SOURCES_EXIT_CODE);
    MatcherAssert.assertThat(
        retrieveReportString(
          resolve(
            NEW_NOTICES_TYPE_FOLDER_NAME,
            ACCEPTANCE_TEST_REPORT_FOLDER_NAME,
            SOURCES_CORRUPTION_REPORT_JSON)),
        Matchers.anyOf(
          Matchers.is(
            "{\"corruptedSources\":[\"source-id-5\",\"source-id-4\"],\"sourceIdCount\":5,"
            + "\"status\":\"invalid\",\"corruptedSourcesCount\":2,"
            + "\"maxPercentageCorruptedSources\":2.0}"),
          Matchers.is(
            "{\"corruptedSources\":[\"source-id-4\",\"source-id-5\"],\"sourceIdCount\":5,"
            + "\"status\":\"invalid\",\"corruptedSourcesCount\":2,"
            + "\"maxPercentageCorruptedSources\":2.0}")));
  }
}
