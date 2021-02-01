/*
 * Copyright 2020 Google LLC
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

/** Base class for all notices produced by GTFS validator. */
public abstract class Notice {
  private final Map<String, Object> context;

  public Notice(Map<String, Object> context) {
    this.context = context;
  }

  public Map<String, Object> getContext() {
    return Collections.unmodifiableMap(context);
  }

  /**
   * Returns a descriptive type-specific name for this notice.
   *
   * @return notice code, e.g., "foreign_key_error".
   */
  public abstract String getCode();

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof Notice) {
      Notice otherNotice = (Notice) other;
      return getCode().equals(otherNotice.getCode())
          && getContext().equals(otherNotice.getContext());
    }
    return false;
  }

  @Override
  public String toString() {
    return getCode()
        + " "
        + Joiner.on(",").useForNull("null").withKeyValueSeparator("=").join(context);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getCode(), getContext());
  }
}
