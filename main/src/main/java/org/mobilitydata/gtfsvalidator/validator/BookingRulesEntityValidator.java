package org.mobilitydata.gtfsvalidator.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.*;
import org.mobilitydata.gtfsvalidator.table.GtfsBookingRules;
import org.mobilitydata.gtfsvalidator.table.GtfsBookingRulesSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsBookingType;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

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
    validatePriorNoticeDayRange(entity, noticeContainer);
    validateMissingPriorDayBookingFields(entity, noticeContainer);
    validatePriorNoticeStartTime(entity, noticeContainer);
  }

  private static void validatePriorNoticeDurationMin(
      GtfsBookingRules entity, NoticeContainer noticeContainer) {
    // Check if prior_notice_duration_min is set for booking_type SAMEDAY
    if (entity.bookingType() == GtfsBookingType.SAMEDAY && !entity.hasPriorNoticeDurationMin()) {
      noticeContainer.addValidationNotice(new MissingPriorNoticeDurationMinNotice(entity));
    }
    // Check if prior_notice_duration_min is less than prior_notice_duration_max
    if (entity.hasPriorNoticeDurationMin() && entity.hasPriorNoticeDurationMax()) {
      if (entity.priorNoticeDurationMax() < entity.priorNoticeDurationMin()) {
        noticeContainer.addValidationNotice(new InvalidPriorNoticeDurationMinNotice(entity));
      }
    }
  }

  private static void validateMissingPriorDayBookingFields(
      GtfsBookingRules entity, NoticeContainer noticeContainer) {
    if (entity.bookingType() == GtfsBookingType.PRIORDAY) {
      if (!entity.hasPriorNoticeLastDay()) {
        noticeContainer.addValidationNotice(
            new MissingPriorNoticeLastDayNotice(entity.csvRowNumber(), entity.bookingRuleId()));
      }
      if (!entity.hasPriorNoticeLastTime()) {
        noticeContainer.addValidationNotice(
            new MissingPriorNoticeLastTimeNotice(entity.csvRowNumber(), entity.bookingRuleId()));
      }
    }
  }

  private static void validatePriorNoticeStartTime(
      GtfsBookingRules entity, NoticeContainer noticeContainer) {
    if (entity.hasPriorNoticeStartTime() && !entity.hasPriorNoticeStartDay()) {
      noticeContainer.addValidationNotice(new ForbiddenPriorNoticeStartTimeNotice(entity));
    }
    if (!entity.hasPriorNoticeStartTime() && entity.hasPriorNoticeStartDay()) {
      noticeContainer.addValidationNotice(new MissingPriorNoticeStartTimeNotice(entity));
    }
  }

  private static void validatePriorNoticeStartDay(
      GtfsBookingRules entity, NoticeContainer noticeContainer) {
    if (entity.hasPriorNoticeDurationMax() && entity.hasPriorNoticeStartDay()) {
      noticeContainer.addValidationNotice(new ForbiddenPriorNoticeStartDayNotice(entity));
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

  private static void validatePriorNoticeDayRange(
      GtfsBookingRules entity, NoticeContainer noticeContainer) {
    if (entity.hasPriorNoticeLastDay()
        && entity.hasPriorNoticeStartDay()
        && entity.priorNoticeLastDay() > entity.priorNoticeStartDay()) {
      noticeContainer.addValidationNotice(new PriorNoticeLastDayAfterStartDayNotice(entity));
    }
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

    InvalidPriorNoticeDurationMinNotice(GtfsBookingRules bookingRule) {
      this.csvRowNumber = bookingRule.csvRowNumber();
      this.bookingRuleId = bookingRule.bookingRuleId();
      this.priorNoticeDurationMin = bookingRule.priorNoticeDurationMin();
      this.priorNoticeDurationMax = bookingRule.priorNoticeDurationMax();
    }
  }

  /**
   * `prior_notice_duration_min` value is required for same day `booking_type` in booking_rules.txt.
   */
  @GtfsValidationNotice(
      severity = SeverityLevel.ERROR,
      files = @FileRefs(GtfsBookingRulesSchema.class))
  static class MissingPriorNoticeDurationMinNotice extends ValidationNotice {
    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The `booking_rules.booking_rule_id` of the faulty record. */
    private final String bookingRuleId;

    MissingPriorNoticeDurationMinNotice(GtfsBookingRules bookingRule) {
      this.csvRowNumber = bookingRule.csvRowNumber();
      this.bookingRuleId = bookingRule.bookingRuleId();
    }
  }

  /** `prior_notice_start_day` value is forbidden when `prior_notice_duration_max` is set. */
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

    ForbiddenPriorNoticeStartDayNotice(GtfsBookingRules bookingRule) {
      this.csvRowNumber = bookingRule.csvRowNumber();
      this.bookingRuleId = bookingRule.bookingRuleId();
      this.priorNoticeStartDay = bookingRule.priorNoticeStartDay();
      this.priorNoticeDurationMax = bookingRule.priorNoticeDurationMax();
    }
  }

  /**
   * Prior notice last day should not be greater than the prior notice start day in
   * booking_rules.txt.
   */
  @GtfsValidationNotice(
      severity = SeverityLevel.ERROR,
      files = @FileRefs(GtfsBookingRulesSchema.class))
  static class PriorNoticeLastDayAfterStartDayNotice extends ValidationNotice {

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The value of the `prior_notice_last_day` of the faulty field. */
    private final int priorNoticeLastDay;

    /** The value of the `prior_notice_start_day` of the faulty field. */
    private final int priorNoticeStartDay;

    /**
     * Constructs a new validation notice.
     *
     * @param bookingRule the booking rule entity that triggered this notice
     */
    PriorNoticeLastDayAfterStartDayNotice(GtfsBookingRules bookingRule) {
      this.csvRowNumber = bookingRule.csvRowNumber();
      this.priorNoticeLastDay = bookingRule.priorNoticeLastDay();
      this.priorNoticeStartDay = bookingRule.priorNoticeStartDay();
    }
  }

  /**
   * `prior_notice_start_time` value is forbidden when `prior_notice_start_day` value is not set in
   * booking_rules.txt.
   */
  @GtfsValidationNotice(
      severity = SeverityLevel.ERROR,
      files = @FileRefs(GtfsBookingRulesSchema.class))
  static class ForbiddenPriorNoticeStartTimeNotice extends ValidationNotice {
    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The `booking_rules.booking_rule_id` of the faulty record. */
    private final String bookingRuleId;

    /** The value of the `prior_notice_start_time` field. */
    private final GtfsTime priorNoticeStartTime;

    ForbiddenPriorNoticeStartTimeNotice(GtfsBookingRules bookingRule) {
      this.csvRowNumber = bookingRule.csvRowNumber();
      this.bookingRuleId = bookingRule.bookingRuleId();
      this.priorNoticeStartTime = bookingRule.priorNoticeStartTime();
    }
  }

  /**
   * `prior_notice_start_time` value is required when `prior_notice_start_day` value is set in
   * booking_rules.txt.
   */
  @GtfsValidationNotice(
      severity = SeverityLevel.ERROR,
      files = @FileRefs(GtfsBookingRulesSchema.class))
  static class MissingPriorNoticeStartTimeNotice extends ValidationNotice {
    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The `booking_rules.booking_rule_id` of the faulty record. */
    private final String bookingRuleId;

    /** The value of the `prior_notice_start_day` field. */
    private final int priorNoticeStartDay;

    MissingPriorNoticeStartTimeNotice(GtfsBookingRules bookingRule) {
      this.csvRowNumber = bookingRule.csvRowNumber();
      this.bookingRuleId = bookingRule.bookingRuleId();
      this.priorNoticeStartDay = bookingRule.priorNoticeStartDay();
    }
  }
}
