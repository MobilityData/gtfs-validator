package org.mobilitydata.gtfsvalidator.report.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.vladsch.flexmark.util.misc.Pair;
import java.util.*;
import java.util.function.Function;
import org.mobilitydata.gtfsvalidator.table.*;

public class FeedMetadata {
  /*
   * Use these strings as keys in the FeedInfo map. Also used to specify the info that will appear
   * in the json report. Adding elements to feedInfo will not automatically be included in the json
   * report and should be explicitly handled in the json report code.
   */
  public static final String FEED_INFO_PUBLISHER_NAME = "Publisher Name";
  public static final String FEED_INFO_PUBLISHER_URL = "Publisher URL";
  public static final String FEED_INFO_FEED_LANGUAGE = "Feed Language";
  public static final String FEED_INFO_FEED_START_DATE = "Feed Start Date";
  public static final String FEED_INFO_FEED_END_DATE = "Feed End Date";

  /*
   * Use these strings as keys in the counts map. Also used to specify the info that will appear in
   * the json report. Adding elements to feedInfo will not automatically be included in the json
   * report and should be explicitly handled in the json report code.
   */
  public static final String COUNTS_SHAPES = "Shapes";
  public static final String COUNTS_STOPS = "Stops";
  public static final String COUNTS_ROUTES = "Routes";
  public static final String COUNTS_TRIPS = "Trips";
  public static final String COUNTS_AGENCIES = "Agencies";
  public static final String COUNTS_BLOCKS = "Blocks";

  private Map<String, TableMetadata> tableMetaData;
  public Map<String, Integer> counts = new TreeMap<>();

  public Map<String, String> feedInfo = new LinkedHashMap<>();

  public Map<String, Boolean> specFeatures = new LinkedHashMap<>();

  public ArrayList<AgencyMetadata> agencies = new ArrayList<>();
  private ImmutableSortedSet<String> filenames;

