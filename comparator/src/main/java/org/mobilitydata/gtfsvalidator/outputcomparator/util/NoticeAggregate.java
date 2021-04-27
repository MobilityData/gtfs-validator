package org.mobilitydata.gtfsvalidator.outputcomparator.util;

import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;

public class NoticeAggregate {
  private final String code;
  private final SeverityLevel severity;
  private final int totalNotices;

  public NoticeAggregate(String code, SeverityLevel severity, int totalNotices) {
    this.code = code;
    this.severity = severity;
    this.totalNotices = totalNotices;
  }

  public int getTotalNotices() {
    return totalNotices;
  }

  public SeverityLevel getSeverity() {
    return severity;
  }

  public String getCode() {
    return code;
  }

  public boolean isError() {
    return getSeverity().ordinal() >= SeverityLevel.ERROR.ordinal();
  }
}
