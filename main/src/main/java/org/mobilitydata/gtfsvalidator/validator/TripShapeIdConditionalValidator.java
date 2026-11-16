package org.mobilitydata.gtfsvalidator.validator;

import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsContinuousPickupDropOff;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

/**
 * Validates that {@code trips.shape_id} is defined when the trip has continuous pickup or drop-off
 * behavior.
 *
 * <p>The spec makes {@code trips.shape_id} conditionally required: it is required if the trip has a
 * continuous pickup or drop-off behavior defined either in {@code routes.txt} or in {@code
 * stop_times.txt}, and optional otherwise.
 *
 * <p>A value of {@code 1}, like an empty value, means no continuous stopping behavior, so only
 * {@code 0}, {@code 2} and {@code 3} make {@code shape_id} required. Values in {@code
 * stop_times.txt} override those in {@code routes.txt}.
 *
 * <p>Generated notice: {@link MissingRequiredFieldNotice}.
 */
@GtfsValidator
public class TripShapeIdConditionalValidator extends FileValidator {
  private final GtfsRouteTableContainer routeTable;
  private final GtfsTripTableContainer tripTable;
  private final GtfsStopTimeTableContainer stopTimeTable;

  @Inject
  TripShapeIdConditionalValidator(
      GtfsRouteTableContainer routeTable,
      GtfsTripTableContainer tripTable,
      GtfsStopTimeTableContainer stopTimeTable) {
    this.routeTable = routeTable;
    this.tripTable = tripTable;
    this.stopTimeTable = stopTimeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsTrip trip : tripTable.getEntities()) {
      if (trip.hasShapeId()) {
        continue;
      }
      if (hasContinuousBehavior(trip)) {
        noticeContainer.addValidationNotice(
            new MissingRequiredFieldNotice(
                GtfsTrip.FILENAME, trip.csvRowNumber(), GtfsTrip.SHAPE_ID_FIELD_NAME));
      }
    }
  }

  /**
   * Returns true if any stop time of this trip has continuous pickup or drop-off behavior, falling
   * back to the values on the route where the stop time does not set them.
   */
  private boolean hasContinuousBehavior(GtfsTrip trip) {
    Optional<GtfsRoute> route = routeTable.byRouteId(trip.routeId());
    GtfsContinuousPickupDropOff routePickup =
        route.map(GtfsRoute::continuousPickup).orElse(GtfsContinuousPickupDropOff.NOT_AVAILABLE);
    GtfsContinuousPickupDropOff routeDropOff =
        route.map(GtfsRoute::continuousDropOff).orElse(GtfsContinuousPickupDropOff.NOT_AVAILABLE);

    List<GtfsStopTime> stopTimes = stopTimeTable.byTripId(trip.tripId());
    if (stopTimes.isEmpty()) {
      return isContinuous(routePickup) || isContinuous(routeDropOff);
    }
    for (GtfsStopTime stopTime : stopTimes) {
      GtfsContinuousPickupDropOff pickup =
          stopTime.hasContinuousPickup() ? stopTime.continuousPickup() : routePickup;
      GtfsContinuousPickupDropOff dropOff =
          stopTime.hasContinuousDropOff() ? stopTime.continuousDropOff() : routeDropOff;
      if (isContinuous(pickup) || isContinuous(dropOff)) {
        return true;
      }
    }
    return false;
  }

  private static boolean isContinuous(GtfsContinuousPickupDropOff value) {
    return value == GtfsContinuousPickupDropOff.ALLOWED
        || value == GtfsContinuousPickupDropOff.MUST_PHONE
        || value == GtfsContinuousPickupDropOff.ON_REQUEST_TO_DRIVER;
  }

  @Override
  public boolean shouldCallValidate() {
    if (tripTable == null) {
      return false;
    }
    boolean routeDefinesContinuous =
        routeTable != null
            && (routeTable.hasColumn(GtfsRoute.CONTINUOUS_PICKUP_FIELD_NAME)
                || routeTable.hasColumn(GtfsRoute.CONTINUOUS_DROP_OFF_FIELD_NAME));
    boolean stopTimeDefinesContinuous =
        stopTimeTable != null
            && (stopTimeTable.hasColumn(GtfsStopTime.CONTINUOUS_PICKUP_FIELD_NAME)
                || stopTimeTable.hasColumn(GtfsStopTime.CONTINUOUS_DROP_OFF_FIELD_NAME));
    return routeDefinesContinuous || stopTimeDefinesContinuous;
  }
}
