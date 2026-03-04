package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDate;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateExceptionType;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

public class ServiceIntervalCacheTest {

  private static GtfsCalendar buildCalendar(
      String serviceId, String startDate, String endDate, int csvRowNumber) {
    return new GtfsCalendar.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setServiceId(serviceId)
        .setStartDate(GtfsDate.fromString(startDate))
        .setEndDate(GtfsDate.fromString(endDate))
        .setMonday(1)
        .setTuesday(1)
        .setWednesday(1)
        .setThursday(1)
        .setFriday(1)
        .setSaturday(1)
        .setSunday(1)
        .build();
  }

  private static GtfsCalendarDate buildCalendarDate(
      String serviceId,
      String date,
      GtfsCalendarDateExceptionType exceptionType,
      int csvRowNumber) {
    return new GtfsCalendarDate.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setServiceId(serviceId)
        .setDate(GtfsDate.fromString(date))
        .setExceptionType(exceptionType)
        .build();
  }

  private static GtfsCalendarTableContainer calendarTable(List<GtfsCalendar> entries) {
    return GtfsCalendarTableContainer.forEntities(
        ImmutableList.copyOf(entries), new NoticeContainer());
  }

  private static GtfsCalendarDateTableContainer calendarDateTable(List<GtfsCalendarDate> entries) {
    return GtfsCalendarDateTableContainer.forEntities(
        ImmutableList.copyOf(entries), new NoticeContainer());
  }

  @Test
  public void unknownServiceId_returnsNull() {
    ServiceIntervalCache cache = new ServiceIntervalCache();
    GtfsCalendarTableContainer calendar =
        calendarTable(ImmutableList.of(buildCalendar("service_1", "20240101", "20240131", 1)));
    GtfsCalendarDateTableContainer calendarDates = calendarDateTable(ImmutableList.of());

    assertThat(cache.getIntervals("unknown_service", calendar, calendarDates)).isNull();
  }

  @Test
  public void emptyCalendarAndCalendarDates_returnsNull() {
    ServiceIntervalCache cache = new ServiceIntervalCache();
    assertThat(
            cache.getIntervals(
                "service_1",
                calendarTable(ImmutableList.of()),
                calendarDateTable(ImmutableList.of())))
        .isNull();
  }

  @Test
  public void nullCalendarTable_unknownService_returnsNull() {
    ServiceIntervalCache cache = new ServiceIntervalCache();
    assertThat(cache.getIntervals("service_1", null, calendarDateTable(ImmutableList.of())))
        .isNull();
  }

  @Test
  public void nullCalendarDateTable_unknownService_returnsNull() {
    ServiceIntervalCache cache = new ServiceIntervalCache();
    assertThat(cache.getIntervals("service_1", calendarTable(ImmutableList.of()), null)).isNull();
  }

  @Test
  public void calendarOnly_knownServiceId_returnsNonNull() {
    ServiceIntervalCache cache = new ServiceIntervalCache();
    GtfsCalendarTableContainer calendar =
        calendarTable(ImmutableList.of(buildCalendar("service_1", "20240101", "20240131", 1)));

    assertThat(cache.getIntervals("service_1", calendar, calendarDateTable(ImmutableList.of())))
        .isNotNull();
  }

  @Test
  public void calendarOnly_continuousService_noGaps() {
    ServiceIntervalCache cache = new ServiceIntervalCache();
    GtfsCalendarTableContainer calendar =
        calendarTable(ImmutableList.of(buildCalendar("service_1", "20240101", "20240131", 1)));

    ServiceInterval interval =
        cache.getIntervals("service_1", calendar, calendarDateTable(ImmutableList.of()));

    assertThat(interval.getGaps()).isEmpty();
  }

  @Test
  public void calendarOnly_multipleServices_eachResolvesIndependently() {
    ServiceIntervalCache cache = new ServiceIntervalCache();
    GtfsCalendarTableContainer calendar =
        calendarTable(
            ImmutableList.of(
                buildCalendar("service_1", "20240101", "20240131", 1),
                buildCalendar("service_2", "20240201", "20240229", 2)));
    GtfsCalendarDateTableContainer calendarDates = calendarDateTable(ImmutableList.of());

    assertThat(cache.getIntervals("service_1", calendar, calendarDates)).isNotNull();
    assertThat(cache.getIntervals("service_2", calendar, calendarDates)).isNotNull();
    assertThat(cache.getIntervals("service_3", calendar, calendarDates)).isNull();
  }

  @Test
  public void calendarDatesOnly_serviceAdded_returnsNonNull() {
    ServiceIntervalCache cache = new ServiceIntervalCache();
    GtfsCalendarDateTableContainer calendarDates =
        calendarDateTable(
            ImmutableList.of(
                buildCalendarDate(
                    "service_cd", "20240101", GtfsCalendarDateExceptionType.SERVICE_ADDED, 1)));

    assertThat(cache.getIntervals("service_cd", calendarTable(ImmutableList.of()), calendarDates))
        .isNotNull();
  }

