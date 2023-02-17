package org.mobilitydata.gtfsvalidator.notice;

/**
 * A string field has a value that does not contain mixed case.
 *
 * <p>Severity: {@code SeverityLevel.WARNING}
 *
 * @see org.mobilitydata.gtfsvalidator.annotation.MixedCase
 */
public class MixedCaseNotice extends ValidationNotice {
  private final String fileName;

  private final String fieldName;

  private final int csvRowNumber;

  public MixedCaseNotice(String fileName, String fieldName, int csvRowNumber) {
    super(SeverityLevel.ERROR);
    this.fileName = fileName;
    this.fieldName = fieldName;
    this.csvRowNumber = csvRowNumber;
  }
}
