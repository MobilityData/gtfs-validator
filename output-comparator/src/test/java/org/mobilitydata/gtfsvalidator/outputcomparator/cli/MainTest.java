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

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.outputcomparator.io.NoticeStat;

@RunWith(JUnit4.class)
public class MainTest {
  private static final Gson GSON = new GsonBuilder().serializeNulls().create();

  @Test
  public void noNewNotice_generatesEmptyReport() {
    JsonObject acceptanceTestReportJson = generateAcceptanceTestReport(new TreeMap<>());
    assertThat(acceptanceTestReportJson.get("newNotices")).isEqualTo(new JsonArray());
  }

  @Test
  public void newNotices_generatesReport() {
    Map<String, NoticeStat> reportData = new TreeMap<>();
    NoticeStat firstNoticeStat =
        new NoticeStat(ImmutableMap.of("dataset-id-1", 4, "dataset-id-2", 6));
    NoticeStat secondNoticeStat = new NoticeStat(ImmutableMap.of("dataset-id-2", 40));
    NoticeStat thirdNoticeStat =
        new NoticeStat(ImmutableMap.of("dataset-id-1", 40, "dataset-id-3", 15, "dataset-id-5", 2));
    NoticeStat fourthNoticeStat = new NoticeStat(ImmutableMap.of("dataset-id-5", 5));
    reportData.put("first_notice_code", firstNoticeStat);
    reportData.put("second_notice_code", secondNoticeStat);
    reportData.put("third_notice_code", thirdNoticeStat);
    reportData.put("fourth_notice_code", fourthNoticeStat);
    JsonObject acceptanceTestReportJson = generateAcceptanceTestReport(reportData);
    assertThat(GSON.toJson(acceptanceTestReportJson))
        .isEqualTo(
            "{\"newNotices\":[{\"first_notice_code\":{\"affectedDatasetsCount\":2,"
                + "\"affectedDatasets\":[\"dataset-id-1\",\"dataset-id-2\"],\"countPerDataset"
                + "\":[{\"dataset-id-1\":4},{\"dataset-id-2\":6}]}},{\"fourth_notice_code\":{"
                + "\"affectedDatasetsCount\":1,\"affectedDatasets\":[\"dataset-id-5\"],"
                + "\"countPerDataset\":[{\"dataset-id-5\":5}]}},{\"second_notice_code\":{"
                + "\"affectedDatasetsCount\":1,\"affectedDatasets\":[\"dataset-id-2\"],"
                + "\"countPerDataset\":[{\"dataset-id-2\":40}]}},{\"third_notice_code\":{"
                + "\"affectedDatasetsCount\":3,\"affectedDatasets\":[\"dataset-id-1\",\"dataset-id-3"
                + "\",\"dataset-id-5\"],\"countPerDataset\":[{\"dataset-id-1\":40},{\"dataset-id-3"
                + "\":15},{\"dataset-id-5\":2}]}}]}");
  }
}
