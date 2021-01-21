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

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.UnusedShapeNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

import java.util.HashSet;
import java.util.Set;

/**
 * Validates that every shape in "shapes.txt" is used by some trip from "trips.txt"
 * <p>
 * Generated notice: {@link UnusedShapeNotice}.
 */
@GtfsValidator
public class ShapeUsageValidator extends FileValidator {
    @Inject
    GtfsTripTableContainer tripTable;

    @Inject
    GtfsShapeTableContainer shapeTable;

    @Override
    public void validate(NoticeContainer noticeContainer) {
        // Do not report the same shape_id multiple times.
        Set<String> reportedShapes = new HashSet<>();
        for (GtfsShape shape : shapeTable.getEntities()) {
            String shapeId = shape.shapeId();
            if (reportedShapes.add(shapeId) && tripTable.byShapeId(shapeId).isEmpty()) {
                noticeContainer.addValidationNotice(
                    new UnusedShapeNotice(shapeId, shape.csvRowNumber()));
            }
        }
    }
}
