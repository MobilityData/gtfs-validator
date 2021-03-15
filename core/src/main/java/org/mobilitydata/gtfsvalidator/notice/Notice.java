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

package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.base.Joiner;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/** Base class for all notices produced by GTFS validator. */
public abstract class Notice {
  private static final String NOTICE_SUFFIX = "_notice";
  private Map<String, Object> context;
  private SeverityLevel severityLevel;

  public Notice(Map<String, Object> context, SeverityLevel severityLevel) {
    this.context = context;
    this.severityLevel = severityLevel;
  }

  public Map<String, Object> getContext() {
    return Collections.unmodifiableMap(context);
  }

  public SeverityLevel getSeverityLevel() {
    return this.severityLevel;
  }

  /**
   * Returns a descriptive type-specific name for this notice based on the class simple name.
   *
   * @return notice code, e.g., "foreign_key_error".
   */
  public String getCode() {
    return StringUtils.removeEnd(getClass().getSimpleName(), NOTICE_SUFFIX);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof Notice) {
      Notice otherNotice = (Notice) other;
      return context.equals(otherNotice.context)
          && getClass().equals(otherNotice.getClass())
          && severityLevel.equals(otherNotice.severityLevel);
    }
    return false;
  }

  @Override
  public String toString() {
    return getCode()
        + " "
        + Joiner.on(",").withKeyValueSeparator("=").join(context)
        + " "
        + getSeverityLevel().toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getClass(), getContext(), getSeverityLevel());
  }
}
