package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFareTransferRule;
import org.mobilitydata.gtfsvalidator.table.GtfsFareTransferRuleSchema;

@GtfsValidator
public class FareTransferRuleDurationLimitTypeValidator
    extends SingleEntityValidator<GtfsFareTransferRule> {

  @Override
  public void validate(GtfsFareTransferRule rule, NoticeContainer noticeContainer) {
    if (rule.hasDurationLimit() && !rule.hasDurationLimitType()) {
      noticeContainer.addValidationNotice(
          new FareTransferRuleDurationLimitWithoutTypeNotice(rule.csvRowNumber()));
    }
    if (!rule.hasDurationLimit() && rule.hasDurationLimitType()) {
      noticeContainer.addValidationNotice(
          new FareTransferRuleDurationLimitTypeWithoutDurationLimitNotice(rule.csvRowNumber()));
    }
  }

  /**
   * A row from GTFS file `fare_transfer_rules.txt` has a defined `duration_limit` field but no
   * `duration_limit_type` specified.
   */
  @GtfsValidationNotice(severity = ERROR, files = @FileRefs(GtfsFareTransferRuleSchema.class))
  static class FareTransferRuleDurationLimitWithoutTypeNotice extends ValidationNotice {

    /** The row of the faulty record. */
    private final int csvRowNumber;

    FareTransferRuleDurationLimitWithoutTypeNotice(int csvRowNumber) {
      this.csvRowNumber = csvRowNumber;
    }
  }

  /**
   * A row from GTFS file `fare_transfer_rules.txt` has a defined `duration_limit_type` field but no
   * `duration_limit` specified.
   */
  @GtfsValidationNotice(severity = ERROR, files = @FileRefs(GtfsFareTransferRuleSchema.class))
  static class FareTransferRuleDurationLimitTypeWithoutDurationLimitNotice
      extends ValidationNotice {

    /** The row of the faulty record. */
    private final int csvRowNumber;

    FareTransferRuleDurationLimitTypeWithoutDurationLimitNotice(int csvRowNumber) {
      this.csvRowNumber = csvRowNumber;
    }
  }
}
