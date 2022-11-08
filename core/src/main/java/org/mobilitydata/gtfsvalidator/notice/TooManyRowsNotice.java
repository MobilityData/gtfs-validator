package org.mobilitydata.gtfsvalidator.notice;

/**
 * Describes a file with too many rows.
 *
 * <p>Feeds with too large files cannot be processed in a reasonable time by GTFS consumers.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class TooManyRowsNotice extends ValidationNotice {

  private final String filename;
  private final long rowNumber;

  public TooManyRowsNotice(String filename, long rowNumber) {
    super(SeverityLevel.ERROR);
    this.filename = filename;
    this.rowNumber = rowNumber;
  }
}
