package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@GtfsTable("frequencies.txt")
public interface GtfsFrequencySchema extends GtfsEntity {
    @Required
    @ForeignKey(table = "trips.txt", field = "trip_id")
    String tripId();

    @Required
    GtfsTime startTime();

    @Required
    GtfsTime endTime();

    @Required
    @Positive
    int headwaySecs();

    GtfsFrequencyExactTimes exactTimes();
}
