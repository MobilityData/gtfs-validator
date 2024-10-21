package org.mobilitydata.gtfsvalidator.util.geojson;

/** Exception thrown when a GeoJSON feature is unparsable. */
public class UnparsableGeoJsonFeatureException extends Exception {
  public UnparsableGeoJsonFeatureException(String message) {
    super(message);
  }

  public UnparsableGeoJsonFeatureException() {}
}
