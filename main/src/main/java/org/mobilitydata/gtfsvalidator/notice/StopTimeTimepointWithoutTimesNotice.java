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

package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

/**
 * Timepoint without time
 *
 * <p>Severity: {@code SeverityLevel.WARNING}
 */
public class StopTimeTimepointWithoutTimesNotice extends ValidationNotice {
  public StopTimeTimepointWithoutTimesNotice(
      final long csvRowNumber,
      final String tripId,
      final long stopSequence,
      final String specifiedField) {
    super(
        ImmutableMap.of(
            "csvRowNumber", csvRowNumber,
            "tripId", tripId,
            "stopSequence", stopSequence,
            "specifiedField", specifiedField),
        SeverityLevel.WARNING);
  }

  @Override
  public String getCode() {
    return "stop_time_timepoint_without_times";
  }
}
