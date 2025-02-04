package org.mobilitydata.gtfsvalidator.notice;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

@GtfsValidationNotice(severity = ERROR)
public class GeoJsonDuplicatedElementNotice extends ValidationNotice {
  private final String filename;
  private final String duplicatedElement;

  public GeoJsonDuplicatedElementNotice(String filename, String duplicatedElement) {
    this.filename = filename;
    this.duplicatedElement = duplicatedElement;
  }
}
