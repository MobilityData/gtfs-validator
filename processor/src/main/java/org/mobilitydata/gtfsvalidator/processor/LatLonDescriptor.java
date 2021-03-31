package org.mobilitydata.gtfsvalidator.processor;

import com.google.auto.value.AutoValue;

/** Describes a pair of latitude and longitude fields. */
@AutoValue
public abstract class LatLonDescriptor {

  public static LatLonDescriptor create(String latField, String lonField, String latLonField) {
    return new AutoValue_LatLonDescriptor(latField, lonField, latLonField);
  }

  /** Latitude field name in lowerCamelCase, e.g. {@code "stopLat"}. */
  public abstract String latField();

  /** Longitude field name in lowerCamelCase, e.g. {@code "stopLon"}. */
  public abstract String lonField();

  /** Combined lat-lon getter name in lowerCamelCase, e.g. {@code "stopLatLon"}. */
  public abstract String latLonField();
}
