package org.mobilitydata.gtfsvalidator.performance;

import static com.google.common.truth.Truth.assertThat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ValidationPerformanceTrackerTest {

  private static final int ALLOCATION_BYTES = 8 * 1024 * 1024;

  /** Keeps the allocation reachable so the JIT cannot remove it. */
  private static volatile byte[] sink;

  @Test
  public void stop_reportsPhasesAndWorkOfTrackedTasks() throws Exception {
    ValidationPerformanceTracker tracker = ValidationPerformanceTracker.getInstance();
    ExecutorService executor = Executors.newSingleThreadExecutor();
    try {
      tracker.start();
      tracker.recordPhase(ValidationPerformanceMetrics.PHASE_INPUT, 2_000_000_000L);
      try (var phase = tracker.startPhase(ValidationPerformanceMetrics.PHASE_LOAD_TABLES)) {
        executor
            .submit(
                tracker.track(
                    () -> {
                      sink = new byte[ALLOCATION_BYTES];
                      return null;
                    }))
            .get();
      }
      ValidationPerformanceMetrics metrics = tracker.stop();

      assertThat(metrics.getPhaseWallTimeSeconds().keySet())
          .containsExactly(
              ValidationPerformanceMetrics.PHASE_INPUT,
              ValidationPerformanceMetrics.PHASE_LOAD_TABLES)
          .inOrder();
      assertThat(metrics.getPhaseWallTimeSeconds().get(ValidationPerformanceMetrics.PHASE_INPUT))
          .isEqualTo(2.0);
      // The input phase is excluded from the validation time.
      assertThat(metrics.getValidationWallTimeSeconds())
          .isEqualTo(
              metrics
                  .getPhaseWallTimeSeconds()
                  .get(ValidationPerformanceMetrics.PHASE_LOAD_TABLES));
      // The allocation happened on the executor thread and is still counted.
      assertThat(metrics.getAllocatedBytes()).isAtLeast((long) ALLOCATION_BYTES);
      assertThat(metrics.getCpuTimeSeconds()).isAtLeast(0.0);
      assertThat(metrics.getGcCount()).isAtLeast(0L);
    } finally {
      executor.shutdown();
    }
  }

  @Test
  public void start_clearsPreviousMeasurements() {
    ValidationPerformanceTracker tracker = ValidationPerformanceTracker.getInstance();
    tracker.start();
    tracker.recordPhase("first", 1_000);
    tracker.stop();

    tracker.start();
    ValidationPerformanceMetrics metrics = tracker.stop();

    assertThat(metrics.getPhaseWallTimeSeconds()).isEmpty();
    assertThat(metrics.getValidationWallTimeSeconds()).isNull();
  }

  @Test
  public void humanReadablePrint_handlesMissingMetrics() {
    ValidationPerformanceMetrics metrics =
        new ValidationPerformanceMetrics(
            java.util.Map.of("loadTables", 1.5), null, 2.0, 2048L, null, 3L, 0.25);

    assertThat(metrics.humanReadablePrint())
        .isEqualTo(
            "Performance: phases [loadTables: 1.500s] CPU: N/A Process CPU: 2.000s"
                + " Allocated: 2.00 KiB Peak heap after GC: N/A Peak live heap: N/A"
                + " GC: 3 collections in 0.250s");
  }

  @Test
  public void phaseClose_liveHeapDisabledByDefault_recordsNoLiveHeap() {
    ValidationPerformanceTracker tracker = ValidationPerformanceTracker.getInstance();
    tracker.start();
    try (var phase = tracker.startPhase(ValidationPerformanceMetrics.PHASE_LOAD_TABLES)) {}
    ValidationPerformanceMetrics metrics = tracker.stop();

    assertThat(metrics.getLiveHeapAfterPhaseBytes()).isEmpty();
    assertThat(metrics.getPeakLiveHeapBytes()).isNull();
  }

  @Test
  public void recordLiveHeap_countsReachableData() {
    ValidationPerformanceTracker tracker = ValidationPerformanceTracker.getInstance();
    tracker.start();
    sink = new byte[ALLOCATION_BYTES];
    tracker.recordLiveHeap(ValidationPerformanceMetrics.PHASE_LOAD_TABLES);
    tracker.recordLiveHeap(ValidationPerformanceMetrics.PHASE_MULTI_FILE_VALIDATORS);
    ValidationPerformanceMetrics metrics = tracker.stop();
    sink = null;

    assertThat(metrics.getLiveHeapAfterPhaseBytes().keySet())
        .containsExactly(
            ValidationPerformanceMetrics.PHASE_LOAD_TABLES,
            ValidationPerformanceMetrics.PHASE_MULTI_FILE_VALIDATORS)
        .inOrder();
    assertThat(metrics.getPeakLiveHeapBytes()).isAtLeast((long) ALLOCATION_BYTES);
  }

  @Test
  public void start_clearsPreviousLiveHeap() {
    ValidationPerformanceTracker tracker = ValidationPerformanceTracker.getInstance();
    tracker.start();
    tracker.recordLiveHeap("first");
    tracker.stop();

    tracker.start();
    ValidationPerformanceMetrics metrics = tracker.stop();

    assertThat(metrics.getLiveHeapAfterPhaseBytes()).isEmpty();
  }
}
