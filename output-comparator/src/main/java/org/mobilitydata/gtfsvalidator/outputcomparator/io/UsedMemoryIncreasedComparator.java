package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Comparator to compare two {@link DatasetMemoryUsage} objects based on the difference between the
 * used memory of the two objects. The difference is calculated by comparing the used memory of the
 * two objects for each key present in both objects. If a key is present in one object but not in
 * the other, the key it is ignored. This comparator is used to sort DatasetMemoryUsage by the
 * minimum difference between the used memory of the two. This means the order is by the dataset
 * validation that increased the memory.
 */
public class UsedMemoryIncreasedComparator implements Comparator<DatasetMemoryUsage> {

  @Override
  public int compare(DatasetMemoryUsage o1, DatasetMemoryUsage o2) {
    if (o1 == o2) {
      return 0;
    }
    if (o1 == null || o2 == null) {
      return o1 == null ? -1 : 1;
    }
    if (o1.getReferenceMemoryUsage() == null
        && o1.getLatestMemoryUsage() == null
        && o2.getReferenceMemoryUsage() == null
        && o2.getLatestMemoryUsage() == null) {
      return 0;
    }
    if (o1.getReferenceMemoryUsage() == null || o2.getReferenceMemoryUsage() == null) {
      return o1.getReferenceMemoryUsage() == null ? -1 : 1;
    }
    if (o1.getLatestMemoryUsage() == null || o2.getLatestMemoryUsage() == null) {
      return o1.getLatestMemoryUsage() == null ? -1 : 1;
    }
    long o1MaxDiff =
        getMaxDifferenceByKey(o1.getReferenceUsedMemoryByKey(), o1.getLatestUsedMemoryByKey());
    long o2MaxDiff =
        getMaxDifferenceByKey(o2.getReferenceUsedMemoryByKey(), o2.getLatestUsedMemoryByKey());
    // Reversing the comparison as we need the major memory usage first in a sorted list
    return Long.compare(o2MaxDiff, o1MaxDiff);
  }

  private long getMaxDifferenceByKey(
      Map<String, Long> referenceMemoryUsage, Map<String, Long> latestMemoryUsage) {
    Set<String> keys = new HashSet<>();
    keys.addAll(latestMemoryUsage.keySet());
    keys.addAll(referenceMemoryUsage.keySet());
    return keys.stream()
        .filter(key -> latestMemoryUsage.containsKey(key) && referenceMemoryUsage.containsKey(key))
        .filter(key -> latestMemoryUsage.get(key) - referenceMemoryUsage.get(key) != 0)
        .mapToLong(key -> latestMemoryUsage.get(key) - referenceMemoryUsage.get(key))
        .max()
        .orElse(Long.MIN_VALUE);
  }
}
