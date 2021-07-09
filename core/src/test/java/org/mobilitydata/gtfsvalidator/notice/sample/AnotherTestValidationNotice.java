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

import com.google.common.collect.ImmutableMap;
import org.mobilitydata.gtfsvalidator.annotation.NoticeExport;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * {@code ValidationNotice} defined for tests purpose.
 */
public class AnotherTestValidationNotice extends ValidationNotice {

  @NoticeExport
  public AnotherTestValidationNotice(String filename, long csvRowNumber, String fieldName,
      Object fieldValue, double otherFieldValue, GtfsDate sampleDate, GtfsTime sampleTime,
      GtfsColor sampleColor) {
    super(new ImmutableMap.Builder<String, Object>()
            .put("filename", filename)
            .put("csvRowNumber", csvRowNumber)
            .put("fieldName", fieldName)
            .put("otherFieldValue", otherFieldValue)
            .put("fieldValue", fieldValue)
            .put("sampleDate", sampleDate)
            .put("sampleTime", sampleTime)
            .put("sampleColor", sampleColor)
            .build(),
        SeverityLevel.WARNING);
  }
}
