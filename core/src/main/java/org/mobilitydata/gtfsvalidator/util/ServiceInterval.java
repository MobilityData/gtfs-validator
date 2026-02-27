package org.mobilitydata.gtfsvalidator.util;

import com.google.common.base.Preconditions;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Maintains a sorted, normalized set of non-overlapping {@link DateInterval}s representing all
 * active service days for a single {@code service_id}.
 *
 * <p>This class is <em>mutable</em> by design: it is intended to be built up incrementally during
 * feed loading first by calling {@link #addInterval} for each {@code calendar.txt} row, then by
 * calling {@link #addDate} / {@link #removeDate} for each matching {@code calendar_dates.txt} row.
 * Once construction is complete the read-only methods ({@link #getIntervals}, {@link #getGaps},
 * {@link #maxGapInDays}) can be used for validation.
 *
 * <h2>Internal representation</h2>
 *
 * <p>Intervals are stored in a {@link TreeMap}{@code <LocalDate, LocalDate>} mapping each interval
 * start date to its end date. This allows O(log n) floor/ceiling lookups needed for efficient
 * merging and splitting.
 *
 * <h2>Normalization invariant</h2>
 *
 * <p>At all times the map satisfies:
 *
 * <ul>
 *   <li>No two intervals overlap.
 *   <li>No two intervals are adjacent (they would have been merged).
 *   <li>All intervals are non-empty ({@code start <= end}).
 * </ul>
 *
 * <h2>Performance</h2>
 *
 * <p>{@link #addInterval} walks the service range week-by-week (O(weeks)), not day-by-day, making
 * it significantly cheaper than enumerating every active date. {@link #addDate} and {@link
 * #removeDate} are O(log n) where n is the current number of intervals.
 */
public class ServiceInterval {

  // Maps interval start -> interval end (both inclusive).
  private final TreeMap<LocalDate, LocalDate> intervals = new TreeMap<>();

  // -------------------------------------------------------------------------
  // Construction
  // -------------------------------------------------------------------------

  /**
   * Adds active dates from a {@code calendar.txt} entry.
   *
   * <p>Walks the range [{@code serviceStart}, {@code serviceEnd}] week-by-week and adds contiguous
   * runs of active weekdays as intervals. Adjacent or overlapping intervals produced by multiple
   * calls are automatically merged.
   *
   * <p>The {@code weeklyPattern} format matches {@link ServicePeriod}: the least-significant bit
   * represents Monday, bit 1 Tuesday, ..., bit 6 Sunday (format {@code 0b0SSFTWTM}).
   *
   * @param serviceStart first date of the calendar range, inclusive
   * @param serviceEnd last date of the calendar range, inclusive
   * @param weeklyPattern bitmask of active weekdays in {@code 0b0SSFTWTM} format
   */
  public void addInterval(LocalDate serviceStart, LocalDate serviceEnd, byte weeklyPattern) {
    Preconditions.checkNotNull(serviceStart, "serviceStart must not be null");
    Preconditions.checkNotNull(serviceEnd, "serviceEnd must not be null");
    Preconditions.checkArgument(
        !serviceStart.isAfter(serviceEnd),
        "serviceStart (%s) must be before or equal to serviceEnd (%s)",
        serviceStart,
        serviceEnd);

    if (weeklyPattern == 0) {
      // No active days at all — nothing to add.
      return;
    }

    // Walk day-by-day but emit intervals: track the start of a current active run.
    LocalDate runStart = null;
    LocalDate current = serviceStart;

    while (!current.isAfter(serviceEnd)) {
      boolean active = ServicePeriod.isIncludedInPattern(current.getDayOfWeek(), weeklyPattern);

      if (active) {
        if (runStart == null) {
          runStart = current;
        }
        // If this is the last day in range, close the run here.
        if (current.equals(serviceEnd)) {
          mergeIn(runStart, current);
          runStart = null;
        }
      } else {
        if (runStart != null) {
          // Close the run just before this inactive day.
          mergeIn(runStart, current.minusDays(1));
          runStart = null;
        }
      }
      current = current.plusDays(1);
    }
  }

  /**
   * Adds a single date as an active day (from a {@code calendar_dates.txt} exception with {@code
   * exception_type=1}).
   *
   * <p>If the date is adjacent to or overlaps an existing interval, the intervals are merged.
   *
   * @param date the date to add
   */
  public void addDate(LocalDate date) {
    Preconditions.checkNotNull(date, "date must not be null");
    mergeIn(date, date);
  }

  /**
   * Removes a single date from the active days (from a {@code calendar_dates.txt} exception with
   * {@code exception_type=2}).
   *
   * <p>If the date falls in the middle of an existing interval, that interval is split into two. If
   * it falls at the boundary, the interval is trimmed. If it does not intersect any interval, this
   * is a no-op.
   *
   * @param date the date to remove
   */
  public void removeDate(LocalDate date) {
    Preconditions.checkNotNull(date, "date must not be null");

    // Find the interval that could contain this date (the one with the largest start <= date).
    Map.Entry<LocalDate, LocalDate> entry = intervals.floorEntry(date);
    if (entry == null) {
      return; // No interval starts on or before date.
    }

    LocalDate start = entry.getKey();
    LocalDate end = entry.getValue();

    if (date.isAfter(end)) {
      return; // date is beyond this interval's end — not contained.
    }

    // Remove the existing interval; we will re-insert up to two trimmed pieces.
    intervals.remove(start);

    // Left piece: [start, date-1] — only if start < date.
    if (start.isBefore(date)) {
      intervals.put(start, date.minusDays(1));
    }

    // Right piece: [date+1, end] — only if date < end.
    if (date.isBefore(end)) {
      intervals.put(date.plusDays(1), end);
    }

    // If start == end == date, both pieces are empty and the interval simply disappears.
  }

  // -------------------------------------------------------------------------
  // Read API
  // -------------------------------------------------------------------------

  /**
   * Returns an unmodifiable, chronologically sorted list of all active {@link DateInterval}s.
   *
   * @return sorted list of active intervals; empty if no active days have been added
   */
  public List<DateInterval> getIntervals() {
    List<DateInterval> result = new ArrayList<>(intervals.size());
    for (Map.Entry<LocalDate, LocalDate> entry : intervals.entrySet()) {
      result.add(new DateInterval(entry.getKey(), entry.getValue()));
    }
    return Collections.unmodifiableList(result);
  }

  /**
   * Returns an unmodifiable, chronologically sorted list of {@link DateInterval}s representing the
   * gaps <em>between</em> active service intervals.
   *
   * <p>A gap is the inactive span between two consecutive active intervals. Dates before the first
   * active interval or after the last are not considered gaps.
   *
   * @return sorted list of gap intervals; empty if there are zero or one active intervals
   */
  public List<DateInterval> getGaps() {
    if (intervals.size() < 2) {
      return Collections.emptyList();
    }

    List<DateInterval> gaps = new ArrayList<>();
    LocalDate prevEnd = null;

    for (Map.Entry<LocalDate, LocalDate> entry : intervals.entrySet()) {
      LocalDate start = entry.getKey();
      LocalDate end = entry.getValue();
      if (prevEnd != null) {
        // Gap is the day after prevEnd up to the day before start.
        LocalDate gapStart = prevEnd.plusDays(1);
        LocalDate gapEnd = start.minusDays(1);
        // By the normalization invariant, gapStart <= gapEnd is always true here
        // (adjacent intervals would have been merged), but guard anyway.
        if (!gapStart.isAfter(gapEnd)) {
          gaps.add(new DateInterval(gapStart, gapEnd));
        }
      }
      prevEnd = end;
    }

    return Collections.unmodifiableList(gaps);
  }

  /**
   * Returns the length in days of the longest gap between active service intervals.
   *
   * @return max gap in days, or 0 if there are zero or one active intervals
   */
  public long maxGapInDays() {
    return getGaps().stream().mapToLong(DateInterval::lengthInDays).max().orElse(0L);
  }

  /** Returns true if this service has no active days at all. */
  public boolean isEmpty() {
    return intervals.isEmpty();
  }

  /** Returns the first active date across all intervals, or {@code null} if empty. */
  public LocalDate firstActiveDate() {
    return intervals.isEmpty() ? null : intervals.firstKey();
  }

  /** Returns the last active date across all intervals, or {@code null} if empty. */
  public LocalDate lastActiveDate() {
    return intervals.isEmpty() ? null : intervals.lastEntry().getValue();
  }

  // -------------------------------------------------------------------------
  // Internal helpers
  // -------------------------------------------------------------------------

  /**
   * Merges the interval [newStart, newEnd] into the map, absorbing any existing intervals that
   * overlap or are adjacent to it.
   *
   * <p>After this call the normalization invariant is maintained.
   */
  private void mergeIn(LocalDate newStart, LocalDate newEnd) {
    // Expand newStart/newEnd to absorb any intervals that are adjacent or overlapping.
    //
    // "Adjacent" means newStart == existingEnd + 1 or newEnd == existingStart - 1.
    // We need to absorb those too, otherwise we'd violate the no-adjacent invariant.

    // Check if there is an interval immediately to the left (its end >= newStart - 1).
    Map.Entry<LocalDate, LocalDate> before = intervals.floorEntry(newStart);
    if (before != null && !before.getValue().isBefore(newStart.minusDays(1))) {
      // This interval overlaps or is adjacent: extend newStart leftward if needed,
      // extend newEnd rightward if needed, then remove it.
      newStart = before.getKey().isBefore(newStart) ? before.getKey() : newStart;
      newEnd = before.getValue().isAfter(newEnd) ? before.getValue() : newEnd;
      intervals.remove(before.getKey());
    }

    // Now absorb all intervals whose start falls within [newStart, newEnd+1]
    // (the +1 handles the adjacency on the right side).
    while (true) {
      Map.Entry<LocalDate, LocalDate> overlap = intervals.ceilingEntry(newStart);
      if (overlap == null || overlap.getKey().isAfter(newEnd.plusDays(1))) {
        break; // No more overlapping or adjacent intervals.
      }
      // Extend newEnd if this interval reaches further right.
      if (overlap.getValue().isAfter(newEnd)) {
        newEnd = overlap.getValue();
      }
      intervals.remove(overlap.getKey());
    }

    intervals.put(newStart, newEnd);
  }

  @Override
  public String toString() {
    return "ServiceInterval" + intervals.toString();
  }
}
