package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;

/**
 * An enum type, along with various convenience methods, for identifying the direction of transfer
 * in a `transfers.txt` entry and accessing associated fields.
 */
public enum TransferDirection {
  /**
   * The source of the transfer, including fields `from_stop_id`, `from_route_id`, and
   * `from_trip_id`.
   */
  TRANSFER_FROM,
  /**
   * The destination of the transfer, including fields `to_stop_id`, `to_route_id`, and
   * `to_trip_id`.
   */
  TRANSFER_TO;

  public String stopIdFieldName() {
    return isFrom() ? GtfsTransfer.FROM_STOP_ID_FIELD_NAME : GtfsTransfer.TO_STOP_ID_FIELD_NAME;
  }

  public String stopId(GtfsTransfer transfer) {
    return isFrom() ? transfer.fromStopId() : transfer.toStopId();
  }

  public boolean hasStopId(GtfsTransfer transfer) {
    return isFrom() ? transfer.hasFromStopId() : transfer.hasToStopId();
  }

  public String routeIdFieldName() {
    return isFrom() ? GtfsTransfer.FROM_ROUTE_ID_FIELD_NAME : GtfsTransfer.TO_ROUTE_ID_FIELD_NAME;
  }

  public boolean hasRouteId(GtfsTransfer transfer) {
    return isFrom() ? transfer.hasFromRouteId() : transfer.hasToRouteId();
  }

  public String routeId(GtfsTransfer transfer) {
    return isFrom() ? transfer.fromRouteId() : transfer.toRouteId();
  }

  public String tripIdFieldName() {
    return isFrom() ? GtfsTransfer.FROM_TRIP_ID_FIELD_NAME : GtfsTransfer.TO_TRIP_ID_FIELD_NAME;
  }

  public boolean hasTripId(GtfsTransfer transfer) {
    return isFrom() ? transfer.hasFromTripId() : transfer.hasToTripId();
  }

  public String tripId(GtfsTransfer transfer) {
    return isFrom() ? transfer.fromTripId() : transfer.toTripId();
  }

  private boolean isFrom() {
    return this == TRANSFER_FROM;
  }
}
