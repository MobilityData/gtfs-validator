package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;

public class FloatFieldValueOutOfRangeNotice extends ErrorNotice {
    public FloatFieldValueOutOfRangeNotice(
            String filename,
            String fieldName,
            String entityId,
            float rangeMin,
            float rangeMax,
            float actualValue) {
        super(filename, "E008",
                "Out of range float value",
                "Invalid value for field:" + fieldName + " of entity with id:" + entityId +
                        " -- min:" + rangeMin + " max:" + rangeMax + " actual:" + actualValue);
    }
}