package org.mobilitydata.gtfsvalidator.table;

import java.util.List;
import org.mobilitydata.gtfsvalidator.annotation.GtfsJson;
import org.mobilitydata.gtfsvalidator.annotation.GtfsJsonEntity;

enum GeometryType {
  POLYGON,
  MULTIPOLYGON
}

abstract class BaseGeometry {
  protected GeometryType type;

  public BaseGeometry(GeometryType type) {
    this.type = type;
  }

  public GeometryType getType() {
    return type;
  }

  public void setType(GeometryType type) {
    this.type = type;
  }
}

class Polygon extends BaseGeometry {
  private List<List<List<Double>>> coordinates; // A list of rings, each a list of coordinate pairs

  public Polygon(List<List<List<Double>>> coordinates) {
    super(GeometryType.POLYGON);
    this.coordinates = coordinates;
  }

  public List<List<List<Double>>> getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(List<List<List<Double>>> coordinates) {
    this.coordinates = coordinates;
  }
}

class MultiPolygon extends BaseGeometry {
  private List<List<List<List<Double>>>> coordinates; // A list of polygons, each a list of rings

  public MultiPolygon(List<List<List<List<Double>>>> coordinates) {
    super(GeometryType.MULTIPOLYGON);
    this.coordinates = coordinates;
  }

  public List<List<List<List<Double>>>> getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(List<List<List<List<Double>>>> coordinates) {
    this.coordinates = coordinates;
  }
}

class Feature {
  private String id;
  private BaseGeometry geometry;
  private Properties properties;

  public Feature(String id, BaseGeometry geometry, Properties properties) {
    this.id = id;
    this.geometry = geometry;
    this.properties = properties;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public BaseGeometry getGeometry() {
    return geometry;
  }

  public void setGeometry(BaseGeometry geometry) {
    this.geometry = geometry;
  }

  public Properties getProperties() {
    return properties;
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }
}

class Properties {}

@GtfsJson("locations.geojson")
public interface GtfsLocationsSchema extends GtfsEntity {
  //   @FieldType(GtfsLocationsGeoJsonTypeEnum.ID)
  String type();

  @GtfsJsonEntity(value = "features", clazz = Feature.class)
  List<Feature> features();
}
