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
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * {@code ValidationNotice} defined for tests purpose.
 */
public class AnotherTestValidationNotice extends ValidationNotice {

  private final String filename;
  private final Long csvRowNumber;
  private final String fieldName;
  private final Double otherFieldValue;
  private final Object fieldValue;
  private final GtfsDate sampleDate;
  private final GtfsTime sampleTime;
  private final GtfsColor sampleColor;
  private final Integer integerValue;

  public AnotherTestValidationNotice(String filename, long csvRowNumber, String fieldName,
      Object fieldValue, double otherFieldValue, GtfsDate sampleDate, GtfsTime sampleTime,
      GtfsColor sampleColor) {
    super(SeverityLevel.WARNING);
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.fieldName = fieldName;
    this.otherFieldValue = otherFieldValue;
    this.fieldValue = fieldValue;
    this.sampleDate = sampleDate;
    this.sampleTime = sampleTime;
    this.sampleColor = sampleColor;
    this.integerValue = null;
  }

  public AnotherTestValidationNotice(String filename, long csvRowNumber, String fieldName,
      Object fieldValue, GtfsTime sampleTime, GtfsColor sampleColor, int integerValue) {
    super(SeverityLevel.WARNING);
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.fieldValue = fieldValue;
    this.fieldName = fieldName;
    this.sampleTime = sampleTime;
    this.sampleColor = sampleColor;
    this.integerValue = integerValue;
    this.sampleDate = null;
    this.otherFieldValue = null;
  }
}
