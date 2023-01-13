package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFarePaymentOption;
import org.mobilitydata.gtfsvalidator.table.GtfsFarePaymentOptionType;

@RunWith(JUnit4.class)
public class FarePaymentOptionNameValidatorTest {

  @Test
  public void testTransitCard() {
    assertThat(
            validationNoticesFor(
                new GtfsFarePaymentOption.Builder()
                    .setCsvRowNumber(2)
                    .setFarePaymentOptionName("Go! Pass")
                    .setFarePaymentOptionType(GtfsFarePaymentOptionType.TRANSIT_CARD)
                    .build()))
        .isEmpty();
    assertThat(
            validationNoticesFor(
                new GtfsFarePaymentOption.Builder()
                    .setCsvRowNumber(2)
                    .setFarePaymentOptionType(GtfsFarePaymentOptionType.TRANSIT_CARD)
                    .build()))
        .containsExactly(
            new MissingRecommendedFieldNotice(
                "fare_payment_options.txt", 2, "fare_payment_option_name"));
  }

  @Test
  public void testCash() {
    assertThat(
            validationNoticesFor(
                new GtfsFarePaymentOption.Builder()
                    .setCsvRowNumber(2)
                    .setFarePaymentOptionType(GtfsFarePaymentOptionType.CASH)
                    .build()))
        .isEmpty();
    assertThat(
            validationNoticesFor(
                new GtfsFarePaymentOption.Builder()
                    .setCsvRowNumber(2)
                    .setFarePaymentOptionName("Cash")
                    .setFarePaymentOptionType(GtfsFarePaymentOptionType.CASH)
                    .build()))
        .isEmpty();
  }

  private List<ValidationNotice> validationNoticesFor(GtfsFarePaymentOption entity) {
    FarePaymentOptionNameValidator validator = new FarePaymentOptionNameValidator();
    NoticeContainer noticeContainer = new NoticeContainer();
    validator.validate(entity, noticeContainer);
    return noticeContainer.getValidationNotices();
  }
}
