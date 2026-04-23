package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

/** Duplicated elements in locations.geojson file. */
@GtfsValidationNotice(severity = ERROR)
public class GeoJsonDuplicatedElementNotice extends ValidationNotice {
  /** The name of the file where the duplicated element was found. */
  private final String filename;

  /** The duplicated element in the GeoJSON file. */
  private final String duplicatedElement;

  public GeoJsonDuplicatedElementNotice(String filename, String duplicatedElement) {
    this.filename = filename;
    this.duplicatedElement = duplicatedElement;
  }
}
