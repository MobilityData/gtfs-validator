package org.mobilitydata.gtfsvalidator.report.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.vladsch.flexmark.util.misc.Pair;
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
    loadSpecFeaturesBasedOnFilePresence(feedContainer);
    loadSpecFeaturesBasedOnFieldPresence(feedContainer);
  }

  private void loadSpecFeaturesBasedOnFilePresence(GtfsFeedContainer feedContainer) {
    List<Pair<String, String>> componentList =
        List.of(
            new Pair<>("Pathways", GtfsPathway.FILENAME),
            new Pair<>("Transfers", GtfsTransfer.FILENAME),
            new Pair<>("Fares V1", GtfsFareAttribute.FILENAME),
            new Pair<>("Fares V2", GtfsFareProduct.FILENAME),
            new Pair<>("Shapes", GtfsShape.FILENAME),
            new Pair<>("Frequency-Based Trip", GtfsFrequency.FILENAME),
            new Pair<>("Feed Information", GtfsFeedInfo.FILENAME),
            new Pair<>("Attributions", GtfsAttribution.FILENAME),
            new Pair<>("Translations", GtfsTranslation.FILENAME),
            new Pair<>("Fare Media", GtfsFareMedia.FILENAME),
            new Pair<>("Zone-Based Fares", GtfsStopArea.FILENAME),
            new Pair<>("Transfers", GtfsTransfer.FILENAME));

    for (Pair<String, String> entry : componentList) {
      specFeatures.put(
          entry.getKey(),
          hasAtLeastOneRecordInFile(feedContainer, entry.getValue()) ? "Yes" : "No");
    }
  }

  private void loadSpecFeaturesBasedOnFieldPresence(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        "Route Names",
        hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsRoute.FILENAME,
                List.of(
                    GtfsRoute::hasRouteShortName,
                    (Function<GtfsRoute, Boolean>) GtfsRoute::hasRouteLongName))
            ? "Yes"
            : "No");

    specFeatures.put(
        "Route Colors",
        hasAtLeastOneRecordForFields(
                    feedContainer,
                    GtfsRoute.FILENAME,
                    List.of((Function<GtfsRoute, Boolean>) GtfsRoute::hasRouteColor))
                || hasAtLeastOneRecordForFields(
                    feedContainer,
                    GtfsRoute.FILENAME,
                    List.of((Function<GtfsRoute, Boolean>) GtfsRoute::hasRouteTextColor))
            ? "Yes"
            : "No");
    specFeatures.put(
        "Agency Information",
        hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsAgency.FILENAME,
                List.of(
                    GtfsAgency::hasAgencyEmail,
                    (Function<GtfsAgency, Boolean>) GtfsAgency::hasAgencyPhone))
            ? "Yes"
            : "No");
    specFeatures.put(
        "Headsigns",
        hasAtLeastOneRecordForFields(
                    feedContainer,
                    GtfsTrip.FILENAME,
                    List.of((Function<GtfsTrip, Boolean>) GtfsTrip::hasTripHeadsign))
                || hasAtLeastOneRecordForFields(
                    feedContainer,
                    GtfsStopTime.FILENAME,
                    List.of((Function<GtfsStopTime, Boolean>) GtfsStopTime::hasStopHeadsign))
            ? "Yes"
            : "No");
    specFeatures.put(
        "Wheelchair Accessibility",
        hasAtLeastOneRecordForFields(
                    feedContainer,
                    GtfsTrip.FILENAME,
                    List.of((Function<GtfsTrip, Boolean>) GtfsTrip::hasWheelchairAccessible))
                || hasAtLeastOneRecordForFields(
                    feedContainer,
                    GtfsStop.FILENAME,
                    List.of((Function<GtfsStop, Boolean>) GtfsStop::hasWheelchairBoarding))
            ? "Yes"
            : "No");
    specFeatures.put(
        "Text-To-Speech",
        hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsStop.FILENAME,
                List.of(((Function<GtfsStop, Boolean>) GtfsStop::hasTtsStopName)))
            ? "Yes"
            : "No");
    specFeatures.put(
        "Bikes Allowance",
        hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsTrip.FILENAME,
                List.of((Function<GtfsTrip, Boolean>) (GtfsTrip::hasBikesAllowed)))
            ? "Yes"
            : "No");
    specFeatures.put(
        "Location Types",
        hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsStop.FILENAME,
                List.of((Function<GtfsStop, Boolean>) GtfsStop::hasLocationType))
            ? "Yes"
            : "No");
    specFeatures.put(
        "Traversal Time",
        hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsPathway.FILENAME,
                List.of((Function<GtfsPathway, Boolean>) GtfsPathway::hasTraversalTime))
            ? "Yes"
            : "No");
    specFeatures.put(
        "Pathway Directions",
        hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsPathway.FILENAME,
                List.of(
                    GtfsPathway::hasSignpostedAs,
                    (Function<GtfsPathway, Boolean>) GtfsPathway::hasReversedSignpostedAs))
            ? "Yes"
            : "No");
    specFeatures.put(
        "Blocks",
        hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsTrip.FILENAME,
                List.of((Function<GtfsTrip, Boolean>) GtfsTrip::hasBlockId))
            ? "Yes"
            : "No");
    specFeatures.put(
        "Route-Based Fares",
        hasAtLeastOneRecordForFields(
                    feedContainer,
                    GtfsFareLegRule.FILENAME,
                    List.of(
                        GtfsFareLegRule::hasFromAreaId,
                        (Function<GtfsFareLegRule, Boolean>) GtfsFareLegRule::hasToAreaId))
                && hasAtLeastOneRecordForFields(
                    feedContainer,
                    GtfsRoute.FILENAME,
                    List.of((Function<GtfsRoute, Boolean>) GtfsRoute::hasNetworkId))
            ? "Yes"
            : "No");
    specFeatures.put(
        "Continuous Stops",
        hasAtLeastOneRecordForFields(
                    feedContainer,
                    GtfsRoute.FILENAME,
                    List.of((Function<GtfsRoute, Boolean>) GtfsRoute::hasContinuousDropOff))
                || hasAtLeastOneRecordForFields(
                    feedContainer,
                    GtfsRoute.FILENAME,
                    List.of((Function<GtfsRoute, Boolean>) GtfsRoute::hasContinuousPickup))
                || hasAtLeastOneRecordForFields(
                    feedContainer,
                    GtfsStopTime.FILENAME,
                    List.of((Function<GtfsStopTime, Boolean>) GtfsStopTime::hasContinuousDropOff))
                || hasAtLeastOneRecordForFields(
                    feedContainer,
                    GtfsStopTime.FILENAME,
                    List.of((Function<GtfsStopTime, Boolean>) GtfsStopTime::hasContinuousPickup))
            ? "Yes"
            : "No");
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

  private boolean hasAtLeastOneRecordInFile(
      GtfsFeedContainer feedContainer, String componentFilename) {
    var table = feedContainer.getTableForFilename(componentFilename);
    return table.isPresent() && table.get().entityCount() > 0;
  }

  private <T extends GtfsEntity> boolean hasAtLeastOneRecordForFields(
      GtfsFeedContainer feedContainer,
      String componentFilename,
      List<Function<T, Boolean>> conditions) {
    return feedContainer
        .getTableForFilename(componentFilename)
        .map(
            table ->
                table.getEntities().stream()
                    .anyMatch( // all values need to be defined for the same entry
                        entity ->
                            conditions.stream().allMatch(condition -> condition.apply((T) entity))))
        .orElse(false);
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
