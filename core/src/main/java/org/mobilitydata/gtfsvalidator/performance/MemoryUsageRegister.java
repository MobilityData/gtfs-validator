package org.mobilitydata.gtfsvalidator.performance;

import com.google.common.flogger.FluentLogger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryUsageRegister {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private static MemoryUsageRegister instance = new MemoryUsageRegister();
  private final Runtime runtime;
  private List<MemoryUsage> registry = new ArrayList<>();

  private MemoryUsageRegister() {
    runtime = Runtime.getRuntime();
  }

  public static MemoryUsageRegister getInstance() {
    return instance;
  }

  public List<MemoryUsage> getRegistry() {
    return Collections.unmodifiableList(registry);
  }

  public MemoryUsage getMemoryUsageSnapshot(String key, MemoryUsage previous) {
    Long memoryDiff = null;
    if (previous != null) {
      memoryDiff = runtime.freeMemory() - previous.freeMemory();
    }
    return MemoryUsage.create(
        key, runtime.totalMemory(), runtime.freeMemory(), runtime.maxMemory(), memoryDiff);
  }

  public MemoryUsage registerMemoryUsage(String key) {
    MemoryUsage memoryUsage = getMemoryUsageSnapshot(key, null);
    registerMemoryUsage(memoryUsage);
    return memoryUsage;
  }

  public MemoryUsage registerMemoryUsage(String key, MemoryUsage previous) {
    MemoryUsage memoryUsage = getMemoryUsageSnapshot(key, previous);
    registerMemoryUsage(memoryUsage);
    return memoryUsage;
  }

  public void registerMemoryUsage(MemoryUsage memoryUsage) {
    registry.add(memoryUsage);
    logger.atInfo().log(memoryUsage.humanReadablePrint());
  }

  public void clearRegistry() {
    registry.clear();
  }
}
