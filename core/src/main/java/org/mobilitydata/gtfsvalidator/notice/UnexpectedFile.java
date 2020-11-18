package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

public class UnexpectedFile extends Notice {
    public UnexpectedFile(String filename) {
        super(ImmutableMap.of("filename", filename));
    }

    @Override
    public String getCode() {
        return "unexpected_file";
    }
}
