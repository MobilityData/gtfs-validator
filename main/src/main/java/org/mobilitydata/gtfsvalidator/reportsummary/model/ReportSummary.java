/*
 * Copyright 2022 Google LLC, MobilityData IO
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

package org.mobilitydata.gtfsvalidator.reportsummary.model;

import com.google.common.annotations.VisibleForTesting;
import java.util.*;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ResolvedNotice;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.util.VersionInfo;

/** ReportSummary is the class containing the summary methods for the HTML report. */
public class ReportSummary {

  /**
   * Maximum number of notices per code for which a {@link NoticeView} is created.
   *
   * <p>A {@code NoticeView} holds the complete JSON tree of its notice, so building one per notice
   * costs a few hundred bytes per notice, allocated at the very end of validation when the heap is
   * already at its peak. The report only lists this many affected records per notice code, so the
   * remaining views were built and discarded without ever being read.
   */
  @VisibleForTesting static final int MAX_NOTICES_PER_CODE = 50;

  private final Map<SeverityLevel, Long> severityCounts;
  private final Map<SeverityLevel, Map<String, List<NoticeView>>> noticesMap;
  private final Map<SeverityLevel, Map<String, Integer>> noticeCountPerSeverityAndCode;
  private final int noticeCount;
  private final VersionInfo versionInfo;

  public ReportSummary(NoticeContainer container, VersionInfo versionInfo) {
    this.versionInfo = versionInfo;
    this.severityCounts = new EnumMap<>(SeverityLevel.class);
    // Severity levels are listed from the most to the least severe, notice codes alphabetically.
    this.noticesMap = new TreeMap<>(Comparator.reverseOrder());
    this.noticeCountPerSeverityAndCode = new EnumMap<>(SeverityLevel.class);

    for (ResolvedNotice<ValidationNotice> notice : container.getResolvedValidationNotices()) {
      List<NoticeView> notices =
          noticesMap
              .computeIfAbsent(notice.getSeverityLevel(), level -> new TreeMap<>())
              .computeIfAbsent(notice.getContext().getCode(), code -> new ArrayList<>());
      if (notices.size() < MAX_NOTICES_PER_CODE) {
        notices.add(new NoticeView(notice));
      }
    }

    // The counts come from the container rather than from the notices above, which are only the
    // ones the container retained and this summary materialized. The report must show how many
    // notices the feed actually produced, the same number the JSON report exports.
    int total = 0;
    for (Map.Entry<SeverityLevel, Map<String, List<NoticeView>>> bySeverity :
        noticesMap.entrySet()) {
      SeverityLevel severityLevel = bySeverity.getKey();
      Map<String, Integer> countPerCode = new TreeMap<>();
      long severityCount = 0;
      for (String code : bySeverity.getValue().keySet()) {
        int count = container.getNoticeCount(code, severityLevel);
        countPerCode.put(code, count);
        severityCount += count;
      }
      noticeCountPerSeverityAndCode.put(severityLevel, countPerCode);
      severityCounts.put(severityLevel, severityCount);
      total += severityCount;
    }
    this.noticeCount = total;
  }

  /**
   * Returns the notices grouped by SeverityLevel and notice code. The notices are returned as a map
   * of maps. The SeverityLevel map is sorted from the most to the least severe level. The notice
   * code map is implemented with a TreeMap to sort the notices alphabetically.
   *
   * <p>Each list holds at most {@link #MAX_NOTICES_PER_CODE} notices, which is what the report
   * lists. Use {@link #getNoticeCountForCode} for the total number of notices of a code.
   *
   * @return the notices as a map of maps.
   */
  public Map<SeverityLevel, Map<String, List<NoticeView>>> getNoticesMap() {
    return noticesMap;
  }

  /**
   * Returns the total count of notices of the given severity level and code, including the ones the
   * container did not retain and which are therefore absent from {@link #getNoticesMap()}.
   *
   * @return the total count of notices for that severity level and code.
   */
  public int getNoticeCountForCode(SeverityLevel severityLevel, String code) {
    return noticeCountPerSeverityAndCode
        .getOrDefault(severityLevel, Map.of())
        .getOrDefault(code, 0);
  }

  /**
   * Returns the maximum number of notices per code listed in the report.
   *
   * @return the maximum number of notices listed per code.
   */
  public int getMaxNoticesPerCode() {
    return MAX_NOTICES_PER_CODE;
  }

  /**
   * Returns the total count of notices in the validation report.
   *
   * @return the total count of notices.
   */
  public int getNoticeCount() {
    return noticeCount;
  }

  /**
   * Returns the count of notices with SeverityLevel.ERROR.
   *
   * @return the count of error notices.
   */
  public long getErrorCount() {
    return severityCounts.getOrDefault(SeverityLevel.ERROR, 0L);
  }

  /**
   * Returns the count of notices with SeverityLevel.WARNING.
   *
   * @return the count of warning notices.
   */
  public long getWarningCount() {
    return severityCounts.getOrDefault(SeverityLevel.WARNING, 0L);
  }

  /**
   * Returns the count of notices with SeverityLevel.INFO.
   *
   * @return the count of info notices.
   */
  public long getInfoCount() {
    return severityCounts.getOrDefault(SeverityLevel.INFO, 0L);
  }

  public String getVersion() {
    return versionInfo.currentVersion().orElse(null);
  }

  public boolean isNewVersionOfValidatorAvailable() {
    return versionInfo.updateAvailable();
  }
}
