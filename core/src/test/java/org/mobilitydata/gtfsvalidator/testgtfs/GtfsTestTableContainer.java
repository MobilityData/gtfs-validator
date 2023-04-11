/*
 * Copyright 2021 Google LLC
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

package org.mobilitydata.gtfsvalidator.testgtfs;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import java.util.*;
import org.mobilitydata.gtfsvalidator.notice.DuplicateKeyNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer;

public final class GtfsTestTableContainer extends GtfsTableContainer<GtfsTestEntity> {
  private static final ImmutableList<String> KEY_COLUMN_NAMES =
      ImmutableList.of(GtfsTestEntity.ID_FIELD_NAME);

  private List<GtfsTestEntity> entities;

  private Map<String, GtfsTestEntity> byStopIdMap = new HashMap<>();

  private ListMultimap<String, GtfsTestEntity> byZoneIdMap = ArrayListMultimap.create();

  private ListMultimap<String, GtfsTestEntity> byParentStationMap = ArrayListMultimap.create();

  private GtfsTestTableContainer(CsvHeader header, List<GtfsTestEntity> entities) {
    super(TableStatus.PARSABLE_HEADERS_AND_ROWS, header);
    this.entities = entities;
  }

  public GtfsTestTableContainer(TableStatus tableStatus) {
    super(tableStatus, CsvHeader.EMPTY);
    this.entities = new ArrayList<>();
  }

  @Override
  public Class<GtfsTestEntity> getEntityClass() {
    return GtfsTestEntity.class;
  }

  @Override
  public String gtfsFilename() {
    return GtfsTestEntity.FILENAME;
  }

  @Override
  public boolean isRecommended() {
    return false;
  }

  @Override
  public boolean isRequired() {
    return true;
  }

  @Override
  public List<GtfsTestEntity> getEntities() {
    return entities;
  }

  /** Creates a table with given header and entities */
  public static GtfsTestTableContainer forHeaderAndEntities(
      CsvHeader header, List<GtfsTestEntity> entities, NoticeContainer noticeContainer) {
    GtfsTestTableContainer table = new GtfsTestTableContainer(header, entities);
    table.setupIndices(noticeContainer);
    return table;
  }

  /**
   * Creates a table with given entities and empty header. This method is intended to be used in
   * tests.
   */
  public static GtfsTestTableContainer forEntities(
      List<GtfsTestEntity> entities, NoticeContainer noticeContainer) {
    return forHeaderAndEntities(CsvHeader.EMPTY, entities, noticeContainer);
  }

  public Optional<GtfsTestEntity> byStopId(String key) {
    return Optional.ofNullable(byStopIdMap.getOrDefault(key, null));
  }

  /** @return List of org.mobilitydata.gtfsvalidator.table.GtfsStop */
  public List<GtfsTestEntity> byZoneId(String key) {
    return byZoneIdMap.get(key);
  }

  /**
   * @return ListMultimap keyed on zone_id with values that are Lists of
   *     org.mobilitydata.gtfsvalidator.table.GtfsStop
   */
  public ListMultimap<String, GtfsTestEntity> byZoneIdMap() {
    return byZoneIdMap;
  }

  /** @return List of org.mobilitydata.gtfsvalidator.table.GtfsStop */
  public List<GtfsTestEntity> byParentStation(String key) {
    return byParentStationMap.get(key);
  }

  /**
   * @return ListMultimap keyed on parent_station with values that are Lists of
   *     org.mobilitydata.gtfsvalidator.table.GtfsStop
   */
  public ListMultimap<String, GtfsTestEntity> byParentStationMap() {
    return byParentStationMap;
  }

  @Override
  public Optional<GtfsTestEntity> byTranslationKey(String recordId, String recordSubId) {
    return Optional.ofNullable(byStopIdMap.getOrDefault(recordId, null));
  }

  @Override
  public ImmutableList<String> getKeyColumnNames() {
    return KEY_COLUMN_NAMES;
  }

  private void setupIndices(NoticeContainer noticeContainer) {
    for (GtfsTestEntity newEntity : entities) {
      if (!newEntity.hasStopId()) {
        continue;
      }
      GtfsTestEntity oldEntity = byStopIdMap.getOrDefault(newEntity.stopId(), null);
      if (oldEntity != null) {
        noticeContainer.addValidationNotice(
            new DuplicateKeyNotice(
                gtfsFilename(),
                newEntity.csvRowNumber(),
                oldEntity.csvRowNumber(),
                GtfsTestEntity.ID_FIELD_NAME,
                newEntity.stopId()));
      } else {
        byStopIdMap.put(newEntity.stopId(), newEntity);
      }
    }
  }
}
