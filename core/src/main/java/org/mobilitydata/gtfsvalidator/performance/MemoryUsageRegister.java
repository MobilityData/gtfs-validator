package org.mobilitydata.gtfsvalidator.performance;

import com.google.common.flogger.FluentLogger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Register for memory usage snapshots. */
public class MemoryUsageRegister {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private static MemoryUsageRegister instance = new MemoryUsageRegister();
  private final Runtime runtime;
  private List<MemoryUsage> registry = new ArrayList<>();

  private MemoryUsageRegister() {
    runtime = Runtime.getRuntime();
  }

  /**
   * @return the singleton instance of the memory usage register.
   */
  public static MemoryUsageRegister getInstance() {
    return instance;
  }

  /**
   * Returns the memory usage registry.
   *
   * @return the memory usage registry unmodifiable list.
   */
  public List<MemoryUsage> getRegistry() {
    return Collections.unmodifiableList(registry);
  }

  /**
   * Returns a memory usage snapshot.
   *
   * @param key
   * @param previous
   * @return
   */
  public MemoryUsage getMemoryUsageSnapshot(String key, MemoryUsage previous) {
    Long memoryDiff = null;
    if (previous != null) {
      memoryDiff = runtime.freeMemory() - previous.getFreeMemory();
    }
    return new MemoryUsage(
        key, runtime.totalMemory(), runtime.freeMemory(), runtime.maxMemory(), memoryDiff);
  }

  /**
   * Registers a memory usage snapshot.
   *
   * @param key
   * @return
   */
  public MemoryUsage registerMemoryUsage(String key) {
    MemoryUsage memoryUsage = getMemoryUsageSnapshot(key, null);
    registerMemoryUsage(memoryUsage);
    return memoryUsage;
  }

  /**
   * Registers a memory usage snapshot.
   *
   * @param key
   * @param previous previous memory usage snapshot used to compute the memory difference between
   *     two snapshots.
   * @return
   */
  public MemoryUsage registerMemoryUsage(String key, MemoryUsage previous) {
    MemoryUsage memoryUsage = getMemoryUsageSnapshot(key, previous);
    registerMemoryUsage(memoryUsage);
    return memoryUsage;
  }

  /**
   * Registers a memory usage snapshot.
   *
   * @param memoryUsage
   */
  public void registerMemoryUsage(MemoryUsage memoryUsage) {
    registry.add(memoryUsage);
    logger.atInfo().log(memoryUsage.humanReadablePrint());
  }

  /** Clears the memory usage registry. */
  public void clearRegistry() {
    registry.clear();
  }
}
