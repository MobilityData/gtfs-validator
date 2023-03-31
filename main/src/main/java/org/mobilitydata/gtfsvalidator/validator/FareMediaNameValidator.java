package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFareMedia;
import org.mobilitydata.gtfsvalidator.table.GtfsFareMediaType;

@GtfsValidator
public class FareMediaNameValidator extends SingleEntityValidator<GtfsFareMedia> {

  @Override
  public void validate(GtfsFareMedia entity, NoticeContainer noticeContainer) {
    if (shouldHaveName(entity.fareMediaType()) && !entity.hasFareMediaName()) {
      noticeContainer.addValidationNotice(
          new MissingRecommendedFieldNotice(
              GtfsFareMedia.FILENAME,
              entity.csvRowNumber(),
              GtfsFareMedia.FARE_MEDIA_NAME_FIELD_NAME));
    }
  }

  private static boolean shouldHaveName(GtfsFareMediaType type) {
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
