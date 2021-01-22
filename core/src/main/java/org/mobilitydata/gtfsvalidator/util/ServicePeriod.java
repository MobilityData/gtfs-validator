package org.mobilitydata.gtfsvalidator.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Stores the service of a transit vehicle, in terms of a weekly pattern and exceptions.
 *
 * <p>This class is immutable.
 */
public class ServicePeriod {
  private final LocalDate serviceStart;
  private final LocalDate serviceEnd;
  private final byte weeklyPattern;
  private final Set<LocalDate> addedDays;
  private final Set<LocalDate> removedDays;

  /** Creates a service period for the given pattern, added and removed days. */
  public ServicePeriod(
      LocalDate serviceStart,
      LocalDate serviceEnd,
      byte weeklyPattern,
      Set<LocalDate> addedDays,
      Set<LocalDate> removedDays) {
    Preconditions.checkArgument(
        serviceStart.compareTo(serviceEnd) <= 0,
        "serviceStart must be before or equal to serviceEnd");
    this.serviceStart = Preconditions.checkNotNull(serviceStart);
    this.serviceEnd = Preconditions.checkNotNull(serviceEnd);
    this.weeklyPattern = weeklyPattern;
    this.addedDays = Preconditions.checkNotNull(addedDays);
    this.removedDays = Preconditions.checkNotNull(removedDays);
  }

  /**
   * Creates a service period from the given dates.
   *
   * <p>serviceStart and serviceEnd will be set to {@code LocalDate.EPOCH}.
   */
  public ServicePeriod(Set<LocalDate> dates) {
    this.serviceStart = LocalDate.EPOCH;
    this.serviceEnd = LocalDate.EPOCH;
    this.weeklyPattern = 0;
    this.addedDays = Preconditions.checkNotNull(dates);
    this.removedDays = ImmutableSet.of();
  }

  /**
   * Checks whether day in week is in pattern.
   *
   * @return whether the day is included in the pattern
   */
  static boolean isIncludedInPattern(DayOfWeek day, byte weeklyPattern) {
    // DayOfWeek.getValue() returns the day-of-week, from 1 (Monday) to 7 (Sunday).
    return ((weeklyPattern >> (day.getValue() - 1)) & 1) != 0;
  }

  /**
   * Returns a binary weekly pattern for the given days.
   *
   * <p>Each of the parameters must be either 0 or 1, where 0 means to skip the day and 1 to include
   * it. The parameters have type {@code int} instead of {@code boolean} because GTFS calendar.txt
   * uses integers there.
   *
   * <p>The returned pattern can be passed to {@code ServicePeriod} constructor.
   *
   * <p>Example. Monday, Tuesday, Wednesday and Saturday: {@code weeklyPatternFromMTWTFSS(1, 1, 1,
   * 0, 0, 1, 0) = 0b0100111}
   *
   * @param monday 1 to include or 0 to exclude Monday
   * @param tuesday 1 to include or 0 to exclude Tuesday
   * @param wednesday 1 to include or 0 to exclude Wednesday
   * @param thursday 1 to include or 0 to exclude Thursday
   * @param friday 1 to include or 0 to exclude Friday
   * @param saturday 1 to include or 0 to exclude Saturday
   * @param sunday 1 to include or 0 to exclude Sunday
   * @return weekly pattern for ServicePeriod, in the format 0b0SSFTWTM
   */
  public static byte weeklyPatternFromMTWTFSS(
      int monday, int tuesday, int wednesday, int thursday, int friday, int saturday, int sunday) {
    int[] days = {monday, tuesday, wednesday, thursday, friday, saturday, sunday};
    byte pattern = 0;
    for (int i = 0; i < days.length; ++i) {
      pattern |= (1 & days[i]) << i;
    }
    return pattern;
  }

  /**
   * Returns the set of active dates in the service period.
   *
   * <p>A {@link SortedSet} is returned to have all the dates sorted.
   *
   * @return a sorted set of all active dates
   */
  public SortedSet<LocalDate> toDates() {
    SortedSet<LocalDate> activeDates = new TreeSet<>();
    for (LocalDate current = serviceStart;
        current.compareTo(serviceEnd) <= 0;
        current = current.plusDays(1)) {
      if (isIncludedInPattern(current.getDayOfWeek(), weeklyPattern)) {
        activeDates.add(current);
      }
    }
    activeDates.addAll(addedDays);
    activeDates.removeAll(removedDays);
    return activeDates;
  }

  /**
   * Returns the first day of the weekly pattern, inclusive.
   *
   * @return the first day of the weekly pattern
   */
  public LocalDate getServiceStart() {
    return serviceStart;
  }

  /**
   * Returns the last day of the weekly pattern, inclusive. Must be greater than or equal to
   * serviceStart.
   *
   * @return the last day of the weekly pattern
   */
  public LocalDate getServiceEnd() {
    return serviceEnd;
  }

  /**
   * Returns the bitmask for the active week days.
   *
   * <p>The least significant bit represents Monday. The most significant bit is not used.
   *
   * <p>Example. {@code 0b01101101} is Mon, Wed, Thu, Sat and Sun.
   *
   * @return the bitmask for the active week days, in the format 0b0SSFTWTM
   */
  public byte getWeeklyPattern() {
    return weeklyPattern;
  }

  /**
   * Days that are explicitly active, regardless of the weekly pattern.
   *
   * <p>These dates can be outside of the range of the pattern (service start and service end).
   *
   * @return additional active days
   */
  public Set<LocalDate> getAddedDays() {
    return Collections.unmodifiableSet(addedDays);
  }

  /**
   * Days that are explicitly inactive, regardless of the weekly pattern.
   *
   * <p>The intersection between {@code removedDays} and {@code addedDays} could be non-empty, in
   * which case the removed have higher precedence.
   *
   * @return explictly inactive days
   */
  public Set<LocalDate> getRemovedDays() {
    return Collections.unmodifiableSet(removedDays);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServicePeriod that = (ServicePeriod) o;
    return weeklyPattern == that.weeklyPattern
        && serviceStart.equals(that.serviceStart)
        && serviceEnd.equals(that.serviceEnd)
        && addedDays.equals(that.addedDays)
        && removedDays.equals(that.removedDays);
  }

  @Override
  public int hashCode() {
    return Objects.hash(serviceStart, serviceEnd, weeklyPattern, addedDays, removedDays);
  }

  @Override
  public String toString() {
    return "ServicePeriod{"
        + "serviceStart="
        + serviceStart
        + ", serviceEnd="
        + serviceEnd
        + ", weeklyPattern="
        + weeklyPattern
        + ", addedDays="
        + addedDays
        + ", removedDays="
        + removedDays
        + '}';
  }
}
