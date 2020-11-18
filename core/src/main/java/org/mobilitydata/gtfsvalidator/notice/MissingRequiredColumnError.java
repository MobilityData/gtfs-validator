package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

public class MissingRequiredColumnError extends Notice {
    public MissingRequiredColumnError(String filename, String fieldName) {
        super(ImmutableMap.of("filename", filename,
                "fieldName", fieldName));
    }

    @Override
    public String getCode() {
        return "missing_required_column";
    }
}
