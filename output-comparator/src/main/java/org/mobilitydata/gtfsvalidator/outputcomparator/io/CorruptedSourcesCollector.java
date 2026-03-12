package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import org.mobilitydata.gtfsvalidator.model.ValidationReport;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.report.CorruptedSources;

/**
 * Given validation reports computed against two versions of the validator across multiple datasets,
 * collects the set of "corrupted" sources where a validation report was not correctly generated.
 */
public class CorruptedSourcesCollector {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  private final float percentCorruptedSourcesThreshold;

  private int sourceIdCount = 0;
  private final List<CorruptedSourceDetail> corruptedSourceDetails = new ArrayList<>();

  public CorruptedSourcesCollector(float percentCorruptedSourcesThreshold) {
    this.percentCorruptedSourcesThreshold = percentCorruptedSourcesThreshold;
  }

  /** Returns a {@link CorruptedSources} object summarizing the collected corrupted sources. */
  public CorruptedSources toReport() {
    this.corruptedSourceDetails.sort((a, b) -> a.sourceId.compareTo(b.sourceId));
    List<String> corruptedSourceIds = new ArrayList<>();
    for (CorruptedSourceDetail detail : corruptedSourceDetails) {
      corruptedSourceIds.add(detail.sourceId);
    }
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

  public void addCorruptedSource(
      String sourceId,
      Boolean refExists,
      Boolean refReadable,
      Boolean latestExists,
      Boolean latestReadable) {
    this.corruptedSourceDetails.add(
        new CorruptedSourceDetail(
            sourceId, refExists, refReadable, latestExists, latestReadable, null, null));
  }

  public void addCorruptedSource(
      String sourceId,
      Boolean refExists,
      Boolean refReadable,
      Boolean latestExists,
      Boolean latestReadable,
      ValidationReport referenceSystemErrors,
      ValidationReport latestSystemErrors) {
    this.corruptedSourceDetails.add(
        new CorruptedSourceDetail(
            sourceId,
            refExists,
            refReadable,
            latestExists,
            latestReadable,
            referenceSystemErrors,
            latestSystemErrors));
  }

  public boolean isAboveThreshold() {
    return computeCorruptedSourcesPercentage() >= percentCorruptedSourcesThreshold;
  }

  public String generateLogString() {
    StringBuilder b = new StringBuilder();
    b.append("### 🛡️ Corruption Check\n");
    if (corruptedSourceDetails.isEmpty()) {
      b.append(
          String.format(
              "%d out of %d sources (~%.0f %%) are corrupted.\n\n",
              0, sourceIdCount, computeCorruptedSourcesPercentage()));
      return b.toString();
    }
    b.append(
        String.format(
            "<details>\n<summary><strong>%d out of %d sources (~%.0f %%) are corrupted.</summary>\n\n",
            corruptedSourceDetails.size(), sourceIdCount, computeCorruptedSourcesPercentage()));
    b.append(
        "| Dataset   | Ref Report Exists | Ref Report Readable | Latest Report Exists | Latest Report Readable |\n");
    b.append(
        "|-----------|-------------------|---------------------|----------------------|------------------------|\n");

    for (CorruptedSourceDetail detail : corruptedSourceDetails) {
      b.append(
          String.format(
              "| %s | %s | %s | %s | %s |\n",
              detail.sourceId,
              detail.refExists ? "✅" : "❌",
              detail.refReadable == null ? "N/A" : (detail.refReadable ? "✅" : "❌"),
              detail.latestExists ? "✅" : "❌",
              detail.latestReadable == null ? "N/A" : (detail.latestReadable ? "✅" : "❌")));
    }
    b.append("\n");

    // Append collapsible system errors for each corrupted source that has them
    for (CorruptedSourceDetail detail : corruptedSourceDetails) {
      if (detail.referenceSystemErrors != null
          && !detail.referenceSystemErrors.getNotices().isEmpty()) {
        b.append(
            String.format(
                "<details>\n<summary>🔍 System errors for <strong>%s</strong> (reference)</summary>\n\n",
                detail.sourceId));
        b.append("```json\n");
        b.append(GSON.toJson(detail.referenceSystemErrors.getNotices()));
        b.append("\n```\n</details>\n\n");
      }
      if (detail.latestSystemErrors != null && !detail.latestSystemErrors.getNotices().isEmpty()) {
        b.append(
            String.format(
                "<details>\n<summary>🔍 System errors for <strong>%s</strong> (latest)</summary>\n\n",
                detail.sourceId));
        b.append("```json\n");
        b.append(GSON.toJson(detail.latestSystemErrors.getNotices()));
        b.append("\n```\n</details>\n\n");
      }
    }

    b.append("</details>");
    return b.toString();
  }

  /** Returns a percentage in the range [0,100]. */
  float computeCorruptedSourcesPercentage() {
    if (sourceIdCount == 0) {
      return 0f;
    }
    return 100f * corruptedSourceDetails.size() / sourceIdCount;
  }

  private static class CorruptedSourceDetail {
    String sourceId;
    Boolean refExists;
    Boolean refReadable;
    Boolean latestExists;
    Boolean latestReadable;
    ValidationReport referenceSystemErrors;
    ValidationReport latestSystemErrors;

    CorruptedSourceDetail(
        String sourceId,
        Boolean refExists,
        Boolean refReadable,
        Boolean latestExists,
        Boolean latestReadable,
        ValidationReport referenceSystemErrors,
        ValidationReport latestSystemErrors) {
      this.sourceId = sourceId;
      this.refExists = refExists;
      this.refReadable = refReadable;
      this.latestExists = latestExists;
      this.latestReadable = latestReadable;
      this.referenceSystemErrors = referenceSystemErrors;
      this.latestSystemErrors = latestSystemErrors;
    }
  }
}
