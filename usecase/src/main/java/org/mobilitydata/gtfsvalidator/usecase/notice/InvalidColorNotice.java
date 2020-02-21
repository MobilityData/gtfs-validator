package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;

public class InvalidColorNotice extends ErrorNotice {
    public InvalidColorNotice(String filename, String fieldName, String entityId, String colorValue) {
        super(filename, "E013",
                "Invalid color",
                "Invalid color:" + colorValue + " in field:" + fieldName + " for entity with id:" + entityId);
    }
}