package org.mobilitydata.gtfsvalidator.reportSummary.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.flogger.FluentLogger;
import com.vladsch.flexmark.util.misc.Pair;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import org.mobilitydata.gtfsvalidator.performance.MemoryUsage;
import org.mobilitydata.gtfsvalidator.performance.MemoryUsageRegister;
import org.mobilitydata.gtfsvalidator.reportSummary.AgencyMetadata;
import org.mobilitydata.gtfsvalidator.reportSummary.JsonReportCounts;
import org.mobilitydata.gtfsvalidator.reportSummary.JsonReportFeedInfo;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.util.CalendarUtil;
import org.mobilitydata.gtfsvalidator.util.ServicePeriod;

public class FeedMetadata {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private Map<String, TableMetadata> tableMetaData;
  public Map<String, Integer> counts = new TreeMap<>();

  public Map<String, String> feedInfo = new LinkedHashMap<>();

  public Map<FeatureMetadata, Boolean> specFeatures = new LinkedHashMap<>();

  public ArrayList<AgencyMetadata> agencies = new ArrayList<>();
  private ImmutableSortedSet<String> filenames;

  public double validationTimeSeconds;

  public List<MemoryUsage> memoryUsageRecords;

  // List of features that only require checking the presence of one record in the file.
  private final List<Pair<FeatureMetadata, String>> FILE_BASED_FEATURES =
      List.of(
          new Pair<>(new FeatureMetadata("Pathway Connections", "Pathways"), GtfsPathway.FILENAME),
          new Pair<>(new FeatureMetadata("Pathway Signs", "Pathways"), GtfsPathway.FILENAME),
          new Pair<>(new FeatureMetadata("Pathway Details", "Pathways"), GtfsPathway.FILENAME),
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
          new Pair<>(new FeatureMetadata("Fare Transfers", "Fares"), GtfsFareTransferRule.FILENAME),
          new Pair<>(new FeatureMetadata("Time-Based Fares", "Fares"), GtfsTimeframe.FILENAME),
          new Pair<>(
              new FeatureMetadata("Booking Rules", "Flexible Services"), GtfsBookingRules.FILENAME),
          new Pair<>(
              new FeatureMetadata("Fixed-Stops Demand Responsive Transit", "Flexible Services"),
              GtfsLocationGroups.FILENAME));

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
      Optional<GtfsTableContainer<GtfsFeedInfo, GtfsFeedInfoTableDescriptor>>
          feedInfoTableOptional = feedContainer.getTableForFilename(GtfsFeedInfo.FILENAME);
      feedMetadata.loadFeedInfo(feedInfoTableOptional.get());
    }
    if (feedContainer.getTableForFilename(GtfsAgency.FILENAME).isPresent()) {
      Optional<GtfsTableContainer<GtfsAgency, GtfsAgencyTableDescriptor>> agencyTableOptional =
          feedContainer.getTableForFilename(GtfsAgency.FILENAME);
      feedMetadata.loadAgencyData(agencyTableOptional.get());
    }

    if (feedContainer.getTableForFilename(GtfsTrip.FILENAME).isPresent()
        && (feedContainer.getTableForFilename(GtfsCalendar.FILENAME).isPresent()
            || feedContainer.getTableForFilename(GtfsCalendarDate.FILENAME).isPresent())) {
      feedMetadata.loadServiceWindow(
          (GtfsTableContainer<GtfsTrip, ?>)
              feedContainer.getTableForFilename(GtfsTrip.FILENAME).get(),
          (GtfsTableContainer<GtfsCalendar, ?>)
              feedContainer.getTableForFilename(GtfsCalendar.FILENAME).get(),
          (GtfsTableContainer<GtfsCalendarDate, ?>)
              feedContainer.getTableForFilename(GtfsCalendarDate.FILENAME).get());
    }

    feedMetadata.loadSpecFeatures(feedContainer);
    feedMetadata.memoryUsageRecords = MemoryUsageRegister.getInstance().getRegistry();
    return feedMetadata;
  }

  private void setCounts(GtfsFeedContainer feedContainer) {
    setCount(
        JsonReportCounts.COUNTS_SHAPES,
        feedContainer,
        GtfsShape.FILENAME,
        GtfsShape.class,
        GtfsShape::shapeId);
    setCount(
        JsonReportCounts.COUNTS_STOPS,
        feedContainer,
        GtfsStop.FILENAME,
        GtfsStop.class,
        GtfsStop::stopId);
    setCount(
        JsonReportCounts.COUNTS_ROUTES,
        feedContainer,
        GtfsRoute.FILENAME,
        GtfsRoute.class,
        GtfsRoute::routeId);
    setCount(
        JsonReportCounts.COUNTS_TRIPS,
        feedContainer,
        GtfsTrip.FILENAME,
        GtfsTrip.class,
        GtfsTrip::tripId);
    setCount(
        JsonReportCounts.COUNTS_AGENCIES,
        feedContainer,
        GtfsAgency.FILENAME,
        GtfsAgency.class,
        GtfsAgency::agencyId);
    setCount(
        JsonReportCounts.COUNTS_BLOCKS,
        feedContainer,
        GtfsTrip.FILENAME,
        GtfsTrip.class,
        GtfsTrip::blockId);
  }

  private <T extends GtfsEntityContainer, E extends GtfsEntity> void setCount(
      String countName,
      GtfsFeedContainer feedContainer,
      String fileName,
      Class<E> clazz,
      Function<E, String> idExtractor) {

    var table = feedContainer.getTableForFilename(fileName);
    this.counts.put(
        countName,
        table.map(gtfsContainer -> loadUniqueCount(gtfsContainer, clazz, idExtractor)).orElse(0));
  }

  private <E extends GtfsEntity> int loadUniqueCount(
      GtfsEntityContainer<?, ?> table, Class<E> clazz, Function<E, String> idExtractor) {
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
    loadPathwaySignsFeature(feedContainer);
    loadPathwayDetailsFeature(feedContainer);
    loadPathwayConnectionsFeature(feedContainer);
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
    return feedContainer
        .getTableForFilename(GtfsStopTime.FILENAME)
        .map(table -> (GtfsStopTimeTableContainer) table)
        .map(GtfsStopTimeTableContainer::byTripIdMap)
        .map(
            byTripIdMap ->
                byTripIdMap.asMap().values().stream()
                    .anyMatch(
                        gtfsStopTimes -> {
                          boolean hasTripId = false,
                              hasLocationId = false,
                              hasStopId = false,
                              hasArrivalTime = false,
                              hasDepartureTime = false;

                          for (GtfsStopTime stopTime : gtfsStopTimes) {
                            hasTripId |= stopTime.hasTripId();
                            hasLocationId |= stopTime.hasLocationId();
                            hasStopId |= stopTime.hasStopId();
                            hasArrivalTime |= stopTime.hasArrivalTime();
                            hasDepartureTime |= stopTime.hasDepartureTime();

                            // Early return if all fields are found for this trip
                            if (hasTripId
                                && hasLocationId
                                && hasStopId
                                && hasArrivalTime
                                && hasDepartureTime) {
                              return true;
                            }
                          }
                          // Continue checking other trips
                          return false;
                        }))
        .orElse(false);
  }

  private void loadZoneBasedDemandResponsiveTransitFeature(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        new FeatureMetadata("Zone-Based Demand Responsive Services", "Flexible Services"),
        hasAtLeastOneTripWithOnlyLocationId(feedContainer));
  }

  private boolean hasAtLeastOneTripWithOnlyLocationId(GtfsFeedContainer feedContainer) {
    var optionalStopTimeTable = feedContainer.getTableForFilename(GtfsStopTime.FILENAME);
    if (optionalStopTimeTable.isPresent()) {
      for (GtfsEntity entity : optionalStopTimeTable.get().getEntities()) {
        if (entity instanceof GtfsStopTime) {
          GtfsStopTime stopTime = (GtfsStopTime) entity;
          if (stopTime.hasTripId() && stopTime.hasLocationId() && (!stopTime.hasStopId())) {
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

  private void loadPathwaySignsFeature(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        new FeatureMetadata("Pathway Signs", "Pathways"),
        hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsPathway.FILENAME,
                List.of((Function<GtfsPathway, Boolean>) GtfsPathway::hasSignpostedAs))
            || hasAtLeastOneRecordForFields(
                feedContainer,
                GtfsPathway.FILENAME,
                List.of((Function<GtfsPathway, Boolean>) GtfsPathway::hasReversedSignpostedAs)));
  }

  private void loadPathwayDetailsFeature(GtfsFeedContainer feedContainer) {
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

  private void loadPathwayConnectionsFeature(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        new FeatureMetadata("Pathway Connections", "Pathways"),
        hasAtLeastOneRecordInFile(feedContainer, GtfsPathway.FILENAME));
  }

  private void loadTraversalTimeFeature(GtfsFeedContainer feedContainer) {
    specFeatures.put(
        new FeatureMetadata("In-station Traversal Time", "Pathways"),
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

  private void loadAgencyData(
      GtfsEntityContainer<GtfsAgency, GtfsAgencyTableDescriptor> agencyTable) {
    for (GtfsAgency agency : agencyTable.getEntities()) {
      agencies.add(
          new AgencyMetadata(
              agency.agencyName(),
              agency.agencyUrl(),
              agency.agencyPhone(),
              agency.agencyEmail(),
              agency.agencyTimezone().getId()));
    }
  }

  private void loadFeedInfo(
      GtfsTableContainer<GtfsFeedInfo, GtfsFeedInfoTableDescriptor> feedTable) {
    var info = feedTable.getEntities().isEmpty() ? null : feedTable.getEntities().get(0);

    feedInfo.put(
        JsonReportFeedInfo.FEED_INFO_PUBLISHER_NAME,
        info == null ? "N/A" : info.feedPublisherName());
    feedInfo.put(
        JsonReportFeedInfo.FEED_INFO_PUBLISHER_URL, info == null ? "N/A" : info.feedPublisherUrl());
    feedInfo.put(
        JsonReportFeedInfo.FEED_INFO_FEED_CONTACT_EMAIL,
        info == null ? "N/A" : info.feedContactEmail());
    feedInfo.put(
        JsonReportFeedInfo.FEED_INFO_FEED_LANGUAGE,
        info == null ? "N/A" : info.feedLang().getDisplayLanguage());
    if (feedTable.hasColumn(GtfsFeedInfo.FEED_START_DATE_FIELD_NAME)) {
      if (info != null) {
        LocalDate localDate = info.feedStartDate().getLocalDate();
        feedInfo.put(JsonReportFeedInfo.FEED_INFO_FEED_START_DATE, checkLocalDate(localDate));
      }
    }
    if (feedTable.hasColumn(GtfsFeedInfo.FEED_END_DATE_FIELD_NAME)) {
      if (info != null) {
        LocalDate localDate = info.feedEndDate().getLocalDate();
        feedInfo.put(JsonReportFeedInfo.FEED_INFO_FEED_END_DATE, checkLocalDate(localDate));
      }
    }
  }

  private String checkLocalDate(LocalDate localDate) {
    String displayDate;
    if (localDate.toString().equals(LocalDate.EPOCH.toString())) {
      displayDate = "N/A";
    } else {
      displayDate = localDate.toString();
    }
    return displayDate;
  }

  /**
   * Loads the service date range by determining the earliest start date and the latest end date for
   * all services referenced with a trip\_id in `trips.txt`. It handles three cases: 1. When only
   * `calendars.txt` is used. 2. When only `calendar\_dates.txt` is used. 3. When both
   * `calendars.txt` and `calendar\_dates.txt` are used.
   *
   * @param tripContainer the container for `trips.txt` data
   * @param calendarTable the container for `calendars.txt` data
   * @param calendarDateTable the container for `calendar\_dates.txt` data
   */
  public void loadServiceWindow(
      GtfsTableContainer<GtfsTrip, ?> tripContainer,
      GtfsTableContainer<GtfsCalendar, ?> calendarTable,
      GtfsTableContainer<GtfsCalendarDate, ?> calendarDateTable) {
    List<GtfsTrip> trips = tripContainer.getEntities();

    LocalDate earliestStartDate = null;
    LocalDate latestEndDate = null;
    try {
      if ((calendarDateTable == null) && (calendarTable != null)) {
        // When only calendars.txt is used
        List<GtfsCalendar> calendars = calendarTable.getEntities();
        for (GtfsTrip trip : trips) {
          String serviceId = trip.serviceId();
          for (GtfsCalendar calendar : calendars) {
            if (calendar.serviceId().equals(serviceId)) {
              LocalDate startDate = calendar.startDate().getLocalDate();
              LocalDate endDate = calendar.endDate().getLocalDate();
              if (startDate != null || endDate != null) {
                if (startDate.toString().equals(LocalDate.EPOCH.toString())
                    || endDate.toString().equals(LocalDate.EPOCH.toString())) {
                  continue;
                }
                if (earliestStartDate == null || startDate.isBefore(earliestStartDate)) {
                  earliestStartDate = startDate;
                }
                if (latestEndDate == null || endDate.isAfter(latestEndDate)) {
                  latestEndDate = endDate;
                }
              }
            }
          }
        }
      } else if ((calendarDateTable != null) && (calendarTable == null)) {
        // When only calendar_dates.txt is used
        List<GtfsCalendarDate> calendarDates = calendarDateTable.getEntities();
        for (GtfsTrip trip : trips) {
          String serviceId = trip.serviceId();
          for (GtfsCalendarDate calendarDate : calendarDates) {
            if (calendarDate.serviceId().equals(serviceId)) {
              LocalDate date = calendarDate.date().getLocalDate();
              if (date != null && !date.toString().equals(LocalDate.EPOCH.toString())) {
                if (earliestStartDate == null || date.isBefore(earliestStartDate)) {
                  earliestStartDate = date;
                }
                if (latestEndDate == null || date.isAfter(latestEndDate)) {
                  latestEndDate = date;
                }
              }
            }
          }
        }
      } else if ((calendarTable != null) && (calendarDateTable != null)) {
        // When both calendars.txt and calendar_dates.txt are used
        Map<String, ServicePeriod> servicePeriods =
            CalendarUtil.buildServicePeriodMap(
                (GtfsCalendarTableContainer) calendarTable,
                (GtfsCalendarDateTableContainer) calendarDateTable);
        List<LocalDate> removedDates = new ArrayList<>();
        for (GtfsTrip trip : trips) {
          String serviceId = trip.serviceId();
          ServicePeriod servicePeriod = servicePeriods.get(serviceId);
          LocalDate startDate = servicePeriod.getServiceStart();
          LocalDate endDate = servicePeriod.getServiceEnd();
          if (startDate != null && endDate != null) {
            if (startDate.toString().equals(LocalDate.EPOCH.toString())
                || endDate.toString().equals(LocalDate.EPOCH.toString())) {
              continue;
            }
            if (earliestStartDate == null || startDate.isBefore(earliestStartDate)) {
              earliestStartDate = startDate;
            }
            if (latestEndDate == null || endDate.isAfter(latestEndDate)) {
              latestEndDate = endDate;
            }
          }
          removedDates.addAll(servicePeriod.getRemovedDays());
        }

        for (LocalDate date : removedDates) {
          if (date.isEqual(earliestStartDate)) {
            earliestStartDate = date.plusDays(1);
          }
          if (date.isEqual(latestEndDate)) {
            latestEndDate = date.minusDays(1);
          }
        }
      }
    } catch (Exception e) {
      logger.atSevere().withCause(e).log("Error while loading Service Window");
    } finally {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
      if ((earliestStartDate == null) && (latestEndDate == null)) {
        feedInfo.put(JsonReportFeedInfo.FEED_INFO_SERVICE_WINDOW, "N/A");
      } else if (earliestStartDate == null && latestEndDate != null) {
        feedInfo.put(JsonReportFeedInfo.FEED_INFO_SERVICE_WINDOW, latestEndDate.format(formatter));
      } else if (latestEndDate == null && earliestStartDate != null) {
        if (earliestStartDate.isAfter(latestEndDate)) {
          feedInfo.put(JsonReportFeedInfo.FEED_INFO_SERVICE_WINDOW, "N/A");
        } else {
          feedInfo.put(
              JsonReportFeedInfo.FEED_INFO_SERVICE_WINDOW, earliestStartDate.format(formatter));
        }
      } else {
        StringBuilder serviceWindow = new StringBuilder();
        serviceWindow.append(earliestStartDate);
        serviceWindow.append(" to ");
        serviceWindow.append(latestEndDate);
        feedInfo.put(JsonReportFeedInfo.FEED_INFO_SERVICE_WINDOW, serviceWindow.toString());
      }
      feedInfo.put(
          JsonReportFeedInfo.FEED_INFO_SERVICE_WINDOW_START,
          earliestStartDate == null ? "" : earliestStartDate.toString());
      feedInfo.put(
          JsonReportFeedInfo.FEED_INFO_SERVICE_WINDOW_END,
          latestEndDate == null ? "" : latestEndDate.toString());
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
      if (table.getTableStatus() != TableStatus.MISSING_FILE) {
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

  public Boolean hasFlexFeatures() {
    return specFeatures.keySet().stream()
        .anyMatch(
            feature ->
                feature.getFeatureGroup() != null
                    && feature.getFeatureGroup().equals("Flexible Services")
                    && !Objects.equals(feature.getFeatureName(), "Continuous Stops")
                    && specFeatures.get(feature));
  }
}
