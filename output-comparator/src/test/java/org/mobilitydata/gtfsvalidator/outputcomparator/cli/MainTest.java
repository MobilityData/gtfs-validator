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
import static org.mobilitydata.gtfsvalidator.outputcomparator.cli.Main.generateAcceptanceTestReport;

import com.google.common.collect.ImmutableSortedMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.outputcomparator.io.NoticeComparisonReport;

@RunWith(JUnit4.class)
public class MainTest {
  private static final Gson GSON =
      new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

  @Rule public final TemporaryFolder tmpDir = new TemporaryFolder();

  private static NoticeComparisonReport createNoticeComparisonReport(
      SortedMap<String, Integer> countPerSource) {
    SortedMap<String, String> affectedSources = new TreeMap<>();
    for (String sourceId : countPerSource.keySet()) {
      affectedSources.put(
          sourceId,
          String.format(
              "https://storage.googleapis.com/storage/v1/b/%s_archives_2021-12-04/o/1234.zip?alt=media",
              sourceId));
    }
    return new NoticeComparisonReport(affectedSources, countPerSource);
  }

  @Test
  public void noNewNotice_generatesEmptyReport() {
    JsonObject acceptanceTestReportJson = generateAcceptanceTestReport(new TreeMap<>());
    assertThat(acceptanceTestReportJson.get("newNotices")).isNull();
  }

  @Test
  public void newNotices_generatesReport() {
    Map<String, NoticeComparisonReport> reportData = new TreeMap<>();
    NoticeComparisonReport firstNoticeComparisonReport =
        createNoticeComparisonReport(ImmutableSortedMap.of("source-id-1", 4, "source-id-2", 6));
    NoticeComparisonReport secondNoticeComparisonReport =
        createNoticeComparisonReport(ImmutableSortedMap.of("source-id-2", 40));
    NoticeComparisonReport thirdNoticeComparisonReport =
        createNoticeComparisonReport(
            ImmutableSortedMap.of("source-id-1", 40, "source-id-3", 15, "source-id-5", 2));
    NoticeComparisonReport fourthNoticeComparisonReport =
        createNoticeComparisonReport(ImmutableSortedMap.of("source-id-5", 5));
    reportData.put("first_notice_code", firstNoticeComparisonReport);
    reportData.put("second_notice_code", secondNoticeComparisonReport);
    reportData.put("third_notice_code", thirdNoticeComparisonReport);
    reportData.put("fourth_notice_code", fourthNoticeComparisonReport);
    JsonObject acceptanceTestReportJson = generateAcceptanceTestReport(reportData);
    assertThat(GSON.toJson(acceptanceTestReportJson))
        .isEqualTo(
            "{\"newErrors\":[{\"first_notice_code\":{\"affectedSourcesCount\":2,"
                + "\"affectedSources\":[{\"source_id\":\"source-id-1\",\"source_url\":"
                + "\"https://storage.googleapis.com/storage/v1/b/source-id-1_archives_2021-12-04/"
                + "o/1234.zip?alt=media\"},{\"source_id\":\"source-id-2\",\"source_url\":"
                + "\"https://storage.googleapis.com/storage/v1/b/source-id-2_archives_2021-12-04"
                + "/o/1234.zip?alt=media\"}],\"countPerSource\":[{\"source-id-1\":4},"
                + "{\"source-id-2\":6}]}},{\"fourth_notice_code\":{\"affectedSourcesCount\":1,"
                + "\"affectedSources\":[{\"source_id\":\"source-id-5\",\"source_url\":"
                + "\"https://storage.googleapis.com/storage/v1/b/source-id-5_archives_2021-12-04"
                + "/o/1234.zip?alt=media\"}],\"countPerSource\":[{\"source-id-5\":5}]}},"
                + "{\"second_notice_code\":{\"affectedSourcesCount\":1,\"affectedSources\":[{"
                + "\"source_id\":\"source-id-2\",\"source_url\":\"https://storage.googleapis.com"
                + "/storage/v1/b/source-id-2_archives_2021-12-04/o/1234.zip?alt=media\"}],"
                + "\"countPerSource\":[{\"source-id-2\":40}]}},{\"third_notice_code\":"
                + "{\"affectedSourcesCount\":3,\"affectedSources\":[{\"source_id\":\"source-id-1\","
                + "\"source_url\":\"https://storage.googleapis.com/storage/v1/b/source-id-1_"
                + "archives_2021-12-04/o/1234.zip?alt=media\"},{\"source_id\":\"source-id-3\","
                + "\"source_url\":\"https://storage.googleapis.com/storage/v1/b/source-id-3"
                + "_archives_2021-12-04/o/1234.zip?alt=media\"},{\"source_id\":\"source-id-5\","
                + "\"source_url\":\"https://storage.googleapis.com/storage/v1/b/source-id-5_a"
                + "rchives_2021-12-04/o/1234.zip?alt=media\"}],\"countPerSource\":[{\"source-id-1"
                + "\":40},{\"source-id-3\":15},{\"source-id-5\":2}]}}]}");
  }
}
