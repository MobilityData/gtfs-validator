package org.mobilitydata.gtfsvalidator.table;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.locationtech.jts.geom.Polygonal;
import org.mobilitydata.gtfsvalidator.util.geojson.GeometryType;

/** This class contains the information from one feature in the GeoJSON file. */
public final class GtfsGeoJSONFeature implements GtfsEntity {
  public static final String FILENAME = "locations.geojson";

  public static final String FEATURE_COLLECTION_FIELD_NAME = "features";

  public static final String FEATURE_ID_FIELD_NAME = "id";

  public static final String FEATURE_PROPERTIES_FIELD_NAME = "properties";

  public static final String GEOMETRY_FIELD_NAME = "geometry";
  public static final String GEOMETRY_TYPE_FIELD_NAME = "type";
  public static final String GEOMETRY_COORDINATES_FIELD_NAME = "coordinates";

  private String featureId; // The id of a feature in the GeoJSON file.
  private GeometryType geometryType; // The type of the geometry.
  private Polygonal geometryDefinition; // The geometry of the feature.

  public GtfsGeoJSONFeature() {}

  // TODO: Change the interface hierarchy so we dont need this. It's not relevant for geojson
  @Override
  public int csvRowNumber() {
    return 0;
  }

  @Nonnull
  public String featureId() {
    return featureId;
  }

  public boolean hasFeatureId() {
    return featureId != null;
  }

  public void setFeatureId(@Nullable String featureId) {
    this.featureId = featureId;
  }

  public Polygonal geometryDefinition() {
    return geometryDefinition;
  }

  public Boolean hasGeometryDefinition() {
    return geometryDefinition != null;
  }

  public void setGeometryDefinition(Polygonal polygon) {
    this.geometryDefinition = polygon;
  }

  public GeometryType geometryType() {
    return geometryType;
  }

  public Boolean hasGeometryType() {
    return geometryType != null;
  }

  public void setGeometryType(GeometryType type) {
    this.geometryType = type;
  }
}
