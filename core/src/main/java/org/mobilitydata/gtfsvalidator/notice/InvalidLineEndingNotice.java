package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRef.FILE_REQUIREMENTS;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRefs;

/**
 * A GTFS text file contains a line ending other than LF (\n) or CRLF (\r\n).
 *
 * <p>This typically happens when files are exported from spreadsheet software or older
 * applications. Re-save the file with LF or CRLF line endings using a text editor with line ending
 * controls.
 */
@GtfsValidationNotice(severity = ERROR, sections = @SectionRefs(FILE_REQUIREMENTS))
public class InvalidLineEndingNotice extends ValidationNotice {

  /** The name of the file containing an invalid line ending. */
  private final String filename;

  /** The 1-based line number containing the first invalid line ending. */
  private final long rowNumber;

  /** The detected line ending (`CR` or `CRCRLF`). */
  private final String lineEnding;

  public InvalidLineEndingNotice(String filename, long rowNumber, String lineEnding) {
    this.filename = filename;
    this.rowNumber = rowNumber;
    this.lineEnding = lineEnding;
  }
}
