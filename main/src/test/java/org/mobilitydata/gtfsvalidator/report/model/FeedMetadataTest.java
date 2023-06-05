package org.mobilitydata.gtfsvalidator.report.model;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;
import org.mobilitydata.gtfsvalidator.input.GtfsInput;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.validator.*;

public class FeedMetadataTest {

  @Rule public final TemporaryFolder tmpDir = new TemporaryFolder();
  GtfsFeedLoader feedLoaderMock;
  ValidationContext validationContext =
      ValidationContext.builder()
          .setCountryCode(CountryCode.forStringOrUnknown("CA"))
          .setCurrentDateTime(new CurrentDateTime(ZonedDateTime.now(ZoneId.systemDefault())))
          .build();
  ValidatorLoader validatorLoader;
  File rootDir;

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
            + "1, name, https://dummy.dummy, CA\n"
            + "2, name, https://dummy.dummy, CA\n";
    createDataFile("agency.txt", agencyContent);
    validatorLoader =
        ValidatorLoader.createForClasses(ClassGraphDiscovery.discoverValidatorsInDefaultPackage());
  }

  private void validateSpecFeature(
      String specFeature,
      String expectedValue,
      ImmutableList<Class<? extends GtfsTableDescriptor<?>>> tableDescriptors)
      throws IOException, InterruptedException {
    feedLoaderMock = new GtfsFeedLoader(tableDescriptors);
    try (GtfsInput gtfsInput = GtfsInput.createFromPath(rootDir.toPath())) {
      GtfsFeedContainer feedContainer =
          feedLoaderMock.loadAndValidate(
              gtfsInput,
              new DefaultValidatorProvider(validationContext, validatorLoader),
              new NoticeContainer());
      FeedMetadata feedMetadata = FeedMetadata.from(feedContainer, gtfsInput.getFilenames());
      assertThat(feedMetadata.specFeatures.get(specFeature)).isEqualTo(expectedValue);
    }
  }

  @Test
  public void containsRouteNamesComponentTest() throws IOException, InterruptedException {
    String routesContent =
        "route_id,agency_id,route_short_name,route_long_name,route_type\n"
            + "1,1,Short Name,Long Name,1\n"
            + "2,1,,,1\n";
    createDataFile("routes.txt", routesContent);
    validateSpecFeature(
        "Route Names",
        "Yes",
        ImmutableList.of(GtfsRouteTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void omitsRouteNamesComponentTest1() throws IOException, InterruptedException {
    String routesContent =
        "route_id,agency_id,route_short_name,route_long_name,route_type\n"
            + "1,1,,,1\n"
            + "2,1,,,1\n";
    createDataFile("routes.txt", routesContent);
    validateSpecFeature(
        "Route Names",
        "No",
        ImmutableList.of(GtfsRouteTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void omitsRouteNamesComponentTest2() throws IOException, InterruptedException {
    String routesContent =
        "route_id,agency_id,route_short_name,route_long_name,route_type\n"
            + "1,1,Short Name,,1\n"
            + "2,1,,,1\n";
    createDataFile("routes.txt", routesContent);
    validateSpecFeature(
        "Route Names",
        "No",
        ImmutableList.of(GtfsRouteTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void omitsRouteNamesComponentTest3() throws IOException, InterruptedException {
    String routesContent =
        "route_id,agency_id,route_short_name,route_long_name,route_type\n"
            + "1,1,,Long Name,1\n"
            + "2,1,,,1\n";
    createDataFile("routes.txt", routesContent);
    validateSpecFeature(
        "Route Names",
        "No",
        ImmutableList.of(GtfsRouteTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void containsPathwaysComponentTest() throws IOException, InterruptedException {
    String pathwayContent =
        "pathway_id,from_stop_id,to_stop_id,pathway_mode,is_bidirectional\n"
            + "pathway1,stop1,stop2,1,1\n"
            + "pathway2,stop2,stop3,2,0\n";
    createDataFile("pathways.txt", pathwayContent);
    validateSpecFeature(
        "Pathways",
        "Yes",
        ImmutableList.of(GtfsPathwayTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void omitsPathwaysComponentTest() throws IOException, InterruptedException {
    String pathwayContent = "pathway_id,from_stop_id,to_stop_id,pathway_mode,is_bidirectional\n";
    createDataFile("pathways.txt", pathwayContent);
    validateSpecFeature(
        "Pathways",
        "No",
        ImmutableList.of(GtfsPathwayTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void omitsComponents() throws IOException, InterruptedException {
    validateSpecFeature(
        "Pathways",
        "No",
        ImmutableList.of(GtfsPathwayTableDescriptor.class, GtfsAgencyTableDescriptor.class));
    validateSpecFeature(
        "Route Names",
        "No",
        ImmutableList.of(GtfsPathwayTableDescriptor.class, GtfsAgencyTableDescriptor.class));
    validateSpecFeature(
        "Shapes",
        "No",
        ImmutableList.of(GtfsPathwayTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }

  @Test
  public void containsShapesComponentTest() throws IOException, InterruptedException {
    String shapesContent =
        "shape_id,shape_pt_lat,shape_pt_lon,shape_pt_sequence\n" + "A_shp,37.61956,-122.48161,0\n";
    createDataFile("shapes.txt", shapesContent);
    validateSpecFeature(
        "Shapes",
        "Yes",
        ImmutableList.of(GtfsShapeTableDescriptor.class, GtfsAgencyTableDescriptor.class));
  }
}
