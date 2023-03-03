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

  private final String filename;

  private final String fieldName;

  private final int csvRowNumber;

  private final String amount;

  public InvalidCurrencyAmountNotice(
      String filename, String fieldName, int csvRowNumber, BigDecimal amount) {
    super(SeverityLevel.ERROR);
    this.filename = filename;
    this.fieldName = fieldName;
    this.csvRowNumber = csvRowNumber;
    this.amount = amount.toPlainString();
  }
}
