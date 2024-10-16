package org.mobilitydata.gtfsvalidator.report.model;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.input.DateForValidation;
import org.mobilitydata.gtfsvalidator.input.GtfsInput;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.validator.*;

public class FeedMetadataTest {

  @Rule public final TemporaryFolder tmpDir = new TemporaryFolder();
  GtfsFeedLoader feedLoaderMock;
  ValidationContext validationContext =
      ValidationContext.builder()
          .setCountryCode(CountryCode.forStringOrUnknown("CA"))
          .setDateForValidation(new DateForValidation(LocalDate.now()))
          .build();
  ValidatorLoader validatorLoader;
  File rootDir;
  NoticeContainer noticeContainer = new NoticeContainer();

  private GtfsTableContainer<GtfsTrip, ?> tripContainer;
  private GtfsTableContainer<GtfsCalendar, ?> calendarTable;
  private GtfsTableContainer<GtfsCalendarDate, ?> calendarDateTable;
  private FeedMetadata feedMetadata = new FeedMetadata();

  private void createDataFile(String filename, String content) throws IOException {
    File dataFile = tmpDir.newFile("data/" + filename);
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
      writer.write(content);
    }
  }

  @Before
  public void setup() throws IOException, ValidatorLoaderException {
    rootDir = tmpDir.newFolder("data");
    String agencyContent =
        "agency_id, agency_name, agency_url, agency_timezone\n"
            + "1, name, https://dummy.ca, America/Los_Angeles\n"
            + "2, name, https://dummy.ca, America/Los_Angeles\n";
    createDataFile("agency.txt", agencyContent);
    validatorLoader =
        ValidatorLoader.createForClasses(ClassGraphDiscovery.discoverValidatorsInDefaultPackage());
  }

  public static GtfsTrip createTrip(int csvRowNumber, String serviceId) {
    return new GtfsTrip.Builder().setCsvRowNumber(csvRowNumber).setServiceId(serviceId).build();
  }

  public static GtfsCalendar createCalendar(
      int csvRowNumber, String serviceId, GtfsDate startDate, GtfsDate endDate) {
    return new GtfsCalendar.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setServiceId(serviceId)
        .setStartDate(startDate)
        .setEndDate(endDate)
        .build();
  }

  public static GtfsCalendarDate createCalendarDate(
      int csvRowNumber,
      String serviceId,
      GtfsDate date,
      GtfsCalendarDateExceptionType exceptionType) {
    return new GtfsCalendarDate.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setServiceId(serviceId)
        .setDate(date)
        .setExceptionType(exceptionType)
        .build();
  }

  @Test
  public void testLoadServiceWindow() {
    GtfsTrip trip1 = createTrip(1, "JUN24-MVS-SUB-Weekday-01");
    GtfsTrip trip2 = createTrip(2, "JUN24-MVS-SUB-Weekday-02");
    // when(tripContainer.getEntities()).thenReturn(List.of(trip1, trip2));
    tripContainer = GtfsTripTableContainer.forEntities(List.of(trip1, trip2), noticeContainer);
    GtfsCalendar calendar1 =
        createCalendar(
            1,
            "JUN24-MVS-SUB-Weekday-01",
            GtfsDate.fromLocalDate(LocalDate.of(2024, 1, 1)),
            GtfsDate.fromLocalDate(LocalDate.of(2024, 12, 20)));
    GtfsCalendar calendar2 =
        createCalendar(
            2,
            "JUN24-MVS-SUB-Weekday-02",
            GtfsDate.fromLocalDate(LocalDate.of(2024, 6, 1)),
            GtfsDate.fromLocalDate(LocalDate.of(2024, 12, 31)));
    // when(calendarTable.getEntities()).thenReturn(List.of(calendar1, calendar2));
    calendarTable =
        GtfsCalendarTableContainer.forEntities(List.of(calendar1, calendar2), noticeContainer);
    GtfsCalendarDate calendarDate1 =
        createCalendarDate(
            1,
            "JUN24-MVS-SUB-Weekday-01",
            GtfsDate.fromLocalDate(LocalDate.of(2024, 1, 1)),
            GtfsCalendarDateExceptionType.SERVICE_REMOVED);
    GtfsCalendarDate calendarDate2 =
        createCalendarDate(
            2,
            "JUN24-MVS-SUB-Weekday-02",
            GtfsDate.fromLocalDate(LocalDate.of(2024, 6, 1)),
            GtfsCalendarDateExceptionType.SERVICE_ADDED);
    // when(calendarDateTable.getEntities()).thenReturn(List.of(calendarDate1, calendarDate2));
    calendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(
            List.of(calendarDate1, calendarDate2), noticeContainer);

    // Call the method
    feedMetadata.loadServiceWindow(tripContainer, calendarTable, calendarDateTable);

    // Verify the result
    String expectedServiceWindow = "2024-01-02 to 2024-12-31";
    assertEquals(
        expectedServiceWindow, feedMetadata.feedInfo.get(FeedMetadata.FEED_INFO_SERVICE_WINDOW));
  }

  private void validateSpecFeature(
      String specFeature,
      Boolean expectedValue,
      ImmutableList<Class<? extends GtfsFileDescriptor<?>>> tableDescriptors)
      throws IOException, InterruptedException {
    feedLoaderMock = new GtfsFeedLoader(tableDescriptors);
    try (GtfsInput gtfsInput = GtfsInput.createFromPath(rootDir.toPath(), noticeContainer)) {
      GtfsFeedContainer feedContainer =
          feedLoaderMock.loadAndValidate(
              gtfsInput,
              new DefaultValidatorProvider(validationContext, validatorLoader),
              new NoticeContainer());
      FeedMetadata feedMetadata = FeedMetadata.from(feedContainer, gtfsInput.getFilenames());
      assertThat(feedMetadata.specFeatures.get(new FeatureMetadata(specFeature, null)))
          .isEqualTo(expectedValue);
    }
  }

  @Test
  /**
   * This method is to test when both route_color and route_text_color are present in routes.txt,
   * and they each have two records
   */
  public void containsRouteColorsFeatureTest() throws IOException, InterruptedException {
    String routesContent =
        "route_id,agency_id,route_short_name,route_long_name,route_desc,route_type,route_url,route_color,route_text_color\n"
            + "01,LTC -2023 Spring Schedules,01,Route 1,,3,,70C2DA,000000\n"
            + "02,LTC -2023 Spring Schedules,02,Route 2,,3,,0080C0,000000\n";
    createDataFile("routes.txt", routesContent);
    validateSpecFeature(
        "Route Colors",
        true,
        ImmutableList.of(GtfsRouteTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  /**
   * This method is to test when both route_color and route_text_color are present in routes.txt,
   * and none of them has records
   */
  @Test
  public void omitsRouteColorsFeatureTest1() throws IOException, InterruptedException {
    String routesContent =
        "route_id,agency_id,route_short_name,route_long_name,route_desc,route_type,route_url,route_color,route_text_color\n"
            + "01,LTC -2023 Spring Schedules,01,Route 1,,3,,,\n"
            + "02,LTC -2023 Spring Schedules,02,Route 2,,3,,,\n";
    createDataFile("routes.txt", routesContent);
    validateSpecFeature(
        "Route Colors",
        false,
        ImmutableList.of(GtfsRouteTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  /**
   * This method is to test when both route_color and route_text_color are present in routes.txt,
   * and they each have one record
   */
  @Test
  public void omitsRouteColorsFeatureTest2() throws IOException, InterruptedException {
    String routesContent =
        "route_id,agency_id,route_short_name,route_long_name,route_desc,route_type,route_url,route_color,route_text_color\n"
            + "01,LTC -2023 Spring Schedules,01,Route 1,,3,,,70C2DA\n"
            + "02,LTC -2023 Spring Schedules,02,Route 2,,3,,0080C0,\n";
    createDataFile("routes.txt", routesContent);
    validateSpecFeature(
        "Route Colors",
        true,
        ImmutableList.of(GtfsRouteTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  /**
   * This method is to test when both route_color and route_text_color are present in routes.txt,
   * and one has two records, one has none
   */
  @Test
  public void omitsRouteColorsFeatureTest3() throws IOException, InterruptedException {
    String routesContent =
        "route_id,agency_id,route_short_name,route_long_name,route_desc,route_type,route_url,route_color,route_text_color\n"
            + "01,LTC -2023 Spring Schedules,01,Route 1,,3,,0080C0,000000\n"
            + "02,LTC -2023 Spring Schedules,02,Route 2,,3,,,\n";
    createDataFile("routes.txt", routesContent);
    validateSpecFeature(
        "Route Colors",
        true,
        ImmutableList.of(GtfsRouteTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  /**
   * This method is to test when route_color is present and route_text_color is missing in
   * routes.txt
   */
  @Test
  public void omitsRouteColorsFeatureTest4() throws IOException, InterruptedException {
    String routesContent =
        "route_id,agency_id,route_short_name,route_long_name,route_desc,route_type,route_url,route_color\n"
            + "01,LTC -2023 Spring Schedules,01,Route 1,,3,,,\n"
            + "02,LTC -2023 Spring Schedules,02,Route 2,,3,,70C2DA\n";
    createDataFile("routes.txt", routesContent);
    validateSpecFeature(
        "Route Colors",
        false,
        ImmutableList.of(GtfsRouteTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  /** This method is to test when both route_color and route_text_color are missing in routes.txt */
  @Test
  public void omitsRouteColorsFeatureTest5() throws IOException, InterruptedException {
    String routesContent =
        "route_id,agency_id,route_short_name,route_long_name,route_desc,route_type,route_url\n"
            + "01,LTC -2023 Spring Schedules,01,Route 1,,3,\n"
            + "02,LTC -2023 Spring Schedules,02,Route 2,,3,\n";
    createDataFile("routes.txt", routesContent);
    validateSpecFeature(
        "Route Colors",
        false,
        ImmutableList.of(GtfsRouteTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void containsPathwaySignsFeatureTest() throws IOException, InterruptedException {
    String pathwayContent =
        "pathway_id,from_stop_id,to_stop_id,pathway_mode,is_bidirectional,signposted_as,reversed_signposted_as\n"
            + "pathway1,stop1,stop2,1,1,sign1,rsign1\n"
            + "pathway2,stop2,stop3,2,0,sign2,rsign2\n";
    createDataFile("pathways.txt", pathwayContent);
    validateSpecFeature(
        "Pathway Signs",
        true,
        ImmutableList.of(GtfsPathwayTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void omitsPathwaySignsFeatureTest() throws IOException, InterruptedException {
    String pathwayContent = "pathway_id,from_stop_id,to_stop_id,pathway_mode,is_bidirectional\n";
    createDataFile("pathways.txt", pathwayContent);
    validateSpecFeature(
        "Pathway Signs",
        false,
        ImmutableList.of(GtfsPathwayTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void containsPathwayDetailsFeatureTest() throws IOException, InterruptedException {
    String pathwayContent =
        "pathway_id,from_stop_id,to_stop_id,pathway_mode,is_bidirectional,traversal_time,max_slope\n"
            + "pathway1,stop1,stop2,1,1,120,0\n"
            + "pathway2,stop2,stop3,2,0,300,1.1\n";
    createDataFile("pathways.txt", pathwayContent);
    validateSpecFeature(
        "Pathway Details",
        true,
        ImmutableList.of(GtfsPathwayTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void omitsPathwayDetailsFeatureTest() throws IOException, InterruptedException {
    String pathwayContent = "pathway_id,from_stop_id,to_stop_id,pathway_mode,is_bidirectional\n";
    createDataFile("pathways.txt", pathwayContent);
    validateSpecFeature(
        "Pathway Details",
        false,
        ImmutableList.of(GtfsPathwayTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void containsPathwayConnectionFeatureTest() throws IOException, InterruptedException {
    String pathwayContent =
        "pathway_id,from_stop_id,to_stop_id,pathway_mode,is_bidirectional\n"
            + "pathway1,stop1,stop2,1,1\n"
            + "pathway2,stop2,stop3,2,0\n";
    createDataFile("pathways.txt", pathwayContent);
    validateSpecFeature(
        "Pathway Connections",
        true,
        ImmutableList.of(GtfsPathwayTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void omitsPathwayConnectionsFeatureTest() throws IOException, InterruptedException {
    String pathwayContent = "pathway_id,from_stop_id,to_stop_id,pathway_mode,is_bidirectional\n";
    createDataFile("pathways.txt", pathwayContent);
    validateSpecFeature(
        "Pathway Connections",
        false,
        ImmutableList.of(GtfsPathwayTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void omitsFeatures() throws IOException, InterruptedException {
    validateSpecFeature(
        "Pathway Connections",
        false,
        ImmutableList.of(GtfsPathwayTableDescriptor.class, GtfsAgencyTableDescriptor.class));
    validateSpecFeature(
        "Shapes",
        false,
        ImmutableList.of(GtfsShapeTableDescriptor.class, GtfsAgencyTableDescriptor.class));
    validateSpecFeature(
        "Route Colors",
        false,
        ImmutableList.of(GtfsRouteTableDescriptor.class, GtfsAgencyTableDescriptor.class));
    validateSpecFeature(
        "Transfers",
        false,
        ImmutableList.of(GtfsTransferTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void containsShapesFeatureTest() throws IOException, InterruptedException {
    String shapesContent =
        "shape_id,shape_pt_lat,shape_pt_lon,shape_pt_sequence\n" + "A_shp,37.61956,-122.48161,0\n";
    createDataFile("shapes.txt", shapesContent);
    validateSpecFeature(
        "Shapes",
        true,
        ImmutableList.of(GtfsShapeTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void containsTransfersFeatureTest() throws IOException, InterruptedException {
    String transfersContent =
        "from_stop_id,to_stop_id,transfer_type,min_transfer_time\n" + "COMMDEV1,COMMDEV4,,\n";
    createDataFile("transfers.txt", transfersContent);
    validateSpecFeature(
        "Transfers",
        true,
        ImmutableList.of(GtfsTransferTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void omitsTransfersFeatureTest() throws IOException, InterruptedException {
    String transfersContent = "from_stop_id,to_stop_id,transfer_type,min_transfer_time\n";
    createDataFile(GtfsTransfer.FILENAME, transfersContent);
    validateSpecFeature(
        "Transfers",
        false,
        ImmutableList.of(GtfsTransferTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void containsFrequencyBasedTripFeatureTest() throws IOException, InterruptedException {
    String content =
        "trip_id, start_time, end_time, headway_secs\n" + "dummy1, 01:01:01, 01:01:02, 1\n";
    createDataFile(GtfsFrequency.FILENAME, content);
    validateSpecFeature(
        "Frequencies",
        true,
        ImmutableList.of(GtfsFrequencyTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void omitsFrequencyBasedTripFeatureTest() throws IOException, InterruptedException {
    String content = "trip_id, start_time, end_time, headway_secs\n";
    createDataFile(GtfsFrequency.FILENAME, content);
    validateSpecFeature(
        "Frequencies",
        false,
        ImmutableList.of(GtfsFrequencyTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void containsFeedInformationFeatureTest() throws IOException, InterruptedException {
    String content =
        "feed_publisher_name, feed_publisher_url, feed_lang\n"
            + "dummyPublisher, http://dummyurl.com, en\n";
    createDataFile(GtfsFeedInfo.FILENAME, content);
    validateSpecFeature(
        "Feed Information",
        true,
        ImmutableList.of(GtfsFeedInfoTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void omitsFeedInformationFeatureTest() throws IOException, InterruptedException {
    String content = "feed_publisher_name, feed_publisher_url, feed_lang\n";
    createDataFile(GtfsFeedInfo.FILENAME, content);
    validateSpecFeature(
        "Feed Information",
        false,
        ImmutableList.of(GtfsFeedInfoTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void containsAttributionsFeatureTest() throws IOException, InterruptedException {
    String content = "organization_name\n" + "dummyAttribution\n";
    createDataFile(GtfsAttribution.FILENAME, content);
    validateSpecFeature(
        "Attributions",
        true,
        ImmutableList.of(GtfsAttributionTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void omitsAttributionsFeatureTest() throws IOException, InterruptedException {
    String content = "organization_name\n";
    createDataFile(GtfsAttribution.FILENAME, content);
    validateSpecFeature(
        "Attributions",
        false,
        ImmutableList.of(GtfsAttributionTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void containsTranslationsFeatureTest() throws IOException, InterruptedException {
    String content =
        "table_name, field_name, language, translation\n" + "agency, agency_name, fr, Agence\n";
    createDataFile(GtfsTranslation.FILENAME, content);
    validateSpecFeature(
        "Translations",
        true,
        ImmutableList.of(GtfsTranslationTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void omitsTranslationsFeatureTest() throws IOException, InterruptedException {
    String content = "table_name, field_name, language, translation\n";
    createDataFile(GtfsTranslation.FILENAME, content);
    validateSpecFeature(
        "Translations",
        false,
        ImmutableList.of(GtfsTranslationTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void containsFareMediaFeatureTest() throws IOException, InterruptedException {
    String content = "fare_media_id, fare_media_type\n" + "dummyFareId, 0\n";
    createDataFile(GtfsFareMedia.FILENAME, content);
    validateSpecFeature(
        "Fare Media",
        true,
        ImmutableList.of(GtfsFareMediaTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void omitsFareMediaFeatureTest() throws IOException, InterruptedException {
    String content = "fare_media_id, fare_media_type\n";
    createDataFile(GtfsFareMedia.FILENAME, content);
    validateSpecFeature(
        "Fare Media",
        false,
        ImmutableList.of(GtfsFareMediaTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  // Zone-Based Fares
  @Test
  public void containsZoneBasedFaresFeatureTest() throws IOException, InterruptedException {
    String content = "area_id, stop_id\n" + "dummyArea, dummyStop\n";
    createDataFile(GtfsArea.FILENAME, content);
    validateSpecFeature(
        "Zone-Based Fares",
        true,
        ImmutableList.of(GtfsAreaTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void omitsZoneBasedFaresFeatureTest() throws IOException, InterruptedException {
    String content = "area_id, stop_id\n";
    createDataFile(GtfsStopArea.FILENAME, content);
    validateSpecFeature(
        "Zone-Based Fares",
        false,
        ImmutableList.of(GtfsStopAreaTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void containsHeadsignsFeature1() throws IOException, InterruptedException {
    String content = "route_id, service_id, trip_id, trip_headsign\n" + "1, 2, 3, headsign_dummy\n";
    createDataFile(GtfsTrip.FILENAME, content);
    content = "trip_id, stop_id, stop_sequence, stop_headsign\n" + "1,2,3, headsign_dummy";
    createDataFile(GtfsStopTime.FILENAME, content);
    validateSpecFeature(
        "Headsigns",
        true,
        ImmutableList.of(
            GtfsAgencyTableDescriptor.class,
            GtfsStopTimeTableDescriptor.class,
            GtfsTripTableDescriptor.class));
  }

  @Test
  public void containsHeadsignsFeature2() throws IOException, InterruptedException {
    String content = "route_id, service_id, trip_id, trip_headsign\n" + "1, 2, 3, headsign_dummy\n";
    createDataFile(GtfsTrip.FILENAME, content);
    validateSpecFeature(
        "Headsigns",
        true,
        ImmutableList.of(
            GtfsAgencyTableDescriptor.class,
            GtfsStopTimeTableDescriptor.class,
            GtfsTripTableDescriptor.class));
  }

  @Test
  public void containsHeadsignsFeature3() throws IOException, InterruptedException {
    String content = "trip_id, stop_id, stop_sequence, stop_headsign\n" + "1,2,3, headsign_dummy";
    createDataFile(GtfsStopTime.FILENAME, content);
    validateSpecFeature(
        "Headsigns",
        true,
        ImmutableList.of(
            GtfsAgencyTableDescriptor.class,
            GtfsStopTimeTableDescriptor.class,
            GtfsTripTableDescriptor.class));
  }

  @Test
  public void omitsHeadsignsFeature() throws IOException, InterruptedException {
    String content = "trip_id, stop_id, stop_sequence, stop_headsign\n";
    createDataFile(GtfsStopTime.FILENAME, content);
    validateSpecFeature(
        "Headsigns",
        false,
        ImmutableList.of(
            GtfsAgencyTableDescriptor.class,
            GtfsStopTimeTableDescriptor.class,
            GtfsTripTableDescriptor.class));
  }

  @Test
  public void containsWheelchairAccessibilityFeature() throws IOException, InterruptedException {
    String content = "route_id, service_id, trip_id, wheelchair_accessible\n" + "1, 2, 3, 1\n";
    createDataFile(GtfsTrip.FILENAME, content);
    validateSpecFeature(
        "Trips Wheelchair Accessibility",
        true,
        ImmutableList.of(GtfsAgencyTableDescriptor.class, GtfsTripTableDescriptor.class));
  }

  @Test
  public void containsDeviatedFixedRouteFeatureTest() throws IOException, InterruptedException {
    // Create stop times with various field combinations for the same trip
    String stopTimesContent =
        "trip_id, stop_sequence, arrival_time, departure_time, stop_id, location_id\n"
            + "trip1,1,01:00:00,01:30:00,stop1,location1\n"
            + "trip1,2,,02:30:00,,location2\n"
            + "trip1,3,02:00:00,,stop2,location2";

    createDataFile(GtfsStopTime.FILENAME, stopTimesContent);

    // Validate that the feature is present
    validateSpecFeature(
        "Predefined Routes with Deviation",
        true,
        ImmutableList.of(
            GtfsAgencyTableDescriptor.class,
            GtfsStopTimeTableDescriptor.class,
            GtfsTripTableDescriptor.class));
  }
}
