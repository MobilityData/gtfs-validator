package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsPickupDropOff;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@RunWith(JUnit4.class)
public class PickupBookingRuleIdValidatorTest {
  static PickupBookingRuleIdValidator validator = new PickupBookingRuleIdValidator(null, null);

  private static List<ValidationNotice> generateNotices(GtfsStopTime stopTime) {
    NoticeContainer noticeContainer = new NoticeContainer();
    validator.validate(stopTime, noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void missingBookingRuleIdShouldGenerateNotice() {
    GtfsStopTime stopTime =
        new GtfsStopTime.Builder()
            .setCsvRowNumber(1)
            .setPickupType(GtfsPickupDropOff.MUST_PHONE)
            .setStartPickupDropOffWindow(GtfsTime.fromSecondsSinceMidnight(18614))
            .build();
    assertThat(generateNotices(stopTime))
        .containsExactly(
            new PickupBookingRuleIdValidator.MissingPickupDropOffBookingRuleIdNotice(
                1, GtfsPickupDropOff.MUST_PHONE, null));
  }

  @Test
  public void existingStartPickupDropOffWindowShouldNotGenerateNotice() {
    GtfsStopTime stopTime =
        new GtfsStopTime.Builder()
            .setCsvRowNumber(2)
            .setPickupType(GtfsPickupDropOff.MUST_PHONE)
            .setStartPickupDropOffWindow(GtfsTime.fromSecondsSinceMidnight(18614))
            .setPickupBookingRuleId("booking_rule_id")
            .build();
    assertThat(generateNotices(stopTime)).isEmpty();
  }

  @Test
  public void pickUpTypeNotMustPhoneShouldNotGenerateNotice() {
    GtfsStopTime stopTime =
        new GtfsStopTime.Builder()
            .setCsvRowNumber(3)
            .setPickupType(GtfsPickupDropOff.NOT_AVAILABLE)
            .build();
    assertThat(generateNotices(stopTime)).isEmpty();
  }
}
