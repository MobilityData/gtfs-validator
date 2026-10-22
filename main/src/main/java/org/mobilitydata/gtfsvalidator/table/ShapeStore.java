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

/**
 * Rows of shapes.txt and their index by shape_id.
 *
 * <p>Once the table reaches {@code columnThreshold} rows, rows are kept as primitive columns and
 * {@link GtfsShape} objects are created on access. See docs/ARCHITECTURE.md.
 */
final class ShapeStore extends AbstractList<GtfsShape> implements RandomAccess {
  static final int COLUMN_THRESHOLD =
      Integer.getInteger("gtfsvalidator.shapes.columnThreshold", 1_000_000);

  private static final int SHAPE_ID = 1;
  private static final int LAT = 1 << 1;
  private static final int LON = 1 << 2;
  private static final int SEQUENCE = 1 << 3;
  private static final int DIST = 1 << 4;

  private final int columnThreshold;
  private int size;
  @Nullable private List<GtfsShape> objects = new ArrayList<>();

  private int[] csvRowNumber;
  private byte[] present;
  private int[] shape;
  private double[] lat;
  private double[] lon;
  private int[] sequence;
  private double[] distTraveled;

  // Same initial capacity as ArrayListMultimap, so that shapes iterate in the same order.
  private final Map<String, Integer> shapeCodes = Maps.newHashMapWithExpectedSize(12);
  private final List<String> shapeIds = new ArrayList<>();

  private static final ThreadLocal<GtfsShape.Builder> BUILDER =
      ThreadLocal.withInitial(GtfsShape.Builder::new);

  private int[] shapeStart;
  private int[] shapeRows;

