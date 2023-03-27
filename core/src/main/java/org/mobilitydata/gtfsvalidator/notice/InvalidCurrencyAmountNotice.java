package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import java.math.BigDecimal;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRefs;

/**
 * A currency amount field has a value that does not match the format (e.g. expected number of
 * decimal places) of its corresponding currency code field.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 *
 * @see org.mobilitydata.gtfsvalidator.annotation.CurrencyAmount
 */
@GtfsValidationNotice(severity = ERROR, sections = @SectionRefs({"field-types"}))
public class InvalidCurrencyAmountNotice extends ValidationNotice {

  // The name of the faulty file.
  private final String filename;

  // Faulty record's field name.
  private final String fieldName;

  // The row of the faulty record.
  private final int csvRowNumber;

  // Faulty currency amount value.
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
