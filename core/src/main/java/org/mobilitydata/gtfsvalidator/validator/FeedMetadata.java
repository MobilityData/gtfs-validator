package org.mobilitydata.gtfsvalidator.validator;

import java.util.*;

import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer;

public class FeedMetadata {
  private Map<String, TableMetadata> tableMetaData;

  public static FeedMetadata from(GtfsFeedContainer feedContainer) {
    var feedMetadata = new FeedMetadata();
    TreeMap<String, TableMetadata> map = new TreeMap<>();
    for (var metadata : feedContainer.tableMetadata()) {
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
    for (var table : tableMetaData.values()) {
      if (table.getTableStatus() == GtfsTableContainer.TableStatus.PARSABLE_HEADERS_AND_ROWS) {
        counts.put(table.getFilename(), table.getEntityCount());
      }
    }
    return counts;
  }

  public void setTableMetaData(Map<String, TableMetadata> tableMetaData) {
    this.tableMetaData = tableMetaData;
  }
}
