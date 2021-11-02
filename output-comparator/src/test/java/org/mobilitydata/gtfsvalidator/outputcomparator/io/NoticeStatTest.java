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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class NoticeStatTest {

  @Test
  public void update_shouldUpdateAllFields() {
    NoticeStat noticeStat = new NoticeStat();
    noticeStat.update("dataset-id-1", 44);
    noticeStat.update("dataset-id-2", 1);
    noticeStat.update("dataset-id-2", 5);
    Map<String, Integer> datasetInfo = new HashMap<>();
    datasetInfo.put("dataset-id-1", 44);
    datasetInfo.put("dataset-id-2", 6);
    assertThat(noticeStat.getAffectedDatasetsCount()).isEqualTo(2);
    assertThat(noticeStat.getAffectedDatasets())
        .containsExactlyElementsIn(Set.of("dataset-id-1", "dataset-id-2"));
    assertThat(noticeStat.getCountPerDataset()).containsExactlyEntriesIn(datasetInfo);
  }

  @Test
  public void toJson_noData() {
    NoticeStat noticeStat = new NoticeStat();
    JsonObject noticeStatJson = noticeStat.toJson();
    assertThat(noticeStatJson.get(NoticeStat.AFFECTED_DATASETS)).isEqualTo(new JsonArray());
    assertThat(noticeStatJson.get(NoticeStat.AFFECTED_DATASETS_COUNT))
        .isEqualTo(new JsonPrimitive(0));
    assertThat(noticeStatJson.get(NoticeStat.COUNT_PER_DATASET)).isEqualTo(new JsonArray());
  }

  @Test
  public void toJson_emptyMap() {
    NoticeStat noticeStat = new NoticeStat();
    noticeStat.update("dataset-id-1", 44);
    noticeStat.update("dataset-id-2", 1);
    noticeStat.update("dataset-id-2", 5);
    JsonObject noticeStatJson = noticeStat.toJson();
    JsonArray affectedDatasetsJsonArray = new JsonArray();
    affectedDatasetsJsonArray.add("dataset-id-1");
    affectedDatasetsJsonArray.add("dataset-id-2");
    JsonArray countPerDatasetJsonArray = new JsonArray();
    JsonObject firstDatasetInformation = new JsonObject();
    firstDatasetInformation.addProperty("dataset-id-1", 44);
    JsonObject secondDatasetInformation = new JsonObject();
    secondDatasetInformation.addProperty("dataset-id-2", 6);
    countPerDatasetJsonArray.add(firstDatasetInformation);
    countPerDatasetJsonArray.add(secondDatasetInformation);
    assertThat(noticeStatJson.get(NoticeStat.AFFECTED_DATASETS))
        .isEqualTo(affectedDatasetsJsonArray);
    assertThat(noticeStatJson.get(NoticeStat.AFFECTED_DATASETS_COUNT))
        .isEqualTo(new JsonPrimitive(2));
    assertThat(noticeStatJson.get(NoticeStat.COUNT_PER_DATASET))
        .isEqualTo(countPerDatasetJsonArray);
  }
}
