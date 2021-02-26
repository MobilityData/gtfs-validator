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
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * Equal `frequencies.start_time` and `frequencies.end_time` from same row of GTFS file
 * `frequencies.txt`.
 *
 * <p>The GTFS spec is currently unclear how this case should be handled (e.g., is it a trip that
 * circulates once?). It is recommended to use a trip not defined via frequencies.txt for this case.
 *
 * <p>Severity: {@code SeverityLevel.WARNING}
 */
public class StartAndEndTimeEqualNotice extends ValidationNotice {
  public StartAndEndTimeEqualNotice(
      String filename, String entityId, long csvRowNumber, GtfsTime time) {
    super(
        ImmutableMap.of(
            "filename", filename,
            "csvRowNumber", csvRowNumber,
            "entityId", entityId,
            "time", time.toHHMMSS()),
        SeverityLevel.WARNING);
  }

  @Override
  public String getCode() {
    return "start_and_end_time_out_of_order";
  }
}
