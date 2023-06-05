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

import java.util.Objects;

/**
 * A resolved notice is a wrapper around a plain {@link Notice} that includes the resolved {@link
 * SeverityLevel} for the notice.
 */
public final class ResolvedNotice<T extends Notice> {
  // The underlying Notice.
  private final T context;

  private final SeverityLevel severityLevel;

  public ResolvedNotice(T context, SeverityLevel severityLevel) {
    this.context = context;
    this.severityLevel = severityLevel;
  }

  public T getContext() {
    return this.context;
  }

  public SeverityLevel getSeverityLevel() {
    return this.severityLevel;
  }

  /**
   * @return the key used to group notices per type and severity: code + ordinal of severity level.
   */
  public String getMappingKey() {
    return context.getCode() + getSeverityLevel().ordinal();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResolvedNotice<?> notice = (ResolvedNotice<?>) o;
    return severityLevel == notice.severityLevel && context.equals(notice.context);
  }

  @Override
  public String toString() {
    return String.format("%s %s %s", context.getCode(), getSeverityLevel(), getContext());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getClass(), getContext(), getSeverityLevel());
  }

  /**
   * Tells if this notice is an {@code ERROR}.
   *
   * <p>This method is preferred to checking {@code severityLevel} directly since more levels may be
   * added in the future.
   *
   * @return true if this notice is an error, false otherwise
   */
  public boolean isError() {
    return getSeverityLevel().ordinal() >= SeverityLevel.ERROR.ordinal();
  }
}
