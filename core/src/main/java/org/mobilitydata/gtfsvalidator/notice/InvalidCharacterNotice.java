package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

/**
 * This field contains invalid characters, such as the replacement character ("\uFFFD").
 *
 * <p>Check that text was properly encoded in UTF-8 as required by GTFS.
 */
@GtfsValidationNotice(severity = ERROR)
public class InvalidCharacterNotice extends ValidationNotice {
  /** The name of the file containing the invalid characters. */
  private final String filename;

  /** The row number in the CSV file where the invalid characters were found. */
  private final long csvRowNumber;

  /** The name of the field containing the invalid characters. */
  private final String fieldName;

  /** The value of the field containing the invalid characters. */
  private final String fieldValue;

  public InvalidCharacterNotice(
      String filename, long csvRowNumber, String fieldName, String fieldValue) {
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
  }
}
