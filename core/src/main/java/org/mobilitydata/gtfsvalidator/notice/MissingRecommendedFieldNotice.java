/*
 * Copyright 2020 Google LLC, Jarvus Innovations LLC
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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

/**
 * A recommended field is missing.
 *
 * <p>The given field has no value in some input row, even though values are recommended.
 */
@GtfsValidationNotice(severity = WARNING)
public class MissingRecommendedFieldNotice extends ValidationNotice {

  /** The name of the faulty file. */
  private final String filename;

  /** The row of the faulty record. */
  private final int csvRowNumber;

  /** The name of the missing field. */
  private final String fieldName;

  public MissingRecommendedFieldNotice(String filename, int csvRowNumber, String fieldName) {
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.fieldName = fieldName;
  }
}
