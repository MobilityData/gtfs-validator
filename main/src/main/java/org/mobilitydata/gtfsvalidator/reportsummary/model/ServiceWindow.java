package org.mobilitydata.gtfsvalidator.reportsummary.model;

import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.util.*;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDate;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateExceptionType;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.util.SetUtil;

record ServiceWindow(LocalDate startDate, LocalDate endDate) {
  static Optional<ServiceWindow> fromCalendars(
      GtfsTripTableContainer tripTable, List<GtfsCalendar> allCalendars) {
    List<GtfsCalendar> calendars =
        allCalendars.stream()
            .filter(calendar -> !tripTable.byServiceId(calendar.serviceId()).isEmpty())
            .toList();

    // Only empty if there are no calendars.
    Optional<LocalDate> startDate =
        calendars.stream().map(c -> c.startDate().getLocalDate()).min(LocalDate::compareTo);
    Optional<LocalDate> endDate =
        calendars.stream().map(c -> c.endDate().getLocalDate()).max(LocalDate::compareTo);
    return startDate.map(d -> new ServiceWindow(d, endDate.get()));
  }

  static Optional<ServiceWindow> fromCalendarDates(
      GtfsTripTableContainer tripTable, List<GtfsCalendarDate> allCalendarDates) {
    List<LocalDate> calendarDates =
        allCalendarDates.stream()
            .filter(
                d ->
                    d.exceptionType() == GtfsCalendarDateExceptionType.SERVICE_ADDED
                        && !tripTable.byServiceId(d.serviceId()).isEmpty())
            .map(d -> d.date().getLocalDate())
            .toList();

    // Only empty if there are no calendar dates.
    Optional<LocalDate> startDate = calendarDates.stream().min(LocalDate::compareTo);
    Optional<LocalDate> endDate = calendarDates.stream().max(LocalDate::compareTo);
    return startDate.map(d -> new ServiceWindow(d, endDate.get()));
  }

  static Optional<ServiceWindow> fromCalendarsAndCalendarDates(
      GtfsTripTableContainer tripTable,
      List<GtfsCalendar> calendars,
      List<GtfsCalendarDate> calendarDates) {
    Optional<ServiceWindow> serviceWindowFromCalendars =
        ServiceWindow.fromCalendars(tripTable, calendars);
    if (serviceWindowFromCalendars.isEmpty()) {
      return Optional.empty();
    }

    Map<String, Set<LocalDate>> removedDaysByServiceId =
        calendarDates.stream()
            .filter(
                d ->
                    d.exceptionType() == GtfsCalendarDateExceptionType.SERVICE_REMOVED
                        && !tripTable.byServiceId(d.serviceId()).isEmpty())
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
      GtfsTripTableContainer tripTable,
      Optional<GtfsCalendarTableContainer> calendarTable,
      Optional<GtfsCalendarDateTableContainer> calendarDateTable) {

    Optional<List<GtfsCalendar>> calendars =
        calendarTable.map(GtfsCalendarTableContainer::getEntities);
    Optional<List<GtfsCalendarDate>> calendarDates =
        calendarDateTable.map(GtfsCalendarDateTableContainer::getEntities);

    if (calendarDates.isEmpty() && calendars.isPresent()) {
      return ServiceWindow.fromCalendars(tripTable, calendars.get());
    }

    if (calendarDates.isPresent() && calendars.isEmpty()) {
      return ServiceWindow.fromCalendarDates(tripTable, calendarDates.get());
    }

    if (calendars.isPresent() && calendarDates.isPresent()) {
      return ServiceWindow.fromCalendarsAndCalendarDates(
          tripTable, calendars.get(), calendarDates.get());
    }

    return Optional.empty();
  }
}
