package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mobilitydata.gtfsvalidator.model.ValidationReport;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.report.AffectedSource;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.report.ChangedNotice;

/**
 * Given validation reports computed against two versions of the validator across multiple datasets,
 * collects the set of newly-generated error notices that changed between the two validator
 * versions.
 */
public class ChangedNoticesCollector {

  /**
   * If the number of new error notices in a report meets or exceeds this threshold, the
   * corresponding dataset will be considered "invalid".
   */
  private final int newErrorThreshold;

  /**
   * A percentage in the interval [0,100]. If percentage of "invalid" datasets (out of the total
   * corpus) exceeds this threshold, the {@link #isAboveThreshold()} method will return true,
   * indicating the validator under test is suspicious.
   */
  private final float percentInvalidDatasetsThreshold;

  /** The collected set of accumulated {@link ChangedNotice}, keyed by notice code. */
  private final Map<String, ChangedNotice> changedNoticesByCode = new HashMap<>();

  /** The total number of datasets, equal to the number of calls to compareValidationReports(). */
  private int totalDatasetCount = 0;

  /**
   * The number of invalid datasets, where the number of new validation errors exceeds {@link
   * #newErrorThreshold}.
   */
  private int invalidDatasetCount = 0;

  public ChangedNoticesCollector(int newErrorThreshold, float percentInvalidDatasetsThreshold) {
    this.newErrorThreshold = newErrorThreshold;
    this.percentInvalidDatasetsThreshold = percentInvalidDatasetsThreshold;
  }

  public List<ChangedNotice> getChangedNotices() {
    List<ChangedNotice> changed = new ArrayList<>(changedNoticesByCode.values());
    Collections.sort(changed, Comparator.comparing(ChangedNotice::noticeCode));
    return changed;
  }

  /**
   * Compares the reference and latest validation report to identify newly introduced validation
   * errors.
   *
   * @param sourceId the id of the dataset used to generate the validation report
   * @param sourceUrl the url for the dataset
   * @param referenceReport the validation report for the base instance of the validator
   * @param latestReport the validation report for the modified instance of the validator
   */
  public void compareValidationReports(
      String sourceId,
      String sourceUrl,
      ValidationReport referenceReport,
      ValidationReport latestReport) {
    totalDatasetCount++;
    if (referenceReport.hasSameErrorCodes(latestReport)) {
      return;
    }
    for (String noticeCode : referenceReport.getNewErrorsListing(latestReport)) {
      ChangedNotice changedNotice =
          changedNoticesByCode.getOrDefault(noticeCode, new ChangedNotice(noticeCode));
      changedNoticesByCode.putIfAbsent(noticeCode, changedNotice);
      changedNotice.addAffectedSource(
          AffectedSource.create(
              sourceId,
              sourceUrl,
              latestReport.getErrorNoticeReportByNoticeCode(noticeCode).getTotalNotices()));
    }
    if (referenceReport.getNewErrorsListing(latestReport).size() >= this.newErrorThreshold) {
      ++invalidDatasetCount;
    }
  }

  /**
   * Returns true if the percentage of "invalid" datasets exceeds the {@link
   * #percentInvalidDatasetsThreshold}.
   */
  public boolean isAboveThreshold() {
    return computeInvalidDatasetPercentage() >= this.percentInvalidDatasetsThreshold;
  }

  /** Returns a human-readable string summarizing the number of invalid datasets. */
  public String generateLogString() {
    StringBuilder b = new StringBuilder();
    b.append(
        String.format(
            "%d out of %d datasets (~%.0f%%) are invalid due to code change, which is ",
            invalidDatasetCount, totalDatasetCount, computeInvalidDatasetPercentage()));
    if (isAboveThreshold()) {
      b.append("above");
    } else {
      b.append("less than");
    }
    b.append(
        String.format(" the provided threshold of %.0f%%.", this.percentInvalidDatasetsThreshold));
    return b.toString();
  }

  /** Returns a percentage in the range [0,100]. */
  float computeInvalidDatasetPercentage() {
    if (totalDatasetCount == 0) {
      return 0f;
    }
    return 100f * invalidDatasetCount / totalDatasetCount;
  }
}
