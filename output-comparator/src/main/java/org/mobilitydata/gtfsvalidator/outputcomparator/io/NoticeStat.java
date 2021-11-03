/*
 * Copyright 2021 Google LLC, MobilityData IO
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

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Used to store details about a particular {@code ValidationNotice}. For one {@code
 * ValidationNotice} this gives information about:
 *
 * <ul>
 *   <li>the number of datasets that raised this {@code ValidationNotice}
 *   <li>the ids of the datasets that raised this {@code ValidationNotice} and the url to be used to
 *       download them
 *   <li>the total number of this {@code ValidationNotice} in each datasets concerned
 * </ul>
 */
public class NoticeStat {

  protected static final String AFFECTED_SOURCES_COUNT = "affectedSourcesCount";
  protected static final String AFFECTED_SOURCES = "affectedSources";
  protected static final String COUNT_PER_SOURCE = "countPerSource";
  private static final String URL_PATTERN =
      "https://storage.googleapis.com/storage/v1/b/%s_latest/o/\\w+.zip\\?alt=media";
  private final SortedMap<String, String> affectedSources;
  private final SortedMap<String, Integer> countPerSource;
  private int affectedSourcesCount;

  public NoticeStat(
      int affectedDatasetsCount,
      SortedMap<String, String> affectedDatasets,
      SortedMap<String, Integer> countPerDataset) {
    this.affectedSourcesCount = affectedDatasetsCount;
    this.affectedSources = affectedDatasets;
    this.countPerSource = countPerDataset;
  }

  public NoticeStat() {
    this(0, new TreeMap<>(), new TreeMap<>());
  }

  @VisibleForTesting
  public SortedMap<String, String> getAffectedSources() {
    return affectedSources;
  }

  @VisibleForTesting
  public Map<String, Integer> getCountPerSource() {
    return countPerSource;
  }

  @VisibleForTesting
  public int getAffectedSourcesCount() {
    return affectedSourcesCount;
  }

  public static String retrieveSourceUrl(String urlAsString, String datasetId) {
    Pattern pattern = Pattern.compile(String.format(URL_PATTERN, datasetId));
    Matcher matcher = pattern.matcher(urlAsString);
    matcher.find();
    return matcher.group();
  }

  /**
   * Updates field countPerDataset for a given sourceId
   *
   * @param sourceId the id of the source to update
   * @param newCount the new value for {@code NoticeStat#count}
   */
  private void updateCountPerDataset(String sourceId, Integer newCount) {
    Integer currentCount = this.countPerSource.getOrDefault(sourceId, 0);
    this.countPerSource.put(sourceId, currentCount + newCount);
  }

  /**
   * Updates all fields of this {@code NoticeStat}.
   *
   * @param sourceId the id of the source
   * @param noticeCount the number of notices raised by the latest dataset version from a given
   *     source identified by its id
   */
  public void update(String sourceId, int noticeCount, String urls) {
    this.affectedSources.put(sourceId, retrieveSourceUrl(urls, sourceId));
    updateCountPerDataset(sourceId, noticeCount);
    this.affectedSourcesCount = this.affectedSources.size();
  }

  /**
   * Transforms this {@code NoticeStat} into a {@code JsonObject} for export.
   *
   * @return the {@code JsonObject} representation of this {@code NoticeStat}
   */
  public JsonObject toJson() {
    JsonObject root = new JsonObject();
    JsonArray affectedDatasetsJsonArray = new JsonArray();
    JsonArray statsJsonArray = new JsonArray();
    root.addProperty(AFFECTED_SOURCES_COUNT, affectedSourcesCount);
    root.add(AFFECTED_SOURCES, affectedDatasetsJsonArray);
    root.add(COUNT_PER_SOURCE, statsJsonArray);

    for (Entry<String, String> entry : affectedSources.entrySet()) {
      JsonObject datasetInfo = new JsonObject();
      datasetInfo.addProperty(entry.getKey(), entry.getValue());
      affectedDatasetsJsonArray.add(datasetInfo);
      JsonObject statJson = new JsonObject();
      statsJsonArray.add(statJson);
      statJson.addProperty(entry.getKey(), countPerSource.get(entry.getKey()));
    }
    return root;
  }
}
