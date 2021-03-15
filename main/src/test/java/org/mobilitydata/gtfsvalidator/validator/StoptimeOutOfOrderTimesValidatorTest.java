package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StopTimeWithDepartureBeforeArrivalTimeNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

public class StoptimeOutOfOrderTimesValidatorTest {

  private static GtfsStopTime createStopTime(
      long csvRowNumber,
      GtfsTime arrivalTime,
      GtfsTime departureTime) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId("first trip id")
        .setArrivalTime(arrivalTime)
        .setDepartureTime(departureTime)
        .setStopSequence(2)
        .setStopId("stop id")
        .build();
  }

  @Test
  public void departureTimeAfterArrivalTimeShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    StoptimeOutOfOrderTimesValidator underTest = new StoptimeOutOfOrderTimesValidator();

    underTest.validate(
        createStopTime(
            0,
            GtfsTime.fromSecondsSinceMidnight(340),
            GtfsTime.fromSecondsSinceMidnight(518)
        ),
        noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void departureTimeBeforeArrivalTimeShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    StoptimeOutOfOrderTimesValidator underTest = new StoptimeOutOfOrderTimesValidator();

    underTest.validate(
        createStopTime(
            1,
            GtfsTime.fromSecondsSinceMidnight(518),
            GtfsTime.fromSecondsSinceMidnight(340)
        ),
        noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new StopTimeWithDepartureBeforeArrivalTimeNotice(
                1,
                "first trip id",
                2,
                GtfsTime.fromSecondsSinceMidnight(340),
                GtfsTime.fromSecondsSinceMidnight(518)));
  }
}
