package org.mobilitydata.gtfsvalidator.notice;

import java.math.BigDecimal;

/**
 * A currency amount field has a value that does not match the format (e.g. expected number of
 * decimal places) of its corresponding currency code field.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 *
 * @see org.mobilitydata.gtfsvalidator.annotation.CurrencyAmount
 */
public class InvalidCurrencyAmountNotice extends ValidationNotice {

  private final String fileName;

  private final String fieldName;

  private final int csvRowNumber;

  private final String amount;

  public InvalidCurrencyAmountNotice(
      String fileName, String fieldName, int csvRowNumber, BigDecimal amount) {
    super(SeverityLevel.ERROR);
    this.fileName = fileName;
    this.fieldName = fieldName;
    this.csvRowNumber = csvRowNumber;
    this.amount = amount.toPlainString();
  }
}
