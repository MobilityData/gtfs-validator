package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.InfoNotice;

public class UnsupportedGtfsTypeNotice extends InfoNotice {
    public UnsupportedGtfsTypeNotice(String filename, String fieldName, String entityId) {
        super(filename, "I001",
                "Unsupported gtfs type",
                "Tried to validate an unsupported Gtfs type in file:" + filename +
                        ", entityId:" + entityId + ", field name:" + fieldName + " -->IGNORED");
    }
}
