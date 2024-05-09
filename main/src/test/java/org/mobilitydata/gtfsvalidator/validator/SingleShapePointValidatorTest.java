package org.mobilitydata.gtfsvalidator.validator;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class SingleShapePointValidatorTest {
    public static GtfsShape createShapePoint(
            int csvRowNumber,
            String shapeId,
            double shapePtLat,
            double shapePtLon,
            int shapePtSequence,
            double shapeDistTraveled) {
        return new GtfsShape.Builder()
                .setCsvRowNumber(csvRowNumber)
                .setShapeId(shapeId)
                .setShapePtLat(shapePtLat)
                .setShapePtLon(shapePtLon)
                .setShapePtSequence(shapePtSequence)
                .setShapeDistTraveled(shapeDistTraveled)
                .build();
    }


    private static List<ValidationNotice> generateNotices(
            List<GtfsShape> shapes) {
        NoticeContainer noticeContainer = new NoticeContainer();
        new SingleShapePointValidator(
                GtfsShapeTableContainer.forEntities(shapes, noticeContainer))
                .validate(noticeContainer);
        return noticeContainer.getValidationNotices();
    }

    @Test
    public void shapeWithMoreThanOneShapePointShouldNotGenerateNotice() {
        assertThat(
                generateNotices(
                        ImmutableList.of(
                                createShapePoint(1, "first shape id", 45.0d, 45.0d, 1, 0.0d),
                                createShapePoint(2, "first shape id", 45.1d, 45.0d, 2, 40.0d)
                        ))
        ).isEmpty();
    }

    @Test
    public void unusedShapeShouldGenerateNotice() {
        assertThat(
                generateNotices(
                        ImmutableList.of(
                                createShapePoint(1, "first shape id", 45.0d, 45.0d, 1, 40.0d),
                                createShapePoint(2, "first shape id", 45.1d, 45.0d, 2, 40.0d),
                                createShapePoint(3, "second shape id", 45.0d, 45.0d, 1, 40.0d)))
                ).containsExactly(new SingleShapePointValidator.SingleShapePointNotice("second shape id", 3));
    }
}
