package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

public class StopTimeWithDepartureBeforeArrivalTimeNotice extends Notice {
    public StopTimeWithDepartureBeforeArrivalTimeNotice(long csvRowNumber, String tripId, int stopSequence, GtfsTime departureTime, GtfsTime arrivalTime) {
        super(ImmutableMap.of(
                "csvRowNumber", csvRowNumber,
                "tripId", tripId,
                "stopSequence", stopSequence,
                "departureTime", departureTime.toHHMMSS(),
                "arrivalTime", arrivalTime.toHHMMSS()
        ));
    }

    @Override
    public String getCode() {
        return "stop_time_with_departure_before_arrival_time";
    }
}

