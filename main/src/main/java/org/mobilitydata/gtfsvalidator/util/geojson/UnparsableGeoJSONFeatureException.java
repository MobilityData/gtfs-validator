package org.mobilitydata.gtfsvalidator.util.geojson;

/** Exception thrown when a GeoJSON feature is unparsable. */
public class UnparsableGeoJSONFeatureException extends Exception {
  public UnparsableGeoJSONFeatureException(String message) {
    super(message);
  }

  public UnparsableGeoJSONFeatureException() {}
}
