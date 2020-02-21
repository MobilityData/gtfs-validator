package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;

public class InvalidUrlNotice extends ErrorNotice {
    public InvalidUrlNotice(String filename, String fieldName, String entityId, String urlValue) {
        super(filename, "E011",
                "Invalid url",
                "Invalid url:" + urlValue + " in field:" + fieldName + " for entity with id:" + entityId);
    }
}