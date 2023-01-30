package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.validator.ValidatorReference.validatedElsewhereBy;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
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
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

/**
 * Validates that if a transfers.txt entry references a trip, then any corresponding route reference
 * or stop reference for the transfer are actually associated with the trip.
 */
@GtfsValidator
public class TransfersTripReferenceValidator extends FileValidator {

  private final GtfsTransferTableContainer transfersContainer;
  private final GtfsTripTableContainer tripsContainer;
  private final GtfsStopTimeTableContainer stopTimeContainer;
  private final GtfsStopTableContainer stopsContainer;

  @Inject
  public TransfersTripReferenceValidator(
      GtfsTransferTableContainer transfersContainer,
      GtfsTripTableContainer tripsContainer,
      GtfsStopTimeTableContainer stopTimeContainer,
      GtfsStopTableContainer stopsContainer) {
    this.transfersContainer = transfersContainer;
    this.tripsContainer = tripsContainer;
    this.stopTimeContainer = stopTimeContainer;
    this.stopsContainer = stopsContainer;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsTransfer transfer : transfersContainer.getEntities()) {
      validateEntity(transfer, noticeContainer);
    }
  }

  public void validateEntity(GtfsTransfer entity, NoticeContainer noticeContainer) {
    for (TransferDirection transferDirection : TransferDirection.values()) {
      validateTripReferences(entity, transferDirection, noticeContainer);
    }
  }

  void validateTripReferences(
      GtfsTransfer entity, TransferDirection transferDirection, NoticeContainer noticeContainer) {
    if (!transferDirection.hasTripId(entity)) {
      return;
    }
    Optional<GtfsTrip> optTrip = tripsContainer.byTripId(transferDirection.tripId(entity));
    if (optTrip.isEmpty()) {
      // The foreign key reference is
      validatedElsewhereBy(
          GtfsTransferFromTripIdForeignKeyValidator.class,
          GtfsTransferToTripIdForeignKeyValidator.class);
      return;
    }
    GtfsTrip trip = optTrip.get();
    if (transferDirection.hasRouteId(entity)) {
      if (!trip.routeId().equals(transferDirection.routeId(entity))) {
        noticeContainer.addValidationNotice(
            new TransferWithInvalidTripAndRouteNotice(entity, transferDirection, trip.routeId()));
      }
    }
    if (transferDirection.hasStopId(entity)) {
      validateTripStopReference(entity, transferDirection, noticeContainer);
    }
  }

  private void validateTripStopReference(
      GtfsTransfer entity, TransferDirection transferDirection, NoticeContainer noticeContainer) {
    Optional<GtfsStop> optStop = stopsContainer.byStopId(transferDirection.stopId(entity));
    if (optStop.isEmpty()) {
      // The foreign key reference is
      validatedElsewhereBy(
          GtfsTransferFromStopIdForeignKeyValidator.class,
          GtfsTransferToStopIdForeignKeyValidator.class);
      return;
    }
    ImmutableSet<GtfsStop> stops = expandStationIfNeeded(optStop.get());
    ImmutableSet<String> ids =
        stops.stream().map(GtfsStop::stopId).collect(ImmutableSet.toImmutableSet());

    List<GtfsStopTime> stopTimes = stopTimeContainer.byTripId(transferDirection.tripId(entity));
    if (!stopTimes.stream().anyMatch((st) -> ids.contains(st.stopId()))) {
      noticeContainer.addValidationNotice(
          new TransferWithInvalidTripAndStopNotice(entity, transferDirection));
    }
  }

  private ImmutableSet<GtfsStop> expandStationIfNeeded(GtfsStop stop) {
    if (stop.locationType() == GtfsLocationType.STOP) {
      return ImmutableSet.of(stop);
    } else if (stop.locationType() == GtfsLocationType.STATION) {
      List<GtfsStop> stops = stopsContainer.byParentStation(stop.stopId());
      return ImmutableSet.copyOf(stops);
    } else {
      // Invalid stop location types are validated elsewhere.
      return ImmutableSet.of();
    }
  }

  /**
   * A `from_trip_id` or `to_trip_id` field from GTFS file `transfers.txt` references a route that
   * does not match its `trips.txt` `route_id`.
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  public static class TransferWithInvalidTripAndRouteNotice extends ValidationNotice {
    // The row number from `transfers.txt` for the faulty entry.
    private final int csvRowNumber;
    // The name of the trip id field (e.g. `from_trip_id`) referencing a trip.
    private final String tripFieldName;
    // The referenced trip id.
    private final String tripId;
    // The name of the route id field (e.g. `from_route_id`) referencing the route.
    private final String routeFieldName;
    // The referenced route id.
    private final String routeId;
    // The expected route id from `trips.txt`.
    private final String expectedRouteId;

    public TransferWithInvalidTripAndRouteNotice(
        GtfsTransfer transfer, TransferDirection transferDirection, String expectedRouteId) {
      super(SeverityLevel.ERROR);
      this.csvRowNumber = transfer.csvRowNumber();
      this.tripFieldName = transferDirection.tripIdFieldName();
      this.tripId = transferDirection.tripId(transfer);
      this.routeFieldName = transferDirection.routeIdFieldName();
      this.routeId = transferDirection.routeId(transfer);
      this.expectedRouteId = expectedRouteId;
    }
  }

  /**
   * A `from_trip_id` or `to_trip_id` field from GTFS file `transfers.txt` references a stop that is
   * not included in the referenced trip's stop-times.
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  public static class TransferWithInvalidTripAndStopNotice extends ValidationNotice {
    // The row number from `transfers.txt` for the faulty entry.
    private final int csvRowNumber;
    // The name of the trip id field (e.g. `from_trip_id`) referencing a trip.
    private final String tripFieldName;
    // The referenced trip id.
    private final String tripId;
    // The name of the stop id field (e.g. `stop_route_id`) referencing the stop.
    private final String stopFieldName;
    // The referenced stop id.
    private final String stopId;

    public TransferWithInvalidTripAndStopNotice(
        GtfsTransfer transfer, TransferDirection transferDirection) {
      super(SeverityLevel.ERROR);
      this.csvRowNumber = transfer.csvRowNumber();
      this.tripFieldName = transferDirection.tripIdFieldName();
      this.tripId = transferDirection.tripId(transfer);
      this.stopFieldName = transferDirection.stopIdFieldName();
      this.stopId = transferDirection.stopId(transfer);
    }
  }
}
