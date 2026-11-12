package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.INFO;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsPickupDropOff;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeSchema;

/**
 * Validates that each stop time allows riders to board or alight.
 *
 * <p>Generated notice: {@link UnusableStopNotice}.
 */
@GtfsValidator
public class UnusableStopValidator extends SingleEntityValidator<GtfsStopTime> {

  @Override
  public void validate(GtfsStopTime stopTime, NoticeContainer noticeContainer) {
    if (stopTime.pickupType() == GtfsPickupDropOff.NOT_AVAILABLE
        && stopTime.dropOffType() == GtfsPickupDropOff.NOT_AVAILABLE) {
      noticeContainer.addValidationNotice(
          new UnusableStopNotice(
              stopTime.csvRowNumber(),
              stopTime.stopId(),
              stopTime.tripId(),
              stopTime.stopSequence()));
    }
  }

  /**
   * A stop time has neither pickup nor drop-off available.
   *
   * <p>Both `pickup_type` and `drop_off_type` are `1` (not available), so riders cannot board or
   * alight at this stop for this trip.
   */
  @GtfsValidationNotice(severity = INFO, files = @FileRefs(GtfsStopTimeSchema.class))
  static class UnusableStopNotice extends ValidationNotice {

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The `stop_times.stop_id` of the faulty record. */
    private final String stopId;

    /** The `stop_times.trip_id` of the faulty record. */
    private final String tripId;

    /** The `stop_times.stop_sequence` of the faulty record. */
    private final int stopSequence;

    UnusableStopNotice(int csvRowNumber, String stopId, String tripId, int stopSequence) {
      this.csvRowNumber = csvRowNumber;
      this.stopId = stopId;
      this.tripId = tripId;
      this.stopSequence = stopSequence;
    }
  }
}
