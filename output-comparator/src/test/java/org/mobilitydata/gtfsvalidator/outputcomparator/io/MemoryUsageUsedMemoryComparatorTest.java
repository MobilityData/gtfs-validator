package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import static org.junit.Assert.assertEquals;

import java.util.*;
import org.junit.Before;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.performance.MemoryUsage;

public class MemoryUsageUsedMemoryComparatorTest {

  private UsedMemoryIncreasedComparator comparator;

  @Before
  public void setUp() {
    comparator = new UsedMemoryIncreasedComparator();
  }

  @Test
  public void testCompare_equalMemoryUsage() {
    List<MemoryUsage> referenceMemoryUsage = getMemoryUsage(100L);
    List<MemoryUsage> latestMemoryUsage = getMemoryUsage(100L);
    DatasetMemoryUsage o1 =
        new DatasetMemoryUsage("dataset1", referenceMemoryUsage, latestMemoryUsage);
    DatasetMemoryUsage o2 =
        new DatasetMemoryUsage("dataset1", referenceMemoryUsage, latestMemoryUsage);
    assertEquals(0, comparator.compare(o1, o2));
  }

  @Test
  public void testCompare_firstHasMoreMemoryDifference() {
    List<MemoryUsage> referenceMemoryUsage = getMemoryUsage(100L);
    List<MemoryUsage> latestMemoryUsage = getMemoryUsage(50L);
    DatasetMemoryUsage o1 =
        new DatasetMemoryUsage("dataset1", referenceMemoryUsage, latestMemoryUsage);
    DatasetMemoryUsage o2 =
        new DatasetMemoryUsage("dataset1", referenceMemoryUsage, getMemoryUsage(100L));
    assertEquals(-1, comparator.compare(o1, o2));
  }

  @Test
  public void testCompare_firstHasLessMemoryDifference() {
    List<MemoryUsage> referenceMemoryUsage = getMemoryUsage(100L);
    List<MemoryUsage> latestMemoryUsage = getMemoryUsage(50L);
    DatasetMemoryUsage o1 =
        new DatasetMemoryUsage("dataset1", referenceMemoryUsage, latestMemoryUsage);
    DatasetMemoryUsage o2 =
        new DatasetMemoryUsage("dataset1", referenceMemoryUsage, getMemoryUsage(10L));
    assertEquals(1, comparator.compare(o1, o2));
  }

  private static List<MemoryUsage> getMemoryUsage(long freeMemory) {
    MemoryUsage[] referenceMemoryUsage =
        new MemoryUsage[] {
          new MemoryUsage("key1", 100L, freeMemory, 100L, 100L),
          new MemoryUsage("key2", 100L, freeMemory, 100L, 100L),
        };
    return Arrays.asList(referenceMemoryUsage);
  }
}
