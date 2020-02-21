package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.WarningNotice;

public class NonAsciiOrNonPrintableCharNotice extends WarningNotice {
    public NonAsciiOrNonPrintableCharNotice(String filename, String fieldName, String entityId, String idValue) {
        super(filename, "W002",
                "Suspicious id",
                "Non ascii or non printable character(s) in:" + idValue + " in field:" + fieldName + " for entity with id:" + entityId);
    }
}