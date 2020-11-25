package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

public class StopTimeWithArrivalBeforePreviousDepartureTimeNotice extends Notice {
    public StopTimeWithArrivalBeforePreviousDepartureTimeNotice(long csvRowNumber, long prevCsvRowNumber, String tripId, GtfsTime arrivalTime, GtfsTime departureTime) {
        super(ImmutableMap.of(
                "csvRowNumber", csvRowNumber,
                "prevCsvRowNumber", prevCsvRowNumber,
                "tripId", tripId,
                "departureTime", departureTime.toHHMMSS(),
                "arrivalTime", arrivalTime.toHHMMSS()
        ));
    }

    @Override
    public String getCode() {
        return "stop_time_with_arrival_before_previous_departure_time";
    }
}

