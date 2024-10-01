package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import java.util.Comparator;

/** A comparator for MemoryUsage objects that compares them based on the used memory. */
public class MemoryUsageUsedMemoryComparator implements Comparator<DatasetMemoryUsage> {

  @Override
  public int compare(DatasetMemoryUsage o1, DatasetMemoryUsage o2) {
    if (o1 == o2) {
      return 0;
    }
    if (o1 == null || o2 == null) {
      return o1 == null ? -1 : 1;
    }
    if (o1.getReferenceMemoryUsage() == null && o2.getLatestMemoryUsage() == null) {
      return 0;
    }
    if (o1.getReferenceMemoryUsage() == null || o2.getLatestMemoryUsage() == null) {
      return o1.getReferenceMemoryUsage() == null ? -1 : 1;
    }
    if (o1.getReferenceMemoryUsage().usedMemory() < o2.getLatestMemoryUsage().usedMemory()) {
      return -1;
    }
    if (o1.getReferenceMemoryUsage().usedMemory() > o2.getLatestMemoryUsage().usedMemory()) {
      return 1;
    }
    return 0;
  }
}
