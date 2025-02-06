package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.INFO;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

/** Unknown elements in locations.geojson file. */
@GtfsValidationNotice(severity = INFO)
public class GeoJsonUnknownElementNotice extends ValidationNotice {
  /** The name of the file where the unknown element was found. */
  private final String filename;

  /** The unknown element in the GeoJSON file. */
  private final String unknownElement;

  /** The unknown element exception message. */
  private final String message;

  public GeoJsonUnknownElementNotice(String filename, String unknownElement, String message) {
    this.filename = filename;
    this.unknownElement = unknownElement;
    this.message = message;
  }
}
