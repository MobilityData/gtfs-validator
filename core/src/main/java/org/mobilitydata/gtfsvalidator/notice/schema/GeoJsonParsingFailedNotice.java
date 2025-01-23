package org.mobilitydata.gtfsvalidator.notice.schema;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;

/**
 * The parsing of a GeoJSON file failed.
 *
 * <p>One common case of the problem is when the file is not a valid GeoJSON file.
 */
@GtfsValidationNotice(severity = ERROR)
public class GeoJsonParsingFailedNotice extends ValidationNotice {
  /** The name of the faulty file. */
  private final String filename;

  /** The detailed message describing the error, and the internal state of the parser/writer. */
  private final String message;

  /**
   * Constructor used while extracting notice information.
   *
   * @param filename the name of the file
   * @param message the message describing the error
   */
  public GeoJsonParsingFailedNotice(String filename, String message) {
    this.filename = filename;
    this.message = message;
  }
}
