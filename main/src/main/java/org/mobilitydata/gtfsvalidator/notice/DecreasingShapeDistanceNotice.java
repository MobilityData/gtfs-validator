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

import com.google.common.collect.ImmutableMap;

/**
 * When sorted on `shapes.shape_pt_sequence` key, shape points should have increasing values for
 * `shapes.shape_dist_traveled`
 *
 * <p>"Values must increase along with shape_pt_sequence."
 * (http://gtfs.org/reference/static/#shapestxt)
 *
 * <p>Severity: {@code SeverityLevel.WARNING}
 */
public class DecreasingShapeDistanceNotice extends ValidationNotice {
  public DecreasingShapeDistanceNotice(
      String shapeId,
      long csvRowNumber,
      double shapeDistTraveled,
      int shapePtSequence,
      long prevCsvRowNumber,
      double prevShapeDistTraveled,
      int prevShapePtSequence) {
    super(
        new ImmutableMap.Builder<String, Object>()
            .put("shapeId", shapeId)
            .put("csvRowNumber", csvRowNumber)
            .put("shapeDistTraveled", shapeDistTraveled)
            .put("shapePtSequence", shapePtSequence)
            .put("prevCsvRowNumber", prevCsvRowNumber)
            .put("prevShapeDistTraveled", prevShapeDistTraveled)
            .put("prevShapePtSequence", prevShapePtSequence)
            .build(),
        SeverityLevel.WARNING);
  }

  @Override
  public String getCode() {
    return "decreasing_shape_distance";
  }
}
