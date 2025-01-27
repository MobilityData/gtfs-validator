package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Consumer; // <-- Added for the Consumer
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.ForeignKeyViolationNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;

@RunWith(JUnit4.class)
public class LocationIdForeignKeyValidatorTest {
  private static <T> T configure(T obj, Consumer<T> configurator) {
    configurator.accept(obj);
    return obj;
  }

  @Test
  public void missingGeoJSONId_yieldsNotice() {
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
    List<GtfsStopTime> stopTimes =
        ImmutableList.of(
            new GtfsStopTime.Builder().setCsvRowNumber(1).setLocationId("locationId2").build());
    GtfsStopTimeTableContainer stopTimeTableContainer =
        GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);
    new LocationIdForeignKeyValidator(geoJsonFeaturesContainer, stopTimeTableContainer)
        .validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new ForeignKeyViolationNotice(
                "stop_times.txt", "location_id", "locations.geojson", "id", "locationId2", 1));
  }

  @Test
  public void existingGeoJSONId_yieldsNoNotice() {
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
    List<GtfsStopTime> stopTimes =
        ImmutableList.of(
            new GtfsStopTime.Builder().setCsvRowNumber(1).setLocationId("locationId").build());
    GtfsStopTimeTableContainer stopTimeTableContainer =
        GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);
    new LocationIdForeignKeyValidator(geoJsonFeaturesContainer, stopTimeTableContainer)
        .validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }
}
