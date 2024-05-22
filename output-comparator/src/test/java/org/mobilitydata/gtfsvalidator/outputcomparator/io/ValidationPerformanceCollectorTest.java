package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

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

        // Generating the log string
        String logString = collector.generateLogString();
        String expectedLogString =
                "### ⏱️ Performance Assessment\n" +
                        "\n" +
                        "<details>\n" +
                        "<summary><strong>📈 Validation Time</strong></summary>\n" +
                        "<p>Assess the performance in terms of seconds taken for the validation process.</p>\n" +
                        "\n" +
                        "| Time Metric                      | Dataset ID        | Reference (s)  | Latest (s)     | Difference (s) |\n" +
                        "|-----------------------------|-------------------|----------------|----------------|----------------|\n" +
                        "| Average | -- | 15.33 | 18.67 | ⬆️+3.33 |\n" +
                        "| Median | -- | 14.00 | 18.00 | ⬆️+4.00 |\n" +
                        "| Standard Deviation | -- | 3.40 | 2.49 | ⬇️-0.90 |\n" +
                        "| Minimum in References Reports | feed-id-a | 12.00 | 16.00 | ⬆️+4.00 |\n" +
                        "| Maximum in Reference Reports | feed-id-b | 20.00 | 22.00 | ⬆️+2.00 |\n" +
                        "| Minimum in Latest Reports | feed-id-a | 12.00 | 16.00 | ⬆️+4.00 |\n" +
                        "| Maximum in Latest Reports | feed-id-b | 20.00 | 22.00 | ⬆️+2.00 |\n" +
                        "</details>\n\n";
        // Assert that the generated log string matches the expected log string
        assertThat(logString).isEqualTo(expectedLogString);
    }
}
