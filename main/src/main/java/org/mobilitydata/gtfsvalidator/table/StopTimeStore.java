package org.mobilitydata.gtfsvalidator.table;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.RandomAccess;
import javax.annotation.Nullable;
import org.mobilitydata.gtfsvalidator.notice.DuplicateKeyNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * Rows of stop_times.txt and their indices.
 *
 * <p>Once the table reaches {@code columnThreshold} rows, rows are kept as primitive columns and
 * {@link GtfsStopTime} objects are created on access. See docs/ARCHITECTURE.md.
 */
final class StopTimeStore extends AbstractList<GtfsStopTime> implements RandomAccess {
  static final int COLUMN_THRESHOLD =
      Integer.getInteger("gtfsvalidator.stopTimes.columnThreshold", 1_000_000);

  private static final int TRIP_ID = 1;
  private static final int ARRIVAL_TIME = 1 << 1;
  private static final int DEPARTURE_TIME = 1 << 2;
  private static final int STOP_ID = 1 << 3;
  private static final int LOCATION_GROUP_ID = 1 << 4;
  private static final int LOCATION_ID = 1 << 5;
  private static final int STOP_SEQUENCE = 1 << 6;
  private static final int STOP_HEADSIGN = 1 << 7;
  private static final int START_WINDOW = 1 << 8;
  private static final int END_WINDOW = 1 << 9;
  private static final int PICKUP_TYPE = 1 << 10;
  private static final int DROP_OFF_TYPE = 1 << 11;
  private static final int CONTINUOUS_PICKUP = 1 << 12;
  private static final int CONTINUOUS_DROP_OFF = 1 << 13;
  private static final int SHAPE_DIST_TRAVELED = 1 << 14;
  private static final int TIMEPOINT = 1 << 15;
  private static final int PICKUP_BOOKING_RULE_ID = 1 << 16;
  private static final int DROP_OFF_BOOKING_RULE_ID = 1 << 17;

  private final int columnThreshold;
  private int size;
  @Nullable private List<GtfsStopTime> objects = new ArrayList<>();

  private int[] csvRowNumber;
  private int[] present;
  private int[] trip;
  private int[] stop;
  private int[] stopSequence;
  private int[] arrivalTime;
  private int[] departureTime;
  private int[] startWindow;
  private int[] endWindow;
  private byte[] pickupType;
  private byte[] dropOffType;
  private byte[] continuousPickup;
  private byte[] continuousDropOff;
  private byte[] timepoint;
  private double[] shapeDistTraveled;
  private String[] locationGroupId;
  private String[] locationId;
  private String[] stopHeadsign;
  private String[] pickupBookingRuleId;
  private String[] dropOffBookingRuleId;

  // Same initial capacity as ArrayListMultimap, so that trips and stops iterate in the same order.
  private final Map<String, Integer> tripCodes = Maps.newHashMapWithExpectedSize(12);
  private final List<String> tripIds = new ArrayList<>();
  private final Map<String, Integer> stopCodes = Maps.newHashMapWithExpectedSize(12);
  private final List<String> stopIds = new ArrayList<>();

  private int[] tripStart;
  private int[] tripRows;
  private int[] stopStart;
  private int[] stopRows;

  StopTimeStore(int columnThreshold) {
    this.columnThreshold = columnThreshold;
  }

