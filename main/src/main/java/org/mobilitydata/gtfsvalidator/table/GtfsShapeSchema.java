package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;

@GtfsTable("shapes.txt")
public interface GtfsShapeSchema extends GtfsEntity {
    @FieldType(FieldTypeEnum.ID)
    @Required
    @FirstKey
    String shapeId();

    @FieldType(FieldTypeEnum.LATITUDE)
    @Required
    double shapePtLat();

    @FieldType(FieldTypeEnum.LONGITUDE)
    @Required
    double shapePtLon();

    @Required
    @NonNegative
    @SequenceKey
    int shapePtSequence();

    @NonNegative
    double shapeDistTraveled();
}
