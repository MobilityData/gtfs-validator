/*
 * Copyright 2021 MobilityData IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.model;

import com.google.gson.annotations.Expose;
import com.google.gson.internal.LinkedTreeMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;

/**
 * Used to deserialize a validation report. It is used to store information about one type of notice
 * encountered in a validation report: error code, severity level, the total number of notices
 * related to the error code and a list of notice contexts (which provides additional information
 * about each notice.
 */
public class NoticeReport {

  @Expose() private final String code;
  @Expose() private final SeverityLevel severity;
  @Expose() private final int totalNotices;
  @Expose() private final List<LinkedTreeMap<String, Object>> sampleNotices;

  public NoticeReport(
      String code,
      SeverityLevel severity,
      int count,
      List<LinkedTreeMap<String, Object>> contexts) {
    this.code = code;
    this.severity = severity;
    this.totalNotices = count;
    this.sampleNotices = contexts;
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

  public List<LinkedTreeMap<String, Object>> getSampleNotices() {
    return Collections.unmodifiableList(sampleNotices);
  }

  public boolean isError() {
    return getSeverity().ordinal() >= SeverityLevel.ERROR.ordinal();
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof NoticeReport) {
      NoticeReport otherNoticeReport = (NoticeReport) other;
      return this.getCode().equals(otherNoticeReport.getCode())
          && this.getSeverity().equals(otherNoticeReport.getSeverity())
          && this.getTotalNotices() == (otherNoticeReport.getTotalNotices())
          && getSampleNotices().equals(otherNoticeReport.getSampleNotices());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, severity);
  }
}
