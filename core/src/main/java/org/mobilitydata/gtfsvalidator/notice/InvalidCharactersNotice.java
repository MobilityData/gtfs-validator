package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

/**
 * Invalid characters.
 *
 * <p>This field contains invalid characters such as the replacement character (U+FFFD). Fields with
 * customer-facing text should not contain invalid characters to ensure good readability and
 * accessibility.
 */
@GtfsValidationNotice(severity = ERROR)
public class InvalidCharactersNotice extends ValidationNotice {
  /** The name of the file containing the invalid characters. */
  private final String filename;

  /** The row number in the CSV file where the invalid characters were found. */
  private final long csvRowNumber;

  /** The name of the field containing the invalid characters. */
  private final String fieldName;

  /** The value of the field containing the invalid characters. */
  private final String fieldValue;

  public InvalidCharactersNotice(
      String filename, long csvRowNumber, String fieldName, String fieldValue) {
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
  }
}
