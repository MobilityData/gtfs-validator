package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.report.CorruptedSources;

/**
 * Given validation reports computed against two versions of the validator across multiple datasets,
 * collects the set of "corrupted" sources where a validation report was not correctly generated.
 */
public class CorruptedSourcesCollector {

  private final float percentCorruptedSourcesThreshold;

  private int sourceIdCount = 0;
  private final List<String> corruptedSourceIds = new ArrayList<>();

  public CorruptedSourcesCollector(float percentCorruptedSourcesThreshold) {
    this.percentCorruptedSourcesThreshold = percentCorruptedSourcesThreshold;
  }

  /** Returns a {@link CorruptedSources} object summarizing the collected corrupted sources. */
  public CorruptedSources toReport() {
    Collections.sort(this.corruptedSourceIds);
    return CorruptedSources.builder()
        .setSourceIdCount(sourceIdCount)
        .setCorruptedSourcesCount(corruptedSourceIds.size())
        .setCorruptedSources(corruptedSourceIds)
        .setPercentCorruptedSourcesThreshold(percentCorruptedSourcesThreshold)
        .setAboveThreshold(isAboveThreshold())
        .build();
  }

  public void addSource() {
    this.sourceIdCount++;
  }

  public void addCorruptedSource(String sourceId) {
    this.corruptedSourceIds.add(sourceId);
  }

  public boolean isAboveThreshold() {
    return computeCorruptedSourcesPercentage() >= percentCorruptedSourcesThreshold;
  }

  public String generateLogString() {
    StringBuilder b = new StringBuilder();
    b.append(
        String.format(
            "%d out of %d sources (~%.0f %%) are corrupted",
            corruptedSourceIds.size(), sourceIdCount, computeCorruptedSourcesPercentage()));
    if (isAboveThreshold()) {
      b.append(
          String.format(
              ", which is greater than or equal to the provided threshold of %.0f%%",
              percentCorruptedSourcesThreshold));
    }
    b.append(".");
    if (!corruptedSourceIds.isEmpty()) {
      b.append("\n");
      b.append("Corrupted sources:");
      for (String sourceId : corruptedSourceIds) {
        b.append("\n").append(sourceId);
      }
    }
    return b.toString();
  }

  /** Returns a percentage in the range [0,100]. */
  float computeCorruptedSourcesPercentage() {
    if (sourceIdCount == 0) {
      return 0f;
    }
    return 100f * corruptedSourceIds.size() / sourceIdCount;
  }
}
