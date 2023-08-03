package org.mobilitydata.gtfsvalidator.report;

import java.io.IOException;
import org.mobilitydata.gtfsvalidator.model.ValidationReport;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.report.model.FeedMetadata;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;
import org.mobilitydata.gtfsvalidator.util.VersionInfo;

public class JsonReportGenerator {

  /** Generate the JSON report using the class ReportSummary and the notice container. */
  public JsonReport generateReport(
      FeedMetadata feedMetadata,
      NoticeContainer noticeContainer,
      ValidationRunnerConfig config,
      VersionInfo versionInfo,
      String date)
      throws IOException {

    ValidationReport validationReport =
        noticeContainer.createValidationReport(noticeContainer.getResolvedValidationNotices());

    JsonReportSummary summary = new JsonReportSummary(feedMetadata, config, versionInfo, date);

    return new JsonReport(summary, validationReport.getNotices());
  }
}
