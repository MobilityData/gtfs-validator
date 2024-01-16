package org.mobilitydata.gtfsvalidator.columns;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Base classes for column store implementation classes, as produced from {@link
 * org.mobilitydata.gtfsvalidator.annotation.ColumnStoreTypes} annotations.
 */
public class ColumnStoreBase {
  static <T> int reserveColumn(List<T[]> valuesByColumnIndex, Class<T> type, int initialCapacity) {
    T[] values = (T[]) Array.newInstance(type, initialCapacity);
    valuesByColumnIndex.add(values);
    return valuesByColumnIndex.size() - 1;
  }

  static void reservePrimitivePresence(
      List<byte[]> primitivePresenceByColumnIndex, int columnIndex, int initialCapacity) {
    int highOrderIndex = columnIndex / 8;
    if (highOrderIndex >= primitivePresenceByColumnIndex.size()) {
      primitivePresenceByColumnIndex.add(new byte[initialCapacity]);
    }
  }

  static boolean hasPrimitive(
      List<byte[]> primitivePresenceByColumnIndex, int columnIndex, int row) {
    if (columnIndex == -1) {
      return false;
    }
    int highOrderIndex = columnIndex / 8;
    int lowOrderIndex = columnIndex % 8;
    byte[] values = primitivePresenceByColumnIndex.get(highOrderIndex);
    return (values[row] & (1 << lowOrderIndex)) != 0;
  }

  static <T> boolean hasValue(List<T[]> valuesByColumnIndex, int columnIndex, int row) {
    if (columnIndex == -1) {
      return false;
    }
    T[] values = valuesByColumnIndex.get(columnIndex);
    return row < values.length && values[row] != null;
  }

  static <T> T getValue(List<T[]> valuesByColumnIndex, int columnIndex, int row, T defaultValue) {
    if (columnIndex == -1) {
      return defaultValue;
    }
    T[] values = valuesByColumnIndex.get(columnIndex);
    if (row >= values.length) {
      return defaultValue;
    }
    T value = values[row];
    return value != null ? value : defaultValue;
  }

  static void setPrimitivePresence(
      List<byte[]> primitivePresenceByColumnIndex, int columnIndex, int row) {
    int highOrderIndex = columnIndex / 8;
    int lowOrderIndex = columnIndex % 8;
    byte[] presenceValues = primitivePresenceByColumnIndex.get(highOrderIndex);
    if (presenceValues.length <= row) {
      int newSize = calculateNewCapacity(presenceValues.length, row + 1);
      presenceValues = Arrays.copyOf(presenceValues, newSize);
      primitivePresenceByColumnIndex.set(highOrderIndex, presenceValues);
    }
    presenceValues[row] |= (1 << lowOrderIndex);
  }

  static <T> void setValue(List<T[]> valuesByColumnIndex, int columnIndex, int row, T value) {
    T[] values = valuesByColumnIndex.get(columnIndex);
    if (values.length <= row) {
      int newSize = calculateNewCapacity(values.length, row + 1);
      values = Arrays.copyOf(values, newSize);
      valuesByColumnIndex.set(columnIndex, values);
    }
    values[row] = value;
  }

  static int calculateNewCapacity(int currentSize, int minSize) {
    int newSize = currentSize;
    while (newSize < minSize) {
      newSize <<= 1;
    }
    return newSize;
  }

  static <T> void trimToSize(List<T[]> valuesByColumnIndex, int size) {
    for (int i = 0; i < valuesByColumnIndex.size(); ++i) {
      T[] values = valuesByColumnIndex.get(i);
      if (values.length > size) {
        valuesByColumnIndex.set(i, Arrays.copyOf(values, size));
      }
    }
  }
}
