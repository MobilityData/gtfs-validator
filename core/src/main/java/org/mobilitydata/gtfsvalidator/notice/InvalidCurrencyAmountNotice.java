package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRef.FILED_TYPES;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import java.math.BigDecimal;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRefs;

/**
 * A currency amount field has a value that does not match the format of its corresponding currency
 * code field.
 *
 * <p>Typically, this means the amount did not have the expected number of decimal places. Check the
 * formatting of your amount field so it matches the number of decimal places specified by the <a
 * href="https://en.wikipedia.org/wiki/ISO_4217#Active_codes">currency_code value</a>.
 *
 * @see org.mobilitydata.gtfsvalidator.annotation.CurrencyAmount
 */
@GtfsValidationNotice(severity = ERROR, sections = @SectionRefs(FILED_TYPES))
public class InvalidCurrencyAmountNotice extends ValidationNotice {

  /** The name of the faulty file. */
  private final String filename;

  /** Faulty record's field name. */
  private final String currencyCode;

  /** The row of the faulty record. */
  private final int csvRowNumber;

  /** Faulty currency amount value. */
  private final String amount;

  public InvalidCurrencyAmountNotice(
      String filename, String currencyCode, int csvRowNumber, BigDecimal amount) {
    this.filename = filename;
    this.currencyCode = currencyCode;
    this.csvRowNumber = csvRowNumber;
    this.amount = amount.toPlainString();
  }
}
