package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.util.geojson.GeometryType;

@RunWith(JUnit4.class)
public class OverlappingPickupDropOffZoneValidatorTest {
  @Test
  public void overlappingPickupDropOffZonesShouldGenerateNotice() {
    // If pickup and drop off windows overlap and zones overlap, a notice should be
    // generated for stop times within the same trip
    NoticeContainer noticeContainer = new NoticeContainer();
    GeometryFactory geometryFactory = new GeometryFactory();
    GtfsGeoJsonFileDescriptor descriptor = new GtfsGeoJsonFileDescriptor();
    ArrayList<GtfsGeoJsonFeature> gtfsGeoJsonFeatures =
        new ArrayList<>(
            List.of(
                new GtfsGeoJsonFeature.Builder()
                    .featureId("1")
                    .geometryDefinition(
                        geometryFactory.createPolygon(
                            new Coordinate[] {
                              new Coordinate(0, 0),
                              new Coordinate(4, 0),
                              new Coordinate(4, 4),
                              new Coordinate(0, 4),
                              new Coordinate(0, 0) // Close the polygon
                            }))
                    .geometryType(GeometryType.POLYGON)
                    .build(),
                new GtfsGeoJsonFeature.Builder()
                    .featureId("2")
                    .geometryDefinition(
                        geometryFactory.createPolygon(
                            new Coordinate[] {
                              new Coordinate(2, 2),
                              new Coordinate(6, 2),
                              new Coordinate(6, 6),
                              new Coordinate(2, 6),
                              new Coordinate(2, 2) // Close the polygon
                            }))
                    .geometryType(GeometryType.POLYGON)
                    .build()));
    GtfsGeoJsonFeaturesContainer geoJsonFeaturesContainer =
        descriptor.createContainerForEntities(gtfsGeoJsonFeatures, noticeContainer);

    ArrayList<GtfsStopTime> stopTimes =
        new ArrayList<>(
            List.of(
                new GtfsStopTime.Builder()
                    .setTripId("t0")
                    .setStopSequence(1)
                    .setLocationId("1")
                    .setPickupType(1)
                    .setStartPickupDropOffWindow(GtfsTime.fromString("05:00:00"))
                    .setEndPickupDropOffWindow(GtfsTime.fromString("07:00:00"))
                    .build(),
                new GtfsStopTime.Builder()
                    .setTripId("t0")
                    .setStopSequence(2)
                    .setLocationId("2")
                    .setPickupType(1)
                    .setStartPickupDropOffWindow(GtfsTime.fromString("06:00:00"))
                    .setEndPickupDropOffWindow(GtfsTime.fromString("08:00:00"))
                    .build()));
    GtfsStopTimeTableContainer stopTimeTableContainer =
        GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);
    OverlappingPickupDropOffZoneValidator validator =
        new OverlappingPickupDropOffZoneValidator(stopTimeTableContainer, geoJsonFeaturesContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).hasSize(1);

