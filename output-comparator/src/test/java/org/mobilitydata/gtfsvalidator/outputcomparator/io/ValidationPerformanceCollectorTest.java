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
                new MemoryUsage(
                    ValidationPerformanceCollector.MEMORY_PIVOT_KEY,
                    baseMemory,
                    baseMemory + baseMemory * 10,
                    200,
                    50L),
                new MemoryUsage("key2", baseMemory, baseMemory, 200, 50L))),
        new ValidationReport(Collections.EMPTY_SET, 16.0, Collections.EMPTY_LIST));
    //    Memory usage increased as there is less free memory
    collector.compareValidationReports(
        "feed-id-m2",
        new ValidationReport(
            Collections.EMPTY_SET,
            null,
            Arrays.asList(
                new MemoryUsage(
                    ValidationPerformanceCollector.MEMORY_PIVOT_KEY,
                    baseMemory,
                    baseMemory,
                    200,
                    50L),
                new MemoryUsage("key2", baseMemory, baseMemory, 200, 50L))),
        new ValidationReport(
            Collections.EMPTY_SET,
            null,
            Arrays.asList(
                new MemoryUsage(
                    ValidationPerformanceCollector.MEMORY_PIVOT_KEY,
                    baseMemory,
                    baseMemory - baseMemory / 2,
                    200,
                    null),
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
            + "| Average | -- | 17.00 | 18.67 | ⬆\uFE0F+1.67 |\n"
            + "| Median | -- | 17.00 | 18.00 | ⬆\uFE0F+1.00 |\n"
            + "| Standard Deviation | -- | 3.00 | 2.49 | ⬇\uFE0F-0.51 |\n"
            + "| Minimum in References Reports | feed-id-a | 14.00 | 18.00 | ⬆\uFE0F+4.00 |\n"
            + "| Maximum in Reference Reports | feed-id-b | 20.00 | 22.00 | ⬆️+2.00 |\n"
            + "| Minimum in Latest Reports | feed-id-m1 | NaN | 16.00 | N/A |\n"
            + "| Maximum in Latest Reports | feed-id-b | 20.00 | 22.00 | ⬆️+2.00 |\n"
            + "#### ⚠️ Warnings\n\n"
            + "The following dataset IDs are missing validation times either in reference or latest:\n"
            + "feed-id-m1\n\n"
            + "</details>\n\n"
            + "<details>\n"
            + "<summary><strong>📜 Memory Consumption</strong></summary>\n\n"
            + "| Metric                      | Dataset ID        | Reference (s)  | Latest (s)     | Difference (s) |\n"
            + "|-----------------------------|-------------------|----------------|----------------|----------------|\n"
            + "| Average | -- | 0 bytes | 488.28 KiB | ⬆️+488.28 KiB |\n"
            + "| Median | -- | 0 bytes | 488.28 KiB | ⬆️+488.28 KiB |\n"
            + "| Standard Deviation | -- | 0 bytes | 0 bytes | ⬇️0 bytes |\n"
            + "| Minimum in References Reports | feed-id-m2 | 0 bytes | 488.28 KiB | ⬆\uFE0F+488.28 KiB |\n"
            + "| Maximum in Reference Reports | feed-id-m2 | 0 bytes | 488.28 KiB | ⬆\uFE0F+488.28 KiB |\n"
            + "| Minimum in Latest Reports | feed-id-m2 | 0 bytes | 488.28 KiB | ⬆\uFE0F+488.28 KiB |\n"
            + "| Maximum in Latest Reports | feed-id-m2 | 0 bytes | 488.28 KiB | ⬆\uFE0F+488.28 KiB |\n"
            + "<p>List of "
            + ValidationPerformanceCollector.MEMORY_USAGE_COMPARE_MAX
            + " datasets(memory has increased).</p>\n\n"
            + "| Dataset ID                  | Snapshot Key(Used Memory)  | Reference  | Latest     | Difference |\n"
            + "|-----------------------------|-------------------|----------------|----------------|----------------|\n"
            + "| feed-id-m2 |  |  |  |  |\n"
            + "| | key2 | 0 bytes | 488.28 KiB | ⬆\uFE0F+488.28 KiB |\n"
            + "| | org.mobilitydata.gtfsvalidator.table.GtfsFeedLoader.loadAndValidate | 0 bytes | 488.28 KiB | ⬆\uFE0F+488.28 KiB |\n"
            + "| feed-id-m3 |  |  |  |  |\n"
            + "| | key3 | -100 bytes | -976.56 KiB | ⬇\uFE0F-976.46 KiB |\n"
            + "| | key4 | -100 bytes | -976.56 KiB | ⬇\uFE0F-976.46 KiB |\n"
            + "<p>List of "
            + ValidationPerformanceCollector.MEMORY_USAGE_COMPARE_MAX
            + " datasets(memory has decreased).</p>\n\n"
            + "| Dataset ID                  | Snapshot Key(Used Memory)  | Reference  | Latest     | Difference |\n"
            + "|-----------------------------|-------------------|----------------|----------------|----------------|\n"
            + "| feed-id-m3 |  |  |  |  |\n"
            + "| | key3 | -100 bytes | -976.56 KiB | ⬇️-976.46 KiB |\n"
            + "| | key4 | -100 bytes | -976.56 KiB | ⬇️-976.46 KiB |\n"
            + "| feed-id-m2 |  |  |  |  |\n"
            + "| | key2 | 0 bytes | 488.28 KiB | ⬆️+488.28 KiB |\n"
            + "| | org.mobilitydata.gtfsvalidator.table.GtfsFeedLoader.loadAndValidate | 0 bytes | 488.28 KiB | ⬆️+488.28 KiB |\n"
            + "<p>List of "
            + ValidationPerformanceCollector.MEMORY_USAGE_COMPARE_MAX
            + " datasets(no reference available).</p>\n\n"
            + "| Dataset ID                  | Snapshot Key(Used Memory)  | Reference  | Latest     |\n"
            + "|-----------------------------|-------------------|----------------|----------------|\n"
            + "| feed-id-m1 |  |  |  |\n"
            + "| | key2 | 0 bytes | N/A |\n"
            + "| | org.mobilitydata.gtfsvalidator.table.GtfsFeedLoader.loadAndValidate | -9.54 MiB | N/A |\n"
            + "</details>\n";
    // Assert that the generated log string matches the expected log string
    assertThat(logString).isEqualTo(expectedLogString);
  }
}
