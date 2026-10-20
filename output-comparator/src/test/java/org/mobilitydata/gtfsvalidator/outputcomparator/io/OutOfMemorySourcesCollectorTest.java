package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class OutOfMemorySourcesCollectorTest {

  @Test
  public void noOomSources_generatesEmptyMessage() {
    OutOfMemorySourcesCollector collector = new OutOfMemorySourcesCollector(2f);

    assertThat(collector.hasOomSources()).isFalse();
    assertThat(collector.generateLogString())
        .isEqualTo(
            "### 💾 Out of Memory Check\n" + "No datasets experienced an OutOfMemoryError.\n");
  }

  @Test
  public void singleOomSource_referenceOnly() {
    OutOfMemorySourcesCollector collector = new OutOfMemorySourcesCollector(2f);
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
    OutOfMemorySourcesCollector collector = new OutOfMemorySourcesCollector(2f);
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
    OutOfMemorySourcesCollector collector = new OutOfMemorySourcesCollector(2f);
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

  @Test
  public void latestOomAboveThreshold_isAboveThreshold() {
    OutOfMemorySourcesCollector collector = new OutOfMemorySourcesCollector(2f);
    for (int i = 0; i < 100; i++) {
      collector.addSource();
    }
    collector.addOomSource("feed-id-a", false, true);
    collector.addOomSource("feed-id-b", false, true);
    collector.addOomSource("feed-id-c", true, true);

    // 3 out of 100 sources = 3% > 2%.
    assertThat(collector.isAboveThreshold()).isTrue();
    assertThat(collector.generateLogString())
        .contains(
            "**Failed:** 3 out of 100 sources (~3 %) ran out of memory with the latest version,"
                + " above the 2 % threshold.");
  }

  @Test
  public void latestOomAtThreshold_isNotAboveThreshold() {
    OutOfMemorySourcesCollector collector = new OutOfMemorySourcesCollector(2f);
    for (int i = 0; i < 100; i++) {
      collector.addSource();
    }
    collector.addOomSource("feed-id-a", false, true);
    collector.addOomSource("feed-id-b", true, true);

    // 2 out of 100 sources = 2%, not above 2%.
    assertThat(collector.isAboveThreshold()).isFalse();
    assertThat(collector.generateLogString()).doesNotContain("**Failed:**");
  }

  @Test
  public void referenceOnlyOom_isNotAboveThreshold() {
    OutOfMemorySourcesCollector collector = new OutOfMemorySourcesCollector(2f);
    for (int i = 0; i < 10; i++) {
      collector.addSource();
    }
    collector.addOomSource("feed-id-a", true, false);
    collector.addOomSource("feed-id-b", true, false);
    collector.addOomSource("feed-id-c", true, false);

    // Only the reference ran out of memory, so the latest version did not regress.
    assertThat(collector.isAboveThreshold()).isFalse();
  }

  private String normalizeWhitespace(String input) {
    return input.replaceAll("\\s+", " ").trim();
  }
}
