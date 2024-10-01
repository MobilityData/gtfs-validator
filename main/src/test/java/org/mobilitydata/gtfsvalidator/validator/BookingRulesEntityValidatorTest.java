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
            .setPriorNoticeDurationMax(60) // Forbidden field
            .setPriorNoticeStartTime(GtfsTime.fromSecondsSinceMidnight(2)) // Forbidden field
            .setPriorNoticeServiceId("service-1") // Forbidden field
            .build();

    assertThat(generateNotices(bookingRule))
        .containsExactly(
            new ForbiddenRealTimeBookingFieldValueNotice(
                bookingRule,
                List.of(
                    GtfsBookingRules.PRIOR_NOTICE_DURATION_MAX_FIELD_NAME,
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
            .setPriorNoticeStartTime(GtfsTime.fromSecondsSinceMidnight(5000))
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
            new BookingRulesEntityValidator.InvalidPriorNoticeDurationMinNotice(
                bookingRule, 60, 30));
  }

  @Test
  public void forbiddenPriorNoticeStartDayShouldGenerateNotice() {
    GtfsBookingRules bookingRule =
        new GtfsBookingRules.Builder()
            .setCsvRowNumber(1)
            .setBookingRuleId("rule-9")
            .setBookingType(GtfsBookingType.SAMEDAY)
            .setPriorNoticeDurationMax(30) // Duration max is set
            .setPriorNoticeStartDay(5) // Forbidden when duration max is set
            .build();

    assertThat(generateNotices(bookingRule))
        .containsExactly(
            new BookingRulesEntityValidator.ForbiddenPriorNoticeStartDayNotice(bookingRule, 5, 30));
  }
}
