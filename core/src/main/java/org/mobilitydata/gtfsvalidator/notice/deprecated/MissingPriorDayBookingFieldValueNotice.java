package org.mobilitydata.gtfsvalidator.notice.deprecated;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingPriorNoticeLastDayNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingPriorNoticeLastTimeNotice;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;

/**
 * The `prior_notice_last_day` and the `prior_notice_last_time` values are required for prior day
 * `booking_type` in booking_rules.txt.
 */
@GtfsValidationNotice(
    severity = SeverityLevel.ERROR,
    deprecated = true,
    deprecationVersion = "7.0.0",
    deprecationReason =
        "Separated into `missing_prior_notice_last_day` and `missing_prior_notice_last_time` notices",
    replacementNotices = {
      MissingPriorNoticeLastDayNotice.class,
      MissingPriorNoticeLastTimeNotice.class
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
