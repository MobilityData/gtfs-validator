package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@GtfsTable("stop_times.txt")
public interface GtfsStopTimeSchema extends GtfsEntity {
    @Required
    @ForeignKey(table = "trips.txt", field = "trip_id")
    @FirstKey
    String tripId();

    @ConditionallyRequired
    GtfsTime arrivalTime();

    @ConditionallyRequired
    GtfsTime departureTime();

    @FieldType(FieldTypeEnum.ID)
    @Required
    @ForeignKey(table = "stops.txt", field = "stop_id")
    String stopId();

    @Required
    @NonNegative
    @SequenceKey
    int stopSequence();

    String stopHeadsign();

    GtfsPickupDropOff pickupType();

    GtfsPickupDropOff dropOffType();

    @DefaultValue("1")
    GtfsContinuousPickupDropOff continuousPickup();

    @DefaultValue("1")
    GtfsContinuousPickupDropOff continuousDropOff();

    @NonNegative
    double shapeDistTraveled();

    @DefaultValue("1")
    GtfsStopTimesTimepoint timepoint();
}
