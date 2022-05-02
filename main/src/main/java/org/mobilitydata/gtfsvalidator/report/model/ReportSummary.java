/*
 * Copyright 2020 Google LLC, MobilityData IO
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.mobilitydata.gtfsvalidator.notice.Notice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;

public class ReportSummary {
  private final NoticeContainer container;
  private final Map<SeverityLevel, Long> severityCounts;
  private final Map<SeverityLevel, Map<String, List<ValidationNotice>>> noticesMap;

  public ReportSummary(NoticeContainer container) {
    this.container = container;
    this.severityCounts =
        container.getValidationNotices().stream()
            .collect(Collectors.groupingBy(Notice::getSeverityLevel, Collectors.counting()));
    this.noticesMap =
        container.getValidationNotices().stream()
            .collect(
                Collectors.groupingBy(
                    Notice::getSeverityLevel,
                    LinkedHashMap::new,
                    Collectors.groupingBy(Notice::getCode, TreeMap::new, Collectors.toList())));
  }

  public Map<SeverityLevel, Map<String, List<ValidationNotice>>> getNoticesMap() {
    return noticesMap;
  }

  public int getNoticeCount() {
    return container.getValidationNotices().size();
  }

  public long getErrorCount() {
    return severityCounts.getOrDefault(SeverityLevel.ERROR, 0L);
  }

  public long getWarningCount() {
    return severityCounts.getOrDefault(SeverityLevel.WARNING, 0L);
  }

  public long getInfoCount() {
    return severityCounts.getOrDefault(SeverityLevel.INFO, 0L);
  }
}
