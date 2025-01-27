package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

/** A GeoJSON feature of locations.geojson could not be parsed. */
@GtfsValidationNotice(severity = ERROR)
public class UnparsableGeoJsonFeatureNotice extends ValidationNotice {
  /** The name of the faulty file. */
  private final String filename;

  /** The detailed message describing the error. */
  private final String message;

  public UnparsableGeoJsonFeatureNotice(String filename, String message) {
    this.filename = filename;
    this.message = message;
  }
}
