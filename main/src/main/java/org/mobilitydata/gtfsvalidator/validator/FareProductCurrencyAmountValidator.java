package org.mobilitydata.gtfsvalidator.validator;

import java.math.BigDecimal;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFareProduct;

@GtfsValidator
public class FareProductCurrencyAmountValidator extends SingleEntityValidator<GtfsFareProduct> {

  @Override
  public void validate(GtfsFareProduct product, NoticeContainer noticeContainer) {
    if (product.hasCurrency() && product.hasAmount()) {
      BigDecimal amount = product.amount();
      if (amount.scale() != product.currency().getDefaultFractionDigits()) {
        noticeContainer.addValidationNotice(
            new InvalidCurrencyAmountNotice(product.csvRowNumber(), amount));
      }
    }
  }

  /**
   * A row from GTFS file `fare_transfer_rules.txt` has a defined `duration_limit` field but no
   * `duration_limit_type` specified.
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  static class InvalidCurrencyAmountNotice extends ValidationNotice {
    private final long csvRowNumber;
    private final BigDecimal amount;

    InvalidCurrencyAmountNotice(long csvRowNumber, BigDecimal amount) {
      super(SeverityLevel.ERROR);
      this.csvRowNumber = csvRowNumber;
      this.amount = amount;
    }
  }
}
