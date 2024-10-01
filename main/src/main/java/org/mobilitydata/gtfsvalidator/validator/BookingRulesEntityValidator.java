package org.mobilitydata.gtfsvalidator.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
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
public class BookingRulesEntityValidator extends SingleEntityValidator<GtfsBookingRules> {

  @Override
  public void validate(GtfsBookingRules entity, NoticeContainer noticeContainer) {
    validateBookingType(
        entity,
        GtfsBookingType.REALTIME,
        BookingRulesEntityValidator::findForbiddenRealTimeFields,
        ForbiddenRealTimeBookingFieldValueNotice::new,
        noticeContainer);
    validateBookingType(
        entity,
        GtfsBookingType.SAMEDAY,
        BookingRulesEntityValidator::findForbiddenSameDayFields,
        ForbiddenSameDayBookingFieldValueNotice::new,
        noticeContainer);
    validateBookingType(
        entity,
        GtfsBookingType.PRIORDAY,
        BookingRulesEntityValidator::findForbiddenPriorDayFields,
        ForbiddenPriorDayBookingFieldValueNotice::new,
        noticeContainer);
    validatePriorNoticeDurationMin(entity, noticeContainer);
    validatePriorNoticeStartDay(entity, noticeContainer);
  }

  private static void validatePriorNoticeDurationMin(
      GtfsBookingRules entity, NoticeContainer noticeContainer) {
    if (entity.hasPriorNoticeDurationMin() && entity.hasPriorNoticeDurationMax()) {
      if (entity.priorNoticeDurationMax() < entity.priorNoticeDurationMin()) {
        noticeContainer.addValidationNotice(
            new InvalidPriorNoticeDurationMinNotice(
                entity, entity.priorNoticeDurationMin(), entity.priorNoticeDurationMax()));
      }
    }
  }

  private static void validatePriorNoticeStartDay(
      GtfsBookingRules entity, NoticeContainer noticeContainer) {
    if (entity.hasPriorNoticeDurationMax() && entity.hasPriorNoticeStartDay()) {
      noticeContainer.addValidationNotice(
          new ForbiddenPriorNoticeStartDayNotice(
              entity, entity.priorNoticeStartDay(), entity.priorNoticeDurationMax()));
    }
  }

  private static void validateBookingType(
      GtfsBookingRules entity,
      GtfsBookingType bookingType,
      Function<GtfsBookingRules, List<String>> forbiddenFieldsFinder,
      ValidationNoticeConstructor validationNoticeConstructor,
      NoticeContainer noticeContainer) {

    if (entity.bookingType() != bookingType) {
      return;
    }

    List<String> forbiddenFields = forbiddenFieldsFinder.apply(entity);

    if (!forbiddenFields.isEmpty()) {
      noticeContainer.addValidationNotice(
          validationNoticeConstructor.create(entity, forbiddenFields));
    }
  }

  private static List<String> findForbiddenSameDayFields(GtfsBookingRules bookingRule) {
    return findForbiddenFields(
        bookingRule.hasPriorNoticeLastDay(),
        GtfsBookingRules.PRIOR_NOTICE_LAST_DAY_FIELD_NAME,
        bookingRule.hasPriorNoticeLastTime(),
        GtfsBookingRules.PRIOR_NOTICE_LAST_TIME_FIELD_NAME,
        bookingRule.hasPriorNoticeServiceId(),
        GtfsBookingRules.PRIOR_NOTICE_SERVICE_ID_FIELD_NAME);
  }

  private static List<String> findForbiddenPriorDayFields(GtfsBookingRules bookingRule) {
    return findForbiddenFields(
        bookingRule.hasPriorNoticeDurationMin(),
        GtfsBookingRules.PRIOR_NOTICE_DURATION_MIN_FIELD_NAME,
        bookingRule.hasPriorNoticeDurationMax(),
        GtfsBookingRules.PRIOR_NOTICE_DURATION_MAX_FIELD_NAME);
  }

  private static List<String> findForbiddenRealTimeFields(GtfsBookingRules bookingRule) {
    return findForbiddenFields(
        bookingRule.hasPriorNoticeDurationMin(),
        GtfsBookingRules.PRIOR_NOTICE_DURATION_MIN_FIELD_NAME,
        bookingRule.hasPriorNoticeDurationMax(),
        GtfsBookingRules.PRIOR_NOTICE_DURATION_MAX_FIELD_NAME,
        bookingRule.hasPriorNoticeLastDay(),
        GtfsBookingRules.PRIOR_NOTICE_LAST_DAY_FIELD_NAME,
        bookingRule.hasPriorNoticeLastTime(),
        GtfsBookingRules.PRIOR_NOTICE_LAST_TIME_FIELD_NAME,
        bookingRule.hasPriorNoticeStartDay(),
        GtfsBookingRules.PRIOR_NOTICE_START_DAY_FIELD_NAME,
        bookingRule.hasPriorNoticeStartTime(),
        GtfsBookingRules.PRIOR_NOTICE_START_TIME_FIELD_NAME,
        bookingRule.hasPriorNoticeServiceId(),
        GtfsBookingRules.PRIOR_NOTICE_SERVICE_ID_FIELD_NAME);
  }

