package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

public class UnexpectedEnumValueError extends Notice {
    public UnexpectedEnumValueError(String filename, long csvRowNumber, String fieldName, int fieldValue) {
        super(ImmutableMap.of("filename", filename,
                "csvRowNumber", csvRowNumber,
                "fieldName", fieldName,
                "fieldValue", fieldValue));
    }

    @Override
    public String getCode() {
        return "unexpected_enum_value";
    }
}
