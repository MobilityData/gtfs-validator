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
import org.mobilitydata.gtfsvalidator.validator.ForbiddenRealTimeBookingFieldValidator.ForbiddenRealTimeBookingFieldNotice;

@RunWith(JUnit4.class)
public class ForbiddenRealTimeBookingFieldValidatorTest {

  private static List<ValidationNotice> generateNotices(GtfsBookingRules bookingRule) {
    NoticeContainer noticeContainer = new NoticeContainer();
    ForbiddenRealTimeBookingFieldValidator validator = new ForbiddenRealTimeBookingFieldValidator();
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
            new ForbiddenRealTimeBookingFieldNotice(
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
            new ForbiddenRealTimeBookingFieldNotice(
                bookingRule,
                List.of(
                    GtfsBookingRules.PRIOR_NOTICE_DURATION_MAX_FIELD_NAME,
                    GtfsBookingRules.PRIOR_NOTICE_START_TIME_FIELD_NAME,
                    GtfsBookingRules.PRIOR_NOTICE_SERVICE_ID_FIELD_NAME)));
  }
}
