package org.mobilitydata.gtfsvalidator.validator;

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
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableLoader;
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
    validateTripReferences(
        entity,
        GtfsTransferTableLoader.FROM_TRIP_ID_FIELD_NAME,
        optional(entity.hasFromTripId(), entity.fromTripId()),
        GtfsTransferTableLoader.FROM_ROUTE_ID_FIELD_NAME,
        optional(entity.hasFromRouteId(), entity.fromRouteId()),
        GtfsTransferTableLoader.FROM_STOP_ID_FIELD_NAME,
        optional(entity.hasFromStopId(), entity.fromStopId()),
        noticeContainer);
    validateTripReferences(
        entity,
        GtfsTransferTableLoader.TO_TRIP_ID_FIELD_NAME,
        optional(entity.hasToTripId(), entity.toTripId()),
        GtfsTransferTableLoader.TO_ROUTE_ID_FIELD_NAME,
        optional(entity.hasToRouteId(), entity.toRouteId()),
        GtfsTransferTableLoader.TO_STOP_ID_FIELD_NAME,
        optional(entity.hasToStopId(), entity.toStopId()),
        noticeContainer);
  }

  void validateTripReferences(
      GtfsTransfer entity,
      String tripFieldName,
      Optional<String> tripId,
      String routeFieldName,
      Optional<String> routeId,
      String stopFieldName,
      Optional<String> stopId,
      NoticeContainer noticeContainer) {
    if (tripId.isEmpty()) {
      return;
    }
    Optional<GtfsTrip> optTrip = tripsContainer.byTripId(tripId.get());
    if (optTrip.isEmpty()) {
      // The foreign key reference is validated elsewhere.
      return;
    }
    GtfsTrip trip = optTrip.get();
    if (routeId.isPresent()) {
      if (!trip.routeId().equals(routeId.get())) {
        noticeContainer.addValidationNotice(
            new TransferWithInvalidTripAndRouteNotice(
                entity.csvRowNumber(),
                tripFieldName,
                tripId.get(),
                routeFieldName,
                routeId.get(),
                trip.routeId()));
      }
    }
    if (stopId.isPresent()) {
      validateTripStopReference(
          entity, tripFieldName, tripId, stopFieldName, stopId.get(), noticeContainer);
    }
  }

  private void validateTripStopReference(
      GtfsTransfer entity,
      String tripFieldName,
      Optional<String> tripId,
      String stopFieldName,
      String stopId,
      NoticeContainer noticeContainer) {
    Optional<GtfsStop> optStop = stopsContainer.byStopId(stopId);
    if (optStop.isEmpty()) {
      // The foreign key reference is validated elsewhere.
      return;
    }
    ImmutableSet<GtfsStop> stops = expandStationIfNeeded(optStop.get());
    ImmutableSet<String> ids =
        stops.stream().map(GtfsStop::stopId).collect(ImmutableSet.toImmutableSet());

    List<GtfsStopTime> stopTimes = stopTimeContainer.byTripId(tripId.get());
    if (!stopTimes.stream().anyMatch((st) -> ids.contains(st.stopId()))) {
      noticeContainer.addValidationNotice(
          new TransferWithInvalidTripAndStopNotice(
              entity.csvRowNumber(), tripFieldName, tripId.get(), stopFieldName, stopId));
    }
  }

  private static <T> Optional<T> optional(boolean hasValue, T value) {
    return hasValue ? Optional.of(value) : Optional.empty();
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
    private final long csvRowNumber;
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
        long csvRowNumber,
        String tripFieldName,
        String tripId,
        String routeFieldName,
        String routeId,
        String expectedRouteId) {
      super(SeverityLevel.ERROR);
      this.csvRowNumber = csvRowNumber;
      this.tripFieldName = tripFieldName;
      this.tripId = tripId;
      this.routeFieldName = routeFieldName;
      this.routeId = routeId;
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
    private final long csvRowNumber;
    // The name of the trip id field (e.g. `from_trip_id`) referencing a trip.
    private final String tripFieldName;
    // The referenced trip id.
    private final String tripId;
    // The name of the stop id field (e.g. `stop_route_id`) referencing the stop.
    private final String stopFieldName;
    // The referenced stop id.
    private final String stopId;

    public TransferWithInvalidTripAndStopNotice(
        long csvRowNumber,
        String tripFieldName,
        String tripId,
        String stopFieldName,
        String stopId) {
      super(SeverityLevel.ERROR);
      this.csvRowNumber = csvRowNumber;
      this.tripFieldName = tripFieldName;
      this.tripId = tripId;
      this.stopFieldName = stopFieldName;
      this.stopId = stopId;
    }
  }
}
