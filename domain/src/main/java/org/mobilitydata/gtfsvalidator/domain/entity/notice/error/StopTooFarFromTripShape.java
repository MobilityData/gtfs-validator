package org.mobilitydata.gtfsvalidator.domain.entity.notice.error;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.ErrorNotice;

import java.io.IOException;

public class StopTooFarFromTripShape extends ErrorNotice {

    public StopTooFarFromTripShape(final String filename, final String stopId, final String stopSequence,
                                   final String tripId, final String shapeId, final float distanceToShape,
                                   final float stopShapeThreshold) {
        super(filename, E_047,
                "Stop too far from trip shape",
                "stop_id " + stopId + " is " + distanceToShape + " meters from shape_id " + shapeId +
                        " for trip_id " + tripId + " at stop_sequence " + stopSequence + " : it should be less than " +
                        stopShapeThreshold + " meters from the trip shape",
                null);
        putNoticeSpecific(KEY_EXPECTED_DISTANCE, stopShapeThreshold);
        putNoticeSpecific(KEY_ACTUAL_DISTANCE, distanceToShape);
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART, "stop_id");
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE, stopId);
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART, "trip_id");
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE, tripId);
        putNoticeSpecific(KEY_COMPOSITE_KEY_THIRD_PART, "shape_id");
        putNoticeSpecific(KEY_COMPOSITE_KEY_THIRD_VALUE, shapeId);
        putNoticeSpecific(KEY_COMPOSITE_KEY_FOURTH_PART, "stop_sequence");
        putNoticeSpecific(KEY_COMPOSITE_KEY_FOURTH_VALUE, stopSequence);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
