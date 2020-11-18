package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

public class InvalidRowLengthError extends Notice {
    public InvalidRowLengthError(String filename, long csvRowNumber, int rowLength, int headerCount) {
        super(ImmutableMap.of(
                "filename", filename,
                "csvRowNumber", csvRowNumber,
                "rowLength", rowLength,
                "headerCount", headerCount
        ));
    }

    @Override
    public String getCode() {
        return "invalid_row_length";
    }
}
