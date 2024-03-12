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

package org.mobilitydata.gtfsvalidator.report.model;

import java.util.*;
import java.util.stream.Collectors;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ResolvedNotice;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.util.VersionInfo;

/** ReportSummary is the class containing the summary methods for the HTML report. */
public class ReportSummary {
  private final NoticeContainer container;
  private final Map<SeverityLevel, Long> severityCounts;
  private final Map<SeverityLevel, Map<String, List<NoticeView>>> noticesMap;
  private final VersionInfo versionInfo;

  public ReportSummary(NoticeContainer container, VersionInfo versionInfo) {
    this.container = container;
    this.severityCounts =
        container.getResolvedValidationNotices().stream()
            .collect(
                Collectors.groupingBy(ResolvedNotice::getSeverityLevel, Collectors.counting()));
    this.noticesMap =
        container.getResolvedValidationNotices().stream()
            .map(NoticeView::new)
            .collect(
                Collectors.groupingBy(
                    NoticeView::getSeverityLevel,
                    () -> new TreeMap<>(Comparator.reverseOrder()),
                    Collectors.groupingBy(NoticeView::getCode, TreeMap::new, Collectors.toList())));
    this.versionInfo = versionInfo;
  }

  /**
   * Returns the notices grouped by SeverityLevel and notice code. The notices are returned as a map
   * of maps. The SeverityLevel map is implemented with a LinkedHashMap to preserve the original
   * order of severity levels. The notice code map is implemented with a TreeMap to sort the notices
   * alphabetically.
   *
   * @return the notices as a map of maps.
   */
  public Map<SeverityLevel, Map<String, List<NoticeView>>> getNoticesMap() {
    return noticesMap;
  }

  /**
   * Returns the total count of notices in the validation report.
   *
   * @return the total count of notices.
   */
  public int getNoticeCount() {
    return container.getValidationNotices().size();
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
