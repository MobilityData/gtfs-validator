package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsPickupDropOff;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;

/**
 * Validates `stop_times.start_pickup_dropoff_window`, `stop_times.end_pickup_dropoff_window`,
 * `stop_times.pickup_type`, and `stop_times.drop_off_type` for a single `GtfsStopTime`.
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link MissingStopTimesRecordNotice}
 * </ul>
 */
@GtfsValidator
public class StopTimesRecordValidator extends FileValidator {
  private final GtfsStopTimeTableContainer stopTimeTable;

  public StopTimesRecordValidator(GtfsStopTimeTableContainer stopTimeTable) {
    this.stopTimeTable = stopTimeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsStopTime entity : stopTimeTable.getEntities()) {
      if (entity.hasStartPickupDropOffWindow()
          && entity.hasEndPickupDropOffWindow()
          && entity.pickupType() == GtfsPickupDropOff.MUST_PHONE
          && entity.dropOffType() == GtfsPickupDropOff.MUST_PHONE) {
        int tripStopCount = stopTimeTable.byTripId(entity.tripId()).size();
        if (tripStopCount == 1) {
          noticeContainer.addValidationNotice(
              new MissingStopTimesRecordNotice(entity.csvRowNumber(), entity.tripId()));
        }
      }
    }
  }

  /**
   * Only 1 stop_times.txt record for the associated trip when
   * `stop_times.start_pickup_dropoff_window` and `stop_times.end_pickup_dropoff_window` are
   * defined, and`stop_times.pickup_type` and `stop_times.drop_off_type` are both set to `2`
   * (MUST_PHONE), and there is only one record in `stop_times.txt` for the associated trip.
   *
   * <p>Fields:
   *
   * <ul>
   *   <li>csvRowNumber: The row number of the faulty record in `stop_times.txt`.
   *   <li>tripId: The ID of the trip associated with the faulty record.
   * </ul>
   */
  @GtfsValidationNotice(severity = ERROR)
  static class MissingStopTimesRecordNotice extends ValidationNotice {
    /** The row of the faulty record. */
    private final long csvRowNumber;

    /** The `tripId` of the faulty record. */
    private final String tripId;

    public MissingStopTimesRecordNotice(long csvRowNumber, String tripId) {
      this.csvRowNumber = csvRowNumber;
      this.tripId = tripId;
    }
  }
}
