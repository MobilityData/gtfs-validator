/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.collect.Comparators.max;
import static com.google.common.collect.Comparators.min;
import static java.lang.Math.max;

import com.google.auto.value.AutoValue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeMap;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequency;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

/** Functions for finding service start and end date based on actual trips running. */
public final class TripCalendarUtil {
  /** An interval between two dates, including bounds. */
  @AutoValue
  public abstract static class DateInterval {

    public static DateInterval create(LocalDate startDate, LocalDate endDate) {
      return new AutoValue_TripCalendarUtil_DateInterval(startDate, endDate);
    }

    public abstract LocalDate startDate();

    public abstract LocalDate endDate();
  }

  /**
   * Returns the interval over which trips are operating in the given feed.
   *
   * <p>Returns {@code Optional.empty()} if there are no trips, no services or trips actually do not
   * operate for any day.
   */
  public static Optional<DateInterval> computeServiceCoverage(
      Map<String, SortedSet<LocalDate>> serviceDates, GtfsTripTableContainer tripTable) {
    if (tripTable.getEntities().isEmpty() || serviceDates.isEmpty()) {
      return Optional.empty();
    }

    boolean noDates = true;
    LocalDate startDate = null;
    LocalDate endDate = null;

    for (GtfsTrip trip : tripTable.getEntities()) {
      SortedSet<LocalDate> tripDates = serviceDates.get(trip.serviceId());
      if (tripDates.isEmpty()) {
        continue;
      }
      if (noDates) {
        startDate = tripDates.first();
        endDate = tripDates.last();
        noDates = false;
      } else {
        startDate = min(startDate, tripDates.first());
        endDate = max(endDate, tripDates.last());
      }
    }
    return noDates ? Optional.empty() : Optional.of(DateInterval.create(startDate, endDate));
  }

  /**
   * Given a set of trips counts for each service date, computes a "majority" service coverage date
   * range for these trip counts.
   *
   * <p>This captures the date range when a significant number of trips are running, relative to the
   * max number of trips on a single day in the feed. This helps detect when a single trip has
   * caused a feed to appear "active" even though the bulk of service is no longer running.
   */
  public static Optional<DateInterval> computeMajorityServiceCoverage(
      NavigableMap<LocalDate, Integer> tripCountByDate) {
    if (tripCountByDate.isEmpty()) {
      return Optional.empty();
    }

    List<Integer> sortedCounts = new ArrayList<>(tripCountByDate.values());
    Collections.sort(sortedCounts);

    int maxServiceDateIndex =
        max(
            (int) (sortedCounts.size() * MAX_SERVICE_DATE_TRIP_COUNT_RATIO),
            sortedCounts.size() - MAX_SERVICE_DATE_TRIP_COUNT_LIMIT);
    final int majorityTripCountThreshold =
        (int) (MAJORITY_TRIP_COUNT_RATIO * sortedCounts.get(maxServiceDateIndex));
    LocalDate majorityStart = tripCountByDate.firstKey();
    LocalDate majorityEnd = tripCountByDate.lastKey();
    for (Map.Entry<LocalDate, Integer> dateAndTripCount : tripCountByDate.entrySet()) {
      if (dateAndTripCount.getValue() >= majorityTripCountThreshold) {
        majorityStart = dateAndTripCount.getKey();
        break;
      }
    }

    for (Map.Entry<LocalDate, Integer> dateAndTripCount :
        tripCountByDate.descendingMap().entrySet()) {
      if (dateAndTripCount.getValue() >= majorityTripCountThreshold) {
        majorityEnd = dateAndTripCount.getKey();
        break;
      }
    }
    return Optional.of(DateInterval.create(majorityStart, majorityEnd));
  }

  /**
   * Counts the number of trips active for each service date of the GTFS feed, as specified in the
   * GTFS schema, and returns the results as a map.
   *
   * <p>Frequency-based trips will automatically be expanded to capture the approximate number of
   * trips that will run during the trip's time interval.
   */
  public static NavigableMap<LocalDate, Integer> countTripsForEachServiceDate(
      Map<String, SortedSet<LocalDate>> serviceDates,
      GtfsTripTableContainer tripTable,
      GtfsFrequencyTableContainer frequencyTable) {
    NavigableMap<LocalDate, Integer> tripCountByDate = new TreeMap<>();
    if (tripTable.getEntities().isEmpty() || serviceDates.isEmpty()) {
      return tripCountByDate;
    }

    Map<String, Integer> tripCountByServiceId = new HashMap<>();
    for (GtfsTrip trip : tripTable.getEntities()) {
      tripCountByServiceId.put(
          trip.serviceId(),
          tripCountByServiceId.getOrDefault(trip.serviceId(), 0)
              + computeTripCount(trip.tripId(), frequencyTable));
    }

    for (Map.Entry<String, Integer> entry : tripCountByServiceId.entrySet()) {
      SortedSet<LocalDate> dates = serviceDates.get(entry.getKey());
      if (dates == null) {
        continue;
      }
      int tripCount = entry.getValue();
      for (LocalDate date : dates) {
        tripCountByDate.put(date, tripCountByDate.getOrDefault(date, 0) + tripCount);
      }
    }
    return tripCountByDate;
  }

  private static int computeTripCount(String tripId, GtfsFrequencyTableContainer frequencyTable) {
    List<GtfsFrequency> frequencies = frequencyTable.byTripId(tripId);
    if (frequencies.isEmpty()) {
      return 1;
    }
    int tripCount = 0;
    for (GtfsFrequency frequency : frequencies) {
      // At least one trip is running in the interval.
      ++tripCount;
      if (frequency.headwaySecs() > 0) {
        // Decrease interval by 1 since we already added a trip.
        tripCount +=
            (frequency.endTime().getSecondsSinceMidnight()
                    - frequency.startTime().getSecondsSinceMidnight()
                    - 1)
                / frequency.headwaySecs();
      }
    }
    return tripCount;
  }

  // When computing the "max trips" count for use in determining the major
  // service coverage dates, we don't take the absolute max since it might be an
  // outlier. Instead, we order the trip counts and pick the i-th count of N
  // counts and use that as a max.  We select i to be the max of (RATIO * N) and
  // (N - LIMIT), where RATIO=MAX_SERVICE_DATE_TRIP_COUNT_RATIO and
  // LIMIT=MAX_SERVICE_DATE_TRIP_COUNT_LIMIT.  The idea here is that we want to avoid
  // potential outliers by skipping some of the max trip counts(thus RATIO * N).
  // However, we also want to avoid the case where one infrequent route has 10x
  // the service period of the actual good service window, causing RATIO*N to
  // fall within the infrequent range (thus N - LIMIT).
  private static final double MAX_SERVICE_DATE_TRIP_COUNT_RATIO = 0.90;
  private static final int MAX_SERVICE_DATE_TRIP_COUNT_LIMIT = 30;

  // When computing the majority service coverage dates (aka dates when the
  // majority of trips are still running), we determine the max number of trips
  // active on a typical service date and consider a date to have "majority"
  // service if its ratio of active trips to the max count is above this ratio.
  private static final double MAJORITY_TRIP_COUNT_RATIO = 0.75;

  private TripCalendarUtil() {}
}
