package org.mobilitydata.gtfsvalidator.table;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.DuplicateKeyNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@RunWith(JUnit4.class)
public class StopTimeStoreTest {

  private static GtfsStopTime stopTime(int row, String tripId, String stopId, int sequence) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(row)
        .setTripId(tripId)
        .setStopId(stopId)
        .setStopSequence(sequence)
        .setArrivalTime(GtfsTime.fromSecondsSinceMidnight(row * 60))
        .build();
  }

  private static GtfsStopTime fullStopTime(int row) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(row)
        .setTripId("t" + row % 3)
        .setStopId("s" + row % 5)
        .setStopSequence(row)
        .setArrivalTime(GtfsTime.fromSecondsSinceMidnight(100_000 + row))
        .setDepartureTime(GtfsTime.fromSecondsSinceMidnight(100_001 + row))
        .setLocationGroupId("group")
        .setLocationId("location")
        .setStopHeadsign("headsign" + row)
        .setStartPickupDropOffWindow(GtfsTime.fromSecondsSinceMidnight(row))
        .setEndPickupDropOffWindow(GtfsTime.fromSecondsSinceMidnight(row + 1))
        .setPickupType(row % 4)
        .setDropOffType(7)
        .setContinuousPickup(0)
        .setContinuousDropOff(2)
        .setShapeDistTraveled(row * 1.5)
        .setTimepoint(0)
        .setPickupBookingRuleId("pickup")
        .setDropOffBookingRuleId("dropoff")
        .build();
  }

  private static List<Object> fields(GtfsStopTime e) {
    return ImmutableList.of(
        e.csvRowNumber(),
        e.hasTripId() + e.tripId(),
        e.hasStopId() + e.stopId(),
        e.hasStopSequence() + ":" + e.stopSequence(),
        e.hasArrivalTime() + e.arrivalTime().toHHMMSS(),
        e.hasDepartureTime() + e.departureTime().toHHMMSS(),
        e.hasLocationGroupId() + e.locationGroupId(),
        e.hasLocationId() + e.locationId(),
        e.hasStopHeadsign() + e.stopHeadsign(),
        e.hasStartPickupDropOffWindow() + e.startPickupDropOffWindow().toHHMMSS(),
        e.hasEndPickupDropOffWindow() + e.endPickupDropOffWindow().toHHMMSS(),
        e.hasPickupType() + ":" + e.pickupTypeValue(),
        e.hasDropOffType() + ":" + e.dropOffTypeValue(),
        e.hasContinuousPickup() + ":" + e.continuousPickupValue(),
        e.hasContinuousDropOff() + ":" + e.continuousDropOffValue(),
        e.hasShapeDistTraveled() + ":" + e.shapeDistTraveled(),
        e.hasTimepoint() + ":" + e.timepointValue(),
        e.hasPickupBookingRuleId() + e.pickupBookingRuleId(),
        e.hasDropOffBookingRuleId() + e.dropOffBookingRuleId());
  }

  private static List<List<Object>> fields(List<GtfsStopTime> stopTimes) {
    return stopTimes.stream().map(StopTimeStoreTest::fields).collect(Collectors.toList());
  }

  private static StopTimeStore store(int threshold, List<GtfsStopTime> rows, NoticeContainer n) {
    StopTimeStore store = new StopTimeStore(threshold);
    store.addAll(rows);
    store.finish(GtfsStopTime.FILENAME, n);
    return store;
  }

  @Test
  public void columns_keepEveryFieldAndItsPresence() {
    List<GtfsStopTime> rows = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      rows.add(
          i % 2 == 0
              ? fullStopTime(i + 2)
              : new GtfsStopTime.Builder().setCsvRowNumber(i + 2).build());
    }

    StopTimeStore columns = store(0, rows, new NoticeContainer());

    assertThat(columns.isColumnar()).isTrue();
    assertThat(fields(columns)).isEqualTo(fields(rows));
  }

  @Test
  public void add_switchesToColumnsAtThreshold() {
    StopTimeStore store = new StopTimeStore(3);
    List<GtfsStopTime> rows = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      rows.add(fullStopTime(i + 2));
      store.add(rows.get(i));
    }
    assertThat(store.isColumnar()).isFalse();

    rows.add(fullStopTime(5));
    store.add(rows.get(3));
    store.finish(GtfsStopTime.FILENAME, new NoticeContainer());

    assertThat(store.isColumnar()).isTrue();
    assertThat(fields(store)).isEqualTo(fields(rows));
  }

  @Test
  public void indices_sameInBothModes() {
    List<GtfsStopTime> rows = new ArrayList<>();
    ListMultimap<String, GtfsStopTime> byTrip = ArrayListMultimap.create();
    ListMultimap<String, GtfsStopTime> byStop = ArrayListMultimap.create();
    for (int i = 0; i < 500; i++) {
      // Sequences go down within a trip and some repeat, so sorting and ties are exercised.
      GtfsStopTime stopTime = stopTime(i + 2, "trip" + (i * 7919 % 97), "stop" + i % 13, -i / 3);
      rows.add(stopTime);
      byTrip.put(stopTime.tripId(), stopTime);
      byStop.put(stopTime.stopId(), stopTime);
    }
    for (String tripId : byTrip.keySet()) {
      byTrip.get(tripId).sort((a, b) -> Integer.compare(a.stopSequence(), b.stopSequence()));
    }

    for (int threshold : new int[] {0, Integer.MAX_VALUE}) {
      StopTimeStore store = store(threshold, rows, new NoticeContainer());

      assertThat(store.byTripIdAsMap().keySet())
          .containsExactlyElementsIn(byTrip.keySet())
          .inOrder();
      for (Map.Entry<String, List<GtfsStopTime>> trip : store.byTripIdAsMap().entrySet()) {
        assertThat(fields(trip.getValue())).isEqualTo(fields(byTrip.get(trip.getKey())));
        assertThat(fields(store.byTripId(trip.getKey()))).isEqualTo(fields(trip.getValue()));
      }
      for (String stopId : byStop.keySet()) {
        assertThat(fields(store.byStopId(stopId))).isEqualTo(fields(byStop.get(stopId)));
      }
      assertThat(store.byTripIdMap().keySet()).containsExactlyElementsIn(byTrip.keySet()).inOrder();
      assertThat(store.byStopIdMap().keySet()).containsExactlyElementsIn(byStop.keySet()).inOrder();
      assertThat(store.byTripId("unknown")).isEmpty();
      assertThat(store.byStopId("unknown")).isEmpty();
    }
  }

  @Test
  public void byTripId_columns_returnsTheSameObjectOnEachGet() {
    StopTimeStore store =
        store(
            0, List.of(stopTime(2, "t", "s", 1), stopTime(3, "t", "s", 2)), new NoticeContainer());

    List<GtfsStopTime> trip = store.byTripId("t");

    assertThat(trip.get(1)).isSameInstanceAs(trip.get(1));
  }

  @Test
  public void duplicateKeys_reportFirstOccurrenceInFileOrder() {
    List<GtfsStopTime> rows =
        List.of(
            stopTime(2, "t1", "s", 1),
            stopTime(3, "t2", "s", 1),
            stopTime(4, "t1", "s", 1),
            stopTime(5, "t1", "s", 2),
            stopTime(6, "t1", "s", 1));

    for (int threshold : new int[] {0, Integer.MAX_VALUE}) {
      NoticeContainer notices = new NoticeContainer();
      store(threshold, rows, notices);

      assertThat(notices.getValidationNotices())
          .containsExactly(
              new DuplicateKeyNotice("stop_times.txt", 2, 4, "trip_id,stop_sequence", "t1,1"),
              new DuplicateKeyNotice("stop_times.txt", 2, 6, "trip_id,stop_sequence", "t1,1"))
          .inOrder();
    }
  }

  @Test
  public void byTripIdAndStopSequence_returnsFirstRowInFileOrder() {
    List<GtfsStopTime> rows =
        List.of(stopTime(2, "t", "a", 2), stopTime(3, "t", "b", 1), stopTime(4, "t", "c", 1));

    for (int threshold : new int[] {0, Integer.MAX_VALUE}) {
      StopTimeStore store = store(threshold, rows, new NoticeContainer());

      assertThat(store.byTripIdAndStopSequence("t", 1).get().stopId()).isEqualTo("b");
      assertThat(store.byTripIdAndStopSequence("t", 3)).isEqualTo(Optional.empty());
      assertThat(store.byTripIdAndStopSequence("x", 1)).isEqualTo(Optional.empty());
    }
  }
}
