package org.mobilitydata.gtfsvalidator.reportsummary.model;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDate;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateExceptionType;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

/**
 * Tests for {@link ServiceWindow}.
 *
 * <p>The test cases cover:
 *
 * <ol>
 *   <li>calendar.txt is not empty and calendar_dates.txt is empty – the service window spans the
 *       minimum start date to the maximum end date across all service IDs.
 *   <li>calendar.txt is empty and calendar_dates.txt is not empty – only SERVICE_ADDED entries
 *       define the window; SERVICE_REMOVED entries can narrow it.
 *   <li>Both files are not empty – calendar ranges establish a base window; calendar_date
 *       exceptions can remove dates or expand the window via additions.
 * </ol>
 */
@RunWith(JUnit4.class)
public class ServiceWindowTest {

  private static final NoticeContainer NOTICES = new NoticeContainer();

  // ---------------------------------------------------------------------------
  // Helper factories
  // ---------------------------------------------------------------------------

  private static GtfsTrip trip(int row, String serviceId) {
    return new GtfsTrip.Builder().setCsvRowNumber(row).setServiceId(serviceId).build();
  }

  private static GtfsCalendar calendar(int row, String serviceId, LocalDate start, LocalDate end) {
    return calendar(row, serviceId, start, end, EnumSet.noneOf(DayOfWeek.class));
  }

  private static GtfsCalendar calendar(
      int row, String serviceId, LocalDate start, LocalDate end, Set<DayOfWeek> removed) {
    return new GtfsCalendar.Builder()
        .setCsvRowNumber(row)
        .setServiceId(serviceId)
        .setStartDate(GtfsDate.fromLocalDate(start))
        .setEndDate(GtfsDate.fromLocalDate(end))
        // By default, service is active every day in the range unless the day-of-week
        // is explicitly listed as removed.
        .setMonday(removed.contains(DayOfWeek.MONDAY) ? 0 : 1)
        .setTuesday(removed.contains(DayOfWeek.TUESDAY) ? 0 : 1)
        .setWednesday(removed.contains(DayOfWeek.WEDNESDAY) ? 0 : 1)
        .setThursday(removed.contains(DayOfWeek.THURSDAY) ? 0 : 1)
        .setFriday(removed.contains(DayOfWeek.FRIDAY) ? 0 : 1)
        .setSaturday(removed.contains(DayOfWeek.SATURDAY) ? 0 : 1)
        .setSunday(removed.contains(DayOfWeek.SUNDAY) ? 0 : 1)
        .build();
  }

  private static GtfsCalendarDate calendarDate(
      int row, String serviceId, LocalDate date, GtfsCalendarDateExceptionType type) {
    return new GtfsCalendarDate.Builder()
        .setCsvRowNumber(row)
        .setServiceId(serviceId)
        .setDate(GtfsDate.fromLocalDate(date))
        .setExceptionType(type)
        .build();
  }

  // Convenience aliases
  private static final GtfsCalendarDateExceptionType ADDED =
      GtfsCalendarDateExceptionType.SERVICE_ADDED;
  private static final GtfsCalendarDateExceptionType REMOVED =
      GtfsCalendarDateExceptionType.SERVICE_REMOVED;

  // ===========================================================================
  // 1. calendar.txt NOT EMPTY – calendar_dates.txt EMPTY
  // ===========================================================================

  /** Single calendar entry: the service window equals its range exactly. */
  @Test
  public void get_singleCalendar_returnsItsRange() {
    List<GtfsCalendar> calendars =
        List.of(calendar(1, "s1", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31)));

