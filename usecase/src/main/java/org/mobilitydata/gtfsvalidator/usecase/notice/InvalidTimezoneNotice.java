package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;

public class InvalidTimezoneNotice extends ErrorNotice {
    public InvalidTimezoneNotice(String filename, String fieldName, String entityId, String timezoneValue) {
        super(filename, "E012",
                "Invalid timezone",
                "Invalid timezone:" + timezoneValue + " in field:" + fieldName + " for entity with id:" + entityId);
    }
}