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
    long baseMemory = 1000000;
    //    Memory usage latest null
    collector.compareValidationReports(
        "feed-id-a",
        new ValidationReport(
            Collections.EMPTY_SET,
            12.0,
            Arrays.asList(
                new MemoryUsage("key1", baseMemory, baseMemory, 200, 50L),
                new MemoryUsage("key2", baseMemory, baseMemory, 200, 50L))),
        new ValidationReport(Collections.EMPTY_SET, 16.0, Collections.EMPTY_LIST));
    //    Memory usage decreased
    collector.compareValidationReports(
        "feed-id-a",
        new ValidationReport(
            Collections.EMPTY_SET,
            14.0,
            Arrays.asList(
                new MemoryUsage("key3", baseMemory, baseMemory - 1000, 200, 50L),
                new MemoryUsage("key4", baseMemory, baseMemory - 1000, 200, 50L))),
        new ValidationReport(
            Collections.EMPTY_SET,
            18.0,
            Arrays.asList(
                new MemoryUsage("key3", baseMemory, baseMemory - baseMemory / 2, 200, null),
                new MemoryUsage("key4", baseMemory, baseMemory - baseMemory / 2, 200, null))));

    //    Memory usage decreased
    collector.compareValidationReports(
        "feed-id-b",
        new ValidationReport(
            Collections.EMPTY_SET,
            20.0,
            Arrays.asList(
                new MemoryUsage("key3", baseMemory, baseMemory * 2, 200, null),
                new MemoryUsage("key4", baseMemory, baseMemory * 2, 200, null))),
        new ValidationReport(Collections.EMPTY_SET, 22.0, Collections.EMPTY_LIST));

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
            + "<summary><strong>📜 Memory Consumption</strong></summary>\n"
            + "<p>List of 20 datasets where memory has decreased .</p>\n\n"
            + "| Key(Used Memory)                      | Dataset ID        | Reference (s)  | Latest (s)     | Difference (s) |\n"
            + "|-----------------------------|-------------------|----------------|----------------|----------------|\n"
            + "| key1 | feed-id-a | 0 | - | N/A |\n"
            + "| key2 | feed-id-a | 0 | - | N/A |\n"
            + "| key4 | feed-id-a | 1000 | 500000 | ⬆️+487.30 KiB |\n"
            + "| key3 | feed-id-a | 1000 | 500000 | ⬆️+487.30 KiB |\n"
            + "| key3 | feed-id-b | -1000000 | - | N/A |\n"
            + "| key4 | feed-id-b | -1000000 | - | N/A |\n"
            + "<p>List of 20 datasets where memory has increased .</p>\n\n"
            + "| Key(Used Memory)                      | Dataset ID        | Reference (s)  | Latest (s)     | Difference (s) |\n"
            + "|-----------------------------|-------------------|----------------|----------------|----------------|\n"
            + "| key3 | feed-id-a | 1000 | 500000 | ⬆️+487.30 KiB |\n"
            + "| key4 | feed-id-a | 1000 | 500000 | ⬆️+487.30 KiB |\n"
            + "| key1 | feed-id-a | 0 | - | N/A |\n"
            + "| key2 | feed-id-a | 0 | - | N/A |\n"
            + "| key3 | feed-id-b | -1000000 | - | N/A |\n"
            + "| key4 | feed-id-b | -1000000 | - | N/A |\n"
            + "</details>\n\n";
    // Assert that the generated log string matches the expected log string
    assertThat(logString).isEqualTo(expectedLogString);
  }
}
