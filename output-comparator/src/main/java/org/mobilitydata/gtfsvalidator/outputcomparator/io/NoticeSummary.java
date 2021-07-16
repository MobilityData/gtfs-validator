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

package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
  private final List<Map<String, Object>> contexts;

  public NoticeSummary(
      String code, SeverityLevel severity, int count, List<Map<String, Object>> contexts) {
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

  public List<Map<String, Object>> getContexts() {
    return Collections.unmodifiableList(contexts);
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
      return this.getCode().equals(((NoticeSummary) other).getCode())
          && this.getSeverity().equals(((NoticeSummary) other).getSeverity())
          && this.getCount() == (((NoticeSummary) other).getCount())
          && getContexts().equals(((NoticeSummary) other).getContexts());
    }
    return false;
  }
}