  private static List<String> findForbiddenFields(Object... conditionsAndFields) {
    List<String> fields = new ArrayList<>();
    for (int i = 0; i < conditionsAndFields.length; i += 2) {
      if ((Boolean) conditionsAndFields[i]) {
        fields.add((String) conditionsAndFields[i + 1]);
      }
    }
    return fields;
  }

  // Abstract Notice Creation using Functional Interface
  @FunctionalInterface
  private interface ValidationNoticeConstructor {
    ValidationNotice create(GtfsBookingRules bookingRule, List<String> forbiddenFields);
  }

  /** A forbidden field value is present for a real-time booking rule in `booking_rules.txt`. */
  @GtfsValidationNotice(
      severity = SeverityLevel.ERROR,
      files = @FileRefs(GtfsBookingRulesSchema.class))
  static class ForbiddenRealTimeBookingFieldValueNotice extends ValidationNotice {
    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The `booking_rules.booking_rule_id` of the faulty record. */
    private final String bookingRuleId;

    /** The names of the forbidden fields comma-separated. */
    private final String fieldNames;

    ForbiddenRealTimeBookingFieldValueNotice(
        GtfsBookingRules bookingRule, List<String> forbiddenFields) {
      this.csvRowNumber = bookingRule.csvRowNumber();
      this.bookingRuleId = bookingRule.bookingRuleId();
      this.fieldNames = String.join(", ", forbiddenFields);
    }
  }

  /** A forbidden field value is present for a same-day booking rule in `booking_rules.txt`. */
  @GtfsValidationNotice(
      severity = SeverityLevel.ERROR,
      files = @FileRefs(GtfsBookingRulesSchema.class))
  static class ForbiddenSameDayBookingFieldValueNotice extends ValidationNotice {
    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The `booking_rules.booking_rule_id` of the faulty record. */
    private final String bookingRuleId;

    /** The names of the forbidden fields comma-separated. */
    private final String fieldNames;

    ForbiddenSameDayBookingFieldValueNotice(
        GtfsBookingRules bookingRule, List<String> forbiddenFields) {
      this.csvRowNumber = bookingRule.csvRowNumber();
      this.bookingRuleId = bookingRule.bookingRuleId();
      this.fieldNames = String.join(", ", forbiddenFields);
    }
  }

  /** A forbidden field value is present for a prior-day booking rule in `booking_rules.txt` */
  @GtfsValidationNotice(
      severity = SeverityLevel.ERROR,
      files = @FileRefs(GtfsBookingRulesSchema.class))
  static class ForbiddenPriorDayBookingFieldValueNotice extends ValidationNotice {
    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The `booking_rules.booking_rule_id` of the faulty record. */
    private final String bookingRuleId;

    /** The names of the forbidden fields comma-separated. */
    private final String fieldNames;

    ForbiddenPriorDayBookingFieldValueNotice(
        GtfsBookingRules bookingRule, List<String> forbiddenFields) {
      this.csvRowNumber = bookingRule.csvRowNumber();
      this.bookingRuleId = bookingRule.bookingRuleId();
      this.fieldNames = String.join(", ", forbiddenFields);
    }
  }

  /**
   * An invalid `prior_notice_duration_min` value is present in a booking rule.
   *
   * <p>The `prior_notice_duration_max` field value needs to be greater or equal to the
   * `prior_notice_duration_min` field value.
   */
  @GtfsValidationNotice(
      severity = SeverityLevel.ERROR,
      files = @FileRefs(GtfsBookingRulesSchema.class))
  static class InvalidPriorNoticeDurationMinNotice extends ValidationNotice {
    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The `booking_rules.booking_rule_id` of the faulty record. */
    private final String bookingRuleId;

    /** The value of the `prior_notice_duration_min` field. */
    private final int priorNoticeDurationMin;

    /** The value of the `prior_notice_duration_max` field. */
    private final int priorNoticeDurationMax;

    InvalidPriorNoticeDurationMinNotice(
        GtfsBookingRules bookingRule, int priorNoticeDurationMin, int priorNoticeDurationMax) {
      this.csvRowNumber = bookingRule.csvRowNumber();
      this.bookingRuleId = bookingRule.bookingRuleId();
      this.priorNoticeDurationMin = priorNoticeDurationMin;
      this.priorNoticeDurationMax = priorNoticeDurationMax;
    }
  }

  /**
   * An invalid `prior_notice_start_day` value is present when `prior_notice_duration_max` is set.
   */
  @GtfsValidationNotice(
      severity = SeverityLevel.ERROR,
      files = @FileRefs(GtfsBookingRulesSchema.class))
  static class ForbiddenPriorNoticeStartDayNotice extends ValidationNotice {
    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The `booking_rules.booking_rule_id` of the faulty record. */
    private final String bookingRuleId;

    /** The value of the `prior_notice_duration_min` field. */
    private final int priorNoticeStartDay;

    /** The value of the `prior_notice_duration_max` field. */
    private final int priorNoticeDurationMax;

    ForbiddenPriorNoticeStartDayNotice(
        GtfsBookingRules bookingRule, int priorNoticeStartDay, int priorNoticeDurationMax) {
      this.csvRowNumber = bookingRule.csvRowNumber();
      this.bookingRuleId = bookingRule.bookingRuleId();
      this.priorNoticeStartDay = priorNoticeStartDay;
      this.priorNoticeDurationMax = priorNoticeDurationMax;
    }
  }
}
