package org.mobilitydata.gtfsvalidator.notice;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

/**
 * The `prior_notice_last_day` value is required for prior day `booking_type` in booking_rules.txt.
 */
@GtfsValidationNotice(severity = SeverityLevel.ERROR)
public class MissingPriorNoticeLastDayNotice extends ValidationNotice {
  /** The row number of the faulty record. */
  private final int csvRowNumber;

  /** The `booking_rules.booking_rule_id` of the faulty record. */
  private final String bookingRuleId;

  public MissingPriorNoticeLastDayNotice(int csvRowNumber, String bookingRuleId) {
    this.csvRowNumber = csvRowNumber;
    this.bookingRuleId = bookingRuleId;
  }
}
