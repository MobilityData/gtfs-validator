package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

public class MissingRequiredFieldError extends Notice {
    public MissingRequiredFieldError(String filename, long csvRowNumber, String fieldName) {
        super(ImmutableMap.of("filename", filename,
                "csvRowNumber", csvRowNumber,
                "fieldName", fieldName));
    }

    @Override
    public String getCode() {
        return "missing_required_field";
    }
}
