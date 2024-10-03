package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import java.util.Comparator;
import org.mobilitydata.gtfsvalidator.performance.MemoryUsage;

/**
 * Comparator to compare two {@link DatasetMemoryUsage} objects based on the difference between the
 * used memory of the two objects based on the {@link DatasetMemoryUsage#getLatestMemoryUsage}.
 */
public class LatestReportUsedMemoryIncreasedComparator implements Comparator<DatasetMemoryUsage> {

  @Override
  public int compare(DatasetMemoryUsage o1, DatasetMemoryUsage o2) {
    if (o1 == o2) {
      return 0;
    }
    if (o1 == null || o2 == null) {
      return o1 == null ? -1 : 1;
    }
    if (o1.getLatestMemoryUsage() == null && o2.getLatestMemoryUsage() == null) {
      return 0;
    }
    if (o1.getLatestMemoryUsage() == null || o2.getLatestMemoryUsage() == null) {
      return o1.getLatestMemoryUsage() == null ? -1 : 1;
    }
    long o1MinDiff =
        o1.getLatestMemoryUsage().stream()
            .min(Comparator.comparingLong(MemoryUsage::usedMemory))
            .get()
            .usedMemory();
    long o2MinDiff =
        o2.getLatestMemoryUsage().stream()
            .min(Comparator.comparingLong(MemoryUsage::usedMemory))
            .get()
            .usedMemory();
    return Long.compare(o1MinDiff, o2MinDiff);
  }

  @Override
  public Comparator<DatasetMemoryUsage> reversed() {
    return Comparator.super.reversed();
  }
}
