package org.mobilitydata.gtfsvalidator.util.geojson;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.mobilitydata.gtfsvalidator.notice.InvalidGeometryNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsGeoJsonFeature;

@RunWith(JUnit4.class)
public class GeoJsonGeometryValidatorTest {
  private GeoJsonGeometryValidator validator;
  private NoticeContainer noticeContainer;
  private GtfsGeoJsonFeature feature;

  @Before
  public void setUp() {
    noticeContainer = new NoticeContainer();
    validator = new GeoJsonGeometryValidator(noticeContainer);
    feature = new GtfsGeoJsonFeature();
    feature.setFeatureId("test_feature");
  }

  @Test
  public void testValidPolygonShouldReturnPolygon() {
    JsonArray validPolygon = createValidPolygonJsonArray();
    feature.setGeometryType(GeometryType.POLYGON);

    Polygon polygon = validator.createPolygon(validPolygon, feature);

    assertThat(polygon).isNotNull();
    assertThat(polygon.isValid()).isTrue();
    assertThat(noticeContainer.getValidationNotices().size()).isEqualTo(0);
  }

  @Test
  public void testInvalidPolygonShouldReturnNullAndAddNotice() {
    JsonArray invalidPolygon = createInvalidPolygonJsonArray();
    feature.setGeometryType(GeometryType.POLYGON);

    Polygon polygon = validator.createPolygon(invalidPolygon, feature);

    assertThat(polygon).isNull();
    List<InvalidGeometryNotice> notices =
        noticeContainer.getValidationNotices().stream()
            .filter(InvalidGeometryNotice.class::isInstance)
            .map(InvalidGeometryNotice.class::cast)
            .collect(Collectors.toList());
    assertThat(notices.size()).isEqualTo(1);
  }

  @Test
  public void testValidMultiPolygonShouldReturnMultiPolygon() {
    JsonArray validMultiPolygon = createValidMultiPolygonJsonArray();
    feature.setGeometryType(GeometryType.MULTI_POLYGON);

    MultiPolygon multiPolygon = validator.createMultiPolygon(validMultiPolygon, feature);

    assertThat(multiPolygon).isNotNull();
    assertThat(multiPolygon.isValid()).isTrue();
    assertThat(noticeContainer.getValidationNotices().size()).isEqualTo(0);
  }

  @Test
  public void testInvalidMultiPolygonShouldReturnNullAndAddNotice() {
    JsonArray invalidMultiPolygon = createInvalidMultiPolygonJsonArray();
    feature.setGeometryType(GeometryType.MULTI_POLYGON);

    MultiPolygon multiPolygon = validator.createMultiPolygon(invalidMultiPolygon, feature);

    assertThat(multiPolygon).isNull();
    List<InvalidGeometryNotice> notices =
        noticeContainer.getValidationNotices().stream()
            .filter(InvalidGeometryNotice.class::isInstance)
            .map(InvalidGeometryNotice.class::cast)
            .collect(Collectors.toList());
    assertThat(notices.size()).isEqualTo(1);
  }

  // Helper methods to create test data
  private JsonArray createValidPolygonJsonArray() {
    return createValidPolygonJsonArray(Optional.empty());
  }

  private JsonArray createValidPolygonJsonArray(Optional<Integer> deltaValue) {
    JsonArray ring = new JsonArray();
    int delta = deltaValue.orElse(0);
    ring.add(createPointArray(delta, delta));
    ring.add(createPointArray(delta, 1 + delta));
    ring.add(createPointArray(1 + delta, 1 + delta));
    ring.add(createPointArray(1 + delta, delta));
    ring.add(createPointArray(delta, delta));

    JsonArray polygon = new JsonArray();
    polygon.add(ring);
    return polygon;
  }

  private JsonArray createInvalidPolygonJsonArray() {
    JsonArray ring = new JsonArray();
    ring.add(createPointArray(0, 0));
    ring.add(createPointArray(0, 1));
    ring.add(createPointArray(0, 0)); // Invalid polygon (not closed properly)

    JsonArray polygon = new JsonArray();
    polygon.add(ring);
    return polygon;
  }

  private JsonArray createValidMultiPolygonJsonArray() {
    JsonArray multiPolygon = new JsonArray();
    multiPolygon.add(createValidPolygonJsonArray());
    multiPolygon.add(createValidPolygonJsonArray(Optional.of(5)));
    return multiPolygon;
  }

  private JsonArray createInvalidMultiPolygonJsonArray() {
    JsonArray multiPolygon = new JsonArray();
    // self-intersecting polygon
    multiPolygon.add(createValidPolygonJsonArray());
    multiPolygon.add(createValidPolygonJsonArray());
    return multiPolygon;
  }

  private JsonArray createPointArray(double x, double y) {
    JsonArray point = new JsonArray();
    point.add(new JsonPrimitive(x));
    point.add(new JsonPrimitive(y));
    return point;
  }
}
