package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

public class MissingTableError extends Notice {
    public MissingTableError(String filename) {
        super(ImmutableMap.of("filename", filename));
    }

    @Override
    public String getCode() {
        return "missing_table";
    }
}
