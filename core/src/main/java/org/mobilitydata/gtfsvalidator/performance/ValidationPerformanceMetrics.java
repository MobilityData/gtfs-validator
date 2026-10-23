package org.mobilitydata.gtfsvalidator.performance;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Performance metrics of one validation run, as collected by {@link ValidationPerformanceTracker}.
 *
 * <p>These values are used internally to compare the performance of two versions of the validator
 * and they can change in future versions. Any metric the JVM cannot provide is {@code null}. All
 * metrics except the {@link #PHASE_INPUT} phase time exclude reading the input, so that network
 * conditions do not affect them.
 */
public class ValidationPerformanceMetrics {

  /** Phase name for reading the GTFS input, including the download when the source is a URL. */
  public static final String PHASE_INPUT = "input";

  /** Phase name for parsing the tables and running the single-file and single-entity validators. */
  public static final String PHASE_LOAD_TABLES = "loadTables";

  /** Phase name for running the multi-file validators. */
  public static final String PHASE_MULTI_FILE_VALIDATORS = "multiFileValidators";

  /** Phase name for computing the feed metadata shown in the reports. */
  public static final String PHASE_FEED_METADATA = "feedMetadata";

  /** Wall-clock time of each phase in seconds, in execution order. */
  private Map<String, Double> phaseWallTimeSeconds;

  /**
   * CPU time in seconds spent by the threads doing the validation work: the thread that runs the
   * validation and the tasks it submits to the loader's thread pool. Excludes GC and JIT threads,
   * which makes it less sensitive to other load on the machine than wall-clock time.
   */
  private Double cpuTimeSeconds;

  /** CPU time in seconds of the whole JVM process, including GC and JIT compiler threads. */
  private Double processCpuTimeSeconds;

  /**
   * Bytes allocated on the heap by the threads doing the validation work. Unlike a heap snapshot,
   * this does not depend on when the garbage collector runs, so it is nearly identical across runs
   * of the same code on the same input.
   */
  private Long allocatedBytes;

  /**
   * Largest heap occupancy observed right after a garbage collection, in bytes. This approximates
   * the peak live data set. It is {@code null} when no collection ran during the validation, which
   * is common for small feeds on a large heap. The JVM reports collections process-wide, so
   * concurrent validations in the same JVM see each other's data.
   */
  private Long peakHeapAfterGcBytes;

  /**
   * Heap in use right after a forced garbage collection at the end of each phase, in bytes, in
   * execution order. Unlike {@link #peakHeapAfterGcBytes}, it is measured on every feed whatever
   * its size. It is empty unless the {@link ValidationPerformanceTracker#LIVE_HEAP_PROPERTY} system
   * property is set.
   */
  private Map<String, Long> liveHeapAfterPhaseBytes;

  /** Number of garbage collections during the validation, process-wide. */
  private Long gcCount;

  /** Time in seconds the garbage collectors reported for those collections, process-wide. */
  private Double gcTimeSeconds;

  public ValidationPerformanceMetrics() {}

  public ValidationPerformanceMetrics(
      Map<String, Double> phaseWallTimeSeconds,
      Double cpuTimeSeconds,
      Double processCpuTimeSeconds,
      Long allocatedBytes,
      Long peakHeapAfterGcBytes,
      Long gcCount,
      Double gcTimeSeconds) {
    this(
        phaseWallTimeSeconds,
        Collections.emptyMap(),
        cpuTimeSeconds,
        processCpuTimeSeconds,
        allocatedBytes,
        peakHeapAfterGcBytes,
        gcCount,
        gcTimeSeconds);
  }

  public ValidationPerformanceMetrics(
      Map<String, Double> phaseWallTimeSeconds,
      Map<String, Long> liveHeapAfterPhaseBytes,
      Double cpuTimeSeconds,
      Double processCpuTimeSeconds,
      Long allocatedBytes,
      Long peakHeapAfterGcBytes,
      Long gcCount,
      Double gcTimeSeconds) {
    this.phaseWallTimeSeconds = new LinkedHashMap<>(phaseWallTimeSeconds);
    // Left null when empty so that reports without the measurement do not carry an empty object.
    this.liveHeapAfterPhaseBytes =
        liveHeapAfterPhaseBytes.isEmpty() ? null : new LinkedHashMap<>(liveHeapAfterPhaseBytes);
    this.cpuTimeSeconds = cpuTimeSeconds;
    this.processCpuTimeSeconds = processCpuTimeSeconds;
    this.allocatedBytes = allocatedBytes;
    this.peakHeapAfterGcBytes = peakHeapAfterGcBytes;
    this.gcCount = gcCount;
    this.gcTimeSeconds = gcTimeSeconds;
  }

  public Map<String, Double> getPhaseWallTimeSeconds() {
    return phaseWallTimeSeconds == null
        ? Collections.emptyMap()
        : Collections.unmodifiableMap(phaseWallTimeSeconds);
  }

  /**
   * Returns the wall-clock time of all phases except {@link #PHASE_INPUT}, so that download time
   * does not hide changes in the validation itself.
   *
   * @return the time in seconds, or {@code null} if no phase other than the input was recorded.
   */
  public Double getValidationWallTimeSeconds() {
    Double total = null;
    for (Map.Entry<String, Double> phase : getPhaseWallTimeSeconds().entrySet()) {
      if (!PHASE_INPUT.equals(phase.getKey()) && phase.getValue() != null) {
        total = (total == null ? 0 : total) + phase.getValue();
      }
    }
    return total;
  }

  public Double getCpuTimeSeconds() {
    return cpuTimeSeconds;
  }

  public Double getProcessCpuTimeSeconds() {
    return processCpuTimeSeconds;
  }

  public Long getAllocatedBytes() {
    return allocatedBytes;
  }

  public Long getPeakHeapAfterGcBytes() {
    return peakHeapAfterGcBytes;
  }

  public Map<String, Long> getLiveHeapAfterPhaseBytes() {
    return liveHeapAfterPhaseBytes == null
        ? Collections.emptyMap()
        : Collections.unmodifiableMap(liveHeapAfterPhaseBytes);
  }

  /**
   * Returns the largest heap in use after any phase, see {@link #getLiveHeapAfterPhaseBytes()}.
   *
   * @return the heap in bytes, or {@code null} if it was not measured.
   */
  public Long getPeakLiveHeapBytes() {
    return getLiveHeapAfterPhaseBytes().values().stream().max(Long::compare).orElse(null);
  }

  public Long getGcCount() {
    return gcCount;
  }

  public Double getGcTimeSeconds() {
    return gcTimeSeconds;
  }

  /**
   * Returns a human-readable, single-line summary of the metrics.
   *
   * @return the summary, e.g. "Performance: phases [input: 0.120s, loadTables: 3.400s] CPU: ..."
   */
  public String humanReadablePrint() {
    StringBuilder result = new StringBuilder("Performance: phases [");
    String separator = "";
    for (Map.Entry<String, Double> phase : getPhaseWallTimeSeconds().entrySet()) {
      result.append(separator).append(phase.getKey()).append(": ");
      result.append(formatSeconds(phase.getValue()));
      separator = ", ";
    }
    result.append("] CPU: ").append(formatSeconds(cpuTimeSeconds));
    result.append(" Process CPU: ").append(formatSeconds(processCpuTimeSeconds));
    result.append(" Allocated: ").append(MemoryUsage.convertToHumanReadableMemory(allocatedBytes));
    result
        .append(" Peak heap after GC: ")
        .append(MemoryUsage.convertToHumanReadableMemory(peakHeapAfterGcBytes));
    result
        .append(" Peak live heap: ")
        .append(MemoryUsage.convertToHumanReadableMemory(getPeakLiveHeapBytes()));
    result.append(" GC: ").append(gcCount == null ? "N/A" : gcCount).append(" collections in ");
    result.append(formatSeconds(gcTimeSeconds));
    return result.toString();
  }

  private static String formatSeconds(Double seconds) {
    return seconds == null ? "N/A" : String.format(Locale.ROOT, "%.3fs", seconds);
  }
}
