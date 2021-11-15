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
import static org.mobilitydata.gtfsvalidator.outputcomparator.io.NoticeStat.URL_PATTERN;

import com.google.common.collect.ImmutableSortedMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.outputcomparator.io.NoticeStat;

@RunWith(JUnit4.class)
public class MainTest {
  private static final Gson GSON =
      new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
//  private static final String URL_PATTERN =
//      "https://storage.googleapis.com/storage/v1/b/%s_latest/o/1234.zip?alt=media";

  private static NoticeStat createNoticeStat(SortedMap<String, Integer> countPerSource) {
    SortedMap<String, String> affectedSources = new TreeMap<>();
    for (String sourceId : countPerSource.keySet()) {
      affectedSources.put(sourceId, String.format(URL_PATTERN, sourceId));
    }
    int affectedSourcesCount = countPerSource.size();
    return new NoticeStat(affectedSourcesCount, affectedSources, countPerSource);
  }

  @Test
  public void noNewNotice_generatesEmptyReport() {
    JsonObject acceptanceTestReportJson = generateAcceptanceTestReport(new TreeMap<>());
    assertThat(acceptanceTestReportJson.get("newNotices")).isNull();
  }

  @Test
  public void newNotices_generatesReport() {
    Map<String, NoticeStat> reportData = new TreeMap<>();
    NoticeStat firstNoticeStat =
        createNoticeStat(ImmutableSortedMap.of("source-id-1", 4, "source-id-2", 6));
    NoticeStat secondNoticeStat = createNoticeStat(ImmutableSortedMap.of("source-id-2", 40));
    NoticeStat thirdNoticeStat =
        createNoticeStat(
            ImmutableSortedMap.of("source-id-1", 40, "source-id-3", 15, "source-id-5", 2));
    NoticeStat fourthNoticeStat = createNoticeStat(ImmutableSortedMap.of("source-id-5", 5));
    reportData.put("first_notice_code", firstNoticeStat);
    reportData.put("second_notice_code", secondNoticeStat);
    reportData.put("third_notice_code", thirdNoticeStat);
    reportData.put("fourth_notice_code", fourthNoticeStat);
    JsonObject acceptanceTestReportJson = generateAcceptanceTestReport(reportData);
    assertThat(GSON.toJson(acceptanceTestReportJson))
        .isEqualTo(
            "{\"newErrors\":[{\"first_notice_code\":{\"affectedSourcesCount\":2,"
                + "\"affectedSources\":[{\"source-id-1\":\"https://storage.googleapis.com"
                + "/storage/v1/b/source-id-1_latest/o/1234.zip?alt=media\"},{\"source-id-2\":"
                + "\"https://storage.googleapis.com/storage/v1/b/source-id-2_latest/o/1234.zip"
                + "?alt=media\"}],\"countPerSource\":[{\"source-id-1\":4},{\"source-id-2"
                + "\":6}]}},{\"fourth_notice_code\":{\"affectedSourcesCount\":1,\"affectedSources"
                + "\":[{\"source-id-5\":\"https://storage.googleapis.com/storage/v1/b/"
                + "source-id-5_latest/o/1234.zip?alt=media\"}],\"countPerSource\":[{"
                + "\"source-id-5\":5}]}},{\"second_notice_code\":{\"affectedSourcesCount\":1,"
                + "\"affectedSources\":[{\"source-id-2\":\"https://storage.googleapis.com/"
                + "storage/v1/b/source-id-2_latest/o/1234.zip?alt=media\"}],\"countPerSource"
                + "\":[{\"source-id-2\":40}]}},{\"third_notice_code\":{\"affectedSourcesCount"
                + "\":3,\"affectedSources\":[{\"source-id-1\":\"https://storage.googleapis.com"
                + "/storage/v1/b/source-id-1_latest/o/1234.zip?alt=media\"},{\"source-id-3\":"
                + "\"https://storage.googleapis.com/storage/v1/b/source-id-3_latest/o/1234.zip"
                + "?alt=media\"},{\"source-id-5\":\"https://storage.googleapis.com/storage/v1/b/"
                + "source-id-5_latest/o/1234.zip?alt=media\"}],\"countPerSource\":["
                + "{\"source-id-1\":40},{\"source-id-3\":15},{\"source-id-5\":2}]}}]}");
  }
}
