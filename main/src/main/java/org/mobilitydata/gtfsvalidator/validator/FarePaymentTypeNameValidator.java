package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFarePaymentMethod;
import org.mobilitydata.gtfsvalidator.table.GtfsFarePaymentType;

@GtfsValidator
public class FarePaymentTypeNameValidator extends SingleEntityValidator<GtfsFarePaymentType> {

  @Override
  public void validate(GtfsFarePaymentType entity, NoticeContainer noticeContainer) {
    if (shouldPaymentMethodHaveName(entity.farePaymentType()) && !entity.hasFarePaymentTypeName()) {
      noticeContainer.addValidationNotice(
          new MissingRecommendedFieldNotice(
              GtfsFarePaymentType.FILENAME,
              entity.csvRowNumber(),
              GtfsFarePaymentType.FARE_PAYMENT_TYPE_NAME_FIELD_NAME));
    }
  }

  private static boolean shouldPaymentMethodHaveName(GtfsFarePaymentMethod method) {
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
