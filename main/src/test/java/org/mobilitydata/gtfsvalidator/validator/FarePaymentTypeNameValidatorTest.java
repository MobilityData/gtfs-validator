package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFarePaymentMethod;
import org.mobilitydata.gtfsvalidator.table.GtfsFarePaymentType;

@RunWith(JUnit4.class)
public class FarePaymentTypeNameValidatorTest {

  @Test
  public void testTransitCard() {
    assertThat(
            validationNoticesFor(
                new GtfsFarePaymentType.Builder()
                    .setCsvRowNumber(2)
                    .setFarePaymentTypeName("Go! Pass")
                    .setFarePaymentType(GtfsFarePaymentMethod.TRANSIT_CARD)
                    .build()))
        .isEmpty();
    assertThat(
            validationNoticesFor(
                new GtfsFarePaymentType.Builder()
                    .setCsvRowNumber(2)
                    .setFarePaymentType(GtfsFarePaymentMethod.TRANSIT_CARD)
                    .build()))
        .containsExactly(
            new MissingRecommendedFieldNotice(
                "fare_payment_types.txt", 2, "fare_payment_type_name"));
  }

  @Test
  public void testCash() {
    assertThat(
            validationNoticesFor(
                new GtfsFarePaymentType.Builder()
                    .setCsvRowNumber(2)
                    .setFarePaymentType(GtfsFarePaymentMethod.CASH)
                    .build()))
        .isEmpty();
    assertThat(
            validationNoticesFor(
                new GtfsFarePaymentType.Builder()
                    .setCsvRowNumber(2)
                    .setFarePaymentTypeName("Cash")
                    .setFarePaymentType(GtfsFarePaymentMethod.CASH)
                    .build()))
        .isEmpty();
  }

  private List<ValidationNotice> validationNoticesFor(GtfsFarePaymentType entity) {
    FarePaymentTypeNameValidator validator = new FarePaymentTypeNameValidator();
    NoticeContainer noticeContainer = new NoticeContainer();
    validator.validate(entity, noticeContainer);
    return noticeContainer.getValidationNotices();
  }
}
