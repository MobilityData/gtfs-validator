package org.mobilitydata.gtfsvalidator.reportsummary.model;

import java.time.LocalDate;
import java.util.Optional;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.util.ServiceInterval;
import org.mobilitydata.gtfsvalidator.util.ServiceIntervalCache;

record ServiceWindow(LocalDate startDate, LocalDate endDate) {

  /**
   * Given some calendars and/or calendar dates, get the service window.
   *
   * <p>Uses {@link ServiceIntervalCache} to resolve each service's active date range, then returns
   * the earliest start and latest end across all services that have at least one active trip.
   *
   * @return The service window if there's at least one date on which service is available, and
   *     empty otherwise.
   */
  static Optional<ServiceWindow> get(
      GtfsTripTableContainer tripTable,
      Optional<GtfsCalendarTableContainer> calendarTable,
      Optional<GtfsCalendarDateTableContainer> calendarDateTable) {

    ServiceIntervalCache cache = new ServiceIntervalCache();

    LocalDate startDate = null;
    LocalDate endDate = null;

    for (String serviceId : tripTable.byServiceIdMap().keys()) {
      ServiceInterval interval =
          cache.getIntervals(serviceId, calendarTable.orElse(null), calendarDateTable.orElse(null));

      if (interval == null || interval.isEmpty()) {
        continue;
      }

      LocalDate first = interval.firstActiveDate();
      LocalDate last = interval.lastActiveDate();

      if (startDate == null || first.isBefore(startDate)) {
        startDate = first;
      }
      if (endDate == null || last.isAfter(endDate)) {
        endDate = last;
      }
    }

    if (startDate == null) {
      return Optional.empty();
    }

    return Optional.of(new ServiceWindow(startDate, endDate));
  }
}
