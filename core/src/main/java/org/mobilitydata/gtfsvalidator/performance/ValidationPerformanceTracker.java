package org.mobilitydata.gtfsvalidator.performance;

import com.google.common.flogger.FluentLogger;
import com.sun.management.GarbageCollectionNotificationInfo;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;

/**
 * Collects {@link ValidationPerformanceMetrics} for one validation run.
 *
 * <p>Usage: call {@link #start()} and {@link #stop()} on the thread that runs the validation, wrap
 * every task submitted to a thread pool with {@link #track(Callable)}, and time the phases with
 * {@link #startPhase(String)}. Like {@link MemoryUsageRegister}, each thread gets its own tracker
 * so concurrent validations do not share counters; tasks capture the tracker of the thread that
 * created them.
 */
public class ValidationPerformanceTracker {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private static final ThreadLocal<ValidationPerformanceTracker> INSTANCE =
      ThreadLocal.withInitial(ValidationPerformanceTracker::new);

  /** Trackers between {@link #start()} and {@link #stop()}, notified of every collection. */
  private static final Set<ValidationPerformanceTracker> ACTIVE = ConcurrentHashMap.newKeySet();

  private static final AtomicBoolean GC_LISTENER_INSTALLED = new AtomicBoolean();

  private static final double NANOS_PER_SECOND = 1e9;

  private final Map<String, Long> phaseNanos = Collections.synchronizedMap(new LinkedHashMap<>());
  private final LongAdder taskCpuNanos = new LongAdder();
  private final LongAdder taskAllocatedBytes = new LongAdder();
  private final AtomicLong peakHeapAfterGcBytes = new AtomicLong(-1);
  private volatile boolean cpuTimeAvailable;
  private volatile boolean allocatedBytesAvailable;
  private long startThreadCpuNanos;
  private long startThreadAllocatedBytes;
  private long startProcessCpuNanos;
  private long startGcCount;
  private long startGcMillis;

  private ValidationPerformanceTracker() {}

  /**
   * @return the tracker of the calling thread.
   */
  public static ValidationPerformanceTracker getInstance() {
    return INSTANCE.get();
  }

  /** Clears the previous measurements and starts measuring. */
  public void start() {
    phaseNanos.clear();
    taskCpuNanos.reset();
    taskAllocatedBytes.reset();
    peakHeapAfterGcBytes.set(-1);
    cpuTimeAvailable = true;
    allocatedBytesAvailable = true;
    startThreadCpuNanos = currentThreadCpuNanos();
    startThreadAllocatedBytes = currentThreadAllocatedBytes();
    startProcessCpuNanos = processCpuNanos();
    startGcCount = gcCount();
    startGcMillis = gcMillis();
    installGcListener();
    ACTIVE.add(this);
  }

  /**
   * Stops measuring. Must be called on the thread that called {@link #start()}.
   *
   * @return the metrics measured since {@link #start()}.
   */
  public ValidationPerformanceMetrics stop() {
    ACTIVE.remove(this);
    long threadCpuNanos = currentThreadCpuNanos();
    long threadAllocatedBytes = currentThreadAllocatedBytes();
    long processCpuNanos = processCpuNanos();
    long gcCount = gcCount();
    long gcMillis = gcMillis();

    Map<String, Double> phaseSeconds = new LinkedHashMap<>();
    synchronized (phaseNanos) {
      phaseNanos.forEach((name, nanos) -> phaseSeconds.put(name, nanos / NANOS_PER_SECOND));
    }
    long peak = peakHeapAfterGcBytes.get();
    return new ValidationPerformanceMetrics(
        phaseSeconds,
        cpuTimeAvailable && threadCpuNanos >= 0 && startThreadCpuNanos >= 0
            ? (threadCpuNanos - startThreadCpuNanos + taskCpuNanos.sum()) / NANOS_PER_SECOND
            : null,
        processCpuNanos >= 0 && startProcessCpuNanos >= 0
            ? (processCpuNanos - startProcessCpuNanos) / NANOS_PER_SECOND
            : null,
        allocatedBytesAvailable && threadAllocatedBytes >= 0 && startThreadAllocatedBytes >= 0
            ? threadAllocatedBytes - startThreadAllocatedBytes + taskAllocatedBytes.sum()
            : null,
        peak >= 0 ? peak : null,
        gcCount >= 0 && startGcCount >= 0 ? gcCount - startGcCount : null,
        gcMillis >= 0 && startGcMillis >= 0 ? (gcMillis - startGcMillis) / 1000.0 : null);
  }

