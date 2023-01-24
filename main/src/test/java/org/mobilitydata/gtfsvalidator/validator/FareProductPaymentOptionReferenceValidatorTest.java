package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFareProduct;
import org.mobilitydata.gtfsvalidator.table.GtfsFareProduct.Builder;
import org.mobilitydata.gtfsvalidator.validator.FareProductPaymentOptionReferenceValidator.FareProductPaymentOptionReferenceNotice;

@RunWith(JUnit4.class)
public class FareProductPaymentOptionReferenceValidatorTest {
  private static List<ValidationNotice> generateNotices(GtfsFareProduct entity) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new FareProductPaymentOptionReferenceValidator().validate(entity, noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void testBothOptionAndGroupSpecified() {
    GtfsFareProduct fareProduct =
        new Builder()
            .setCsvRowNumber(2)
            .setFarePaymentOptionId("cash")
            .setFarePaymentOptionGroupId("cash_and_card")
            .build();

    assertThat(generateNotices(fareProduct))
        .containsExactly(new FareProductPaymentOptionReferenceNotice(fareProduct));
  }

  @Test
  public void testValidFarePaymentOptionCombinations() {
    // No payment option specified.
    assertThat(generateNotices(new Builder().setCsvRowNumber(2).build())).isEmpty();
    // Fare payment option specified.
    assertThat(
            generateNotices(
                new Builder().setCsvRowNumber(2).setFarePaymentOptionId("cash").build()))
        .isEmpty();
    // Fare payment option group specified.
    assertThat(
            generateNotices(
                new Builder()
                    .setCsvRowNumber(2)
                    .setFarePaymentOptionGroupId("cash_and_card")
                    .build()))
        .isEmpty();
  }
}
