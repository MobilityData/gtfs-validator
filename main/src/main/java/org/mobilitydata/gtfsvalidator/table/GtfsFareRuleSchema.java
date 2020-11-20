package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;

@GtfsTable("fare_rules.txt")
public interface GtfsFareRuleSchema extends GtfsEntity {
    @FieldType(FieldTypeEnum.ID)
    @Required
    @ForeignKey(table = "fare_attributes", field = "fare_id")
    String fareId();

    @FieldType(FieldTypeEnum.ID)
    @ForeignKey(table = "routes.txt", field = "route_id")
    String routeId();

    @FieldType(FieldTypeEnum.ID)
    @ForeignKey(table = "stops.txt", field = "zone_id")
    String originId();

    @FieldType(FieldTypeEnum.ID)
    @ForeignKey(table = "stops.txt", field = "zone_id")
    String destinationId();

    @FieldType(FieldTypeEnum.ID)
    @ForeignKey(table = "stops.txt", field = "zone_id")
    String containsId();
}

