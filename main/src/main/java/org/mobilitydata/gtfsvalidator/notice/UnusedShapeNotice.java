package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

public class UnusedShapeNotice extends Notice {
    public UnusedShapeNotice(String shapeId, long csvRowNumber) {
        super(ImmutableMap.of(
                "shapeId", shapeId,
                "csvRowNumber", csvRowNumber));
    }

    @Override
    public String getCode() {
        return "unused_shape";
    }
}
