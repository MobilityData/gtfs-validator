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
