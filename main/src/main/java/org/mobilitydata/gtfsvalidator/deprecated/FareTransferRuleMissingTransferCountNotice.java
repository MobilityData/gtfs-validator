package org.mobilitydata.gtfsvalidator.deprecated;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFareTransferRuleSchema;
import org.mobilitydata.gtfsvalidator.validator.FareTransferRuleTransferCountValidator;

/**
 * A row from `fare_transfer_rules.txt` has `from_leg_group_id` equal to `to_leg_group_id`, but has
 * no `transfer_count` specified.
 *
 * <p>Per the spec, `transfer_count` is required if the two leg group ids are equal.
 */
@GtfsValidationNotice(
    severity = ERROR,
    files = @GtfsValidationNotice.FileRefs(GtfsFareTransferRuleSchema.class),
    deprecated = true,
    deprecationReason = "Renamed to `fare_transfer_rule_without_transfer_count`.",
    deprecationVersion = "8.0",
    replacementNotices = {
      FareTransferRuleTransferCountValidator.FareTransferRuleWithoutTransferCountNotice.class
    })
class FareTransferRuleMissingTransferCountNotice extends ValidationNotice {

  /** The row of the faulty record. */
  private final int csvRowNumber;

  FareTransferRuleMissingTransferCountNotice(int csvRowNumber) {
    this.csvRowNumber = csvRowNumber;
  }
}