  ShapeStore(int columnThreshold) {
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
  public GtfsShape get(int row) {
    Objects.checkIndex(row, size);
    return objects != null ? objects.get(row) : materialize(row);
  }

  @Override
  public boolean add(GtfsShape shapePoint) {
    if (objects != null && size >= columnThreshold) {
      toColumns();
    }
    if (objects != null) {
      objects.add(shapePoint);
    } else {
      append(shapePoint);
    }
    size++;
    return true;
  }

  private void toColumns() {
    List<GtfsShape> rows = objects;
    objects = null;
    int capacity = Math.max(16, rows.size() * 2);
    csvRowNumber = new int[capacity];
    present = new byte[capacity];
    shape = new int[capacity];
    lat = new double[capacity];
    lon = new double[capacity];
    sequence = new int[capacity];
    int n = size;
    size = 0;
    for (GtfsShape row : rows) {
      append(row);
      size++;
    }
    size = n;
  }

  private void append(GtfsShape e) {
    if (size == csvRowNumber.length) {
      resize(size * 2);
    }
    int r = size;
    int p = 0;
    csvRowNumber[r] = e.csvRowNumber();
    shape[r] = code(e.shapeId());
    lat[r] = e.shapePtLat();
    lon[r] = e.shapePtLon();
    sequence[r] = e.shapePtSequence();
    if (e.hasShapeId()) p |= SHAPE_ID;
    if (e.hasShapePtLat()) p |= LAT;
    if (e.hasShapePtLon()) p |= LON;
    if (e.hasShapePtSequence()) p |= SEQUENCE;
    if (e.hasShapeDistTraveled()) {
      p |= DIST;
      if (distTraveled == null) {
        distTraveled = new double[csvRowNumber.length];
      }
      distTraveled[r] = e.shapeDistTraveled();
    }
    present[r] = (byte) p;
  }

  private GtfsShape materialize(int r) {
    int p = present[r];
    GtfsShape.Builder b = BUILDER.get();
    b.clear();
    b.setCsvRowNumber(csvRowNumber[r]);
    if ((p & SHAPE_ID) != 0) b.setShapeId(shapeIds.get(shape[r]));
    if ((p & LAT) != 0) b.setShapePtLat(lat[r]);
    if ((p & LON) != 0) b.setShapePtLon(lon[r]);
    if ((p & SEQUENCE) != 0) b.setShapePtSequence(sequence[r]);
    if ((p & DIST) != 0) b.setShapeDistTraveled(distTraveled[r]);
    return b.build();
  }

  /** Builds the index and reports duplicate keys. Must be called once, after the last add. */
  void finish(String filename, NoticeContainer noticeContainer) {
    int n = size;
    if (objects == null) {
      resize(n);
    }
    int[] shapeOf = shape;
    int[] sequenceOf = sequence;
    if (objects != null) {
      shapeOf = new int[n];
      sequenceOf = new int[n];
      for (int r = 0; r < n; r++) {
        GtfsShape e = objects.get(r);
        shapeOf[r] = code(e.shapeId());
        sequenceOf[r] = e.shapePtSequence();
      }
    }
    shapeStart = new int[shapeIds.size() + 1];
    for (int r = 0; r < n; r++) {
      shapeStart[shapeOf[r] + 1]++;
    }
    int longest = 0;
    for (int s = 0; s + 1 < shapeStart.length; s++) {
      longest = Math.max(longest, shapeStart[s + 1]);
      shapeStart[s + 1] += shapeStart[s];
    }
    int[] next = Arrays.copyOf(shapeStart, shapeStart.length);
    shapeRows = new int[n];
    for (int r = 0; r < n; r++) {
      shapeRows[next[shapeOf[r]]++] = r;
    }

    // Within a shape, rows are sorted by shape_pt_sequence, then by row.
    long[] keys = new long[longest];
    List<Long> duplicates = new ArrayList<>();
    for (int s = 0; s + 1 < shapeStart.length; s++) {
      int from = shapeStart[s];
      int length = shapeStart[s + 1] - from;
      for (int i = 0; i < length; i++) {
        int r = shapeRows[from + i];
        keys[i] = ((long) sequenceOf[r] << 32) | r;
      }
      Arrays.sort(keys, 0, length);
      int first = 0;
      for (int i = 0; i < length; i++) {
        shapeRows[from + i] = (int) keys[i];
        if (i > 0 && (int) (keys[i] >> 32) == (int) (keys[i - 1] >> 32)) {
          duplicates.add(((long) shapeRows[from + i] << 32) | shapeRows[from + first]);
        } else {
          first = i;
        }
      }
    }
    Collections.sort(duplicates);
    for (long duplicate : duplicates) {
      GtfsShape oldEntity = get((int) duplicate);
      GtfsShape newEntity = get((int) (duplicate >>> 32));
      noticeContainer.addValidationNotice(
          new DuplicateKeyNotice(
              filename,
              oldEntity.csvRowNumber(),
              newEntity.csvRowNumber(),
              definedKeys(oldEntity),
              definedValues(oldEntity)));
    }
  }

  List<GtfsShape> byShapeId(String shapeId) {
    Integer s = shapeCodes.get(shapeId);
    return s == null ? Collections.emptyList() : shapeView(s);
  }

  Map<String, List<GtfsShape>> byShapeIdAsMap() {
    return Maps.transformValues(shapeCodes, this::shapeView);
  }

  ListMultimap<String, GtfsShape> byShapeIdMap() {
    ListMultimap<String, GtfsShape> map = ArrayListMultimap.create();
    shapeCodes.forEach((shapeId, s) -> map.putAll(shapeId, shapeView(s)));
    return map;
  }

  /** Returns the first row, in file order, with the given shape_id and shape_pt_sequence. */
  Optional<GtfsShape> byShapeIdAndSequence(String shapeId, int shapePtSequence) {
    for (GtfsShape shapePoint : byShapeId(shapeId)) {
      if (shapePoint.shapePtSequence() == shapePtSequence) {
        return Optional.of(shapePoint);
      }
    }
    return Optional.empty();
  }

  private List<GtfsShape> shapeView(int s) {
    return new Rows(shapeStart[s], shapeStart[s + 1]);
  }

  /** Rows of a shape. In columnar mode, each row is created once per list. */
  private final class Rows extends AbstractList<GtfsShape> implements RandomAccess {
    private final int from;
    private final int to;
    private GtfsShape[] cache;

    Rows(int from, int to) {
      this.from = from;
      this.to = to;
    }

    @Override
    public GtfsShape get(int i) {
      Objects.checkIndex(i, to - from);
      int r = shapeRows[from + i];
      if (objects != null) {
        return objects.get(r);
      }
      if (cache == null) {
        cache = new GtfsShape[to - from];
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

  private int code(String shapeId) {
    Integer c = shapeCodes.get(shapeId);
    if (c == null) {
      c = shapeIds.size();
      shapeCodes.put(shapeId, c);
      shapeIds.add(shapeId);
    }
    return c;
  }

  private static String definedKeys(GtfsShape entity) {
    List<String> keys = new ArrayList<>();
    if (entity.hasShapeId()) {
      keys.add(GtfsShape.SHAPE_ID_FIELD_NAME);
    }
    if (entity.hasShapePtSequence()) {
      keys.add(GtfsShape.SHAPE_PT_SEQUENCE_FIELD_NAME);
    }
    return String.join(",", keys);
  }

  private static Object definedValues(GtfsShape entity) {
    List<Object> values = new ArrayList<>();
    if (entity.hasShapeId()) {
      values.add(entity.shapeId());
    }
    if (entity.hasShapePtSequence()) {
      values.add(entity.shapePtSequence());
    }
    return values.stream().reduce((a, b) -> (a.toString() + "," + b.toString())).orElse(null);
  }

  private void resize(int capacity) {
    csvRowNumber = Arrays.copyOf(csvRowNumber, capacity);
    present = Arrays.copyOf(present, capacity);
    shape = Arrays.copyOf(shape, capacity);
    lat = Arrays.copyOf(lat, capacity);
    lon = Arrays.copyOf(lon, capacity);
    sequence = Arrays.copyOf(sequence, capacity);
    distTraveled = distTraveled == null ? null : Arrays.copyOf(distTraveled, capacity);
  }
}