  boolean isColumnar() {
    return objects == null;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public GtfsStopTime get(int row) {
    Objects.checkIndex(row, size);
    return objects != null ? objects.get(row) : materialize(row);
  }

  @Override
  public boolean add(GtfsStopTime stopTime) {
    if (objects != null && size >= columnThreshold) {
      toColumns();
    }
    if (objects != null) {
      objects.add(stopTime);
    } else {
      append(stopTime);
    }
    size++;
    return true;
  }

  private void toColumns() {
    List<GtfsStopTime> rows = objects;
    objects = null;
    int capacity = Math.max(16, rows.size() * 2);
    csvRowNumber = new int[capacity];
    present = new int[capacity];
    trip = new int[capacity];
    stop = new int[capacity];
    stopSequence = new int[capacity];
    arrivalTime = new int[capacity];
    departureTime = new int[capacity];
    int n = size;
    size = 0;
    for (GtfsStopTime row : rows) {
      append(row);
      size++;
    }
    size = n;
  }

  private void append(GtfsStopTime e) {
    if (size == csvRowNumber.length) {
      resize(size * 2);
    }
    int r = size;
    int p = 0;
    csvRowNumber[r] = e.csvRowNumber();
    trip[r] = code(e.tripId(), tripCodes, tripIds);
    stop[r] = code(e.stopId(), stopCodes, stopIds);
    stopSequence[r] = e.stopSequence();
    if (e.hasTripId()) p |= TRIP_ID;
    if (e.hasStopId()) p |= STOP_ID;
    if (e.hasStopSequence()) p |= STOP_SEQUENCE;
    if (e.hasArrivalTime()) {
      p |= ARRIVAL_TIME;
      arrivalTime[r] = e.arrivalTime().getSecondsSinceMidnight();
    }
    if (e.hasDepartureTime()) {
      p |= DEPARTURE_TIME;
      departureTime[r] = e.departureTime().getSecondsSinceMidnight();
    }
    if (e.hasStartPickupDropOffWindow()) {
      p |= START_WINDOW;
      startWindow = ensure(startWindow);
      startWindow[r] = e.startPickupDropOffWindow().getSecondsSinceMidnight();
    }
    if (e.hasEndPickupDropOffWindow()) {
      p |= END_WINDOW;
      endWindow = ensure(endWindow);
      endWindow[r] = e.endPickupDropOffWindow().getSecondsSinceMidnight();
    }
    if (e.hasPickupType()) {
      p |= PICKUP_TYPE;
      pickupType = ensure(pickupType);
      pickupType[r] = (byte) e.pickupTypeValue();
    }
    if (e.hasDropOffType()) {
      p |= DROP_OFF_TYPE;
      dropOffType = ensure(dropOffType);
      dropOffType[r] = (byte) e.dropOffTypeValue();
    }
    if (e.hasContinuousPickup()) {
      p |= CONTINUOUS_PICKUP;
      continuousPickup = ensure(continuousPickup);
      continuousPickup[r] = (byte) e.continuousPickupValue();
    }
    if (e.hasContinuousDropOff()) {
      p |= CONTINUOUS_DROP_OFF;
      continuousDropOff = ensure(continuousDropOff);
      continuousDropOff[r] = (byte) e.continuousDropOffValue();
    }
    if (e.hasTimepoint()) {
      p |= TIMEPOINT;
      timepoint = ensure(timepoint);
      timepoint[r] = (byte) e.timepointValue();
    }
    if (e.hasShapeDistTraveled()) {
      p |= SHAPE_DIST_TRAVELED;
      if (shapeDistTraveled == null) {
        shapeDistTraveled = new double[csvRowNumber.length];
      }
      shapeDistTraveled[r] = e.shapeDistTraveled();
    }
    if (e.hasLocationGroupId()) {
      p |= LOCATION_GROUP_ID;
      locationGroupId = ensure(locationGroupId);
      locationGroupId[r] = e.locationGroupId();
    }
    if (e.hasLocationId()) {
      p |= LOCATION_ID;
      locationId = ensure(locationId);
      locationId[r] = e.locationId();
    }
    if (e.hasStopHeadsign()) {
      p |= STOP_HEADSIGN;
      stopHeadsign = ensure(stopHeadsign);
      stopHeadsign[r] = e.stopHeadsign();
    }
    if (e.hasPickupBookingRuleId()) {
      p |= PICKUP_BOOKING_RULE_ID;
      pickupBookingRuleId = ensure(pickupBookingRuleId);
      pickupBookingRuleId[r] = e.pickupBookingRuleId();
    }
    if (e.hasDropOffBookingRuleId()) {
      p |= DROP_OFF_BOOKING_RULE_ID;
      dropOffBookingRuleId = ensure(dropOffBookingRuleId);
      dropOffBookingRuleId[r] = e.dropOffBookingRuleId();
    }
    present[r] = p;
  }

  private GtfsStopTime materialize(int r) {
    int p = present[r];
    GtfsStopTime.Builder b = new GtfsStopTime.Builder();
    b.setCsvRowNumber(csvRowNumber[r]);
    if ((p & TRIP_ID) != 0) b.setTripId(tripIds.get(trip[r]));
    if ((p & STOP_ID) != 0) b.setStopId(stopIds.get(stop[r]));
    if ((p & STOP_SEQUENCE) != 0) b.setStopSequence(stopSequence[r]);
    if ((p & ARRIVAL_TIME) != 0) b.setArrivalTime(time(arrivalTime[r]));
    if ((p & DEPARTURE_TIME) != 0) b.setDepartureTime(time(departureTime[r]));
    if ((p & START_WINDOW) != 0) b.setStartPickupDropOffWindow(time(startWindow[r]));
    if ((p & END_WINDOW) != 0) b.setEndPickupDropOffWindow(time(endWindow[r]));
    if ((p & PICKUP_TYPE) != 0) b.setPickupType((int) pickupType[r]);
    if ((p & DROP_OFF_TYPE) != 0) b.setDropOffType((int) dropOffType[r]);
    if ((p & CONTINUOUS_PICKUP) != 0) b.setContinuousPickup((int) continuousPickup[r]);
    if ((p & CONTINUOUS_DROP_OFF) != 0) b.setContinuousDropOff((int) continuousDropOff[r]);
    if ((p & TIMEPOINT) != 0) b.setTimepoint((int) timepoint[r]);
    if ((p & SHAPE_DIST_TRAVELED) != 0) b.setShapeDistTraveled(shapeDistTraveled[r]);
    if ((p & LOCATION_GROUP_ID) != 0) b.setLocationGroupId(locationGroupId[r]);
    if ((p & LOCATION_ID) != 0) b.setLocationId(locationId[r]);
    if ((p & STOP_HEADSIGN) != 0) b.setStopHeadsign(stopHeadsign[r]);
    if ((p & PICKUP_BOOKING_RULE_ID) != 0) b.setPickupBookingRuleId(pickupBookingRuleId[r]);
    if ((p & DROP_OFF_BOOKING_RULE_ID) != 0) b.setDropOffBookingRuleId(dropOffBookingRuleId[r]);
    return b.build();
  }

  /** Builds the indices and reports duplicate keys. Must be called once, after the last add. */
  void finish(String filename, NoticeContainer noticeContainer) {
    int n = size;
    if (objects == null) {
      resize(n);
    }
    int[] tripOf = trip;
    int[] stopOf = stop;
    int[] sequenceOf = stopSequence;
    if (objects != null) {
      tripOf = new int[n];
      stopOf = new int[n];
      sequenceOf = new int[n];
      for (int r = 0; r < n; r++) {
        GtfsStopTime e = objects.get(r);
        tripOf[r] = code(e.tripId(), tripCodes, tripIds);
        stopOf[r] = code(e.stopId(), stopCodes, stopIds);
        sequenceOf[r] = e.stopSequence();
      }
    }
    tripStart = new int[tripIds.size() + 1];
    tripRows = groupRows(tripOf, tripStart);
    stopStart = new int[stopIds.size() + 1];
    stopRows = groupRows(stopOf, stopStart);

    // Within a trip, rows are sorted by stop_sequence, then by row.
    int longest = 0;
    for (int t = 0; t + 1 < tripStart.length; t++) {
      longest = Math.max(longest, tripStart[t + 1] - tripStart[t]);
    }
    long[] keys = new long[longest];
    List<Long> duplicates = new ArrayList<>();
    for (int t = 0; t + 1 < tripStart.length; t++) {
      int from = tripStart[t];
      int length = tripStart[t + 1] - from;
      for (int i = 0; i < length; i++) {
        int r = tripRows[from + i];
        keys[i] = ((long) sequenceOf[r] << 32) | r;
      }
      Arrays.sort(keys, 0, length);
      int first = 0;
      for (int i = 0; i < length; i++) {
        tripRows[from + i] = (int) keys[i];
        if (i > 0 && (int) (keys[i] >> 32) == (int) (keys[i - 1] >> 32)) {
          duplicates.add(((long) tripRows[from + i] << 32) | tripRows[from + first]);
        } else {
          first = i;
        }
      }
    }
    Collections.sort(duplicates);
    for (long duplicate : duplicates) {
      GtfsStopTime oldEntity = get((int) duplicate);
      GtfsStopTime newEntity = get((int) (duplicate >>> 32));
      noticeContainer.addValidationNotice(
          new DuplicateKeyNotice(
              filename,
              oldEntity.csvRowNumber(),
              newEntity.csvRowNumber(),
              definedKeys(oldEntity),
              definedValues(oldEntity)));
    }
  }

  List<GtfsStopTime> byTripId(String tripId) {
    Integer t = tripCodes.get(tripId);
    return t == null ? Collections.emptyList() : tripView(t);
  }

  Map<String, List<GtfsStopTime>> byTripIdAsMap() {
    return Maps.transformValues(tripCodes, this::tripView);
  }

  List<GtfsStopTime> byStopId(String stopId) {
    Integer s = stopCodes.get(stopId);
    return s == null ? Collections.emptyList() : new Rows(stopRows, stopStart[s], stopStart[s + 1]);
  }

  ListMultimap<String, GtfsStopTime> byTripIdMap() {
    ListMultimap<String, GtfsStopTime> map = ArrayListMultimap.create();
    tripCodes.forEach((tripId, t) -> map.putAll(tripId, tripView(t)));
    return map;
  }

  ListMultimap<String, GtfsStopTime> byStopIdMap() {
    ListMultimap<String, GtfsStopTime> map = ArrayListMultimap.create();
    stopCodes.forEach((stopId, s) -> map.putAll(stopId, byStopId(stopId)));
    return map;
  }

  /** Returns the first row, in file order, with the given trip_id and stop_sequence. */
  Optional<GtfsStopTime> byTripIdAndStopSequence(String tripId, int sequence) {
    Integer t = tripCodes.get(tripId);
    if (t == null) {
      return Optional.empty();
    }
    for (GtfsStopTime stopTime : tripView(t)) {
      if (stopTime.stopSequence() == sequence) {
        return Optional.of(stopTime);
      }
    }
    return Optional.empty();
  }

  private List<GtfsStopTime> tripView(int t) {
    return new Rows(tripRows, tripStart[t], tripStart[t + 1]);
  }

  /** Rows of an index range. In columnar mode, each row is created once per list. */
  private final class Rows extends AbstractList<GtfsStopTime> implements RandomAccess {
    private final int[] rows;
    private final int from;
    private final int to;
    private GtfsStopTime[] cache;

    Rows(int[] rows, int from, int to) {
      this.rows = rows;
      this.from = from;
      this.to = to;
    }

    @Override
    public GtfsStopTime get(int i) {
      Objects.checkIndex(i, to - from);
      int r = rows[from + i];
      if (objects != null) {
        return objects.get(r);
      }
      if (cache == null) {
        cache = new GtfsStopTime[to - from];
      }
      if (cache[i] == null) {
        cache[i] = materialize(r);
      }
      return cache[i];
    }

    @Override
    public int size() {
      return to - from;
    }
  }

  private static int[] groupRows(int[] groupOf, int[] start) {
    int n = groupOf.length;
    int[] rows = new int[n];
    for (int r = 0; r < n; r++) {
      start[groupOf[r] + 1]++;
    }
    for (int g = 0; g + 1 < start.length; g++) {
      start[g + 1] += start[g];
    }
    int[] next = Arrays.copyOf(start, start.length);
    for (int r = 0; r < n; r++) {
      rows[next[groupOf[r]]++] = r;
    }
    return rows;
  }

  private static int code(String value, Map<String, Integer> codes, List<String> values) {
    Integer c = codes.get(value);
    if (c == null) {
      c = values.size();
      codes.put(value, c);
      values.add(value);
    }
    return c;
  }

  private static GtfsTime time(int secondsSinceMidnight) {
    return GtfsTime.fromSecondsSinceMidnight(secondsSinceMidnight);
  }

  private static String definedKeys(GtfsStopTime entity) {
    List<String> keys = new ArrayList<>();
    if (entity.hasTripId()) {
      keys.add(GtfsStopTime.TRIP_ID_FIELD_NAME);
    }
    if (entity.hasStopSequence()) {
      keys.add(GtfsStopTime.STOP_SEQUENCE_FIELD_NAME);
    }
    return String.join(",", keys);
  }

  private static Object definedValues(GtfsStopTime entity) {
    List<Object> values = new ArrayList<>();
    if (entity.hasTripId()) {
      values.add(entity.tripId());
    }
    if (entity.hasStopSequence()) {
      values.add(entity.stopSequence());
    }
    return values.stream().reduce((a, b) -> (a.toString() + "," + b.toString())).orElse(null);
  }

  private int[] ensure(int[] column) {
    return column != null ? column : new int[csvRowNumber.length];
  }

  private byte[] ensure(byte[] column) {
    return column != null ? column : new byte[csvRowNumber.length];
  }

  private String[] ensure(String[] column) {
    return column != null ? column : new String[csvRowNumber.length];
  }

  private void resize(int capacity) {
    csvRowNumber = Arrays.copyOf(csvRowNumber, capacity);
    present = Arrays.copyOf(present, capacity);
    trip = Arrays.copyOf(trip, capacity);
    stop = Arrays.copyOf(stop, capacity);
    stopSequence = Arrays.copyOf(stopSequence, capacity);
    arrivalTime = Arrays.copyOf(arrivalTime, capacity);
    departureTime = Arrays.copyOf(departureTime, capacity);
    startWindow = startWindow == null ? null : Arrays.copyOf(startWindow, capacity);
    endWindow = endWindow == null ? null : Arrays.copyOf(endWindow, capacity);
    pickupType = pickupType == null ? null : Arrays.copyOf(pickupType, capacity);
    dropOffType = dropOffType == null ? null : Arrays.copyOf(dropOffType, capacity);
    continuousPickup = continuousPickup == null ? null : Arrays.copyOf(continuousPickup, capacity);
    continuousDropOff =
        continuousDropOff == null ? null : Arrays.copyOf(continuousDropOff, capacity);
    timepoint = timepoint == null ? null : Arrays.copyOf(timepoint, capacity);
    shapeDistTraveled =
        shapeDistTraveled == null ? null : Arrays.copyOf(shapeDistTraveled, capacity);
    locationGroupId = locationGroupId == null ? null : Arrays.copyOf(locationGroupId, capacity);
    locationId = locationId == null ? null : Arrays.copyOf(locationId, capacity);
    stopHeadsign = stopHeadsign == null ? null : Arrays.copyOf(stopHeadsign, capacity);
    pickupBookingRuleId =
        pickupBookingRuleId == null ? null : Arrays.copyOf(pickupBookingRuleId, capacity);
    dropOffBookingRuleId =
        dropOffBookingRuleId == null ? null : Arrays.copyOf(dropOffBookingRuleId, capacity);
  }
}
