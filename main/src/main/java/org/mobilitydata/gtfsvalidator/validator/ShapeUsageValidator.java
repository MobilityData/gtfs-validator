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
 *
 * Generated notices:
 * * UnusedShapeNotice
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
                noticeContainer.addNotice(new UnusedShapeNotice(shapeId, shape.csvRowNumber()));
            }
        }
    }
}
