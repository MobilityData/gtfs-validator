package org.mobilitydata.gtfsvalidator.table;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.locationtech.jts.geom.Geometry;
import org.mobilitydata.gtfsvalidator.util.geojson.GeometryType;

/** This class contains the information from one feature in the GeoJSON file. */
public final class GtfsGeoJsonFeature implements GtfsEntity {
  public static final String FILENAME = "locations.geojson";

  public static final String FEATURE_COLLECTION_FIELD_NAME = "features";

  public static final String FEATURE_ID_FIELD_NAME = "id";

  public static final String FEATURE_TYPE_FIELD_NAME = "type";

  public static final String FEATURE_PROPERTIES_FIELD_NAME = "properties";
  public static final String FEATURE_PROPERTIES_STOP_NAME_FIELD_NAME = "stop_name";
  public static final String FEATURE_PROPERTIES_STOP_DESC_FIELD_NAME = "stop_desc";

  public static final String GEOMETRY_FIELD_NAME = "geometry";
  public static final String GEOMETRY_TYPE_FIELD_NAME = "type";
  public static final String GEOMETRY_COORDINATES_FIELD_NAME = "coordinates";

  private String featureId; // The id of a feature in the GeoJSON file.
  private GeometryType geometryType; // The type of the geometry.
  private Geometry geometryDefinition; // The geometry of the feature.
  private String stopName; // The name of the location as displayed to the riders.
  private String stopDesc; // A description of the location.
  private int featureIndex;

  public GtfsGeoJsonFeature() {}

  private GtfsGeoJsonFeature(Builder builder) {
    this.featureId = builder.featureId;
    this.geometryType = builder.geometryType;
    this.geometryDefinition = builder.geometryDefinition;
    this.stopName = builder.stopName;
    this.stopDesc = builder.stopDesc;
  }

  // TODO: Change the interface hierarchy so we dont need this. It's not relevant for geojson
  @Override
  public int csvRowNumber() {
    return 0;
  }

  public int featureIndex() {
    return featureIndex;
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

  public Geometry geometryDefinition() {
    return geometryDefinition;
  }

  public Boolean geometryOverlaps(GtfsGeoJsonFeature other) {
    if (geometryDefinition == null || other.geometryDefinition == null) {
      return false;
    }
    return geometryDefinition.overlaps(other.geometryDefinition);
  }

  public Boolean hasGeometryDefinition() {
    return geometryDefinition != null;
  }

  public void setGeometryDefinition(Geometry polygon) {
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

  public String stopName() {
    return stopName;
  }

  public Boolean hasStopName() {
    return stopName != null;
  }

  public void setStopName(@Nullable String stopName) {
    this.stopName = stopName;
  }

  public String stopDesc() {
    return stopDesc;
  }

  public Boolean hasStopDesc() {
    return stopDesc != null;
  }

  public void setStopDesc(@Nullable String stopDesc) {
    this.stopDesc = stopDesc;
  }

  public void setFeatureIndex(int featureIndex) {
    this.featureIndex = featureIndex;
  }

  /** Builder class for GtfsGeoJsonFeature. */
  public static class Builder {
    private String featureId;
    private GeometryType geometryType;
    private Geometry geometryDefinition;
    private String stopName;
    private String stopDesc;

    public Builder featureId(String featureId) {
      this.featureId = featureId;
      return this;
    }

    public Builder geometryType(GeometryType geometryType) {
      this.geometryType = geometryType;
      return this;
    }

    public Builder geometryDefinition(Geometry geometryDefinition) {
      this.geometryDefinition = geometryDefinition;
      return this;
    }

    public Builder stopName(String stopName) {
      this.stopName = stopName;
      return this;
    }

    public Builder stopDesc(String stopDesc) {
      this.stopDesc = stopDesc;
      return this;
    }

    public GtfsGeoJsonFeature build() {
      return new GtfsGeoJsonFeature(this);
    }
  }
}
