package org.mobilitydata.gtfsvalidator.report.model;

import java.util.*;

import org.mobilitydata.gtfsvalidator.table.*;

public class FeedMetadata {
  private Map<String, TableMetadata> tableMetaData;
  private int blockCount = 0;

  public static FeedMetadata from(GtfsFeedContainer feedContainer) {
    var feedMetadata = new FeedMetadata();
    TreeMap<String, TableMetadata> map = new TreeMap<>();
    for (var table : feedContainer.getTables()) {
      var metadata = TableMetadata.from(table);
      map.put(metadata.getFilename(), metadata);
    }
    feedMetadata.setTableMetaData(map);
    if(feedContainer.getTableForFilename(GtfsTrip.FILENAME).isPresent()){
      feedMetadata.setBlockCount(feedMetadata.countBlocks((GtfsTableContainer<GtfsTrip>) feedContainer.getTableForFilename(GtfsTrip.FILENAME).get()));
    }
    return feedMetadata;
  }

  private int countBlocks(GtfsTableContainer<GtfsTrip> tripFile) {
    // iterate through entities and count unique block_ids
    Set<String> blockIds = new HashSet<>();
    for (GtfsTrip trip : tripFile.getEntities()) {
      if (trip.hasBlockId()) {
        blockIds.add(trip.blockId());
      }
    }
    return blockIds.size();
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
    var counts = new TreeMap<String, Integer>();
    counts.put("Agencies", tableMetaData.get(GtfsAgency.FILENAME).getEntityCount());
    counts.put("Routes", tableMetaData.get(GtfsRoute.FILENAME).getEntityCount());
    counts.put("Trips", tableMetaData.get(GtfsTrip.FILENAME).getEntityCount());
    counts.put("Stops", tableMetaData.get(GtfsStop.FILENAME).getEntityCount());
    counts.put("Shapes", tableMetaData.get(GtfsShape.FILENAME).getEntityCount());
    counts.put("Blocks", blockCount);



    return counts;
  }

  public void setTableMetaData(Map<String, TableMetadata> tableMetaData) {
    this.tableMetaData = tableMetaData;
  }


  public void setBlockCount(int blockCount) {
    this.blockCount = blockCount;
  }
}
