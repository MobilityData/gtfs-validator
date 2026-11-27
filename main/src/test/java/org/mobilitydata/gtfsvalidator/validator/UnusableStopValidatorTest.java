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
import org.mobilitydata.gtfsvalidator.validator.UnusableStopValidator.UnusableStopNotice;

@RunWith(JUnit4.class)
public class UnusableStopValidatorTest {

  private static List<ValidationNotice> generateNotices(GtfsStopTime stopTime) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new UnusableStopValidator().validate(stopTime, noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  private static GtfsStopTime createStopTime(
      GtfsPickupDropOff pickupType, GtfsPickupDropOff dropOffType) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(2)
        .setTripId("t1")
        .setStopId("s1")
        .setStopSequence(3)
        .setPickupType(pickupType)
        .setDropOffType(dropOffType)
        .build();
  }

  @Test
  public void noPickupAndNoDropOff_generatesNotice() {
    assertThat(
            generateNotices(
                createStopTime(GtfsPickupDropOff.NOT_AVAILABLE, GtfsPickupDropOff.NOT_AVAILABLE)))
        .containsExactly(new UnusableStopNotice(2, "s1", "t1", 3));
  }

  @Test
  public void pickupOrDropOffAvailable_noNotice() {
    for (GtfsPickupDropOff pickupType : GtfsPickupDropOff.values()) {
      for (GtfsPickupDropOff dropOffType : GtfsPickupDropOff.values()) {
        if (pickupType == GtfsPickupDropOff.NOT_AVAILABLE
            && dropOffType == GtfsPickupDropOff.NOT_AVAILABLE) {
          continue;
        }
        assertThat(generateNotices(createStopTime(pickupType, dropOffType))).isEmpty();
      }
    }
  }

  @Test
  public void defaultPickupAndDropOff_noNotice() {
    GtfsStopTime stopTime =
        new GtfsStopTime.Builder()
            .setCsvRowNumber(2)
            .setTripId("t1")
            .setStopId("s1")
            .setStopSequence(3)
            .build();
    assertThat(generateNotices(stopTime)).isEmpty();
  }
}