  @Test
  public void calendarDatesOnly_serviceAddedTwoDates_createsGap() {
    // Jan 1 and Jan 14 → 13-day gap
    ServiceIntervalCache cache = new ServiceIntervalCache();
    GtfsCalendarDateTableContainer calendarDates =
        calendarDateTable(
            ImmutableList.of(
                buildCalendarDate(
                    "service_cd", "20240101", GtfsCalendarDateExceptionType.SERVICE_ADDED, 1),
                buildCalendarDate(
                    "service_cd", "20240114", GtfsCalendarDateExceptionType.SERVICE_ADDED, 2)));

    ServiceInterval interval =
        cache.getIntervals("service_cd", calendarTable(ImmutableList.of()), calendarDates);

    assertThat(interval != null).isTrue();
    assertThat(interval.getGaps().size()).isEqualTo(1);
  }

  @Test
  public void calendarDatesOnly_serviceAddedTwoDatesLargeGap_hasGap() {
    // Jan 1 and Feb 1 → 31-day gap, should appear in getGaps().
    ServiceIntervalCache cache = new ServiceIntervalCache();
    GtfsCalendarDateTableContainer calendarDates =
        calendarDateTable(
            ImmutableList.of(
                buildCalendarDate(
                    "service_cd", "20240101", GtfsCalendarDateExceptionType.SERVICE_ADDED, 1),
                buildCalendarDate(
                    "service_cd", "20240201", GtfsCalendarDateExceptionType.SERVICE_ADDED, 2)));

    ServiceInterval interval =
        cache.getIntervals("service_cd", calendarTable(ImmutableList.of()), calendarDates);

    assertThat(interval != null).isTrue();
    assertThat(interval.getGaps()).hasSize(1);
  }

  @Test
  public void combined_serviceRemovedCreatesGap_gapDetected() {
    // Full month active, then 14 days removed → gap should be present.
    ServiceIntervalCache cache = new ServiceIntervalCache();
    GtfsCalendarTableContainer calendar =
        calendarTable(ImmutableList.of(buildCalendar("service_1", "20240101", "20240131", 1)));

    ImmutableList.Builder<GtfsCalendarDate> removals = ImmutableList.builder();
    for (int d = 11; d <= 24; d++) {
      removals.add(
          buildCalendarDate(
              "service_1",
              String.format("202401%02d", d),
              GtfsCalendarDateExceptionType.SERVICE_REMOVED,
              d));
    }

    ServiceInterval interval =
        cache.getIntervals("service_1", calendar, calendarDateTable(removals.build()));

    assertThat(interval != null).isTrue();
    assertThat(interval.getGaps()).hasSize(1);
  }

  @Test
  public void combined_serviceRemovedCreatesGap() {
    ServiceIntervalCache cache = new ServiceIntervalCache();
    GtfsCalendarTableContainer calendar =
        calendarTable(ImmutableList.of(buildCalendar("service_1", "20240101", "20240131", 1)));

    GtfsCalendarDateTableContainer calendarDates =
        calendarDateTable(
            ImmutableList.of(
                buildCalendarDate(
                    "service_1", "20240115", GtfsCalendarDateExceptionType.SERVICE_REMOVED, 1),
                buildCalendarDate(
                    "service_1", "20240116", GtfsCalendarDateExceptionType.SERVICE_REMOVED, 2),
                buildCalendarDate(
                    "service_1", "20240117", GtfsCalendarDateExceptionType.SERVICE_REMOVED, 3)));

    ServiceInterval interval = cache.getIntervals("service_1", calendar, calendarDates);

    assertThat(interval != null).isTrue();
    assertThat(interval.getGaps().size()).isEqualTo(1);
  }

  @Test
  public void caching_sameInstanceReturnedOnRepeatedCalls() {
    ServiceIntervalCache cache = new ServiceIntervalCache();
    GtfsCalendarTableContainer calendar =
        calendarTable(ImmutableList.of(buildCalendar("service_1", "20240101", "20240131", 1)));
    GtfsCalendarDateTableContainer calendarDates = calendarDateTable(ImmutableList.of());

    ServiceInterval first = cache.getIntervals("service_1", calendar, calendarDates);
    ServiceInterval second = cache.getIntervals("service_1", calendar, calendarDates);

    assertThat(first).isSameInstanceAs(second);
  }

  @Test
  public void caching_builtOnce_subsequentCallIgnoresNewTables() {
    // After the cache is built, passing different tables should still return
    // the original cached result (i.e. the map is not rebuilt).
    ServiceIntervalCache cache = new ServiceIntervalCache();
    GtfsCalendarTableContainer originalCalendar =
        calendarTable(ImmutableList.of(buildCalendar("service_1", "20240101", "20240131", 1)));
    GtfsCalendarDateTableContainer emptyDates = calendarDateTable(ImmutableList.of());

    // First call — builds the cache with service_1.
    ServiceInterval first = cache.getIntervals("service_1", originalCalendar, emptyDates);

    // Second call with a different calendar that has service_2 — cache should NOT rebuild.
    GtfsCalendarTableContainer newCalendar =
        calendarTable(ImmutableList.of(buildCalendar("service_2", "20240201", "20240229", 1)));
    ServiceInterval shouldBeNull = cache.getIntervals("service_2", newCalendar, emptyDates);

    assertThat(first).isNotNull();
    assertThat(shouldBeNull).isNull(); // service_2 was not in the original build
  }
}
