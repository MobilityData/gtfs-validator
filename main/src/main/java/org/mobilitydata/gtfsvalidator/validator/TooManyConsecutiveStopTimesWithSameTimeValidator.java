package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.INFO;

import com.google.common.collect.Multimaps;
import java.util.List;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * Reports trips with more than five consecutive stop times sharing the same arrival/departure time.
 *
 * <p>This reproduces the historical Google Python validator rule. Stop-time rows with a missing
 * arrival or departure time may lie within a potential same-time run and contribute to the reported
 * run length if a later fully specified row confirms the same time.
 */
@GtfsValidator
public class TooManyConsecutiveStopTimesWithSameTimeValidator extends FileValidator {

  private static final int MAX_CONSECUTIVE_SAME_TIME_STOP_TIMES = 5;

  private final GtfsStopTimeTableContainer stopTimeTable;

  @Inject
  TooManyConsecutiveStopTimesWithSameTimeValidator(GtfsStopTimeTableContainer stopTimeTable) {
    this.stopTimeTable = stopTimeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (List<GtfsStopTime> stopTimes : Multimaps.asMap(stopTimeTable.byTripIdMap()).values()) {
      validateTrip(stopTimes, noticeContainer);
    }
  }

  private static void validateTrip(List<GtfsStopTime> stopTimes, NoticeContainer noticeContainer) {
    GtfsTime previousDepartureTime = null;
    int potentiallySameTimeCount = 0;
    int fullySpecifiedSameTimeCount = 0;
    String tripId = null;

    for (GtfsStopTime stopTime : stopTimes) {
      tripId = stopTime.tripId();

      if (!stopTime.hasArrivalTime() || !stopTime.hasDepartureTime()) {
        potentiallySameTimeCount++;
        continue;
      }

      if (previousDepartureTime != null
          && previousDepartureTime.equals(stopTime.arrivalTime())
          && stopTime.arrivalTime().equals(stopTime.departureTime())) {
        potentiallySameTimeCount++;
        fullySpecifiedSameTimeCount = potentiallySameTimeCount;
      } else {
        addNoticeIfNeeded(
            tripId, fullySpecifiedSameTimeCount, previousDepartureTime, noticeContainer);

        potentiallySameTimeCount = 1;
        fullySpecifiedSameTimeCount = 1;
      }

      previousDepartureTime = stopTime.departureTime();
    }

    addNoticeIfNeeded(tripId, fullySpecifiedSameTimeCount, previousDepartureTime, noticeContainer);
  }

  private static void addNoticeIfNeeded(
      String tripId, int consecutiveStopTimeCount, GtfsTime time, NoticeContainer noticeContainer) {
    if (time != null && consecutiveStopTimeCount > MAX_CONSECUTIVE_SAME_TIME_STOP_TIMES) {
      noticeContainer.addValidationNotice(
          new TooManyConsecutiveStopTimesWithSameTimeNotice(
              tripId, consecutiveStopTimeCount, time));
    }
  }

  /**
   * More than five consecutive stop-time records have the same fully specified time.
   *
   * <p>This is a community rule inherited from the historical Google Python validator rather than a
   * GTFS specification requirement.
   */
  @GtfsValidationNotice(severity = INFO, files = @FileRefs(GtfsStopTimeSchema.class))
  static class TooManyConsecutiveStopTimesWithSameTimeNotice extends ValidationNotice {

    /** The affected trip_id. */
    private final String tripId;

    /** Number of consecutive stop-time records in the run. */
    private final int entityCount;

    /** Shared arrival/departure time for the fully specified records in the run. */
    private final GtfsTime time;

    TooManyConsecutiveStopTimesWithSameTimeNotice(String tripId, int entityCount, GtfsTime time) {
      this.tripId = tripId;
      this.entityCount = entityCount;
      this.time = time;
    }
  }
}
