package org.mobilitydata.gtfsvalidator.report.model;

import java.util.*;
import org.mobilitydata.gtfsvalidator.table.*;

public class FeedMetadata {
  private Map<String, TableMetadata> tableMetaData;
  private int blockCount = 0;

 public Map<String, String> feedInfo = new LinkedHashMap<>();

public ArrayList<AgencyMetadata> agencies = new ArrayList<>();


  public static FeedMetadata from(GtfsFeedContainer feedContainer) {
    var feedMetadata = new FeedMetadata();
    TreeMap<String, TableMetadata> map = new TreeMap<>();
    for (var table : feedContainer.getTables()) {
      var metadata = TableMetadata.from(table);
      map.put(metadata.getFilename(), metadata);
    }
    feedMetadata.setTableMetaData(map);
    if (feedContainer.getTableForFilename(GtfsTrip.FILENAME).isPresent()) {
      feedMetadata.loadBlockCount(
          (GtfsTableContainer<GtfsTrip>)
              feedContainer.getTableForFilename(GtfsTrip.FILENAME).get());
    }
    if (feedContainer.getTableForFilename(GtfsFeedInfo.FILENAME).isPresent()) {
      feedMetadata.loadFeedInfo(
          (GtfsTableContainer<GtfsFeedInfo>)
              feedContainer.getTableForFilename(GtfsFeedInfo.FILENAME).get());
    }
    feedMetadata.loadAgencyData(
        (GtfsTableContainer<GtfsAgency>)
            feedContainer.getTableForFilename(GtfsAgency.FILENAME).get());
    return feedMetadata;
  }

  private void loadAgencyData(GtfsTableContainer<GtfsAgency> agencyTable) {
    for (GtfsAgency agency : agencyTable.getEntities()) {
      agencies.add(AgencyMetadata.from(agency));
    }
  }

  private void loadFeedInfo(GtfsTableContainer<GtfsFeedInfo> feedTable) {
    var info = feedTable.getEntities().get(0);

    feedInfo.put("Publisher Name", info.feedPublisherName());
    feedInfo.put("Publisher URL", info.feedPublisherUrl());
    feedInfo.put("Feed Language", info.feedLang().getDisplayLanguage());
    if (feedTable.hasColumn(GtfsFeedInfo.FEED_START_DATE_FIELD_NAME)) {
      feedInfo.put("Feed Start Date", info.feedStartDate().getLocalDate().toString());
    }
    if (feedTable.hasColumn(GtfsFeedInfo.FEED_END_DATE_FIELD_NAME)) {
      feedInfo.put("Feed End Date", info.feedEndDate().getLocalDate().toString());
    }
  }

  private void loadBlockCount(GtfsTableContainer<GtfsTrip> tripFile) {
    // iterate through entities and count unique block_ids
    Set<String> blockIds = new HashSet<>();
    for (GtfsTrip trip : tripFile.getEntities()) {
      if (trip.hasBlockId()) {
        blockIds.add(trip.blockId());
      }
    }
    blockCount = blockIds.size();
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
