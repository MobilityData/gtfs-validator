package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;

public class IntegerFieldValueOutOfRangeNotice extends ErrorNotice {
    public IntegerFieldValueOutOfRangeNotice(
            String filename,
            String fieldName,
            String entityId,
            int rangeMin,
            int rangeMax,
            int actualValue) {
        super(filename, "E009",
                "Out of range integer value",
                "Invalid value for field:" + fieldName + " of entity with id:" + entityId +
                        " -- min:" + rangeMin + " max:" + rangeMax + " actual:" + actualValue);
    }
}