package org.mobilitydata.gtfsvalidator.util;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDate;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;

/**
 * Lazily computes and caches a {@link ServiceInterval} for each {@code service_id} in the feed.
 *
 * <h2>Lifecycle</h2>
 *
 * <p>An empty instance is placed in {@link
 * org.mobilitydata.gtfsvalidator.validator.ValidationContext} before the feed is loaded:
 *
 * <pre>{@code
 * ValidationContext context = ValidationContext.builder()
 *     .set(ServiceIntervalCache.class, new ServiceIntervalCache())
 *     .build();
 * }</pre>
 *
 * <p>Validators declare only the cache as a constructor parameter — {@code DependencyResolver}
 * injects it from the context, and the cache builds itself transparently on first access:
 *
 * <pre>{@code
 * @Inject
 * public ServiceGapValidator(ServiceIntervalCache cache) {
 *   this.cache = cache;
 * }
 * }</pre>
 *
 * <h2>Thread safety</h2>
 *
 * <p>Built using double-checked locking on a {@code volatile} field. Safe to call {@link
 * #getIntervals} from multiple threads simultaneously.
 */
public class ServiceIntervalCache {

  /** Null until first access. Volatile for correct double-checked locking. */
  @Nullable private volatile ImmutableMap<String, ServiceInterval> intervalsByServiceId = null;

  /** Creates an empty cache. Place this instance in {@code ValidationContext}. */
  public ServiceIntervalCache() {}

  /**
   * Returns the {@link ServiceInterval} for the given {@code service_id}, building the internal map
   * on first call.
   *
   * @param serviceId the {@code service_id} to look up
   * @return the intervals, or {@code null} if the service ID is unknown
   */
  @Nullable
  public ServiceInterval getIntervals(
      String serviceId,
      GtfsCalendarTableContainer calendarTable,
      GtfsCalendarDateTableContainer calendarDateTable) {
    return getOrBuild(calendarTable, calendarDateTable).get(serviceId);
  }

  // ---------------------------------------------------------------------------
  // Internal
  // ---------------------------------------------------------------------------

  private ImmutableMap<String, ServiceInterval> getOrBuild(
      @Nullable GtfsCalendarTableContainer calendarTable,
      @Nullable GtfsCalendarDateTableContainer calendarDateTable) {
    if (intervalsByServiceId != null) {
      return intervalsByServiceId; // Already built, no locking needed.
    }
    synchronized (this) {
      if (intervalsByServiceId == null) {
        intervalsByServiceId = build(calendarTable, calendarDateTable);
      }
      return intervalsByServiceId;
    }
  }

  private static ImmutableMap<String, ServiceInterval> build(
      @Nullable GtfsCalendarTableContainer calendarTable,
      @Nullable GtfsCalendarDateTableContainer calendarDateTable) {

    Map<String, ServiceInterval> mutable =
        new HashMap<>(); // key: service_id, value: interval for that service_id

    // Phase 1: calendar.txt
    if (calendarTable != null) {
      for (GtfsCalendar calendar : calendarTable.getEntities()) {
        ServiceInterval interval =
            mutable.computeIfAbsent(calendar.serviceId(), id -> new ServiceInterval());
        byte pattern =
            ServicePeriod.weeklyPatternFromMTWTFSS(
                calendar.monday().getNumber(),
                calendar.tuesday().getNumber(),
                calendar.wednesday().getNumber(),
                calendar.thursday().getNumber(),
                calendar.friday().getNumber(),
                calendar.saturday().getNumber(),
                calendar.sunday().getNumber());
        interval.addInterval(
            calendar.startDate().getLocalDate(), calendar.endDate().getLocalDate(), pattern);
      }
    }

    // Phase 2: calendar_dates.txt.
    if (calendarDateTable != null) {
      for (GtfsCalendarDate calendarDate : calendarDateTable.getEntities()) {
        ServiceInterval interval =
            mutable.computeIfAbsent(calendarDate.serviceId(), id -> new ServiceInterval());
        switch (calendarDate.exceptionType()) {
          case SERVICE_ADDED:
            interval.addDate(calendarDate.date().getLocalDate());
            break;
          case SERVICE_REMOVED:
            interval.removeDate(calendarDate.date().getLocalDate());
            break;
          default:
            // Unknown exception type
            break;
        }
      }
    }

    return ImmutableMap.copyOf(mutable);
  }
}
