package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

public class UnexpectedFileNotice extends Notice {
    public UnexpectedFileNotice(String filename) {
        super(ImmutableMap.of("filename", filename));
    }

    @Override
    public String getCode() {
        return "unexpected_file";
    }
}
