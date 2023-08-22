package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

/**
 * At least 1 GTFS file is in a subfolder.
 *
 * <p>All GTFS files must reside at the root level directly.
 */
@GtfsValidationNotice(severity = ERROR)
public class InvalidInputFilesInSubfolderNotice extends ValidationNotice {
  /** The error message that explains the reason for the exception. */
  private final String message;

  public InvalidInputFilesInSubfolderNotice(String message) {
    this.message = message;
  }
}
