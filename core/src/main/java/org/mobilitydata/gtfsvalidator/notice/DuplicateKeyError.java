package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;


public class DuplicateKeyError extends Notice {
    public DuplicateKeyError(String filename, long oldCsvRowNumber,
                             long newCsvRowNumber,
                             String fieldName, Object fieldValue) {
        super(ImmutableMap.of("filename", filename,
                "oldCsvRowNumber", oldCsvRowNumber,
                "newCsvRowNumber", newCsvRowNumber,
                fieldName, fieldValue));
    }

    public DuplicateKeyError(String filename, long oldCsvRowNumber,
                             long newCsvRowNumber,
                             String fieldName1, Object fieldValue1,
                             String fieldName2, Object fieldValue2) {
        super(ImmutableMap.of("filename", filename,
                "oldCsvRowNumber", oldCsvRowNumber,
                "newCsvRowNumber", newCsvRowNumber,
                fieldName1, fieldValue1,
                fieldName2, fieldValue2));
    }

    @Override
    public String getCode() {
        return "duplicate_key";
    }
}
