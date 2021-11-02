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
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Used to store details about a particular {@code ValidationNotice}. For one {@code
 * ValidationNotice} this gives information about:
 *
 * <ul>
 *   <li>the number of datasets that raised this {@code Notice}
 *   <li>the ids of the datasets that raised this {@code Notice}
 *   <li>the total number of this {@code ValidationNotice} in each datasets concerned
 * </ul>
 */
public class NoticeStat {

  protected static final String AFFECTED_DATASETS_COUNT = "affectedDatasetsCount";
  protected static final String AFFECTED_DATASETS = "affectedDatasets";
  protected static final String COUNT_PER_DATASET = "countPerDataset";
  private final SortedSet<String> affectedDatasets;
  private final SortedMap<String, Integer> countPerDataset;
  private int affectedDatasetsCount;

  public NoticeStat(
      int affectedDatasetsCount,
      SortedSet<String> affectedDatasets,
      SortedMap<String, Integer> countPerDataset) {
    this.affectedDatasetsCount = affectedDatasetsCount;
    this.affectedDatasets = affectedDatasets;
    this.countPerDataset = countPerDataset;
  }

  public NoticeStat() {
    this(0, new TreeSet<>(), new TreeMap<>());
  }

  @VisibleForTesting
  public Set<String> getAffectedDatasets() {
    return affectedDatasets;
  }

  @VisibleForTesting
  public Map<String, Integer> getCountPerDataset() {
    return countPerDataset;
  }

  @VisibleForTesting
  public int getAffectedDatasetsCount() {
    return affectedDatasetsCount;
  }

  /**
   * Updates field countPerDataset for a given datasetId
   *
   * @param datasetId the id of the dataset to update
   * @param newCount the new value for {@code NoticeStat#count}
   */
  private void updateCountPerDataset(String datasetId, Integer newCount) {
    Integer currentCount = this.countPerDataset.getOrDefault(datasetId, 0);
    this.countPerDataset.put(datasetId, currentCount + newCount);
  }

  /**
   * Updates all fields of this {@code NoticeStat}.
   *
   * @param datasetId the id of the dataset
   * @param noticeCount the number of notices raised by a given dataset identified by its id
   */
  public void update(String datasetId, int noticeCount) {
    this.affectedDatasets.add(datasetId);
    updateCountPerDataset(datasetId, noticeCount);
    this.affectedDatasetsCount = this.affectedDatasets.size();
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
    root.addProperty(AFFECTED_DATASETS_COUNT, affectedDatasetsCount);
    root.add(AFFECTED_DATASETS, affectedDatasetsJsonArray);
    root.add(COUNT_PER_DATASET, statsJsonArray);

    for (String datasetId : affectedDatasets) {
      affectedDatasetsJsonArray.add(datasetId);
      JsonObject statJson = new JsonObject();
      statsJsonArray.add(statJson);
      statJson.addProperty(datasetId, countPerDataset.get(datasetId));
    }
    return root;
  }
}
