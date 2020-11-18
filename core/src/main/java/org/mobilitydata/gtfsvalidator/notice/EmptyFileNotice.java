package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

public class EmptyFileNotice extends Notice {

    public EmptyFileNotice(String filename) {
        super(ImmutableMap.of("filename", filename));
    }

    @Override
    public String getCode() {
        return "empty_file";
    }
}
