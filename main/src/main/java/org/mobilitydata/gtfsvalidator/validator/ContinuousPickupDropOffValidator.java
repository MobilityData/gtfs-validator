package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * Validates that if `routes.continuous_pickup` or `routes.continuous_drop_off` are included, then
 * `stop_times.start_pickup_drop_off_window` or `stop_times.end_pickup_drop_off_window` are not
 * defined for any trip of this route.
 *
 * <p>Generated notice: {@link ForbiddenContinuousPickupDropOffNotice}.
 */
@GtfsValidator
public class ContinuousPickupDropOffValidator extends FileValidator {
  private final GtfsRouteTableContainer routeTable;
  private final GtfsTripTableContainer tripTable;
  private final GtfsStopTimeTableContainer stopTimeTable;

  @Inject
  public ContinuousPickupDropOffValidator(
      GtfsRouteTableContainer routeTable,
      GtfsTripTableContainer tripTable,
      GtfsStopTimeTableContainer stopTimeTable) {
    this.routeTable = routeTable;
    this.tripTable = tripTable;
    this.stopTimeTable = stopTimeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsRoute route : routeTable.getEntities()) {
      boolean continuous =
          (route.continuousPickup() == GtfsContinuousPickupDropOff.ALLOWED
                  || route.continuousPickup() == GtfsContinuousPickupDropOff.MUST_PHONE
                  || route.continuousPickup() == GtfsContinuousPickupDropOff.ON_REQUEST_TO_DRIVER)
              || (route.continuousDropOff() == GtfsContinuousPickupDropOff.ALLOWED
                  || route.continuousDropOff() == GtfsContinuousPickupDropOff.MUST_PHONE
                  || route.continuousDropOff() == GtfsContinuousPickupDropOff.ON_REQUEST_TO_DRIVER);
      if (!continuous) {
        continue;
      }
      for (GtfsTrip trip : tripTable.byRouteId(route.routeId())) {
        for (GtfsStopTime stopTime : stopTimeTable.byTripId(trip.tripId())) {
          if (stopTime.hasStartPickupDropOffWindow() || stopTime.hasEndPickupDropOffWindow()) {
            noticeContainer.addValidationNotice(
                new ForbiddenContinuousPickupDropOffNotice(
                    route.csvRowNumber(),
                    trip.tripId(),
                    stopTime.startPickupDropOffWindow(),
                    stopTime.endPickupDropOffWindow()));
          }
        }
      }
    }
  }

  @Override
  public boolean shouldCallValidate() {
    if (routeTable != null && stopTimeTable != null) {
      return routeTable.hasColumn(GtfsRoute.CONTINUOUS_PICKUP_FIELD_NAME)
          || routeTable.hasColumn(GtfsRoute.CONTINUOUS_DROP_OFF_FIELD_NAME)
              && (stopTimeTable.hasColumn(GtfsStopTime.START_PICKUP_DROP_OFF_WINDOW_FIELD_NAME)
                  || stopTimeTable.hasColumn(GtfsStopTime.END_PICKUP_DROP_OFF_WINDOW_FIELD_NAME));
    } else {
      return false;
    }
  }

  /**
   * Continuous pickup or drop-off are forbidden when routes.continuous_pickup or
   * routes.continuous_drop_off are 0, 2 or 3 and stop_times.start_pickup_drop_off_window or
   * stop_times.end_pickup_drop_off_window are defined for any trip of this route.
   */
  @GtfsValidationNotice(severity = ERROR)
  public static class ForbiddenContinuousPickupDropOffNotice extends ValidationNotice {
    /** The row number of the route in the CSV file. */
    private final int routeCsvRowNumber;

    /** The ID of the trip. */
    private final String tripId;

    /** The start time of the pickup/drop-off window. */
    private final GtfsTime startPickupDropOffWindow;

    /** The end time of the pickup/drop-off window. */
    private final GtfsTime endPickupDropOffWindow;

    public ForbiddenContinuousPickupDropOffNotice(
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
