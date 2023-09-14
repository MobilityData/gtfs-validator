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

// We need a second test table class to test multi file validators.
public class GtfsTestTableContainer2 extends GtfsTableContainer<GtfsTestEntity> {
  private static final ImmutableList<String> KEY_COLUMN_NAMES =
      ImmutableList.of(GtfsTestEntity.ID_FIELD_NAME);

  private List<GtfsTestEntity> entities;

  private GtfsTestTableContainer2(
      GtfsTestTableDescriptor2 descriptor, CsvHeader header, List<GtfsTestEntity> entities) {
    super(descriptor, TableStatus.PARSABLE_HEADERS_AND_ROWS, header);
    this.entities = entities;
  }

  public GtfsTestTableContainer2(TableStatus tableStatus) {
    super(new GtfsTestTableDescriptor2(), tableStatus, CsvHeader.EMPTY);
    this.entities = new ArrayList<>();
  }

  @Override
  public Class<GtfsTestEntity> getEntityClass() {
    return GtfsTestEntity.class;
  }

  @Override
  public String gtfsFilename() {
    return GtfsTestEntity.FILENAME + "2";
  }

  @Override
  public List<GtfsTestEntity> getEntities() {
    return entities;
  }

  /** Creates a table with given header and entities */
  public static GtfsTestTableContainer2 forHeaderAndEntities(
      GtfsTestTableDescriptor2 descriptor,
      CsvHeader header,
      List<GtfsTestEntity> entities,
      NoticeContainer noticeContainer) {
    GtfsTestTableContainer2 table = new GtfsTestTableContainer2(descriptor, header, entities);
    return table;
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
