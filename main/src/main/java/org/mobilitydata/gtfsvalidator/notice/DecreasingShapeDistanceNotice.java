package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

public class DecreasingShapeDistanceNotice extends Notice {
    public DecreasingShapeDistanceNotice(String shapeId,
                                         long csvRowNumber, double shapeDistTraveled, int shapePtSequence,
                                         long prevCsvRowNumber, double prevShapeDistTraveled, int prevShapePtSequence) {
        super(new ImmutableMap.Builder<String, Object>()
                .put("shapeId", shapeId)
                .put("csvRowNumber", csvRowNumber)
                .put("shapeDistTraveled", shapeDistTraveled)
                .put("shapePtSequence", shapePtSequence)
                .put("prevCsvRowNumber", prevCsvRowNumber)
                .put("prevShapeDistTraveled", prevShapeDistTraveled)
                .put("prevShapePtSequence", prevShapePtSequence)
                .build());
    }

    @Override
    public String getCode() {
        return "decreasing_shape_distance";
    }
}
