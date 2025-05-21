package org.mobilitydata.gtfsvalidator.reportsummary.model;

import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.util.*;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDate;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateExceptionType;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.util.SetUtil;

record ServiceWindow(LocalDate startDate, LocalDate endDate) {
  static Optional<ServiceWindow> fromCalendars(
      List<GtfsTrip> trips, List<GtfsCalendar> allCalendars) {
    Set<String> serviceIds = new HashSet<>(trips.stream().map(GtfsTrip::serviceId).toList());

    List<GtfsCalendar> calendars =
        allCalendars.stream()
            .filter(calendar -> serviceIds.contains(calendar.serviceId()))
            .toList();

    // Only empty if there are no calendars.
    Optional<LocalDate> startDate =
        calendars.stream().map(c -> c.startDate().getLocalDate()).min(LocalDate::compareTo);
    Optional<LocalDate> endDate =
        calendars.stream().map(c -> c.endDate().getLocalDate()).max(LocalDate::compareTo);
    return startDate.map(d -> new ServiceWindow(d, endDate.get()));
  }

  static Optional<ServiceWindow> fromCalendarDates(
      List<GtfsTrip> trips, List<GtfsCalendarDate> allCalendarDates) {
    Set<String> serviceIds = new HashSet<>(trips.stream().map(GtfsTrip::serviceId).toList());

    List<LocalDate> calendarDates =
        allCalendarDates.stream()
            .filter(
                d ->
                    serviceIds.contains(d.serviceId())
                        && d.exceptionType() == GtfsCalendarDateExceptionType.SERVICE_ADDED)
            .map(d -> d.date().getLocalDate())
            .toList();

    // Only empty if there are no calendar dates.
    Optional<LocalDate> startDate = calendarDates.stream().min(LocalDate::compareTo);
    Optional<LocalDate> endDate = calendarDates.stream().max(LocalDate::compareTo);
    return startDate.map(d -> new ServiceWindow(d, endDate.get()));
  }

  static Optional<ServiceWindow> fromCalendarsAndCalendarDates(
      List<GtfsTrip> trips, List<GtfsCalendar> calendars, List<GtfsCalendarDate> calendarDates) {
    Optional<ServiceWindow> serviceWindowFromCalendars =
        ServiceWindow.fromCalendars(trips, calendars);
    if (serviceWindowFromCalendars.isEmpty()) {
      return Optional.empty();
    }

    Set<String> serviceIds = new HashSet<>(trips.stream().map(GtfsTrip::serviceId).toList());

    Map<String, Set<LocalDate>> removedDaysByServiceId =
        calendarDates.stream()
            .filter(
                d ->
                    d.exceptionType() == GtfsCalendarDateExceptionType.SERVICE_REMOVED
                        && serviceIds.contains(d.serviceId()))
            .collect(
                groupingBy(
                    GtfsCalendarDate::serviceId, mapping(d -> d.date().getLocalDate(), toSet())));

    // We compute the set of days that are removed across all services in
    // order to shift the start and end dates.
    Set<LocalDate> removedDays = SetUtil.intersectAll(removedDaysByServiceId.values());

    LocalDate startDate = serviceWindowFromCalendars.get().startDate();
    LocalDate endDate = serviceWindowFromCalendars.get().endDate();

    while (removedDays.contains(startDate)) {
      startDate = startDate.plusDays(1);
    }
    while (removedDays.contains(endDate)) {
      endDate = endDate.minusDays(1);
    }
    return Optional.of(new ServiceWindow(startDate, endDate));
  }

  static Optional<ServiceWindow> get(
      List<GtfsTrip> trips,
      Optional<List<GtfsCalendar>> calendars,
      Optional<List<GtfsCalendarDate>> calendarDates) {
    if (calendarDates.isEmpty() && calendars.isPresent()) {
      return ServiceWindow.fromCalendars(trips, calendars.get());
    }

    if (calendarDates.isPresent() && calendars.isEmpty()) {
      return ServiceWindow.fromCalendarDates(trips, calendarDates.get());
    }

    if (calendars.isPresent() && calendarDates.isPresent()) {
      return ServiceWindow.fromCalendarsAndCalendarDates(
          trips, calendars.get(), calendarDates.get());
    }

    return Optional.empty();
  }
}
