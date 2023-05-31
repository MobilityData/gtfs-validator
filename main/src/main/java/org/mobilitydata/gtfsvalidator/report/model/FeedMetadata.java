package org.mobilitydata.gtfsvalidator.report.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import java.util.*;
import java.util.function.Function;
import org.mobilitydata.gtfsvalidator.table.*;

public class FeedMetadata {
  private Map<String, TableMetadata> tableMetaData;
  public Map<String, Integer> counts = new TreeMap<>();

  public Map<String, String> feedInfo = new LinkedHashMap<>();

  public Map<String, String> specFeatures = new LinkedHashMap<>();

  public ArrayList<AgencyMetadata> agencies = new ArrayList<>();
  private ImmutableSortedSet<String> filenames;

  protected FeedMetadata() {}

  public static FeedMetadata from(GtfsFeedContainer feedContainer, ImmutableSet<String> filenames) {
    var feedMetadata = new FeedMetadata();
    feedMetadata.setFilenames(ImmutableSortedSet.copyOf(filenames));
    TreeMap<String, TableMetadata> map = new TreeMap<>();
    for (var table : feedContainer.getTables()) {
      var metadata = TableMetadata.from(table);
      map.put(metadata.getFilename(), metadata);
    }
    feedMetadata.setTableMetaData(map);

    feedMetadata.setCounts(feedContainer);

    if (feedContainer.getTableForFilename(GtfsFeedInfo.FILENAME).isPresent()) {
      feedMetadata.loadFeedInfo(
          (GtfsTableContainer<GtfsFeedInfo>)
              feedContainer.getTableForFilename(GtfsFeedInfo.FILENAME).get());
    }
    feedMetadata.loadAgencyData(
        (GtfsTableContainer<GtfsAgency>)
            feedContainer.getTableForFilename(GtfsAgency.FILENAME).get());
    feedMetadata.loadSpecFeatures(feedContainer);
    return feedMetadata;
  }

  private void setCounts(GtfsFeedContainer feedContainer) {
    this.setCount("Shapes", feedContainer, GtfsShape.FILENAME, GtfsShape.class, GtfsShape::shapeId);
    this.setCount("Stops", feedContainer, GtfsStop.FILENAME, GtfsStop.class, GtfsStop::stopId);
    this.setCount("Routes", feedContainer, GtfsRoute.FILENAME, GtfsRoute.class, GtfsRoute::routeId);
    this.setCount("Trips", feedContainer, GtfsTrip.FILENAME, GtfsTrip.class, GtfsTrip::tripId);
    this.setCount(
        "Agencies", feedContainer, GtfsAgency.FILENAME, GtfsAgency.class, GtfsAgency::agencyId);
    this.setCount("Blocks", feedContainer, GtfsTrip.FILENAME, GtfsTrip.class, GtfsTrip::blockId);
  }

  private <T extends GtfsTableContainer<E>, E extends GtfsEntity> void setCount(
      String countName,
      GtfsFeedContainer feedContainer,
      String fileName,
      Class<E> clazz,
      Function<E, String> idExtractor) {

    var table = feedContainer.getTableForFilename(fileName);
    this.counts.put(
        countName,
        table
            .map(gtfsTableContainer -> loadUniqueCount(gtfsTableContainer, clazz, idExtractor))
            .orElse(0));
  }

  private <E extends GtfsEntity> int loadUniqueCount(
      GtfsTableContainer<?> table, Class<E> clazz, Function<E, String> idExtractor) {
    // Iterate through entities and count unique IDs
    Set<String> uniqueIds = new HashSet<>();
    for (GtfsEntity entity : table.getEntities()) {
      if (entity != null) {
        E castedEntity = clazz.cast(entity);
        String id = idExtractor.apply(castedEntity);
        if (id != null) {
          uniqueIds.add(id);
        }
      }
    }
    return uniqueIds.size();
  }

  private void loadSpecFeatures(GtfsFeedContainer feedContainer) {
    var pathwaysTable = feedContainer.getTableForFilename(GtfsPathway.FILENAME);
    boolean pathways = pathwaysTable.isPresent() && pathwaysTable.get().entityCount() > 0;
    specFeatures.put("Pathways", pathways ? "Yes" : "No");
    var fareAttributesTable = feedContainer.getTableForFilename(GtfsFareAttribute.FILENAME);
    boolean faresV1 =
        fareAttributesTable.isPresent() && fareAttributesTable.get().entityCount() > 0;
    specFeatures.put("Fares V1", faresV1 ? "Yes" : "No");
    var fareProductsTable = feedContainer.getTableForFilename(GtfsFareProduct.FILENAME);
    boolean faresV2 = fareProductsTable.isPresent() && fareProductsTable.get().entityCount() > 0;
    specFeatures.put("Fares V2", faresV2 ? "Yes" : "No");
    specFeatures.put("Route Names", hasRouteNamesComponent(feedContainer) ? "Yes" : "No");
    var stopTimesTable = feedContainer.getTableForFilename(GtfsStopTime.FILENAME);
    // TODO: figure out Flex V1 & V2 checks

  }

  private void loadAgencyData(GtfsTableContainer<GtfsAgency> agencyTable) {
    for (GtfsAgency agency : agencyTable.getEntities()) {
      agencies.add(AgencyMetadata.from(agency));
    }
  }

  private void loadFeedInfo(GtfsTableContainer<GtfsFeedInfo> feedTable) {
    var info = feedTable.getEntities().isEmpty() ? null : feedTable.getEntities().get(0);

    feedInfo.put("Publisher Name", info == null ? "N/A" : info.feedPublisherName());
    feedInfo.put("Publisher URL", info == null ? "N/A" : info.feedPublisherUrl());
    feedInfo.put("Feed Language", info == null ? "N/A" : info.feedLang().getDisplayLanguage());
    if (feedTable.hasColumn(GtfsFeedInfo.FEED_START_DATE_FIELD_NAME)) {
      feedInfo.put(
          "Feed Start Date", info == null ? "N/A" : info.feedStartDate().getLocalDate().toString());
    }
    if (feedTable.hasColumn(GtfsFeedInfo.FEED_END_DATE_FIELD_NAME)) {
      feedInfo.put(
          "Feed End Date", info == null ? "N/A" : info.feedEndDate().getLocalDate().toString());
    }
  }

  private boolean hasRouteNamesComponent(GtfsFeedContainer feedContainer) {
    var routeContainer = feedContainer.getTableForFilename(GtfsRoute.FILENAME);
    if (routeContainer.isPresent()) {
      GtfsRouteTableContainer routeTable = (GtfsRouteTableContainer) routeContainer.get();
      if (routeTable.hasColumn(GtfsRoute.ROUTE_SHORT_NAME_FIELD_NAME)
          && routeTable.hasColumn(GtfsRoute.ROUTE_LONG_NAME_FIELD_NAME))
        return routeTable.getEntities().stream().anyMatch(GtfsRoute::hasRouteShortName)
            && routeTable.getEntities().stream().anyMatch(GtfsRoute::hasRouteLongName);
    }
    return false;
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

  public void setTableMetaData(Map<String, TableMetadata> tableMetaData) {
    this.tableMetaData = tableMetaData;
  }

  public void setFilenames(ImmutableSortedSet<String> filenames) {
    this.filenames = filenames;
  }

  public ImmutableSortedSet<String> getFilenames() {
    return filenames;
  }
}
