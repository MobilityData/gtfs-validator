package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;

public class MissingRequiredValueNotice extends ErrorNotice {
    public MissingRequiredValueNotice(String filename, String fieldName, String entityId) {
        super(filename, "E014",
                "Missing required value",
                "Missing value for field:" + fieldName + " marked as required in entity with id:" + entityId);
    }
}