package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.validator.TooManyConsecutiveStopTimesWithSameTimeValidator.TooManyConsecutiveStopTimesWithSameTimeNotice;

public class TooManyConsecutiveStopTimesWithSameTimeValidatorTest {

  private static final String TRIP_ID = "trip";

  private static GtfsStopTime createStopTime(
      int csvRowNumber, int stopSequence, Integer arrivalSeconds, Integer departureSeconds) {
    GtfsStopTime.Builder builder =
        new GtfsStopTime.Builder()
            .setCsvRowNumber(csvRowNumber)
            .setTripId(TRIP_ID)
            .setStopSequence(stopSequence)
            .setStopId("stop-" + stopSequence);

    if (arrivalSeconds != null) {
      builder.setArrivalTime(GtfsTime.fromSecondsSinceMidnight(arrivalSeconds));
    }
    if (departureSeconds != null) {
      builder.setDepartureTime(GtfsTime.fromSecondsSinceMidnight(departureSeconds));
    }

    return builder.build();
  }

  private static List<ValidationNotice> generateNotices(GtfsStopTime... stopTimes) {
    NoticeContainer noticeContainer = new NoticeContainer();

    new TooManyConsecutiveStopTimesWithSameTimeValidator(
            GtfsStopTimeTableContainer.forEntities(Arrays.asList(stopTimes), noticeContainer))
        .validate(noticeContainer);

    return noticeContainer.getValidationNotices();
  }

  @Test
  public void fiveConsecutiveSameTimesShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                createStopTime(2, 1, 3600, 3600),
                createStopTime(3, 2, 3600, 3600),
                createStopTime(4, 3, 3600, 3600),
                createStopTime(5, 4, 3600, 3600),
                createStopTime(6, 5, 3600, 3600)))
        .isEmpty();
  }

  @Test
  public void sixConsecutiveSameTimesShouldGenerateNotice() {
    assertThat(
            generateNotices(
                createStopTime(2, 1, 3600, 3600),
                createStopTime(3, 2, 3600, 3600),
                createStopTime(4, 3, 3600, 3600),
                createStopTime(5, 4, 3600, 3600),
                createStopTime(6, 5, 3600, 3600),
                createStopTime(7, 6, 3600, 3600)))
        .containsExactly(
            new TooManyConsecutiveStopTimesWithSameTimeNotice(
                TRIP_ID, 6, GtfsTime.fromSecondsSinceMidnight(3600)));
  }

  @Test
  public void changedTimeShouldEndPreviousRun() {
    assertThat(
            generateNotices(
                createStopTime(2, 1, 3600, 3600),
                createStopTime(3, 2, 3600, 3600),
                createStopTime(4, 3, 3600, 3600),
                createStopTime(5, 4, 3600, 3600),
                createStopTime(6, 5, 3600, 3600),
                createStopTime(7, 6, 3600, 3600),
                createStopTime(8, 7, 3660, 3660)))
        .containsExactly(
            new TooManyConsecutiveStopTimesWithSameTimeNotice(
                TRIP_ID, 6, GtfsTime.fromSecondsSinceMidnight(3600)));
  }

  @Test
  public void qualifyingRunAtEndOfTripShouldGenerateNotice() {
    assertThat(
            generateNotices(
                createStopTime(2, 1, 3500, 3500),
                createStopTime(3, 2, 3600, 3600),
                createStopTime(4, 3, 3600, 3600),
                createStopTime(5, 4, 3600, 3600),
                createStopTime(6, 5, 3600, 3600),
                createStopTime(7, 6, 3600, 3600),
                createStopTime(8, 7, 3600, 3600)))
        .containsExactly(
            new TooManyConsecutiveStopTimesWithSameTimeNotice(
                TRIP_ID, 6, GtfsTime.fromSecondsSinceMidnight(3600)));
  }

  @Test
  public void missingTimesWithinPotentialRunShouldCountWhenLaterTimeConfirmsRun() {
    assertThat(
            generateNotices(
                createStopTime(2, 1, 3600, 3600),
                createStopTime(3, 2, 3600, 3600),
                createStopTime(4, 3, null, null),
                createStopTime(5, 4, null, null),
                createStopTime(6, 5, null, null),
                createStopTime(7, 6, 3600, 3600)))
        .containsExactly(
            new TooManyConsecutiveStopTimesWithSameTimeNotice(
                TRIP_ID, 6, GtfsTime.fromSecondsSinceMidnight(3600)));
  }

  @Test
  public void twoQualifyingRunsShouldGenerateTwoNotices() {
    assertThat(
            generateNotices(
                createStopTime(2, 1, 3600, 3600),
                createStopTime(3, 2, 3600, 3600),
                createStopTime(4, 3, 3600, 3600),
                createStopTime(5, 4, 3600, 3600),
                createStopTime(6, 5, 3600, 3600),
                createStopTime(7, 6, 3600, 3600),
                createStopTime(8, 7, 3660, 3660),
                createStopTime(9, 8, 3660, 3660),
                createStopTime(10, 9, 3660, 3660),
                createStopTime(11, 10, 3660, 3660),
                createStopTime(12, 11, 3660, 3660),
                createStopTime(13, 12, 3660, 3660)))
        .containsExactly(
            new TooManyConsecutiveStopTimesWithSameTimeNotice(
                TRIP_ID, 6, GtfsTime.fromSecondsSinceMidnight(3600)),
            new TooManyConsecutiveStopTimesWithSameTimeNotice(
                TRIP_ID, 6, GtfsTime.fromSecondsSinceMidnight(3660)));
  }

  @Test
  public void separateTripsShouldBeEvaluatedIndependently() {
    NoticeContainer noticeContainer = new NoticeContainer();

    List<GtfsStopTime> stopTimes =
        ImmutableList.of(
            new GtfsStopTime.Builder()
                .setCsvRowNumber(2)
                .setTripId("trip-a")
                .setStopSequence(1)
                .setArrivalTime(GtfsTime.fromSecondsSinceMidnight(3600))
                .setDepartureTime(GtfsTime.fromSecondsSinceMidnight(3600))
                .build(),
            new GtfsStopTime.Builder()
                .setCsvRowNumber(3)
                .setTripId("trip-b")
                .setStopSequence(1)
                .setArrivalTime(GtfsTime.fromSecondsSinceMidnight(3600))
                .setDepartureTime(GtfsTime.fromSecondsSinceMidnight(3600))
                .build());

    new TooManyConsecutiveStopTimesWithSameTimeValidator(
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer))
        .validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }
}
