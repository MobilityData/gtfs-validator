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
import java.util.List;
import java.util.Optional;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer;

/** Test class to avoid dependency on the real GtfsStopTableContainer and annotation processor. */
public class GtfsStopTableContainer extends GtfsTableContainer<GtfsStop> {

  public GtfsStopTableContainer(TableStatus tableStatus, CsvHeader header) {
    super(tableStatus, header);
  }

  @Override
  public Class<GtfsStop> getEntityClass() {
    return GtfsStop.class;
  }

  @Override
  public List<GtfsStop> getEntities() {
    return ImmutableList.of();
  }

  @Override
  public String gtfsFilename() {
    return "stops.txt";
  }

  @Override
  public ImmutableList<String> getKeyColumnNames() {
    return ImmutableList.of();
  }

  @Override
  public Optional<GtfsStop> byPrimaryKey(String id, String subId) {
    return Optional.empty();
  }

  @Override
  public boolean isRequired() {
    return true;
  }
}
