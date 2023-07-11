package org.mobilitydata.gtfsvalidator.report;

import com.google.gson.Gson;
import java.io.IOException;
import org.mobilitydata.gtfsvalidator.model.ValidationReport;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.report.model.FeedMetadata;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;
import org.mobilitydata.gtfsvalidator.util.VersionInfo;

public class JsonReportGenerator {

  /** Generate the JSON report using the class ReportSummary and the notice container. */
  public String generateReport(
      Gson gson,
      FeedMetadata feedMetadata,
      NoticeContainer noticeContainer,
      ValidationRunnerConfig config,
      VersionInfo versionInfo)
      throws IOException {

    ValidationReport validationReport =
        noticeContainer.createValidationReport(noticeContainer.getResolvedValidationNotices());

    JsonReportSummary summary = new JsonReportSummary(feedMetadata, config, versionInfo);

    JsonReport jsonReport = new JsonReport(summary, validationReport.getNotices());

    return gson.toJson(jsonReport);
  }
}
