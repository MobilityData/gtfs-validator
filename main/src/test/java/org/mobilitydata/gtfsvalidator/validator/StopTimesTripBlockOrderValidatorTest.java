package org.mobilitydata.gtfsvalidator.validator;

import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

@RunWith(JUnit4.class)
public class StopTimesTripBlockOrderValidatorTest {
  public static GtfsStopTime createStopTime(
      int csvRowNumber, String tripId, String stopId, int stopSequence) {
    var builder =
        new GtfsStopTime.Builder()
            .setCsvRowNumber(csvRowNumber)
            .setTripId(tripId)
            .setStopSequence(stopSequence);
    if (stopId != null) {
      builder.setStopId(stopId);
    }
    return builder.build();
  }

  private static List<ValidationNotice> generateNotices(List<GtfsStopTime> stopTimes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new StopTimesTripBlockOrderValidator(
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void UnsortedStopTimesNotice_generateNotice_nonContiguousTripBlock() {
    // tripId 0914 appears, then 0915, then 0914 again: should trigger contiguity notice.
    var stopTimes =
        List.of(
            createStopTime(1, "0914", "S1", 1),
            createStopTime(2, "0914", "S2", 2),
            createStopTime(3, "0915", "S3", 1),
            createStopTime(4, "0914", "S4", 3));

    assertTrue(
        generateNotices(stopTimes)
            .contains(new StopTimesTripBlockOrderValidator.UnsortedStopTimesNotice("0914", 1, 4)));
  }

  @Test
  public void UnsortedStopTimesNotice_generateNotice_nonIncreasingStopSequence() {
    // tripId 0916 has stop_sequence 1, 3, 2 in file order: should trigger sequence notice.
    var stopTimes =
        List.of(
            createStopTime(1, "0916", "S1", 1),
            createStopTime(2, "0916", "S2", 3),
            createStopTime(3, "0916", "S3", 2));

    assertTrue(
        generateNotices(stopTimes)
            .contains(new StopTimesTripBlockOrderValidator.UnsortedStopTimesNotice("0916", 1, 3)));
  }
}
