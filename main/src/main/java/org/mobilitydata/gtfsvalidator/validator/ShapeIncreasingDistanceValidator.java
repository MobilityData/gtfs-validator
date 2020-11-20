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
 *
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

