package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

@GtfsTable("calendar.txt")
@ConditionallyRequired
public interface GtfsCalendarSchema extends GtfsEntity {
    @FieldType(FieldTypeEnum.ID)
    @PrimaryKey
    @Required
    String serviceId();

    @Required
    GtfsCalendarService monday();

    @Required
    GtfsCalendarService tuesday();

    @Required
    GtfsCalendarService wednesday();

    @Required
    GtfsCalendarService thursday();

    @Required
    GtfsCalendarService friday();

    @Required
    GtfsCalendarService saturday();

    @Required
    GtfsCalendarService sunday();

    @Required
    GtfsDate startDate();

    @Required
    GtfsDate endDate();
}
