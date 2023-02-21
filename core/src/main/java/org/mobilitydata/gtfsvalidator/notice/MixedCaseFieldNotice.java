package org.mobilitydata.gtfsvalidator.notice;

/**
 * A string field has a value that does not contain mixed case.
 *
 * <p>Severity: {@code SeverityLevel.WARNING}
 *
 * @see org.mobilitydata.gtfsvalidator.annotation.MixedCase
 */
public class MixedCaseFieldNotice extends ValidationNotice {
  private final String filename;

  private final String fieldName;

  private final int csvRowNumber;

  public MixedCaseFieldNotice(String filename, String fieldName, int csvRowNumber) {
    super(SeverityLevel.WARNING);
    this.filename = filename;
    this.fieldName = fieldName;
    this.csvRowNumber = csvRowNumber;
  }
}
