package org.mobilitydata.gtfsvalidator.table;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Distinct values of a column. The code of a value is its position in order of first appearance, so
 * that a column of repeated values can be kept as an {@code int[]}.
 */
public final class ColumnDictionary<V> {
  // Same initial capacity as ArrayListMultimap, so that values iterate in the same order.
  @Nullable private Map<V, Integer> codes = Maps.newHashMapWithExpectedSize(12);
  private final List<V> values = new ArrayList<>();

  /** Returns the code of the value, adding it if it is new. */
  public int code(V value) {
    Integer c = codes.get(value);
    if (c == null) {
      c = values.size();
      codes.put(value, c);
      values.add(value);
    }
    return c;
  }

  public V value(int code) {
    return values.get(code);
  }

  /** Returns the code of the value, or -1 if it is not in the column. */
  public int find(Object value) {
    Integer c = codes.get(value);
    return c == null ? -1 : c;
  }

  public int size() {
    return values.size();
  }

  /** Returns the code of each value. */
  public Map<V, Integer> codes() {
    return Collections.unmodifiableMap(codes);
  }

  /** Releases the codes by value. Afterwards, only {@link #value} and {@link #size} can be used. */
  public void compact() {
    codes = null;
  }
}
