package org.mobilitydata.gtfsvalidator.validator;

import java.util.ArrayList;
import java.util.List;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsBookingRules;
import org.mobilitydata.gtfsvalidator.table.GtfsBookingRulesSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsBookingType;

@GtfsValidator
public class ForbiddenRealTimeBookingFieldValidator
    extends SingleEntityValidator<GtfsBookingRules> {

  @Override
  public void validate(GtfsBookingRules entity, NoticeContainer noticeContainer) {
    // Only validate entities with REALTIME booking type
    if (entity.bookingType() != GtfsBookingType.REALTIME) {
      return;
    }

    // Retrieve the list of forbidden fields
    List<String> forbiddenFields = findForbiddenRealTimeFields(entity);

    // If there are any forbidden fields, add a validation notice
    if (!forbiddenFields.isEmpty()) {
      noticeContainer.addValidationNotice(
          new ForbiddenRealTimeBookingFieldNotice(entity, forbiddenFields));
    }
  }

  /** Finds forbidden fields that should not be present for real-time booking rules. */
  public static List<String> findForbiddenRealTimeFields(GtfsBookingRules bookingRule) {
    List<String> fields = new ArrayList<>();

    // Check each field and add its name to the list if it's present
    if (bookingRule.hasPriorNoticeDurationMin()) {
      fields.add(GtfsBookingRules.PRIOR_NOTICE_DURATION_MIN_FIELD_NAME);
    }
    if (bookingRule.hasPriorNoticeDurationMax()) {
      fields.add(GtfsBookingRules.PRIOR_NOTICE_DURATION_MAX_FIELD_NAME);
    }
    if (bookingRule.hasPriorNoticeLastDay()) {
      fields.add(GtfsBookingRules.PRIOR_NOTICE_LAST_DAY_FIELD_NAME);
    }
    if (bookingRule.hasPriorNoticeLastTime()) {
      fields.add(GtfsBookingRules.PRIOR_NOTICE_LAST_TIME_FIELD_NAME);
    }
    if (bookingRule.hasPriorNoticeStartDay()) {
      fields.add(GtfsBookingRules.PRIOR_NOTICE_START_DAY_FIELD_NAME);
    }
    if (bookingRule.hasPriorNoticeStartTime()) {
      fields.add(GtfsBookingRules.PRIOR_NOTICE_START_TIME_FIELD_NAME);
    }
    if (bookingRule.hasPriorNoticeServiceId()) {
      fields.add(GtfsBookingRules.PRIOR_NOTICE_SERVICE_ID_FIELD_NAME);
    }

    return fields;
  }

  /** A forbidden field is present for a real-time booking rule. */
  @GtfsValidationNotice(
      severity = SeverityLevel.ERROR,
      files = @FileRefs(GtfsBookingRulesSchema.class))
  static class ForbiddenRealTimeBookingFieldNotice extends ValidationNotice {
    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The `booking_rules.booking_rule_id` of the faulty record. */
    private final String bookingRuleId;

    /** The names of the forbidden fields comma-separated. */
    private final String fieldNames;

    ForbiddenRealTimeBookingFieldNotice(
        GtfsBookingRules bookingRule, List<String> forbiddenFields) {
      this.csvRowNumber = bookingRule.csvRowNumber();
      this.bookingRuleId = bookingRule.bookingRuleId();
      this.fieldNames = String.join(", ", forbiddenFields);
    }
  }
}
