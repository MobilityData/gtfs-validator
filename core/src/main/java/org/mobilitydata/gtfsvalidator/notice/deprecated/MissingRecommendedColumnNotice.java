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
package org.mobilitydata.gtfsvalidator.notice.deprecated;

import static org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRef.TERM_DEFINITIONS;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRefs;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;

/** A recommended column is missing in the input file. */
@GtfsValidationNotice(
    severity = WARNING,
    sections = @SectionRefs(TERM_DEFINITIONS),
    deprecated = true,
    deprecationVersion = "7.0.0",
    deprecationReason = "Unused validation notice")
public class MissingRecommendedColumnNotice extends ValidationNotice {
  /** The name of the faulty file. */
  private final String filename;

  /** The name of the missing column. */
  private final String fieldName;

  public MissingRecommendedColumnNotice(String filename, String fieldName) {
    this.filename = filename;
    this.fieldName = fieldName;
  }
}
