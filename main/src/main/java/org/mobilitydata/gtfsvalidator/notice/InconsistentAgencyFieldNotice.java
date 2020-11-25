package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

public class InconsistentAgencyFieldNotice extends Notice {
    public InconsistentAgencyFieldNotice(long csvRowNumber, String fieldName, String expected, String actual) {
        super(ImmutableMap.of(
                "csvRowNumber", csvRowNumber,
                "fieldName", fieldName,
                "expected", expected,
                "actual", actual));
    }

    @Override
    public String getCode() {
        return "inconsistent_agency_field";
    }
}
