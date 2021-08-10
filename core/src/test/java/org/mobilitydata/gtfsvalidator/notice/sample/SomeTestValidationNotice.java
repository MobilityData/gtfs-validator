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
package org.mobilitydata.gtfsvalidator.notice.sample;

import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;

/**
 * {@code ValidationNotice} defined for tests purpose.
 */
public class SomeTestValidationNotice extends ValidationNotice {
  private final String filename;
  private final long csvRowNumber;
  private final String fieldName;
  private final Object fieldValue;

  public SomeTestValidationNotice(String filename, long csvRowNumber, String fieldName,
      Object fieldValue, SeverityLevel severityLevel) {
    super(severityLevel);
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
  }
}
