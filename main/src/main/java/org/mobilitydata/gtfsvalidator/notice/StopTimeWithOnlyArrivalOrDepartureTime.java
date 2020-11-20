package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

public class StopTimeWithOnlyArrivalOrDepartureTime extends Notice {
    public StopTimeWithOnlyArrivalOrDepartureTime(long csvRowNumber, String tripId, int stopSequence, String specifiedField) {
        super(ImmutableMap.of(
                "csvRowNumber", csvRowNumber,
                "tripId", tripId,
                "stopSequence", stopSequence,
                "specifiedField", specifiedField
        ));
    }

    @Override
    public String getCode() {
        return "stop_time_with_only_arrival_or_departure_time";
    }
}

