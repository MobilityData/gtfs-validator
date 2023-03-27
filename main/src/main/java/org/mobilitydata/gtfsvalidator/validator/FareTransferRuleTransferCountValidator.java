package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import java.util.Objects;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFareTransferRule;
import org.mobilitydata.gtfsvalidator.table.GtfsFareTransferRuleSchema;

@GtfsValidator
public class FareTransferRuleTransferCountValidator
    extends SingleEntityValidator<GtfsFareTransferRule> {

  @Override
  public void validate(GtfsFareTransferRule rule, NoticeContainer noticeContainer) {
    if (rule.hasFromLegGroupId()
        && rule.hasToLegGroupId()
        && Objects.equals(rule.fromLegGroupId(), rule.toLegGroupId())) {
      if (rule.hasTransferCount()) {
        if (rule.transferCount() < -1 || rule.transferCount() == 0) {
          noticeContainer.addValidationNotice(
              new FareTransferRuleInvalidTransferCountNotice(
                  rule.csvRowNumber(), rule.transferCount()));
        }
      } else {
        noticeContainer.addValidationNotice(
            new FareTransferRuleMissingTransferCountNotice(rule.csvRowNumber()));
      }
    } else {
      if (rule.hasTransferCount()) {
        noticeContainer.addValidationNotice(
            new FareTransferRuleWithForbiddenTransferCountNotice(rule.csvRowNumber()));
      }
    }
  }

  /**
   * A row from GTFS file `fare_transfer_rules.txt` has a defined `transfer_count` with an invalid
   * value.
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  @GtfsValidationNotice(severity = ERROR, files = @FileRefs(GtfsFareTransferRuleSchema.class))
  static class FareTransferRuleInvalidTransferCountNotice extends ValidationNotice {

    // The row of the faulty record.
    private final int csvRowNumber;

    // The transfer count value of the faulty record.
    private final int transferCount;

    FareTransferRuleInvalidTransferCountNotice(int csvRowNumber, int transferCount) {
      super(SeverityLevel.ERROR);
      this.csvRowNumber = csvRowNumber;
      this.transferCount = transferCount;
    }
  }

  /**
   * A row from GTFS file `fare_transfer_rules.txt` has `from_leg_group_id` equal to
   * `to_leg_group_id`, but has no `transfer_count` specified.
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  static class FareTransferRuleMissingTransferCountNotice extends ValidationNotice {

    // The row of the faulty record.
    private final int csvRowNumber;

    FareTransferRuleMissingTransferCountNotice(int csvRowNumber) {
      super(SeverityLevel.ERROR);
      this.csvRowNumber = csvRowNumber;
    }
  }

  /**
   * A row from GTFS file `fare_transfer_rules.txt` has `from_leg_group_id` not equal to
   * `to_leg_group_id`, but has `transfer_count` specified.
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  static class FareTransferRuleWithForbiddenTransferCountNotice extends ValidationNotice {

    // The row of the faulty record.
    private final int csvRowNumber;

    FareTransferRuleWithForbiddenTransferCountNotice(int csvRowNumber) {
      super(SeverityLevel.ERROR);
      this.csvRowNumber = csvRowNumber;
    }
  }
}
