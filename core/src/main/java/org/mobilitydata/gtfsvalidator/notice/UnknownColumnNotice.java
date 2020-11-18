package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

public class UnknownColumnNotice extends Notice {
    public UnknownColumnNotice(String filename, String fieldName, int index) {
        super(ImmutableMap.of(
                "filename", filename,
                "fieldName", fieldName,
                "index", index));
    }

    @Override
    public String getCode() {
        return "unknown_column";
    }
}
