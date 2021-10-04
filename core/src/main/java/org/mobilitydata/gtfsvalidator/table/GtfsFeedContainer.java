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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer.TableStatus;

/**
 * Container for a whole parsed GTFS feed with all its tables.
 *
 * <p>The tables are kept as {@code GtfsTableContainer} instances.
 */
public class GtfsFeedContainer {
  private final Map<String, GtfsTableContainer<?>> tables = new HashMap<>();
  private final Map<Class<? extends GtfsTableContainer>, GtfsTableContainer<?>> tablesByClass =
      new HashMap<>();

  public GtfsFeedContainer(List<GtfsTableContainer<?>> tableContainerList) {
    for (GtfsTableContainer<?> table : tableContainerList) {
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
  public Optional<GtfsTableContainer<?>> getTableForFilename(String filename) {
    return Optional.ofNullable(tables.getOrDefault(Ascii.toLowerCase(filename), null));
  }

  public <T extends GtfsTableContainer<?>> T getTable(Class<T> clazz) {
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
    for (GtfsTableContainer<?> table : tables.values()) {
      if (!table.isParsedSuccessfully()) {
        return false;
      }
    }
    return true;
  }

  public String tableTotals() {
    List<String> totalList = new ArrayList<>();
    for (GtfsTableContainer<?> table : tables.values()) {
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
