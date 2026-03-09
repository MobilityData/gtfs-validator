package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class OutOfMemorySourcesCollectorTest {

  @Test
  public void noOomSources_generatesEmptyMessage() {
    OutOfMemorySourcesCollector collector = new OutOfMemorySourcesCollector();

    assertThat(collector.hasOomSources()).isFalse();
    assertThat(collector.generateLogString())
        .isEqualTo(
            "### 💾 Out of Memory Check\n" + "No datasets experienced an OutOfMemoryError.\n");
  }

  @Test
  public void singleOomSource_referenceOnly() {
    OutOfMemorySourcesCollector collector = new OutOfMemorySourcesCollector();
    collector.addOomSource("feed-id-a", true, false);

    assertThat(collector.hasOomSources()).isTrue();
    assertThat(normalizeWhitespace(collector.generateLogString()))
        .isEqualTo(
            normalizeWhitespace(
                "### 💾 Out of Memory Check\n"
                    + "<details>\n"
                    + "<summary><strong>1 dataset(s) experienced an OutOfMemoryError.</strong></summary>\n\n"
                    + "| Dataset | Reference OOM | Latest OOM |\n"
                    + "|---------|---------------|------------|\n"
                    + "| feed-id-a | ⚠️ | ✅ |\n"
                    + "</details>"));
  }

  @Test
  public void singleOomSource_latestOnly() {
    OutOfMemorySourcesCollector collector = new OutOfMemorySourcesCollector();
    collector.addOomSource("feed-id-b", false, true);

    assertThat(collector.hasOomSources()).isTrue();
    assertThat(normalizeWhitespace(collector.generateLogString()))
        .isEqualTo(
            normalizeWhitespace(
                "### 💾 Out of Memory Check\n"
                    + "<details>\n"
                    + "<summary><strong>1 dataset(s) experienced an OutOfMemoryError.</strong></summary>\n\n"
                    + "| Dataset | Reference OOM | Latest OOM |\n"
                    + "|---------|---------------|------------|\n"
                    + "| feed-id-b | ✅ | ⚠️ |\n"
                    + "</details>"));
  }

  @Test
  public void multipleOomSources_bothSides() {
    OutOfMemorySourcesCollector collector = new OutOfMemorySourcesCollector();
    collector.addOomSource("feed-id-a", true, false);
    collector.addOomSource("feed-id-b", true, true);

    assertThat(collector.hasOomSources()).isTrue();
    assertThat(normalizeWhitespace(collector.generateLogString()))
        .isEqualTo(
            normalizeWhitespace(
                "### 💾 Out of Memory Check\n"
                    + "<details>\n"
                    + "<summary><strong>2 dataset(s) experienced an OutOfMemoryError.</strong></summary>\n\n"
                    + "| Dataset | Reference OOM | Latest OOM |\n"
                    + "|---------|---------------|------------|\n"
                    + "| feed-id-a | ⚠️ | ✅ |\n"
                    + "| feed-id-b | ⚠️ | ⚠️ |\n"
                    + "</details>"));
  }

  private String normalizeWhitespace(String input) {
    return input.replaceAll("\\s+", " ").trim();
  }
}
