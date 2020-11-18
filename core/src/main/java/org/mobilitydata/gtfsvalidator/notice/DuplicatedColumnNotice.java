package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

public class DuplicatedColumnNotice extends Notice {
    // Indices should start from 1.
    public DuplicatedColumnNotice(String filename, String fieldName, int firstIndex, int secondIndex) {
        super(ImmutableMap.of(
                "filename", filename,
                "fieldName", fieldName,
                "firstIndex", firstIndex,
                "secondIndex", secondIndex));
    }

    @Override
    public String getCode() {
        return "duplicated_column";
    }
}
