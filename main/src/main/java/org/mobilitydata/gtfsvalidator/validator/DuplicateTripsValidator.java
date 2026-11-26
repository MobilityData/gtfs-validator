package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

/**
 * Checks that no two trips from `trips.txt` have the same `service_id`, the same ordered list of
 * stops and the same arrival and departure times at each stop.
 *
 * <p>Trips without stop times, or with a stop time that has no `stop_id` (e.g. GTFS-Flex
 * location-based stop times), are skipped.
 *
 * <p>Generated notice: {@link DuplicateTripsNotice}.
 */
@GtfsValidator
public class DuplicateTripsValidator extends FileValidator {

  private static final HashFunction HASH_FUNCTION = Hashing.farmHashFingerprint64();

  /** Hashed value used for an absent arrival or departure time. */
  private static final int ABSENT_TIME = -1;

  private final GtfsTripTableContainer tripTable;

  private final GtfsStopTimeTableContainer stopTimeTable;

  @Inject
  DuplicateTripsValidator(
      GtfsTripTableContainer tripTable, GtfsStopTimeTableContainer stopTimeTable) {
    this.tripTable = tripTable;
    this.stopTimeTable = stopTimeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    // Trips are bucketed by a fingerprint of their service id and stop times. Within a bucket,
    // candidates are compared exactly, so a hash collision never produces a false notice.
    final Map<Long, List<GtfsTrip>> tripsByKey = new HashMap<>(tripTable.entityCount());
    for (GtfsTrip trip : tripTable.getEntities()) {
      // frequencies.txt is ignored on purpose: frequency-based trips are compared like others.
      final List<GtfsStopTime> stopTimes = stopTimeTable.byTripId(trip.tripId());
      if (stopTimes.isEmpty() || !stopTimes.stream().allMatch(GtfsStopTime::hasStopId)) {
        continue;
      }
      final List<GtfsTrip> bucket =
          tripsByKey.computeIfAbsent(getTripKey(trip, stopTimes), k -> new ArrayList<>());
      GtfsTrip duplicateOf = null;
      for (GtfsTrip candidate : bucket) {
        if (isDuplicate(trip, stopTimes, candidate)) {
          duplicateOf = candidate;
          break;
        }
      }
      if (duplicateOf != null) {
        noticeContainer.addValidationNotice(
            new DuplicateTripsNotice(
                duplicateOf,
                stopTimeTable.byTripId(duplicateOf.tripId()).get(0),
                trip,
                stopTimes.get(0)));
      } else {
        bucket.add(trip);
      }
    }
  }

  /** Generates a hash based on the service id and the ordered stop ids and times of a trip. */
  private static long getTripKey(GtfsTrip trip, List<GtfsStopTime> tripStopTimes) {
    final Hasher hasher = HASH_FUNCTION.newHasher().putUnencodedChars(trip.serviceId());
    // tripStopTimes is sorted by stop_sequence (see GtfsStopTimeTableContainer.byTripId).
    for (GtfsStopTime stopTime : tripStopTimes) {
      hasher
          .putChar('\0')
          .putUnencodedChars(stopTime.stopId())
          .putInt(arrivalSeconds(stopTime))
          .putInt(departureSeconds(stopTime));
    }
    return hasher.hash().asLong();
  }

  private boolean isDuplicate(GtfsTrip trip, List<GtfsStopTime> stopTimes, GtfsTrip other) {
    if (!trip.serviceId().equals(other.serviceId())) {
      return false;
    }
    final List<GtfsStopTime> otherStopTimes = stopTimeTable.byTripId(other.tripId());
    if (stopTimes.size() != otherStopTimes.size()) {
      return false;
    }
    for (int i = 0; i < stopTimes.size(); ++i) {
      final GtfsStopTime a = stopTimes.get(i);
      final GtfsStopTime b = otherStopTimes.get(i);
      if (!a.stopId().equals(b.stopId())
          || arrivalSeconds(a) != arrivalSeconds(b)
          || departureSeconds(a) != departureSeconds(b)) {
        return false;
      }
    }
    return true;
  }

  private static int arrivalSeconds(GtfsStopTime stopTime) {
    return stopTime.hasArrivalTime()
        ? stopTime.arrivalTime().getSecondsSinceMidnight()
        : ABSENT_TIME;
  }

  private static int departureSeconds(GtfsStopTime stopTime) {
    return stopTime.hasDepartureTime()
        ? stopTime.departureTime().getSecondsSinceMidnight()
        : ABSENT_TIME;
  }

  /**
   * Two trips have the same `service_id`, the same stops in the same order and the same arrival and
   * departure times.
   *
   * <p>Such trips are likely to be duplicates.
   *
   * <p>Example of bad data. In `trips.txt`, two trips have the same `service_id`:
   *
   * <table style="table-layout:auto; width:auto;">
   *   <tr>
   *     <th><code>trip_id</code></th>
   *     <th><code>service_id</code></th>
   *   </tr>
   *   <tr><td>trip1</td><td>WEEKDAY</td></tr>
   *   <tr><td>trip2</td><td>WEEKDAY</td></tr>
   * </table>
   *
   * <p>In `stop_times.txt`, they serve the same stops in the same order at the same times:
   *
   * <table style="table-layout:auto; width:auto;">
   *   <tr>
   *     <th><code>trip_id</code></th>
   *     <th><code>arrival_time</code></th>
   *     <th><code>departure_time</code></th>
   *     <th><code>stop_id</code></th>
   *     <th><code>stop_sequence</code></th>
   *   </tr>
   *   <tr><td>trip1</td><td>08:00:00</td><td>08:00:00</td><td>stopA</td><td>1</td></tr>
   *   <tr><td>trip1</td><td>08:10:00</td><td>08:10:00</td><td>stopB</td><td>2</td></tr>
   *   <tr><td>trip2</td><td>08:00:00</td><td>08:00:00</td><td>stopA</td><td>1</td></tr>
   *   <tr><td>trip2</td><td>08:10:00</td><td>08:10:00</td><td>stopB</td><td>2</td></tr>
   * </table>
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @FileRefs({GtfsTripSchema.class, GtfsStopTimeSchema.class}))
  static class DuplicateTripsNotice extends ValidationNotice {

    /** The id of the first trip. */
    private final String tripId1;

    /** The id of the duplicate trip. */
    private final String tripId2;

    /** The row number from `stop_times.txt` of the first stop time of the first trip. */
    private final int csvRowNumberTripId1FirstStopTime;

    /** The row number from `stop_times.txt` of the first stop time of the duplicate trip. */
    private final int csvRowNumberTripId2FirstStopTime;

    DuplicateTripsNotice(
        GtfsTrip trip1, GtfsStopTime firstStopTime1, GtfsTrip trip2, GtfsStopTime firstStopTime2) {
      this.tripId1 = trip1.tripId();
      this.tripId2 = trip2.tripId();
      this.csvRowNumberTripId1FirstStopTime = firstStopTime1.csvRowNumber();
      this.csvRowNumberTripId2FirstStopTime = firstStopTime2.csvRowNumber();
    }
  }
}
