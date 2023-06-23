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

import static org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRef.FIELD_DEFINITIONS;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRefs;

/**
 * More than one row in CSV.
 *
 * <p>The file is expected to have a single entity but has more (e.g., "feed_info.txt").
 */
@GtfsValidationNotice(severity = WARNING, sections = @SectionRefs(FIELD_DEFINITIONS))
public class MoreThanOneEntityNotice extends ValidationNotice {

  /** Name of the faulty file. */
  private final String filename;

  /** Number of occurrences. */
  private final long entityCount;

  public MoreThanOneEntityNotice(String filename, long entityCount) {
    this.filename = filename;
    this.entityCount = entityCount;
  }
}
