package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import static com.google.common.truth.Truth.assertThat;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.model.ValidationReport;
import org.mobilitydata.gtfsvalidator.performance.MemoryUsage;

public class ValidationPerformanceCollectorTest {

  @Test
  public void generateLogString_test() {
    ValidationPerformanceCollector collector = new ValidationPerformanceCollector();

    // Adding some sample data
    collector.addReferenceTime("feed-id-a", 12.0);
    collector.addReferenceTime("feed-id-a", 14.0);
    collector.addLatestTime("feed-id-a", 16.0);
    collector.addLatestTime("feed-id-a", 18.0);

    collector.addReferenceTime("feed-id-b", 20.0);
    collector.addLatestTime("feed-id-b", 22.0);

    // Adding some sample data
    long baseMemory = 1000000;
    //    Memory usage latest null
    collector.compareValidationReports(
        "feed-id-m1",
        new ValidationReport(
            Collections.EMPTY_SET,
            null,
            Arrays.asList(
                new MemoryUsage("key1", baseMemory, baseMemory, 200, 50L),
                new MemoryUsage("key2", baseMemory, baseMemory, 200, 50L))),
        new ValidationReport(Collections.EMPTY_SET, 16.0, Collections.EMPTY_LIST));
    //    Memory usage increased as there is less free memory
    collector.compareValidationReports(
        "feed-id-m2",
        new ValidationReport(
            Collections.EMPTY_SET,
            null,
            Arrays.asList(
                new MemoryUsage("key1", baseMemory, baseMemory, 200, 50L),
                new MemoryUsage("key2", baseMemory, baseMemory, 200, 50L))),
        new ValidationReport(
            Collections.EMPTY_SET,
            null,
            Arrays.asList(
                new MemoryUsage("key1", baseMemory, baseMemory - baseMemory / 2, 200, null),
                new MemoryUsage("key2", baseMemory, baseMemory - baseMemory / 2, 200, null))));

    //    //    Memory usage decreased as there is more free memory
    collector.compareValidationReports(
        "feed-id-m3",
        new ValidationReport(
            Collections.EMPTY_SET,
            null,
            Arrays.asList(
                new MemoryUsage("key3", baseMemory, baseMemory + 100, 200, null),
                new MemoryUsage("key4", baseMemory, baseMemory + 100, 200, null))),
        new ValidationReport(
            Collections.EMPTY_SET,
            null,
            Arrays.asList(
                new MemoryUsage("key3", baseMemory, baseMemory * 2, 200, null),
                new MemoryUsage("key4", baseMemory, baseMemory * 2, 200, null))));
    // Generating the log string
    String logString = collector.generateLogString();
    String expectedLogString =
        "### ⏱️ Performance Assessment\n"
            + "\n"
            + "<details>\n"
            + "<summary><strong>📈 Validation Time</strong></summary>\n"
            + "<p>Assess the performance in terms of seconds taken for the validation process.</p>\n"
            + "\n"
            + "| Time Metric                      | Dataset ID        | Reference (s)  | Latest (s)     | Difference (s) |\n"
            + "|-----------------------------|-------------------|----------------|----------------|----------------|\n"
            + "| Average | -- | 17.00 | 20.00 | ⬆\uFE0F+3.00 |\n"
            + "| Median | -- | 17.00 | 20.00 | ⬆\uFE0F+3.00 |\n"
            + "| Standard Deviation | -- | 3.00 | 2.00 | ⬇\uFE0F-1.00 |\n"
            + "| Minimum in References Reports | feed-id-a | 14.00 | 18.00 | ⬆\uFE0F+4.00 |\n"
            + "| Maximum in Reference Reports | feed-id-b | 20.00 | 22.00 | ⬆️+2.00 |\n"
            + "| Minimum in Latest Reports | feed-id-a | 14.00 | 18.00 | ⬆\uFE0F+4.00 |\n"
            + "| Maximum in Latest Reports | feed-id-b | 20.00 | 22.00 | ⬆️+2.00 |\n"
            + "#### ⚠️ Warnings\n\n"
            + "The following dataset IDs are missing validation times either in reference or latest:\n"
            + "feed-id-m1\n\n"
            + "</details>\n\n"
            + "<details>\n"
            + "<summary><strong>📜 Memory Consumption</strong></summary>\n"
            + "<p>List of "
            + ValidationPerformanceCollector.MEMORY_USAGE_COMPARE_MAX
            + " datasets where memory has increased.</p>\n"
            + "| Dataset ID                  | Snapshot Key(Used Memory)  | Reference (s)  | Latest (s)     | Difference (s) |\n"
            + "|-----------------------------|-------------------|----------------|----------------|----------------|\n"
            + "| feed-id-m2 |  |  |  |  |\n"
            + "| | key1 | 0 | 500000 | ⬆\uFE0F+488.28 KiB |\n"
            + "| | key2 | 0 | 500000 | ⬆\uFE0F+488.28 KiB |\n"
            + "| feed-id-m3 |  |  |  |  |\n"
            + "| | key3 | -100 | -1000000 | ⬇\uFE0F-976.46 KiB |\n"
            + "| | key4 | -100 | -1000000 | ⬇\uFE0F-976.46 KiB |\n"
            + "<p>List of "
            + ValidationPerformanceCollector.MEMORY_USAGE_COMPARE_MAX
            + " datasets where memory has decreased.</p>\n"
            + "| Dataset ID                  | Snapshot Key(Used Memory)  | Reference (s)  | Latest (s)     | Difference (s) |\n"
            + "|-----------------------------|-------------------|----------------|----------------|----------------|\n"
            + "| feed-id-m3 |  |  |  |  |\n"
            + "| | key3 | -100 | -1000000 | ⬇️-976.46 KiB |\n"
            + "| | key4 | -100 | -1000000 | ⬇️-976.46 KiB |\n"
            + "| feed-id-m2 |  |  |  |  |\n"
            + "| | key1 | 0 | 500000 | ⬆️+488.28 KiB |\n"
            + "| | key2 | 0 | 500000 | ⬆️+488.28 KiB |\n"
            + "</details>\n";
    // Assert that the generated log string matches the expected log string
    assertThat(logString).isEqualTo(expectedLogString);
  }
}
