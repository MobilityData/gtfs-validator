package org.mobilitydata.gtfsvalidator.report.model;

import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;
import org.mobilitydata.gtfsvalidator.util.VersionInfo;

public class ReportData {
  public final ValidationRunnerConfig config;
  public final NoticeContainer noticeContainer;
  public FeedMetadata feedMetadata;
  public ReportSummary reportSummary;
  public VersionInfo versionInfo;

  public ReportData(
      FeedMetadata feedMetadata,
      NoticeContainer noticeContainer,
      ValidationRunnerConfig config,
      VersionInfo versionInfo) {
    this.feedMetadata = feedMetadata;
    this.noticeContainer = noticeContainer;
    this.versionInfo = versionInfo;
    this.config = config;
    this.reportSummary = new ReportSummary(noticeContainer, versionInfo);
  }
}
