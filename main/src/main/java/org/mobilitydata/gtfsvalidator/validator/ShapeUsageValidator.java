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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.UrlRef;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTripSchema;
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
   * Shape is not used in GTFS file `trips.txt`.
   *
   * <p>All records defined by GTFS `shapes.txt` should be used in `trips.txt`.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @FileRefs({GtfsShapeSchema.class, GtfsTripSchema.class}),
      urls = {
        @UrlRef(
            label = "Original Python validator implementation",
            url = "https://github.com/google/transitfeed")
      })
  static class UnusedShapeNotice extends ValidationNotice {

    /** The faulty record's id. */
    private final String shapeId;

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    UnusedShapeNotice(String shapeId, int csvRowNumber) {
      super(SeverityLevel.WARNING);
      this.shapeId = shapeId;
      this.csvRowNumber = csvRowNumber;
    }
  }
}
