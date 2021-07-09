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
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.SchemaExport;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

/**
 * Validates that every shape in "shapes.txt" is used by some trip from "trips.txt"
 *
 * <p>Generated notice: {@link UnusedShapeNotice}.
 */
@GtfsValidator
public class ShapeUsageValidator extends FileValidator {
  private final GtfsTripTableContainer tripTable;
  private final GtfsShapeTableContainer shapeTable;

  @Inject
  ShapeUsageValidator(GtfsTripTableContainer tripTable, GtfsShapeTableContainer shapeTable) {
    this.tripTable = tripTable;
    this.shapeTable = shapeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    // Do not report the same shape_id multiple times.
    Set<String> reportedShapes = new HashSet<>();
    for (GtfsShape shape : shapeTable.getEntities()) {
      String shapeId = shape.shapeId();
      if (reportedShapes.add(shapeId) && tripTable.byShapeId(shapeId).isEmpty()) {
        noticeContainer.addValidationNotice(new UnusedShapeNotice(shapeId, shape.csvRowNumber()));
      }
    }
  }

  /**
   * A {@code GtfsShape} should be referred to at least once in {@code GtfsTripTableContainer}
   * station).
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  static class UnusedShapeNotice extends ValidationNotice {
    @SchemaExport
    UnusedShapeNotice(String shapeId, long csvRowNumber) {
      super(
          ImmutableMap.of(
              "shapeId", shapeId,
              "csvRowNumber", csvRowNumber),
          SeverityLevel.WARNING);
    }
  }
}
