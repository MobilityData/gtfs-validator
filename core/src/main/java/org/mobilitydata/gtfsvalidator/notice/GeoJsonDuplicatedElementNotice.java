package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

/**
 * Duplicated elements in locations.geojson file.
 */
@GtfsValidationNotice(severity = ERROR)
public class GeoJsonDuplicatedElementNotice extends ValidationNotice {
  /**
   * The name of the file where the duplicated element was found.
   */
  private final String filename;

  /**
   * The duplicated element in the GeoJSON file.
   */
  private final String duplicatedElement;
  /**
   * The duplicated exception message.
   */
  private final String message;

  public GeoJsonDuplicatedElementNotice(String filename, String duplicatedElement, String message) {
    this.filename = filename;
    this.duplicatedElement = duplicatedElement;
    this.message = message;
  }
}
