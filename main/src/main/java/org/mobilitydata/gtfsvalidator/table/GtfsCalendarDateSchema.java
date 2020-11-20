package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

@GtfsTable("calendar_dates.txt")
public interface GtfsCalendarDateSchema extends GtfsEntity {
    @FieldType(FieldTypeEnum.ID)
    @Required
    @Index
    String serviceId();

    @Required
    GtfsDate date();

    @Required
    GtfsCalendarDateExceptionType exceptionType();
}
