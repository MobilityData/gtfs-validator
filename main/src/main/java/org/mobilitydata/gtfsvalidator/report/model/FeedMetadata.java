package org.mobilitydata.gtfsvalidator.report.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.vladsch.flexmark.util.misc.Pair;
import java.time.LocalDate;
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
  public static final String FEED_INFO_FEED_CONTACT_EMAIL = "Feed Email";
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

  public Map<FeatureMetadata, Boolean> specFeatures = new LinkedHashMap<>();

  public ArrayList<AgencyMetadata> agencies = new ArrayList<>();
  private ImmutableSortedSet<String> filenames;

  public double validationTimeSeconds;

  // List of features that only require checking the presence of one record in the file.
  private final List<Pair<FeatureMetadata, String>> FILE_BASED_FEATURES =
      List.of(
          new Pair<>(new FeatureMetadata("Pathway Connections", "Pathways"), GtfsPathway.FILENAME),
          new Pair<>(new FeatureMetadata("Levels", "Pathways"), GtfsLevel.FILENAME),
          new Pair<>(new FeatureMetadata("Transfers", null), GtfsTransfer.FILENAME),
          new Pair<>(new FeatureMetadata("Shapes", null), GtfsShape.FILENAME),
          new Pair<>(new FeatureMetadata("Frequencies", null), GtfsFrequency.FILENAME),
          new Pair<>(new FeatureMetadata("Feed Information", null), GtfsFeedInfo.FILENAME),
          new Pair<>(new FeatureMetadata("Attributions", null), GtfsAttribution.FILENAME),
          new Pair<>(new FeatureMetadata("Translations", null), GtfsTranslation.FILENAME),
          new Pair<>(new FeatureMetadata("Fares V1", "Fares"), GtfsFareAttribute.FILENAME),
          new Pair<>(new FeatureMetadata("Fare Products", "Fares"), GtfsFareProduct.FILENAME),
          new Pair<>(new FeatureMetadata("Fare Media", "Fares"), GtfsFareMedia.FILENAME),
          new Pair<>(new FeatureMetadata("Zone-Based Fares", "Fares"), GtfsArea.FILENAME),
          new Pair<>(
              new FeatureMetadata("Fares Transfers", "Fares"), GtfsFareTransferRule.FILENAME),
          new Pair<>(new FeatureMetadata("Time-Based Fares", "Fares"), GtfsTimeframe.FILENAME),
          new Pair<>(
              new FeatureMetadata("Booking Rules", "Flexible Services"), GtfsTimeframe.FILENAME),
          new Pair<>(
              new FeatureMetadata("Fixed-Stops Demand Responsive Transit", "Flexible Services"),
              GtfsTimeframe.FILENAME));

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
        if (id != null && !id.isEmpty()) {
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
    for (Pair<FeatureMetadata, String> entry : FILE_BASED_FEATURES) {
      specFeatures.put(entry.getKey(), hasAtLeastOneRecordInFile(feedContainer, entry.getValue()));
    }
  }

  private void loadSpecFeaturesBasedOnFieldPresence(GtfsFeedContainer feedContainer) {
    loadRouteColorsFeature(feedContainer);
    loadHeadsignsFeature(feedContainer);
    loadWheelchairAccessibilityFeature(feedContainer);
    loadTTSFeature(feedContainer);
    loadBikeAllowanceFeature(feedContainer);
    loadLocationTypesFeature(feedContainer);
    loadTraversalTimeFeature(feedContainer);
    loadPathwayDirectionsFeature(feedContainer);
    loadPathwayExtraFeature(feedContainer);
    loadRouteBasedFaresFeature(feedContainer);
    loadContinuousStopsFeature(feedContainer);
    loadZoneBasedDemandResponsiveTransitFeature(feedContainer);
    loadDeviatedFixedRouteFeature(feedContainer);
  }

  private void loadDeviatedFixedRouteFeature(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        new FeatureMetadata("Predefined Routes with Deviation", "Flexible Services"),
        hasAtLeastOneTripWithAllFields(feedContainer));
  }

  private boolean hasAtLeastOneTripWithAllFields(GtfsFeedContainer feedContainer) {
    Optional<GtfsTableContainer<?>> optionalStopTimeTable =
        feedContainer.getTableForFilename(GtfsStopTime.FILENAME);
    if (optionalStopTimeTable.isPresent()) {
      for (GtfsEntity entity : optionalStopTimeTable.get().getEntities()) {
        if (entity instanceof GtfsStopTime) {
          GtfsStopTime stopTime = (GtfsStopTime) entity;
          return stopTime.hasTripId()
              && stopTime.tripId() != null
              && stopTime.hasLocationId()
              && stopTime.locationId() != null
              && stopTime.hasStopId()
              && stopTime.stopId() != null
              && stopTime.hasArrivalTime()
              && stopTime.arrivalTime() != null
              && stopTime.hasDepartureTime()
              && stopTime.departureTime() != null;
        }
      }
    }
    return false;
  }

  private void loadZoneBasedDemandResponsiveTransitFeature(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        new FeatureMetadata("Zone-based Demand Responsive Services", "Flexible Services"),
        hasAtLeastOneTripWithOnlyLocationId(feedContainer));
  }

  private boolean hasAtLeastOneTripWithOnlyLocationId(GtfsFeedContainer feedContainer) {
    Optional<GtfsTableContainer<?>> optionalStopTimeTable =
        feedContainer.getTableForFilename(GtfsStopTime.FILENAME);
    if (optionalStopTimeTable.isPresent()) {
      for (GtfsEntity entity : optionalStopTimeTable.get().getEntities()) {
        if (entity instanceof GtfsStopTime) {
          GtfsStopTime stopTime = (GtfsStopTime) entity;
          if (stopTime.hasTripId()
              && stopTime.tripId() != null
              && stopTime.hasLocationId()
              && stopTime.locationId() != null
              && (!stopTime.hasStopId() || stopTime.stopId() == null)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private void loadContinuousStopsFeature(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        new FeatureMetadata("Continuous Stops", "Flexible Services"),
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

  private void loadRouteBasedFaresFeature(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        new FeatureMetadata("Route-Based Fares", "Fares"),
        hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsRoute.FILENAME,
                List.of((Function<GtfsRoute, Boolean>) GtfsRoute::hasNetworkId))
            || hasAtLeastOneRecordInFile(feedContainer, GtfsNetwork.FILENAME));
  }

  private void loadPathwayDirectionsFeature(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        new FeatureMetadata("Pathway Signs", "Pathways"),
        hasAtLeastOneRecordForFields(
            feedContainer,
            GtfsPathway.FILENAME,
            List.of(
                GtfsPathway::hasSignpostedAs,
                (Function<GtfsPathway, Boolean>) GtfsPathway::hasReversedSignpostedAs)));
  }

  private void loadPathwayExtraFeature(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        new FeatureMetadata("Pathway Details", "Pathways"),
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

  private void loadTraversalTimeFeature(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        new FeatureMetadata("In-station traversal time", "Pathways"),
        hasAtLeastOneRecordForFields(
            feedContainer,
            GtfsPathway.FILENAME,
            List.of((Function<GtfsPathway, Boolean>) GtfsPathway::hasTraversalTime)));
  }

  private void loadLocationTypesFeature(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        new FeatureMetadata("Location Types", null),
        hasAtLeastOneRecordForFields(
            feedContainer,
            GtfsStop.FILENAME,
            List.of((Function<GtfsStop, Boolean>) GtfsStop::hasLocationType)));
  }

  private void loadBikeAllowanceFeature(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        new FeatureMetadata("Bike Allowed", null),
        hasAtLeastOneRecordForFields(
            feedContainer,
            GtfsTrip.FILENAME,
            List.of((Function<GtfsTrip, Boolean>) (GtfsTrip::hasBikesAllowed))));
  }

  private void loadTTSFeature(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        new FeatureMetadata("Text-to-Speech", "Accessibility"),
        hasAtLeastOneRecordForFields(
            feedContainer,
            GtfsStop.FILENAME,
            List.of(((Function<GtfsStop, Boolean>) GtfsStop::hasTtsStopName))));
  }

  private void loadWheelchairAccessibilityFeature(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        new FeatureMetadata("Stops Wheelchair Accessibility", "Accessibility"),
        hasAtLeastOneRecordForFields(
            feedContainer,
            GtfsStop.FILENAME,
            List.of((Function<GtfsStop, Boolean>) GtfsStop::hasWheelchairBoarding)));
    specFeatures.put(
        new FeatureMetadata("Trips Wheelchair Accessibility", "Accessibility"),
        hasAtLeastOneRecordForFields(
            feedContainer,
            GtfsTrip.FILENAME,
            List.of((Function<GtfsTrip, Boolean>) GtfsTrip::hasWheelchairAccessible)));
  }

  private void loadHeadsignsFeature(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        new FeatureMetadata("Headsigns", null),
        hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsTrip.FILENAME,
                List.of((Function<GtfsTrip, Boolean>) GtfsTrip::hasTripHeadsign))
            || hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsStopTime.FILENAME,
                List.of((Function<GtfsStopTime, Boolean>) GtfsStopTime::hasStopHeadsign)));
  }

  private void loadRouteColorsFeature(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        new FeatureMetadata("Route Colors", null),
        hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsRoute.FILENAME,
                List.of((Function<GtfsRoute, Boolean>) GtfsRoute::hasRouteColor))
            || hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsRoute.FILENAME,
                List.of((Function<GtfsRoute, Boolean>) GtfsRoute::hasRouteTextColor)));
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
    feedInfo.put(FEED_INFO_FEED_CONTACT_EMAIL, info == null ? "N/A" : info.feedContactEmail());
    feedInfo.put(
        FEED_INFO_FEED_LANGUAGE, info == null ? "N/A" : info.feedLang().getDisplayLanguage());
    if (feedTable.hasColumn(GtfsFeedInfo.FEED_START_DATE_FIELD_NAME)) {
      LocalDate localDate = info.feedStartDate().getLocalDate();
      String displayDate =
          localDate.equals(GtfsFeedInfo.DEFAULT_FEED_START_DATE) ? "N/A" : localDate.toString();
      feedInfo.put(FEED_INFO_FEED_START_DATE, info == null ? "N/A" : displayDate);
    }
    if (feedTable.hasColumn(GtfsFeedInfo.FEED_END_DATE_FIELD_NAME)) {
      LocalDate localDate = info.feedEndDate().getLocalDate();
      String displayDate =
          localDate.equals(GtfsFeedInfo.DEFAULT_FEED_END_DATE) ? "N/A" : localDate.toString();
      feedInfo.put(FEED_INFO_FEED_END_DATE, info == null ? "N/A" : displayDate);
    }
  }

  private boolean hasAtLeastOneRecordInFile(
      GtfsFeedContainer feedContainer, String featureFilename) {
    var table = feedContainer.getTableForFilename(featureFilename);
    return table.isPresent() && table.get().entityCount() > 0;
  }

  private <T extends GtfsEntity> boolean hasAtLeastOneRecordForFields(
      GtfsFeedContainer feedContainer,
      String featureFilename,
      List<Function<T, Boolean>> conditions) {
    return feedContainer
        .getTableForFilename(featureFilename)
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
