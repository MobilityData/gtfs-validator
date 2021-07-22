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

package org.mobilitydata.gtfsvalidator.validator;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimaps;
import java.util.List;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.SchemaExport;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;

/**
 * Validates that shape_dist_traveled along a shape in "shapes.txt" are not decreasing.
 *
 * <p>Generated notice: {@link DecreasingOrEqualShapeDistanceNotice}.
 */
@GtfsValidator
public class ShapeIncreasingDistanceValidator extends FileValidator {
  private final GtfsShapeTableContainer table;

  @Inject
  ShapeIncreasingDistanceValidator(GtfsShapeTableContainer table) {
    this.table = table;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (List<GtfsShape> shapeList : Multimaps.asMap(table.byShapeIdMap()).values()) {
      // GtfsShape objects are sorted based on @SequenceKey annotation on shape_pt_sequence field.
      for (int i = 1; i < shapeList.size(); ++i) {
        GtfsShape prev = shapeList.get(i - 1);
        GtfsShape curr = shapeList.get(i);
        if (prev.hasShapeDistTraveled()
            && curr.hasShapeDistTraveled()
            && prev.shapeDistTraveled() >= curr.shapeDistTraveled()) {
          noticeContainer.addValidationNotice(
              new DecreasingOrEqualShapeDistanceNotice(
                  curr.shapeId(),
                  curr.csvRowNumber(),
                  curr.shapeDistTraveled(),
                  curr.shapePtSequence(),
                  prev.csvRowNumber(),
                  prev.shapeDistTraveled(),
                  prev.shapePtSequence()));
        }
      }
    }
  }

  /**
   * When sorted on `shapes.shape_pt_sequence` key, shape points should have strictly increasing
   * values for `shapes.shape_dist_traveled`
   *
   * <p>"Values must increase along with shape_pt_sequence."
   * (http://gtfs.org/reference/static/#shapestxt)
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  static class DecreasingOrEqualShapeDistanceNotice extends ValidationNotice {
    @SchemaExport
    DecreasingOrEqualShapeDistanceNotice(
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
          SeverityLevel.ERROR);
    }
  }
}
