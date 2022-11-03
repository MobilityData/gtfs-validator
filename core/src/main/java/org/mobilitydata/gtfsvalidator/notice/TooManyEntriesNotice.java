package org.mobilitydata.gtfsvalidator.notice;

/**
 * Describes a file with too many entries.
 *
 * <p>Feeds with too large files cannot be processed in a reasonable time by GTFS consumers.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class TooManyEntriesNotice extends ValidationNotice {

  private final String filename;
  private final long entryCount;

  public TooManyEntriesNotice(String filename, long entryCount) {
    super(SeverityLevel.ERROR);
    this.filename = filename;
    this.entryCount = entryCount;
  }
}
