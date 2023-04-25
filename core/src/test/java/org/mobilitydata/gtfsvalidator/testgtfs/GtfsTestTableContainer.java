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

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer;

public class GtfsTestTableContainer extends GtfsTableContainer<GtfsTestEntity> {
  private static final ImmutableList<String> KEY_COLUMN_NAMES =
      ImmutableList.of(GtfsTestEntity.ID_FIELD_NAME);

  private List<GtfsTestEntity> entities;

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

  @Override
  public ImmutableList<String> getKeyColumnNames() {
    return KEY_COLUMN_NAMES;
  }

  @Override
  public Optional<GtfsTestEntity> byTranslationKey(String recordId, String recordSubId) {
    return Optional.empty();
  }
}