    assertThat(noticeContainer.getValidationNotices().iterator().next())
        .isInstanceOf(
            OverlappingPickupDropOffZoneValidator.OverlappingZoneAndPickupDropOffWindowNotice
                .class);
  }

  @Test
  public void nonOverlappingPickupDropOffWindowsShouldNotGenerateNotice() {
    // If pickup and drop off windows do not overlap and zones overlap, a notice should not be
    // generated for stop times within the same trip
    NoticeContainer noticeContainer = new NoticeContainer();
    GeometryFactory geometryFactory = new GeometryFactory();
    GtfsGeoJsonFileDescriptor descriptor = new GtfsGeoJsonFileDescriptor();
    ArrayList<GtfsGeoJsonFeature> gtfsGeoJsonFeatures =
        new ArrayList<>(
            List.of(
                new GtfsGeoJsonFeature.Builder()
                    .featureId("1")
                    .geometryDefinition(
                        geometryFactory.createPolygon(
                            new Coordinate[] {
                              new Coordinate(0, 0),
                              new Coordinate(4, 0),
                              new Coordinate(4, 4),
                              new Coordinate(0, 4),
                              new Coordinate(0, 0) // Close the polygon
                            }))
                    .geometryType(GeometryType.POLYGON)
                    .build(),
                new GtfsGeoJsonFeature.Builder()
                    .featureId("2")
                    .geometryDefinition(
                        geometryFactory.createPolygon(
                            new Coordinate[] {
                              new Coordinate(2, 2),
                              new Coordinate(6, 2),
                              new Coordinate(6, 6),
                              new Coordinate(2, 6),
                              new Coordinate(2, 2) // Close the polygon
                            }))
                    .geometryType(GeometryType.POLYGON)
                    .build()));
    GtfsGeoJsonFeaturesContainer geoJsonFeaturesContainer =
        descriptor.createContainerForEntities(gtfsGeoJsonFeatures, noticeContainer);

    ArrayList<GtfsStopTime> stopTimes =
        new ArrayList<>(
            List.of(
                new GtfsStopTime.Builder()
                    .setTripId("t0")
                    .setStopSequence(1)
                    .setLocationId("1")
                    .setStartPickupDropOffWindow(GtfsTime.fromString("05:00:00"))
                    .setEndPickupDropOffWindow(GtfsTime.fromString("07:00:00"))
                    .build(),
                new GtfsStopTime.Builder()
                    .setTripId("t0")
                    .setStopSequence(2)
                    .setLocationId("2")
                    .setStartPickupDropOffWindow(GtfsTime.fromString("07:00:00"))
                    .setEndPickupDropOffWindow(GtfsTime.fromString("08:00:00"))
                    .build()));
    GtfsStopTimeTableContainer stopTimeTableContainer =
        GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);
    OverlappingPickupDropOffZoneValidator validator =
        new OverlappingPickupDropOffZoneValidator(stopTimeTableContainer, geoJsonFeaturesContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).hasSize(0);
  }

  @Test
  public void nonOverlappingPickupDropOffZonesShouldNotGenerateNotice() {
    // If pickup and drop off windows overlap but zones do NOT overlap, no notice should be
    // generated
    NoticeContainer noticeContainer = new NoticeContainer();
    GeometryFactory geometryFactory = new GeometryFactory();
    GtfsGeoJsonFileDescriptor descriptor = new GtfsGeoJsonFileDescriptor();
    ArrayList<GtfsGeoJsonFeature> gtfsGeoJsonFeatures =
        new ArrayList<>(
            List.of(
                new GtfsGeoJsonFeature.Builder()
                    .featureId("1")
                    .geometryDefinition(
                        geometryFactory.createPolygon(
                            new Coordinate[] {
                              new Coordinate(0, 0),
                              new Coordinate(2, 0),
                              new Coordinate(2, 2),
                              new Coordinate(0, 2),
                              new Coordinate(0, 0) // Close the polygon
                            }))
                    .geometryType(GeometryType.POLYGON)
                    .build(),
                new GtfsGeoJsonFeature.Builder()
                    .featureId("2")
                    .geometryDefinition(
                        geometryFactory.createPolygon(
                            new Coordinate[] {
                              new Coordinate(5, 5),
                              new Coordinate(7, 5),
                              new Coordinate(7, 7),
                              new Coordinate(5, 7),
                              new Coordinate(5, 5) // Close the polygon
                            }))
                    .geometryType(GeometryType.POLYGON)
                    .build()));
    GtfsGeoJsonFeaturesContainer geoJsonFeaturesContainer =
        descriptor.createContainerForEntities(gtfsGeoJsonFeatures, noticeContainer);

    ArrayList<GtfsStopTime> stopTimes =
        new ArrayList<>(
            List.of(
                new GtfsStopTime.Builder()
                    .setTripId("t0")
                    .setStopSequence(1)
                    .setLocationId("1")
                    .setStartPickupDropOffWindow(GtfsTime.fromString("05:00:00"))
                    .setEndPickupDropOffWindow(GtfsTime.fromString("07:00:00"))
                    .build(),
                new GtfsStopTime.Builder()
                    .setTripId("t0")
                    .setStopSequence(2)
                    .setLocationId("2")
                    .setStartPickupDropOffWindow(GtfsTime.fromString("06:00:00"))
                    .setEndPickupDropOffWindow(GtfsTime.fromString("08:00:00"))
                    .build()));
    GtfsStopTimeTableContainer stopTimeTableContainer =
        GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);
    OverlappingPickupDropOffZoneValidator validator =
        new OverlappingPickupDropOffZoneValidator(stopTimeTableContainer, geoJsonFeaturesContainer);
    validator.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }
}
