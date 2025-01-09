package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

import javax.inject.Inject;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

/**
 * Validates that if `routes.continuous_pickup` or `routes.continuous_drop_off` are included,
 * then `stop_times.start_pickup_drop_off_window` or `stop_times.end_pickup_drop_off_window`
 * are not defined for any trip of this route.
 *
 * <p>Generated notice: {@link RouteContinuousPickupDropOffNotice}.
 */
@GtfsValidator
public class RouteContinuousPickupDropOffValidator extends  FileValidator{
  private final GtfsRouteTableContainer routeTable;
  private final GtfsTripTableContainer tripTable;
  private final GtfsStopTimeTableContainer stopTimeTable;

  @Inject
  public RouteContinuousPickupDropOffValidator(GtfsRouteTableContainer routeTable, GtfsTripTableContainer tripTable, GtfsStopTimeTableContainer stopTimeTable) {
    this.routeTable = routeTable;
    this.tripTable = tripTable;
    this.stopTimeTable = stopTimeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsRoute route : routeTable.getEntities()) {
      boolean continuous = route.hasContinuousPickup() || route.hasContinuousDropOff();
      if (!continuous) {
        continue;
      }
      for (GtfsTrip trip : tripTable.byRouteId(route.routeId())) {
        for (GtfsStopTime stopTime : stopTimeTable.byTripId(trip.tripId())) {
          if (stopTime.hasStartPickupDropOffWindow() || stopTime.hasEndPickupDropOffWindow()) {
            noticeContainer.addValidationNotice(
                new RouteContinuousPickupDropOffNotice(
                    route.csvRowNumber(),
                    trip.tripId(),
                    stopTime.startPickupDropOffWindow(),
                    stopTime.endPickupDropOffWindow()));
          }
        }
      }
    }
  }

/**
 * Notice generated when `routes.continuous_pickup` or `routes.continuous_drop_off` are included
 * and `stop_times.start_pickup_drop_off_window` or `stop_times.end_pickup_drop_off_window`
 * are defined for any trip of this route.
 */
@GtfsValidationNotice(severity = ERROR)
public static class RouteContinuousPickupDropOffNotice extends ValidationNotice {
  // The row number of the route in the CSV file.
  private final int routeCsvRowNumber;

  // The ID of the trip.
  private final String tripId;

  // The start time of the pickup/drop-off window.
  private final GtfsTime startPickupDropOffWindow;

  // The end time of the pickup/drop-off window.
  private final GtfsTime endPickupDropOffWindow;

  public RouteContinuousPickupDropOffNotice(
          int routeCsvRowNumber,
          String tripId,
          GtfsTime startPickupDropOffWindow,
          GtfsTime endPickupDropOffWindow) {
      this.routeCsvRowNumber = routeCsvRowNumber;
      this.tripId = tripId;
      this.startPickupDropOffWindow = startPickupDropOffWindow;
      this.endPickupDropOffWindow = endPickupDropOffWindow;
    }
  }
}