package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;


@GtfsTable("routes.txt")
public interface GtfsRouteSchema extends GtfsEntity {
    @FieldType(FieldTypeEnum.ID)
    @PrimaryKey
    @Required
    String routeId();

    @FieldType(FieldTypeEnum.ID)
    @ForeignKey(table = "agency.txt", field = "agency_id")
    String agencyId();

    String routeShortName();

    String routeLongName();

    String routeDesc();

    GtfsRouteType routeType();

    @FieldType(FieldTypeEnum.URL)
    String routeUrl();

    @DefaultValue("FFFFFF")
    GtfsColor routeColor();

    GtfsColor routeTextColor();

    @NonNegative
    int sortOrder();

    @DefaultValue("1")
    GtfsContinuousPickupDropOff continuousPickup();

    @DefaultValue("1")
    GtfsContinuousPickupDropOff continuousDropOff();
}
