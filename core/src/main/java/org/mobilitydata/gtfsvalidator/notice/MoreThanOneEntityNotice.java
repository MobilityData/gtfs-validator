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

/**
 * A notice that the file is expected to have a single entity but has more (e.g., "feed_info.txt").
 *
 * <p>Severity: {@code SeverityLevel.WARNING}
 */
public class MoreThanOneEntityNotice extends ValidationNotice {
  private final String filename;
  private final long entityCount;

  public MoreThanOneEntityNotice(String filename, long entityCount) {
    super(SeverityLevel.WARNING);
    this.filename = filename;
    this.entityCount = entityCount;
  }
}
