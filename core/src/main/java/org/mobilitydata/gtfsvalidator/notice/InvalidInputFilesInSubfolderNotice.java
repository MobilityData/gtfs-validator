package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

/**
 * At least 1 GTFS file is in a subfolder.
 *
 * <p>All GTFS files must reside at the root level directly. There are two common cases that trigger
 * this error:
 *
 * <pre>
 * 1. The root folder (e.g., a folder called "GTFS 2020") was zipped instead of the individual
 *    files within the folder (e.g., `calendar.txt`, `agency.txt`, etc.). This can be fixed by
 *    zipping the files directly instead.
 * 2. A file <a href="https://gtfs.org/documentation/schedule/reference/#dataset-files">associated
 *    with GTFS</a> is in a subfolder rather than the root folder of the dataset, and needs to be
 *    removed if not needed or moved to the root level.
 * </pre>
 */
@GtfsValidationNotice(severity = ERROR)
public class InvalidInputFilesInSubfolderNotice extends ValidationNotice {
  /** The error message that explains the reason for the exception. */
  private final String message;

  public InvalidInputFilesInSubfolderNotice(String message) {
    this.message = message;
  }
}
