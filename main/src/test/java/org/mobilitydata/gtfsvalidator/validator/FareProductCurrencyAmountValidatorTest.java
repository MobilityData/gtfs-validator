package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFareProduct;
import org.mobilitydata.gtfsvalidator.validator.FareProductCurrencyAmountValidator.InvalidCurrencyAmountNotice;

@RunWith(JUnit4.class)
public class FareProductCurrencyAmountValidatorTest {

  private static List<ValidationNotice> generateNotices(GtfsFareProduct product) {
    NoticeContainer noticeContainer = new NoticeContainer();
    FareProductCurrencyAmountValidator validator = new FareProductCurrencyAmountValidator();
    validator.validate(product, noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void testValidCurrencyUSD() {
    assertThat(
            generateNotices(
                new GtfsFareProduct.Builder()
                    .setCsvRowNumber(2)
                    .setCurrency(Currency.getInstance("USD"))
                    .setAmount(new BigDecimal("5.00"))
                    .build()))
        .isEmpty();
  }

  @Test
  public void testInvalidCurrencyUSD() {
    assertThat(
            generateNotices(
                new GtfsFareProduct.Builder()
                    .setCsvRowNumber(2)
                    .setCurrency(Currency.getInstance("USD"))
                    .setAmount(new BigDecimal("5"))
                    .build()))
        .containsExactly(new InvalidCurrencyAmountNotice(2, new BigDecimal("5")));
  }

  @Test
  public void testValidCurrencyISK() {
    // Icelandic króna expects no digits after decimal separator.
    assertThat(
            generateNotices(
                new GtfsFareProduct.Builder()
                    .setCsvRowNumber(2)
                    .setCurrency(Currency.getInstance("ISK"))
                    .setAmount(new BigDecimal("5"))
                    .build()))
        .isEmpty();
  }

  @Test
  public void testInvalidCurrencyISK() {
    // Icelandic króna expects no digits after decimal separator.
    assertThat(
            generateNotices(
                new GtfsFareProduct.Builder()
                    .setCsvRowNumber(2)
                    .setCurrency(Currency.getInstance("ISK"))
                    .setAmount(new BigDecimal("5.00"))
                    .build()))
        .containsExactly(new InvalidCurrencyAmountNotice(2, new BigDecimal("5.00")));
  }
}
