package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsPickupDropOff;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@RunWith(JUnit4.class)
public class StopTimesRecordValidatorTest {

  public static GtfsStopTime createStopTime(
      int csvRowNumber,
      String tripId,
      String locationGroupId,
      String locationId,
      GtfsPickupDropOff pickupType,
      GtfsPickupDropOff dropOffType,
      GtfsTime startWindow,
      GtfsTime endWindow) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId(tripId)
        .setLocationGroupId(locationGroupId)
        .setLocationId(locationId)
        .setPickupType(pickupType)
        .setDropOffType(dropOffType)
        .setStartPickupDropOffWindow(startWindow)
        .setEndPickupDropOffWindow(endWindow)
        .build();
  }

  private static List<ValidationNotice> generateNotices(List<GtfsStopTime> stopTimes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsStopTimeTableContainer stopTimeTable =
        GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);
    new StopTimesRecordValidator(stopTimeTable).validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void containsStopTimesRecordShouldNotGenerateNotice1() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStopTime(
                        1,
                        "trip1",
                        "locationGroupId1",
                        "locationId1",
                        GtfsPickupDropOff.ALLOWED,
                        GtfsPickupDropOff.ALLOWED,
                        GtfsTime.fromString("08:00:00"),
                        GtfsTime.fromString("09:00:00")))))
        .isEmpty();
  }

  @Test
  public void missingStopTimesRecordShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStopTime(
                        1,
                        "trip2",
                        "locationGroupId1",
                        "locationId1",
                        GtfsPickupDropOff.MUST_PHONE,
                        GtfsPickupDropOff.MUST_PHONE,
                        GtfsTime.fromString("08:00:00"),
                        GtfsTime.fromString("09:00:00")))))
        .containsExactly(
            new StopTimesRecordValidator.MissingStopTimesRecordNotice(
                1, "trip2", "locationGroupId1", "locationId1"));
  }
}
