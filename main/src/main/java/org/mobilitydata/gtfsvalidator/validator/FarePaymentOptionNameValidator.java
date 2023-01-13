package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFarePaymentOption;
import org.mobilitydata.gtfsvalidator.table.GtfsFarePaymentOptionType;

@GtfsValidator
public class FarePaymentOptionNameValidator extends SingleEntityValidator<GtfsFarePaymentOption> {

  @Override
  public void validate(GtfsFarePaymentOption entity, NoticeContainer noticeContainer) {
    if (shouldPaymentMethodHaveName(entity.farePaymentOptionType())
        && !entity.hasFarePaymentOptionName()) {
      noticeContainer.addValidationNotice(
          new MissingRecommendedFieldNotice(
              GtfsFarePaymentOption.FILENAME,
              entity.csvRowNumber(),
              GtfsFarePaymentOption.FARE_PAYMENT_OPTION_NAME_FIELD_NAME));
    }
  }

  private static boolean shouldPaymentMethodHaveName(GtfsFarePaymentOptionType method) {
    switch (method) {
      case TRANSIT_CARD:
      case MOBILE_APP:
        return true;
      case CASH:
      case CONTACTLESS_PAYMENT:
      default:
        return false;
    }
  }
}
