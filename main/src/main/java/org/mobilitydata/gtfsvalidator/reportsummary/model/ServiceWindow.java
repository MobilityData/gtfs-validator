package org.mobilitydata.gtfsvalidator.reportsummary.model;

import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDate;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateExceptionType;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarService;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.util.SetUtil;

record ServiceWindow(LocalDate startDate, LocalDate endDate) {
  /**
   * Given a list of calendars, get the service window.
   *
   * @return The service window if there's at least one calendar, and empty otherwise.
   */
  private static Optional<ServiceWindow> fromCalendars(List<GtfsCalendar> calendars) {
    List<LocalDate> activeDatesForCalendars =
        calendars.stream().flatMap(c -> getActiveDatesForCalendar(c).stream()).toList();
    // Only empty if there are no calendars or no active weekdays for
    // any of the calendars.
    Optional<LocalDate> startDate = activeDatesForCalendars.stream().min(LocalDate::compareTo);
    Optional<LocalDate> endDate = activeDatesForCalendars.stream().max(LocalDate::compareTo);
    return startDate.map(d -> new ServiceWindow(d, endDate.get()));
  }

  /**
   * Given a list of calendar dates, get the service window.
   *
   * @return The service window if there's at least one date on which service is available, and
   *     empty otherwise.
   */
  private static Optional<ServiceWindow> fromCalendarDates(
      List<GtfsCalendarDate> allCalendarDates) {
    List<LocalDate> calendarDates =
        allCalendarDates.stream()
            .filter(d -> d.exceptionType() == GtfsCalendarDateExceptionType.SERVICE_ADDED)
            .map(d -> d.date().getLocalDate())
            .toList();

    // Only empty if there are no calendar dates.
    Optional<LocalDate> startDate = calendarDates.stream().min(LocalDate::compareTo);
    Optional<LocalDate> endDate = calendarDates.stream().max(LocalDate::compareTo);
    return startDate.map(d -> new ServiceWindow(d, endDate.get()));
  }

  /**
   * Given a list of calendars, map each date to the services it's in range for.
   *
   * <p>This doesn't take exceptions into account.
   *
   * @return The set of service ids for each date.
   */
  private static Map<LocalDate, Set<String>> getServiceIdsByDateFromCalendars(
      List<GtfsCalendar> calendars) {
    Map<LocalDate, Set<String>> serviceIdsByDate = new HashMap<>();

    for (GtfsCalendar calendar : calendars) {
      for (LocalDate date : getActiveDatesForCalendar(calendar)) {
        Set<String> serviceIdsForDate = serviceIdsByDate.getOrDefault(date, new HashSet<>());
        serviceIdsForDate.add(calendar.serviceId());
        serviceIdsByDate.put(date, serviceIdsForDate);
      }
    }

    return serviceIdsByDate;
  }

  /**
   * Given some calendars and calendar dates, get the service window. Removed dates are only taken
   * into account if they apply to all relevant services.
   *
   * @return The service window if there's at least one date with service, and empty otherwise.
   */
  private static Optional<ServiceWindow> fromCalendarsAndCalendarDates(
      List<GtfsCalendar> calendars, List<GtfsCalendarDate> calendarDates) {
    Map<LocalDate, Set<String>> serviceIdsByDateFromCalendars =
        getServiceIdsByDateFromCalendars(calendars);

    // Dates added to at least one service via an exception. We don't check for
    // contradicting exceptions.
    Set<LocalDate> addedDates =
        calendarDates.stream()
            .filter(d -> d.exceptionType() == GtfsCalendarDateExceptionType.SERVICE_ADDED)
            .map(d -> d.date().getLocalDate())
            .collect(toSet());

    // Dates removed from all relevant services via an exception.
    Set<LocalDate> removedDates =
        calendarDates.stream()
            .filter(d -> d.exceptionType() == GtfsCalendarDateExceptionType.SERVICE_REMOVED)
            .collect(
                groupingBy(
                    d -> d.date().getLocalDate(), mapping(GtfsCalendarDate::serviceId, toSet())))
            .entrySet()
            .stream()
            .filter(
                serviceIdsForDate -> {
                  LocalDate date = serviceIdsForDate.getKey();
                  Set<String> serviceIds = serviceIdsForDate.getValue();
                  // If the date is in `addedDates`, we know there's at least one service
                  // available on that date.
                  return serviceIds.equals(serviceIdsByDateFromCalendars.get(date));
                })
            .map(Map.Entry::getKey)
            .collect(toSet());

    Set<LocalDate> servicedDates =
        SetUtil.union(
            SetUtil.difference(serviceIdsByDateFromCalendars.keySet(), removedDates), addedDates);

    // Only empty if there are no serviced dates.
    Optional<LocalDate> startDate = servicedDates.stream().min(LocalDate::compareTo);
    Optional<LocalDate> endDate = servicedDates.stream().max(LocalDate::compareTo);
    return startDate.map(d -> new ServiceWindow(d, endDate.get()));
  }

  /**
   * Given some calendars and/or calendar dates, get the service window.
   *
   * @return The service window if there's at least one date on which service is available, and
   *     empty otherwise.
   */
  static Optional<ServiceWindow> get(
      GtfsTripTableContainer tripTable,
      Optional<GtfsCalendarTableContainer> calendarTable,
      Optional<GtfsCalendarDateTableContainer> calendarDateTable) {

    Optional<List<GtfsCalendar>> calendars =
        calendarTable
            .map(GtfsCalendarTableContainer::getEntities)
            .map(List::stream)
            .map(cs -> cs.filter(c -> !tripTable.byServiceId(c.serviceId()).isEmpty()))
            .map(Stream::toList);
    Optional<List<GtfsCalendarDate>> calendarDates =
        calendarDateTable
            .map(GtfsCalendarDateTableContainer::getEntities)
            .map(List::stream)
            .map(ds -> ds.filter(d -> !tripTable.byServiceId(d.serviceId()).isEmpty()))
            .map(Stream::toList);

    if (calendarDates.isEmpty() && calendars.isPresent()) {
      return ServiceWindow.fromCalendars(calendars.get());
    }

    if (calendarDates.isPresent() && calendars.isEmpty()) {
      return ServiceWindow.fromCalendarDates(calendarDates.get());
    }

    if (calendars.isPresent() && calendarDates.isPresent()) {
      return ServiceWindow.fromCalendarsAndCalendarDates(calendars.get(), calendarDates.get());
    }

    return Optional.empty();
  }

  /**
   * Given a calendar, get the dates that are active based on the service interval and on weekday
   * patterns.
   */
  private static List<LocalDate> getActiveDatesForCalendar(GtfsCalendar calendar) {
    LocalDate startDate = calendar.startDate().getLocalDate();
    LocalDate endDate = calendar.endDate().getLocalDate();

    return startDate.datesUntil(endDate.plusDays(1)).toList().stream()
        .filter(
            d ->
                switch (d.getDayOfWeek()) {
                      case MONDAY -> calendar.monday();
                      case TUESDAY -> calendar.tuesday();
                      case WEDNESDAY -> calendar.wednesday();
                      case THURSDAY -> calendar.thursday();
                      case FRIDAY -> calendar.friday();
                      case SATURDAY -> calendar.saturday();
                      case SUNDAY -> calendar.sunday();
                    }
                    == GtfsCalendarService.AVAILABLE)
        .toList();
  }
}
