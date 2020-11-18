package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

public class ForeignKeyError extends Notice {
    public ForeignKeyError(String childFilename, String childFieldName,
                           String parentFilename, String parentFieldName,
                           String fieldValue, long csvRowNumber) {
        super(new ImmutableMap.Builder<String, Object>().
                put("childFilename", childFilename)
                .put("childfieldName", childFieldName)
                .put("parentFilename", parentFilename)
                .put("parentFieldName", parentFieldName)
                .put("fieldValue", fieldValue)
                .put("csvRowNumber", csvRowNumber).build());
    }

    @Override
    public String getCode() {
        return "foreign_key_error";
    }
}
