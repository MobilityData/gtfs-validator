package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFareTransferRule;

@GtfsValidator
public class FareTransferRuleDurationLimitTypeValidator
    extends SingleEntityValidator<GtfsFareTransferRule> {

  @Override
  public void validate(GtfsFareTransferRule rule, NoticeContainer noticeContainer) {
    if (rule.hasDurationLimit() && !rule.hasDurationLimitType()) {
      noticeContainer.addValidationNotice(
          new FareTransferRuleDurationLimitWithoutTypeNotice(rule.csvRowNumber()));
    }
  }

  /**
   * A row from GTFS file `fare_transfer_rules.txt` has a defined `duration_limit` field but no
   * `duration_limit_type` specified.
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  static class FareTransferRuleDurationLimitWithoutTypeNotice extends ValidationNotice {
    private final long csvRowNumber;

    FareTransferRuleDurationLimitWithoutTypeNotice(long csvRowNumber) {
      super(SeverityLevel.ERROR);
      this.csvRowNumber = csvRowNumber;
    }
  }
}
