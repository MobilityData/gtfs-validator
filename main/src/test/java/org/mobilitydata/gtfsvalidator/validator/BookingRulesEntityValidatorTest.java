package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.table.GtfsBookingType.REALTIME;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsBookingRules;
import org.mobilitydata.gtfsvalidator.table.GtfsBookingType;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.validator.BookingRulesEntityValidator.ForbiddenRealTimeBookingFieldValueNotice;
import org.mobilitydata.gtfsvalidator.validator.BookingRulesEntityValidator.PriorNoticeLastDayAfterStartDayNotice;

@RunWith(JUnit4.class)
public class BookingRulesEntityValidatorTest {

  private static List<ValidationNotice> generateNotices(GtfsBookingRules bookingRule) {
    NoticeContainer noticeContainer = new NoticeContainer();
    BookingRulesEntityValidator validator = new BookingRulesEntityValidator();
    validator.validate(bookingRule, noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void realTimeBookingWithForbiddenFieldsShouldGenerateNotice() {
    GtfsBookingRules bookingRule =
        new GtfsBookingRules.Builder()
            .setCsvRowNumber(1)
            .setBookingRuleId("rule-1")
            .setBookingType(REALTIME)
            .setPriorNoticeDurationMin(30) // Forbidden field
            .setPriorNoticeLastDay(2) // Forbidden field
            .build();

    assertThat(generateNotices(bookingRule))
        .containsExactly(
            new ForbiddenRealTimeBookingFieldValueNotice(
                bookingRule,
                List.of(
                    GtfsBookingRules.PRIOR_NOTICE_DURATION_MIN_FIELD_NAME,
                    GtfsBookingRules.PRIOR_NOTICE_LAST_DAY_FIELD_NAME)));
  }

  @Test
  public void realTimeBookingWithoutForbiddenFieldsShouldNotGenerateNotice() {
    GtfsBookingRules bookingRule =
        new GtfsBookingRules.Builder()
            .setCsvRowNumber(1)
            .setBookingRuleId("rule-2")
            .setBookingType(REALTIME)
            .build();

    assertThat(generateNotices(bookingRule)).isEmpty();
  }

  @Test
  public void scheduledBookingShouldNotGenerateNotice() {
    GtfsBookingRules bookingRule =
        new GtfsBookingRules.Builder()
            .setCsvRowNumber(1)
            .setPriorNoticeDurationMin(1)
            .setBookingRuleId("rule-3")
            .setBookingType(GtfsBookingType.SAMEDAY)
            .build();

    assertThat(generateNotices(bookingRule)).isEmpty();
  }

  @Test
  public void realTimeBookingWithMultipleForbiddenFieldsShouldGenerateNotice() {
    GtfsBookingRules bookingRule =
        new GtfsBookingRules.Builder()
            .setCsvRowNumber(1)
            .setBookingRuleId("rule-4")
            .setBookingType(REALTIME)
            .setPriorNoticeStartDay(5) // Forbidden field
            .setPriorNoticeStartTime(GtfsTime.fromSecondsSinceMidnight(2)) // Forbidden field
            .setPriorNoticeServiceId("service-1") // Forbidden field
            .build();

    assertThat(generateNotices(bookingRule))
        .containsExactly(
            new ForbiddenRealTimeBookingFieldValueNotice(
                bookingRule,
                List.of(
                    GtfsBookingRules.PRIOR_NOTICE_START_DAY_FIELD_NAME,
                    GtfsBookingRules.PRIOR_NOTICE_START_TIME_FIELD_NAME,
                    GtfsBookingRules.PRIOR_NOTICE_SERVICE_ID_FIELD_NAME)));
  }

  @Test
  public void sameDayBookingWithForbiddenFieldsShouldGenerateNotice() {
    GtfsBookingRules bookingRule =
        new GtfsBookingRules.Builder()
            .setCsvRowNumber(1)
            .setBookingRuleId("rule-5")
            .setBookingType(GtfsBookingType.SAMEDAY)
            .setPriorNoticeLastDay(2) // Forbidden field
            .setPriorNoticeDurationMin(1)
            .build();

    assertThat(generateNotices(bookingRule))
        .containsExactly(
            new BookingRulesEntityValidator.ForbiddenSameDayBookingFieldValueNotice(
                bookingRule, List.of(GtfsBookingRules.PRIOR_NOTICE_LAST_DAY_FIELD_NAME)));
  }

  @Test
  public void sameDayBookingWithoutForbiddenFieldsShouldNotGenerateNotice() {
    GtfsBookingRules bookingRule =
        new GtfsBookingRules.Builder()
            .setCsvRowNumber(1)
            .setPriorNoticeDurationMin(1)
            .setBookingRuleId("rule-6")
            .setBookingType(GtfsBookingType.SAMEDAY)
            .build();

    assertThat(generateNotices(bookingRule)).isEmpty();
  }

  @Test
  public void priorDayBookingWithForbiddenFieldsShouldGenerateNotice() {
    GtfsBookingRules bookingRule =
        new GtfsBookingRules.Builder()
            .setCsvRowNumber(1)
            .setBookingRuleId("rule-7")
            .setBookingType(GtfsBookingType.PRIORDAY)
            .setPriorNoticeLastDay(1)
            .setPriorNoticeLastTime(GtfsTime.fromSecondsSinceMidnight(5000))
            .setPriorNoticeDurationMin(30) // Forbidden field
            .setPriorNoticeDurationMax(60) // Forbidden field
            .build();

    assertThat(generateNotices(bookingRule))
        .containsExactly(
            new BookingRulesEntityValidator.ForbiddenPriorDayBookingFieldValueNotice(
                bookingRule,
                List.of(
                    GtfsBookingRules.PRIOR_NOTICE_DURATION_MIN_FIELD_NAME,
                    GtfsBookingRules.PRIOR_NOTICE_DURATION_MAX_FIELD_NAME)));
  }

  @Test
  public void invalidPriorNoticeDurationMinShouldGenerateNotice() {
    GtfsBookingRules bookingRule =
        new GtfsBookingRules.Builder()
            .setCsvRowNumber(1)
            .setBookingRuleId("rule-8")
            .setBookingType(GtfsBookingType.SAMEDAY)
            .setPriorNoticeDurationMin(60) // Invalid: greater than max
            .setPriorNoticeDurationMax(30)
            .build();

    assertThat(generateNotices(bookingRule))
        .containsExactly(
            new BookingRulesEntityValidator.InvalidPriorNoticeDurationMinNotice(bookingRule));
  }

  @Test
  public void forbiddenPriorNoticeStartDayShouldGenerateNotice() {
    GtfsBookingRules bookingRule =
        new GtfsBookingRules.Builder()
            .setCsvRowNumber(1)
            .setPriorNoticeDurationMin(1)
            .setBookingRuleId("rule-9")
            .setBookingType(GtfsBookingType.SAMEDAY)
            .setPriorNoticeStartTime(GtfsTime.fromSecondsSinceMidnight(5000))
            .setPriorNoticeDurationMax(30) // Duration max is set
            .setPriorNoticeStartDay(5) // Forbidden when duration max is set
            .build();

    assertThat(generateNotices(bookingRule))
        .containsExactly(
            new BookingRulesEntityValidator.ForbiddenPriorNoticeStartDayNotice(bookingRule));
  }

  @Test
  public void priorNoticeLastDayAfterStartDayShouldGenerateNotice() {
    GtfsBookingRules bookingRule =
        new GtfsBookingRules.Builder()
            .setCsvRowNumber(1)
            .setPriorNoticeLastDay(5)
            .setPriorNoticeStartDay(3)
            .build();

    assertThat(generateNotices(bookingRule))
        .contains(new PriorNoticeLastDayAfterStartDayNotice(bookingRule));
  }

  @Test
  public void missingPriorNoticeDurationMinShouldGenerateNotice() {
    GtfsBookingRules bookingRule =
        new GtfsBookingRules.Builder()
            .setCsvRowNumber(1)
            .setBookingRuleId("rule-10")
            .setBookingType(GtfsBookingType.SAMEDAY) // SAMEDAY booking type
            .build(); // No prior_notice_duration_min set

    assertThat(generateNotices(bookingRule))
        .containsExactly(
            new BookingRulesEntityValidator.MissingPriorNoticeDurationMinNotice(bookingRule));
  }

  @Test
  public void missingPriorDayBookingFieldValueShouldGenerateNotice() {
    // Case 1: Missing both prior_notice_last_day and prior_notice_last_time
    GtfsBookingRules bookingRule =
        new GtfsBookingRules.Builder()
            .setCsvRowNumber(1)
            .setBookingRuleId("rule-11")
            .setBookingType(GtfsBookingType.PRIORDAY) // PRIORDAY booking type
            .build(); // No prior_notice_last_day or prior_notice_last_time set

    assertThat(generateNotices(bookingRule))
        .containsExactly(
            new BookingRulesEntityValidator.MissingPriorDayBookingFieldValueNotice(bookingRule));

    // Case 2: Missing prior_notice_last_time only
    GtfsBookingRules bookingRuleMissingTime =
        new GtfsBookingRules.Builder()
            .setCsvRowNumber(2)
            .setBookingRuleId("rule-12")
            .setBookingType(GtfsBookingType.PRIORDAY) // PRIORDAY booking type
            .setPriorNoticeLastDay(2) // Setting prior_notice_last_day
            .build(); // No prior_notice_last_time set

    assertThat(generateNotices(bookingRuleMissingTime))
        .containsExactly(
            new BookingRulesEntityValidator.MissingPriorDayBookingFieldValueNotice(
                bookingRuleMissingTime));

    // Case 3: Missing prior_notice_last_day only
    GtfsBookingRules bookingRuleMissingDay =
        new GtfsBookingRules.Builder()
            .setCsvRowNumber(3)
            .setBookingRuleId("rule-13")
            .setBookingType(GtfsBookingType.PRIORDAY) // PRIORDAY booking type
            .setPriorNoticeLastTime(
                GtfsTime.fromSecondsSinceMidnight(5000)) // Setting prior_notice_last_time
            .build(); // No prior_notice_last_day set

    assertThat(generateNotices(bookingRuleMissingDay))
        .containsExactly(
            new BookingRulesEntityValidator.MissingPriorDayBookingFieldValueNotice(
                bookingRuleMissingDay));
  }

  @Test
  public void forbiddenPriorNoticeStartTimeShouldGenerateNotice() {
    GtfsBookingRules bookingRule =
        new GtfsBookingRules.Builder()
            .setCsvRowNumber(2)
            .setPriorNoticeLastTime(GtfsTime.fromSecondsSinceMidnight(5000))
            .setPriorNoticeLastDay(2)
            .setBookingRuleId("rule-14")
            .setBookingType(GtfsBookingType.PRIORDAY)
            .setPriorNoticeStartTime(GtfsTime.fromSecondsSinceMidnight(5000)) // Set start time
            .build(); // No prior_notice_start_day set

    assertThat(generateNotices(bookingRule))
        .containsExactly(
            new BookingRulesEntityValidator.ForbiddenPriorNoticeStartTimeNotice(bookingRule));
  }

  @Test
  public void missingPriorNoticeStartTimeShouldGenerateNotice() {
    GtfsBookingRules bookingRule =
        new GtfsBookingRules.Builder()
            .setCsvRowNumber(2)
            .setPriorNoticeLastTime(GtfsTime.fromSecondsSinceMidnight(5000))
            .setPriorNoticeLastDay(2)
            .setBookingRuleId("rule-14")
            .setBookingType(GtfsBookingType.PRIORDAY)
            .setPriorNoticeStartDay(3) // Set start day
            .build(); // No prior_notice_start_time set

    assertThat(generateNotices(bookingRule))
        .containsExactly(
            new BookingRulesEntityValidator.MissingPriorNoticeStartTimeNotice(bookingRule));
  }
}
