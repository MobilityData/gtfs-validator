package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

@GtfsTable("calendar.txt")
public interface GtfsCalendarSchema extends GtfsEntity {
    @FieldType(FieldTypeEnum.ID)
    @PrimaryKey
    @Required
    String serviceId();

    @Required
    boolean monday();

    @Required
    boolean tuesday();

    @Required
    boolean wednesday();

    @Required
    boolean thursday();

    @Required
    boolean friday();

    @Required
    boolean saturday();

    @Required
    boolean sunday();

    @Required
    GtfsDate startDate();

    @Required
    GtfsDate endDate();
}
