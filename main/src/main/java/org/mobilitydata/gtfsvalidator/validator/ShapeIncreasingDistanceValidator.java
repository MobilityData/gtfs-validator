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

import com.google.common.collect.Multimaps;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.DecreasingShapeDistanceNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;

import java.util.List;

/**
 * Validates that shape_dist_traveled along a shape in "shapes.txt" are not decreasing.
 * <p>
 * Generated notices:
 * * DecreasingShapeDistanceNotice
 */
@GtfsValidator
public class ShapeIncreasingDistanceValidator extends FileValidator {
    @Inject
    GtfsShapeTableContainer table;

    @Override
    public void validate(NoticeContainer noticeContainer) {
        for (List<GtfsShape> shapeList : Multimaps.asMap(table.byShapeIdMap()).values()) {
            // GtfsShape objects are sorted based on @SequenceKey annotation on shape_pt_sequence field.
            for (int i = 1; i < shapeList.size(); ++i) {
                GtfsShape prev = shapeList.get(i - 1);
                GtfsShape curr = shapeList.get(i);
                if (prev.hasShapeDistTraveled() && curr.hasShapeDistTraveled() &&
                        prev.shapeDistTraveled() > curr.shapeDistTraveled()) {
                    noticeContainer.addNotice(new DecreasingShapeDistanceNotice(
                            curr.shapeId(),
                            curr.csvRowNumber(), curr.shapeDistTraveled(), curr.shapePtSequence(),
                            prev.csvRowNumber(), prev.shapeDistTraveled(), prev.shapePtSequence()));
                }
            }
        }
    }
}

