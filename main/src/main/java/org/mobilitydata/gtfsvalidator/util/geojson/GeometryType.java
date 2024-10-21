package org.mobilitydata.gtfsvalidator.util.geojson;

public enum GeometryType {
  POLYGON("Polygon"),
  MULTI_POLYGON("MultiPolygon");
  private final String type;

  GeometryType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
