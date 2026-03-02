package org.mobilitydata.gtfsvalidator.deprecated;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsBookingRulesSchema;
import org.mobilitydata.gtfsvalidator.validator.BookingRulesEntityValidator;

/**
 * The `prior_notice_last_day` and the `prior_notice_last_time` values are required for prior day
 * `booking_type` in booking_rules.txt.
 */
@GtfsValidationNotice(
    severity = SeverityLevel.ERROR,
    files = @GtfsValidationNotice.FileRefs(GtfsBookingRulesSchema.class),
    deprecated = true,
    deprecationVersion = "7.0.0",
    deprecationReason =
        "Separated into `missing_prior_notice_last_day` and `missing_prior_notice_last_time` notices",
    replacementNotices = {
      BookingRulesEntityValidator.MissingPriorNoticeLastDayNotice.class,
      BookingRulesEntityValidator.MissingPriorNoticeLastTimeNotice.class
    })
public class MissingPriorDayBookingFieldValueNotice extends ValidationNotice {
  /** The row number of the faulty record. */
  private final int csvRowNumber;

  /** The `booking_rules.booking_rule_id` of the faulty record. */
  private final String bookingRuleId;

  MissingPriorDayBookingFieldValueNotice(int csvRowNumber, String bookingRuleId) {
    this.csvRowNumber = csvRowNumber;
    this.bookingRuleId = bookingRuleId;
  }
}
