package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;

@GtfsTable("pathways.txt")
public interface GtfsPathwaySchema extends GtfsEntity {
    @FieldType(FieldTypeEnum.ID)
    @PrimaryKey
    @Required
    String pathwayId();

    @FieldType(FieldTypeEnum.ID)
    @Required
    @ForeignKey(table = "stops.txt", field = "stop_id")
    String fromStopId();

    @FieldType(FieldTypeEnum.ID)
    @Required
    @ForeignKey(table = "stops.txt", field = "stop_id")
    String toStopId();

    @Required
    GtfsPathwayMode pathwayMode();

    @Required
    int isBidirectional();

    @NonNegative
    double length();

    @Positive
    int traversalTime();

    @NonZero
    int stairCount();

    double maxSlope();

    @Positive
    double minWidth();

    String signpostedAs();

    String reversedSignpostedAs();
}
