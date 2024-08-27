/*
 * Copyright 2020 Google LLC
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

package org.mobilitydata.gtfsvalidator.table;

import com.google.common.base.Ascii;
import java.util.*;

/**
 * Container for a whole parsed GTFS feed with all its tables.
 *
 * <p>The tables are kept as {@code GtfsContainer} instances.
 */
public class GtfsFeedContainer {
  private final Map<String, GtfsContainer<?, ?>> tables = new HashMap<>();
  private final Map<Class<? extends GtfsContainer>, GtfsContainer<?, ?>> tablesByClass =
      new HashMap<>();

  public GtfsFeedContainer(List<GtfsContainer<?, ?>> tableContainerList) {
    for (GtfsContainer<?, ?> table : tableContainerList) {
      tables.put(table.gtfsFilename(), table);
      tablesByClass.put(table.getClass(), table);
    }
  }

  /**
   * Returns GTFS table for given file name, if any.
   *
   * <p>Returns empty if the table is not supported by schema. If table is supported but not
   * provided in the feed, then returns an empty table container with {@link
   * TableStatus#MISSING_FILE}.
   *
   * <p>File names are case-insensitive.
   *
   * @param filename file name, including ".txt" extension
   * @return GTFS table or empty if the table is not supported by schema
   */
  public Optional<GtfsContainer<?, ?>> getTableForFilename(String filename) {
    return Optional.ofNullable(tables.getOrDefault(Ascii.toLowerCase(filename), null));
  }

  public <T extends GtfsContainer<?, ?>> T getTable(Class<T> clazz) {
    return (T) tablesByClass.get(clazz);
  }

  /**
   * Tells if all files were successfully parsed.
   *
   * <p>If all files in the feed were successfully parsed, then file validators may be executed.
   *
   * @return true if all files were successfully parsed, false otherwise
   */
  public boolean isParsedSuccessfully() {
    for (GtfsContainer<?, ?> table : tables.values()) {
      if (!table.isParsedSuccessfully()) {
        return false;
      }
    }
    return true;
  }

  public Collection<GtfsContainer<?, ?>> getTables() {
    return tables.values();
  }

  public String tableTotalsText() {
    List<String> totalList = new ArrayList<>();
    for (GtfsContainer<?, ?> table : tables.values()) {
      if (table.getTableStatus() == TableStatus.MISSING_FILE
          && !table.getDescriptor().isRequired()) {
        continue;
      }
      totalList.add(
          table.gtfsFilename()
              + "\t"
              + (table.getTableStatus() == TableStatus.PARSABLE_HEADERS_AND_ROWS
                  ? Integer.toString(table.entityCount())
                  : table.getTableStatus().toString()));
    }
    Collections.sort(totalList);
    return String.join("\n", totalList);
  }
}
