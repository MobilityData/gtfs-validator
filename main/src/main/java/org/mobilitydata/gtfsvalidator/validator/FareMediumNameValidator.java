package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFareMedium;
import org.mobilitydata.gtfsvalidator.table.GtfsFareMediumType;

@GtfsValidator
public class FareMediumNameValidator extends SingleEntityValidator<GtfsFareMedium> {

  @Override
  public void validate(GtfsFareMedium entity, NoticeContainer noticeContainer) {
    if (shouldHaveName(entity.fareMediumType()) && !entity.hasFareMediumName()) {
      noticeContainer.addValidationNotice(
          new MissingRecommendedFieldNotice(
              GtfsFareMedium.FILENAME,
              entity.csvRowNumber(),
              GtfsFareMedium.FARE_MEDIUM_NAME_FIELD_NAME));
    }
  }

  private static boolean shouldHaveName(GtfsFareMediumType type) {
    switch (type) {
      case TRANSIT_CARD:
      case MOBILE_APP:
        return true;
      case NONE:
      case PAPER_TICKET:
      case CONTACTLESS_EMV:
      default:
        return false;
    }
  }
}
