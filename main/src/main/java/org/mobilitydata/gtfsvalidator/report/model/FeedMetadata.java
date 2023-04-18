package org.mobilitydata.gtfsvalidator.report.model;

import java.util.*;

import org.mobilitydata.gtfsvalidator.table.*;

public class FeedMetadata {
  private Map<String, TableMetadata> tableMetaData;

  public static FeedMetadata from(GtfsFeedContainer feedContainer) {
    var feedMetadata = new FeedMetadata();
    TreeMap<String, TableMetadata> map = new TreeMap<>();
    for (var table : feedContainer.getTables()) {
      var metadata = TableMetadata.from(table);
      map.put(metadata.getFilename(), metadata);
    }
    feedMetadata.setTableMetaData(map);
    return feedMetadata;
  }

  public ArrayList<String> foundFiles() {
    var foundFiles = new ArrayList<String>();
    for (var table : tableMetaData.values()) {
      if (table.getTableStatus() != GtfsTableContainer.TableStatus.MISSING_FILE) {
        foundFiles.add(table.getFilename());
      }
    }
    return foundFiles;
  }

  public Map<String, Integer> counts() {
    var counts = new HashMap<String, Integer>();
    counts.put("Agencies", tableMetaData.get(GtfsAgency.FILENAME).getEntityCount());
    counts.put("Routes", tableMetaData.get(GtfsRoute.FILENAME).getEntityCount());
    counts.put("Trips", tableMetaData.get(GtfsTrip.FILENAME).getEntityCount());
    counts.put("Stops", tableMetaData.get(GtfsStop.FILENAME).getEntityCount());
    counts.put("Shapes", tableMetaData.get(GtfsShape.FILENAME).getEntityCount());
    return counts;
  }

  public void setTableMetaData(Map<String, TableMetadata> tableMetaData) {
    this.tableMetaData = tableMetaData;
  }
}
