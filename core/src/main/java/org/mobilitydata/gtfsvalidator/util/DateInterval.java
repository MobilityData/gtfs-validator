package org.mobilitydata.gtfsvalidator.util;

import com.google.common.base.Preconditions;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * An immutable, inclusive date range [start, end].
 *
 * <p>Represents a contiguous block of calendar days. Used as the building block of {@link
 * ServiceIntervals} to represent runs of active service days without enumerating every individual
 * date.
 */
public final class DateInterval {

  private final LocalDate start;
  private final LocalDate end;

  /**
   * Creates a new interval.
   *
   * @param start the first active date, inclusive
   * @param end the last active date, inclusive; must be >= start
   */
  public DateInterval(LocalDate start, LocalDate end) {
    Preconditions.checkNotNull(start, "start must not be null");
    Preconditions.checkNotNull(end, "end must not be null");
    Preconditions.checkArgument(
        !start.isAfter(end), "start (%s) must be before or equal to end (%s)", start, end);
    this.start = start;
    this.end = end;
  }

  /** Returns the first date of this interval, inclusive. */
  public LocalDate getStart() {
    return start;
  }

  /** Returns the last date of this interval, inclusive. */
  public LocalDate getEnd() {
    return end;
  }

  /**
   * Returns the number of days in this interval.
   *
   * <p>A single-day interval returns 1.
   */
  public long lengthInDays() {
    return ChronoUnit.DAYS.between(start, end) + 1;
  }

  /**
   * Returns the number of days in the gap between this interval and the next one.
   *
   * <p>The two intervals must not overlap and must not be adjacent. Returns 0 if they are adjacent
   * (i.e. {@code this.end.plusDays(1).equals(next.start)}).
   *
   * @param next the interval that starts after this one ends
   * @return number of days in the gap, 0 if adjacent
   */
  public long gapDaysUntil(DateInterval next) {
    Preconditions.checkArgument(
        !end.isAfter(next.start), "next interval must start after this one ends");
    return ChronoUnit.DAYS.between(end, next.start) - 1;
  }

  /** Returns true if this interval contains the given date. */
  public boolean contains(LocalDate date) {
    return !date.isBefore(start) && !date.isAfter(end);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DateInterval)) return false;
    DateInterval that = (DateInterval) o;
    return start.equals(that.start) && end.equals(that.end);
  }

  @Override
  public int hashCode() {
    return Objects.hash(start, end);
  }

  @Override
  public String toString() {
    return "[" + start + ", " + end + "]";
  }
}
