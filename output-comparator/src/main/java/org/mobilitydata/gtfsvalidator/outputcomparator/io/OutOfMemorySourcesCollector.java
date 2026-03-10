package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects sources where an OutOfMemoryError was detected during validation, tracking which side
 * (reference, latest, or both) experienced the error.
 */
public class OutOfMemorySourcesCollector {

  private final List<OutOfMemorySourceDetail> oomSourceDetails = new ArrayList<>();

  public void addOomSource(String sourceId, boolean referenceHasOom, boolean latestHasOom) {
    oomSourceDetails.add(new OutOfMemorySourceDetail(sourceId, referenceHasOom, latestHasOom));
  }

  public boolean hasOomSources() {
    return !oomSourceDetails.isEmpty();
  }

  public String generateLogString() {
    StringBuilder b = new StringBuilder();
    b.append("### 💾 Out of Memory Check\n");
    if (oomSourceDetails.isEmpty()) {
      b.append("No datasets experienced an OutOfMemoryError.\n");
      return b.toString();
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
