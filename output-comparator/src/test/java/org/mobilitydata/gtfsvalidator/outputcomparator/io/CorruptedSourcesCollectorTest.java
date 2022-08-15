package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import static com.google.common.truth.Truth.assertThat;

import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.report.CorruptedSources;

@RunWith(JUnit4.class)
public class CorruptedSourcesCollectorTest {

  @Test
  public void testBasicFunctionality() {
    CorruptedSourcesCollector collector = new CorruptedSourcesCollector(25);

    collector.addSource();

    assertThat(collector.isAboveThreshold()).isFalse();
    assertThat(collector.computeCorruptedSourcesPercentage()).isWithin(1f).of(0f);
    assertThat(collector.toReport())
        .isEqualTo(
            CorruptedSources.builder()
                .setSourceIdCount(1)
                .setCorruptedSourcesCount(0)
                .setCorruptedSources(Arrays.asList())
                .setPercentCorruptedSourcesThreshold(25)
                .setAboveThreshold(false)
                .build());
    assertThat(collector.generateLogString()).isEqualTo("0 out of 1 sources (~0 %) are corrupted.");

    collector.addSource();
    collector.addCorruptedSource("source-a");

    assertThat(collector.isAboveThreshold()).isTrue();
    assertThat(collector.computeCorruptedSourcesPercentage()).isWithin(1f).of(50f);
    assertThat(collector.toReport())
        .isEqualTo(
            CorruptedSources.builder()
                .setSourceIdCount(2)
                .setCorruptedSourcesCount(1)
                .setCorruptedSources(Arrays.asList("source-a"))
                .setPercentCorruptedSourcesThreshold(25)
                .setAboveThreshold(true)
                .build());
    assertThat(collector.generateLogString())
        .isEqualTo(
            "1 out of 2 sources (~50 %) are corrupted, which is greater than or equal to the provided threshold of 25%.\n"
                + "Corrupted sources:\n"
                + "source-a");
  }
}
