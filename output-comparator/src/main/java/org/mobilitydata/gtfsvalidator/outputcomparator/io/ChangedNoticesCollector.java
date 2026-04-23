package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mobilitydata.gtfsvalidator.model.ValidationReport;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.report.AffectedSource;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.report.ChangedNotice;

/**
 * Given validation reports computed against two versions of the validator across multiple datasets,
 * collects the set of newly-generated notices of a particular severity level that changed between
 * the two validator versions.
 */
public class ChangedNoticesCollector {

  // The notice severity level we will examine for changes.
  private final SeverityLevel severityLevel;

  /**
   * If the number of new notices in a report meets or exceeds this threshold, the corresponding
   * dataset will be considered "invalid".
   */
  private final int newNoticeThreshold;

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
   * #newNoticeThreshold}.
   */
  private int invalidDatasetCount = 0;

  public ChangedNoticesCollector(
      SeverityLevel severityLevel, int newNoticeThreshold, float percentInvalidDatasetsThreshold) {
    this.severityLevel = severityLevel;
    this.newNoticeThreshold = newNoticeThreshold;
    this.percentInvalidDatasetsThreshold = percentInvalidDatasetsThreshold;
  }

  public List<ChangedNotice> getChangedNotices() {
    List<ChangedNotice> changed = new ArrayList<>(changedNoticesByCode.values());
    Collections.sort(changed, Comparator.comparing(ChangedNotice::noticeCode));
    changed.stream().forEach(ChangedNotice::sortAffectedSources);
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
    NoticeSet referenceNotices = filterNoticesFoSeverity(referenceReport);
    NoticeSet latestNotices = filterNoticesFoSeverity(latestReport);

    totalDatasetCount++;
    if (referenceNotices.hasSameNoticeCodes(latestNotices)) {
      return;
    }
    Set<String> newNotices = referenceNotices.getNewNotices(latestNotices);
    for (String noticeCode : newNotices) {
      ChangedNotice changedNotice =
          changedNoticesByCode.getOrDefault(noticeCode, new ChangedNotice(noticeCode));
      changedNoticesByCode.putIfAbsent(noticeCode, changedNotice);
      changedNotice.addAffectedSource(
          AffectedSource.create(
              sourceId, sourceUrl, latestNotices.getTotalNoticeCountForCode(noticeCode)));
    }
    if (newNotices.size() >= this.newNoticeThreshold) {
      ++invalidDatasetCount;
    }
  }

  private NoticeSet filterNoticesFoSeverity(ValidationReport report) {
    return new NoticeSet(
        report.getNotices().stream().filter(n -> n.getSeverity() == severityLevel));
  }

  /**
   * Returns true if the percentage of "invalid" datasets exceeds the {@link
   * #percentInvalidDatasetsThreshold}.
   */
  public boolean isAboveThreshold() {
    return computeInvalidDatasetPercentage() >= this.percentInvalidDatasetsThreshold;
  }

  /**
   * Returns a human-readable string summarizing the number of invalid datasets.
   *
   * @param severityLevelName the name of the severity level
   */
  public String generateLogString(String severityLevelName) {
    StringBuilder b = new StringBuilder();
    b.append("<details>\n<summary><strong>").append(severityLevelName).append("</strong> ");
    b.append(
        String.format(
            "(%d out of %d datasets, ~%.0f%%)",
            invalidDatasetCount, totalDatasetCount, computeInvalidDatasetPercentage()));
    if (isAboveThreshold()) {
      b.append(" ❌</summary>\n");
    } else {
      b.append(" ✅</summary>\n");
    }

    List<ChangedNotice> changedNotices = getChangedNotices();
    if (!changedNotices.isEmpty()) {
      if (isAboveThreshold()) {
        b.append("<p>Details of new errors due to code change, which is above");
      } else {
        b.append("<p>Details of new errors due to code change, which is less than");
      }
      b.append(
          String.format(
              " the provided threshold of %.0f%%.</p>\n", this.percentInvalidDatasetsThreshold));
      b.append("\n| Dataset | Notice Code |\n");
      b.append("|---------|-------------|\n");
      for (ChangedNotice notice : changedNotices) {
        for (AffectedSource source : notice.getAffectedSources()) {
          b.append("| ")
              .append(source.sourceId())
              .append(" | ")
              .append(notice.noticeCode())
              .append(" |\n");
        }
      }
    } else {
      b.append("<p>No changes were detected due to the code change.</p>\n");
    }

    b.append("</details>");
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
