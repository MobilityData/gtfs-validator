package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects sources where an OutOfMemoryError was detected during validation, tracking which side
 * (reference, latest, or both) experienced the error.
 *
 * <p>The check fails when the percentage of sources whose latest validation ran out of memory is
 * above a threshold. Sources where only the reference ran out of memory do not count: they are not
 * a regression of the latest version.
 */
public class OutOfMemorySourcesCollector {

  private final float percentLatestOomSourcesThreshold;

  private int sourceIdCount = 0;
  private final List<OutOfMemorySourceDetail> oomSourceDetails = new ArrayList<>();

  public OutOfMemorySourcesCollector(float percentLatestOomSourcesThreshold) {
    this.percentLatestOomSourcesThreshold = percentLatestOomSourcesThreshold;
  }

  /** Counts a compared source, whether or not it ran out of memory. */
  public void addSource() {
    this.sourceIdCount++;
  }

  public void addOomSource(String sourceId, boolean referenceHasOom, boolean latestHasOom) {
    oomSourceDetails.add(new OutOfMemorySourceDetail(sourceId, referenceHasOom, latestHasOom));
  }

  public boolean hasOomSources() {
    return !oomSourceDetails.isEmpty();
  }

  public boolean isAboveThreshold() {
    return computeLatestOomSourcesPercentage() > percentLatestOomSourcesThreshold;
  }

  public String generateLogString() {
    StringBuilder b = new StringBuilder();
    b.append("### 💾 Out of Memory Check\n");
    if (oomSourceDetails.isEmpty()) {
      b.append("No datasets experienced an OutOfMemoryError.\n");
      return b.toString();
    }
    if (isAboveThreshold()) {
      b.append(
          String.format(
              "**Failed:** %d out of %d sources (~%.0f %%) ran out of memory with the latest"
                  + " version, above the %.0f %% threshold.\n\n",
              countLatestOomSources(),
              sourceIdCount,
              computeLatestOomSourcesPercentage(),
              percentLatestOomSourcesThreshold));
    }
    b.append(
        String.format(
            "<details>\n<summary><strong>%d dataset(s) experienced an OutOfMemoryError.</strong></summary>\n\n",
            oomSourceDetails.size()));
    b.append("| Dataset | Reference OOM | Latest OOM |\n");
    b.append("|---------|---------------|------------|\n");
    for (OutOfMemorySourceDetail detail : oomSourceDetails) {
      b.append(
          String.format(
              "| %s | %s | %s |\n",
              detail.sourceId,
              detail.referenceHasOom ? "⚠️" : "✅",
              detail.latestHasOom ? "⚠️" : "✅"));
    }
    b.append("</details>");
    return b.toString();
  }

  private int countLatestOomSources() {
    int count = 0;
    for (OutOfMemorySourceDetail detail : oomSourceDetails) {
      if (detail.latestHasOom) {
        count++;
      }
    }
    return count;
  }

  /** Returns a percentage in the range [0,100]. */
  float computeLatestOomSourcesPercentage() {
    if (sourceIdCount == 0) {
      return 0f;
    }
    return 100f * countLatestOomSources() / sourceIdCount;
  }

  private static class OutOfMemorySourceDetail {
    final String sourceId;
    final boolean referenceHasOom;
    final boolean latestHasOom;

    OutOfMemorySourceDetail(String sourceId, boolean referenceHasOom, boolean latestHasOom) {
      this.sourceId = sourceId;
      this.referenceHasOom = referenceHasOom;
      this.latestHasOom = latestHasOom;
    }
  }
}
