package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

public class NumberOutOfBoundsError extends Notice {
    public NumberOutOfBoundsError(String filename, long csvRowNumber, String fieldName, String fieldType, Object fieldValue) {
        super(ImmutableMap.of("filename", filename,
                "csvRowNumber", csvRowNumber,
                "fieldName", fieldName,
                "fieldType", fieldType,
                "fieldValue", fieldValue));
    }

    @Override
    public String getCode() {
        return "number_out_of_bounds";
    }
}

