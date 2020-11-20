package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.ForeignKey;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.Positive;
import org.mobilitydata.gtfsvalidator.annotation.Required;
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

    boolean exactTimes();
}
