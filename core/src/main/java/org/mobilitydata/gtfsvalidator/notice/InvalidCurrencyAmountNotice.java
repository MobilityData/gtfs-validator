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
 * <p>Typically, this means the amount did not have the expected number of decimal places. The
 * number of decimal places is specified by <a
 * href="https://en.wikipedia.org/wiki/ISO_4217#Active_codes">ISO 4217</a>.
 *
 * @see org.mobilitydata.gtfsvalidator.annotation.CurrencyAmount
 */
@GtfsValidationNotice(severity = ERROR, sections = @SectionRefs(FILED_TYPES))
public class InvalidCurrencyAmountNotice extends ValidationNotice {

  /** The name of the faulty file. */
  private final String filename;

  /** Faulty record's field name. */
  private final String fieldName;

  /** The row of the faulty record. */
  private final int csvRowNumber;

  /** Faulty currency amount value. */
  private final String amount;

  public InvalidCurrencyAmountNotice(
      String filename, String fieldName, int csvRowNumber, BigDecimal amount) {
    super();
    this.filename = filename;
    this.fieldName = fieldName;
    this.csvRowNumber = csvRowNumber;
    this.amount = amount.toPlainString();
  }
}
