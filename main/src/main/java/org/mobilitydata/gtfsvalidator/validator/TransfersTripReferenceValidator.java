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
            new TransferTripReferenceNotice(
                entity.csvRowNumber(), tripFieldName, tripId.get(), routeFieldName, routeId.get()));
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
          new TransferTripReferenceNotice(
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

  public static class TransferTripReferenceNotice extends ValidationNotice {
    private final long csvRowNumber;
    private final String tripFieldName;
    private final String tripFieldValue;
    private final String referenceFieldName;
    private final String referenceFieldValue;

    public TransferTripReferenceNotice(
        long csvRowNumber,
        String tripFieldName,
        String tripFieldValue,
        String referenceFieldName,
        String referenceFieldValue) {
      super(SeverityLevel.ERROR);
      this.csvRowNumber = csvRowNumber;
      this.tripFieldName = tripFieldName;
      this.tripFieldValue = tripFieldValue;
      this.referenceFieldName = referenceFieldName;
      this.referenceFieldValue = referenceFieldValue;
    }
  }
}
