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

/**
 * The given field has no value in some input row, even though values are required.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class MissingRecommendedFieldNotice extends ValidationNotice {

  // The name of the faulty file.
  private final String filename;

  // The row of the faulty record.
  private final int csvRowNumber;

  // The name of the missing field.
  private final String fieldName;

  public MissingRecommendedFieldNotice(String filename, int csvRowNumber, String fieldName) {
    super(SeverityLevel.WARNING);
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.fieldName = fieldName;
  }
}
