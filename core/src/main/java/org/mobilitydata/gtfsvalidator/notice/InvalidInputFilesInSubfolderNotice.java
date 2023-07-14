package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

/** Zip files containing Gtfs files in a subfolder are invalid. */
@GtfsValidationNotice(severity = ERROR)
public class InvalidInputFilesInSubfolderNotice extends ValidationNotice {
  /** The error message that explains the reason for the exception. */
  private final String message;

  public InvalidInputFilesInSubfolderNotice(String message) {
    this.message = message;
  }
}