  private final List<Pair<String, String>> FILE_BASED_COMPONENTS =
      List.of(
          new Pair<>("Pathways (basic)", GtfsPathway.FILENAME),
          new Pair<>("Pathways (extra)", GtfsPathway.FILENAME),
          new Pair<>("Transfers", GtfsTransfer.FILENAME),
          new Pair<>("Fares V1", GtfsFareAttribute.FILENAME),
          new Pair<>("Fare Products", GtfsFareProduct.FILENAME),
          new Pair<>("Shapes", GtfsShape.FILENAME),
          new Pair<>("Frequencies", GtfsFrequency.FILENAME),
          new Pair<>("Feed Information", GtfsFeedInfo.FILENAME),
          new Pair<>("Attributions", GtfsAttribution.FILENAME),
          new Pair<>("Translations", GtfsTranslation.FILENAME),
          new Pair<>("Fare Media", GtfsFareMedia.FILENAME),
          new Pair<>("Zone-Based Fares", GtfsArea.FILENAME),
          new Pair<>("Transfer Fares", GtfsFareTransferRule.FILENAME),
          new Pair<>("Time-Based Fares", GtfsTimeframe.FILENAME),
          new Pair<>("Route-Based Fares", GtfsNetwork.FILENAME),
          new Pair<>("Levels", GtfsLevel.FILENAME));

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
    setCount(COUNTS_SHAPES, feedContainer, GtfsShape.FILENAME, GtfsShape.class, GtfsShape::shapeId);
    setCount(COUNTS_STOPS, feedContainer, GtfsStop.FILENAME, GtfsStop.class, GtfsStop::stopId);
    setCount(COUNTS_ROUTES, feedContainer, GtfsRoute.FILENAME, GtfsRoute.class, GtfsRoute::routeId);
    setCount(COUNTS_TRIPS, feedContainer, GtfsTrip.FILENAME, GtfsTrip.class, GtfsTrip::tripId);
    setCount(
        COUNTS_AGENCIES,
        feedContainer,
        GtfsAgency.FILENAME,
        GtfsAgency.class,
        GtfsAgency::agencyId);
    setCount(COUNTS_BLOCKS, feedContainer, GtfsTrip.FILENAME, GtfsTrip.class, GtfsTrip::blockId);
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
    for (Pair<String, String> entry : FILE_BASED_COMPONENTS) {
      specFeatures.put(entry.getKey(), hasAtLeastOneRecordInFile(feedContainer, entry.getValue()));
    }
  }

  private void loadSpecFeaturesBasedOnFieldPresence(GtfsFeedContainer feedContainer) {
    loadRouteNamesComponent(feedContainer);
    loadRouteColorsComponent(feedContainer);
    loadAgencyInformationComponent(feedContainer);
    loadHeadsignsComponent(feedContainer);
    loadWheelchairAccessibilityComponent(feedContainer);
    loadTTSComponent(feedContainer);
    loadBikeAllowanceComponent(feedContainer);
    loadLocationTypesComponent(feedContainer);
    loadTraversalTimeComponent(feedContainer);
    loadPathwayDirectionsComponent(feedContainer);
    loadPathwayExtraComponent(feedContainer);
    loadBlocksComponent(feedContainer);
    loadRouteBasedFaresComponent(feedContainer);
    loadContinuousStopsComponent(feedContainer);
    loadZoneBasedComponent(feedContainer);
    loadTimeBasedFaresComponent(feedContainer);
    loadTransferFaresComponent(feedContainer);
    loadLevelsComponent(feedContainer);
  }

  private void loadContinuousStopsComponent(GtfsFeedContainer feedContainer) {
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
                List.of((Function<GtfsStopTime, Boolean>) GtfsStopTime::hasContinuousPickup)));
  }

  private void loadRouteBasedFaresComponent(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        "Route-Based Fares",
        hasAtLeastOneRecordForFields(
            feedContainer,
            GtfsRoute.FILENAME,
            List.of((Function<GtfsRoute, Boolean>) GtfsRoute::hasNetworkId))
            || hasAtLeastOneRecordInFile(feedContainer, GtfsNetwork.FILENAME));
  }

  private void loadBlocksComponent(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        "Blocks",
        hasAtLeastOneRecordForFields(
            feedContainer,
            GtfsTrip.FILENAME,
            List.of((Function<GtfsTrip, Boolean>) GtfsTrip::hasBlockId)));
  }

  private void loadPathwayDirectionsComponent(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        "Pathway Directions",
        hasAtLeastOneRecordForFields(
            feedContainer,
            GtfsPathway.FILENAME,
            List.of(
                GtfsPathway::hasSignpostedAs,
                (Function<GtfsPathway, Boolean>) GtfsPathway::hasReversedSignpostedAs)));
  }

  private void loadPathwayExtraComponent(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        "Pathway (extra)",
        hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsPathway.FILENAME,
                List.of((Function<GtfsPathway, Boolean>) GtfsPathway::hasMaxSlope))
            || hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsPathway.FILENAME,
                List.of((Function<GtfsPathway, Boolean>) GtfsPathway::hasMinWidth))
            || hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsPathway.FILENAME,
                List.of((Function<GtfsPathway, Boolean>) GtfsPathway::hasLength))
            || hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsPathway.FILENAME,
                List.of((Function<GtfsPathway, Boolean>) GtfsPathway::hasStairCount)));
  }

  private void loadTraversalTimeComponent(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        "Traversal Time",
        hasAtLeastOneRecordForFields(
            feedContainer,
            GtfsPathway.FILENAME,
            List.of((Function<GtfsPathway, Boolean>) GtfsPathway::hasTraversalTime)));
  }

  private void loadLocationTypesComponent(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        "Location Types",
        hasAtLeastOneRecordForFields(
            feedContainer,
            GtfsStop.FILENAME,
            List.of((Function<GtfsStop, Boolean>) GtfsStop::hasLocationType)));
  }

  private void loadBikeAllowanceComponent(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        "Bikes Allowance",
        hasAtLeastOneRecordForFields(
            feedContainer,
            GtfsTrip.FILENAME,
            List.of((Function<GtfsTrip, Boolean>) (GtfsTrip::hasBikesAllowed))));
  }

  private void loadTTSComponent(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        "Text-To-Speech",
        hasAtLeastOneRecordForFields(
            feedContainer,
            GtfsStop.FILENAME,
            List.of(((Function<GtfsStop, Boolean>) GtfsStop::hasTtsStopName))));
  }

  private void loadWheelchairAccessibilityComponent(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        "Wheelchair Accessibility",
        hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsTrip.FILENAME,
                List.of((Function<GtfsTrip, Boolean>) GtfsTrip::hasWheelchairAccessible))
            || hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsStop.FILENAME,
                List.of((Function<GtfsStop, Boolean>) GtfsStop::hasWheelchairBoarding)));
  }

  private void loadHeadsignsComponent(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        "Headsigns",
        hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsTrip.FILENAME,
                List.of((Function<GtfsTrip, Boolean>) GtfsTrip::hasTripHeadsign))
            || hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsStopTime.FILENAME,
                List.of((Function<GtfsStopTime, Boolean>) GtfsStopTime::hasStopHeadsign)));
  }

  private void loadAgencyInformationComponent(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        "Agency Information",
        hasAtLeastOneRecordForFields(
            feedContainer,
            GtfsAgency.FILENAME,
            List.of(
                GtfsAgency::hasAgencyEmail,
                (Function<GtfsAgency, Boolean>) GtfsAgency::hasAgencyPhone)));
  }

  private void loadRouteColorsComponent(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        "Route Colors",
        hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsRoute.FILENAME,
                List.of((Function<GtfsRoute, Boolean>) GtfsRoute::hasRouteColor))
            || hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsRoute.FILENAME,
                List.of((Function<GtfsRoute, Boolean>) GtfsRoute::hasRouteTextColor)));
  }

  private void loadRouteNamesComponent(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        "Route Names",
        hasAtLeastOneRecordForFields(
            feedContainer,
            GtfsRoute.FILENAME,
            List.of(
                GtfsRoute::hasRouteShortName,
                (Function<GtfsRoute, Boolean>) GtfsRoute::hasRouteLongName)));
  }

  private void loadZoneBasedComponent(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        "Zone-Based Fares", hasAtLeastOneRecordInFile(feedContainer, GtfsArea.FILENAME));
  }

  private void loadTimeBasedFaresComponent(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        "Time-Based Fares", hasAtLeastOneRecordInFile(feedContainer, GtfsTimeframe.FILENAME));
  }

  private void loadTransferFaresComponent(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        "Transfer Fares", hasAtLeastOneRecordInFile(feedContainer, GtfsFareTransferRule.FILENAME));
  }

  private void loadLevelsComponent(GtfsFeedContainer feedContainer) {
    specFeatures.put("Levels", hasAtLeastOneRecordInFile(feedContainer, GtfsLevel.FILENAME));
  }

  private void loadAgencyData(GtfsTableContainer<GtfsAgency> agencyTable) {
    for (GtfsAgency agency : agencyTable.getEntities()) {
      agencies.add(AgencyMetadata.from(agency));
    }
  }

  private void loadFeedInfo(GtfsTableContainer<GtfsFeedInfo> feedTable) {
    var info = feedTable.getEntities().isEmpty() ? null : feedTable.getEntities().get(0);

    feedInfo.put(FEED_INFO_PUBLISHER_NAME, info == null ? "N/A" : info.feedPublisherName());
    feedInfo.put(FEED_INFO_PUBLISHER_URL, info == null ? "N/A" : info.feedPublisherUrl());
    feedInfo.put(
        FEED_INFO_FEED_LANGUAGE, info == null ? "N/A" : info.feedLang().getDisplayLanguage());
    if (feedTable.hasColumn(GtfsFeedInfo.FEED_START_DATE_FIELD_NAME)) {
      feedInfo.put(
          FEED_INFO_FEED_START_DATE,
          info == null ? "N/A" : info.feedStartDate().getLocalDate().toString());
    }
    if (feedTable.hasColumn(GtfsFeedInfo.FEED_END_DATE_FIELD_NAME)) {
      feedInfo.put(
          FEED_INFO_FEED_END_DATE,
          info == null ? "N/A" : info.feedEndDate().getLocalDate().toString());
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
