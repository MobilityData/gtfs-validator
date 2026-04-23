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
public class PickupDropOffTypeValidatorTest {
  static PickupDropOffTypeValidator validator = new PickupDropOffTypeValidator();

  private static List<ValidationNotice> generateNotices(GtfsStopTime stopTime) {
    NoticeContainer noticeContainer = new NoticeContainer();
    validator.validate(stopTime, noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void forbiddenDropOffTypeShouldGenerateNotice() {
    GtfsStopTime stopTime =
        new GtfsStopTime.Builder()
            .setCsvRowNumber(1)
            .setPickupType(GtfsPickupDropOff.NOT_AVAILABLE)
            .setDropOffType(GtfsPickupDropOff.REGULAR)
            .setStartPickupDropOffWindow(GtfsTime.fromString("00:00:02"))
            .setEndPickupDropOffWindow(GtfsTime.fromString("00:00:03"))
            .build();
    assertThat(generateNotices(stopTime))
        .containsExactly(
            new PickupDropOffTypeValidator.ForbiddenDropOffTypeNotice(
                1, GtfsTime.fromString("00:00:02"), GtfsTime.fromString("00:00:03")));
  }

  @Test
  public void allowedDropOffTypeShouldNotGenerateNotice() {
    GtfsStopTime stopTime =
        new GtfsStopTime.Builder()
            .setCsvRowNumber(2)
            .setDropOffType(GtfsPickupDropOff.NOT_AVAILABLE)
            .build();
    assertThat(generateNotices(stopTime)).isEmpty();
  }

  @Test
  public void forbiddenPickupTypeShouldGenerateNotice() {
    GtfsStopTime stopTime =
        new GtfsStopTime.Builder()
            .setCsvRowNumber(3)
            .setPickupType(GtfsPickupDropOff.REGULAR)
            .setDropOffType(GtfsPickupDropOff.NOT_AVAILABLE)
            .setStartPickupDropOffWindow(GtfsTime.fromString("08:00:00"))
            .setEndPickupDropOffWindow(GtfsTime.fromString("09:00:00"))
            .build();
    assertThat(generateNotices(stopTime))
        .containsExactly(
            new PickupDropOffTypeValidator.ForbiddenPickupTypeNotice(
                3, GtfsTime.fromString("08:00:00"), GtfsTime.fromString("09:00:00")));
  }

  @Test
  public void allowedPickupTypeShouldNotGenerateNotice() {
    GtfsStopTime stopTime =
        new GtfsStopTime.Builder()
            .setCsvRowNumber(4)
            .setPickupType(GtfsPickupDropOff.NOT_AVAILABLE)
            .build();
    assertThat(generateNotices(stopTime)).isEmpty();
  }
}
