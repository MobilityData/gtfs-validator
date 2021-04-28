/*
 * Copyright 2020 MobilityData IO
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

package org.mobilitydata.gtfsvalidator.outputcomparator.util;

import java.util.List;
import java.util.Map;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;

/** Used to deserialize a validation report */
public class NoticeAggregate {
  private final String code;
  private final SeverityLevel severity;
  private final int totalNotices;
  private final List<Map<String, Object>> notices;

  public NoticeAggregate(
      String code, SeverityLevel severity, int totalNotices, List<Map<String, Object>> notices) {
    this.code = code;
    this.severity = severity;
    this.totalNotices = totalNotices;
    this.notices = notices;
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

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof NoticeAggregate) {
      return this.getCode().equals(((NoticeAggregate) other).getCode())
          && this.getSeverity().equals(((NoticeAggregate) other).getSeverity())
          && this.getTotalNotices() == (((NoticeAggregate) other).getTotalNotices())
          && getNotices().equals(((NoticeAggregate) other).getNotices());
    }
    return false;
  }

  public List<Map<String, Object>> getNotices() {
    return notices;
  }
}
