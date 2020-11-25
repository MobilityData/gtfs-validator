package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;

@GtfsTable("trips.txt")
public interface GtfsTripSchema extends GtfsEntity {
    @FieldType(FieldTypeEnum.ID)
    @Required
    @PrimaryKey
    String tripId();

    @FieldType(FieldTypeEnum.ID)
    @Required
    @ForeignKey(table = "routes.txt", field = "route_id")
    String routeId();

    @FieldType(FieldTypeEnum.ID)
    @Required
    String serviceId();

    String tripHeadsign();

    String tripShortName();

    GtfsTripDirectionId directionId();

    @FieldType(FieldTypeEnum.ID)
    String blockId();

    @FieldType(FieldTypeEnum.ID)
    @ForeignKey(table = "shapes.txt", field = "shape_id")
    @Index
    @ConditionallyRequired
    String shapeId();

    GtfsWheelchairBoarding wheelchairAccessible();

    GtfsBikesAllowed bikesAllowed();
}
