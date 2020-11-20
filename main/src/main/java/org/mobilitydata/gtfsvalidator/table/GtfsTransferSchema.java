package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;

@GtfsTable("transfers.txt")
public interface GtfsTransferSchema extends GtfsEntity {
    @FieldType(FieldTypeEnum.ID)
    @Required
    @ForeignKey(table = "stops.txt", field = "stop_id")
    String fromStopId();

    @FieldType(FieldTypeEnum.ID)
    @Required
    @ForeignKey(table = "stops.txt", field = "stop_id")
    String toStopId();

    GtfsTransferType transferType();

    @NonNegative
    int minTransferTime();
}

