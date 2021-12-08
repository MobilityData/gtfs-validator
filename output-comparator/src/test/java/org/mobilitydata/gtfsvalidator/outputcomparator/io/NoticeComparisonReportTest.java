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

package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.SourceUrlContainer;

@RunWith(JUnit4.class)
public class NoticeComparisonReportTest {

  @Rule public final TemporaryFolder tmpDir = new TemporaryFolder();

  private SourceUrlContainer createUrlFile(String jsonString) throws IOException {
    if (!Files.exists(tmpDir.getRoot().toPath().resolve("metadata"))) {
      tmpDir.newFolder("metadata");
    }
    File urlFile = tmpDir.newFile("gtfs_latest_versions.json");
    Files.write(urlFile.toPath(), jsonString.getBytes(StandardCharsets.UTF_8));
    return new SourceUrlContainer(urlFile.toPath());
  }

  @Test
  public void update_shouldUpdateAllFields() throws IOException {
    NoticeComparisonReport noticeComparisonReport = new NoticeComparisonReport();
    SourceUrlContainer urlContainer =
        createUrlFile(
            "{\"source-id-1\":\"url to source id 1\",\"source-id-2\":\"url to source id 2\"}");

    noticeComparisonReport.update("source-id-1", 44, urlContainer);
    noticeComparisonReport.update("source-id-2", 1, urlContainer);
    noticeComparisonReport.update("source-id-2", 5, urlContainer);
    Map<String, Integer> sourceInfo = new HashMap<>();
    sourceInfo.put("source-id-1", 44);
    sourceInfo.put("source-id-2", 6);
    assertThat(noticeComparisonReport.getAffectedSources().size()).isEqualTo(2);
    assertThat(noticeComparisonReport.getAffectedSources().keySet())
        .containsExactlyElementsIn(Set.of("source-id-1", "source-id-2"));
    assertThat(noticeComparisonReport.getCountPerSource()).containsExactlyEntriesIn(sourceInfo);
  }

  @Test
  public void toJson_noData() {
    NoticeComparisonReport noticeComparisonReport = new NoticeComparisonReport();
    JsonObject noticeComparisonReportJson = noticeComparisonReport.toJson();
    assertThat(noticeComparisonReportJson.get(NoticeComparisonReport.AFFECTED_SOURCES))
        .isEqualTo(new JsonArray());
    assertThat(noticeComparisonReportJson.get(NoticeComparisonReport.AFFECTED_SOURCES_COUNT))
        .isEqualTo(new JsonPrimitive(0));
    assertThat(noticeComparisonReportJson.get(NoticeComparisonReport.COUNT_PER_SOURCE))
        .isEqualTo(new JsonArray());
  }

  @Test
  public void toJson_emptyMap() throws IOException {
    NoticeComparisonReport noticeComparisonReport = new NoticeComparisonReport();
    SourceUrlContainer urlContainer =
        createUrlFile(
            "{\"source-id-1\":\"url to the latest version of the dataset issued by "
                + "source-id-1\",\"source-id-2\":\"url to the latest version of the dataset "
                + "issued by source-id-2\"}");

    noticeComparisonReport.update("source-id-1", 44, urlContainer);
    noticeComparisonReport.update("source-id-2", 1, urlContainer);
    noticeComparisonReport.update("source-id-2", 5, urlContainer);
    JsonObject noticeComparisonReportJson = noticeComparisonReport.toJson();
    JsonArray affectedSourcesJsonArray = new JsonArray();

    JsonObject firstSourceInfo = new JsonObject();
    firstSourceInfo.addProperty("source_id", "source-id-1");
    firstSourceInfo.addProperty(
        "source_url", "url to the latest version of the dataset issued by source-id-1");
    JsonObject secondSourceInfo = new JsonObject();
    secondSourceInfo.addProperty("source_id", "source-id-2");
    secondSourceInfo.addProperty(
        "source_url", "url to the latest version of the dataset issued by source-id-2");

    affectedSourcesJsonArray.add(firstSourceInfo);
    affectedSourcesJsonArray.add(secondSourceInfo);
    JsonArray countPerSourceJsonArray = new JsonArray();
    JsonObject firstDatasetInformation = new JsonObject();
    firstDatasetInformation.addProperty("source-id-1", 44);
    JsonObject secondDatasetInformation = new JsonObject();
    secondDatasetInformation.addProperty("source-id-2", 6);

    countPerSourceJsonArray.add(firstDatasetInformation);

    countPerSourceJsonArray.add(secondDatasetInformation);
    assertThat(noticeComparisonReportJson.get(NoticeComparisonReport.AFFECTED_SOURCES))
        .isEqualTo(affectedSourcesJsonArray);
    assertThat(noticeComparisonReportJson.get(NoticeComparisonReport.AFFECTED_SOURCES_COUNT))
        .isEqualTo(new JsonPrimitive(2));
    assertThat(noticeComparisonReportJson.get(NoticeComparisonReport.COUNT_PER_SOURCE))
        .isEqualTo(countPerSourceJsonArray);
  }
}