    assertThat(
            ServiceWindow.get(
                GtfsTripTableContainer.forEntities(List.of(trip(0, "s1")), NOTICES),
                Optional.of(GtfsCalendarTableContainer.forEntities(calendars, NOTICES)),
                Optional.empty()))
        .isEqualTo(
            Optional.of(new ServiceWindow(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31))));
  }

  @Test
  public void get_singleCalendar_noActiveWeeRange() {
    List<GtfsCalendar> calendars =
        List.of(calendar(1, "s1", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31)));

    assertThat(
            ServiceWindow.get(
                GtfsTripTableContainer.forEntities(List.of(trip(0, "s1")), NOTICES),
                Optional.of(GtfsCalendarTableContainer.forEntities(calendars, NOTICES)),
                Optional.empty()))
        .isEqualTo(
            Optional.of(new ServiceWindow(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31))));
  }

  @Test
  public void get_singleCalendar_allWeekdaysDisabled_returnsEmpty() {
    // Calendar covers a date range but has no active weekdays (all set to 0),
    // so there is effectively no service even though start/end dates are present.
    GtfsCalendar disabledCalendar =
        new GtfsCalendar.Builder()
            .setCsvRowNumber(1)
            .setServiceId("s1")
            .setStartDate(GtfsDate.fromLocalDate(LocalDate.of(2025, 1, 1)))
            .setEndDate(GtfsDate.fromLocalDate(LocalDate.of(2025, 12, 31)))
            .setMonday(0)
            .setTuesday(0)
            .setWednesday(0)
            .setThursday(0)
            .setFriday(0)
            .setSaturday(0)
            .setSunday(0)
            .build();

    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "s1")), NOTICES);

    Optional<ServiceWindow> result =
        ServiceWindow.get(
            trips,
            Optional.of(GtfsCalendarTableContainer.forEntities(List.of(disabledCalendar), NOTICES)),
            Optional.empty());

    assertThat(result).isEqualTo(Optional.empty());
  }

  /**
   * Multiple calendars: window starts at the earliest start date and ends at the latest end date,
   * regardless of service ID.
   */
  @Test
  public void get_multipleCalendars_usesMinStartAndMaxEnd() {
    List<GtfsCalendar> calendars =
        List.of(
            calendar(1, "s1", LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 30)),
            calendar(2, "s2", LocalDate.of(2025, 1, 15), LocalDate.of(2025, 5, 31)),
            calendar(3, "s3", LocalDate.of(2025, 4, 1), LocalDate.of(2025, 12, 15)));
    List<GtfsTrip> trips = List.of(trip(1, "s1"), trip(2, "s2"), trip(3, "s3"));
    // min start = 2025-01-15 (s2), max end = 2025-12-15 (s3)
    assertThat(
            ServiceWindow.get(
                GtfsTripTableContainer.forEntities(trips, NOTICES),
                Optional.of(GtfsCalendarTableContainer.forEntities(calendars, NOTICES)),
                Optional.empty()))
        .isEqualTo(
            Optional.of(new ServiceWindow(LocalDate.of(2025, 1, 15), LocalDate.of(2025, 12, 15))));
  }

  /** Empty calendar list: no service window can be derived. */
  @Test
  public void get_emptyList_returnsEmpty() {
    List<GtfsTrip> trips = List.of(trip(1, "s1"));
    assertThat(
            ServiceWindow.get(
                GtfsTripTableContainer.forEntities(trips, NOTICES),
                Optional.empty(),
                Optional.empty()))
        .isEqualTo(Optional.empty());
  }

  /**
   * Via ServiceWindow.get: calendar table present, calendar_dates table absent. Trips must match a
   * service ID in the calendar to be counted.
   */
  @Test
  public void get_calendarOnlyNoCalendarDates_returnsCalendarWindow() {
    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "s1"), trip(2, "s2")), NOTICES);

    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(
            List.of(
                calendar(1, "s1", LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 31)),
                calendar(2, "s2", LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30))),
            NOTICES);

    Optional<ServiceWindow> result =
        ServiceWindow.get(trips, Optional.of(calendarTable), Optional.empty());

    assertThat(result)
        .isEqualTo(
            Optional.of(new ServiceWindow(LocalDate.of(2025, 5, 1), LocalDate.of(2025, 6, 30))));
  }

  /** Via ServiceWindow.get: a calendar entry whose service ID has no matching trip is ignored. */
  @Test
  public void get_calendarOnly_serviceIdWithNoTripIsIgnored() {
    // Only trip for s1; s2 has no trips
    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "s1")), NOTICES);

    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(
            List.of(
                calendar(1, "s1", LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 31)),
                // s2 has a wider range but no trips → must be excluded
                calendar(2, "s2", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31))),
            NOTICES);

    Optional<ServiceWindow> result =
        ServiceWindow.get(trips, Optional.of(calendarTable), Optional.empty());

    assertThat(result)
        .isEqualTo(
            Optional.of(new ServiceWindow(LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 31))));
  }

  // ===========================================================================
  // 2. calendar.txt EMPTY – calendar_dates.txt NOT EMPTY
  // ===========================================================================

  /** Only SERVICE_ADDED entries: window is the min/max of those dates. */
  @Test
  public void get_onlyAddedEntries_usesMinAndMaxAddedDate() {
    List<GtfsCalendarDate> dates =
        List.of(
            calendarDate(1, "s1", LocalDate.of(2025, 5, 10), ADDED),
            calendarDate(2, "s1", LocalDate.of(2025, 5, 20), ADDED),
            calendarDate(3, "s1", LocalDate.of(2025, 5, 30), ADDED));
    List<GtfsTrip> trips = List.of(trip(1, "s1"));

    assertThat(
            ServiceWindow.get(
                GtfsTripTableContainer.forEntities(trips, NOTICES),
                Optional.empty(),
                Optional.of(GtfsCalendarDateTableContainer.forEntities(dates, NOTICES))))
        .isEqualTo(
            Optional.of(new ServiceWindow(LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 30))));
  }

  /**
   * SERVICE_REMOVED entries are ignored when computing the window from calendar_dates alone; only
   * SERVICE_ADDED entries determine the range.
   */
  @Test
  public void get_removedEntriesIgnored_windowBasedOnAdded() {
    List<GtfsCalendarDate> dates =
        List.of(
            calendarDate(1, "s1", LocalDate.of(2025, 5, 10), ADDED),
            calendarDate(2, "s1", LocalDate.of(2025, 5, 25), ADDED),
            // This REMOVED entry is earlier than both ADDED dates → must be ignored
            calendarDate(3, "s1", LocalDate.of(2025, 4, 1), REMOVED));
    List<GtfsTrip> trips = List.of(trip(1, "s1"));

    assertThat(
            ServiceWindow.get(
                GtfsTripTableContainer.forEntities(trips, NOTICES),
                Optional.empty(),
                Optional.of(GtfsCalendarDateTableContainer.forEntities(dates, NOTICES))))
        .isEqualTo(
            Optional.of(new ServiceWindow(LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 25))));
  }

  /** Only SERVICE_REMOVED entries (no SERVICE_ADDED): no window can be derived. */
  @Test
  public void get_onlyRemovedEntries_returnsEmpty() {
    List<GtfsCalendarDate> dates =
        List.of(
            calendarDate(1, "s1", LocalDate.of(2025, 5, 10), REMOVED),
            calendarDate(2, "s1", LocalDate.of(2025, 5, 20), REMOVED));
    List<GtfsTrip> trips = List.of(trip(1, "s1"));

    assertThat(
            ServiceWindow.get(
                GtfsTripTableContainer.forEntities(trips, NOTICES),
                Optional.empty(),
                Optional.of(GtfsCalendarDateTableContainer.forEntities(dates, NOTICES))))
        .isEqualTo(Optional.empty());
  }

  /**
   * Via ServiceWindow.get: calendar table absent, calendar_dates table present. Only service IDs
   * that have matching trips are considered.
   */
  @Test
  public void get_calendarDatesOnly_returnsWindowFromAddedDates() {
    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "s1")), NOTICES);

    GtfsCalendarDateTableContainer calendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(
            List.of(
                calendarDate(1, "s1", LocalDate.of(2025, 5, 5), ADDED),
                calendarDate(2, "s1", LocalDate.of(2025, 5, 15), ADDED),
                calendarDate(3, "s1", LocalDate.of(2025, 5, 25), ADDED)),
            NOTICES);

    Optional<ServiceWindow> result =
        ServiceWindow.get(trips, Optional.empty(), Optional.of(calendarDateTable));

    assertThat(result)
        .isEqualTo(
            Optional.of(new ServiceWindow(LocalDate.of(2025, 5, 5), LocalDate.of(2025, 5, 25))));
  }

  /**
   * Via ServiceWindow.get: calendar_dates only; a SERVICE_ADDED entry whose service ID has no
   * matching trip must be excluded from the window.
   */
  @Test
  public void get_calendarDatesOnly_serviceIdWithNoTripIsIgnored() {
    // Trip only for s1; s2 has no trips
    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "s1")), NOTICES);

    GtfsCalendarDateTableContainer calendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(
            List.of(
                calendarDate(1, "s1", LocalDate.of(2025, 5, 10), ADDED),
                // s2 has a much later added date but no trips → must be excluded
                calendarDate(2, "s2", LocalDate.of(2025, 12, 31), ADDED)),
            NOTICES);

    Optional<ServiceWindow> result =
        ServiceWindow.get(trips, Optional.empty(), Optional.of(calendarDateTable));

    assertThat(result)
        .isEqualTo(
            Optional.of(new ServiceWindow(LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 10))));
  }

  // ===========================================================================
  // 3. BOTH calendar.txt AND calendar_dates.txt are NOT EMPTY
  // ===========================================================================

  /** No exceptions affect the window: the result equals the raw calendar window. */
  @Test
  public void get_bothTables_noExceptions_returnsCalendarWindow() {
    List<GtfsCalendar> calendars =
        List.of(calendar(1, "s1", LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 31)));

    // A REMOVED entry that isn't even inside the calendar range → no effect
    List<GtfsCalendarDate> dates =
        List.of(calendarDate(1, "s1", LocalDate.of(2025, 6, 1), REMOVED));

    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "s1")), NOTICES);
    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(calendars, NOTICES);
    GtfsCalendarDateTableContainer calendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(dates, NOTICES);

    assertThat(ServiceWindow.get(trips, Optional.of(calendarTable), Optional.of(calendarDateTable)))
        .isEqualTo(
            Optional.of(new ServiceWindow(LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 31))));
  }

  /**
   * A REMOVED entry that only covers a subset of services on a given date does NOT remove that date
   * from the window.
   */
  @Test
  public void get_bothTables_partialRemovalOnDate_dateKeptInWindow() {
    // Both s1 and s2 operate on 2025-05-01
    List<GtfsCalendar> calendars =
        List.of(
            calendar(1, "s1", LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 31)),
            calendar(2, "s2", LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 31)));

    // Only s1 is removed on 2025-05-01 → s2 still runs → date stays in window
    List<GtfsCalendarDate> dates =
        List.of(calendarDate(1, "s1", LocalDate.of(2025, 5, 1), REMOVED));

    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "s1"), trip(2, "s2")), NOTICES);
    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(calendars, NOTICES);
    GtfsCalendarDateTableContainer calendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(dates, NOTICES);

    assertThat(ServiceWindow.get(trips, Optional.of(calendarTable), Optional.of(calendarDateTable)))
        .isEqualTo(
            Optional.of(new ServiceWindow(LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 31))));
  }

  /**
   * A REMOVED entry that covers ALL services on a date removes that date from the window. When the
   * removed date is the first day of the range, the window start shifts forward.
   */
  @Test
  public void get_bothTables_allServicesRemovedOnFirstDay_startShiftsForward() {
    // Only s1 operates; 2025-05-01 is removed for s1 → window starts 2025-05-02
    List<GtfsCalendar> calendars =
        List.of(calendar(1, "s1", LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 31)));

    List<GtfsCalendarDate> dates =
        List.of(calendarDate(1, "s1", LocalDate.of(2025, 5, 1), REMOVED));

    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "s1")), NOTICES);
    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(calendars, NOTICES);
    GtfsCalendarDateTableContainer calendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(dates, NOTICES);

    assertThat(ServiceWindow.get(trips, Optional.of(calendarTable), Optional.of(calendarDateTable)))
        .isEqualTo(
            Optional.of(new ServiceWindow(LocalDate.of(2025, 5, 2), LocalDate.of(2025, 5, 31))));
  }

  /**
   * A REMOVED entry that covers ALL services on a date removes that date from the window. When the
   * removed date is the last day of the range, the window end shifts backward.
   */
  @Test
  public void get_bothTables_allServicesRemovedOnLastDay_endShiftsBackward() {
    List<GtfsCalendar> calendars =
        List.of(calendar(1, "s1", LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 31)));

    List<GtfsCalendarDate> dates =
        List.of(calendarDate(1, "s1", LocalDate.of(2025, 5, 31), REMOVED));

    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "s1")), NOTICES);
    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(calendars, NOTICES);
    GtfsCalendarDateTableContainer calendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(dates, NOTICES);

    assertThat(ServiceWindow.get(trips, Optional.of(calendarTable), Optional.of(calendarDateTable)))
        .isEqualTo(
            Optional.of(new ServiceWindow(LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 30))));
  }

  /**
   * Multiple calendars: removing a date only from a proper subset of their service IDs keeps the
   * date in the window; removing it for every service ID that operates on that date drops it.
   */
  @Test
  public void get_bothTables_multipleCalendars_removeFromAllDropsDate() {
    // Three services all start on 2025-10-10
    List<GtfsCalendar> calendars =
        List.of(
            calendar(1, "service1", LocalDate.of(2025, 10, 10), LocalDate.of(2026, 2, 17)),
            calendar(2, "service2", LocalDate.of(2025, 10, 10), LocalDate.of(2026, 4, 3)),
            calendar(3, "service3", LocalDate.of(2025, 10, 10), LocalDate.of(2026, 8, 11)));

    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(
            List.of(trip(1, "service1"), trip(2, "service2"), trip(3, "service3")), NOTICES);
    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(calendars, NOTICES);

    // Only 2 of 3 services removed on the first day → date still in window
    GtfsCalendarDateTableContainer partialRemovalCalendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(
            List.of(
                calendarDate(1, "service1", LocalDate.of(2025, 10, 10), REMOVED),
                calendarDate(2, "service2", LocalDate.of(2025, 10, 10), REMOVED)),
            NOTICES);

    assertThat(
            ServiceWindow.get(
                trips, Optional.of(calendarTable), Optional.of(partialRemovalCalendarDateTable)))
        .isEqualTo(
            Optional.of(new ServiceWindow(LocalDate.of(2025, 10, 10), LocalDate.of(2026, 8, 11))));

    // All 3 services removed on the first day → window starts a day later
    GtfsCalendarDateTableContainer fullRemovalCalendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(
            List.of(
                calendarDate(4, "service1", LocalDate.of(2025, 10, 10), REMOVED),
                calendarDate(5, "service2", LocalDate.of(2025, 10, 10), REMOVED),
                calendarDate(6, "service3", LocalDate.of(2025, 10, 10), REMOVED)),
            NOTICES);

    assertThat(
            ServiceWindow.get(
                trips, Optional.of(calendarTable), Optional.of(fullRemovalCalendarDateTable)))
        .isEqualTo(
            Optional.of(new ServiceWindow(LocalDate.of(2025, 10, 11), LocalDate.of(2026, 8, 11))));
  }

  /** Full worked example from the spec discussion, via the public ServiceWindow.get API. */
  @Test
  public void get_bothTables_specExample_correctWindow() {
    List<GtfsCalendar> calendars =
        List.of(calendar(1, "s1", LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 30)));

    List<GtfsCalendarDate> dates =
        List.of(
            calendarDate(1, "s1", LocalDate.of(2025, 5, 1), REMOVED),
            calendarDate(2, "s3", LocalDate.of(2025, 5, 24), ADDED),
            calendarDate(3, "s3", LocalDate.of(2025, 5, 31), REMOVED));

    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "s1"), trip(2, "s3")), NOTICES);
    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(calendars, NOTICES);
    GtfsCalendarDateTableContainer calendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(dates, NOTICES);

    assertThat(ServiceWindow.get(trips, Optional.of(calendarTable), Optional.of(calendarDateTable)))
        .isEqualTo(
            Optional.of(new ServiceWindow(LocalDate.of(2025, 5, 2), LocalDate.of(2025, 5, 30))));
  }

  /** An ADDED entry in calendar_dates extends the window beyond the calendar range. */
  @Test
  public void get_bothTables_addedEntryExpandsWindow() {
    List<GtfsCalendar> calendars =
        List.of(calendar(1, "s1", LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 31)));

    // s2 is added on a date beyond the calendar end → end of window must expand
    List<GtfsCalendarDate> dates = List.of(calendarDate(1, "s2", LocalDate.of(2025, 6, 15), ADDED));

    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "s1"), trip(2, "s2")), NOTICES);
    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(calendars, NOTICES);
    GtfsCalendarDateTableContainer calendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(dates, NOTICES);

    Optional<ServiceWindow> result =
        ServiceWindow.get(trips, Optional.of(calendarTable), Optional.of(calendarDateTable));

    // The window is at minimum the calendar range [2025-05-01, 2025-05-31].
    assertThat(result).isPresent();
    assertThat(result.get().startDate()).isEqualTo(LocalDate.of(2025, 5, 1));
    assertThat(result.get().endDate()).isAtLeast(LocalDate.of(2025, 5, 31));
  }

  /** All calendar dates are SERVICE_REMOVED only, and the calendar table is empty: no window. */
  @Test
  public void get_bothTables_emptyCalendars_returnsEmpty() {
    List<GtfsCalendarDate> dates =
        List.of(calendarDate(1, "s1", LocalDate.of(2025, 5, 1), REMOVED));

    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "s1")), NOTICES);
    GtfsCalendarDateTableContainer calendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(dates, NOTICES);

    assertThat(ServiceWindow.get(trips, Optional.empty(), Optional.of(calendarDateTable)))
        .isEqualTo(Optional.empty());
  }

  /**
   * Via ServiceWindow.get: both tables present; removed date for all services shifts window start.
   */
  @Test
  public void get_bothTables_removedDateForAllServicesShiftsStart() {
    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "s1"), trip(2, "s2")), NOTICES);

    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(
            List.of(
                calendar(1, "s1", LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 31)),
                calendar(2, "s2", LocalDate.of(2025, 5, 1), LocalDate.of(2025, 6, 30))),
            NOTICES);

    // Both s1 and s2 are removed on 2025-05-01 → window must start 2025-05-02
    GtfsCalendarDateTableContainer calendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(
            List.of(
                calendarDate(1, "s1", LocalDate.of(2025, 5, 1), REMOVED),
                calendarDate(2, "s2", LocalDate.of(2025, 5, 1), REMOVED)),
            NOTICES);

    Optional<ServiceWindow> result =
        ServiceWindow.get(trips, Optional.of(calendarTable), Optional.of(calendarDateTable));

    assertThat(result)
        .isEqualTo(
            Optional.of(new ServiceWindow(LocalDate.of(2025, 5, 2), LocalDate.of(2025, 6, 30))));
  }

  /**
   * Via ServiceWindow.get: both tables present but neither table nor calendar_dates has entries
   * that produce a valid service date → empty result.
   */
  @Test
  public void get_bothTables_allDatesRemoved_returnsEmpty() {
    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "s1")), NOTICES);

    // Calendar covers only a single day
    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(
            List.of(calendar(1, "s1", LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 1))),
            NOTICES);

    // That single day is removed for s1
    GtfsCalendarDateTableContainer calendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(
            List.of(calendarDate(1, "s1", LocalDate.of(2025, 5, 1), REMOVED)), NOTICES);

    Optional<ServiceWindow> result =
        ServiceWindow.get(trips, Optional.of(calendarTable), Optional.of(calendarDateTable));

    assertThat(result).isEqualTo(Optional.empty());
  }

  /** Via ServiceWindow.get: both tables absent → empty result. */
  @Test
  public void get_bothTablesAbsent_returnsEmpty() {
    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "s1")), NOTICES);

    Optional<ServiceWindow> result = ServiceWindow.get(trips, Optional.empty(), Optional.empty());

    assertThat(result).isEqualTo(Optional.empty());
  }

  /** Via ServiceWindow.get: calendar table present but no trips match any service ID → empty. */
  @Test
  public void get_calendarOnly_noMatchingTrips_returnsEmpty() {
    // Trip has serviceId "unknown", calendar has "s1"
    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "unknown")), NOTICES);

    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(
            List.of(calendar(1, "s1", LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 31))),
            NOTICES);

    Optional<ServiceWindow> result =
        ServiceWindow.get(trips, Optional.of(calendarTable), Optional.empty());

    assertThat(result).isEqualTo(Optional.empty());
  }

  /**
   * Via ServiceWindow.get: calendar_dates table present but no trips match any service ID → empty.
   */
  @Test
  public void get_calendarDatesOnly_noMatchingTrips_returnsEmpty() {
    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "unknown")), NOTICES);

    GtfsCalendarDateTableContainer calendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(
            List.of(calendarDate(1, "s1", LocalDate.of(2025, 5, 10), ADDED)), NOTICES);

    Optional<ServiceWindow> result =
        ServiceWindow.get(trips, Optional.empty(), Optional.of(calendarDateTable));

    assertThat(result).isEqualTo(Optional.empty());
  }

  /**
   * A calendar-based service that operates only on Wednesday and Thursday, where the first
   * Wednesday in the calendar range is fully removed by an exception. The calendar range starts on
   * Monday. The service window, computed through {@link ServiceWindow#get}, should still start on
   * the first Thursday that actually runs service.
   */
  @Test
  public void get_firstWednesdayRemoved_startStillOnFirstThursday() {
    // Trip table: a single trip using serviceId "s1".
    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "s1")), NOTICES);

    // Calendar table: "s1" from Monday 2025-01-06 to 2025-01-31, operating on Wednesday/Thursday
    // only.
    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(
            List.of(
                calendar(
                    1,
                    "s1",
                    LocalDate.of(2025, 1, 6),
                    LocalDate.of(2025, 1, 31),
                    EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))),
            NOTICES);

    // calendar_dates: remove the first Wednesday (2025-01-08) completely for s1.
    GtfsCalendarDateTableContainer calendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(
            List.of(calendarDate(1, "s1", LocalDate.of(2025, 1, 8), REMOVED)), NOTICES);

    // In this scenario:
    //   - The calendar.txt period starts on Monday 2025-01-06.
    //   - Service is modeled as operating on Wednesday/Thursday within that range.
    //   - The first Wednesday (2025-01-08) is removed via calendar_dates.
    // We expect the service window still to start on the first Thursday that has service,
    // i.e. 2025-01-09, and run through the end of the calendar range.
    //
    // NOTE: This behaviour is not supported by the current implementation; this test documents
    // the expected result and will currently fail.
    Optional<ServiceWindow> result =
        ServiceWindow.get(trips, Optional.of(calendarTable), Optional.of(calendarDateTable));

    assertThat(result)
        .isEqualTo(
            Optional.of(new ServiceWindow(LocalDate.of(2025, 1, 9), LocalDate.of(2025, 1, 31))));
  }

  /**
   * A single calendar service with a SERVICE_REMOVED exception outside its date range. The
   * exception must be ignored when computing the window start and end, since there is no service on
   * that date according to calendar.txt.
   */
  @Test
  public void get_bothTables_removedDateOutsideCalendarRange_isIgnored() {
    // Service s1 operates daily from 2025-05-10 to 2025-05-20.
    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(
            List.of(calendar(1, "s1", LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 20))),
            NOTICES);

    // calendar_dates removes 2025-05-05, which is *outside* the calendar range.
    GtfsCalendarDateTableContainer calendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(
            List.of(calendarDate(1, "s1", LocalDate.of(2025, 5, 5), REMOVED)), NOTICES);

    // A single trip using s1.
    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "s1")), NOTICES);

    // The removed date is outside the calendar range and must not affect the window; we still
    // expect the window to span exactly the calendar range.
    Optional<ServiceWindow> result =
        ServiceWindow.get(trips, Optional.of(calendarTable), Optional.of(calendarDateTable));

    assertThat(result)
        .isEqualTo(
            Optional.of(new ServiceWindow(LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 20))));
  }

  /**
   * Two calendar services share the same start and end dates. calendar_dates removes only one of
   * the services on the first and last day. Because the other service still operates on those
   * dates, the service window must remain the full calendar range.
   */
  @Test
  public void get_bothTables_partialRemovalOnStartAndEnd_keepsFullRange() {
    // Both services s1 and s2 operate daily from 2025-05-10 to 2025-05-20.
    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(
            List.of(
                calendar(1, "s1", LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 20)),
                calendar(2, "s2", LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 20))),
            NOTICES);

    // Remove s1 on the start and end dates, but keep s2 operating on those dates.
    GtfsCalendarDateTableContainer calendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(
            List.of(
                calendarDate(1, "s1", LocalDate.of(2025, 5, 10), REMOVED),
                calendarDate(2, "s1", LocalDate.of(2025, 5, 20), REMOVED)),
            NOTICES);

    // Trips for both s1 and s2.
    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "s1"), trip(2, "s2")), NOTICES);

    Optional<ServiceWindow> result =
        ServiceWindow.get(trips, Optional.of(calendarTable), Optional.of(calendarDateTable));

    // Even though s1 does not operate on the first and last day, s2 still does, so the window
    // must still be [2025-05-10, 2025-05-20].
    assertThat(result)
        .isEqualTo(
            Optional.of(new ServiceWindow(LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 20))));
  }

  /**
   * A SERVICE_ADDED entry outside the calendar.txt range must still be reflected in the service
   * window: when no service operates on that date via calendars, the window should extend to
   * include the added date, and if it is the only service day, the window start and end must both
   * equal that date.
   */
  @Test
  public void get_bothTables_addedDateOutsideCalendarRange_definesWindow() {
    // calendar.txt: s1 operates daily from 2025-05-10 to 2025-05-20.
    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(
            List.of(calendar(1, "s1", LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 20))),
            NOTICES);

    // calendar_dates: s2 is added on 2025-05-25, which lies completely outside s1's range.
    GtfsCalendarDateTableContainer calendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(
            List.of(calendarDate(1, "s2", LocalDate.of(2025, 5, 25), ADDED)), NOTICES);

    // Trips for both s1 and s2 so both service IDs are considered.
    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "s1"), trip(2, "s2")), NOTICES);

    Optional<ServiceWindow> result =
        ServiceWindow.get(trips, Optional.of(calendarTable), Optional.of(calendarDateTable));

    // At a minimum we expect the window to cover the calendar range [2025-05-10, 2025-05-20] and
    // also extend to include the added date 2025-05-25, so the end must be at least 2025-05-25.
    assertThat(result).isPresent();
    assertThat(result.get().startDate()).isEqualTo(LocalDate.of(2025, 5, 10));
    assertThat(result.get().endDate()).isAtLeast(LocalDate.of(2025, 5, 25));
  }

  /**
   * calendar.txt has a service with no active weekdays (all zero), while calendar_dates provides
   * SERVICE_ADDED entries for that service. One added date defines the start of the window and
   * another defines the end; SERVICE_REMOVED entries in between do not affect the outer bounds.
   */
  @Test
  public void get_bothTables_calendarNoActiveDays_windowFromAddedDates() {
    // Calendar for s1 with a broad date range but no active weekdays.
    GtfsCalendar disabledCalendar =
        new GtfsCalendar.Builder()
            .setCsvRowNumber(1)
            .setServiceId("s1")
            .setStartDate(GtfsDate.fromLocalDate(LocalDate.of(2025, 1, 1)))
            .setEndDate(GtfsDate.fromLocalDate(LocalDate.of(2025, 12, 31)))
            .setMonday(0)
            .setTuesday(0)
            .setWednesday(0)
            .setThursday(0)
            .setFriday(0)
            .setSaturday(0)
            .setSunday(0)
            .build();

    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(List.of(disabledCalendar), NOTICES);

    // calendar_dates for s1: two added dates (start and end) and one removed date in between.
    LocalDate startAdded = LocalDate.of(2025, 3, 10);
    LocalDate midRemoved = LocalDate.of(2025, 6, 1);
    LocalDate endAdded = LocalDate.of(2025, 9, 20);

    GtfsCalendarDateTableContainer calendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(
            List.of(
                calendarDate(1, "s1", startAdded, ADDED),
                calendarDate(2, "s1", midRemoved, REMOVED),
                calendarDate(3, "s1", endAdded, ADDED)),
            NOTICES);

    // Single trip using s1 so the serviceId is included in the computation.
    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(List.of(trip(1, "s1")), NOTICES);

    Optional<ServiceWindow> result =
        ServiceWindow.get(trips, Optional.of(calendarTable), Optional.of(calendarDateTable));

    // Even though calendar.txt provides no active weekdays, the SERVICE_ADDED entries in
    // calendar_dates must define the service window: from the first added date to the last.
    assertThat(result).isEqualTo(Optional.of(new ServiceWindow(startAdded, endAdded)));
  }
}
