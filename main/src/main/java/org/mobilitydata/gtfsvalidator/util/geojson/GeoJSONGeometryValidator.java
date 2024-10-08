package org.mobilitydata.gtfsvalidator.util.geojson;

import com.google.gson.JsonArray;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.operation.valid.IsValidOp;
import org.mobilitydata.gtfsvalidator.notice.InvalidGeometryNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsGeoJSONFeature;

/**
 * Utility class responsible for handling GeoJSON geometry validation and creation. This class
 * provides methods to create and validate Polygon and MultiPolygon geometries from GeoJSON data
 * structures.
 */
public class GeoJSONGeometryValidator {

  // GeometryFactory instance used to create geometric shapes.
  private final GeometryFactory geometryFactory = new GeometryFactory();
  private final NoticeContainer noticeContainer;

  public GeoJSONGeometryValidator(NoticeContainer noticeContainer) {
    this.noticeContainer = noticeContainer;
  }

  /**
   * Creates a Polygon from a JsonArray representing the rings of the polygon. Validates the polygon
   * and adds a validation notice to the NoticeContainer if invalid.
   *
   * @return A valid Polygon object or null if the geometry is invalid.
   */
  public Polygon createPolygon(JsonArray rings, GtfsGeoJSONFeature feature) {
    Coordinate[][] polygonRings = parseCoordinates(rings);
    try {
      Polygon polygon =
          geometryFactory.createPolygon(
              geometryFactory.createLinearRing(polygonRings[0]), createInteriorRings(polygonRings));
      if (!IsValidOp.isValid(polygon)) {
        addInvalidGeometryNotice(polygon, feature);
        return null;
      }
      return polygon;
    } catch (IllegalArgumentException e) {
      addInvalidGeometryNotice(e, feature);
      return null;
    }
  }

  /**
   * Creates a MultiPolygon from a JsonArray representing an array of polygons. Validates each
   * polygon and adds a validation notice to the NoticeContainer if invalid.
   *
   * @return A valid MultiPolygon object or null if any of the geometries are invalid.
   */
  public MultiPolygon createMultiPolygon(JsonArray polygons, GtfsGeoJSONFeature feature) {
    Polygon[] multiPolygonArray = new Polygon[polygons.size()];
    for (int p = 0; p < polygons.size(); p++) {
      Polygon polygon = createPolygon(polygons.get(p).getAsJsonArray(), feature);
      if (polygon == null) {
        return null;
      }
      multiPolygonArray[p] = polygon;
    }

    try {
      MultiPolygon multiPolygon = geometryFactory.createMultiPolygon(multiPolygonArray);
      if (!IsValidOp.isValid(multiPolygon)) {
        addInvalidGeometryNotice(multiPolygon, feature);
        return null;
      }
      return multiPolygon;
    } catch (IllegalArgumentException e) {
      addInvalidGeometryNotice(e, feature);
      return null;
    }
  }

  /** Parses a JsonArray to extract coordinates for creating polygon rings. */
  private Coordinate[][] parseCoordinates(JsonArray rings) {
    Coordinate[][] polygonRings = new Coordinate[rings.size()][];
    for (int r = 0; r < rings.size(); r++) {
      JsonArray ringCoordinates = rings.get(r).getAsJsonArray();
      Coordinate[] coordinates = new Coordinate[ringCoordinates.size()];
      for (int i = 0; i < ringCoordinates.size(); i++) {
        JsonArray points = ringCoordinates.get(i).getAsJsonArray();
        coordinates[i] = new Coordinate(points.get(0).getAsDouble(), points.get(1).getAsDouble());
      }
      polygonRings[r] = coordinates;
    }
    return polygonRings;
  }

  /** Creates the interior rings of a polygon from the parsed coordinates. */
  private LinearRing[] createInteriorRings(Coordinate[][] polygonRings) {
    if (polygonRings.length > 1) {
      LinearRing[] interiorRings = new LinearRing[polygonRings.length - 1];
      for (int i = 1; i < polygonRings.length; i++) {
        interiorRings[i - 1] = geometryFactory.createLinearRing(polygonRings[i]);
      }
      return interiorRings;
    }
    return null;
  }

  /** Adds a validation notice to the NoticeContainer if the geometry is invalid. */
  private void addInvalidGeometryNotice(Geometry geometry, GtfsGeoJSONFeature feature) {
    noticeContainer.addValidationNotice(
        new InvalidGeometryNotice(
            feature.featureId(),
            feature.geometryType().getType(),
            new IsValidOp(geometry).getValidationError().getMessage()));
  }

  /**
   * Adds a validation notice to the NoticeContainer if an exception occurs during geometry
   * creation.
   */
  private void addInvalidGeometryNotice(IllegalArgumentException e, GtfsGeoJSONFeature feature) {
    noticeContainer.addValidationNotice(
        new InvalidGeometryNotice(
            feature.featureId(), feature.geometryType().getType(), e.getLocalizedMessage()));
  }
}
