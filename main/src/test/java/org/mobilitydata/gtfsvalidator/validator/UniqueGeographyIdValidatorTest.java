package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;

@RunWith(JUnit4.class)
public class UniqueGeographyIdValidatorTest {
  private static <T> T configure(T obj, Consumer<T> configurator) {
    configurator.accept(obj);
    return obj;
  }

  @Test
  public void duplicateGeographyId_yieldsNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsGeoJsonFileDescriptor descriptor = new GtfsGeoJsonFileDescriptor();
    List<GtfsGeoJsonFeature> geoJsonFeatures =
        ImmutableList.of(
            configure(
                new GtfsGeoJsonFeature(),
                f -> {
                  f.setFeatureId("locationId");
                }));
    GtfsGeoJsonFeaturesContainer geoJsonFeaturesContainer =
        descriptor.createContainerForEntities(geoJsonFeatures, noticeContainer);
    List<GtfsStop> stops =
        ImmutableList.of(new GtfsStop.Builder().setCsvRowNumber(1).setStopId("locationId").build());
    GtfsStopTableContainer stopTableContainer =
        GtfsStopTableContainer.forEntities(stops, noticeContainer);
    List<GtfsLocationGroups> locationGroups =
        ImmutableList.of(
            new GtfsLocationGroups.Builder()
                .setCsvRowNumber(1)
                .setLocationGroupId("locationId")
                .build());
    GtfsLocationGroupsTableContainer locationGroupsTableContainer =
        GtfsLocationGroupsTableContainer.forEntities(locationGroups, noticeContainer);
    new UniqueGeographyIdValidator(
            geoJsonFeaturesContainer, stopTableContainer, locationGroupsTableContainer)
        .validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new UniqueGeographyIdValidator.DuplicateGeographyIdNotice("locationId", 1, 1, 0));
  }

  @Test
  public void uniqueGeographyId_yieldsNoNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsGeoJsonFileDescriptor descriptor = new GtfsGeoJsonFileDescriptor();
    List<GtfsGeoJsonFeature> geoJsonFeatures =
        ImmutableList.of(
            configure(
                new GtfsGeoJsonFeature(),
                f -> {
                  f.setFeatureId("locationId1");
                }));
    GtfsGeoJsonFeaturesContainer geoJsonFeaturesContainer =
        descriptor.createContainerForEntities(geoJsonFeatures, noticeContainer);
    List<GtfsStop> stops =
        ImmutableList.of(
            new GtfsStop.Builder().setCsvRowNumber(1).setStopId("locationId2").build());
    GtfsStopTableContainer stopTableContainer =
        GtfsStopTableContainer.forEntities(stops, noticeContainer);
    List<GtfsLocationGroups> locationGroups =
        ImmutableList.of(
            new GtfsLocationGroups.Builder()
                .setCsvRowNumber(1)
                .setLocationGroupId("locationId3")
                .build());
    GtfsLocationGroupsTableContainer locationGroupsTableContainer =
        GtfsLocationGroupsTableContainer.forEntities(locationGroups, noticeContainer);
    new UniqueGeographyIdValidator(
            geoJsonFeaturesContainer, stopTableContainer, locationGroupsTableContainer)
        .validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }
}