  /**
   * Starts timing a phase. Close the returned {@link Phase} to record its wall-clock time; the time
   * of phases with the same name is added up.
   */
  public Phase startPhase(String name) {
    return new Phase(name);
  }

  /** Records the wall-clock time of a phase that was timed outside this tracker. */
  public void recordPhase(String name, long nanos) {
    phaseNanos.merge(name, nanos, Long::sum);
  }

  /**
   * Wraps a task so that the CPU time and the allocations of the thread running it are added to
   * this tracker.
   */
  public <T> Callable<T> track(Callable<T> task) {
    return () -> {
      long cpuBefore = currentThreadCpuNanos();
      long allocatedBefore = currentThreadAllocatedBytes();
      try {
        return task.call();
      } finally {
        long cpuAfter = currentThreadCpuNanos();
        long allocatedAfter = currentThreadAllocatedBytes();
        if (cpuBefore >= 0 && cpuAfter >= 0) {
          taskCpuNanos.add(cpuAfter - cpuBefore);
        } else {
          cpuTimeAvailable = false;
        }
        if (allocatedBefore >= 0 && allocatedAfter >= 0) {
          taskAllocatedBytes.add(allocatedAfter - allocatedBefore);
        } else {
          allocatedBytesAvailable = false;
        }
      }
    };
  }

  /** A phase being timed by {@link #startPhase(String)}. */
  public class Phase implements AutoCloseable {
    private final String name;
    private final long startNanos;

    private Phase(String name) {
      this.name = name;
      this.startNanos = System.nanoTime();
    }

    @Override
    public void close() {
      recordPhase(name, System.nanoTime() - startNanos);
    }
  }

  private static long currentThreadCpuNanos() {
    ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
    if (!threadBean.isCurrentThreadCpuTimeSupported()) {
      return -1;
    }
    return threadBean.getCurrentThreadCpuTime();
  }

  private static long currentThreadAllocatedBytes() {
    ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
    if (threadBean instanceof com.sun.management.ThreadMXBean) {
      com.sun.management.ThreadMXBean sunThreadBean = (com.sun.management.ThreadMXBean) threadBean;
      if (sunThreadBean.isThreadAllocatedMemorySupported()
          && sunThreadBean.isThreadAllocatedMemoryEnabled()) {
        return sunThreadBean.getCurrentThreadAllocatedBytes();
      }
    }
    return -1;
  }

  private static long processCpuNanos() {
    OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
    if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
      return ((com.sun.management.OperatingSystemMXBean) osBean).getProcessCpuTime();
    }
    return -1;
  }

  private static long gcCount() {
    long total = 0;
    for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
      long count = gcBean.getCollectionCount();
      if (count < 0) {
        return -1;
      }
      total += count;
    }
    return total;
  }

  private static long gcMillis() {
    long total = 0;
    for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
      long millis = gcBean.getCollectionTime();
      if (millis < 0) {
        return -1;
      }
      total += millis;
    }
    return total;
  }

  /**
   * Installs, once per JVM, a listener that reports the heap occupancy after every collection to
   * the active trackers. Notifications are delivered asynchronously, so a collection that ends
   * right before {@link #stop()} may be missed.
   */
  private static void installGcListener() {
    if (!GC_LISTENER_INSTALLED.compareAndSet(false, true)) {
      return;
    }
    try {
      Set<String> heapPools =
          ManagementFactory.getMemoryPoolMXBeans().stream()
              .filter(pool -> pool.getType() == MemoryType.HEAP)
              .map(MemoryPoolMXBean::getName)
              .collect(Collectors.toUnmodifiableSet());
      NotificationListener listener =
          (notification, handback) -> {
            if (!GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION.equals(
                notification.getType())) {
              return;
            }
            Map<String, java.lang.management.MemoryUsage> usageAfterGc =
                GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData())
                    .getGcInfo()
                    .getMemoryUsageAfterGc();
            long heapUsed = 0;
            for (Map.Entry<String, java.lang.management.MemoryUsage> pool :
                usageAfterGc.entrySet()) {
              if (heapPools.contains(pool.getKey())) {
                heapUsed += pool.getValue().getUsed();
              }
            }
            for (ValidationPerformanceTracker tracker : ACTIVE) {
              tracker.peakHeapAfterGcBytes.accumulateAndGet(heapUsed, Math::max);
            }
          };
      for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
        if (gcBean instanceof NotificationEmitter) {
          ((NotificationEmitter) gcBean).addNotificationListener(listener, null, null);
        }
      }
    } catch (RuntimeException e) {
      logger.atWarning().withCause(e).log("Cannot monitor garbage collections");
    }
  }
}
