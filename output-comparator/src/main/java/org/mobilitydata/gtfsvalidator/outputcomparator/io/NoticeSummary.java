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

package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;

/**
 * Used to deserialize a validation report. It is used to store information about one type of notice
 * encountered in a validation report: error code, severity level, the total number of notices
 * related to the error code and a list of notice contexts (which provides additional information
 * about each notice.
 */
public class NoticeSummary {

  private final String code;
  private final SeverityLevel severity;

  @SerializedName("totalNotices")
  private final int count;

  @SerializedName("notices")
  private final Set<Map<String, Object>> contexts;

  public NoticeSummary(
      String code, SeverityLevel severity, int count, Set<Map<String, Object>> contexts) {
    this.code = code;
    this.severity = severity;
    this.count = count;
    this.contexts = contexts;
  }

  public int getCount() {
    return count;
  }

  public SeverityLevel getSeverity() {
    return severity;
  }

  public String getCode() {
    return code;
  }

  public Set<Map<String, Object>> getContexts() {
    return Collections.unmodifiableSet(contexts);
  }

  public boolean isError() {
    return getSeverity().ordinal() >= SeverityLevel.ERROR.ordinal();
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof NoticeSummary) {
      NoticeSummary otherNoticeSummary = (NoticeSummary) other;
      return this.getCode().equals(otherNoticeSummary.getCode())
          && this.getSeverity().equals(otherNoticeSummary.getSeverity())
          && this.getCount() == (otherNoticeSummary.getCount())
          && getContexts().equals(otherNoticeSummary.getContexts());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, severity);
  }
}
