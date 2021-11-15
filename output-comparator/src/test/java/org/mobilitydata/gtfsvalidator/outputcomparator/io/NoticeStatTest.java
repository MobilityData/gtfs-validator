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
import static org.mobilitydata.gtfsvalidator.outputcomparator.io.NoticeStat.URL_PATTERN;

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
  private static final String URLS =
      String.format(
          "%s %s",
          String.format(URL_PATTERN, "source-id-1"), String.format(URL_PATTERN, "source-id-2"));

  @Test
  public void update_shouldUpdateAllFields() {
    NoticeStat noticeStat = new NoticeStat();
    noticeStat.update("source-id-1", 44, URLS);
    noticeStat.update("source-id-2", 1, URLS);
    noticeStat.update("source-id-2", 5, URLS);
    Map<String, Integer> sourceInfo = new HashMap<>();
    sourceInfo.put("source-id-1", 44);
    sourceInfo.put("source-id-2", 6);
    assertThat(noticeStat.getAffectedSourcesCount()).isEqualTo(2);
    assertThat(noticeStat.getAffectedSources().keySet())
        .containsExactlyElementsIn(Set.of("source-id-1", "source-id-2"));
    assertThat(noticeStat.getCountPerSource()).containsExactlyEntriesIn(sourceInfo);
  }

  @Test
  public void toJson_noData() {
    NoticeStat noticeStat = new NoticeStat();
    JsonObject noticeStatJson = noticeStat.toJson();
    assertThat(noticeStatJson.get(NoticeStat.AFFECTED_SOURCES)).isEqualTo(new JsonArray());
    assertThat(noticeStatJson.get(NoticeStat.AFFECTED_SOURCES_COUNT))
        .isEqualTo(new JsonPrimitive(0));
    assertThat(noticeStatJson.get(NoticeStat.COUNT_PER_SOURCE)).isEqualTo(new JsonArray());
  }

  @Test
  public void toJson_emptyMap() {
    NoticeStat noticeStat = new NoticeStat();

    noticeStat.update("source-id-1", 44, URLS);
    noticeStat.update("source-id-2", 1, URLS);
    noticeStat.update("source-id-2", 5, URLS);
    JsonObject noticeStatJson = noticeStat.toJson();
    JsonArray affectedSourcesJsonArray = new JsonArray();

    JsonObject firstSourceInfo = new JsonObject();
    firstSourceInfo.addProperty("source-id-1", String.format(URL_PATTERN, "source-id-1"));
    JsonObject secondSourceInfo = new JsonObject();
    secondSourceInfo.addProperty("source-id-2", String.format(URL_PATTERN, "source-id-2"));

    affectedSourcesJsonArray.add(firstSourceInfo);
    affectedSourcesJsonArray.add(secondSourceInfo);
    JsonArray countPerSourceJsonArray = new JsonArray();
    JsonObject firstDatasetInformation = new JsonObject();
    firstDatasetInformation.addProperty("source-id-1", 44);
    JsonObject secondDatasetInformation = new JsonObject();
    secondDatasetInformation.addProperty("source-id-2", 6);

    countPerSourceJsonArray.add(firstDatasetInformation);

    countPerSourceJsonArray.add(secondDatasetInformation);
    assertThat(noticeStatJson.get(NoticeStat.AFFECTED_SOURCES)).isEqualTo(affectedSourcesJsonArray);
    assertThat(noticeStatJson.get(NoticeStat.AFFECTED_SOURCES_COUNT))
        .isEqualTo(new JsonPrimitive(2));
    assertThat(noticeStatJson.get(NoticeStat.COUNT_PER_SOURCE)).isEqualTo(countPerSourceJsonArray);
  }
}
