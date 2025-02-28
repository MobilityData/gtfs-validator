package org.mobilitydata.gtfsvalidator.notice;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

/** `prior_notice_last_time` value is required for prior day `booking_type` in booking_rules.txt. */
@GtfsValidationNotice(severity = SeverityLevel.ERROR)
public class MissingPriorNoticeLastTimeNotice extends ValidationNotice {
  /** The row number of the faulty record. */
  private final int csvRowNumber;

  /** The `booking_rules.booking_rule_id` of the faulty record. */
  private final String bookingRuleId;

  public MissingPriorNoticeLastTimeNotice(int csvRowNumber, String bookingRuleId) {
    this.csvRowNumber = csvRowNumber;
    this.bookingRuleId = bookingRuleId;
  }
}
