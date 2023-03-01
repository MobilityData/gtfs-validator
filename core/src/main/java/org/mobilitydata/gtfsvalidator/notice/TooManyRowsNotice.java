package org.mobilitydata.gtfsvalidator.notice;

/**
 * Describes a file with too many rows.
 *
 * <p>Feeds with too large files cannot be processed in a reasonable time by GTFS consumers.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class TooManyRowsNotice extends ValidationNotice {

  // Name of the CSV file that has too many rows.
  private final String filename;

  // Number of the row when reading was stopped.
  private final long rowNumber;

  public TooManyRowsNotice(String filename, long rowNumber) {
    super(SeverityLevel.ERROR);
    this.filename = filename;
    this.rowNumber = rowNumber;
  }
}
