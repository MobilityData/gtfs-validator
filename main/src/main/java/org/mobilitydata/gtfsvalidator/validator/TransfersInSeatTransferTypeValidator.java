package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;
import static org.mobilitydata.gtfsvalidator.validator.ValidatorReference.validatedElsewhereBy;

import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferType;
import org.mobilitydata.gtfsvalidator.validator.TransfersStopTypeValidator.TransferWithInvalidStopLocationTypeNotice;

/**
 * Validates that entries in `transfers.txt` with an in-seat transfer type are properly specified.
 *
 * @see TransfersStopTypeValidator
 * @see TransfersTripReferenceValidator
 */
@GtfsValidator
public class TransfersInSeatTransferTypeValidator extends FileValidator {

  private final GtfsTransferTableContainer transfers;

  private final GtfsStopTableContainer stops;

  private final GtfsStopTimeTableContainer stopTimes;

  @Inject
  public TransfersInSeatTransferTypeValidator(
      GtfsTransferTableContainer transfers,
      GtfsStopTableContainer stops,
      GtfsStopTimeTableContainer stopTimes) {
    this.transfers = transfers;
    this.stops = stops;
    this.stopTimes = stopTimes;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsTransfer transfer : transfers.getEntities()) {
      validateEntity(transfer, noticeContainer);
    }
  }

  public void validateEntity(GtfsTransfer transfer, NoticeContainer noticeContainer) {
    if (!transfer.hasTransferType()) {
      return;
    }
    if (!isInSeatTransferType(transfer.transferType())) {
      return;
    }
    for (TransferDirection transferDirection : TransferDirection.values()) {
      // Trip IDs are required for in-seat transfer types.
      if (!transferDirection.hasTripId(transfer)) {
        noticeContainer.addValidationNotice(
            new MissingRequiredFieldNotice(
                GtfsTransfer.FILENAME,
                transfer.csvRowNumber(),
                transferDirection.tripIdFieldName()));
      }
      validateStop(transfer, transferDirection, noticeContainer);
    }
  }

  private boolean isInSeatTransferType(GtfsTransferType transferType) {
    switch (transferType) {
      case IN_SEAT_TRANSFER_ALLOWED:
      case IN_SEAT_TRANSFER_NOT_ALLOWED:
        return true;
      default:
        return false;
    }
  }

  private void validateStop(
      GtfsTransfer transfer, TransferDirection transferDirection, NoticeContainer noticeContainer) {
    String stopId = transferDirection.stopId(transfer);
    Optional<GtfsStop> optStop = stops.byStopId(stopId);
    if (optStop.isEmpty()) {
      // This foreign key reference is
      validatedElsewhereBy(
          GtfsTransferFromStopIdForeignKeyValidator.class,
          GtfsTransferToStopIdForeignKeyValidator.class);
      return;
    }
    // Per the spec, normally a stop or station location type is required for a transfer entry.
    // However, for in-seat transfers, stations are specifically forbidden.
    GtfsLocationType locationType = optStop.get().locationType();
    if (locationType == GtfsLocationType.STATION) {
      noticeContainer.addValidationNotice(
          new TransferWithInvalidStopLocationTypeNotice(transfer, transferDirection, locationType));
    }
    List<GtfsStopTime> stopTimesForTrip = stopTimes.byTripId(transferDirection.tripId(transfer));
    if (stopTimesForTrip.isEmpty()
        || !stopTimesForTrip.stream().anyMatch((st) -> st.stopId().equals(stopId))) {
      // Requiring that a transfer trip's stop-times reference the transfer stop is
      validatedElsewhereBy(TransfersTripReferenceValidator.class);
      return;
    }
    GtfsStopTime transferStop = getInSeatTransferStopTime(stopTimesForTrip, transferDirection);
    if (!transferStop.stopId().equals(stopId)) {
      noticeContainer.addValidationNotice(
          new TransferWithSuspiciousMidTripInSeatNotice(transfer, transferDirection));
    }
  }

  private GtfsStopTime getInSeatTransferStopTime(
      List<GtfsStopTime> stopTimesForTrip, TransferDirection transferDirection) {
    switch (transferDirection) {
      case TRANSFER_FROM:
        return stopTimesForTrip.get(stopTimesForTrip.size() - 1);
      case TRANSFER_TO:
        return stopTimesForTrip.get(0);
      default:
        throw new UnsupportedOperationException("Unhandled TransferDirection=" + transferDirection);
    }
  }

  /**
   * A `from_trip_id` or `to_trip_id` field from GTFS file `transfers.txt` with an in-seat transfer
   * type references a stop that is not in the expected position in the trip's stop-times.
   *
   * <p>For in-seat transfers, we expect the stop to be the last stop-time in the trip sequence for
   * `from_stop_id` and the first stop-time for `to_stop_id`. If you are intentionally using this
   * feature to model mid-trip transfers, you can ignore this warning, but be aware that this
   * functionality is still considered to be partially experimental in some interpretations of the
   * spec.
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  @GtfsValidationNotice(severity = WARNING)
  public static class TransferWithSuspiciousMidTripInSeatNotice extends ValidationNotice {

    /** The row number from `transfers.txt` for the faulty entry. */
    private final int csvRowNumber;

    /** The name of the trip id field (e.g. `from_trip_id`) referencing a trip. */
    private final String tripIdFieldName;

    /** The referenced trip id. */
    private final String tripId;

    /** The name of the stop id field (e.g. `from_stop_id`) referencing the stop. */
    private final String stopIdFieldName;

    /** The referenced stop id. */
    private final String stopId;

    public TransferWithSuspiciousMidTripInSeatNotice(
        GtfsTransfer transfer, TransferDirection transferDirection) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = transfer.csvRowNumber();
      this.tripIdFieldName = transferDirection.tripIdFieldName();
      this.tripId = transferDirection.tripId(transfer);
      this.stopIdFieldName = transferDirection.stopIdFieldName();
      this.stopId = transferDirection.stopId(transfer);
    }
  }
}
