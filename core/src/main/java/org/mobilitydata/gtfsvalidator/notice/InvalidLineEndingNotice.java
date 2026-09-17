package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRef.FILE_REQUIREMENTS;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRefs;

/** A GTFS text file contains a line ending other than LF or CRLF. */
@GtfsValidationNotice(severity = ERROR, sections = @SectionRefs(FILE_REQUIREMENTS))
public class InvalidLineEndingNotice extends ValidationNotice {

  /** The name of the file containing an invalid line ending. */
  private final String filename;

  public InvalidLineEndingNotice(String filename) {
    this.filename = filename;
  }
}
