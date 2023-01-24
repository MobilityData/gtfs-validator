package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFareProduct;

@GtfsValidator
public class FareProductPaymentOptionReferenceValidator
    extends SingleEntityValidator<GtfsFareProduct> {

  @Override
  public void validate(GtfsFareProduct entity, NoticeContainer noticeContainer) {
    if (entity.hasFarePaymentOptionId() && entity.hasFarePaymentOptionGroupId()) {
      noticeContainer.addValidationNotice(new FareProductPaymentOptionReferenceNotice(entity));
    }
  }

  static class FareProductPaymentOptionReferenceNotice extends ValidationNotice {
    private int csvRowNumber;

    public FareProductPaymentOptionReferenceNotice(GtfsFareProduct entity) {
      super(SeverityLevel.ERROR);
      this.csvRowNumber = entity.csvRowNumber();
    }
  }
}
