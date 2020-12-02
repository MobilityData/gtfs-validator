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

import java.util.*;

/**
 * Container for a whole parsed GTFS feed with all its tables.
 * <p>
 * The tables are kept as {@code GtfsTableContainer} instances.
 */
public class GtfsFeedContainer {
    private final Map<String, GtfsTableContainer> tables = new HashMap<>();
    private final Map<Class<? extends GtfsTableContainer>, GtfsTableContainer> tablesByClass = new HashMap<>();

    public GtfsFeedContainer(List<GtfsTableContainer> tableContainerList) {
        for (GtfsTableContainer table : tableContainerList) {
            tables.put(table.gtfsFilename(), table);
            tablesByClass.put(table.getClass(), table);
        }
    }

    public GtfsTableContainer getTable(String filename) {
        return tables.get(filename);
    }

    public <T extends GtfsTableContainer> T getTable(Class<T> clazz) {
        return (T) tablesByClass.get(clazz);
    }

    public String tableTotals() {
        List<String> totalList = new ArrayList<>();
        for (GtfsTableContainer table : tables.values()) {
            totalList.add(table.gtfsFilename() + "\t" + table.entityCount());
        }
        Collections.sort(totalList);
        return String.join("\n", totalList);
    }
}
