package org.mobilitydata.gtfsvalidator.notice;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

@GtfsValidationNotice(severity = ERROR)
public class GeoJsonUnknownElementNotice extends ValidationNotice {
  private final String filename;
  private final String unknownElement;

  public GeoJsonUnknownElementNotice(String filename, String unknownElement) {
    this.filename = filename;
    this.unknownElement = unknownElement;
  }
}
