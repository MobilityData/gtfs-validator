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
 * When sorted on `stops.stop_sequence` key, stop times should have strictly increasing values for
 * `stops.shape_dist_traveled`
 *
 * <p>"Values used for shape_dist_traveled must increase along with stop_sequence"
 * (http://gtfs.org/reference/static/#stoptimestxt)
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class DecreasingOrEqualStopTimeDistanceNotice extends ValidationNotice {
  public DecreasingOrEqualStopTimeDistanceNotice(
      String tripId,
      long csvRowNumber,
      double shapeDistTraveled,
      int stopSequence,
      long prevCsvRowNumber,
      double prevStopTimeDistTraveled,
      int prevStopSequence) {
    super(
        new ImmutableMap.Builder<String, Object>()
            .put("tripId", tripId)
            .put("csvRowNumber", csvRowNumber)
            .put("shapeDistTraveled", shapeDistTraveled)
            .put("stopSequence", stopSequence)
            .put("prevCsvRowNumber", prevCsvRowNumber)
            .put("prevStopTimeDistTraveled", prevStopTimeDistTraveled)
            .put("prevStopSequence", prevStopSequence)
            .build());
  }

  @Override
  public String getCode() {
    return "decreasing_or_equal_stop_time_distance";
  }
}
